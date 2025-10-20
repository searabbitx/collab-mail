package io.searabbitx.mail;

import java.io.Serial;
import java.io.Serializable;

public record Mail(String from, String to, String cc, String bcc, String subject, String plainContent,
                   String htmlContent, String smtpConversation) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
