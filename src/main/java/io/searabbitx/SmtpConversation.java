package io.searabbitx;

import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.parser.ContentHandler;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.apache.james.mime4j.stream.BodyDescriptor;
import org.apache.james.mime4j.stream.Field;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SmtpConversation {
    private final String conversation;

    SmtpConversation(String conversation) {
        this.conversation = conversation;
    }

    Optional<Mail> extractMail() {
        Logger.log("Received smtp connection");
        var recipient = extractRecipient().orElse("???");
        return extractDataCommand().flatMap(data -> parseData(data, recipient));
    }

    private Optional<String> extractRecipient() {
        return extractRegex("RCPT TO\\s*:\\s<(.*)>\\s*\\r?\\n");
    }

    private Optional<String> extractDataCommand() {
        return extractRegex("DATA\\s*\\r?\\n354.*?\\r?\\n(.*?)\\r?\\n\\.\\r?\\n");
    }

    private Optional<String> extractRegex(String regex) {
        Pattern pattern = Pattern.compile(
                regex,
                Pattern.DOTALL | Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(conversation);
        return matcher.find() ? Optional.of(matcher.group(1).trim()) : Optional.empty();
    }

    private Optional<Mail> parseData(String data, String recipient) {
        var parser = new MimeStreamParser();
        CompletableFuture<Mail> future = new CompletableFuture<>();
        parser.setContentHandler(new SimplContentHandler(future));

        try {
            var is = new ByteArrayInputStream(
                data.getBytes(StandardCharsets.UTF_8)
            );
            parser.parse(is);
            return Optional.of(future.get());
        } catch (MimeException | IOException | InterruptedException | ExecutionException e) {
            return Optional.empty();
        }
    }

    private static class SimplContentHandler implements ContentHandler {
        private Mail.Builder builder;
        private final CompletableFuture<Mail> future;

        private SimplContentHandler(CompletableFuture<Mail> future) {
            this.future = future;
            builder = Mail.builder();
        }


        @Override
        public void startMessage() throws MimeException {

        }

        @Override
        public void endMessage() throws MimeException {
            future.complete(builder.build());
        }

        @Override
        public void startBodyPart() throws MimeException {

        }

        @Override
        public void endBodyPart() throws MimeException {

        }

        @Override
        public void startHeader() throws MimeException {

        }

        @Override
        public void field(Field rawField) throws MimeException {
            if (rawField.getNameLowerCase().equals("from")) {
                builder.withFrom(rawField.getBody());
            }
        }

        @Override
        public void endHeader() throws MimeException {

        }

        @Override
        public void preamble(InputStream is) throws MimeException, IOException {

        }

        @Override
        public void epilogue(InputStream is) throws MimeException, IOException {

        }

        @Override
        public void startMultipart(BodyDescriptor bd) throws MimeException {

        }

        @Override
        public void endMultipart() throws MimeException {

        }

        @Override
        public void body(BodyDescriptor bd, InputStream is) throws MimeException, IOException {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int length; (length = is.read(buffer)) != -1; ) {
                result.write(buffer, 0, length);
            }
            var body = result.toString(bd.getCharset());
            builder.withBody(body);
        }

        @Override
        public void raw(InputStream is) throws MimeException, IOException {

        }
    }
}
