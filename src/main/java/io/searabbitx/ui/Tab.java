package io.searabbitx.ui;

import io.searabbitx.mail.MailBox;
import io.searabbitx.ui.pane.PollButtonPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Tab extends JPanel {
    private static final int POLLING_PERIOD_SECONDS = 5;

    private final MailBox mailBox;

    private MessagesTable messagesTable;
    private AddressTable addressTable;

    private PollButtonPane pollButtonPane;

    private JButton actionButton;
    private JButton removeButton;
    private JButton clearButton;
    private JButton removeMessageButton;

    public Tab(MailBox mailBox) {
        this.mailBox = mailBox;

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

        addressTable = new AddressTable(this.mailBox::removeAddressAt);

        // Initialize button
        actionButton = new JButton("Add");
        removeButton = new JButton("Remove");
        clearButton = new JButton("Clear");
        removeMessageButton = new JButton("Remove selected");

        restoreValues();
    }

    private void restoreValues() {
        mailBox.addresses().forEach(addressTable::addRow);
        mailBox.mails().forEach(messagesTable::addRow);
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        var messagesPane = createMessagesPane();
        var addressPane = createAddressPane();


        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, addressPane, messagesPane);
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

        pollButtonPane = new PollButtonPane();

        var verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pollButtonPane, splitPane);
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

    private Component createAddressPane() {
        JScrollPane addressScrollPane = new JScrollPane(addressTable);
        addressScrollPane.setPreferredSize(new Dimension(250, 200));

        // Create button panel with both buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        var buttonGbc = new GridBagConstraints();
        buttonGbc.gridy = 0;
        buttonGbc.gridx = 0;
        buttonGbc.fill = GridBagConstraints.HORIZONTAL;
        buttonGbc.anchor = GridBagConstraints.NORTH;
        buttonGbc.weightx = 1.0;
        buttonGbc.weighty = 0;
        buttonGbc.insets = new Insets(5, 10, 5, 10);
        buttonPanel.add(actionButton, buttonGbc);
        buttonGbc.gridy = 1;
        buttonPanel.add(removeButton, buttonGbc);
        buttonGbc.weighty = 2.0;
        buttonPanel.add(new JPanel(), buttonGbc);

        JSplitPane addressPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buttonPanel, addressScrollPane);
        addressPanel.setResizeWeight(0.10);
        addressPanel.setOneTouchExpandable(true); // Add expand/collapse buttons
        addressPanel.setContinuousLayout(true); // Smooth resizing
        addressPanel.setDividerSize(8); // Set divider thickness
        addressPanel.setBorder(BorderFactory.createTitledBorder("Addresses"));
        return addressPanel;
    }

    private void setupEventHandlers() {
        actionButton.addActionListener(_ -> showAddEntryDialog());
        removeButton.addActionListener(_ -> addressTable.removeSelectedEntry());
        clearButton.addActionListener(_ -> messagesTable.clearMessages());
        removeMessageButton.addActionListener(_ -> messagesTable.removeSelectedEntry());

        pollButtonPane.onPollButtonPressed(this::pollMessages);
    }

    private void pollMessages() {
        this.mailBox.pollInteractions().forEach(messagesTable::addRow);
    }

    private void showAddEntryDialog() {
        // Create input dialog for Info and Value
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New User", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Create input fields
        JTextField userField = new JTextField(20);

        // Create labels
        JLabel infoLabel = new JLabel("Username:");

        // Create buttons
        JButton addButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");

        // Layout components
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Info label and field
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(infoLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        dialog.add(userField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        dialog.add(buttonPanel, gbc);

        // Button actions
        addButton.addActionListener(e -> {
            String username = userField.getText().trim();

            if (!username.isEmpty()) {
                String address = this.mailBox.generate(username);
                addressTable.addRow(address);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter username",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        // Enter key support for add button
        dialog.getRootPane().setDefaultButton(addButton);

        // Set focus to info field
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                userField.requestFocus();
            }
        });

        // Configure and show dialog
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }
}
