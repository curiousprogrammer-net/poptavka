package cz.poptavka.sample.client.service.demand;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import cz.poptavka.sample.domain.common.OrderType;
import cz.poptavka.sample.shared.domain.OfferDetail;
import cz.poptavka.sample.shared.domain.demand.OfferDemandDetail;
import cz.poptavka.sample.shared.domain.offer.FullOfferDetail;
import java.util.List;
import java.util.Map;

@RemoteServiceRelativePath("service/offers")
public interface OfferRPCService extends RemoteService {

    ArrayList<OfferDemandDetail> getClientDemands(long clientId);

    ArrayList<OfferDetail> getDemandOffers(long demandId, long threadRootId);

    OfferDetail changeOfferState(OfferDetail offerDetail);

    FullOfferDetail updateOffer(FullOfferDetail newOffer);

    Long getAllOffersCount();

    List<FullOfferDetail> getOffers(int fromResult, int toResult);

    List<FullOfferDetail> getSortedOffers(int start, int count, Map<String, OrderType> orderColumns);
}
