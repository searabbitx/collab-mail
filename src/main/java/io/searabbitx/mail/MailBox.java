package io.searabbitx.mail;

import burp.api.montoya.collaborator.CollaboratorClient;
import burp.api.montoya.collaborator.Interaction;
import burp.api.montoya.collaborator.SmtpDetails;
import io.searabbitx.storage.Storage;

import java.util.Optional;
import java.util.stream.Stream;

public class MailBox {
    private final CollaboratorClient client;
    private final Storage storage;

    public MailBox(Storage storage) {
        this.storage = storage;
        this.client = storage.fetchClient();
    }

    public String generate(String username) {
        var dom = client.generatePayload();
        var a = new Address(username, dom.toString());
        storage.storeAddress(a.toString());
        return a.toString();
    }

    public Stream<String> addresses() {
        return storage.fetchAddresses();
    }

    public Stream<Mail> mails() {
        return storage.fetchMails();
    }

    public Stream<Mail> pollInteractions() {
        return client.getAllInteractions().stream()
                .map(Interaction::smtpDetails)
                .flatMap(Optional::stream)
                .map(SmtpDetails::conversation)
                .map(SmtpConversation::new)
                .map(SmtpConversation::extractMail)
                .flatMap(Optional::stream)
                .peek(storage::storeMail);
    }

    public void removeAddress(String add) {
        this.storage.removeAddress(add);
    }

    public void clearMails() {
        this.storage.clearMails();
    }

    public void removeMailAt(int row) {
        this.storage.removeMailAt(row);
    }

    private record Address(String username, String domain) {
        @Override
        public String toString() {
            return username + "@" + domain;
        }
    }
}
