package io.searabbitx.util;

import burp.api.montoya.logging.Logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

public class Logger {
    public static Logging logging;

    public static void log(String msg) {
        logging.logToOutput("[" + LocalDateTime.now() + "] " + msg);
    }

    public static void error(String msg) {
        logging.logToError("[" + LocalDateTime.now() + "] " + msg);
    }

    public static void exception(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        error("Exception: " + e.getClass() + "\nStacktrace: " + sw);
    }
}
