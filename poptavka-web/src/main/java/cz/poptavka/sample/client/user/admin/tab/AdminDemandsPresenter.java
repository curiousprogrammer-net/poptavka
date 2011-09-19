/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.poptavka.sample.client.user.admin.tab;

import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.AsyncHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.mvp4g.client.annotation.Presenter;
import com.mvp4g.client.presenter.BasePresenter;

import cz.poptavka.sample.client.user.UserEventBus;
//import cz.poptavka.sample.shared.domain.demand.DemandDetail;
import cz.poptavka.sample.domain.common.OrderType;
import cz.poptavka.sample.shared.domain.demand.FullDemandDetail;
import cz.poptavka.sample.shared.domain.offer.FullOfferDetail;
import cz.poptavka.sample.shared.domain.type.ClientDemandType;
import cz.poptavka.sample.shared.domain.type.DemandStatusType;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author ivan.vlcek
 */
@Presenter(view = AdminDemandsView.class)//, multiple=true)
public class AdminDemandsPresenter
        //      extends LazyPresenter<AdminDemandsPresenter.AdminDemandsInterface, UserEventBus>
        extends BasePresenter<AdminDemandsPresenter.AdminDemandsInterface, UserEventBus>
        implements HasValueChangeHandlers<String> {

    private final static Logger LOGGER = Logger.getLogger("    AdminDemandsPresenter");
    private Map<Long, FullDemandDetail> dataToUpdate = new HashMap<Long, FullDemandDetail>();
    private Map<Long, String> metadataToUpdate = new HashMap<Long, String>();

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public interface AdminDemandsInterface { //extends LazyView {

        Widget getWidgetView();

//        SimplePager getPager();
        DataGrid<FullDemandDetail> getDataGrid();

        ListDataProvider<FullDemandDetail> getDataProvider();

        Column<FullDemandDetail, String> getClientIdColumn();

        Column<FullDemandDetail, String> getDemandTitleColumn();

        Column<FullDemandDetail, String> getDemandTypeColumn();

        Column<FullDemandDetail, String> getDemandStatusColumn();

        Column<FullDemandDetail, Date> getDemandExpirationColumn();

        Column<FullDemandDetail, Date> getDemandEndColumn();

        ClientDemandType[] getDemandTypes();

        DemandStatusType[] getDemandStatuses();

        SingleSelectionModel<FullDemandDetail> getSelectionModel();

        SimplePanel getAdminDemandDetail();

        SimplePanel getAdminOfferDetail();

        SimplePager getPager();

        int getPageSize();

        Button getCommitBtn();

        ListBox getPageSizeCombo();
    }
    private AsyncDataProvider dataProvider = null;
    private int start = 0;

    public void onCreateAdminDemandsAsyncDataProvider(final int totalFound) {
        this.start = 0;
        dataProvider = new AsyncDataProvider<FullDemandDetail>() {

            @Override
            protected void onRangeChanged(HasData<FullDemandDetail> display) {
                display.setRowCount(totalFound);
                start = display.getVisibleRange().getStart();
                int length = display.getVisibleRange().getLength();
                eventBus.getAdminDemands(start, start + length);
                eventBus.loadingHide();
            }
        };
        this.dataProvider.addDataDisplay(view.getDataGrid());
        createAsyncSortHandler();
    }
    private AsyncHandler sortHandler = null;

    public void createAsyncSortHandler() {
        //Moze byt hned na zaciatku? Ak ano , tak potom aj asynchdataprovider by mohol nie?
        sortHandler = new AsyncHandler(view.getDataGrid()) {

            @Override
            public void onColumnSort(ColumnSortEvent event) {
                OrderType orderType = OrderType.DESC;
                Map<String, OrderType> orderColumns = new HashMap<String, OrderType>();
                if (event.isSortAscending()) {
                    orderType = OrderType.ASC;
                }
                Column<FullDemandDetail, String> column = (Column<FullDemandDetail, String>) event.getColumn();
                if (column == null) {
                    return;
                }
                int idx = view.getDataGrid().getColumnIndex(column);

                LOGGER.info("Column IDX: " + idx + " orderType: " + orderType);
//                LOGGER.info("Column NAME: " + FullDemandDetail.SupplierField[idx - 1]);
                orderColumns.put("did", orderType);

                eventBus.getSortedDemands(start, view.getPageSize(), orderColumns);
            }
        };
        view.getDataGrid().addColumnSortHandler(sortHandler);
    }

    public void onInvokeAdminDemands() {
        // TODO ivlcek - ktoru event mam volat skor? Je v tom nejaky rozdiel?
//        eventBus.getAllDemands();
        eventBus.getAdminDemandsCount();
        eventBus.displayAdminContent(view.getWidgetView());
    }

    public void onDisplayAdminTabDemands(List<FullDemandDetail> demands) {
        dataProvider.updateRowData(start, demands);
    }

    public void onRefreshUpdatedDemand(FullDemandDetail demand) {
//        view.getDataGrid().setSize("10%", "10%");
    }

    public void onRefreshUpdatedOffer(FullOfferDetail demand) {
//      view.getDataGrid().setSize("10%", "10%");
    }

    public void onResponseAdminDemandDetail(Widget widget) {
        view.getAdminDemandDetail().setWidget(widget);
    }

    public void onResponseAdminOfferDetail(Widget widget) {
        view.getAdminOfferDetail().setWidget(widget);
    }

    /**
     * Refresh all displays.
     */
    public void refreshDisplays() {
        view.getDataProvider().refresh();
    }

    @Override
    public void bind() {
        view.getDemandTitleColumn().setFieldUpdater(new FieldUpdater<FullDemandDetail, String>() {

            @Override
            public void update(int index, FullDemandDetail object, String value) {
                object.setTitle(value);
                eventBus.addDemandToCommit(object, "demand", "table");
//                refreshDisplays(); TODO - Martin - musi byt refresh?
            }
        });
        view.getDemandTypeColumn().setFieldUpdater(new FieldUpdater<FullDemandDetail, String>() {

            @Override
            public void update(int index, FullDemandDetail object, String value) {
                for (ClientDemandType clientDemandType : view.getDemandTypes()) {
                    if (clientDemandType.getValue().equals(value)) {
                        object.setDemandType(clientDemandType.name());
                        eventBus.addDemandToCommit(object, "demand", "table");
                    }
                }
//                refreshDisplays();
            }
        });
        view.getDemandStatusColumn().setFieldUpdater(new FieldUpdater<FullDemandDetail, String>() {

            @Override
            public void update(int index, FullDemandDetail object, String value) {
                for (DemandStatusType demandStatusType : DemandStatusType.values()) {
                    if (demandStatusType.getValue().equals(value)) {
                        object.setDemandStatus(demandStatusType.name());
                        eventBus.addDemandToCommit(object, "demand", "table");
                    }
                }
//                refreshDisplays();
            }
        });
        view.getDemandExpirationColumn().setFieldUpdater(new FieldUpdater<FullDemandDetail, Date>() {

            @Override
            public void update(int index, FullDemandDetail object, Date value) {
                object.setValidToDate(value);
                eventBus.addDemandToCommit(object, "other", "table");
//                refreshDisplays();
            }
        });
        view.getDemandEndColumn().setFieldUpdater(new FieldUpdater<FullDemandDetail, Date>() {

            @Override
            public void update(int index, FullDemandDetail object, Date value) {
                object.setEndDate(value);
                eventBus.addDemandToCommit(object, "other", "table");
//                refreshDisplays();
            }
        });
        view.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
//                contactForm.setContact(selectionModel.getSelectedObject());
//                eventBus.displayContent(view.getWidgetView());
//                eventBus.getAllDemands();
                if (dataToUpdate.containsKey(view.getSelectionModel().getSelectedObject())) {
                    eventBus.showAdminDemandDetail(dataToUpdate.get(
                            view.getSelectionModel().getSelectedObject().getDemandId()));
                } else {
                    eventBus.showAdminDemandDetail(view.getSelectionModel().getSelectedObject());
                }
                eventBus.setDetailDisplayed(true);
            }
        });
        view.getPageSizeCombo().addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent arg0) {
                int page = view.getPager().getPageStart() / view.getPageSize();
                view.getPager().setPageStart(page * view.getPageSize());
                view.getPager().setPageSize(view.getPageSize());
            }
        });
        view.getCommitBtn().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (Window.confirm("Realy commit changes?")) {
                    eventBus.loadingShow("Commiting");
                    for (Long idx : dataToUpdate.keySet()) {
                        eventBus.updateDemand(dataToUpdate.get(idx), metadataToUpdate.get(idx));
                    }
                    eventBus.loadingHide();
                    dataToUpdate.clear();
                    metadataToUpdate.clear();
                    Window.alert("Demand updated");
                }
            }
        });
    }
    private Boolean detailDisplayed = false;

    public void onAddDemandToCommit(FullDemandDetail data, String dataType, String source) {
        dataToUpdate.remove(data.getDemandId());
        metadataToUpdate.remove(data.getDemandId());
        dataToUpdate.put(data.getDemandId(), data);
        metadataToUpdate.put(data.getDemandId(), dataType);
        if (detailDisplayed) {
            eventBus.showAdminDemandDetail(data);
        }
        if (source.equals("detail")) {
            List<FullDemandDetail> list = view.getDataGrid().getVisibleItems();
            int idx = list.indexOf(data);
            list.remove(idx);
            list.add(idx, data);
            dataProvider.updateRowData(start, list);
            view.getDataGrid().redraw();
        }
    }

    public void onSetDetailDisplayed(Boolean displayed) {
        detailDisplayed = displayed;
    }
}