package io.searabbitx.mail;

import java.io.Serializable;

public interface Attachment extends Serializable {
    String name();

    String contentType();

    byte[] content();
}
