package io.searabbitx.mail;

import java.io.Serial;

public record AttachmentV1(String name, String contentType, byte[] content) implements Attachment {
    @Serial
    private static final long serialVersionUID = 1L;
}
