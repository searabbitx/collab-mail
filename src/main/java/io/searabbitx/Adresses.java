package io.searabbitx;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.collaborator.CollaboratorClient;

import java.util.ArrayList;
import java.util.List;

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

    public void remove(String add) {
        this.book.removeIf(a -> a.toString().equals(add));
    }

    private record Address(String username, String domain) {
        public String toString() {
            return username + "@" + domain;
        }
    }
}
