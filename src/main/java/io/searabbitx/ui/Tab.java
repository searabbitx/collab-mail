package io.searabbitx.ui;

import io.searabbitx.mail.Mail;
import io.searabbitx.mail.MailBox;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Tab extends JPanel {
    private static final int POLLING_PERIOD = 10;

    private final MailBox mailBox;

    private JTable messagesTable;
    private JTable addressTable;
    private JButton actionButton;
    private JButton removeButton;
    private JButton pollButton;
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
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(this::pollMessages, POLLING_PERIOD, POLLING_PERIOD, TimeUnit.SECONDS);
    }

    private void initializeComponents() {
        // Initialize left table (larger table)
        String[] messagesColumns = {"From", "To", "Subject", "Body"};
        DefaultTableModel messagesModel = new DefaultTableModel(messagesColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        messagesTable = new JTable(messagesModel);
        messagesTable.setFillsViewportHeight(true);
        messagesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setupLeftTableColumnWidths();

        // Initialize right table (smaller table)
        String[] addressesColumns = {"Address"};
        DefaultTableModel rightModel = new DefaultTableModel(addressesColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        addressTable = new JTable(rightModel);
        addressTable.setFillsViewportHeight(true);
        addressTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Initialize button
        actionButton = new JButton("Add");
        removeButton = new JButton("Remove");
        pollButton = new JButton("Poll now!");
        clearButton = new JButton("Clear");
        removeMessageButton = new JButton("Remove selected");

        restoreValues();
    }

    private void restoreValues() {
        mailBox.addresses().forEach(this::addAddressTableRow);
        mailBox.mails().forEach(this::addMessagesTableRow);
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        var messagesPane = createMessagesPane();

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


        // Create resizable split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, addressPanel, messagesPane);
        splitPane.setResizeWeight(0.10); // Initially give 75% to left pane
        splitPane.setOneTouchExpandable(true); // Add expand/collapse buttons
        splitPane.setContinuousLayout(true); // Smooth resizing
        splitPane.setDividerSize(8); // Set divider thickness

        // Add split pane to main panel using GridBagLayout
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);

        JPanel pollButtonPane = new JPanel();
        pollButtonPane.setLayout(new BoxLayout(pollButtonPane, BoxLayout.X_AXIS));
        pollButtonPane.setPreferredSize(new Dimension(0, 30));
        pollButton.setMaximumSize(new Dimension(100, 30));
        pollButton.setSize(new Dimension(100, 30));
        pollButtonPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        pollButtonPane.add(pollButton);

        var verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pollButtonPane, splitPane);
        verticalSplit.setResizeWeight(0);
//        verticalSplit.setOneTouchExpandable(true); // Add expand/collapse buttons
//        verticalSplit.setContinuousLayout(true); // Smooth resizing
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
        actionButton.addActionListener(_ -> showAddEntryDialog());
        removeButton.addActionListener(_ -> removeSelectedAddress());
        pollButton.addActionListener(_ -> pollMessages());
        clearButton.addActionListener(_ -> clearMessages());
        removeMessageButton.addActionListener(_ -> removeSelectedMail());

        var mouseAdapter = new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                int col = table.columnAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1 && table.getSelectedColumn() != -1 && row != -1 && col != -1) {
                    int modelRow = table.convertRowIndexToModel(row);
                    int modelCol = table.convertColumnIndexToModel(col);
                    String val = (String) table.getModel().getValueAt(modelRow, modelCol);
                    String dispVal = val.replaceAll("\n", " ");
                    if (dispVal.length() > 50) {
                        dispVal = dispVal.substring(0, 47) + "...";
                    }
                    Toolkit.getDefaultToolkit()
                            .getSystemClipboard()
                            .setContents(new StringSelection(val), null);
                    JOptionPane.showConfirmDialog(
                            table,
                            "'" + dispVal + "' copied to clipboard",
                            "Value copied",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        };

        addressTable.addMouseListener(mouseAdapter);
        messagesTable.addMouseListener(mouseAdapter);
    }

    private void clearMessages() {
        this.mailBox.clearMails();
        this.clearMessagesTable();
    }

    private void removeSelectedMail() {
        int selectedRow = messagesTable.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }
        // Confirm deletion
        var model = (DefaultTableModel) messagesTable.getModel();
        var subject = model.getValueAt(selectedRow, 2);

        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to remove this entry?\n'" + subject + "'",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            this.mailBox.removeMailAt(selectedRow);
            model.removeRow(selectedRow);

            // Select next row if available
            int rowCount = model.getRowCount();
            if (rowCount > 0) {
                int newSelection = Math.min(selectedRow, rowCount - 1);
                messagesTable.setRowSelectionInterval(newSelection, newSelection);
            }
        }
    }

    private void pollMessages() {
        this.mailBox.pollInteractions().forEach(this::addMessagesTableRow);
    }

    private void removeSelectedAddress() {
        int selectedRow = addressTable.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }
        // Confirm deletion
        DefaultTableModel model = (DefaultTableModel) addressTable.getModel();
        String addr = (String) model.getValueAt(selectedRow, 0);

        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to remove this entry?\n" + addr,
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            this.mailBox.removeAddress(addr);
            model.removeRow(selectedRow);

            // Select next row if available
            int rowCount = model.getRowCount();
            if (rowCount > 0) {
                int newSelection = Math.min(selectedRow, rowCount - 1);
                addressTable.setRowSelectionInterval(newSelection, newSelection);
            }
        }
    }

    private void setupLeftTableColumnWidths() {
        final int max = 200;
        final int min = 150;
        // Set initial widths for From and To columns (200px each), but allow resizing
        TableColumn fromColumn = messagesTable.getColumnModel().getColumn(0);
        fromColumn.setPreferredWidth(max); // Initial width
        fromColumn.setMinWidth(min); // Minimum width to prevent too small
        // No max width set - allows user to resize wider than 200px

        TableColumn toColumn = messagesTable.getColumnModel().getColumn(1);
        toColumn.setPreferredWidth(max); // Initial width
        toColumn.setMinWidth(min); // Minimum width to prevent too small
        // No max width set - allows user to resize wider than 200px

        // Title column will take remaining space
        TableColumn titleColumn = messagesTable.getColumnModel().getColumn(2);
        titleColumn.setPreferredWidth(max); // Initial preferred width
        titleColumn.setMinWidth(min); // Minimum width
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
                addAddressTableRow(this.mailBox.generate(username));
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

    private void addAddressTableRow(String address) {
        DefaultTableModel model = (DefaultTableModel) addressTable.getModel();
        model.addRow(new Object[]{address});
    }

    private void addMessagesTableRow(Mail mail) {
        DefaultTableModel model = (DefaultTableModel) messagesTable.getModel();
        model.addRow(new Object[]{mail.from(), mail.to(), mail.subject(), mail.plainContent()});
    }

    private void clearMessagesTable() {
        ((DefaultTableModel) messagesTable.getModel()).setRowCount(0);
    }
}
