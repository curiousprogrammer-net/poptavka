/*
 * Copyright (C), eProvement s.r.o. All rights reserved.
 */
package com.eprovement.poptavka.client.homesuppliers;

import com.eprovement.poptavka.client.catLocSelector.others.CatLocSelectorBuilder;
import com.eprovement.poptavka.client.common.session.Constants;
import com.eprovement.poptavka.client.common.session.Storage;
import com.eprovement.poptavka.client.detail.DetailModuleBuilder;
import com.eprovement.poptavka.client.homesuppliers.HomeSuppliersPresenter.HomeSuppliersViewInterface;
import com.eprovement.poptavka.client.root.toolbar.ProvidesToolbar;
import com.eprovement.poptavka.client.service.demand.CatLocSelectorRPCServiceAsync;
import com.eprovement.poptavka.client.user.widget.grid.UniversalAsyncGrid;
import com.eprovement.poptavka.shared.selectors.catLocSelector.ICatLocDetail;
import com.eprovement.poptavka.shared.domain.supplier.LesserSupplierDetail;
import com.eprovement.poptavka.shared.search.SearchDefinition;
import com.eprovement.poptavka.shared.search.SearchModuleDataHolder;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.mvp4g.client.annotation.Presenter;
import com.mvp4g.client.history.NavigationConfirmationInterface;
import com.mvp4g.client.history.NavigationEventCommand;
import com.mvp4g.client.presenter.LazyPresenter;
import com.mvp4g.client.view.LazyView;
import java.util.ArrayList;
import java.util.List;

/**
 * Module displays all registered supplier in table. Suppliers can be filtered by choosing
 * category from tree on the left side of view (table). By selecting item from table, full supplier detail
 * is retrieved from DB and displayed in detail view on the right side of view (table).
 *
 * Data are retrieved asynchronously for both tree and table.
 *
 * If tree node is not opening by clicking on the node's text, check if root category has
 * set right value for level. For root categories, 1 must be set, otherwise the algorithm
 * won't work.
 *
 * @author Martin Slavkovsky
 */
@Presenter(view = HomeSuppliersView.class)
public class HomeSuppliersPresenter
    extends LazyPresenter<HomeSuppliersViewInterface, HomeSuppliersEventBus>
    implements NavigationConfirmationInterface {

    /**************************************************************************/
    /* View interface                                                         */
    /**************************************************************************/
    public interface HomeSuppliersViewInterface extends LazyView, IsWidget, ProvidesToolbar {

        //Table
        UniversalAsyncGrid<LesserSupplierDetail> getDataGrid();

        SimplePager getPager();

        //Filter
        Label getFilterLabel();

        Button getFilterClearBtn();

        //Other
        SimplePanel getCategoryTreePanel();

        SimplePanel getDetailPanel();

        SimplePanel getFooterPanel();

    }
    /**************************************************************************/
    /* Attributes                                                             */
    /**************************************************************************/
    private SearchModuleDataHolder searchDataHolder;
    private ICatLocDetail selectedCategory;
    /** Flag attributes. **/
    private long selectedSupplierId = -1;
    private long selectedSupplierIdByHistory = -1;
    private boolean calledFromHistory;
    private boolean sameCategorySelection;
    private int pageFromToken = 0;
    private SelectionChangeEvent.Handler handler = new SelectionChangeEvent.Handler() {
        @Override
        public void onSelectionChange(SelectionChangeEvent event) {
            //Get selected item
            List<ICatLocDetail> selectedCategories = new ArrayList<ICatLocDetail>();
            eventBus.fillCatLocs(selectedCategories, builder.getInstanceId());
            //if new selection was made, need to set searchDataHolder = null before creating
            //token for new selection made in categorySelection.onSelected.
            if (!selectedCategories.isEmpty()) {
                searchDataHolder = null;
                //Tree browser supports olny single selection model, therefore can use get(0).
                selectedCategory = selectedCategories.get(0);
                selectCategoryChangeHandlerInner(selectedCategory);
            }
        }
    };
    private CatLocSelectorBuilder builder = new CatLocSelectorBuilder.Builder(Constants.HOME_SUPPLIERS_MODULE)
        .initCategorySelector()
        .initSelectorTreeBrowser()
        .displayCountOfSuppliers()
        .addSelectionHandler(handler)
        .build();

    /**************************************************************************/
    /* RPC Service                                                            */
    /* Need RPC service defined here because we need to pass it to CellTree   */
    /* and then to CategoryDataProvider                                       */
    /**************************************************************************/
    @Inject
    private CatLocSelectorRPCServiceAsync categoryService;

    public CatLocSelectorRPCServiceAsync getCategoryService() {
        return categoryService;
    }

    /**************************************************************************/
    /* General Module events                                                  */
    /**************************************************************************/
    public void onStart() {
        // nothing by default
        eventBus.initCatLocSelector(view.getCategoryTreePanel(), builder);
    }

    /**
     * Sets body and toolbar, footer.
     * Inits Detail module and category selector.
     */
    public void onForward() {
        eventBus.setBody(view);
        eventBus.setToolbarContent("Categories", view.getToolbarContent());
        eventBus.setFooter(view.getFooterPanel());
        eventBus.menuStyleChange(Constants.HOME_SUPPLIERS_MODULE);
        eventBus.initDetailSection(view.getDetailPanel());
    }

    @Override
    public void confirm(NavigationEventCommand event) {
        // nothing by default
    }

    /**************************************************************************/
    /* Navigation events                                                      */
    /**************************************************************************/
    /**
     * Main Navigation method called either by default application startup or by searching mechanism.
     * @param searchModuleDataHolder - if searching is needed, this object holds conditions to do so.
     *                               - it's also used as pointer to differ root and child sections
     */
    public void onGoToHomeSuppliersModule(SearchModuleDataHolder searchModuleDataHolder) {
        if (searchModuleDataHolder == null) {
            eventBus.resetSearchBar(null);
        }
        //flags initialization
        //----------------------------------------------------------------------
        calledFromHistory = false;

        //widget initialization
        //----------------------------------------------------------------------
        createTokenForHistory();
        deselectGridSelection();
        setFilteringLabel(searchModuleDataHolder);
        //Get data
        view.getDataGrid().getDataCount(eventBus, new SearchDefinition(searchModuleDataHolder));
    }

    /**
     * History Navigation method called either when application started from URL
     * or called by back & forward browser actions.
     *
     * @param historyTree - CellTree's open nodes hierarchy stored and parsed from URL.
     * @param categoryDetail - Retrieved CategoryDetail object according to category ID stored and parsed from URL.
     * @param page - Table's page stored and parsed from URL
     * @param supplierID - Supplier's ID stored and parsed from URL
     */
    public void onGoToHomeSuppliersModuleByHistory(SearchModuleDataHolder filterHolder,
        ICatLocDetail categoryDetail, int page, long supplierID) {
        calledFromHistory = true;
        setFilteringLabel(filterHolder);
        //Restore tree opened nodes
        if (categoryDetail == null) {
            //if no category selection -> no selection model selection -> no data retrieving -> manually ask for data
            view.getDataGrid().getDataCount(eventBus, new SearchDefinition(filterHolder));
        } else {
            //if category selection -> select and display in tree
            eventBus.requestHierarchy(
                CatLocSelectorBuilder.SELECTOR_TYPE_CATEGORIES, categoryDetail, builder.getInstanceId());
        }
        //Restore table page
        this.calledFromHistory = true;
        this.pageFromToken = page;
        this.selectedSupplierIdByHistory = supplierID;
    }

    /**************************************************************************/
    /* Restoring methods                                                      */
    /**************************************************************************/
    /**
     * Set currentlyLoadedView, filterLabel, searchDataHolder according to given filter.
     *
     * @param filterHolder
     * - if null - hides filterLabel and sets currentlyLoadedView to Constants.HOME_SUPPLIERS_BY_DEFAULT
     * - if not null - display filterLabel and sets currentlyLoadedView to Constants.HOME_SUPPLIERS_BY_SEARCH
     */
    private void setFilteringLabel(SearchModuleDataHolder filterHolder) {
        //FILTER
        //----------------------------------------------------------------------
        if (filterHolder == null) {
            Storage.setCurrentlyLoadedView(Constants.HOME_SUPPLIERS_BY_DEFAULT);
            view.getFilterLabel().setTitle("");
            view.getFilterLabel().setVisible(false);
            view.getFilterClearBtn().setVisible(false);
            eventBus.resetSearchBar(null);
        } else {
            Storage.setCurrentlyLoadedView(Constants.HOME_SUPPLIERS_BY_SEARCH);
            view.getFilterLabel().setTitle(filterHolder.toString());
            view.getFilterLabel().setVisible(true);
            view.getFilterClearBtn().setVisible(true);
        }
        searchDataHolder = filterHolder;
    }

    /**************************************************************************/
    /* Bind events                                                            */
    /**************************************************************************/
    /**
     * Events description.
     * - DataGrid.RangeChange
     *        - creates token when table page was changed
     *        - invoked when table page changed
     * - DataGrid.Selection
     *        - display details according to table selection
     * - CellTree.Selection (see onForward)
     *        - according to selection retrieve data to table
     *        - also if selected object (node) is not opened, open it
     *        - invoked when selecting category detail object in category selection model.
     *          CategorySelectionModel is bind to CellTree
     */
    @Override
    public void bindView() {
        dataGridRangeChangeHandler();
        dataGridSelectioChangeHandler();
        view.getFilterClearBtn().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                eventBus.goToHomeSuppliersModule(null);
            }
        });
    }

    /**************************************************************************/
    /* Bind events - helper methods                                           */
    /**************************************************************************/
    /**
     * Handle table range change by creating token for new range/page.
     */
    private void dataGridRangeChangeHandler() {
        view.getDataGrid().addRangeChangeHandler(new RangeChangeEvent.Handler() {
            @Override
            public void onRangeChange(RangeChangeEvent event) {
                createTokenForHistory();
            }
        });
    }

    /**
     * Is called either from by selecting SelectionCategoryModel or when reestablishing history.
     * @param selected
     */
    private void selectCategoryChangeHandlerInner(ICatLocDetail selected) {
        //Table selection
        //----------------------------------------------------------------------
        //If new category is selected, already selected supplier won't match -> deselect
        //Deselecting table object is needed desptite table data provider has changed,
        //because selection model holds its selected object independent to data provider.
        deselectGridSelection();

        //Set pager
        //----------------------------------------------------------------------
        //If any changed has been made, pager must be set to right page. Setting different
        //page that is already selected fires dataGrid.RangeChangeEvent that handles creating token.
        //In some scenarious we don't want to create another token, therefore differ two cases:
        //1) If setting page is called from history, cancel any attemts to create new token
        if (calledFromHistory) {
            //if actual page differs to history page, pager.setPage fires dataGrid.RangeChangeEvent
            //that handles creating token -> cancel creating new token in this scenario
            if (view.getPager().getPage() != pageFromToken) {
                eventBus.setHistoryStoredForNextOne(false);
            }

            //If same category is selected -> setPage must fire getData on dataProvider
            //If other category is selected -> setPage need not fire getData on dataProvider,
            //because new data will be retrieved therefore there is no point to do that
            //(we don't need to get data on page 0 on actual data because new
            //data will be retrieved)
            if (!sameCategorySelection) {
                view.getDataGrid().cancelRangeChangedEvent();
            }
            calledFromHistory = false;
            view.getPager().setPage(pageFromToken);
            //2) If setting page is called by user, make sure to create new token
        } else {
            //If user selected new category, pager must be reset to 0 and again:
            //If same page is set -> setPage won't fire getData on dataProvider
            //If different page is set -> setPage to 0 will fire getData on dataProvider,
            //therefore cancel rangeChangeEvent because new data will be retrieved
            //(we don't need to get data on page 0 on actual data because new
            //data will be retrieved)
            if (view.getPager().getPage() != 0) {
                view.getDataGrid().cancelRangeChangedEvent();
            }
            view.getPager().setPage(0);
            createTokenForHistory();
        }

        //Retireve data if needed
        //----------------------------------------------------------------------
        if (!sameCategorySelection) {
            if (selected == null) {
                //Retrieve data
                view.getDataGrid().getDataCount(eventBus, new SearchDefinition(searchDataHolder));
            } else {
                //cancel filtering if user selected category from celltree
                view.getFilterLabel().setVisible(false);
                view.getFilterClearBtn().setVisible(false);
                //Set selected category as filter and pass it to filter through that category
                SearchModuleDataHolder filterHolder = SearchModuleDataHolder.getSearchModuleDataHolder();
                filterHolder.getCategories().add(selected);
                //Retrieve data
                view.getDataGrid().getDataCount(eventBus, new SearchDefinition(
                    0, view.getPager().getPageSize(), filterHolder, view.getDataGrid().getSort().getSortOrder()));
            }
        }

        //Reset flags
        //----------------------------------------------------------------------
        sameCategorySelection = false;
    }

    /**
     * Display supplier detail in detail view when selected by user.
     */
    private void dataGridSelectioChangeHandler() {
        view.getDataGrid().getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                //get selected supplier
                LesserSupplierDetail selected =
                    (LesserSupplierDetail) ((SingleSelectionModel) view.getDataGrid().getSelectionModel())
                    .getSelectedObject();

                if (selected != null) {
                    selectedSupplierId = selected.getSupplierId();
                    //retrieve supplier detail info and display it
                    eventBus.buildDetailSectionTabs(
                        new DetailModuleBuilder.Builder()
                            .addSupplierTab(selectedSupplierId, false)
                            .selectTab(DetailModuleBuilder.USER_DETAIL_TAB)
                            .build());
                    eventBus.openDetail();
                    //create token for this selection
                    createTokenForHistory();
                }
            }
        });
    }

    /**************************************************************************/
    /* Additional methods                                                     */
    /**************************************************************************/
    /**
     * Used to select table item.
     * When supplier id is parsed from token, supplier object is retrieved and then selected.
     * @param supplierDetail
     */
    public void onDisplaySupplierDetail(LesserSupplierDetail supplierDetail) {
        //display by selecting item in selection model which
        //fires appropiate events to display supplier detail in detail section
        view.getDataGrid().getSelectionModel().setSelected(supplierDetail, true);
    }

    /**
     * Display suppliers of selected category.
     */
    public void onResponseGetData() {
        //If supplier must be selected, get its detail and select in selectionModel
        if (selectedSupplierId != -1) {
            eventBus.getSupplier(selectedSupplierId);
        } else if (selectedSupplierIdByHistory != -1) {
            eventBus.getSupplier(selectedSupplierIdByHistory);
        }
    }

    /**
     * Recalculate table height if resize event occurs.
     * Usually paddings or margins changes on smaller resolutions.
     * @param actualWidth
     */
    public void onResize(int actualWidth) {
        view.getDataGrid().resize(actualWidth);
    }

    /**************************************************************************/
    /* Private methods                                                        */
    /**************************************************************************/
    /**
     * Cancel table selection and hide supplier detail.
     */
    private void deselectGridSelection() {
        ((SingleSelectionModel) view.getDataGrid().getSelectionModel()).clear();
        //TODO Martin - remove if not needed
//        SingleSelectionModel selectionModel = (SingleSelectionModel) view.getDataGrid().getSelectionModel();
//        FullSupplierDetail supplier = (FullSupplierDetail) (selectionModel).getSelectedObject();
//        if (supplier != null) {
//            selectionModel.setSelected(supplier, false);
//        }
        selectedSupplierId = -1L;
        eventBus.displayAdvertisement();
    }

    /**
     * Creates token for history according to CellTree's open nodes hierarchy, table page, table selection.
     * @param setStorageHistoryPointerValue
     */
    private void createTokenForHistory() {
        LesserSupplierDetail supplier =
            (LesserSupplierDetail) ((SingleSelectionModel) view.getDataGrid().getSelectionModel())
            .getSelectedObject();

        eventBus.createTokenForHistory(searchDataHolder, selectedCategory, view.getPager().getPage(), supplier);
    }
}