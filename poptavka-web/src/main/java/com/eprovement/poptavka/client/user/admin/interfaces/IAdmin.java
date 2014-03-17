/*
 * Copyright (C) 2014, eProvement s.r.o. All rights reserved.
 */
package com.eprovement.poptavka.client.user.admin.interfaces;

import com.eprovement.poptavka.client.root.interfaces.HandleResizeEvent;
import com.eprovement.poptavka.client.root.toolbar.ProvidesToolbar;
import com.eprovement.poptavka.shared.search.SearchModuleDataHolder;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.mvp4g.client.history.NavigationConfirmationInterface;
import com.mvp4g.client.view.LazyView;

/**
 * @author Martin Slavkovsky
 *         Date: 14.01.2014
 */
public interface IAdmin {

    public enum AdminWidget {

        NEW_DEMANDS,
        ASSIGNED_DEMANDS,
        ACTIVE_DEMANDS;
    }

    public interface Gateway {

        String goToAdminModule(SearchModuleDataHolder searchDataHolder, AdminWidget loadWidget);

        void setClientMenuActStyle(AdminWidget widget);
    }

    public interface Presenter extends HandleResizeEvent, NavigationConfirmationInterface {

        void onGoToAdminModule(SearchModuleDataHolder filter, AdminWidget loadWidget);

        void onSetClientMenuActStyle(AdminWidget widget);
    }

    public interface View extends LazyView, IsWidget, ProvidesToolbar {

        void setClientMenuActStyle(AdminWidget widget);

        void setContent(Widget contentWidget);

        Button getNewDemandsBtn();

        Button getAssignedDemandsBtn();

        Button getActiveDemandsBtn();

        Button getDemandsButton();

        Button getClientsButton();

        Button getOffersButton();

        Button getSuppliersButton();

        Button getAccessRoleButton();

        Button getEmailActivationButton();

        Button getInvoiceButton();

        Button getMessageButton();

        Button getPaymentMethodButton();

        Button getPermissionButton();

        Button getPreferenceButton();

        Button getProblemButton();

        SimplePanel getContentContainer();

        Widget getWidgetView();
    }
}
