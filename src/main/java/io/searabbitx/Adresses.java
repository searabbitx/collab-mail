package io.searabbitx;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.collaborator.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Adresses {
    private CollaboratorClient client;
    private List<Address> book;

    public Adresses(MontoyaApi api) {
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
        return Stream.of(
                new Mail("test@example.com", "test2@example.com", "Interaction 1 message", "Some content"),
                new Mail("test2@example.com", "test3@example.com", "Interaction 2 message", "Some other content")
        );
    }

    public void remove(String add) {
        this.book.removeIf(a -> a.toString().equals(add));
    }

    private record Address(String username, String domain) {
        public String toString() {
            return username + "@" + domain;
        }
    }

    public record Mail(String from, String to, String topic, String content){}
}
