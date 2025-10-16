package io.searabbitx;

import burp.api.montoya.logging.Logging;

import java.time.LocalDateTime;

class Logger {
    static Logging logging;

    static void log(String msg) {
        logging.logToOutput("[" + LocalDateTime.now() + "] " + msg);
    }

    static void error(String msg) {
        logging.logToError("[" + LocalDateTime.now() + "] " + msg);
    }
}
