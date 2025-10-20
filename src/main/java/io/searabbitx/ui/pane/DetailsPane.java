package io.searabbitx.ui.pane;

import javax.swing.*;
import java.awt.*;

public class DetailsPane {
    public Component component() {
        var panel = new JPanel();
        panel.add(new JTextArea("Message details pane stub"));
        return panel;
    }
}
