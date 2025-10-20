package io.searabbitx.ui;

import burp.api.montoya.ui.UserInterface;
import io.searabbitx.mail.MailBox;
import io.searabbitx.ui.pane.AddressPane;
import io.searabbitx.ui.pane.DetailsPane;
import io.searabbitx.ui.pane.MessagesPane;
import io.searabbitx.ui.pane.PollButtonPane;

import javax.swing.*;
import java.awt.*;

public class Tab extends JPanel {
    private static final int POLLING_PERIOD_SECONDS = 5;

    private final PollButtonPane pollButtonPane;
    private final AddressPane addressPane;
    private final MessagesPane messagesPane;
    private final DetailsPane detailsPane;

    public Tab(MailBox mailBox, UserInterface ui) {
        pollButtonPane = new PollButtonPane();
        addressPane = new AddressPane(mailBox);
        messagesPane = new MessagesPane(mailBox);
        detailsPane = new DetailsPane(ui);

        messagesPane.addMessageConsumer(detailsPane::update);

        pollButtonPane.onPollButtonPressed(messagesPane::pollMessages);

        restoreValues();
        setupPeriodicTasks();
        setupLayout();
    }

    private void restoreValues() {
        addressPane.restoreValues();
        messagesPane.restoreValues();
    }

    private void setupPeriodicTasks() {
        var timer = new Timer(POLLING_PERIOD_SECONDS * 1000, _ -> messagesPane.pollMessages());
        timer.setRepeats(true);
        timer.start();
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        var gbc = new GridBagConstraints();

        var addrAndMsg = new JSplitPane(JSplitPane.VERTICAL_SPLIT, addressPane.component(), messagesPane.component());
        addrAndMsg.setResizeWeight(0.10);
        addrAndMsg.setOneTouchExpandable(true);
        addrAndMsg.setContinuousLayout(true);
        addrAndMsg.setDividerSize(8);

        var addrMsgAndDetails = new JSplitPane(JSplitPane.VERTICAL_SPLIT, addrAndMsg, detailsPane.component());
        addrMsgAndDetails.setResizeWeight(0.50);
        addrMsgAndDetails.setOneTouchExpandable(true);
        addrMsgAndDetails.setContinuousLayout(true);
        addrMsgAndDetails.setDividerSize(8);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);

        var verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pollButtonPane.component(), addrMsgAndDetails);
        verticalSplit.setResizeWeight(0);
        verticalSplit.setDividerSize(8);

        add(verticalSplit, gbc);
    }

}
