package io.searabbitx.ui.pane;

import javax.swing.*;
import java.awt.*;

public class PollButtonPane extends JPanel {
    private final JButton pollButton;

    public PollButtonPane() {
        super();
        pollButton = new JButton("Poll now");
        add(pollButton);

        setupLayout();
    }

    private void setupLayout() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setPreferredSize(new Dimension(0, 30));
        setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        pollButton.setMaximumSize(new Dimension(100, 30));
        pollButton.setSize(new Dimension(100, 30));
    }

    public void onPollButtonPressed(Runnable callback) {
        pollButton.addActionListener(_ -> callback.run());
    }
}
