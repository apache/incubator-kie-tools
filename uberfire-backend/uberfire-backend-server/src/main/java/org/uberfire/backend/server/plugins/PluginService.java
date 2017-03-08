/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.backend.server.plugins;

import java.io.File;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.server.plugins.engine.PluginManager;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;

/**
 * Provides an Errai RPC endpoint to serve JavaScript runtime plugins (authored
 * in plain JS) and Perspective Layouts (authored in plain JSON via Layout Editor
 * to the client where the corresponding scripts get injected into
 * the DOM. These plugins contain logic to programmatically register themselves
 * with UberFire.
 * It also initializes the {@link PluginManager} which
 * is responsible for loading plugins authored in GWT/Errai/UberFire.
 * These plugin scripts are injected into the host page by Errai once they are
 * registered with Errai's script registry or injected in runtime (when they
 * use Uberfire JS API).
 * Scripts injected via Errai's script registry do not need any programmatic
 * registration logic on the client as all contained managed beans (i.e.
 * perspectives, editors, screens) are automatically discovered and activated by
 * Errai IOC.
 */
@Service
@ApplicationScoped
@Startup(StartupType.BOOTSTRAP)
public class PluginService {

    private static PluginService instance;

    private PluginManager pluginManager;

    public PluginService() {
    }

    @Inject
    public PluginService(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public static PluginService getInstance() {
        if (instance == null) {
            throw new IllegalStateException(PluginService.class.getName() + " was not initialized on startup");
        }
        return instance;
    }

    @PostConstruct
    private void startUp() {
        instance = this;
    }

    public void init(final ServletContext servletContext) {
        final String contextRootDir = getContextRootDir(servletContext);
        final String pluginDir = getPlugins(servletContext,
                                            "plugins");
        if (contextRootDir != null && pluginDir != null) {
            pluginManager.init(contextRootDir,
                               pluginDir);
        }
    }

    String getPlugins(ServletContext servletContext,
                      String plugins) {
        return PluginUtils.getRealPath(servletContext,
                                       plugins);
    }

    String getContextRootDir(ServletContext servletContext) {
        return getPlugins(servletContext,
                          File.separator);
    }
}

