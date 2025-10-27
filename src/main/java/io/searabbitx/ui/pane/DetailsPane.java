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
    private static final int RENDER_HTML_TAB = 1;
    private static final int RAW_HTML_TAB = 2;
    private static final int SMTP_CONVERSATION_TAB = 3;
    private static final int ATTACHMENTS_TAB = 4;

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
        tabs.setEnabledAt(RENDER_HTML_TAB, false);

        renderTextPane = new JTextPane();
        renderTextPane.setEditable(false);
        renderTextPane.setContentType("text/html");
        renderTextPane.setText("");
        renderTextPane.setBackground(Color.WHITE);
        renderTextPane.setForeground(Color.BLACK);
        tabs.addTab("Render", new JScrollPane(renderTextPane));
        tabs.setEnabledAt(RAW_HTML_TAB, false);

        rawEditor = ui.createRawEditor(EditorOptions.READ_ONLY);
        tabs.addTab("SMTP Conversation", rawEditor.uiComponent());
        tabs.setEnabledAt(SMTP_CONVERSATION_TAB, false);

        attachmentsTab = new AttachmentsTab(ui);
        tabs.addTab("Attachments", attachmentsTab.component());
        tabs.setEnabledAt(ATTACHMENTS_TAB, false);

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
            out += !Character.isLetterOrDigit(c) ? String.format("&#x%x;", (int) c) : String.format("%s", c);
        }
        return out;
    }

    private static String renderAttachmentsList(Mail mail) {
        return mail.attachments().stream().map(a -> String.format("<li>%s (%s) <i>%s</i></li>", escape(a.name()), escape(a.contentType()), escape(humanReadableSize(a.content().length)))).reduce("", (a, b) -> a + "\n" + b);
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
        updateDetailsTab(mail);
        updateHtmlContentTab(mail);
        renderTextPane.setText(mail.htmlContent());
        rawEditor.setContents(ByteArray.byteArray(mail.smtpConversation().getBytes()));
        attachmentsTab.show(mail.attachments());
        setEnabledStatusForTabs(mail);
    }

    private void updateHtmlContentTab(Mail mail) {
        if (mail.hasHtml()) {
            var dummyHttpPrefix = """
                    Content-Type: text/html\r
                    \r
                    OK""";
            HttpResponse response = HttpResponse.httpResponse(dummyHttpPrefix).withBody(mail.htmlContent());
            htmlEditor.setResponse(response);
        }
    }

    private void updateDetailsTab(Mail mail) {
        var text = template.replace("{{FROM}}", escape(mail.from())).replace("{{TO}}", escape(mail.to())).replace("{{CC}}", escape(mail.cc())).replace("{{BCC}}", escape(mail.bcc())).replace("{{SUBJECT}}", escape(mail.subject())).replace("{{TRUNCATED}}", mail.isTruncated() ? "<b>YES</b> (SMTP conversation exceeded interaction limit)" : "NO").replace("{{PLAIN}}", mail.plainContent()).replace("{{ATTACHMENTS}}", renderAttachmentsList(mail));
        detailsTextPane.setText(text);
    }

    private void setEnabledStatusForTabs(Mail mail) {
        tabs.setEnabledAt(SMTP_CONVERSATION_TAB, true);
        tabs.setEnabledAt(RENDER_HTML_TAB, mail.hasHtml());
        tabs.setEnabledAt(RAW_HTML_TAB, mail.hasHtml());
        tabs.setEnabledAt(ATTACHMENTS_TAB, mail.hasAttachments());
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
