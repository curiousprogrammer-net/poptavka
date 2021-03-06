package com.eprovement.poptavka.application.logging;

import static com.google.common.base.Preconditions.checkNotNull;

import com.eprovement.poptavka.service.mail.MailService;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;

/**
 * Aspect which is responsible for logging all uncaught exceptions that occur in any method in application.
 *
 * @author Juraj Martinka
 *         Date: 10.4.11
 */
@Aspect
public class ExceptionLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionLogger.class);

    private static final int DEFAULT_STACKTRASE_SIZE = 1000;
    private static final String DEFAUL_MAIL_SENDER_ADDRESS = "noreply@want-something.com";

    /** if true the emails will be send on error. */
    private boolean emailEnabled;
    /** Email address of sender: This will be set to FROM header of sent emails. */
    private String mailSenderAddress = DEFAUL_MAIL_SENDER_ADDRESS;
    private MailService mailService;
    private List<String> recipients;

    public boolean isEmailEnabled() {
        return emailEnabled;
    }

    public void setEmailEnabled(boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }

    public void setMailSenderAddress(String mailSenderAddress) {
        this.mailSenderAddress = mailSenderAddress;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public void addRecipient(String recipient) {
        if (this.recipients == null) {
            this.recipients = new ArrayList<String>();
        }
        this.recipients.add(recipient);
    }

    public void removeRecipient(String recipient) {
        if (this.recipients != null) {
            this.recipients.remove(recipient);
        }
    }

    public List<String> getRecipients() {
        return Collections.unmodifiableList(recipients);
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }


    @Pointcut("execution(* com.eprovement.poptavka..*.*(..))")
    private void exceptionAware() { }

    @AfterThrowing(pointcut = "exceptionAware()", throwing = "exception")
    public void logExceptionMethod(Exception exception) {
        LOGGER.error("An exception has been thrown.", exception);
        sendNotificationMail(exception);
    }



    //--------------------------------------------------- PRIVATE STUFF ------------------------------------------------
    private void sendNotificationMail(Exception exception) {
        if (isEmailEnabled() && CollectionUtils.isNotEmpty(recipients)) {
            checkNotNull(exception);

            final SimpleMailMessage exceptionNotificationMessage = createNotificationMessage(exception);
            try {
                // send asynchronously to avoid blocking of normal execution
                this.mailService.sendAsync(exceptionNotificationMessage);
            } catch (MailException me) {
                LOGGER.warn("An error occured while sending exception notification mail: ", me);
            }
        }
    }

    private SimpleMailMessage createNotificationMessage(Exception exception) {
        final SimpleMailMessage exceptionNotificationMessage = new SimpleMailMessage();
        exceptionNotificationMessage.setFrom(mailSenderAddress);
        exceptionNotificationMessage.setTo(recipients.toArray(new String[recipients.size()]));

        final StringWriter stackTraceWriter = new StringWriter(DEFAULT_STACKTRASE_SIZE);
        exception.printStackTrace(new PrintWriter(stackTraceWriter));
        exceptionNotificationMessage.setSubject(
                "Poptavka exception notification: "
                + exception.getMessage());
        exceptionNotificationMessage.setText(
                "Following exception occurs while executing Poptavka application [location=" + getApplicationLocation()
                        + "]: " + exception.getMessage()
                + "\nStacktrace:"
                + "\n" + stackTraceWriter.toString());
        return exceptionNotificationMessage;
    }

    private InetAddress getApplicationLocation() {
        InetAddress applicationLocation = null;
        try {
            applicationLocation = InetAddress.getLocalHost();
        } catch (UnknownHostException uhe) {
            LOGGER.warn("Cannot get application location (host)", uhe);
        }
        return applicationLocation;
    }

}
