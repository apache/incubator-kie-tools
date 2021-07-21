/*
 * Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ait.lienzo.test.util;

import java.io.PrintStream;

/**
 * Logger class. Used only for the framework's itself debugging purposes.
 *
 * @author Roger Martinez
 * @since 1.0
 */
public class LienzoMockitoLogger {

    private static PrintStream stream;

    private static boolean enabled = false;

    public static void enable(final PrintStream stream) {
        LienzoMockitoLogger.stream = stream;

        LienzoMockitoLogger.enabled = true;
    }

    public static void disable() {
        LienzoMockitoLogger.enabled = false;
    }

    public static void log(final String context, final String message) {
        if (enabled) {
            LienzoMockitoLogger.stream.println("[" + context + "] " + message);
        }
    }
}
