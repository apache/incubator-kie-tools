/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.backend.server.plugin;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.uberfire.backend.plugin.RuntimePluginService;

/**
 * Initializes the {@link RuntimePluginService} when the web application starts
 * up.
 */
@WebListener
public class PluginServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized( ServletContextEvent sce ) {
        RuntimePluginServiceImpl.getInstance().init( sce.getServletContext() );
    }

    @Override
    public void contextDestroyed( ServletContextEvent sce ) {

    }

}
