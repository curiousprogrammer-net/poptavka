/*
 * Copyright (C), eProvement s.r.o. All rights reserved.
 */
package com.eprovement.poptavka.client.home.createSupplier;

import com.eprovement.poptavka.client.catLocSelector.others.CatLocSelectorBuilder;
import com.eprovement.poptavka.client.common.GATracker;
import com.eprovement.poptavka.client.common.session.Constants;
import com.eprovement.poptavka.client.common.validation.ProvidesValidate;
import com.eprovement.poptavka.client.home.createSupplier.interfaces.ISupplierCreationModule;
import com.eprovement.poptavka.resources.StyleResource;
import com.eprovement.poptavka.shared.domain.supplier.FullSupplierDetail;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.LocalizableMessages;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.mvp4g.client.annotation.Presenter;
import com.mvp4g.client.history.NavigationEventCommand;
import com.mvp4g.client.presenter.LazyPresenter;
import java.util.logging.Logger;

/**
 * Supplier creation presenter.
 *
 * @author Martin Slavkovsky
 */
@Presenter(view = SupplierCreationView.class)
public class SupplierCreationPresenter
    extends LazyPresenter<ISupplierCreationModule.View, SupplierCreationEventBus>
    implements ISupplierCreationModule.Presenter {

    /**************************************************************************/
    /* Attributes                                                             */
    /**************************************************************************/
    private static final int FIRST_TAB_USER_REGISTRATION = 0;
    private static final int SECOND_TAB_CATEGORY = 1;
    private static final int THIRD_TAB_LOCALITY = 2;
    private final static Logger LOGGER = Logger.getLogger(ISupplierCreationModule.NAME);
    private static final LocalizableMessages MSGS = GWT.create(LocalizableMessages.class);
    private int maxSelectedTab = 1;
    private int instaceIdCategories;
    private int instaceIdLocalities;

    /**************************************************************************/
    /* General Module events                                                  */
    /**************************************************************************/
    public void onStart() {
        // nothing by default
    }

    /**
     * Sets body, toolbar, footer, search bar on each module forwad event.
     */
    public void onForward() {
        LOGGER.info("SupplierCreationPresenter loaded");
        GATracker.trackPageview(Window.Location.getHref());
        GATracker.trackEvent(ISupplierCreationModule.NAME, ISupplierCreationModule.GA_EVENT_USER);
        eventBus.setBody(view.asWidget());
        eventBus.setToolbarContent("Became Professional", null);
        eventBus.setFooter(view.getFooterPanel());
        eventBus.resetSearchBar(null);
        eventBus.menuStyleChange(Constants.CREATE_SUPPLIER);
        maxSelectedTab = 1;
        view.getAgreedCheck().setValue(false);
        view.getRegisterButton().setEnabled(true);
    }

    @Override
    public void confirm(NavigationEventCommand event) {
        // nothing by default
    }

    /**************************************************************************/
    /* Bind handlers                                                          */
    /**************************************************************************/
    @Override
    public void bindView() {
        addMainPanelBeforeSelectionHandler();
        addMainPanelSelectionHandler();
        addRegisterButtonHandler();
        addTermsAndConditionsHandler();
    }

    /**
     * Binds tab layout before selection handlers.
     * Check if current step is valid before continuing.
     */
    private void addMainPanelBeforeSelectionHandler() {
        view.getMainPanel().addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            @Override
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                int eventItem = event.getItem();
                //If there is unvalidated tab between clicked tabs, cancel event.
                if (maxSelectedTab + 1 >= eventItem) {
                    //if selecting other tab, check if present tab is valid
                    if (view.getMainPanel().getSelectedIndex() < eventItem) {
                        //if present tab is valid, continue
                        if (!canContinue(view.getMainPanel().getSelectedIndex())) {
                            displayTooltip();
                            event.cancel();
                        }
                        //define how far am i allowed to click
                        //If there is unvalidated tab between clicked tabs, cancel event.
                        if (maxSelectedTab < eventItem) {
                            maxSelectedTab = eventItem;
                        }
                    }
                } else {
                    event.cancel();
                }
            }
        });
    }

    /**
     * Binds tab layout selection handler.
     * Inits particular step's widget.
     */
    private void addMainPanelSelectionHandler() {
        view.getMainPanel().addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                addMainPanelSelectionHandlerInner(event);
            }
        });
    }

    /**
     * Tab layout selection handler inner class.
     * Inits particular step's widget.
     */
    private void addMainPanelSelectionHandlerInner(SelectionEvent<Integer> event) {
        switch (event.getSelectedItem()) {
            case FIRST_TAB_USER_REGISTRATION:
                LOGGER.info(" -> Login Or Registration Selection Form");
                eventBus.checkCompanySelected();
                break;
            case SECOND_TAB_CATEGORY:
                LOGGER.info(" -> Category Widget");
                GATracker.trackEvent(ISupplierCreationModule.NAME, ISupplierCreationModule.GA_EVENT_CATEGORY);
                if (view.getHolderPanel(SECOND_TAB_CATEGORY).getWidget() == null) {
                    CatLocSelectorBuilder builder = new CatLocSelectorBuilder.Builder(Constants.CREATE_SUPPLIER)
                        .initCategorySelector()
                        .initSelectorManager()
                        .withCheckboxesOnLeafsAndLeafsParent()
                        .displayCountOfSuppliers()
                        .setSelectionRestriction(Constants.REGISTER_MAX_CATEGORIES)
                        .build();
                    instaceIdCategories = builder.getInstanceId();
                    eventBus.initCatLocSelector(view.getHolderPanel(SECOND_TAB_CATEGORY), builder);
                }
                setHeightSelector();
                break;
            case THIRD_TAB_LOCALITY:
                LOGGER.info(" -> Locality Widget");
                GATracker.trackEvent(ISupplierCreationModule.NAME, ISupplierCreationModule.GA_EVENT_LOCALITY);
                if (view.getHolderPanel(THIRD_TAB_LOCALITY).getWidget() == null) {
                    CatLocSelectorBuilder builder = new CatLocSelectorBuilder.Builder(Constants.CREATE_SUPPLIER)
                        .initLocalitySelector()
                        .initSelectorManager()
                        .withCheckboxes()
                        .displayCountOfSuppliers()
                        .setSelectionRestriction(Constants.REGISTER_MAX_LOCALITIES)
                        .build();
                    instaceIdLocalities = builder.getInstanceId();
                    eventBus.initCatLocSelector(view.getHolderPanel(THIRD_TAB_LOCALITY), builder);
                }
                setHeightSelector();
                break;
            default:
                break;
        }
    }

    /**
     * Binds register button hadnler.
     */
    private void addRegisterButtonHandler() {
        view.getRegisterButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                GATracker.trackEvent(ISupplierCreationModule.NAME, ISupplierCreationModule.GA_EVENT_NEW_SUPPLIER);
                if (canContinue(THIRD_TAB_LOCALITY)) {
                    LOGGER.fine("register him!");
                    view.getRegisterButton().setEnabled(false);
                    registerSupplier();
                } else {
                    LOGGER.fine("cannot continue");
                    displayTooltip();
                }
            }
        });
    }

    private void addTermsAndConditionsHandler() {
        view.getTermsAndConditionsButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                eventBus.showTermsAndConditionsPopup();
            }
        });
    }

    /**************************************************************************/
    /* Business events                                                        */
    /**************************************************************************/
    /**
     * {@inheritDoc}
     */
    @Override
    public void onGoToCreateSupplierModule() {
        view.getMainPanel().selectTab(FIRST_TAB_USER_REGISTRATION);
        if (view.getHolderPanel(FIRST_TAB_USER_REGISTRATION).getWidget() == null) {
            eventBus.initUserRegistration(view.getHolderPanel(FIRST_TAB_USER_REGISTRATION));
        }
        view.reset();
        setHeightRegistration();
    }

    /**
     * Sets registration form height.
     * @param company - true if company form is visible
     */
    public void onSetUserRegistrationHeight(boolean company) {
        if (company) {
            setHeightRegistrationExtended();
        } else {
            setHeightRegistration();
        }
    }

    /**************************************************************************/
    /* Helper methods                                                         */
    /**************************************************************************/
    /**
     * Register supplier.
     * Creates supplier detail and fill its attributes by calling fill events of
     * each step.
     */
    private void registerSupplier() {
        FullSupplierDetail newSupplier = new FullSupplierDetail();

        eventBus.fillBusinessUserDetail(newSupplier.getUserData());
        eventBus.fillCatLocs(newSupplier.getCategories(), instaceIdCategories);
        eventBus.fillCatLocs(newSupplier.getLocalities(), instaceIdLocalities);

        eventBus.requestRegisterSupplier(newSupplier);

        eventBus.loadingShow(MSGS.progressRegisterSupplier());
    }

    /**
     * Check if current widget's components are valid before continuing.
     * @param step - current step
     * @return true if valid, false otherwise
     */
    private boolean canContinue(int step) {
        boolean valid = true;
        valid = ((ProvidesValidate) view.getHolderPanel(step).getWidget()).isValid();
        //If firt tab, check if condition checked?
        if (step == FIRST_TAB_USER_REGISTRATION) {
            valid = view.isValid();
        }
        return valid;
    }

    /**
     * Displays tooltip on next button if something is missing.
     */
    private void displayTooltip() {
        view.getNextBtnTooltip(view.getMainPanel().getSelectedIndex()).show();
        Timer timer = new Timer() {

            @Override
            public void run() {
                view.getNextBtnTooltip(view.getMainPanel().getSelectedIndex()).hide();
            }
        };
        timer.schedule(Constants.VALIDATION_TOOLTIP_DISPLAY_TIME);
    }

    /**
     * Sets <b>selector</b> tab layout height.
     */
    private void setHeightSelector() {
        clearHeight();
        view.getMainPanel().addStyleName(StyleResource.INSTANCE.createTabPanel().heightSelector());
    }

    /**
     * Sets <b>registration</b> tab layout height.
     */
    private void setHeightRegistration() {
        clearHeight();
        view.getMainPanel().addStyleName(StyleResource.INSTANCE.createTabPanel().heightRegistration());
    }

    /**
     * Sets <b>registration extended</b> tab layout height.
     */
    private void setHeightRegistrationExtended() {
        clearHeight();
        view.getMainPanel().addStyleName(StyleResource.INSTANCE.createTabPanel().heightRegistrationExtended());
    }

    /**
     * Clear tab layout height.
     */
    private void clearHeight() {
        view.getMainPanel().removeStyleName(StyleResource.INSTANCE.createTabPanel().heightBasic());
        view.getMainPanel().removeStyleName(StyleResource.INSTANCE.createTabPanel().heightSelector());
        view.getMainPanel().removeStyleName(StyleResource.INSTANCE.createTabPanel().heightRegistration());
        view.getMainPanel().removeStyleName(StyleResource.INSTANCE.createTabPanel().heightRegistrationExtended());
    }
}
