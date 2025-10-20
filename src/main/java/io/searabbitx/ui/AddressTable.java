package io.searabbitx.ui;

import java.util.function.Consumer;

public class AddressTable extends RemovableEntriesTable {
    private final AddressTableModel model;
    private final Consumer<Integer> entryRemovalCallback;

    public AddressTable(Consumer<Integer> entryRemovalCallback) {
        super();
        this.entryRemovalCallback = entryRemovalCallback;
        model = new AddressTableModel();
        setModel(model);
    }

    public void addRow(String add) {
        model.addRow(add);
    }

    @Override
    protected String rowStringRepresentation(int selectedRow) {
        return model.getAddressAt(selectedRow);
    }

    @Override
    protected void onEntryRemoval(int selectedRow) {
        entryRemovalCallback.accept(selectedRow);
    }
}
