/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.backend.server;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.mvp.Command;

/**
 * It captures the webapp startup/destroy events and notifies the interested parties.
 */
@WebListener
public class WebAppListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(WebAppListener.class);

    private static List<Command> onStartupCommandList = new ArrayList<>();
    private static List<Command> onDestroyCommandList = new ArrayList<>();
    private static boolean initialized = false;
    private static boolean destroyed = false;

    public synchronized static void registerOnStartupCommand(Command command) {
        if (initialized) {
            command.execute();
        } else {
            onStartupCommandList.add(command);
        }
    }

    public synchronized static void registerOnDestroyCommand(Command command) {
        if (destroyed) {
            command.execute();
        } else {
            onDestroyCommandList.add(command);
        }
    }

    private synchronized static void setRootDir(String rootDir) {
        initialized = true;
        WebAppSettings.get().setRootDir(rootDir);
        logger.info("Root directory = " + rootDir);
        notifyCallbacks(onStartupCommandList);
    }

    private synchronized static void resetRootDir() {
        destroyed = true;
        WebAppSettings.get().setRootDir(null);
        notifyCallbacks(onDestroyCommandList);
    }

    private static void notifyCallbacks(List<Command> commandList) {
        for (Command command : commandList) {
            command.execute();
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        String rootDir = servletContext.getRealPath("/");
        setRootDir(rootDir);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        resetRootDir();
    }
}