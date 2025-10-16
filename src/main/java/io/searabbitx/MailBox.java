package io.searabbitx;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.collaborator.CollaboratorClient;
import burp.api.montoya.collaborator.Interaction;
import burp.api.montoya.collaborator.SmtpDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class MailBox {
    private final CollaboratorClient client;
    private final List<Address> book;

    public MailBox(MontoyaApi api) {
        this.client = api.collaborator().createClient();
        this.book = new ArrayList<>();
    }

    public String generate(String username) {
        var dom = client.generatePayload();
        var a = new Address(username, dom.toString());
        book.add(a);
        return a.toString();
    }

    public Stream<Mail> pollInteractions() {
        return client.getAllInteractions().stream()
                .map(Interaction::smtpDetails)
                .flatMap(Optional::stream)
                .map(SmtpDetails::conversation)
                .map(SmtpConversation::new)
                .flatMap(sc -> sc.extractMail().stream());
    }

    public void remove(String add) {
        this.book.removeIf(a -> a.toString().equals(add));
    }

    private record Address(String username, String domain) {
        @Override
        public String toString() {
            return username + "@" + domain;
        }
    }
}
