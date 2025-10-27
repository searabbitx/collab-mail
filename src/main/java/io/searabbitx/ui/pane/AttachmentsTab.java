package io.searabbitx.ui.pane;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.editor.EditorOptions;
import io.searabbitx.mail.Attachment;
import io.searabbitx.mail.MailV1;
import io.searabbitx.ui.table.AttachmentTable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class AttachmentsTab {
    private final AttachmentTable table;
    private final UserInterface ui;
    private JSplitPane component;

    public AttachmentsTab(UserInterface ui) {
        this.ui = ui;
        table = new AttachmentTable();
        setupLayout();
    }

    public Component component() {
        return component;
    }

    public void show(List<? extends Attachment> as) {
        table.setAttachments(as);
    }

    private void setupLayout() {
        var buttonPanel = new JPanel(new GridBagLayout());
        var buttonGbc = new GridBagConstraints();
        buttonGbc.gridy = 0;
        buttonGbc.gridx = 0;
        buttonGbc.fill = GridBagConstraints.HORIZONTAL;
        buttonGbc.anchor = GridBagConstraints.NORTH;
        buttonGbc.weightx = 1.0;
        buttonGbc.weighty = 0;
        buttonGbc.insets = new Insets(5, 10, 5, 10);
        var viewButton = new JButton("View");
        viewButton.addActionListener(_ -> viewSelected());
        buttonPanel.add(viewButton, buttonGbc);
        buttonGbc.gridy = 1;

        var saveButton = new JButton("Save");
        saveButton.addActionListener(_ -> saveSelected());
        buttonPanel.add(saveButton, buttonGbc);

        buttonGbc.weighty = 2.0;
        buttonPanel.add(new JPanel(), buttonGbc);


        component = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buttonPanel, new JScrollPane(table));
        component.setResizeWeight(0.10);
        component.setOneTouchExpandable(true); // Add expand/collapse buttons
        component.setContinuousLayout(true); // Smooth resizing
        component.setDividerSize(8); // Set divider thickness
        component.setBorder(BorderFactory.createTitledBorder("Addresses"));
    }

    private void saveSelected() {
        table.selectedAttachment().ifPresent(this::saveAttachment);
    }

    private void saveAttachment(Attachment attachment) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Attachment");
        fileChooser.setSelectedFile(new File(attachment.name()));

        int userSelection = fileChooser.showSaveDialog(component);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            try (FileOutputStream fos = new FileOutputStream(fileToSave)) {
                byte[] content = attachment.content();
                if (content != null) {
                    fos.write(content);
                    fos.flush();

                    JOptionPane.showMessageDialog(component,
                            "Attachment saved successfully to:\n" + fileToSave.getAbsolutePath(),
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(component,
                            "No content to save",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(component,
                        "Error saving file: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewSelected() {
        table.selectedAttachment().ifPresent(this::viewAttachment);
    }

    private void viewAttachment(Attachment attachment) {
        var f = new JFrame(attachment.name());
        var editor = ui.createRawEditor(EditorOptions.READ_ONLY);
        editor.setContents(ByteArray.byteArray(attachment.content()));
        f.add(editor.uiComponent());
        f.setVisible(true);
    }
}
