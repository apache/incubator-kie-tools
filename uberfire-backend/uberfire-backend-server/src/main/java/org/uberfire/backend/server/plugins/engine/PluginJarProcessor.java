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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.plugin.PluginProcessor;
import org.uberfire.workbench.events.PluginReloadedEvent;

import static org.apache.commons.lang3.StringUtils.substringAfterLast;

/**
 * Processes and activates deployed plugin jars. The plugin jars can contain
 * GWT .js files, Uberfire Runtime Plugins and html templates.
 * The corresponding GWT .js files are registered with the {@link PluginProcessor}
 * and are added to the host page's &lt;head&gt; element by Errai,
 * so the plugin scripts execute before the web application's main script runs.
 * This is required to ensure plugins and their managed beans are discoverable by
 * Errai's bean manager when the main application bootstraps.
 * Uberfire Runtime Plugins are loaded after Errai and Uberfire bootstrap.
 */
@Dependent
public class PluginJarProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(PluginJarProcessor.class);

    private Instance<PluginProcessor> pluginProcessors;
    private String pluginsDir;
    private String pluginsDeploymentUrlPath;
    private String pluginsDeploymentDir;
    private Event<PluginReloadedEvent> pluginsReloadedEvent;

    @Inject
    public PluginJarProcessor(@Any Instance<PluginProcessor> pluginProcessors,
                              final Event<PluginReloadedEvent> pluginsReloadedEvent) {
        this.pluginProcessors = pluginProcessors;
        this.pluginsReloadedEvent = pluginsReloadedEvent;
    }

    /**
     * Processes and loads the currently deployed plugins.
     * @param pluginsDir the plugin directory, must not be null.
     * @param pluginsDeploymentDir the directory to deploy plugin contents to, must not be null.
     */
    public void init(final String pluginsDir,
                     final String pluginsDeploymentDir) throws IOException {

        this.pluginsDeploymentUrlPath = substringAfterLast(pluginsDeploymentDir,
                                                           File.separator);
        this.pluginsDeploymentDir = pluginsDeploymentDir;
        this.pluginsDir = pluginsDir;
        loadPlugins();
    }

    void loadPlugins() throws IOException {
        removeAllPlugins();

        final File pluginsRoot = new File(pluginsDir);
        if (pluginsRoot.exists()) {
            Collection<File> deployedPlugins = FileUtils.listFiles(pluginsRoot,
                                                                   new String[]{"jar"},
                                                                   false);

            deployedPlugins.forEach(p -> loadPlugins(Paths.get(p.getAbsolutePath()),
                                                     false));
        }
    }

    void removeAllPlugins() {
        pluginProcessors.forEach(p -> p.removeAll());
    }

    /**
     * Clears the plugins and reloads all currently deployed plugins.
     * Fires a {@link PluginReloadedEvent} when done.
     */
    public void reload() throws IOException {
        loadPlugins();
        pluginsReloadedEvent.fire(new PluginReloadedEvent());
    }

    /**
     * Unpacks the provided plugin (path pointing to a JAR file), searches for
     * the corresponding plugins files and process the plugin with the
     * corresponding {@link PluginProcessor}.
     * @param path path to a deployed jar file.
     * @param notifyClients true if clients should be notified (of added and updated
     * plugins) through CDI events, otherwise false.
     */
    public void loadPlugins(Path path,
                            boolean notifyClients) {
        final List<String> pluginsFiles = extractFilesFromPluginsJar(pluginsDir + File.separator + path.toFile().getName());

        if (!pluginsFiles.isEmpty()) {
            for (String pluginName : pluginsFiles) {
                for (PluginProcessor pluginRegistry : pluginProcessors) {
                    if (pluginRegistry.shouldProcess(pluginName)) {
                        pluginRegistry.process(pluginName,
                                               pluginsDeploymentDir,
                                               notifyClients);
                    }
                }
            }
        } else {
            LOG.warn("Deployed plugin " + path.toFile().getName() + " does not contain any plugins!");
        }
    }

    List<String> extractFilesFromPluginsJar(String jarFileName) {
        List<String> pluginsFiles = new ArrayList<>();

        try (JarFile jar = new JarFile(jarFileName)) {
            final Enumeration<?> enumEntries = jar.entries();
            while (enumEntries.hasMoreElements()) {
                final JarEntry file = (JarEntry) enumEntries.nextElement();
                String fileName = StringUtils.substringAfterLast(file.getName(),
                                                                 File.separator);

                if (PluginProcessor.isAValidPluginFileExtension(fileName)) {
                    final File f = new File(pluginsDeploymentDir + File.separator + fileName);
                    try (InputStream is = jar.getInputStream(file);
                         FileOutputStream fos = new FileOutputStream(f)) {

                        while (is.available() > 0) {
                            fos.write(is.read());
                        }
                    }

                    pluginsFiles.add(fileName);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return pluginsFiles;
    }
}