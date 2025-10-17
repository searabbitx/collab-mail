package io.searabbitx.ui;

import javax.swing.table.DefaultTableModel;

abstract class NonEditableModel extends DefaultTableModel {
    NonEditableModel() {
        super();
        setColumnIdentifiers(cols());
        setRowCount(0);
    }

    abstract String[] cols();

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    void clear() {
        setRowCount(0);
    }
}
