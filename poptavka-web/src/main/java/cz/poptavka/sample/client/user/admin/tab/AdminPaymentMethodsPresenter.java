/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.poptavka.sample.client.user.admin.tab;

import java.util.List;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.AsyncHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.SingleSelectionModel;
import com.mvp4g.client.annotation.Presenter;

import com.mvp4g.client.presenter.LazyPresenter;

import com.mvp4g.client.view.LazyView;
import cz.poptavka.sample.client.main.Constants;
import cz.poptavka.sample.client.main.Storage;
import cz.poptavka.sample.client.main.common.search.SearchModuleDataHolder;
import cz.poptavka.sample.client.user.admin.AdminEventBus;
import cz.poptavka.sample.domain.common.OrderType;
import cz.poptavka.sample.shared.domain.adminModule.PaymentMethodDetail;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Martin Slavkovsky
 */
@Presenter(view = AdminPaymentMethodsView.class)
public class AdminPaymentMethodsPresenter
        extends LazyPresenter<AdminPaymentMethodsPresenter.AdminPaymentMethodsInterface, AdminEventBus> {

    //history of changes
    private Map<Long, PaymentMethodDetail> dataToUpdate = new HashMap<Long, PaymentMethodDetail>();
    private Map<Long, PaymentMethodDetail> originalData = new HashMap<Long, PaymentMethodDetail>();
    //need to remember for asynchDataProvider if asking for more data
    private SearchModuleDataHolder searchDataHolder;
    //for asynch data retrieving
    private AsyncDataProvider dataProvider = null;
    private int start = 0;
    //for asynch data sorting
    private AsyncHandler sortHandler = null;
    private Map<String, OrderType> orderColumns = new HashMap<String, OrderType>();
    //list of grid columns, used to sort them. First must by blank (checkbox in table)
    private final String[] columnNames = new String[]{
        "id", "name", "description"
    };
    private List<String> gridColumns = Arrays.asList(columnNames);

    /**
     * Interface for widget AdminPaymentMethodsView.
     */
    public interface AdminPaymentMethodsInterface extends LazyView {

        // TABLE
        DataGrid<PaymentMethodDetail> getDataGrid();

        Column<PaymentMethodDetail, String> getNameColumn();

        Column<PaymentMethodDetail, String> getDescriptionColumn();

        SingleSelectionModel<PaymentMethodDetail> getSelectionModel();

        // PAGER
        SimplePager getPager();

        ListBox getPageSizeCombo();

        int getPageSize();

        // BUTTONS
        Button getCommitBtn();

        Button getRollbackBtn();

        Button getRefreshBtn();

        Label getChangesLabel();

        // WIDGETS
        Widget getWidgetView();
    }

    /*** INIT ***
     *
     * Initial methods for handling starting.
     * @param filter
     */
    public void onInitPaymentMethods(SearchModuleDataHolder filter) {
        Storage.setCurrentlyLoadedView(Constants.ADMIN_PAYMENT_METHODS);
        eventBus.clearSearchContent();
        searchDataHolder = filter;
        eventBus.getAdminPaymentMethodsCount(searchDataHolder);
        view.getWidgetView().setStyleName(Storage.RSCS.common().userContent());
        eventBus.displayView(view.getWidgetView());
    }

    /*** DISPLAY ***
     *
     * Displays retrieved data.
     * @param accessRoles -- list to display
     */
    public void onDisplayAdminTabPaymentMethods(List<PaymentMethodDetail> lis) {
        dataProvider.updateRowData(start, lis);
        view.getDataGrid().flush();
        view.getDataGrid().redraw();
        Storage.hideLoading();
    }

    /*** DATA PROVIDER ***
     *
     * Creates asynchronous data provider for datagrid. Also sets sorting on ID column.
     * @param totalFound - count of all data in DB displayed in pager
     */
    public void onCreateAdminPaymentMethodAsyncDataProvider(final int totalFound) {
        this.start = 0;
        orderColumns.clear();
        orderColumns.put(columnNames[0], OrderType.ASC);
        dataProvider = new AsyncDataProvider<PaymentMethodDetail>() {

            @Override
            protected void onRangeChanged(HasData<PaymentMethodDetail> display) {
                display.setRowCount(totalFound);
                start = display.getVisibleRange().getStart();
                int length = display.getVisibleRange().getLength();
                eventBus.getAdminPaymentMethods(start, start + length, searchDataHolder, orderColumns);
                Storage.hideLoading();
            }
        };
        this.dataProvider.addDataDisplay(view.getDataGrid());
        createAsyncSortHandler();
    }

    /*** SORTING HANDLER ***
     *
     * Creates asynchronous sort handler. Handle sorting of data provided by asynchronous data provider.
     */
    public void createAsyncSortHandler() {
        //Moze byt hned na zaciatku? Ak ano , tak potom aj asynchdataprovider by mohol nie?
        sortHandler = new AsyncHandler(view.getDataGrid()) {

            @Override
            public void onColumnSort(ColumnSortEvent event) {
                orderColumns.clear();
                OrderType orderType = OrderType.DESC;

                if (event.isSortAscending()) {
                    orderType = OrderType.ASC;
                }
                Column<PaymentMethodDetail, String> column = (Column<PaymentMethodDetail, String>) event.getColumn();
                if (column == null) {
                    return;
                }
                orderColumns.put(gridColumns.get(
                        view.getDataGrid().getColumnIndex(column)), orderType);

                eventBus.getAdminPaymentMethods(start, view.getPageSize(), searchDataHolder, orderColumns);
            }
        };
        view.getDataGrid().addColumnSortHandler(sortHandler);
    }

    /*** DATA CHANGE ***
     *
     * Store changes made in table data
     */
    public void onAddPaymentMethodToCommit(PaymentMethodDetail data) {
        dataToUpdate.remove(data.getId());
        dataToUpdate.put(data.getId(), data);
        view.getChangesLabel().setText(Integer.toString(dataToUpdate.size()));
        view.getDataGrid().flush();
        view.getDataGrid().redraw();
    }

    /*** ACTION HANDLERS ***
     *
     * Register handlers for widget actions
     */
    @Override
    public void bindView() {
        addPageChangedHandler();
        //
        setNameColumnUpdater();
        setDescriptionColumnUpdater();
        //
        addCommitButtonHandler();
        addRollbackButtonHandler();
        addRefreshButtonHandler();
    }

    /*
     * TABLE PAGE CHANGER
     */
    private void addPageChangedHandler() {
        view.getPageSizeCombo().addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent arg0) {
                int page = view.getPager().getPageStart() / view.getPageSize();
                view.getPager().setPageStart(page * view.getPageSize());
                view.getPager().setPageSize(view.getPageSize());
            }
        });
    }

    /*
     * COLUMN UPDATER - NAME
     */
    private void setNameColumnUpdater() {
        view.getNameColumn().setFieldUpdater(new FieldUpdater<PaymentMethodDetail, String>() {

            @Override
            public void update(int index, PaymentMethodDetail object, String value) {
                if (!object.getName().equals(value)) {
                    if (!originalData.containsKey(object.getId())) {
                        originalData.put(object.getId(), new PaymentMethodDetail(object));
                    }
                    object.setName(value);
//                    eventBus.addPaymentMethodToCommit(object);
                }
            }
        });
    }

    /*
     * COLUMN UPDATER - DESCRIPTION
     */
    private void setDescriptionColumnUpdater() {
        view.getDescriptionColumn().setFieldUpdater(new FieldUpdater<PaymentMethodDetail, String>() {

            @Override
            public void update(int index, PaymentMethodDetail object, String value) {
                if (!object.getDescription().equals(value)) {
                    if (!originalData.containsKey(object.getId())) {
                        originalData.put(object.getId(), new PaymentMethodDetail(object));
                    }
                    object.setDescription(value);
//                    eventBus.addPaymentMethodToCommit(object);
                }
            }
        });
    }

    /*
     * COMMIT
     */
    private void addCommitButtonHandler() {
        view.getCommitBtn().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (Window.confirm("Realy commit changes?")) {
                    view.getDataGrid().setFocus(true);
                    Storage.showLoading(Storage.MSGS.commit());
                    for (Long idx : dataToUpdate.keySet()) {
//                        eventBus.updatePaymentDescription(dataToUpdate.get(idx));
                    }
                    Storage.hideLoading();
                    dataToUpdate.clear();
                    originalData.clear();
                    Window.alert("Changes commited");
                }
            }
        });
    }

    /*
     * ROLLBACK
     */
    private void addRollbackButtonHandler() {
        view.getRollbackBtn().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                dataToUpdate.clear();
                view.getDataGrid().setFocus(true);
                int idx = 0;
                for (PaymentMethodDetail data : originalData.values()) {
                    idx = view.getDataGrid().getVisibleItems().indexOf(data);
                    view.getDataGrid().getVisibleItem(idx).updateWholePaymentMethod(data);
                }
                view.getDataGrid().flush();
                view.getDataGrid().redraw();
                Window.alert(view.getChangesLabel().getText() + " changes rolledback.");
                view.getChangesLabel().setText("0");
                originalData.clear();
            }
        });
    }

    /*
     * REFRESH
     */
    private void addRefreshButtonHandler() {

        view.getRefreshBtn().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (dataToUpdate.isEmpty()) {
                    dataProvider.updateRowCount(0, true);
                    dataProvider = null;
                    view.getDataGrid().flush();
                    view.getDataGrid().redraw();
                    eventBus.getAdminPaymentMethodsCount(searchDataHolder);
                } else {
                    Window.alert("You have some uncommited data. Do commit or rollback first");
                }
            }
        });
    }
}
