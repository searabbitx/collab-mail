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
import java.util.stream.Stream;

class SmtpConversation {
    private static final Pattern PATTERN = Pattern.compile(
            "DATA\\s*\\r?\\n354.*?\\r?\\n(.*?)\\r?\\n\\.\\r?\\n",
            Pattern.DOTALL | Pattern.CASE_INSENSITIVE
    );
    private static final Pattern TRUNCATED_PATTERN = Pattern.compile(
            "DATA\\s*\\r?\\n354.*?\\r?\\n(.*?)$",
            Pattern.DOTALL | Pattern.CASE_INSENSITIVE
    );

    private final String conversation;
    private final LocalDateTime time;

    SmtpConversation(String conversation, LocalDateTime time) {
        this.conversation = conversation;
        this.time = time;
    }

    static Optional<SmtpConversation> fromInteraction(Interaction i) {
        return i.smtpDetails().map(sd -> new SmtpConversation(sd.conversation(), i.timeStamp().toLocalDateTime()));
    }

    private static List<? extends Attachment> mimeParserAttachments(MimeMessageParser parser) {
        return parser.getAttachmentList().stream()
                .map(ds -> {
                    try {
                        return new AttachmentV1(ds.getName(), ds.getContentType(), ds.getInputStream().readAllBytes());
                    } catch (IOException e) {
                        Logger.exception(e);
                        throw new RuntimeException(e);
                    }
                }).toList();
    }

    Optional<Mail> extractMail() {
        Logger.log("Received smtp connection");
        return extractDataCommand().flatMap(this::parseData);
    }

    private Mail mimeParserToMail(MimeMessageParser parser, boolean isTruncated) throws Exception {
        var tos = parser.getTo().stream().map(Address::toString).toList();
        var bcc = parser.getBcc().stream().map(Address::toString).toList();
        var cc = parser.getCc().stream().map(Address::toString).toList();
        return new MailV1(
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
                isTruncated
        );
    }

    private Optional<DataCommand> extractDataCommand() {
        return Stream.concat(
                extract(PATTERN).map(dc -> new DataCommand(dc, false)).stream(),
                extract(TRUNCATED_PATTERN).map(dc -> new DataCommand(dc, true)).stream()
        ).findFirst();
    }

    private Optional<String> extract(Pattern pattern) {
        Matcher matcher = pattern.matcher(conversation);
        return matcher.find() ? Optional.of(matcher.group(1).trim()) : Optional.empty();
    }

    private Optional<Mail> parseData(DataCommand dc) {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props);
        ByteArrayInputStream bis = new ByteArrayInputStream(dc.command().getBytes());
        try {
            MimeMessage message = new MimeMessage(session, bis);
            MimeMessageParser parser = new MimeMessageParser(message);
            parser.parse();
            return Optional.of(mimeParserToMail(parser, dc.isTruncated()));
        } catch (Exception e) {
            if (e.getCause() instanceof DecodingException) {
                return parseData(dc.removeLastByte());
            }
            Logger.exception(e);
            throw new RuntimeException(e);
        }
    }

    private record DataCommand(String command, boolean isTruncated) {
        private DataCommand removeLastByte() {
            return new DataCommand(command.substring(0, command.length() - 1), isTruncated);
        }
    }
}
