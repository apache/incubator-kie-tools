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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.plugin.RuntimePlugin;
import org.uberfire.backend.plugin.RuntimePluginProcessor;
import org.uberfire.backend.plugin.RuntimePluginService;
import org.uberfire.backend.server.plugins.processors.HTMLPluginProcessor;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.spaces.SpacesAPI;

@Service
@ApplicationScoped
@Startup(StartupType.BOOTSTRAP)
public class RuntimePluginServiceImpl implements RuntimePluginService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimePluginServiceImpl.class);

    @Inject
    @Any
    Instance<RuntimePluginProcessor> runtimePluginProcessors;

    @Inject
    HTMLPluginProcessor htmlPluginProcessor;

    @Inject
    SpacesAPI spaces;

    @Override
    public Collection<String> listFrameworksContent() {
        return directoryContent("frameworks",
                                "*.js");
    }

    @Override
    public Collection<String> listPluginsContent() {
        return directoryContent("plugins",
                                "*.js");
    }

    @Override
    public String getTemplateContent(String url) {

        String pluginTemplateContent = getRuntimePluginTemplateContent(url);
        if (isAJarPluginTemplate(pluginTemplateContent)) {
            return pluginTemplateContent;
        }

        String realPath = PluginUtils.getRealPath("plugins");
        if (realPath == null) {
            LOGGER.info("Not fetching template content for " + url + " because getRealPath() is"
                                + " returning null. (This app is probably deployed in an unexploded .war)");
            return "";
        }
        final Path template;
        if (url.startsWith("/")) {
            template = Paths.get(URI.create("file://" + realPath + url));
        } else {
            template = Paths.get(URI.create("file://" + realPath + "/" + url));
        }

        if (Files.isRegularFile(template)) {
            return new String(Files.readAllBytes(template));
        }
        return "";
    }

    private boolean isAJarPluginTemplate(String pluginContent) {
        return pluginContent != null && !pluginContent.isEmpty();
    }

    private Collection<String> directoryContent(final String directory,
                                                final String glob) {
        String realPath = PluginUtils.getRealPath(directory);
        if (realPath == null) {
            LOGGER.info("Not listing directory content for " + directory + "/" + glob +
                                " because getRealPath() is returning null. (This app is probably deployed in an unexploded .war)");
            return Collections.emptyList();
        }
        final Collection<String> result = new ArrayList<>();

        final Path pluginsRootPath = Paths.get(URI.create("file://" + realPath));

        if (Files.isDirectory(pluginsRootPath)) {
            final DirectoryStream<Path> stream = Files.newDirectoryStream(pluginsRootPath,
                                                                          glob);

            for (final Path activeJS : stream) {
                result.add(new String(Files.readAllBytes(activeJS)));
            }
        }

        return result;
    }

    @Override
    public String getRuntimePluginTemplateContent(String pluginName) {
        if (htmlPluginProcessor.isRegistered(pluginName)) {
            Optional<RuntimePlugin> runtimePlugin = htmlPluginProcessor.lookupForTemplate(pluginName);

            return runtimePlugin.
                    map(p -> p.getPluginContent()).orElse("");
        }
        return "";
    }

    @Override
    public List<RuntimePlugin> getRuntimePlugins() {
        List<RuntimePlugin> runtimePlugins = new ArrayList<>();

        runtimePluginProcessors.forEach(p -> runtimePlugins.addAll(p.getAvailableRuntimePlugins()));

        return runtimePlugins;
    }
}
