package io.searabbitx.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class CopyTableCellMouseAdapter extends MouseAdapter {

    private static final int DIALOG_AUTOCLOSE_DELAY = 2000;

    private static void showAutoclosingDialog(String dispVal, JTable table) {
        var pane = new JOptionPane();
        pane.setMessage("'" + dispVal + "' copied to clipboard!");

        var dialog = pane.createDialog(table, "Copied!");

        var timer = new Timer(DIALOG_AUTOCLOSE_DELAY, _ -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();

        dialog.setVisible(true);
    }

    public void mousePressed(MouseEvent mouseEvent) {
        JTable table = (JTable) mouseEvent.getSource();
        Point point = mouseEvent.getPoint();
        int row = table.rowAtPoint(point);
        int col = table.columnAtPoint(point);
        if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1 && table.getSelectedColumn() != -1 && row != -1 && col != -1) {
            int modelRow = table.convertRowIndexToModel(row);
            int modelCol = table.convertColumnIndexToModel(col);
            String val = (String) table.getModel().getValueAt(modelRow, modelCol);
            String dispVal = val.replaceAll("\n", " ");
            if (dispVal.length() > 50) {
                dispVal = dispVal.substring(0, 47) + "...";
            }
            Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .setContents(new StringSelection(val), null);

            showAutoclosingDialog(dispVal, table);
        }
    }
}
