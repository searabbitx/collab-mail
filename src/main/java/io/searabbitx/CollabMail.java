package io.searabbitx;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;

public class CollabMail implements BurpExtension {
    @Override
    public void initialize(MontoyaApi api) {
        api.extension().setName("CollabMail");
        api.logging().logToOutput("Initializing collab mail");
        api.userInterface().registerSuiteTab("CollabMail", new Tab(new MailBox(api)));

        Logger.logging = api.logging();
        Logger.log("Logger initialized");
    }
}