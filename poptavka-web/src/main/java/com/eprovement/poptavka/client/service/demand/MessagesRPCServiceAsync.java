/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eprovement.poptavka.client.service.demand;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.eprovement.poptavka.shared.search.SearchModuleDataHolder;
import com.eprovement.poptavka.shared.domain.message.MessageDetail;
import com.eprovement.poptavka.shared.domain.message.UnreadMessagesDetail;

import com.eprovement.poptavka.shared.domain.message.UserMessageDetail;
import com.eprovement.poptavka.shared.search.SearchDefinition;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Drobcek
 */
public interface MessagesRPCServiceAsync {

    void sendInternalMessage(MessageDetail messageDetailImpl, AsyncCallback<MessageDetail> callback);

    /** INBOX. **/
    void getInboxMessagesCount(Long recipientId, SearchDefinition searchDefinition,
            AsyncCallback<Integer> callback);

    void getInboxMessages(Long recipientId, SearchDefinition searchDefinition,
            AsyncCallback<List<MessageDetail>> callback);

    void getSentMessages(Long senderId, SearchModuleDataHolder searchDataHolder,
            AsyncCallback<List<UserMessageDetail>> callback);

    void getDeletedMessages(Long userId, SearchModuleDataHolder searchDataHolder,
            AsyncCallback<List<UserMessageDetail>> callback);

    void getConversationMessages(long threadRootId, long subRootId,
            AsyncCallback<ArrayList<MessageDetail>> callback);

    void setMessageReadStatus(List<Long> userMessageIds, boolean isRead, AsyncCallback<Void> callback);

    void setMessageStarStatus(List<Long> list, boolean newStatus, AsyncCallback<Void> callback);

    void deleteMessages(List<Long> messagesIds, AsyncCallback<List<UserMessageDetail>> callback);

    void updateUnreadMessagesCount(AsyncCallback<UnreadMessagesDetail> callback);
}
