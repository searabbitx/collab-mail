package io.searabbitx.ui.table;

import io.searabbitx.mail.Mail;

import java.util.Arrays;

class MessagesTableModel extends NonEditableModel {
    private static final String[] COLS = {"From", "To", "Subject", "Body"};

    @Override
    protected String[] cols() {
        return COLS;
    }

    String getSubjectAt(int row) {
        return (String) getValueAt(row, Arrays.asList(COLS).indexOf("Subject"));
    }

    void addRow(Mail mail) {
        addRow(new Object[]{mail.from(), mail.to(), mail.subject(), mail.plainContent()});
    }

    Mail getMailAt(int row) {
        return new Mail(
                (String) getValueAt(row, 0),
                (String) getValueAt(row, 1),
                (String) getValueAt(row, 2),
                (String) getValueAt(row, 3)
        );
    }
}
