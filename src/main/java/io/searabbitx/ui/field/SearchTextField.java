package io.searabbitx.ui.field;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.function.Consumer;

public class SearchTextField extends JTextField {
    public SearchTextField(Consumer<String> onInputChanged) {
        setText("Search...");
        addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                setText("");
            }

            public void focusLost(FocusEvent e) {
                if (getText().isEmpty()) {
                    setText("Search...");
                }
            }
        });
        setColumns(50);
        addActionListener(_ -> onInputChanged.accept(getText()));
    }
}
