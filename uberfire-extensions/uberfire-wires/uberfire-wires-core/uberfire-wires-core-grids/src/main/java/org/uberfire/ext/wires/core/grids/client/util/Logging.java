/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.core.grids.client.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Logging {

    private Logging() {
        //Sonar rule: Utility classes should not have public constructors
    }

    /**
     * Logs a message at {@see Level.FINEST}
     * @param logger Logger to record the entry.
     * @param message Message to log.
     * @return Returns the current (system) time in milliseconds.
     */
    public static long log(final Logger logger,
                           final String message) {
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, message);
        }
        return System.currentTimeMillis();
    }

    /**
     * Logs a message at {@see Level.FINEST} including the elapsed time between the
     * previousTimeMillis and the current (system) time in milliseconds.
     * @param logger Logger to record the entry.
     * @param message Message to log.
     * @param previousTimeMillis Previous time in milliseconds.
     * @return Returns the current (system) time in milliseconds.
     */
    public static long log(final Logger logger,
                           final String message,
                           final long previousTimeMillis) {
        final long currentTimeMillis = System.currentTimeMillis();
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, message + " - " + (currentTimeMillis - previousTimeMillis) + "ms");
        }
        return currentTimeMillis;
    }
}
