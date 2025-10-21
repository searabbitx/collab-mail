package io.searabbitx.ui.table;

import io.searabbitx.mail.Mail;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

class MessagesTableModel extends NonEditableModel {
    private static final String[] COLS = {"Time", "From", "To", "Subject", "Body"};

    @Override
    protected String[] cols() {
        return COLS;
    }

    String getSubjectAt(int row) {
        return (String) getValueAt(row, Arrays.asList(COLS).indexOf("Subject"));
    }

    void addRow(Mail mail) {
        var time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(mail.time());
        addRow(new Object[]{time, mail.from(), mail.to(), mail.subject(), mail.plainContent()});
    }
}
