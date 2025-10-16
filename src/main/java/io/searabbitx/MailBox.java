package io.searabbitx;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.collaborator.CollaboratorClient;
import burp.api.montoya.collaborator.Interaction;
import burp.api.montoya.collaborator.SmtpDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

class MailBox {
    private final CollaboratorClient client;
    private final List<Address> book;

    MailBox(MontoyaApi api) {
        this.client = api.collaborator().createClient();
        this.book = new ArrayList<>();
    }

    String generate(String username) {
        var dom = client.generatePayload();
        var a = new Address(username, dom.toString());
        book.add(a);
        return a.toString();
    }

    Stream<Mail> pollInteractions() {
        Logger.log("Polling!");
        var convos = client.getAllInteractions().stream()
                .peek(_ -> Logger.log("Got some interaction"))
                .map(Interaction::smtpDetails)
                .flatMap(Optional::stream)
                .map(SmtpDetails::conversation)
                .toList();

        Logger.log("Parsing " + convos.size() + " convos");

        var result = new ArrayList<Mail>();
        for (var convo : convos) {
            Logger.log("inside convos loop");
            var parser = new SmtpConversation(convo);
            Logger.log("SmtpConversation obj constructed");
            parser.extractMail().ifPresent(result::add);
            Logger.log("Mail extracted");
        }

        return result.stream();
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
