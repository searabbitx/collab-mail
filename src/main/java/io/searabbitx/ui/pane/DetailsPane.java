package io.searabbitx.ui.pane;

import io.searabbitx.mail.Mail;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class DetailsPane {
    private final JPanel component;
    private final JTextPane textPane;
    private final String template;

    public DetailsPane() {
        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setContentType("text/html");
        textPane.setText("<html></html>");
        template = readTemplate();

        var scroll = new JScrollPane(textPane);

        component = new JPanel(new GridBagLayout());
        var gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        component.add(scroll, gbc);
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
        var text = template
                .replace("{{FROM}}", escape(mail.from()))
                .replace("{{TO}}", escape(mail.to()))
                .replace("{{SUBJECT}}", escape(mail.subject()))
                .replace("{{PLAIN}}", mail.plainContent());
        textPane.setText(text);
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
