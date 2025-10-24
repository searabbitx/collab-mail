package io.searabbitx.mail;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public record Mail(LocalDateTime time, String from, String to, String cc, String bcc, String subject,
                   String plainContent,
                   String htmlContent, String smtpConversation, List<Attachment> attachments) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public record Attachment(String name, String contentType, byte[] content) implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }
}
