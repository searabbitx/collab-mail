package io.searabbitx.ui.table;

import io.searabbitx.mail.Mail;

import javax.swing.*;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static io.searabbitx.ui.dialog.DialogUtils.yesNoDialog;

public class MessagesTable extends BaseTable {
    private final MessagesTableModel model;
    private final Consumer<Integer> entryRemovalCallback;
    private final Runnable clearCallback;

    public MessagesTable(Consumer<Integer> entryRemovalCallback, Runnable clearCallback) {
        super();
        this.entryRemovalCallback = entryRemovalCallback;
        this.clearCallback = clearCallback;
        model = new MessagesTableModel();
        setModel(model);
        setAutoCreateRowSorter(true);

        getRowSorter().setSortKeys(
                List.of(new RowSorter.SortKey(0, SortOrder.DESCENDING))
        );
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

    public Optional<Integer> selectedRowModelIndex() {
        return selectedRowIndex().map(RowIndex::modelRow);
    }

    @Override
    protected String rowStringRepresentation(RowIndex ri) {
        return model.getSubjectAt(ri.modelRow());
    }

    @Override
    protected void onEntryRemoval(RowIndex ri) {
        entryRemovalCallback.accept(ri.modelRow());
    }
}
