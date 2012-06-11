/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.util;

import org.drools.guvnor.shared.common.LogEntry;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Collects messages for displaying in the GUI as well as providing a logger.
 */
public class LoggingHelper {
    private final org.slf4j.Logger log;

    private static final MessageList messages = new MessageList();

    public static LogEntry[] getMessages() {
        return messages.getMessages();
    }

    public static void cleanLog() {
        messages.cleanEntry();
    }

    public static LoggingHelper getLogger(Class<?> cls) {
        return new LoggingHelper(cls);
    }

    private LoggingHelper(Class<?> cls) {
        log = LoggerFactory.getLogger(cls);
    }

    public void info(String message) {
        log.info(message);
        messages.add(message,
                1);
    }

    public void info(String message,
                     Throwable error) {
        log.info(message,
                error);
        messages.add(message + " " + error.getMessage(),
                1);
    }

    public void debug(String message) {
        log.debug(message);
    }

    public void error(String message) {
        log.error(message);
        messages.add(message,
                0);
    }

    public void error(String message,
                      Throwable error) {
        log.error(message,
                error);
        messages.add(message + " " + error.getMessage(),
                0);
    }

    public void warn(String message) {
        log.warn(message);
    }

    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

}

class MessageList {
    private static final int MAX = 500;
    private LogEntry[] messages = new LogEntry[MAX];
    private int current = 0;

    public MessageList() {

    }

    public synchronized void add(String message,
                                 int severity) {
        LogEntry entry = new LogEntry();
        entry.message = message;
        entry.timestamp = new Date();
        entry.severity = severity;

        if (current == MAX) {
            current = 0;
        }
        messages[current++] = entry;
    }

    public LogEntry[] getMessages() {
        //JDK1.5 Incompatible. 
        //return Arrays.copyOf( messages, current );
        LogEntry[] result = new LogEntry[current];
        System.arraycopy(messages,
                0,
                result,
                0,
                Math.min(messages.length,
                        current));
        return result;
    }

    public synchronized void cleanEntry() {
        messages = new LogEntry[MAX];
        current = 0;
    }
}
