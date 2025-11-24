package io.searabbitx.mail;

public record AddressV1(String username, String domain, String note) implements Address {
    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return username + "@" + domain;
    }
}
