/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.backend.remote.services.dummy;

import java.util.Collection;
import java.util.Collections;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.file.DefaultMetadata;
import org.uberfire.ext.plugin.model.DynamicMenu;
import org.uberfire.ext.plugin.model.LayoutEditorModel;
import org.uberfire.ext.plugin.model.Media;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginContent;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.plugin.model.RuntimePlugin;
import org.uberfire.ext.plugin.service.PluginServices;

/**
 * This should be removed as soon as PluginServices cliend side mocked service starts working.
 *
 */
@Service
@ApplicationScoped
public class DummyPluginServices implements PluginServices {

    @Override
    public void delete(Path path, String comment) {
        // not used
    }

    @Override
    public Path copy(Path path, String newName, String comment) {
        return null;
    }

    @Override
    public Path copy(Path path, String newName, Path targetDirectory, String comment) {
        return null;
    }

    @Override
    public Path saveAndRename(Path path, String newFileName, DefaultMetadata metadata, Plugin content, String comment) {
        return null;
    }

    @Override
    public Path rename(Path path, String newName, String comment) {
        return null;
    }

    @Override
    public Path save(Path path, Plugin content, DefaultMetadata metadata, String comment) {
        return null;
    }

    @Override
    public String getMediaServletURI() {
        return null;
    }

    @Override
    public Collection<RuntimePlugin> listRuntimePlugins() {
        return Collections.emptyList();
    }

    @Override
    public Collection<RuntimePlugin> listPluginRuntimePlugins(Path pluginPath) {
        return Collections.emptyList();
    }

    @Override
    public Collection<Plugin> listPlugins() {
        return Collections.emptyList();
    }

    @Override
    public Collection<Plugin> listPlugins(PluginType type) {
        return Collections.emptyList();
    }

    @Override
    public Plugin createNewPlugin(String name, PluginType type) {
        return null;
    }

    @Override
    public PluginContent getPluginContent(Path path) {
        return null;
    }

    @Override
    public void deleteMedia(Media media) {
        // not used
    }

    @Override
    public DynamicMenu getDynamicMenuContent(Path path) {
        return null;
    }

    @Override
    public Path save(Plugin plugin, String commitMessage) {
        return null;
    }

    @Override
    public LayoutEditorModel getLayoutEditor(Path path, PluginType pluginType) {
        return null;
    }

    @Override
    public Path saveMenu(DynamicMenu menu, String commitMessage) {
        return null;
    }

    @Override
    public Path saveLayout(LayoutEditorModel layoutContent, String commitMessage) {
        return null;
    }

    @Override
    public Collection<DynamicMenu> listDynamicMenus() {
        return Collections.emptyList();
    }

    @Override
    public Collection<LayoutEditorModel> listLayoutEditor(PluginType pluginType) {
        return Collections.emptyList();
    }

}