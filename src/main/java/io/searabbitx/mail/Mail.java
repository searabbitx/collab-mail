package io.searabbitx.mail;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public record Mail(LocalDateTime time, String from, String to, String cc, String bcc, String subject,
                   String plainContent,
                   String htmlContent, String smtpConversation) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
