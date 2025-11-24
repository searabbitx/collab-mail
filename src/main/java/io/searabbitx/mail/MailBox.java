package io.searabbitx.mail;

import burp.api.montoya.collaborator.CollaboratorClient;
import io.searabbitx.storage.Storage;
import io.searabbitx.util.Logger;

import java.util.Optional;
import java.util.stream.Stream;

public class MailBox {
    private final CollaboratorClient client;
    private final Storage storage;

    public MailBox(Storage storage) {
        this.storage = storage;
        this.client = storage.fetchClient();
    }

    public Address generate(String username, String note) {
        var dom = client.generatePayload();
        var a = new AddressV1(username, dom.toString(), note);
        storage.storeAddress(a);
        return a;
    }

    public Stream<Address> addresses() {
        return storage.fetchAddresses();
    }

    public Stream<Mail> mails() {
        return storage.fetchMails();
    }

    public Stream<Mail> pollInteractions() {
        return client.getAllInteractions().stream()
                .map(SmtpConversation::fromInteraction)
                .flatMap(Optional::stream)
                .map(SmtpConversation::extractMail)
                .flatMap(Optional::stream)
                .peek(m -> Logger.log("Parsed mail from: " + m.from()))
                .filter(mail -> addresses().anyMatch(a -> (mail.to() + mail.cc() + mail.bcc()).contains(a.toString())))
                .peek(storage::storeMail);
    }

    public void removeAddressAt(int row) {
        this.storage.removeAddressAt(row);
    }

    public void clearMails() {
        this.storage.clearMails();
    }

    public void removeMailAt(int row) {
        this.storage.removeMailAt(row);
    }

    public Mail mailAt(int row) {
        return this.storage.mailAt(row);
    }

    public void updateAddressNoteAt(int row, String n) {
        this.storage.updateAddressNoteAt(row, n);
    }
}
