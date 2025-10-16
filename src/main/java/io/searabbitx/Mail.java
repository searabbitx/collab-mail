package io.searabbitx;

public record Mail(String from, String to, String subject, String content) {
}
