package io.searabbitx.ui;

import io.searabbitx.mail.MailBox;
import io.searabbitx.ui.pane.AddressPane;
import io.searabbitx.ui.pane.PollButtonPane;

import javax.swing.*;
import java.awt.*;

public class Tab extends JPanel {
    private static final int POLLING_PERIOD_SECONDS = 5;

    private final MailBox mailBox;

    private MessagesTable messagesTable;

    private final PollButtonPane pollButtonPane;
    private final AddressPane addressPane;

    private JButton clearButton;
    private JButton removeMessageButton;

    public Tab(MailBox mailBox) {
        this.mailBox = mailBox;
        addressPane = new AddressPane(mailBox);
        pollButtonPane = new PollButtonPane();

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupPeriodicTasks();
    }

    private void setupPeriodicTasks() {
        var timer = new Timer(POLLING_PERIOD_SECONDS * 1000, _ -> pollMessages());
        timer.setRepeats(true);
        timer.start();
    }

    private void initializeComponents() {
        messagesTable = new MessagesTable(this.mailBox::removeMailAt, this.mailBox::clearMails);

        clearButton = new JButton("Clear");
        removeMessageButton = new JButton("Remove selected");

        restoreValues();
    }

    private void restoreValues() {
        addressPane.restoreValues();
        mailBox.mails().forEach(messagesTable::addRow);
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        var messagesPane = createMessagesPane();


        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, addressPane.component(), messagesPane);
        splitPane.setResizeWeight(0.10); // Initially give 75% to left pane
        splitPane.setOneTouchExpandable(true); // Add expand/collapse buttons
        splitPane.setContinuousLayout(true); // Smooth resizing
        splitPane.setDividerSize(8); // Set divider thickness

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);

        var verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pollButtonPane.component(), splitPane);
        verticalSplit.setResizeWeight(0);
        verticalSplit.setDividerSize(8); // Set divider thickness

        add(verticalSplit, gbc);
    }

    private Component createMessagesPane() {
        var main = new JPanel(new GridBagLayout());

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
        buttonPanel.add(removeMessageButton, buttonGbc);

        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        main.add(buttonPanel, gbc);

        JScrollPane messagesScrollPane = new JScrollPane(messagesTable);
        messagesScrollPane.setMinimumSize(new Dimension(300, 400));
        messagesScrollPane.setPreferredSize(new Dimension(600, 400));
        gbc.weighty = 2.0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        main.add(messagesScrollPane, gbc);

        main.setBorder(BorderFactory.createTitledBorder("Inbox"));

        return main;
    }

    private void setupEventHandlers() {
        clearButton.addActionListener(_ -> messagesTable.clearMessages());
        removeMessageButton.addActionListener(_ -> messagesTable.removeSelectedEntry());

        pollButtonPane.onPollButtonPressed(this::pollMessages);
    }

    private void pollMessages() {
        this.mailBox.pollInteractions().forEach(messagesTable::addRow);
    }

}
