package com.eprovement.poptavka.client.root.interfaces;

import com.google.gwt.user.client.ui.IsWidget;

public interface IUserMenuView extends IsWidget {

    public interface IUserMenuPresenter {

        void goToAdministration();

        void goToSettings();

        void goToMessages();

        void goToClient();

        void goToSupplier();
    }

    void userMenuStyleChange(int loadedModule);

    void setTabVisibility(int module, boolean visible);
}
