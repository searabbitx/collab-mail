package io.searabbitx.util;

import burp.api.montoya.logging.Logging;

import java.time.LocalDateTime;

public class Logger {
    public static Logging logging;

    public static void log(String msg) {
        logging.logToOutput("[" + LocalDateTime.now() + "] " + msg);
    }

    static void error(String msg) {
        logging.logToError("[" + LocalDateTime.now() + "] " + msg);
    }
}
