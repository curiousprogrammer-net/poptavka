package cz.poptavka.sample.client.root.interfaces;

import com.google.gwt.user.client.ui.IsWidget;

public interface IMenuView extends IsWidget {

    public interface IMenuPresenter {

        void goToHomeWelcome();

        void goToHomeDemands();

        void goToHomeSuppliers();

        void goToCreateSupplier();

        void goToCreateDemand();
    }

    void clickHomeMenuStyleChange();

    void clickDemandsMenuStyleChange();

    void clickSuppliersMenuStyleChange();

    void clickCreateSupplierMenuStyleChange();

    void clickCreateDemandMenuStyleChange();
}
