package io.searabbitx.ui.pane;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.editor.EditorOptions;
import burp.api.montoya.ui.editor.HttpResponseEditor;
import burp.api.montoya.ui.editor.RawEditor;
import io.searabbitx.mail.Mail;
import io.searabbitx.util.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class DetailsPane {
    private final JPanel component;
    private final JTextPane detailsTextPane;
    private final JTextPane renderTextPane;
    private final JTabbedPane tabs;
    private final String template;
    private final HttpResponseEditor htmlEditor;
    private final RawEditor rawEditor;
    private final AttachmentsTab attachmentsTab;

    public DetailsPane(UserInterface ui) {
        detailsTextPane = new JTextPane();
        detailsTextPane.setEditable(false);
        detailsTextPane.setContentType("text/html");
        detailsTextPane.setText("<html></html>");
        template = readTemplate();

        var scroll = new JScrollPane(detailsTextPane);

        tabs = new JTabbedPane();
        tabs.addTab("Details", scroll);
        htmlEditor = ui.createHttpResponseEditor(EditorOptions.READ_ONLY);
        tabs.addTab("Html", htmlEditor.uiComponent());
        tabs.setEnabledAt(1, false);

        renderTextPane = new JTextPane();
        renderTextPane.setEditable(false);
        renderTextPane.setContentType("text/html");
        renderTextPane.setText("");
        renderTextPane.setBackground(Color.WHITE);
        renderTextPane.setForeground(Color.BLACK);
        tabs.addTab("Render", renderTextPane);
        tabs.setEnabledAt(2, false);

        rawEditor = ui.createRawEditor(EditorOptions.READ_ONLY);
        tabs.addTab("SMTP Conversation", rawEditor.uiComponent());
        tabs.setEnabledAt(3, false);

        attachmentsTab = new AttachmentsTab(ui);
        tabs.addTab("Attachments", attachmentsTab.component());
        tabs.setEnabledAt(4, false);

        component = new JPanel(new GridBagLayout());
        var gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        component.add(tabs, gbc);
    }

    private static String escape(String str) {
        String out = "";
        for (char c : str.toCharArray()) {
            if (!Character.isLetterOrDigit(c))
                out += String.format("&#x%x;", (int) c);
            else
                out += String.format("%s", c);

        }
        return out;
    }

    private static String renderAttachmentsList(Mail mail) {
        return mail.attachments().stream()
                .map(a -> String.format(
                        "<li>%s (%s) <i>%s</i></li>",
                        escape(a.name()),
                        escape(a.contentType()),
                        escape(humanReadableSize(a.content().length)))
                ).reduce("", (a, b) -> a + "\n" + b);
    }

    public static String humanReadableSize(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }

    public Component component() {
        return component;
    }

    public void update(Mail mail) {
        tabs.setEnabledAt(1, true);
        tabs.setEnabledAt(2, true);
        tabs.setEnabledAt(3, true);
        var text = template
                .replace("{{FROM}}", escape(mail.from()))
                .replace("{{TO}}", escape(mail.to()))
                .replace("{{CC}}", escape(mail.cc()))
                .replace("{{BCC}}", escape(mail.bcc()))
                .replace("{{SUBJECT}}", escape(mail.subject()))
                .replace("{{PLAIN}}", mail.plainContent())
                .replace("{{ATTACHMENTS}}", renderAttachmentsList(mail));
        detailsTextPane.setText(text);

        var dummyHttpPrefix = """
                Content-Type: text/html\r
                \r
                OK""";
        HttpResponse response = HttpResponse.httpResponse(dummyHttpPrefix).withBody(mail.htmlContent());

        htmlEditor.setResponse(response);

        renderTextPane.setText(mail.htmlContent());

        rawEditor.setContents(ByteArray.byteArray(mail.smtpConversation().getBytes()));

        attachmentsTab.show(mail.attachments());
        tabs.setEnabledAt(4, !mail.attachments().isEmpty());
    }

    private String readTemplate() {
        try (InputStream is = getClass().getResourceAsStream("/mailDetailsTemplate.html")) {
            assert is != null;
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            Logger.exception(e);
            throw new RuntimeException(e);
        }
    }
}
