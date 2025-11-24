package io.searabbitx.ui.table;

import java.util.Arrays;

import io.searabbitx.mail.Address;

class AddressTableModel extends NonEditableModel {
    private static final String[] COLS = { "Address", "Note" };

    @Override
    protected String[] cols() {
        return COLS;
    }

    String getAddressStringAt(int row) {
        return (String) getValueAt(row, Arrays.asList(COLS).indexOf("Address"));
    }

    void addRow(Address address) {
        addRow(new Object[] { address.toString(), address.note() });
    }

    public Address getAddressAt(int row) {
        var fullAddress = getAddressStringAt(row);
        var note = (String) getValueAt(row, Arrays.asList(COLS).indexOf("Note"));
        return Address.fromFullAddr(fullAddress, note);
    }

    public void updateNoteAt(RowIndex ri, String n) {
        setValueAt((Object) n, ri.selectedRow(), Arrays.asList(COLS).indexOf("Note"));
    }
}
