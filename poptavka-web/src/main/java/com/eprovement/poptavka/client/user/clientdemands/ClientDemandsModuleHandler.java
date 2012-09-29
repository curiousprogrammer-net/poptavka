package com.eprovement.poptavka.client.user.clientdemands;

import com.eprovement.poptavka.client.common.security.SecuredAsyncCallback;
import com.eprovement.poptavka.client.common.session.Constants;
import com.eprovement.poptavka.client.common.session.Storage;
import com.eprovement.poptavka.client.service.demand.ClientDemandsModuleRPCServiceAsync;
import com.eprovement.poptavka.client.user.widget.grid.UniversalAsyncGrid;
import com.eprovement.poptavka.shared.domain.adminModule.OfferDetail;
import com.eprovement.poptavka.shared.domain.clientdemands.ClientDemandConversationDetail;
import com.eprovement.poptavka.shared.domain.clientdemands.ClientDemandDetail;
import com.eprovement.poptavka.shared.domain.demand.FullDemandDetail;
import com.eprovement.poptavka.shared.domain.offer.FullOfferDetail;
import com.eprovement.poptavka.shared.search.SearchDefinition;
import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.mvp4g.client.annotation.EventHandler;
import com.mvp4g.client.event.BaseEventHandler;
import java.util.ArrayList;
import java.util.List;

@EventHandler
public class ClientDemandsModuleHandler extends BaseEventHandler<ClientDemandsModuleEventBus> {

    @Inject
    private ClientDemandsModuleRPCServiceAsync clientDemandsService;

    //*************************************************************************/
    // Overriden methods of IEventBusData interface.                          */
    //*************************************************************************/
    public void onGetDataCount(final UniversalAsyncGrid grid, SearchDefinition searchDefinition) {
        switch (Storage.getCurrentlyLoadedView()) {
            case Constants.CLIENT_DEMANDS:
                getClientDemandsCount(grid, searchDefinition);
                break;
            case Constants.CLIENT_DEMAND_DISCUSSIONS:
                getClientDemandConversationsCount(grid, searchDefinition);
                break;
            case Constants.CLIENT_OFFERED_DEMANDS:
                getClientOfferedDemandsCount(grid, searchDefinition);
                break;
            case Constants.CLIENT_OFFERED_DEMAND_OFFERS:
                getClientOfferedDemandOffersCount(grid, searchDefinition);
                break;
            case Constants.CLIENT_ASSIGNED_DEMANDS:
                getClientAssignedDemandsCount(grid, searchDefinition);
                break;
            default:
                break;
        }
    }

    public void onGetData(SearchDefinition searchDefinition) {
        switch (Storage.getCurrentlyLoadedView()) {
            case Constants.CLIENT_DEMANDS:
                getClientDemands(searchDefinition);
                break;
            case Constants.CLIENT_DEMAND_DISCUSSIONS:
                getClientDemandConversations(searchDefinition);
                break;
            case Constants.CLIENT_OFFERED_DEMANDS:
                getClientOfferedDemands(searchDefinition);
                break;
            case Constants.CLIENT_OFFERED_DEMAND_OFFERS:
                getClientOfferedDemandOffers(searchDefinition);
                break;
            case Constants.CLIENT_ASSIGNED_DEMANDS:
                getClientAssignedDemands(searchDefinition);
                break;
            default:
                break;
        }
    }

    //*************************************************************************/
    // Retrieving methods - CLIENT PROJECTS                                   */
    //*************************************************************************/
    private void getClientDemandsCount(final UniversalAsyncGrid grid, SearchDefinition searchDefinition) {
        clientDemandsService.getClientDemandsCount(Storage.getUser().getUserId(), searchDefinition,
                new SecuredAsyncCallback<Long>(eventBus) {
                    @Override
                    public void onSuccess(Long result) {
                        grid.createAsyncDataProvider(result.intValue());
                    }
                });
    }

    private void getClientDemands(SearchDefinition searchDefinition) {
        clientDemandsService.getClientDemands(
                Storage.getUser().getUserId(), searchDefinition,
                new SecuredAsyncCallback<List<ClientDemandDetail>>(eventBus) {
                    @Override
                    public void onSuccess(List<ClientDemandDetail> result) {
                        eventBus.displayClientDemands(result);
                    }
                });
    }

    //*************************************************************************/
    // Retrieving methods - CLIENT PROJECT CONVERSATIONS                      */
    //*************************************************************************/
    private void getClientDemandConversationsCount(final UniversalAsyncGrid grid, SearchDefinition searchDefinition) {
        clientDemandsService.getClientDemandConversationsCount(
                Storage.getUser().getUserId(), Storage.getDemandId(), searchDefinition,
                new SecuredAsyncCallback<Long>(eventBus) {
                    @Override
                    public void onSuccess(Long result) {
                        grid.createAsyncDataProvider(result.intValue());
                    }
                });
    }

    private void getClientDemandConversations(SearchDefinition searchDefinition) {
        clientDemandsService.getClientDemandConversations(
                Storage.getUser().getUserId(), Storage.getDemandId(), searchDefinition,
                new SecuredAsyncCallback<List<ClientDemandConversationDetail>>(eventBus) {
                    @Override
                    public void onSuccess(List<ClientDemandConversationDetail> result) {
                        eventBus.displayClientDemandConversations(result);
                    }
                });
    }

    //*************************************************************************/
    // Retrieving methods - CLIENT OFFERED PROJECTS                           */
    //*************************************************************************/
    private void getClientOfferedDemandsCount(final UniversalAsyncGrid grid, SearchDefinition searchDefinition) {
        clientDemandsService.getClientOfferedDemandsCount(Storage.getUser().getUserId(), searchDefinition,
                new SecuredAsyncCallback<Long>(eventBus) {
                    @Override
                    public void onSuccess(Long result) {
                        grid.createAsyncDataProvider(result.intValue());
                    }
                });
    }

    private void getClientOfferedDemands(SearchDefinition searchDefinition) {
        clientDemandsService.getClientOfferedDemands(
                Storage.getUser().getUserId(), Storage.getDemandId(), searchDefinition,
                new SecuredAsyncCallback<List<ClientDemandDetail>>(eventBus) {
                    @Override
                    public void onSuccess(List<ClientDemandDetail> result) {
                        eventBus.displayClientOfferedDemands(result);
                    }
                });
    }

    //*************************************************************************/
    // Retrieving methods - CLIENT PROJECT CONTESTANTS                        */
    //*************************************************************************/
    private void getClientOfferedDemandOffersCount(final UniversalAsyncGrid grid, SearchDefinition searchDefinition) {
        clientDemandsService.getClientOfferedDemandOffersCount(
                Storage.getUser().getUserId(), Storage.getDemandId(), searchDefinition,
                new SecuredAsyncCallback<Long>(eventBus) {
                    @Override
                    public void onSuccess(Long result) {
                        grid.createAsyncDataProvider(result.intValue());
                    }
                });
    }

    private void getClientOfferedDemandOffers(SearchDefinition searchDefinition) {
        clientDemandsService.getClientOfferedDemandOffers(
                Storage.getUser().getUserId(), Storage.getDemandId(), searchDefinition,
                new SecuredAsyncCallback<List<FullOfferDetail>>(eventBus) {
                    @Override
                    public void onSuccess(List<FullOfferDetail> result) {
                        eventBus.displayClientOfferedDemandOffers(result);
                    }
                });
    }

    /**************************************************************************/
    /* Retrieving methods - CLIENT PROJECTS                                   */
    /**************************************************************************/
    private void getClientAssignedDemandsCount(final UniversalAsyncGrid grid, SearchDefinition searchDefinition) {
        clientDemandsService.getClientAssignedDemandsCount(
                Storage.getUser().getUserId(), searchDefinition,
                new SecuredAsyncCallback<Long>(eventBus) {
                    @Override
                    public void onSuccess(Long result) {
                        grid.createAsyncDataProvider(result.intValue());
                    }
                });
    }

    private void getClientAssignedDemands(SearchDefinition searchDefinition) {
        clientDemandsService.getClientAssignedDemands(
                Storage.getUser().getUserId(), searchDefinition,
                new SecuredAsyncCallback<List<FullOfferDetail>>(eventBus) {
                    @Override
                    public void onSuccess(List<FullOfferDetail> result) {
                        eventBus.displayClientAssignedDemands(result);
                    }
                });
    }

    /**************************************************************************/
    /* Other                                                                  */
    /**************************************************************************/
    /**
     * Changes demands Read status. Changes are displayed immediately on frontend. No onSuccess code is needed.
     *
     * @param selectedIdList list of demands which read status should be changed
     * @param newStatus of demandList
     */
    public void onRequestReadStatusUpdate(List<Long> selectedIdList, boolean newStatus) {
        clientDemandsService.setMessageReadStatus(selectedIdList, newStatus, new SecuredAsyncCallback<Void>(eventBus) {
            @Override
            public void onSuccess(Void result) {
                //Empty by default
            }
        });
    }

    /**
     * Changes demands star status. Changes are displayed immediately on frontend. No onSuccess code is needed.
     *
     * @param userMessageIdList list od demands which star status should be changed
     * @param newStatus of demandList
     */
    public void onRequestStarStatusUpdate(List<Long> userMessageIdList, boolean newStatus) {
        clientDemandsService.setMessageStarStatus(userMessageIdList, newStatus,
                new SecuredAsyncCallback<Void>(eventBus) {
                @Override
                public void onSuccess(Void result) {
                    //Empty by default
                }
            });
    }

    public void onRequestCloseDemand(FullDemandDetail demandDetail) {
        clientDemandsService.closeDemand(demandDetail, new SecuredAsyncCallback<ArrayList<Void>>(eventBus) {
            @Override
            public void onSuccess(ArrayList<Void> result) {
                //Empty by default
            }
        });
    }

    public void onRequestAcceptOffer(FullOfferDetail fullOfferDetail) {
        clientDemandsService.acceptOffer(fullOfferDetail, new SecuredAsyncCallback<ArrayList<Void>>(eventBus) {
            @Override
            public void onSuccess(ArrayList<Void> result) {
                //Empty by default
            }
        });
    }

    public void onRequestDeclineOffer(OfferDetail offerDetail) {
        clientDemandsService.declineOffer(offerDetail, new SecuredAsyncCallback<ArrayList<Void>>(eventBus) {
            @Override
            public void onSuccess(ArrayList<Void> result) {
                //Empty by default
            }
        });
    }

    /**************************************************************************/
    /* Button actions - messaging.                                            */
    /**************************************************************************/
    public void onGetOfferStatusChange(OfferDetail offerDetail) {
        GWT.log("STATE: " + offerDetail.getState());
        clientDemandsService.changeOfferState(offerDetail, new SecuredAsyncCallback<OfferDetail>(eventBus) {
            @Override
            public void onSuccess(OfferDetail result) {
                //TODO zistit ci bude treba nejaky refresh aj ked mame asyynchDataProvider, asi hej
                //skusit najpr redreaw na gride
//                eventBus.setOfferDetailChange(result);
            }
        });
    }
}