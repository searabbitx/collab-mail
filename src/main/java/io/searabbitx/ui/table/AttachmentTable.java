package io.searabbitx.ui.table;

import io.searabbitx.mail.Attachment;

import java.util.List;
import java.util.Optional;

public class AttachmentTable extends BaseTable {
    private final AttachmentTableModel model;
    private List<? extends Attachment> attachments;

    public AttachmentTable() {
        super();
        model = new AttachmentTableModel();
        attachments = List.of();
        setModel(model);
    }

    @Override
    protected String rowStringRepresentation(RowIndex ri) {
        return attachments.get(ri.modelRow()).name();
    }

    @Override
    protected void onEntryRemoval(RowIndex ri) {
    }

    public Optional<? extends Attachment> selectedAttachment() {
        return selectedRowIndex().map(i -> attachments.get(i.modelRow()));
    }

    public void setAttachments(List<? extends Attachment> attachments) {
        this.attachments = attachments;
        model.clear();
        attachments.forEach(model::addRow);
    }
}
