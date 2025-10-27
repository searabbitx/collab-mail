package io.searabbitx.mail;

import burp.api.montoya.collaborator.Interaction;
import com.sun.mail.util.DecodingException;
import io.searabbitx.util.Logger;
import jakarta.mail.Address;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.mail2.jakarta.util.MimeMessageParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SmtpConversation {
    private static final int SMTP_CONVERSATION_LIMIT = 8192;

    private final String conversation;
    private final LocalDateTime time;

    SmtpConversation(String conversation, LocalDateTime time) {
        this.conversation = conversation;
        this.time = time;
    }

    static Optional<SmtpConversation> fromInteraction(Interaction i) {
        return i.smtpDetails().map(sd -> new SmtpConversation(sd.conversation(), i.timeStamp().toLocalDateTime()));
    }

    Optional<Mail> extractMail() {
        Logger.log("Received smtp connection");
        return extractDataCommand().flatMap(this::parseData);
    }

    private static List<Mail.Attachment> mimeParserAttachments(MimeMessageParser parser) {
        return parser.getAttachmentList().stream()
                .map(ds -> {
                    try {
                        return new Mail.Attachment(ds.getName(), ds.getContentType(), ds.getInputStream().readAllBytes());
                    } catch (IOException e) {
                        Logger.exception(e);
                        throw new RuntimeException(e);
                    }
                }).toList();
    }

    private Mail mimeParserToMail(MimeMessageParser parser) throws Exception {
        var tos = parser.getTo().stream().map(Address::toString).toList();
        var bcc = parser.getBcc().stream().map(Address::toString).toList();
        var cc = parser.getCc().stream().map(Address::toString).toList();
        return new Mail(
                time,
                parser.getFrom(),
                String.join(", ", tos),
                String.join(", ", cc),
                String.join(", ", bcc),
                parser.getSubject(),
                parser.getPlainContent(),
                parser.getHtmlContent(),
                conversation,
                mimeParserAttachments(parser),
                isConversationTruncated()
        );
    }

    private boolean isConversationTruncated() {
        return conversation.length() >= SMTP_CONVERSATION_LIMIT;
    }

    private Optional<String> extractDataCommand() {
        var regex = isConversationTruncated()
                ? "DATA\\s*\\r?\\n354.*?\\r?\\n(.*?)$"
                : "DATA\\s*\\r?\\n354.*?\\r?\\n(.*?)\\r?\\n\\.\\r?\\n";
        Pattern pattern = Pattern.compile(
                regex,
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
            if (e.getCause() instanceof DecodingException) {
                return parseData(data.substring(0, data.length() - 1));
            }
            Logger.exception(e);
            throw new RuntimeException(e);
        }
    }
}
