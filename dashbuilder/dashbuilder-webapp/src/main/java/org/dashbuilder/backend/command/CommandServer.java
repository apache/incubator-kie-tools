/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.backend.command;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.dashbuilder.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.Startup;

/**
 * This class receives string commands from a TCP socket and transform such commend into CDI event instances
 * to be consumed by server side components/services.
 */
@ApplicationScoped
@Startup
public class CommandServer implements Runnable {

    private static Logger log = LoggerFactory.getLogger(CommandServer.class);

    @Inject @Config("10000")
    private int portNumber;

    @Inject
    private Event<CommandEvent> commandEvent;

    private Thread serverSocketThread = new Thread(this);

    @PostConstruct
    private void init() {
        serverSocketThread.start();
    }

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            Socket clientSocket = serverSocket.accept();
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String commandStr;
            while ((commandStr = in.readLine()) != null) {
                commandEvent.fire(new CommandEvent(commandStr));
                out.println(">>> " + commandStr + " [OK]");
            }
        } catch (Exception e) {
            log.error("Command server error", e);
        }
    }
}
