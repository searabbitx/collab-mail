package io.searabbitx.ui.table;

import io.searabbitx.mail.Address;

import java.util.Arrays;

class AddressTableModel extends NonEditableModel {
    private static final String[] COLS = {"Address", "Note"};

    @Override
    protected String[] cols() {
        return COLS;
    }

    String getAddressAt(int row) {
        return (String) getValueAt(row, Arrays.asList(COLS).indexOf("Address"));
    }

    void addRow(Address address) {
        addRow(new Object[]{address.toString(), address.note()});
    }
}
