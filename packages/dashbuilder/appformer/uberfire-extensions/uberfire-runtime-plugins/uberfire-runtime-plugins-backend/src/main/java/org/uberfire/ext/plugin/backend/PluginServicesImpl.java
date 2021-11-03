/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.plugin.backend;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.project.storage.ProjectStorageServices;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.ext.plugin.model.LayoutEditorModel;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.plugin.service.PluginServices;

@Service
@ApplicationScoped
public class PluginServicesImpl implements PluginServices {

    private ProjectStorageServices projectStorageServices;

    public PluginServicesImpl() {
        // empty
    }

    @Inject
    public PluginServicesImpl(ProjectStorageServices projectStorageServices) {
        this.projectStorageServices = projectStorageServices;
    }

    @Override
    public Collection<Plugin> listPlugins() {
        return projectStorageServices.listPerspectives()
                .keySet()
                .stream()
                .map(p -> p.getParent().getFileName()
                        .toString()).map(p -> new Plugin(p, PluginType.PERSPECTIVE_LAYOUT, PathFactory.newPath(p, p)))
                .collect(Collectors.toList());
    }

    public LayoutEditorModel getLayoutEditor(Path path) {
        if (path == null) {
            return new LayoutEditorModel();
        }
        return projectStorageServices.getPerspective(path.getFileName())
                .map(lt -> new LayoutEditorModel(path.getFileName(), PluginType.PERSPECTIVE_LAYOUT, path, lt))
                .orElse(new LayoutEditorModel());
    }
}
