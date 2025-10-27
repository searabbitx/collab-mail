package io.searabbitx.mail;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;

public record MailV1(LocalDateTime time, String from, String to, String cc, String bcc, String subject,
                     String plainContent,
                     String htmlContent, String smtpConversation, List<? extends Attachment> attachments,
                     boolean isTruncated) implements Mail {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public boolean hasHtml() {
        return htmlContent != null;
    }

    @Override
    public boolean hasAttachments() {
        return !attachments.isEmpty();
    }

}
