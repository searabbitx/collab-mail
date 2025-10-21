package io.searabbitx.ui.pane;

import io.searabbitx.mail.Mail;
import io.searabbitx.mail.MailBox;
import io.searabbitx.ui.table.MessagesTable;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;
import java.util.function.Consumer;

public class MessagesPane {
    private final JPanel component;

    private final JButton clearButton;
    private final JButton removeButton;
    private final MessagesTable messagesTable;
    private final MailBox mailBox;

    public MessagesPane(MailBox mailBox) {
        this.mailBox = mailBox;
        component = new JPanel(new GridBagLayout());
        clearButton = new JButton("Clear");
        removeButton = new JButton("Remove selected");
        messagesTable = new MessagesTable(mailBox::removeMailAt, mailBox::clearMails);

        clearButton.addActionListener(_ -> messagesTable.clearMessages());
        removeButton.addActionListener(_ -> messagesTable.removeSelectedEntry());

        setupLayout();
    }

    public void pollMessages() {
        mailBox.pollInteractions().forEach(messagesTable::addRow);
    }

    public Component component() {
        return component;
    }

    public void restoreValues() {
        mailBox.mails().forEach(messagesTable::addRow);
    }

    private void setupLayout() {
        var gbc = new GridBagConstraints();

        var buttonPanel = new JPanel(new GridBagLayout());
        var buttonGbc = new GridBagConstraints();
        buttonGbc.gridy = 0;
        buttonGbc.gridx = 0;
        buttonGbc.fill = GridBagConstraints.HORIZONTAL;
        buttonGbc.anchor = GridBagConstraints.NORTH;
        buttonGbc.weightx = 1.0;
        buttonGbc.weighty = 0;
        buttonGbc.insets = new Insets(5, 0, 5, 5);
        buttonPanel.add(clearButton, buttonGbc);
        buttonGbc.gridx = 1;
        buttonPanel.add(removeButton, buttonGbc);

        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        component.add(buttonPanel, gbc);

        JScrollPane messagesScrollPane = new JScrollPane(messagesTable);
        gbc.weighty = 2.0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        component.add(messagesScrollPane, gbc);

        component.setBorder(BorderFactory.createTitledBorder("Inbox"));
    }

    public void addMessageConsumer(Consumer<Mail> consumer) {
        messagesTable
                .getSelectionModel()
                .addListSelectionListener(_ -> {
                    var mail = selectedMail();
                    assert mail.isPresent();
                    mail.ifPresent(consumer);
                });
    }

    private Optional<Mail> selectedMail() {
        return messagesTable.selectedRowModelIndex().map(mailBox::mailAt);
    }
}
