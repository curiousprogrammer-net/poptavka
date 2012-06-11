package com.eprovement.poptavka.client.main;


import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocalizableMessages;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.mvp4g.client.annotation.Presenter;
import com.mvp4g.client.presenter.BasePresenter;

import com.eprovement.poptavka.client.main.common.LoadingPopup;
import com.eprovement.poptavka.client.main.common.category.CategorySelectorPresenter;
import com.eprovement.poptavka.client.home.createDemand.widget.FormDemandAdvPresenter;
import com.eprovement.poptavka.client.home.createDemand.widget.FormDemandBasicPresenter;
import com.eprovement.poptavka.client.main.common.locality.LocalitySelectorPresenter;
import com.eprovement.poptavka.client.main.login.LoginPopupPresenter;
import com.eprovement.poptavka.client.resources.StyleResource;

@Presenter(view = MainView.class)
public class MainPresenter extends BasePresenter<MainPresenter.MainViewInterface, MainEventBus> {

    private static final Logger LOGGER = Logger.getLogger("MainPresenter");

    private static final LocalizableMessages MSGS = GWT.create(LocalizableMessages.class);
    private PopupPanel popup = null;

    public interface MainViewInterface {
        void setBodyWidget(Widget body);

        void toggleMainLayout(boolean switchToUserLayout);

        HTMLPanel getHeaderHolder();

        Anchor getLoginLink();

    }

    private boolean loggedIn = false;

    /** list of reusable widgets **/
    private CategorySelectorPresenter categorySelector = null;
    private LocalitySelectorPresenter localitySelector = null;
    private FormDemandBasicPresenter demandBasicForm = null;
    private FormDemandAdvPresenter demandAdvForm = null;
//    private SupplierServicePresenter supplierService = null;
//    private SupplierInfoPresenter supplierInfo = null;

    private LoginPopupPresenter login;

    @Override
    public void bind() {
        view.getLoginLink().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (loggedIn) {
                    //eventBus.atHome();
                    Cookies.setCookie("user-presenter", "unloaded");
                } else {
                    onInitLoginWindow();
                }
            }
        });
        Window.addWindowClosingHandler(new Window.ClosingHandler() {
            @Override
            public void onWindowClosing(ClosingEvent event) {
                Cookies.setCookie("user-presenter", "unloaded");
            }
        });
    }

    /**
     * Initial Event. Calls all default modules to load: HomeModule
     *
     * For development purposes User Module can be loaded instantly
     */
    public void onStart() {
        GWT.log("Initializing application ... ");
        /** for public part development **/
//        GWT.log("    > Home Module");
        //eventBus.atHome();
        /** for user part development **/
//        LOGGER.info("    > User Module");
//        eventBus.atAccount();
    }

    /**
     * Sets widget to View's body section. Body section can hold one widget only.
     *
     * @param body widget to be inserted
     */
    public void onSetHomeBodyHolderWidget(Widget body) {
        view.setBodyWidget(body);
    }

    public void onBeforeLoad() {
        onLoadingShow(MSGS.loading());
    }

    public void onAfterLoad() {
        onLoadingHide();
    }

    public void onSetPublicLayout() {
        LOGGER.fine("Toggle layout -> PUBLIC");
        eventBus.clearUserOnUnload();
        view.toggleMainLayout(false);
        view.getLoginLink().setText(MSGS.logIn());
        this.loggedIn = false;

    }

    public void onSetUserLayout() {
        LOGGER.fine("Toggle layout -> USER");
        view.toggleMainLayout(true);
        view.getLoginLink().setText(MSGS.logOut());
        this.loggedIn = true;
    }

    public void onAtAccount() {
        this.loggedIn = true;
        view.getLoginLink().setText(MSGS.logOut());
    }
    // TODO do it by cookies?
    public void onAtHome() {
        this.loggedIn = false;
        view.getLoginLink().setText(MSGS.logIn());
    }

    public void onLoadingShow(String loadingMessage) {
        if (!(popup == null)) {
            LoadingPopup popupContent = (LoadingPopup) popup.getWidget();
            popupContent.setMessage(loadingMessage);
        } else {
            createLoadingPopup(loadingMessage);
        }
    }

    public void onLoadingShowWithAnchor(String loadingMessage, Widget anchor) {
        if (popup != null) {
            LoadingPopup popupContent = (LoadingPopup) popup.getWidget();
            popupContent.setMessage(loadingMessage);
        } else {
            createLoadingPopup(loadingMessage, anchor);
        }
    }

    public void onLoadingHide() {
        if (popup != null) {
            popup.hide();
            popup = null;
        }
    }

    private static final int OFFSET_X = 60;
    private static final int OFFSET_Y = 35;

    private void createLoadingPopup(String loadingMessage) {
        popup = new PopupPanel(false, false);
        popup.setStylePrimaryName(StyleResource.INSTANCE.common().loadingPopup());
        popup.setWidget(new LoadingPopup(loadingMessage));
        popup.setPopupPosition((Window.getClientWidth() / 2) - OFFSET_X, (Window.getClientHeight() / 2) - OFFSET_Y);
        popup.show();
    }

    private void createLoadingPopup(String loadingMessage, Widget anchor) {
        popup = new PopupPanel(false, false);
        popup.setStylePrimaryName(StyleResource.INSTANCE.common().loadingPopup());
        popup.setWidget(new LoadingPopup(loadingMessage));
//        popup.setPopupPosition((Window.getClientWidth() / 2) - OFFSET_X, (Window.getClientHeight() / 2) - OFFSET_Y);
        int top = anchor.getAbsoluteTop() + (anchor.getOffsetHeight() / 2);
        int left = anchor.getAbsoluteLeft() + (anchor.getOffsetWidth() / 2) - OFFSET_X;
        popup.showRelativeTo(anchor);
        GWT.log("AbsoluteLeft: " + anchor.getAbsoluteLeft() + " OffsetWidth: " + (anchor.getOffsetWidth()));
        GWT.log("AbsoluteTop: " + anchor.getAbsoluteTop() + " Offsetheight: " + (anchor.getOffsetHeight()));

        GWT.log("L: " + left + " T: " + top);
//        popup.setPopupPosition(left, top);

        popup.show();
    }

    /** multiple presenters handling methods **/
    public void onInitCategoryWidget(SimplePanel holderPanel) {
        if (categorySelector != null) {
            eventBus.removeHandler(categorySelector);
        }
        categorySelector = eventBus.addHandler(CategorySelectorPresenter.class);
        categorySelector.initCategoryWidget(holderPanel);
    }

    public void onInitLocalityWidget(SimplePanel holderPanel) {
        if (localitySelector != null) {
            eventBus.removeHandler(localitySelector);
        }
        localitySelector = eventBus.addHandler(LocalitySelectorPresenter.class);
        localitySelector.initLocalityWidget(holderPanel);
    }

    public void onInitDemandBasicForm(SimplePanel holderWidget) {
//        if (demandBasicForm != null) {
//            eventBus.removeHandler(demandBasicForm);
//        }
//        demandBasicForm = eventBus.addHandler(FormDemandBasicPresenter.class);
//        demandBasicForm.initDemandBasicForm(holderWidget);
    }
//
    public void onInitDemandAdvForm(SimplePanel holderWidget) {
//        if (demandAdvForm != null) {
//            eventBus.removeHandler(demandAdvForm);
//        }
//        demandAdvForm = eventBus.addHandler(FormDemandAdvPresenter.class);
//        demandAdvForm.initDemandAdvForm(holderWidget);
    }

//    public void onInitServiceForm(SimplePanel serviceHolder) {
//        if (supplierService != null) {
//            eventBus.removeHandler(supplierService);
//        }
//        supplierService = eventBus.addHandler(SupplierServicePresenter.class);
//        supplierService.initServiceForm(serviceHolder);
//    }
//
//    public void onInitSupplierForm(SimplePanel supplierInfoHolder) {
//        if (supplierInfo != null) {
//            eventBus.removeHandler(supplierInfo);
//        }
//        supplierInfo = eventBus.addHandler(SupplierInfoPresenter.class);
//        supplierInfo.onInitSupplierForm(supplierInfoHolder);
//    }

    public void onInitLoginWindow() {
        login = eventBus.addHandler(LoginPopupPresenter.class);
        login.onLogin();
    }

}