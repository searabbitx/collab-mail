package io.searabbitx;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import io.searabbitx.mail.MailBox;
import io.searabbitx.storage.Storage;
import io.searabbitx.ui.Tab;
import io.searabbitx.util.Logger;

public class CollabMail implements BurpExtension {
    private static final String VERSION = "0.1.5";

    @Override
    public void initialize(MontoyaApi api) {
        api.extension().setName("CollabMail");
        api.logging().logToOutput("Initializing collab mail");
        api.logging().logToOutput("Version: " + VERSION);

        api.userInterface().registerSuiteTab("CollabMail", initializeTab(api));

        Logger.logging = api.logging();
    }

    private Tab initializeTab(MontoyaApi api) {
        var storage = new Storage(api.persistence().extensionData(), api.collaborator());
        return new Tab(new MailBox(storage), api.userInterface());
    }
}