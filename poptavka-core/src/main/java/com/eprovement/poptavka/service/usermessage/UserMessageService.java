/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eprovement.poptavka.service.usermessage;

import com.eprovement.poptavka.dao.message.MessageFilter;
import com.eprovement.poptavka.dao.usermessage.UserMessageDao;
import com.eprovement.poptavka.domain.message.Message;
import com.eprovement.poptavka.domain.message.UserMessage;
import com.eprovement.poptavka.domain.user.BusinessUser;
import com.eprovement.poptavka.domain.user.User;
import com.eprovement.poptavka.service.GenericService;
import com.googlecode.genericdao.search.Search;
import java.util.List;

/**
 *
 * @author ivan.vlcek
 */
public interface UserMessageService extends GenericService<UserMessage, UserMessageDao> {

    /**
     * Returns a userMessage of given message. UserMessage stores attributes like
     * isRead, isStared
     *
     * @param message
     * @param messageFilter
     * @return
     */
    List<UserMessage> getUserMessages(List<Message> messages,
            User user, MessageFilter messageFilter);

    /**
     * Returns a userMessage to a given message and user.
     * UserMessage stores attributes like isRead, isStared
     *
     * @param message
     * @param messageFilter
     * @return
     */
    UserMessage getUserMessage(Message message, User user);

    /**
     * Load all potential demands for given supplier.
     * Those are all messages which ware sent by the Poptavka system to the given supplier
     * when some client created new Demand suitable for given supplier.
     *
     *
     *
     * @param supplier supplier for which all potential demands are found. It is of type
     *                 {@link com.eprovement.poptavka.domain.user.BusinessUser}
     *                 because that is the most specific descendant of {@link User} we can use.
     *                 {@link com.eprovement.poptavka.domain.user.Supplier} cannot be used directly (in a simple way)
     *                 because it is NOT a descendant of User.
     *                 Various implementations can have different requirements for this parameter, but in general,
     *                 at least <code>id</code> attribute should be filled
     * @return all potential demands for given <code>supplier</code>
     *
     * @see com.eprovement.poptavka.service.message.MessageServiceImpl
     * @see com.eprovement.poptavka.service.demand.DemandService#sendDemandsToSuppliers()
     *
     */
    List<UserMessage> getPotentialDemands(BusinessUser supplier);

    /**
     * Load all potential demands for given supplier.
     * Those are all messages which ware sent by the Poptavka system to the given supplier
     * when some client created new Demand suitable for given supplier.
     *
     *
     *
     * @param supplier supplier for which all potential demands are found. It is of type
     *                 {@link com.eprovement.poptavka.domain.user.BusinessUser}
     *                 because that is the most specific descendant of {@link User} we can use.
     *                 {@link com.eprovement.poptavka.domain.user.Supplier} cannot be used directly (in a simple way)
     *                 because it is NOT a descendant of User.
     *                 Various implementations can have different requirements for this parameter, but in general,
     *                 at least <code>id</code> attribute should be filled
     * @param search specifies how the resulting list should be filtered/sorted/
     *               paged
     * @return all potential demands for given <code>supplier</code>
     *
     * @see com.eprovement.poptavka.service.message.MessageServiceImpl
     * @see com.eprovement.poptavka.service.demand.DemandService#sendDemandsToSuppliers()
     *
     */
    List<UserMessage> getPotentialDemands(BusinessUser supplier, Search search);

    /**
     * Gets the user's inbox
     * @param user the user whose inbox to get
     * @return list of <code>UserMesage</code> of the mesages in the user's
     * inbox
     */
    List<UserMessage> getInbox(User user);

    /**
     * Gets the user's inbox
     * @param user the user whose inbox to get
     * @param search specifies how the resulting list should be filtered/sorted/
     *               paged
     * @return list of <code>UserMesage</code> of the mesages in the user's
     *         inbox
     */
    List<UserMessage> getInbox(User user, Search search);

    /**
     * Gets the user's sent items folder
     * @param user the user whose sent items to get
     * @return list of <code>UserMesage</code> of the mesages in the user's
     * sent items' folder
     */
    List<UserMessage> getSentItems(User user);

    /**
     * Gets the user's sent items folder
     * @param user the user whose sent items to get
     * @param search specifies how the resulting list should be filtered/sorted/
     *               paged
     * @return list of <code>UserMesage</code> of the mesages in the user's
     * sent items' folder
     */
    List<UserMessage> getSentItems(User user, Search search);

}