package io.searabbitx.ui.table;

import java.util.function.Consumer;

public class AddressTable extends BaseTable {
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
    protected void onEntryRemoval(RowIndex ri) {
        entryRemovalCallback.accept(ri.selectedRow());
    }
}
