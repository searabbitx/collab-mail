package io.searabbitx.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class CopyTableCellMouseAdapter extends MouseAdapter {
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
            JOptionPane.showConfirmDialog(
                    table,
                    "'" + dispVal + "' copied to clipboard",
                    "Value copied",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
