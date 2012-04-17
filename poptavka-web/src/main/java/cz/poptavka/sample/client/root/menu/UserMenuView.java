package cz.poptavka.sample.client.root.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

import cz.poptavka.sample.client.resources.StyleResource;
import cz.poptavka.sample.client.root.ReverseCompositeView;
import cz.poptavka.sample.client.root.interfaces.IUserMenuView;
import cz.poptavka.sample.client.root.interfaces.IUserMenuView.IUserMenuPresenter;

public class UserMenuView extends ReverseCompositeView<IUserMenuPresenter> implements IUserMenuView {

    private static UserMenuViewUiBinder uiBinder = GWT.create(UserMenuViewUiBinder.class);

    interface UserMenuViewUiBinder extends UiBinder<Widget, UserMenuView> {
    }
    @UiField
    UListElement menuList;
    @UiField
    Button demands, messages, settings, administration;

    public UserMenuView() {
        initWidget(uiBinder.createAndBindUi(this));
        menuList.addClassName(StyleResource.INSTANCE.layout().homeMenu());
        demands.addStyleName(StyleResource.INSTANCE.layout().selected());
    }

    /**************************************************************************/
    /* UiHanders.                                                             */
    /**************************************************************************/
    @UiHandler("demands")
    public void onClickDemands(ClickEvent e) {
        presenter.goToDemands();
        demandsUserMenuStyleChange();
    }

    @UiHandler("messages")
    public void onClickMessages(ClickEvent e) {
        presenter.goToMessages();
        messagesUserMenuStyleChange();
    }

    @UiHandler("settings")
    public void onClickSettings(ClickEvent e) {
        presenter.goToSettings();
        settingsUserMenuStyleChange();
    }

    @UiHandler("administration")
    public void onClickAdministration(ClickEvent e) {
        presenter.goToAdministration();
        administrationUserMenuStyleChange();
    }

    /**************************************************************************/
    /* Style change methods.                                                  */
    /**************************************************************************/
    /**
     * Loads right styles to menu buttons.
     * @param loadedModule - 0 - Demands
     *                     - 1 - Messages
     *                     - 2 - Settings
     *                     - 3 - Administration
     */
    @Override
    public void userMenuStyleChange(int loadedModule) {
        switch (loadedModule) {
            case 0:
                demandsUserMenuStyleChange();
                break;
            case 1:
                messagesUserMenuStyleChange();
                break;
            case 2:
                settingsUserMenuStyleChange();
                break;
            case 3:
                administrationUserMenuStyleChange();
                break;
            default:
                break;
        }
    }

    /**************************************************************************/
    /* Helper methods.                                                        */
    /**************************************************************************/
    private void demandsUserMenuStyleChange() {
        demands.addStyleName(StyleResource.INSTANCE.layout().selected());
        messages.removeStyleName(StyleResource.INSTANCE.layout().selected());
        settings.removeStyleName(StyleResource.INSTANCE.layout().selected());
        administration.removeStyleName(StyleResource.INSTANCE.layout().selected());
    }

    private void messagesUserMenuStyleChange() {
        demands.removeStyleName(StyleResource.INSTANCE.layout().selected());
        messages.addStyleName(StyleResource.INSTANCE.layout().selected());
        settings.removeStyleName(StyleResource.INSTANCE.layout().selected());
        administration.removeStyleName(StyleResource.INSTANCE.layout().selected());
    }

    private void settingsUserMenuStyleChange() {
        demands.removeStyleName(StyleResource.INSTANCE.layout().selected());
        messages.removeStyleName(StyleResource.INSTANCE.layout().selected());
        settings.addStyleName(StyleResource.INSTANCE.layout().selected());
        administration.removeStyleName(StyleResource.INSTANCE.layout().selected());
    }

    private void administrationUserMenuStyleChange() {
        demands.removeStyleName(StyleResource.INSTANCE.layout().selected());
        messages.removeStyleName(StyleResource.INSTANCE.layout().selected());
        settings.removeStyleName(StyleResource.INSTANCE.layout().selected());
        administration.addStyleName(StyleResource.INSTANCE.layout().selected());
    }
}
