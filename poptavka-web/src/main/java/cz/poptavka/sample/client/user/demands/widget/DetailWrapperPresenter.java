package cz.poptavka.sample.client.user.demands.widget;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.mvp4g.client.annotation.Presenter;
import com.mvp4g.client.presenter.LazyPresenter;
import com.mvp4g.client.view.LazyView;

import cz.poptavka.sample.client.user.UserEventBus;
import cz.poptavka.sample.client.user.messages.OfferWindowPresenter;
import cz.poptavka.sample.client.user.messages.ReplyWindowPresenter;
import cz.poptavka.sample.client.user.messages.UserConversationPanel;
import cz.poptavka.sample.shared.domain.OfferDetail;
import cz.poptavka.sample.shared.domain.demand.BaseDemandDetail;
import cz.poptavka.sample.shared.domain.demand.DemandDetail;
import cz.poptavka.sample.shared.domain.message.MessageDetail;
import cz.poptavka.sample.shared.domain.type.ViewType;

@Presenter(view = DetailWrapperView.class, multiple = true)
public class DetailWrapperPresenter extends
        LazyPresenter<DetailWrapperPresenter.IDetailWrapper, UserEventBus> {

    public interface IDetailWrapper extends LazyView {
        Widget getWidgetView();

        void setDetail(Widget demandDetailWidget);

        SimplePanel getDetailHolder();

        UserConversationPanel getConversationPanel();

        SimplePanel getReplyHolder();
    }

    private ViewType type;

    private ReplyWindowPresenter replyPresenter = null;

    private LoadingDiv detailLoader;
    private LoadingDiv conversationLoader;

    /**
     * Initialize widget and sets his type.
     *
     * @param detailSection holder for widget
     * @param type type of view, where is this widget loaded
     */
    public void initDetailWrapper(SimplePanel detailSection, ViewType viewType) {
        GWT.log("DEMAND DETAIL Presenter LOADED");
        detailSection.setWidget(view.getWidgetView());
        this.type = viewType;
    }

    /**
     * Response when user click demand to see the details. DemandDetails widget,
     * past conversation regarding this demand and reply widget is created.
     *
     * @param detail FullDemandDetail to be displayed
     * @param typeOfDetail type of what detail section should handle this event
     */
    public void onSetDemandDetail(DemandDetail detail, ViewType typeOfDetail) {
        if (!typeOfDetail.equals(type)) {
            return;
        }
        view.setDetail(new DemandDetailView(detail));
        // GUI visual event
        toggleDetailLoading();
    }

    public void onSetBaseDemandDetail(BaseDemandDetail detail, ViewType typeOfDetail) {
        if (!typeOfDetail.equals(type)) {
            return;
        }
        view.setDetail(new DemandDetailView(detail));


        // GUI visual event
        toggleDetailLoading();
    }

    // TODO test
    public void onSetPotentialDemandConversation(ArrayList<MessageDetail> messageList, ViewType wrapperhandlerType) {
        // TODO this should be enough, testing needed
//        if (!(!wrapperhandlerType.equals(type) || messageList == null)) {
//            return;
//        }
        // old and long, test commented one
        if (!wrapperhandlerType.equals(type)) {
            return;
        }
        toggleConversationLoading();
        if (messageList == null) {
            return;
        }
        view.getConversationPanel().setMessageList(messageList, true);
    }

    public void onAddReplyToPotentailDemandConversation(MessageDetail result, ViewType wrapperhandlerType) {
        if (!wrapperhandlerType.equals(type)) {
            return;
        }
        replyPresenter.enableResponse();
    }

    /**
     * Visual sign, that demand detail is loading it's children. clear all it's children;
     *
     * @param demandId not needed for this method
     * @param typeOfDetail
     */
    // TODO REWORK completely
    public void onGetDemandDetail(Long demandId, ViewType typeOfDetail) {
        if (!typeOfDetail.equals(type)) {
            return;
        }
        toggleDetailLoading();

        // clear offersPresenters for OFFER
        // messages are cleared in the widget itself
        ArrayList<OfferWindowPresenter> listToClear = view.getConversationPanel().clearContent();
        for (OfferWindowPresenter p : listToClear) {
            eventBus.removeHandler(p);
        }

        // reply window stay always loaded, do not do anything
    }

    /**
     * Display offer message from presenter. Client can react to it.
     *
     * @param offerDetail offer detail
     */
    public void onSetOfferMessage(OfferDetail offerDetail) {

        OfferWindowPresenter presenter = eventBus.addHandler(OfferWindowPresenter.class);
        presenter.setOfferDetail(offerDetail);
        GWT.log("OFFER ID: " + offerDetail.getOfferId());
        view.getConversationPanel().addOfferMessagePresenter(presenter);
    }


    /**
     * Creates reply window for creating Offer/Question message.
     *
     * @param demandId
     */
    public void setReplyWidget(Long demandId) {
        if (replyPresenter != null) {
            eventBus.removeHandler(replyPresenter);
        }
        replyPresenter = eventBus.addHandler(ReplyWindowPresenter.class);
        replyPresenter.initReplyWindow(view.getReplyHolder());
        replyPresenter.addSubmitHandler(bindReplyWindowAction(), demandId);
    }

    private ClickHandler bindReplyWindowAction() {
        return new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // sending message only when valid
                if (replyPresenter.isMessageValid()) {
                    //distinguish what kind of message should be sent
                    if (replyPresenter.hasResponseQuestion()) {
                        MessageDetail messageToSend = replyPresenter.getCreatedMessage();
                        messageToSend = view.getConversationPanel().updateSendingMessage(messageToSend);
                        eventBus.bubbleMessageSending(messageToSend);
                    } else {
                        // TODO offer branch
                        OfferDetail offerToSend = replyPresenter.getOfferMessage();
                        offerToSend.setMessageDetail(view.getConversationPanel()
                                .updateSendingMessage(offerToSend.getMessageDetail()));
                        eventBus.bubbleOfferSending(offerToSend);
                    }
                }
            }
        };
    }

    /** loading events **/
    //just to enable loading
    public void onRequestPotentialDemandConversation(long messageId, long userMessageId) {
        toggleConversationLoading();
    }


    public void toggleDetailLoading() {
        if (detailLoader == null) {
            GWT.log("  - loading created");
            detailLoader = new LoadingDiv(view.getDetailHolder());
        } else {
            GWT.log("  - loading removed");
            detailLoader.getElement().removeFromParent();
            detailLoader = null;
        }
    }

    public void toggleConversationLoading() {
        if (conversationLoader == null) {
            GWT.log("  - loading created");
            conversationLoader = new LoadingDiv(view.getConversationPanel());
        } else {
            GWT.log("  - loading removed");
            conversationLoader.getElement().removeFromParent();
            conversationLoader = null;
        }
    }

}
