package io.searabbitx;

import burp.api.montoya.collaborator.CollaboratorClient;
import burp.api.montoya.collaborator.Interaction;
import burp.api.montoya.collaborator.SmtpDetails;

import java.util.Optional;
import java.util.stream.Stream;

class MailBox {
    private final CollaboratorClient client;
    private final Storage storage;

    MailBox(Storage storage) {
        this.storage = storage;
        this.client = storage.fetchClient();
    }

    String generate(String username) {
        var dom = client.generatePayload();
        var a = new Address(username, dom.toString());
        storage.storeAddress(a.toString());
        return a.toString();
    }

    Stream<String> addresses() {
        return storage.fetchAddresses();
    }

    Stream<Mail> mails() {
        return storage.fetchMails();
    }

    Stream<Mail> pollInteractions() {
        return client.getAllInteractions().stream()
                .map(Interaction::smtpDetails)
                .flatMap(Optional::stream)
                .map(SmtpDetails::conversation)
                .map(SmtpConversation::new)
                .map(SmtpConversation::extractMail)
                .flatMap(Optional::stream)
                .peek(storage::storeMail);
    }

    void remove(String add) {
        this.storage.removeAddress(add);
    }

    private record Address(String username, String domain) {
        @Override
        public String toString() {
            return username + "@" + domain;
        }
    }
}
