package io.searabbitx.ui.table;

import io.searabbitx.mail.Address;

import java.awt.event.MouseEvent;
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

    public void addRow(Address add) {
        model.addRow(add);
    }

    @Override
    protected String rowStringRepresentation(RowIndex ri) {
        return model.getAddressAt(ri.modelRow());
    }

    @Override
    protected void onEntryRemoval(RowIndex ri) {
        entryRemovalCallback.accept(ri.modelRow());
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        var p = e.getPoint();
        int rowIndex = rowAtPoint(p);
        int colIndex = columnAtPoint(p);

        try {
            return getValueAt(rowIndex, colIndex).toString();
        } catch (RuntimeException _) {
            return null;
        }
    }
}
