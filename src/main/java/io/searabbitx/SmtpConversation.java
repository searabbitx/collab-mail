package io.searabbitx;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmtpConversation {
    private final String conversation;

    public SmtpConversation(String conversation) {
        this.conversation = conversation;
    }

    public Optional<Mail> extractMail() {
        return extractDataCommand().map(data -> new Mail("test123", "testto", "SOme subject", "Some content"));
    }

    private Optional<String> extractDataCommand() {
        Pattern dataPattern = Pattern.compile(
                "DATA\\s*\\r?\\n354.*?\\r?\\n(.*?)\\r?\\n\\.\\r?\\n",
                Pattern.DOTALL | Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = dataPattern.matcher(conversation);
        return matcher.find() ? Optional.of(matcher.group(1).trim()) : Optional.empty();
    }
}
