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

package org.uberfire.backend.server.plugins.engine;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.util.URIUtil;

/**
 * Manages Uberfire Plugins which involves monitoring the plugins directory
 * (see {@link PluginWatcher}), and loading the deployed plugins (see
 * {@link PluginJarProcessor}).
 */
@Dependent
public class PluginManager {

    private static final Logger LOG = LoggerFactory.getLogger(PluginManager.class);

    private PluginWatcher pluginWatcher;
    private PluginJarProcessor pluginJarProcessor;
    private ExecutorService executor;

    @Inject
    public PluginManager(final PluginWatcher pluginWatcher,
                         final PluginJarProcessor pluginJarProcessor) {

        this.pluginWatcher = pluginWatcher;
        this.pluginJarProcessor = pluginJarProcessor;
        this.executor = Executors.newSingleThreadExecutor();
    }

    @PreDestroy
    void shutDown() {
        pluginWatcher.stop();
    }

    /**
     * Initializes the {@link PluginJarProcessor} and
     * {@link PluginWatcher} based on the provided parameters.
     * @param contextRootDir the web application's context root directory, must not be
     * null.
     * @param pluginDir the plugin directory, must not be null.
     */
    public void init(final String contextRootDir,
                     final String pluginDir) {
        try {
            pluginJarProcessor.init(pluginDir,
                                    findPluginDeploymentDir(contextRootDir));

            pluginWatcher.start(pluginDir,
                                executor,
                                pluginJarProcessor);
        } catch (Exception e) {
            LOG.error("Failed to initialize " + PluginManager.class.getName(),
                      e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Finds the directory to deploy plugin contents to.
     * @param contextRootDir the web application's context root directory, must not be
     * null.
     * @return the directory hosting the plugin's JS files or the provided
     * context root directory if not found.
     */
    String findPluginDeploymentDir(String contextRootDir) throws IOException {
        final Collection<File> gwtFiles = FileUtils.listFiles(new File(encodePath(contextRootDir)),
                                                              new String[]{"nocache.js"},
                                                              true);
        if (!gwtFiles.isEmpty()) {
            final File gwtFile = gwtFiles.iterator().next();
            return gwtFile.getParentFile().getCanonicalPath();
        }
        return new File(contextRootDir).getCanonicalPath();
    }

    String encodePath(String contextRootDir) {
        return URIUtil.decode(contextRootDir);
    }
}