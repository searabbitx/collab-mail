package io.searabbitx.mail;

import java.io.Serializable;

public interface Address extends Serializable {
    String username();
    String domain();
    String note();
}
