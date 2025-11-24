package io.searabbitx.ui.table;

import io.searabbitx.mail.Address;
import io.searabbitx.mail.AddressV1;

import java.util.Arrays;
import java.util.Optional;

class AddressTableModel extends NonEditableModel {
    private static final String[] COLS = {"Address", "Note"};

    @Override
    protected String[] cols() {
        return COLS;
    }

    String getAddressStringAt(int row) {
        return (String) getValueAt(row, Arrays.asList(COLS).indexOf("Address"));
    }

    void addRow(Address address) {
        addRow(new Object[]{address.toString(), address.note()});
    }

    public Address getAddressAt(int row) {
        var add = getAddressStringAt(row);
        var ind = add.indexOf('@');
        var uname = add.substring(0, ind);
        var dom = add.substring(ind + 1);

        var note = (String) getValueAt(row, Arrays.asList(COLS).indexOf("Note"));
        return new AddressV1(uname, dom, note);
    }

    public void updateNoteAt(RowIndex ri, String n) {
        setValueAt((Object) n, ri.selectedRow(), Arrays.asList(COLS).indexOf("Note"));
    }
}
