package com.eprovement.poptavka.dao.message;

import com.eprovement.poptavka.dao.GenericDao;
import com.eprovement.poptavka.domain.demand.Demand;
import com.eprovement.poptavka.domain.message.Message;
import com.eprovement.poptavka.domain.message.UserMessage;
import com.eprovement.poptavka.domain.user.User;

import java.util.List;
import java.util.Map;

/**
 * Basic interface for dao which is provides methods for messages.
 *
 * @author Juraj Martinka
 *         Date: 4.5.11
 */
public interface MessageDao extends GenericDao<Message> {

    /**
     * Load all message threads' roots for specified <code>user</code>.
     * No default ordering is applied!
     *
     * @param user
     * @param messageFilter additional filtering, e.g. only incoming messages
     * @return all threads of messages for given user represented by thread roots
     * @see Message
     */
    List<Message> getMessageThreads(User user, MessageFilter messageFilter);

    /**
     * Loads (really) all messages for given user without any special structuring of result.
     * <code>resultCriteria</code> parameter can be used for simple ordering or limiting size of results.
     * <p>
     * No default ordering is applied!
     *
     * @param user
     * @param messageFilter additional filter, e.g. only outgoing messages
     * @return
     */
    List<Message> getAllMessages(User user, MessageFilter messageFilter);

    /**
     * Returns a userMessage of given message. UserMessage stores attributes like
     * read, starred
     *
     * @param messages
     * @param messageFilter
     * @return
     */
    List<UserMessage> getUserMessages(List<Message> messages, MessageFilter messageFilter);

    /**
     * Loads conversation between supplier and  client related to potential demand supplier's queries.
     *
     * @param message
     * @param supplierUser
     * @return
     */
    List<Message> getPotentialDemandConversation(Message message, User supplierUser);

    /**
     * Loads conversation between supplier and  client related to potential demand supplier's queries.
     *
     * @param message
     * @param supplierUser
     * @return List of user messages
     */
    List<UserMessage> getConversationUserMessages(Message message, User supplierUser);

    /**
     * Returns message thread root assigned to given demand.
     * @param demand
     * @return
     */
    Message getThreadRootMessage(Demand demand);

    /**
     * Returns offer messages from this threadRoot
     * @param threadRoot
     * @return
     */
    List<Message> getAllOfferMessagesForDemand(Message threadRoot);

    /**
     * Loads conversation between supplier and client related to potential offer.
     *
     * @param message
     * @param supplierUser
     * @return
     */
    List<Message> getPotentialOfferConversation(Message threadRoot, User supplierUser);

    /**
     * Gets all the demand messages of the given user along with the number of
     * ALL the messages that span from the demand message (including the demand
     * message itself)
     *
     * @param user
     * @return a map keyed by the user's demand messages containing the number
     * of ALL the messages spanning from the demand message (including the
     * demand message itself)
     */
    Map<Message, Integer> getListOfClientDemandMessagesAll(User user);

    /**
     * Gets all the demand messages of the given user along with the number of
     * UNREAD messages that span from the demand message (including the demand
     * message itself)
     *
     * @param user
     * @return a map keyed by the user's demand messages containing the number
     * of UNREAD messages spanning from the demand message (including the
     * demand message itself)
     */
    Map<Long, Integer> getListOfClientDemandMessagesUnread(User user);

    /**
     * Gets all the demand messages with offer of the given user along with the number of
     * UNREAD messages that span from the demand message (including the demand
     * message itself)
     *
     * @param user
     * @return a map keyed by the user's demand messages containing the number
     * of UNREAD messages spanning from the demand message (including the
     * demand message itself)
     */
    Map<Long, Integer> getListOfClientDemandMessagesWithOfferUnreadSub(User user);

    /**
     * Gets all the descendants (not just the children) of every item
     * in the given list of messages
     *
     * @param messages
     * @return
     */
    List<Message> getAllDescendants(List<Message> messages);

    /**
     * Gets the child of the message that has been sent the last
     * @param parent
     * @return
     */
    Message getLastChild(Message parent);

    /**
     * Gets all latest userMessages from all suppliers who sent some quesstion response to demand.
     *
     * @param user
     * @param threadRoot
     * @return
     */
    Map<Long, Integer> getLatestSupplierUserMessagesWithoutOfferForDemnd(User user, Message threadRoot);

    /**
     * Gets all latest userMessages from all suppliers who sent some offer response to demand.
     *
     * @param user
     * @param threadRoot
     * @return
     */
    Map<Long, Integer> getLatestSupplierUserMessagesWithOfferForDemnd(User user, Message threadRoot);

    /**
     * Gets all latest userMessages from all suppliers who are assigned to client demands.
     *
     * @param user
     * @param threadRoot
     * @return
     */
    Map<Long, Integer> getLatestSupplierUserMessagesForAssignedDemand(User user, Message threadRoot);
}
