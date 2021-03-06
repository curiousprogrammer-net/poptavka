/*
 * Copyright (C), eProvement s.r.o. All rights reserved.
 */
package com.eprovement.poptavka.client.catLocSelector;

import com.eprovement.poptavka.client.common.security.SecuredAsyncCallback;
import com.eprovement.poptavka.client.service.demand.CatLocSelectorRPCServiceAsync;
import com.eprovement.poptavka.shared.selectors.catLocSelector.ICatLocDetail;
import com.google.inject.Inject;
import com.mvp4g.client.annotation.EventHandler;
import com.mvp4g.client.event.BaseEventHandler;
import java.util.LinkedList;

/**
 * Handle RPC calls for CatLocSelector Handler.
 * @author Martin Slavkovsky
 */
@EventHandler
public class CatLocSelectorHandler extends BaseEventHandler<CatLocSelectorEventBus> {

    /**************************************************************************/
    /* Inject Services                                                        */
    /**************************************************************************/
    @Inject
    CatLocSelectorRPCServiceAsync catLocSelectorService;

    /**************************************************************************/
    /* Business events                                                        */
    /**************************************************************************/
    /**
     * There is not need to request for all selected hierarchies,
     * because at the same time, there can be opened only one path.
     * @param CatLocDetail - catLoc or locality which hierarchy we want to retrieved
     */
    public void onRequestHierarchy(int selectorType, ICatLocDetail catLoc, final int instanceId) {
        catLocSelectorService.requestHierarchy(selectorType, catLoc,
                new SecuredAsyncCallback<LinkedList<ICatLocDetail>>(eventBus) {
                @Override
                public void onSuccess(LinkedList<ICatLocDetail> result) {
                    eventBus.responseHierarchy(result, instanceId);
                }
            });
    }
}
