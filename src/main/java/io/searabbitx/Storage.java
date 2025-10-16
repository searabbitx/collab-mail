package io.searabbitx;

import burp.api.montoya.collaborator.Collaborator;
import burp.api.montoya.collaborator.CollaboratorClient;
import burp.api.montoya.collaborator.SecretKey;
import burp.api.montoya.persistence.PersistedList;
import burp.api.montoya.persistence.PersistedObject;

import java.io.*;
import java.util.Base64;
import java.util.stream.Stream;

class Storage {
    private static final String ADDRESSES_KEY = "addresses";
    private static final String MAILS_KEY = "mails";
    private static final String COLLAB_CLIENT = "collab-client";

    private PersistedObject data;
    private Collaborator collaborator;

    Storage(PersistedObject data, Collaborator collaborator) {
        this.data = data;
        this.collaborator = collaborator;
    }

    private static String encodeMail(Mail mail) {
        var baos = new ByteArrayOutputStream();
        try (
                var oos = new ObjectOutputStream(baos)
        ) {
            oos.writeObject(mail);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    private static Mail encodeMail(String enc) {
        byte[] data = Base64.getDecoder().decode(enc);
        try (
                var ois = new ObjectInputStream(new ByteArrayInputStream(data))
        ) {
            // TODO: safe deserialization. Although if some can control your project file they can inject code in a different way too
            return (Mail) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
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

    void storeMail(Mail mail) {
        var encoded = encodeMail(mail);
        persistedMailList().add(encoded);
    }

    Stream<Mail> fetchMails() {
        return persistedMailList().stream()
                .map(Storage::encodeMail);
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

    private PersistedList<String> persistedMailList() {
        if (!data.stringListKeys().contains(MAILS_KEY)) {
            data.setStringList(MAILS_KEY, PersistedList.persistedStringList());
        }
        return data.getStringList(MAILS_KEY);
    }
}
