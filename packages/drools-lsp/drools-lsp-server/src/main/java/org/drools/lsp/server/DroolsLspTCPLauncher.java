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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;

/**
 * TCP Launcher for hello language server. Usefull for remote connection and debug
 */
public class DroolsLspTCPLauncher {

    private static final int PORT = 9925;
    private static final Logger logger = Logger.getLogger(DroolsLspTCPLauncher.class.getSimpleName());

    public static void main(String[] args) throws Exception {
        try(ServerSocket serverSocket  = new ServerSocket(PORT)) {
            logger.info("The chat server is running on PORT " + PORT + ": " + serverSocket);
            logger.info("wait for clients to connect");
            Socket socket = serverSocket.accept();
            logger.info("Connected {}" + socket);
            startServer(socket.getInputStream(), socket.getOutputStream());
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start the language server.
     * @param in System Standard input stream
     * @param out System standard output stream
     * @throws ExecutionException Unable to start the server
     * @throws InterruptedException Unable to start the server
     */
    private static void startServer(InputStream in, OutputStream out) throws ExecutionException, InterruptedException {
        // Initialize the server
        DroolsLspServer server = new DroolsLspServer();
        // Create JSON RPC launcher for HelloLanguageServer instance.
        Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(server, in, out);

        // Connect the server
        server.connect(launcher.getRemoteProxy());

        // Start the listener for JsonRPC
        Future<?> startListening = launcher.startListening();

        // Get the computed result from LS.
        startListening.get();
    }
}
