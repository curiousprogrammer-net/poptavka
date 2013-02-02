/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eprovement.poptavka.shared.domain.message;

import com.eprovement.poptavka.domain.enums.OfferStateType;
import java.io.Serializable;

import com.google.gwt.view.client.ProvidesKey;

/**
 *
 * @author ivan.vlcek
 */
public class ClientDemandMessageDetail extends DemandMessageDetail
        implements Serializable, TableDisplay {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String clientName;
    private int clientRating;



    @Override
    public String toString() {
        return "ClientDemandMessageDetail{\n" + "messageId=" + getMessageId()
                + ",\n threadRoodId=" + getThreadRootId()
                + ",\n demandId=" + getDemandId()
                + ",\n senderId=" + getSenderId()
                + ",\n demandTitle=" + getDemandTitle()
                + ",\n demandStatus=" + getDemandStatus()
                + ",\n messagesCount=" + getMessageCount()
                + ",\n unreadSubMessages=" + getUnreadSubMessages()
                + ",\n endDate=" + getEndDate()
                + ",\n validToDate=" + getValidToDate()
                + ",\n price=" + getPrice() + "}\n\n";
    }
    public static final ProvidesKey<ClientDemandMessageDetail> KEY_PROVIDER =
            new ProvidesKey<ClientDemandMessageDetail>() {
                @Override
                public Object getKey(ClientDemandMessageDetail item) {
                    return item == null ? null : item.getDemandId();
                }
            };

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public int getClientRating() {
        return clientRating;
    }

    public void setClientRating(int clientRating) {
        this.clientRating = clientRating;
    }

    @Override
    public OfferStateType getOfferState() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setIsStarred(boolean value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getUnreadSubmessagesCount() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}