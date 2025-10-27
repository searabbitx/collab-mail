package io.searabbitx.mail;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public interface Mail extends Serializable {
    boolean hasHtml();

    boolean hasAttachments();

    LocalDateTime time();

    String from();

    String to();

    String cc();

    String bcc();

    String subject();

    String plainContent();

    String htmlContent();

    String smtpConversation();

    List<? extends Attachment> attachments();

    boolean isTruncated();
}
