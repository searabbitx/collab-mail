package io.searabbitx.ui;

import java.util.function.Consumer;

class AddressTable extends RemovableEntriesTable {
    private final AddressTableModel model;
    private final Consumer<Integer> entryRemovalCallback;

    AddressTable(Consumer<Integer> entryRemovalCallback) {
        super();
        this.entryRemovalCallback = entryRemovalCallback;
        model = new AddressTableModel();
    }

    void addRow(String add) {
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
