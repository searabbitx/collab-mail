package io.searabbitx.ui.pane;

import io.searabbitx.mail.MailBox;
import io.searabbitx.ui.AddressTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddressPane {
    private final MailBox mailBox;
    private final AddressTable addressTable;
    
    private JSplitPane component;
    private final JButton addButton;
    private final JButton removeButton;

    public AddressPane(MailBox mailBox) {
        this.mailBox = mailBox;
        addressTable = new AddressTable(this.mailBox::removeAddressAt);
        addButton = new JButton("Add");
        removeButton = new JButton("Remove");

        addButton.addActionListener(_ -> showAddEntryDialog());
        removeButton.addActionListener(_ -> addressTable.removeSelectedEntry());

        setLayout();
    }

    public void restoreValues() {
        mailBox.addresses().forEach(addressTable::addRow);
    }

    public Component component() {
        return component;
    }

    private void showAddEntryDialog() {
        // Create input dialog for Info and Value
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(component), "Add New User", true);
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
                String address = mailBox.generate(username);
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
        dialog.setLocationRelativeTo(component);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    private void setLayout() {
        JScrollPane addressScrollPane = new JScrollPane(addressTable);
        addressScrollPane.setPreferredSize(new Dimension(250, 200));

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        var buttonGbc = new GridBagConstraints();
        buttonGbc.gridy = 0;
        buttonGbc.gridx = 0;
        buttonGbc.fill = GridBagConstraints.HORIZONTAL;
        buttonGbc.anchor = GridBagConstraints.NORTH;
        buttonGbc.weightx = 1.0;
        buttonGbc.weighty = 0;
        buttonGbc.insets = new Insets(5, 10, 5, 10);
        buttonPanel.add(addButton, buttonGbc);
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
        
        component = addressPanel;
    }
}
