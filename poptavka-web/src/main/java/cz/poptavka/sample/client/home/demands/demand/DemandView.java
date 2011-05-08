package cz.poptavka.sample.client.home.demands.demand;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

import cz.poptavka.sample.domain.demand.Demand;

public class DemandView extends Composite implements
    DemandPresenter.DemandViewInterface  {

    private static DemandViewUiBinder uiBinder = GWT.create(DemandViewUiBinder.class);

    interface DemandViewUiBinder extends UiBinder<Widget, DemandView> { }

    public DemandView() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiField Button buttonAttachments;
    @UiField Button buttonLogin;
    @UiField Button buttonRegister;

    @UiField FlexTable infoTable;

    @UiField Label label1;
    @UiField Label label2;

    @UiField TextArea textArea;

//    @UiField CellList<Object> cellList;

    public DemandView(String firstName) {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("buttonAttachments")
    void onClickAttachments(ClickEvent e) {
        Window.alert("Attachement!");
    }

    @UiHandler("buttonLogin")
    void onClickLogin(ClickEvent e) {
        Window.alert("Login!");
    }

    @UiHandler("buttonRegister")
    void onClickRegister(ClickEvent e) {
        Window.alert("Register");
    }

    public void setDemand(Demand demand) {
        int row = 0;
        infoTable.setWidget(row, 0, new Label("Title: "));
        infoTable.setWidget(row, 1, new Label(demand.getTitle()));

        infoTable.setWidget(row++, 0, new Label("Price: "));
        infoTable.setWidget(row, 1, new Label(demand.getPrice().toString()));

        infoTable.setWidget(row++, 0, new Label("End Date: "));
        infoTable.setWidget(row, 1, new Label(demand.getEndDate().toString()));

        infoTable.setWidget(row++, 0, new Label("Type: "));
        infoTable.setWidget(row, 1, new Label(demand.getType().getDescription()));

        infoTable.setWidget(row++, 0, new Label("Categories: "));
        infoTable.setWidget(row, 1, new Label(demand.getCategories().toString()));

//        infoTable.setWidget(row++, 0, new Label("Client: "));
//        infoTable.setWidget(row, 1, new Label(demand.getClient().getEmail()));
    }
}