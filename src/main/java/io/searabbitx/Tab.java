package io.searabbitx;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Tab extends JPanel {
    private final MailBox addresses;

    private JTable leftTable;
    private JTable rightTable;
    private JButton actionButton;
    private JButton removeButton;
    private JButton pollButton;

    public Tab(MailBox addresses) {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        this.addresses = addresses;
    }

    private void initializeComponents() {
        // Initialize left table (larger table)
        String[] leftColumns = {"From", "To", "Title"};
        DefaultTableModel leftModel = new DefaultTableModel(leftColumns, 0);
        leftTable = new JTable(leftModel);
        leftTable.setFillsViewportHeight(true);
        leftTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add some sample data to left table
        //leftModel.addRow(new Object[]{"test@example.com", "foo@example.com", "hello"});
        //leftModel.addRow(new Object[]{"test2@example.com", "bar@example.com", "hi"});

        setupLeftTableColumnWidths();

        // Initialize right table (smaller table)
        String[] rightColumns = {"Address"};
        DefaultTableModel rightModel = new DefaultTableModel(rightColumns, 0);
        rightTable = new JTable(rightModel);
        rightTable.setFillsViewportHeight(true);
        rightTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Initialize button
        actionButton = new JButton("Add address");
        removeButton = new JButton("Remove Entry");
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Create left panel with scroll pane
        JScrollPane leftScrollPane = new JScrollPane(leftTable);
        leftScrollPane.setBorder(BorderFactory.createTitledBorder("Inbox"));
        leftScrollPane.setMinimumSize(new Dimension(300, 400));
        leftScrollPane.setPreferredSize(new Dimension(600, 400));

        // Create right panel with table and button
        JPanel rightPanel = new JPanel(new GridBagLayout());
        GridBagConstraints rightGbc = new GridBagConstraints();

        // Right table scroll pane
        JScrollPane rightScrollPane = new JScrollPane(rightTable);
        rightScrollPane.setBorder(BorderFactory.createTitledBorder("Addresses"));
        rightScrollPane.setMinimumSize(new Dimension(200, 300));
        rightScrollPane.setPreferredSize(new Dimension(250, 350));

        // Add right table to right panel
        rightGbc.gridx = 0;
        rightGbc.gridy = 0;
        rightGbc.gridwidth = 2;
        rightGbc.weightx = 1.0;
        rightGbc.weighty = 1.0;
        rightGbc.fill = GridBagConstraints.BOTH;
        rightGbc.insets = new Insets(5, 5, 5, 5);
        rightPanel.add(rightScrollPane, rightGbc);

        // Create button panel with both buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        buttonPanel.add(actionButton);
        buttonPanel.add(removeButton);

        // Add button panel to right panel
        rightGbc.gridy = 1;
        rightGbc.gridwidth = 2;
        rightGbc.weighty = 0.0;
        rightGbc.fill = GridBagConstraints.HORIZONTAL;
        rightGbc.anchor = GridBagConstraints.SOUTH;
        rightGbc.insets = new Insets(0, 5, 5, 5);
        rightPanel.add(buttonPanel, rightGbc);


        // Create resizable split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScrollPane, rightPanel);
        splitPane.setResizeWeight(0.75); // Initially give 75% to left pane
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
        pollButtonPane.setLayout(new BorderLayout());
        pollButtonPane.setPreferredSize(new Dimension(0, 30));
        pollButton = new JButton("Poll now!");
        pollButton.setMaximumSize(new Dimension(100, 30));
        pollButton.setSize(new Dimension(100, 30));
        pollButtonPane.add(pollButton, BorderLayout.WEST);

        var verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pollButtonPane, splitPane);
        verticalSplit.setResizeWeight(0);
        verticalSplit.setOneTouchExpandable(true); // Add expand/collapse buttons
        verticalSplit.setContinuousLayout(true); // Smooth resizing
        verticalSplit.setDividerSize(8); // Set divider thickness

        add(verticalSplit, gbc);
    }

    private void setupEventHandlers() {
        actionButton.addActionListener(_ -> showAddEntryDialog());
        removeButton.addActionListener(_ -> removeSelectedEntry());
        pollButton.addActionListener(_ -> pollMessages());
    }

    private void pollMessages() {
        this.addresses.pollInteractions().forEach(this::addLeftTableRow);
    }

    private void removeSelectedEntry() {
        int selectedRow = rightTable.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }
        // Confirm deletion
        DefaultTableModel model = (DefaultTableModel) rightTable.getModel();
        String addr = (String) model.getValueAt(selectedRow, 0);

        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to remove this entry?\n" + addr,
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            this.addresses.remove(addr);
            model.removeRow(selectedRow);

            // Select next row if available
            int rowCount = model.getRowCount();
            if (rowCount > 0) {
                int newSelection = Math.min(selectedRow, rowCount - 1);
                rightTable.setRowSelectionInterval(newSelection, newSelection);
            }
        }
    }

    private void setupLeftTableColumnWidths() {
        final int max = 200;
        final int min = 150;
        // Set initial widths for From and To columns (200px each), but allow resizing
        TableColumn fromColumn = leftTable.getColumnModel().getColumn(0);
        fromColumn.setPreferredWidth(max); // Initial width
        fromColumn.setMinWidth(min); // Minimum width to prevent too small
        // No max width set - allows user to resize wider than 200px

        TableColumn toColumn = leftTable.getColumnModel().getColumn(1);
        toColumn.setPreferredWidth(max); // Initial width
        toColumn.setMinWidth(min); // Minimum width to prevent too small
        // No max width set - allows user to resize wider than 200px

        // Title column will take remaining space
        TableColumn titleColumn = leftTable.getColumnModel().getColumn(2);
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
                addRightTableRow(new Object[]{this.addresses.generate(username)});
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

    public void addRightTableRow(Object[] rowData) {
        DefaultTableModel model = (DefaultTableModel) rightTable.getModel();
        model.addRow(rowData);
    }

    public void addLeftTableRow(Mail mail) {
        DefaultTableModel model = (DefaultTableModel) leftTable.getModel();
        model.addRow(new Object[]{mail.from(), mail.to(), mail.subject()});
    }

    private void clearLeftTable() {
        ((DefaultTableModel) leftTable.getModel()).setRowCount(0);
    }
}
