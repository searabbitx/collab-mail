package io.searabbitx.ui.table;

import io.searabbitx.mail.Mail;

class AttachmentTableModel extends NonEditableModel {
    private static final String[] COLS = {"Name", "Type"};

    @Override
    protected String[] cols() {
        return COLS;
    }

    void addRow(Mail.Attachment a) {
        addRow(new Object[]{a.name(), a.contentType()});
    }
}
