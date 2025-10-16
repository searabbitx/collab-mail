package io.searabbitx;

import burp.api.montoya.collaborator.Collaborator;
import burp.api.montoya.collaborator.CollaboratorClient;
import burp.api.montoya.collaborator.SecretKey;
import burp.api.montoya.persistence.PersistedList;
import burp.api.montoya.persistence.PersistedObject;

import java.util.stream.Stream;

class Storage {
    private static final String ADDRESSES_KEY = "addresses";
    private static final String COLLAB_CLIENT = "collab-client";

    private PersistedObject data;
    private Collaborator collaborator;

    Storage(PersistedObject data, Collaborator collaborator) {
        this.data = data;
        this.collaborator = collaborator;
    }

    void storeAddress(String address) {
        persistedAddressList().add(address);
    }

    void removeAddress(String add) {
        persistedAddressList().removeIf(add::equals);
    }

    void storeClient(CollaboratorClient client) {
        data.setString(COLLAB_CLIENT, client.getSecretKey().toString());
    }

    CollaboratorClient fetchClient() {
        var key = data.getString(COLLAB_CLIENT);
        if (null == key) {
            var client = collaborator.createClient();
            storeClient(client);
            return client;
        }
        return collaborator.restoreClient(SecretKey.secretKey(key));
    }

    Stream<String> fetchAddresses() {
        return persistedAddressList().stream();
    }

    private PersistedList<String> persistedAddressList() {
        if (!data.stringListKeys().contains(ADDRESSES_KEY)) {
            data.setStringList(ADDRESSES_KEY, PersistedList.persistedStringList());
        }
        return data.getStringList(ADDRESSES_KEY);
    }
}
