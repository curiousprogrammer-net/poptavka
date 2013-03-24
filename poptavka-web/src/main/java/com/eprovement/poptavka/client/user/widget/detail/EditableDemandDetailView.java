package com.eprovement.poptavka.client.user.widget.detail;

import com.eprovement.poptavka.client.common.ChangeMonitor;
import com.eprovement.poptavka.client.common.ListChangeMonitor;
import com.eprovement.poptavka.client.common.UrgencySelectorView;
import com.eprovement.poptavka.client.common.category.CategoryCell;
import com.eprovement.poptavka.client.common.locality.LocalityCell;
import com.eprovement.poptavka.client.common.session.Storage;
import com.eprovement.poptavka.client.common.validation.ProvidesValidate;
import com.eprovement.poptavka.resources.StyleResource;
import com.eprovement.poptavka.shared.domain.CategoryDetail;
import com.eprovement.poptavka.shared.domain.ChangeDetail;
import com.eprovement.poptavka.shared.domain.LocalityDetail;
import com.eprovement.poptavka.shared.domain.demand.FullDemandDetail;
import com.eprovement.poptavka.shared.domain.demand.FullDemandDetail.DemandField;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class EditableDemandDetailView extends Composite implements
        EditableDemandDetailPresenter.IEditableDemandDetailView, ProvidesValidate {

    /**************************************************************************/
    /* UiBinder                                                               */
    /**************************************************************************/
    interface DemandDetailViewUiBinder extends UiBinder<Widget, EditableDemandDetailView> {
    }
    private static DemandDetailViewUiBinder uiBinder = GWT.create(DemandDetailViewUiBinder.class);
    /**************************************************************************/
    /* Attributes                                                               */
    /**************************************************************************/
    /** UiBinder attributes. **/
    @UiField(provided = true) ChangeMonitor titleMonitor, priceMonitor, endDateMonitor;
    @UiField(provided = true) ChangeMonitor maxOffersMonitor, minRatingMonitor, descriptionMonitor;
    @UiField(provided = true) ListChangeMonitor categoriesMonitor, localitiesMonitor;
    @UiField(provided = true) CellList categories, localities;
    @UiField UrgencySelectorView urgencySelector;
    @UiField FluidRow editButtonsPanel;
    @UiField Button editCatBtn, editLocBtn, submitButton, cancelButton;
    /** Class attributes. **/
    private List<ChangeMonitor> monitors;
    private long demandId;
    private PopupPanel selectorWidgetPopup;

    /**************************************************************************/
    /* INITIALIZATON                                                          */
    /**************************************************************************/
    //Constructor
    @Override
    public void createView() {
        createSelectorWidgetPopup();

        categories = new CellList<CategoryDetail>(new CategoryCell(CategoryCell.DISPLAY_COUNT_DISABLED));
        localities = new CellList<LocalityDetail>(new LocalityCell(LocalityCell.DISPLAY_COUNT_DISABLED));

        initValidationMonitors();

        initWidget(uiBinder.createAndBindUi(this));

        ((DateBox) endDateMonitor.getWidget()).setFormat(new DateBox.DefaultFormat(Storage.DATE_FORMAT));

        StyleResource.INSTANCE.detailViews().ensureInjected();
        StyleResource.INSTANCE.common().ensureInjected();
    }

    private void initValidationMonitors() {
        titleMonitor = createDemandChangeMonitor(DemandField.TITLE);
        categoriesMonitor = createDemandListChangeMonitor(DemandField.CATEGORIES);
        localitiesMonitor = createDemandListChangeMonitor(DemandField.LOCALITIES);
        priceMonitor = createDemandChangeMonitor(DemandField.PRICE);
        endDateMonitor = createDemandChangeMonitor(DemandField.END_DATE);
        maxOffersMonitor = createDemandChangeMonitor(DemandField.MAX_OFFERS);
        minRatingMonitor = createDemandChangeMonitor(DemandField.MIN_RATING);
        descriptionMonitor = createDemandChangeMonitor(DemandField.DESCRIPTION);
        monitors = Arrays.asList(
                titleMonitor, priceMonitor, endDateMonitor, maxOffersMonitor, minRatingMonitor, descriptionMonitor);
    }

    private ChangeMonitor createDemandChangeMonitor(DemandField fieldField) {
        return new ChangeMonitor<FullDemandDetail>(
                FullDemandDetail.class, new ChangeDetail(fieldField.getValue()));
    }

    private ListChangeMonitor createDemandListChangeMonitor(DemandField fieldField) {
        return new ListChangeMonitor<FullDemandDetail>(
                FullDemandDetail.class, new ChangeDetail(fieldField.getValue()));
    }

    //Popup
    private void createSelectorWidgetPopup() {
        selectorWidgetPopup = new PopupPanel(true);
        selectorWidgetPopup.setSize("300px", "300px");
        selectorWidgetPopup.setGlassEnabled(true);
        selectorWidgetPopup.hide();
    }

    /**************************************************************************/
    /* METHODS                                                                */
    /**************************************************************************/
    @Override
    public void resetFields() {
        for (ChangeMonitor monitor : monitors) {
            monitor.reset();
        }
        urgencySelector.setChangeStyle(false);
        categoriesMonitor.reset();
        localitiesMonitor.reset();
    }

    @Override
    public void revertFields() {
        for (ChangeMonitor monitor : monitors) {
            monitor.revert();
        }
        urgencySelector.revert(null);
        categoriesMonitor.revert();
        localitiesMonitor.revert();
    }

    /**************************************************************************/
    /* SETTERS                                                                */
    /**************************************************************************/
    public void setDemanDetail(FullDemandDetail demandDetail) {
        GWT.log("detail detail" + demandDetail.toString());
        demandId = demandDetail.getDemandId();
        titleMonitor.setBothValues(demandDetail.getTitle());
        priceMonitor.setBothValues(demandDetail.getPrice());
        endDateMonitor.setBothValues(demandDetail.getEndDate());
        urgencySelector.setValidTo(demandDetail.getValidTo());
        categoriesMonitor.setBothValues(demandDetail.getCategories());
        localitiesMonitor.setBothValues(demandDetail.getLocalities());
        maxOffersMonitor.setBothValues(demandDetail.getMaxSuppliers());
        minRatingMonitor.setBothValues(demandDetail.getMinRating());
        descriptionMonitor.setBothValues(demandDetail.getDescription());
    }

    @Override
    public FullDemandDetail updateDemandDetail(FullDemandDetail demandToUpdate) {
        demandToUpdate.setTitle((String) titleMonitor.getValue());
        demandToUpdate.setPrice((BigDecimal) priceMonitor.getValue());
        demandToUpdate.setEndDate((Date) endDateMonitor.getValue());
        demandToUpdate.setValidTo(urgencySelector.getValidTo());
        demandToUpdate.setCategories((List<CategoryDetail>) categoriesMonitor.getValue());
        demandToUpdate.setLocalities((List<LocalityDetail>) localitiesMonitor.getValue());
        demandToUpdate.setMaxSuppliers((Integer) maxOffersMonitor.getValue());
        demandToUpdate.setMinRating((Integer) minRatingMonitor.getValue());
        demandToUpdate.setDescription((String) descriptionMonitor.getValue());
        return demandToUpdate;
    }

    @Override
    public void setChangeHandler(ChangeHandler handler) {
        for (ChangeMonitor monitor : monitors) {
            monitor.addChangeHandler(handler);
        }
    }

    @Override
    public void setListChangeHandler(ChangeHandler handler) {
        categoriesMonitor.addChangeHandler(handler);
        localitiesMonitor.addChangeHandler(handler);
    }

    /**
     * Need for CategorySelector when closing to set newly chosen categories.
     * @param categories
     */
    @Override
    public void setCategories(List<CategoryDetail> categories) {
        categoriesMonitor.setValue(categories);
    }

    /**
     * Need for LocalitySelector when closing to set newly chosen localities.
     * @param loclaities
     */
    @Override
    public void setLocalities(List<LocalityDetail> localities) {
        localitiesMonitor.setValue(localities);
    }

    /**************************************************************************/
    /* GETTER                                                                 */
    /**************************************************************************/
    /** Button. **/
    @Override
    public Button getEditCatBtn() {
        return editCatBtn;
    }

    @Override
    public Button getEditLocBtn() {
        return editLocBtn;
    }

    @Override
    public Button getSubmitButton() {
        return submitButton;
    }

    @Override
    public Button getCancelButton() {
        return cancelButton;
    }

    /** Panels. **/
    @Override
    public FluidRow getEditButtonsPanel() {
        return editButtonsPanel;
    }

    @Override
    public PopupPanel getSelectorWidgetPopup() {
        return selectorWidgetPopup;
    }

    /** Data. **/
    @Override
    public long getDemandId() {
        return demandId;
    }

    @Override
    public ArrayList<CategoryDetail> getCategories() {
        return (ArrayList<CategoryDetail>) categoriesMonitor.getValue();
    }

    @Override
    public ArrayList<LocalityDetail> getLocalities() {
        return (ArrayList<LocalityDetail>) localitiesMonitor.getValue();
    }

    /** Validation. **/
    @Override
    public boolean isValid() {
        boolean valid = true;
        for (ChangeMonitor monitor : monitors) {
            valid = monitor.isValid() && valid;
        }
        return valid && categoriesMonitor.isValid() && localitiesMonitor.isValid();
    }

    /** Widget view. **/
    @Override
    public Widget asWidget() {
        return this;
    }
}
