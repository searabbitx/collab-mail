package io.searabbitx.ui.table;

import io.searabbitx.mail.Mail;

import java.util.function.Consumer;

import static io.searabbitx.ui.dialog.DialogUtils.yesNoDialog;

public class MessagesTable extends RemovableEntriesTable {
    private final MessagesTableModel model;
    private final Consumer<Integer> entryRemovalCallback;
    private final Runnable clearCallback;

    public MessagesTable(Consumer<Integer> entryRemovalCallback, Runnable clearCallback) {
        super();
        this.entryRemovalCallback = entryRemovalCallback;
        this.clearCallback = clearCallback;
        model = new MessagesTableModel();
        setModel(model);
    }

    public void clearMessages() {
        yesNoDialog(
                "Do you want to remove all messages?",
                "Confirm Messages Removal",
                () -> {
                    this.clearCallback.run();
                    model.clear();
                },
                this
        );
    }

    public void addRow(Mail mail) {
        model.addRow(mail);
    }

    @Override
    protected String rowStringRepresentation(int selectedRow) {
        return model.getSubjectAt(selectedRow);
    }

    @Override
    protected void onEntryRemoval(int selectedRow) {
        entryRemovalCallback.accept(selectedRow);
    }
}
