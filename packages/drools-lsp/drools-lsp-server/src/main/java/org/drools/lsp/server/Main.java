/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.lsp.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        configureLogging();
        startServer(System.in, System.out);
    }

    /**
     * Applies the log level from the {@code drools.lsp.logLevel} system property
     * (set via {@code -Ddrools.lsp.logLevel=FINE} in the JVM launch args).
     * Defaults to INFO when absent or invalid.
     */
    private static void configureLogging() {
        String levelName = System.getProperty("drools.lsp.logLevel", "INFO");
        try {
            Level level = Level.parse(levelName);
            Logger.getLogger("org.drools").setLevel(level);
            for (var handler : Logger.getLogger("").getHandlers()) {
                handler.setLevel(level);
            }
        } catch (IllegalArgumentException e) {
            // Leave the default level unchanged when the property value is invalid.
        }
    }

    public static void startServer(InputStream in, OutputStream out) throws InterruptedException, ExecutionException {
        DroolsLspServer server = new DroolsLspServer();
        Launcher<LanguageClient> l = LSPLauncher.createServerLauncher(server, in, out);
        Future<?> startListening = l.startListening();
        server.connect(l.getRemoteProxy());
        startListening.get();
    }

}

