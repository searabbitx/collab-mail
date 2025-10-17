package io.searabbitx.ui;

import java.util.Arrays;

class AddressTableModel extends NonEditableModel {
    private static final String[] COLS = {"Address"};

    @Override
    protected String[] cols() {
        return COLS;
    }

    String getAddressAt(int row) {
        return (String) getValueAt(row, Arrays.asList(COLS).indexOf("Address"));
    }

    void addRow(String address) {
        addRow(new Object[]{address});
    }
}
