/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.poptavka.sample.server.service.message;

import cz.poptavka.sample.client.service.demand.MessageRPCService;
import cz.poptavka.sample.dao.message.MessageFilter;
import cz.poptavka.sample.domain.message.Message;
import cz.poptavka.sample.domain.message.MessageContext;
import cz.poptavka.sample.domain.message.MessageState;
import cz.poptavka.sample.domain.message.MessageUserRole;
import cz.poptavka.sample.domain.message.MessageUserRoleType;
import cz.poptavka.sample.domain.user.User;
import cz.poptavka.sample.server.service.AutoinjectingRemoteService;
import cz.poptavka.sample.service.GeneralService;
import cz.poptavka.sample.service.message.MessageService;
import cz.poptavka.sample.service.usermessage.UserMessageService;
import cz.poptavka.sample.shared.domain.MessageDetail;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author ivan.vlcek
 */
public class MessageRPCServiceImpl extends AutoinjectingRemoteService implements MessageRPCService {

    /**
     * Generated serialVersionUID.
     */
    private static final long serialVersionUID = -2239531608577928736L;
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageRPCServiceImpl.class);
    private GeneralService generalService;
    private MessageService messageService;
    private UserMessageService userMessageService;

    @Autowired
    public void setGeneralService(GeneralService generalService) {
        this.generalService = generalService;
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Autowired
    public void setUserMessageService(UserMessageService userMessageService) {
        this.userMessageService = userMessageService;
    }

    /**
     * Message sent by supplier about a query to potential demand.
     * @param messageDetail
     * @return message
     */
    public Message sendQueryToPotentialDemand(MessageDetail messageDetail) {
        Message m = new Message();
        m.setBody(messageDetail.getBody());
        m.setCreated(new Date());
        m.setLastModified(new Date());
        m.setMessageState(MessageState.SENT);
        // TODO ivlcek - how to set this next sibling?
//        m.setNextSibling(null);
        Message parentMessage = this.messageService.getById(messageDetail.getParentId());
        m.setParent(parentMessage);
        User sender = this.generalService.find(User.class, messageDetail.getSenderId());
        m.setSender(sender);
        m.setSent(new Date());
        m.setSubject(messageDetail.getSubject());
        m.setThreadRoot(this.messageService.getById(messageDetail.getThreadRootId()));
        // set message roles
        List<MessageUserRole> messageUserRoles = new ArrayList<MessageUserRole>();
        // messageToUserRole
        MessageUserRole messageToUserRole = new MessageUserRole();
        messageToUserRole.setMessage(m);
        messageToUserRole.setUser(this.generalService.find(User.class, messageDetail.getReceiverId()));
        messageToUserRole.setType(MessageUserRoleType.TO);
        messageToUserRole.setMessageContext(MessageContext.QUERY_TO_POTENTIAL_SUPPLIERS_DEMAND);
        messageUserRoles.add(messageToUserRole);
        // messageFromUserRole
        MessageUserRole messageFromUserRole = new MessageUserRole();
        messageFromUserRole.setMessage(m);
        messageFromUserRole.setType(MessageUserRoleType.SENDER);
        messageFromUserRole.setMessageContext(MessageContext.QUERY_TO_POTENTIAL_SUPPLIERS_DEMAND);
        messageFromUserRole.setUser(sender);
        messageUserRoles.add(messageFromUserRole);
        m.setRoles(messageUserRoles);
        this.messageService.create(m);
        // TODO set children for parent message
        parentMessage.getChildren().add(m);
        parentMessage.setMessageState(MessageState.REPLY_RECEIVED);
        this.messageService.update(parentMessage);
        return m;
    }

    @Override
    public ArrayList<MessageDetail> loadSuppliersPotentialDemandConversation(long threadId, long userId) {
        Message threadRoot = messageService.getById(threadId);
        User user = this.generalService.find(User.class, userId);
        ArrayList<Message> messages = (ArrayList<Message>) this.messageService.getPotentialDemandConversation(
                threadRoot, user, MessageFilter.EMPTY_FILTER);
        ArrayList<MessageDetail> messageDetails = new ArrayList<MessageDetail>();
        for (Message message : messages) {
            MessageDetail md = new MessageDetail();
            md.setBody(message.getBody());
            md.setCreated(message.getCreated());
            md.setFirstBornId(message.getFirstBorn().getId());
            md.setMessageState(message.getMessageState().name());
            md.setNexSiblingId(message.getNextSibling().getId().longValue());
            md.setParentId(message.getParent().getId());
            md.setSent(message.getSent());
            md.setSubject(message.getSubject());
            md.setThreadRootId(message.getThreadRoot().getId());
            messageDetails.add(md);
        }
        return messageDetails;
    }
}