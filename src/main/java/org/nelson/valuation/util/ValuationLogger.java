package org.nelson.valuation.util;

import java.time.LocalDateTime;

public class ValuationLogger {

    private ValuationLogger() {
    }

    public static void info(String message, Object... args) {
        log(" [INFO] ", message, args);
    }

//    public static void error(String message, Object... args) {
//        log(" [ERROR] ", message, args);
//    }

    private static void log(String logLevel, String message, Object... args) {
        final String logMsg = LocalDateTime.now() + logLevel + "(" + Thread.currentThread().getName() + ") " + message;
        System.out.println(String.format(logMsg, args));
    }

}
