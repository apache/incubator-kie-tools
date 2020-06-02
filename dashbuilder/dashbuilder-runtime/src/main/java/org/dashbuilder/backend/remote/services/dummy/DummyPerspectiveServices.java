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
import org.uberfire.ext.layout.editor.api.PerspectiveServices;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate.Style;
import org.uberfire.ext.plugin.model.Plugin;

@Service
@ApplicationScoped
public class DummyPerspectiveServices implements PerspectiveServices {

    @Override
    public Path copy(Path path, String newName, String comment) {
        return null;
    }

    @Override
    public Path copy(Path path, String newName, Path targetDirectory, String comment) {
        return null;
    }

    @Override
    public void delete(Path path, String comment) {
        // ignored
    }

    @Override
    public Path saveAndRename(Path path, String newFileName, DefaultMetadata metadata, LayoutTemplate content, String comment) {
        return null;
    }

    @Override
    public Path rename(Path path, String newName, String comment) {
        return null;
    }

    @Override
    public Path save(Path path, LayoutTemplate content, DefaultMetadata metadata, String comment) {
        return null;
    }

    @Override
    public Plugin createNewPerspective(String name, Style style) {
        return null;
    }

    @Override
    public Collection<LayoutTemplate> listLayoutTemplates() {
        return Collections.emptyList();
    }

    @Override
    public LayoutTemplate getLayoutTemplate(String perspectiveName) {
        return null;
    }

    @Override
    public LayoutTemplate getLayoutTemplate(Path perspectivePath) {
        return null;
    }

    @Override
    public LayoutTemplate getLayoutTemplate(Plugin perspectivePlugin) {
        return null;
    }

    @Override
    public LayoutTemplate convertToLayoutTemplate(String layoutModel) {
        return null;
    }

    @Override
    public Path saveLayoutTemplate(Path perspectivePath, LayoutTemplate layoutTemplate, String commitMessage) {
        return null;
    }

}