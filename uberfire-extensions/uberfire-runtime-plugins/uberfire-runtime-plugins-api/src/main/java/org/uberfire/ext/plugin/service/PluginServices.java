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

package org.uberfire.ext.plugin.service;

import java.util.Collection;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.file.DefaultMetadata;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRename;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
import org.uberfire.ext.plugin.model.DynamicMenu;
import org.uberfire.ext.plugin.model.LayoutEditorModel;
import org.uberfire.ext.plugin.model.Media;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginContent;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.plugin.model.RuntimePlugin;

@Remote
public interface PluginServices extends SupportsDelete,
                                        SupportsCopy,
                                        SupportsSaveAndRename<Plugin, DefaultMetadata> {

    String getMediaServletURI();

    Collection<RuntimePlugin> listRuntimePlugins();

    Collection<RuntimePlugin> listPluginRuntimePlugins(final org.uberfire.backend.vfs.Path pluginPath);

    Collection<Plugin> listPlugins();

    Collection<Plugin> listPlugins(final PluginType type);

    Plugin createNewPlugin(final String name,
                           final PluginType type);

    PluginContent getPluginContent(final Path path);

    void deleteMedia(final Media media);

    DynamicMenu getDynamicMenuContent(final Path path);

    Path save(final Plugin plugin,
              final String commitMessage);

    LayoutEditorModel getLayoutEditor(Path path,
                                      PluginType pluginType);

    Path saveMenu(final DynamicMenu menu,
                  final String commitMessage);

    Path saveLayout(LayoutEditorModel layoutContent,
                    String commitMessage);

    Collection<DynamicMenu> listDynamicMenus();

    Collection<LayoutEditorModel> listLayoutEditor(PluginType pluginType);
}
