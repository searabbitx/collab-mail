package io.searabbitx.mail;

import java.io.Serializable;

public interface Address extends Serializable {
    String username();

    String domain();

    String note();

    static Address fromFullAddr(String fullAddr, String note) {
        var ind = fullAddr.indexOf('@');
        var uname = fullAddr.substring(0, ind);
        var dom = fullAddr.substring(ind + 1);
        return new AddressV1(uname, dom, note);
    }
}
