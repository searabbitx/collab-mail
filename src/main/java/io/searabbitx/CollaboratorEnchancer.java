package io.searabbitx;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;

public class CollaboratorEnchancer implements BurpExtension {
    @Override
    public void initialize(MontoyaApi api) {
        api.extension().setName("Collaborator Enchancer");
        api.logging().logToOutput("Initializing collab enchancer");
        api.userInterface().registerSuiteTab("CollabEnchancer", new Tab(new MailBox(api)));

        Logger.logging = api.logging();
        Logger.log("Logger initialized");
    }
}