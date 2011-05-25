package cz.poptavka.sample.server.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import net.sf.gilead.gwt.PersistentRemoteService;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cz.poptavka.sample.domain.address.Locality;
import cz.poptavka.sample.domain.demand.Category;
import cz.poptavka.sample.domain.demand.Demand;
import cz.poptavka.sample.domain.offer.Offer;
import cz.poptavka.sample.domain.user.Client;
import cz.poptavka.sample.shared.domain.DemandDetail;
import cz.poptavka.sample.shared.domain.OfferDetail;
import cz.poptavka.sample.shared.domain.UserDetail;


/**
 * Expose Spring services to GWT app.
 * From http://pgt.de/2009/07/17/non-invasive-gwt-and-spring-integration-reloaded/
 */
public abstract class AutoinjectingRemoteService extends PersistentRemoteService  {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(
                this.getServletContext());
        AutowireCapableBeanFactory beanFactory = ctx.getAutowireCapableBeanFactory();
        beanFactory.autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, true);

    }


    protected ArrayList<DemandDetail> toDemandDetailList(List<Demand> list) {
        ArrayList<DemandDetail> details = new ArrayList<DemandDetail>();
        for (Demand demand : list) {
            DemandDetail detail = new DemandDetail();
            detail.setId(demand.getId());
            detail.setTitle(demand.getTitle());
            detail.setDescription(demand.getDescription());
            detail.setPrice(demand.getPrice());
            detail.setEndDate(demand.getEndDate());
            detail.setExpireDate(demand.getValidTo());
            detail.setMaxOffers(demand.getMaxSuppliers());
            detail.setMinRating(demand.getMinRating());
            ArrayList<String> catList = new ArrayList<String>();
            for (Category cat : demand.getCategories()) {
                catList.add(cat.getName());
            }
            detail.setCategories(catList);
            ArrayList<String> locList = new ArrayList<String>();
            for (Locality loc : demand.getLocalities()) {
                locList.add(loc.getName());
            }
            detail.setLocalities(locList);
            details.add(detail);
        }
        return details;
    }


    protected ArrayList<OfferDetail> toOfferDetailList(List<Offer> offerList) {
        ArrayList<OfferDetail> details = new ArrayList<OfferDetail>();
        for (Offer offer : offerList) {
            OfferDetail detail = new OfferDetail();
            detail.setDemandId(offer.getDemand().getId());
            detail.setFinishDate(offer.getFinishDate());
            detail.setPrice(offer.getPrice());
            detail.setSupplierId(offer.getSupplier().getId());
            if (offer.getSupplier().getBusinessUser().getBusinessUserData() != null) {
                detail.setSupplierName(offer.getSupplier().getBusinessUser().getBusinessUserData().getCompanyName());
            } else {
                detail.setSupplierName("unknown");
            }

            details.add(detail);
        }
        return details;
    }

    protected UserDetail toClientDetail(Client client) {
        UserDetail clientDetail =
            new UserDetail(client.getBusinessUser().getEmail(), client.getBusinessUser().getPassword());
        clientDetail.setId(client.getId());
        /** personal & company **/
        clientDetail.setFirstName(client.getBusinessUser().getBusinessUserData().getPersonFirstName());
        clientDetail.setLastName(client.getBusinessUser().getBusinessUserData().getPersonLastName());
        clientDetail.setPhone(client.getBusinessUser().getBusinessUserData().getPhone());
        clientDetail.setCompanyName(client.getBusinessUser().getBusinessUserData().getCompanyName());
        clientDetail.setTaxId(client.getBusinessUser().getBusinessUserData().getTaxId());
        clientDetail.setIdentifiacationNumber(client.getBusinessUser().getBusinessUserData().getIdentificationNumber());
        /** TODO address - not in the mood now **/
        /** TODO website **/

        /** demands **/
        List<Demand> demands = client.getDemands();
        ArrayList<String> demandIds = new ArrayList<String>();
        for (Demand d : demands) {
            demandIds.add(d.getId().toString());
        }
        clientDetail.setDemandsId(demandIds);
        return clientDetail;
    }
}
