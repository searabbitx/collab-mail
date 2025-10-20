package io.searabbitx.mail;

import io.searabbitx.util.Logger;
import jakarta.mail.Address;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.mail2.jakarta.util.MimeMessageParser;

import java.io.ByteArrayInputStream;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SmtpConversation {
    private final String conversation;

    SmtpConversation(String conversation) {
        this.conversation = conversation;
    }

    private static Mail mimeParserToMail(MimeMessageParser parser) throws Exception {
        var tos = parser.getTo().stream().map(Address::toString).toList();
        var bcc = parser.getBcc().stream().map(Address::toString).toList();
        var cc = parser.getCc().stream().map(Address::toString).toList();
        return new Mail(
                parser.getFrom(),
                String.join(", ", tos),
                String.join(", ", cc),
                String.join(", ", bcc),
                parser.getSubject(),
                parser.getPlainContent(),
                parser.getHtmlContent()
        );
    }

    Optional<Mail> extractMail() {
        Logger.log("Received smtp connection");
        return extractDataCommand().flatMap(this::parseData);
    }

    private Optional<String> extractDataCommand() {
        Pattern pattern = Pattern.compile(
                "DATA\\s*\\r?\\n354.*?\\r?\\n(.*?)\\r?\\n\\.\\r?\\n",
                Pattern.DOTALL | Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(conversation);
        return matcher.find() ? Optional.of(matcher.group(1).trim()) : Optional.empty();
    }

    private Optional<Mail> parseData(String data) {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props);
        ByteArrayInputStream bis = new ByteArrayInputStream(data.getBytes());
        try {
            MimeMessage message = new MimeMessage(session, bis);
            MimeMessageParser parser = new MimeMessageParser(message);
            parser.parse();
            return Optional.of(mimeParserToMail(parser));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
