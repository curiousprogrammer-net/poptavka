/*
 * A message can be in one of the following main states. In general, the state
 * is independent of where the message is filed. The user may  or instance
 * file a sent message in the Inbox, but its state will show that it is a sent
 * message.
 */
package cz.poptavka.sample.domain.mail;

/**
 *
 * @author Vojtech Hubr
 *         Date 12.4.11

 */
public enum MessageState {

    /* 1.composed: A message generated by the user but not yet queued for sending. */
    COMPOSED("COMPOSED"),
    /* 2. queued: A message to be sent but not yet physically transmitted. */
    QUEUED("QUEUED"),
    /* 3. sent: A message physically sent, but no reply has been received yet. */
    SENT("SENT"),
    /* 4, seen: A received message that the user has seen. The system may for
     instance record the state seen when the message has been open for more
     than 3 seconds. */
    SEEN("SEEN"),
    /* 5. replied, forwarded, replied+forwarded: The message is received and
     the user has sent a reply and/or forwarded it to someone. */
    REPLIED("REPLIED"),
    FORWARDED("FORWARED"),
    REPLIED_FORWARDED("REPLIED_FORWARDED"),
    /* 6. replyReceived: The message has been sent and a reply received. */
    /* Akonahle pride Message A do stavu replied a nasledne replyReceived tak
    to znamena ze povodny adresat vytvoril uplne novu spravu Message B, ktorej
    sa opat nastavia vyssie popisane stavy. Povodna sprava A sa cez entitu
    MessageTree(firstborn) naviaze na tuto novu spravu B aby sme vedeli, ze
    nova sprava B je odpovedou na A. */
    REPLY_RECEIVED("REPLY_RECEIVED"),
    /* 7. warning: A message generated by the user’s own mail system because one
     of the timers has expired. */
    WARNING("WARNING"),
    /* 8. deleted: A message was deleted by user. */
    DELETED("DELETED");

    private final String value;

    MessageState(String value) {
        this.value = value;
    }
}
