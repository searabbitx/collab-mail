package io.searabbitx.ui.table;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Optional;

import static io.searabbitx.ui.dialog.DialogUtils.yesNoDialog;

abstract class BaseTable extends JTable {
    BaseTable() {
        setFillsViewportHeight(true);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        addMouseListener(new CopyTableCellMouseAdapter());
    }

    public void removeSelectedEntry() {
        selectedRowIndex().ifPresent(ri -> {
            yesNoDialog("Are you sure you want to remove this entry?\n" + rowStringRepresentation(ri), "Confirm Removal", () -> {
                this.onEntryRemoval(ri);
                ((DefaultTableModel) getModel()).removeRow(ri.modelRow());
                selectNextRowIfAvailable(ri.selectedRow());
            }, this);
        });
    }

    private void selectNextRowIfAvailable(int selectedRow) {
        int rowCount = getModel().getRowCount();
        if (rowCount > 0) {
            int newSelection = Math.min(selectedRow, rowCount - 1);
            setRowSelectionInterval(newSelection, newSelection);
        }
    }

    protected abstract String rowStringRepresentation(RowIndex ri);

    protected abstract void onEntryRemoval(RowIndex rowIndex);

    public Optional<RowIndex> selectedRowIndex() {
        int selectedRow = getSelectedRow();
        return selectedRow >= 0 ? Optional.of(new RowIndex(selectedRow, convertRowIndexToModel(selectedRow))) : Optional.empty();
    }
}
