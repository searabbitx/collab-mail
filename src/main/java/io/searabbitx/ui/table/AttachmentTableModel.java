package io.searabbitx.ui.table;

import io.searabbitx.mail.Attachment;
import io.searabbitx.mail.MailV1;

class AttachmentTableModel extends NonEditableModel {
    private static final String[] COLS = {"Name", "Type"};

    @Override
    protected String[] cols() {
        return COLS;
    }

    void addRow(Attachment a) {
        addRow(new Object[]{a.name(), a.contentType()});
    }
}
