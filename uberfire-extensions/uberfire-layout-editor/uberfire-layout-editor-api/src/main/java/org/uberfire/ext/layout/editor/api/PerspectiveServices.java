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

package org.uberfire.ext.layout.editor.api;

import java.util.Collection;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.file.DefaultMetadata;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRename;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.plugin.model.Plugin;

@Remote
public interface PerspectiveServices extends SupportsCopy,
                                             SupportsDelete,
                                             SupportsSaveAndRename<LayoutTemplate, DefaultMetadata> {

    Plugin createNewPerspective(String name, LayoutTemplate.Style style);

    Collection<LayoutTemplate> listLayoutTemplates();

    LayoutTemplate getLayoutTemplate(String perspectiveName);

    LayoutTemplate getLayoutTemplate(Path perspectivePath);

    LayoutTemplate getLayoutTemplate(Plugin perspectivePlugin);

    LayoutTemplate convertToLayoutTemplate(String layoutModel);

    Path saveLayoutTemplate(Path perspectivePath, LayoutTemplate layoutTemplate, String commitMessage);
}
