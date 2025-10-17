package io.searabbitx.ui.dialog;

import javax.swing.*;
import java.awt.*;

public class DialogUtils {
    public static void yesNoDialog(String message, String title, Runnable yesCallback, Component parent) {
        int result = JOptionPane.showConfirmDialog(
                parent,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            yesCallback.run();
        }
    }
}
