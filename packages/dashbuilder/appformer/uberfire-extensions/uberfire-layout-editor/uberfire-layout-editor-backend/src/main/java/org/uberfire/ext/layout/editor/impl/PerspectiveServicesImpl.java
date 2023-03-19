/*
 * Copyright 2017 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.layout.editor.impl;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.dashbuilder.project.storage.ProjectStorageServices;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.ext.editor.commons.file.DefaultMetadata;
import org.uberfire.ext.layout.editor.api.PerspectiveServices;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.plugin.event.PluginAdded;
import org.uberfire.ext.plugin.event.PluginDeleted;
import org.uberfire.ext.plugin.event.PluginRenamed;
import org.uberfire.ext.plugin.event.PluginSaved;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginType;

@Service
@ApplicationScoped
public class PerspectiveServicesImpl implements PerspectiveServices {

    private LayoutServicesImpl layoutServices;
    private ProjectStorageServices projectStorageServices;
    private Event<PluginAdded> pluginAddedEvent;
    private Event<PluginDeleted> pluginDeletedEvent;
    private Event<PluginSaved> pluginSavedEvent;
    private Event<PluginRenamed> pluginRenamedEvent;

    @Inject
    public PerspectiveServicesImpl(final ProjectStorageServices projectStorageServices,
                                   final LayoutServicesImpl layoutServices,
                                   final Event<PluginAdded> pluginAddedEvent,
                                   final Event<PluginDeleted> pluginDeletedEvent,
                                   final Event<PluginSaved> pluginSavedEvent,
                                   final Event<PluginRenamed> pluginRenamedEvent) {
        this.projectStorageServices = projectStorageServices;
        this.layoutServices = layoutServices;
        this.pluginAddedEvent = pluginAddedEvent;
        this.pluginDeletedEvent = pluginDeletedEvent;
        this.pluginSavedEvent = pluginSavedEvent;
        this.pluginRenamedEvent = pluginRenamedEvent;
    }

    @Override
    public Plugin createNewPerspective(String name, LayoutTemplate.Style style) {
        LayoutTemplate layoutTemplate = new LayoutTemplate(name, style);
        saveLayoutTemplate(layoutTemplate);
        var plugin = new Plugin(name, PluginType.PERSPECTIVE_LAYOUT, PathFactory.newPath(name, name));
        pluginAddedEvent.fire(new PluginAdded(plugin, null));
        return plugin;
    }

    @Override
    public Collection<LayoutTemplate> listLayoutTemplates() {
        return projectStorageServices.listPerspectives()
                .values()
                .stream()
                .map(layoutServices::fromJson)
                .collect(Collectors.toList());
    }

    @Override
    public LayoutTemplate getLayoutTemplate(String perspectiveName) {
        Plugin perspectivePlugin = getLayoutTemplatePlugin(perspectiveName);
        return perspectivePlugin != null ? getLayoutTemplate(perspectivePlugin) : null;
    }

    @Override
    public LayoutTemplate getLayoutTemplate(Path perspectivePath) {
        var name = perspectivePath.getFileName();
        return projectStorageServices.getPerspective(name)
                .map(layoutServices::fromJson)
                .orElse(new LayoutTemplate(name, LayoutTemplate.Style.PAGE));
    }

    @Override
    public LayoutTemplate getLayoutTemplate(Plugin perspectivePlugin) {
        return getLayoutTemplate(perspectivePlugin.getPath());
    }

    public Plugin getLayoutTemplatePlugin(String perspectiveName) {
        if (perspectiveName == null) {
            return null;
        }
        return projectStorageServices.getPerspective(perspectiveName)
                .map(p -> new Plugin(perspectiveName, PluginType.PERSPECTIVE_LAYOUT, PathFactory.newPath(
                        perspectiveName, perspectiveName)))
                .orElse(null);
    }

    @Override
    public Path saveLayoutTemplate(LayoutTemplate layoutTemplate) {
        var layoutModel = layoutServices.toJson(layoutTemplate);
        var path = PathFactory.newPath(layoutTemplate.getName(), layoutTemplate.getName());
        
        projectStorageServices.savePerspective(layoutTemplate.getName(), layoutModel);
        pluginSavedEvent.fire(new PluginSaved(new Plugin(layoutTemplate.getName(), PluginType.PERSPECTIVE_LAYOUT, path), null));
        return path;
    }

    @Override
    public LayoutTemplate convertToLayoutTemplate(String layoutModel) {
        return layoutServices.fromJson(layoutModel);
    }

    @Override
    public Path copy(Path path, String newName, String comment) {
        var perspectiveOp = projectStorageServices.getPerspective(path.getFileName());
        var newPath = PathFactory.newPath(newName, newName);
        perspectiveOp.ifPresent(p -> {
            var template = getLayoutTemplate(path);
            template.setName(newName);
            projectStorageServices.savePerspective(newName, layoutServices.toJson(template));
        });
        pluginAddedEvent.fire(new PluginAdded(new Plugin(newName, PluginType.PERSPECTIVE_LAYOUT, newPath), null));
        return newPath;
    }

    @Override
    public Path copy(Path path, String newName, Path targetDirectory, String comment) {
        return copy(path, newName, comment);
    }

    @Override
    public Path rename(Path path, String newName, String comment) {
        var perspectiveOp = projectStorageServices.getPerspective(path.getFileName());
        var newPath = PathFactory.newPath(newName, newName);
        perspectiveOp.ifPresent(p -> projectStorageServices.savePerspective(newName, p));
        projectStorageServices.removePerspective(path.getFileName());
        pluginRenamedEvent.fire(new PluginRenamed(path.getFileName(),  new Plugin(newName, PluginType.PERSPECTIVE_LAYOUT, newPath), null));
        return newPath;
    }

    @Override
    public void delete(Path path, String comment) {
        projectStorageServices.removePerspective(path.getFileName());
        pluginDeletedEvent.fire(new PluginDeleted(new Plugin(path.getFileName(), PluginType.PERSPECTIVE_LAYOUT, path), null));
    }

    @Override
    public Path save(final Path path,
                     final LayoutTemplate content,
                     final DefaultMetadata metadata,
                     final String comment) {
        return saveLayoutTemplate(content);
    }

    @Override
    public Path saveAndRename(final Path path,
                              final String newFileName,
                              final DefaultMetadata metadata,
                              final LayoutTemplate content,
                              final String comment) {
        this.save(path, content, metadata, comment);
        return this.rename(path, newFileName, comment);
    }
}
