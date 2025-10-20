package io.searabbitx.ui.pane;

import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.editor.EditorOptions;
import burp.api.montoya.ui.editor.HttpResponseEditor;
import io.searabbitx.mail.Mail;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class DetailsPane {
    private final JPanel component;
    private final JTextPane textPane;
    private final JTabbedPane tabs;
    private final String template;
    private final UserInterface ui;
    private final HttpResponseEditor htmlEditor;

    public DetailsPane(UserInterface ui) {
        this.ui = ui;
        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setContentType("text/html");
        textPane.setText("<html></html>");
        template = readTemplate();

        var scroll = new JScrollPane(textPane);

        tabs = new JTabbedPane();
        tabs.addTab("Details", scroll);
        htmlEditor = ui.createHttpResponseEditor(EditorOptions.READ_ONLY);
        tabs.addTab("Html", htmlEditor.uiComponent());
        tabs.setEnabledAt(1, false);

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

    public Component component() {
        return component;
    }

    public void update(Mail mail) {
        tabs.setEnabledAt(1, true);
        var text = template
                .replace("{{FROM}}", escape(mail.from()))
                .replace("{{TO}}", escape(mail.to()))
                .replace("{{SUBJECT}}", escape(mail.subject()))
                .replace("{{PLAIN}}", mail.plainContent());
        textPane.setText(text);

        var dummyHttpPrefix = """
                Content-Type: text/html\r
                \r
                OK""";
        HttpResponse response = HttpResponse.httpResponse(dummyHttpPrefix).withBody(mail.htmlContent());

        htmlEditor.setResponse(response);
    }

    private String readTemplate() {
        try (InputStream is = getClass().getResourceAsStream("/mailDetailsTemplate.html")) {
            assert is != null;
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
