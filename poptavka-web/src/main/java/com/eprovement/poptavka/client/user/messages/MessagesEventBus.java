/*
 * Copyright (C), eProvement s.r.o. All rights reserved.
 */
package com.eprovement.poptavka.client.user.messages;

import com.mvp4g.client.annotation.Debug;
import com.mvp4g.client.annotation.Debug.LogLevel;
import com.mvp4g.client.annotation.Event;
import com.mvp4g.client.annotation.Events;
import com.mvp4g.client.annotation.Forward;
import com.mvp4g.client.annotation.Start;

import com.eprovement.poptavka.shared.search.SearchModuleDataHolder;
import com.eprovement.poptavka.client.user.messages.widgets.MessageListPresenter;
import com.eprovement.poptavka.shared.domain.message.MessageDetail;
import com.eprovement.poptavka.client.common.BaseChildEventBus;
import com.eprovement.poptavka.client.root.gateways.ActionBoxGateway;
import com.eprovement.poptavka.client.user.widget.grid.UniversalAsyncGrid;
import com.eprovement.poptavka.client.user.widget.grid.UniversalAsyncGrid.IEventBusData;
import com.eprovement.poptavka.shared.domain.message.UnreadMessagesDetail;
import com.eprovement.poptavka.shared.search.SearchDefinition;
import com.google.gwt.user.client.ui.IsWidget;
import com.mvp4g.client.event.EventBusWithLookup;

/**
 * Message module - provides internal mailbox to user.
 * @author Martin Slavkovsky
 */
@Debug(logLevel = LogLevel.DETAILED)
@Events(startPresenter = MessagesPresenter.class, module = MessagesModule.class)
public interface MessagesEventBus extends EventBusWithLookup, IEventBusData, BaseChildEventBus,
    ActionBoxGateway {

    /**
     * Start event is called only when module is instantiated first time.
     * We can use it for history initialization.
     */
    @Start
    @Event(handlers = MessagesPresenter.class)
    void start();

    /**
     * Forward event is called only if it is configured here. It there is nothing to carry out
     * in this method we should remove forward event to save the number of method invocations.
     * We can use forward event to switch css style for selected menu button.
     */
    @Forward
    @Event(handlers = MessagesPresenter.class, navigationEvent = true)
    void forward();

    /**************************************************************************/
    /* Navigation events.                                                     */
    /**************************************************************************/
    /**
     * The only entry point to this module due to code-spliting feature.
     *
     * @param filter - defines data holder to be displayed in advanced search bar
     * @param loadWidget - prosim doplnit ???
     */
    @Event(handlers = MessagesPresenter.class, navigationEvent = true)
    void goToMessagesModule(SearchModuleDataHolder searchDataHolder, int loadWidget);

    /**************************************************************************/
    /* Common event                                                           */
    /**************************************************************************/
    @Event(handlers = MessageListPresenter.class)
    void resize(int actualWidth);

    /**************************************************************************/
    /* Parent events                                                          */
    /**************************************************************************/
    @Event(forwardToParent = true)
    void setUpdatedUnreadMessagesCount(UnreadMessagesDetail numberOfMessages);

    /**************************************************************************/
    /* History events                                                         */
    /**************************************************************************/
    @Event(historyConverter = MessagesHistoryConverter.class, name = "token")
    String createTokenForHistory();

    /**************************************************************************/
    /* Overriden methods of IEventBusData interface. */
    /* Should be called only from UniversalAsyncGrid. */
    /**************************************************************************/
    @Override
    @Event(handlers = MessagesHandler.class)
    void getDataCount(UniversalAsyncGrid grid, SearchDefinition searchDefinition);

    @Override
    @Event(handlers = MessagesHandler.class)
    void getData(UniversalAsyncGrid grid, SearchDefinition searchDefinition, int requestId);

    /**************************************************************************/
    /* Business Initialization events                                         */
    /**************************************************************************/
    @Event(handlers = MessageListPresenter.class)
    void initInbox(SearchModuleDataHolder searchDataHolder);

    /**************************************************************************/
    /* Business events handled by presenters                                  */
    /**************************************************************************/
    @Event(handlers = MessagesPresenter.class)
    void displayView(IsWidget content);

    @Event(handlers = MessagesPresenter.class)
    void messagesMenuStyleChange(int loadedWidget);

    /**************************************************************************/
    /* Business events handled by MessagesModuleMessageHandler                */
    /**************************************************************************/
    @Event(handlers = MessagesHandler.class)
    void updateUnreadMessagesCount();

    /**
     * Send/Response method pair
     * Sends message and receive the answer in a form of the same message to be displayed on UI.
     * @param messageToSend
     * @param type type of handling view
     */
    @Event(handlers = MessagesHandler.class)
    void sendMessage(MessageDetail messageToSend, String action);
}
