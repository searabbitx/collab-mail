package io.searabbitx.ui.pane;

import io.searabbitx.mail.Address;
import io.searabbitx.mail.MailBox;
import io.searabbitx.ui.table.AddressTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;
import java.util.function.BiConsumer;

public class AddressPane {
    private final MailBox mailBox;
    private final AddressTable addressTable;
    private final JButton addButton;
    private final JButton editButton;
    private final JButton removeButton;
    private JSplitPane component;

    public AddressPane(MailBox mailBox) {
        this.mailBox = mailBox;
        addressTable = new AddressTable(this.mailBox::removeAddressAt);
        addButton = new JButton("Add");
        editButton = new JButton("Edit");
        removeButton = new JButton("Remove");

        BiConsumer<String, String> onAddrAdded = (u, n) -> {
            var address = mailBox.generate(u, n);
            addressTable.addRow(address);
        };
        addButton.addActionListener(_ -> showAddressDialog(onAddrAdded, Optional.empty()));
        BiConsumer<String, String> onAddrEdited = (_, n) -> {
            addressTable.selectedRowIndex().ifPresent(ri -> {
                addressTable.updateNoteAt(ri, n);
                mailBox.updateAddressNoteAt(ri.modelRow(), n);
            });
        };
        editButton.addActionListener(_ -> showAddressDialog(onAddrEdited, addressTable.selectedAddress()));
        removeButton.addActionListener(_ -> addressTable.removeSelectedEntry());

        setLayout();
    }

    public void restoreValues() {
        mailBox.addresses().forEach(addressTable::addRow);
    }

    public Component component() {
        return component;
    }

    private void showAddressDialog(BiConsumer<String, String> onSuccess, Optional<Address> address) {
        // Create input dialog for Info and Value
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(component), "Add New User", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Create input fields
        JTextField userField = new JTextField(20);
        // Note field
        JTextArea noteField = new JTextArea(7, 20);
        JScrollPane noteScroll = new JScrollPane(noteField);

        // Create labels
        JLabel userLabel = new JLabel("Username:");
        JLabel noteLabel = new JLabel("Note:");

        // Create buttons
        JButton okButton = new JButton("Ok");
        JButton cancelButton = new JButton("Cancel");

        // Layout components
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        var initialFill = gbc.fill;
        // Info label and field
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(userLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        dialog.add(userField, gbc);

        // note label and field
        gbc.weightx = 0.0;
        gbc.fill = initialFill;
        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(noteLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        dialog.add(noteScroll, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        dialog.add(buttonPanel, gbc);

        address.ifPresent(a -> {
            userField.setText(a.toString());
            userField.setEditable(false);
            noteField.setText(a.note());
        });

        // Button actions
        okButton.addActionListener(e -> {
            String username = userField.getText().trim();
            String note = noteField.getText().trim();

            if (!username.isEmpty()) {
                onSuccess.accept(username, note);
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
        dialog.getRootPane().setDefaultButton(okButton);

        // Set focus to info field
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                userField.requestFocus();
            }
        });

        // Configure and show dialog
        dialog.setSize(new Dimension(355, 255));
        dialog.setLocationRelativeTo(component);
        dialog.setResizable(true);
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
        buttonPanel.add(editButton, buttonGbc);
        buttonGbc.gridy = 2;
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
