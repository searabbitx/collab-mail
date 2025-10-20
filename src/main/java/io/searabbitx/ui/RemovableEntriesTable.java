package io.searabbitx.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import static io.searabbitx.ui.dialog.DialogUtils.yesNoDialog;

abstract class RemovableEntriesTable extends JTable {
    RemovableEntriesTable() {
        setFillsViewportHeight(true);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        addMouseListener(new CopyTableCellMouseAdapter());
    }

    public void removeSelectedEntry() {
        int selectedRow = getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        yesNoDialog(
                "Are you sure you want to remove this entry?\n" + rowStringRepresentation(selectedRow),
                "Confirm Removal",
                () -> {
                    this.onEntryRemoval(selectedRow);
                    ((DefaultTableModel) getModel()).removeRow(selectedRow);
                    selectNextRowIfAvailable(selectedRow);
                },
                this
        );
    }

    private void selectNextRowIfAvailable(int selectedRow) {
        int rowCount = getModel().getRowCount();
        if (rowCount > 0) {
            int newSelection = Math.min(selectedRow, rowCount - 1);
            setRowSelectionInterval(newSelection, newSelection);
        }
    }

    protected abstract String rowStringRepresentation(int selectedRow);

    protected abstract void onEntryRemoval(int selectedRow);
}
