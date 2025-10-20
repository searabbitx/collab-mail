package io.searabbitx.mail;

import burp.api.montoya.collaborator.CollaboratorClient;
import burp.api.montoya.collaborator.Interaction;
import burp.api.montoya.collaborator.SmtpDetails;
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
                .peek(m -> Logger.log("Parsed mail from: " + m.from()))
                .filter(mail -> addresses().anyMatch(mail.to()::contains))
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

    private record Address(String username, String domain) {
        @SuppressWarnings("NullableProblems")
        @Override
        public String toString() {
            return username + "@" + domain;
        }
    }
}
