package io.searabbitx.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.Optional;
import java.util.stream.Stream;

import burp.api.montoya.collaborator.Collaborator;
import burp.api.montoya.collaborator.CollaboratorClient;
import burp.api.montoya.collaborator.SecretKey;
import burp.api.montoya.persistence.PersistedList;
import burp.api.montoya.persistence.PersistedObject;
import io.searabbitx.mail.Address;
import io.searabbitx.mail.AddressV1;
import io.searabbitx.mail.Mail;
import io.searabbitx.util.Logger;

public class Storage {
    private static final String ADDRESSES_KEY = "addresses";
    private static final String MAILS_KEY = "mails";
    private static final String COLLAB_CLIENT = "collab-client";

    private final PersistedObject data;
    private final Collaborator collaborator;

    public Storage(PersistedObject data, Collaborator collaborator) {
        this.data = data;
        this.collaborator = collaborator;
    }

    private static String encode(Object obj) {
        var baos = new ByteArrayOutputStream();
        try (
                var oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
        } catch (IOException e) {
            Logger.exception(e);
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    private static Optional<Mail> decodeMail(String enc) {
        byte[] data = Base64.getDecoder().decode(enc);
        try (
                var ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            // TODO: safe deserialization. Although if some can control your project file
            // they can inject code in a different way too
            return Optional.of((Mail) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    private static Optional<Address> decodeAddress(String enc) {
        if (enc.matches("^[^@]+@[^@]+$")) {
            return Optional.of(Address.fromFullAddr(enc, ""));
        }
        byte[] data = Base64.getDecoder().decode(enc);
        try (
                var ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            // TODO: safe deserialization. Although if some can control your project file
            // they can inject code in a different way too
            return Optional.of((Address) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    public void storeAddress(Address address) {
        var encoded = encode(address);
        persistedAddressList().add(encoded);
    }

    public void storeMail(Mail mail) {
        var encoded = encode(mail);
        persistedMailList().add(encoded);
    }

    public void removeAddressAt(int row) {
        var perAdd = persistedAddressList();
        String add = perAdd.get(row);
        var filtered = perAdd.stream().filter(o -> !o.equals(add)).toList();
        perAdd.clear();
        perAdd.addAll(filtered);
    }

    void storeClient(CollaboratorClient client) {
        data.setString(COLLAB_CLIENT, client.getSecretKey().toString());
    }

    public Stream<Mail> fetchMails() {
        return persistedMailList().stream()
                .map(Storage::decodeMail)
                .flatMap(Optional::stream);
    }

    public void clearMails() {
        persistedMailList().clear();
    }

    public void removeMailAt(int row) {
        var mail = persistedMailList().get(row);
        var perMail = persistedMailList();
        var filtered = perMail.stream().filter(o -> !o.equals(mail)).toList();
        perMail.clear();
        perMail.addAll(filtered);
    }

    public void updateAddressNoteAt(int row, String n) {
        var addr = decodeAddress(persistedAddressList().get(row));
        addr.ifPresent(a -> {
            var updated = new AddressV1(a.username(), a.domain(), n);
            persistedAddressList().set(row, encode(updated));
        });
    }

    public Mail mailAt(int row) {
        return decodeMail(persistedMailList().get(row)).orElseThrow();
    }

    public CollaboratorClient fetchClient() {
        var key = data.getString(COLLAB_CLIENT);
        if (null == key) {
            var client = collaborator.createClient();
            storeClient(client);
            return client;
        }
        return collaborator.restoreClient(SecretKey.secretKey(key));
    }

    public Stream<Address> fetchAddresses() {
        return persistedAddressList().stream()
                .map(Storage::decodeAddress)
                .flatMap(Optional::stream);
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
