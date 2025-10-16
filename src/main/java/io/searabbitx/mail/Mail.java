package io.searabbitx.mail;

import java.io.Serial;
import java.io.Serializable;

public record Mail(String from, String to, String subject, String plainContent) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
