package io.searabbitx.ui;

import burp.api.montoya.ui.UserInterface;
import io.searabbitx.mail.MailBox;

import javax.swing.*;
import java.awt.*;

public class Tab extends JPanel {

    private static final Integer MAIN_LAYER = 0;
    private static final Integer LOADING_LAYER = 1;

    private final JLayeredPane layeredPane;
    private final MainPanel mainTab;
    private final LoadingPanel loadingTab;

    public Tab(MailBox mailBox, UserInterface ui) {
        this.mainTab = new MainPanel(mailBox, ui);
        this.loadingTab = new LoadingPanel();

        setLayout(new BorderLayout());

        layeredPane = new JLayeredPane();
        add(layeredPane, BorderLayout.CENTER);

        setupPanels();
    }

    public void showMainScreen() {
        loadingTab.setVisible(false);
    }

    public void showLoadingScreen() {
        loadingTab.setVisible(true);
        layeredPane.moveToFront(loadingTab);
    }

    private void setupPanels() {
        handleResizing();
        layeredPane.add(mainTab, MAIN_LAYER);
        layeredPane.add(loadingTab, LOADING_LAYER);

        // Make loading tab semi-transparent
        loadingTab.setOpaque(false);
        // Initially hide the loading tab
        loadingTab.setVisible(false);
    }

    private void handleResizing() {
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                resizePanels();
            }
        });
    }

    private void resizePanels() {
        Dimension size = layeredPane.getSize();
        mainTab.setBounds(0, 0, size.width, size.height);
        loadingTab.setBounds(0, 0, size.width, size.height);
    }
}
