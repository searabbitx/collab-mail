package io.searabbitx.ui.pane;

import javax.swing.*;
import java.awt.*;

public class PollButtonPane {
    private final JButton pollButton;
    private final JPanel component;

    public PollButtonPane() {
        component = new JPanel();
        pollButton = new JButton("Poll now");
        component.add(pollButton);

        setupLayout();
    }

    public void onPollButtonPressed(Runnable callback) {
        pollButton.addActionListener(_ -> callback.run());
    }

    public Component component() {
        return component;
    }

    private void setupLayout() {
        component.setLayout(new BoxLayout(component, BoxLayout.X_AXIS));
        component.setPreferredSize(new Dimension(0, 30));
        component.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        pollButton.setMaximumSize(new Dimension(100, 30));
        pollButton.setSize(new Dimension(100, 30));
    }
}
