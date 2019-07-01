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
package org.dashbuilder.navigation.service;

import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.navigation.layout.LayoutRecursionIssue;
import org.dashbuilder.navigation.layout.LayoutTemplateContext;
import org.dashbuilder.navigation.layout.LayoutTemplateInfo;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.impl.LayoutServicesImpl;
import org.uberfire.ext.plugin.backend.PluginServicesImpl;
import org.uberfire.ext.plugin.model.LayoutEditorModel;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginType;

@ApplicationScoped
@Service
public class PerspectivePluginServicesImpl implements PerspectivePluginServices {

    private PluginServicesImpl pluginServices;
    private LayoutServicesImpl layoutServices;
    private LayoutTemplateAnalyzer layoutTemplateAnalyzer;

    public PerspectivePluginServicesImpl() {
    }

    @Inject
    public PerspectivePluginServicesImpl(PluginServicesImpl pluginServices, LayoutServicesImpl layoutServices, LayoutTemplateAnalyzer layoutTemplateAnalyzer) {
        this.pluginServices = pluginServices;
        this.layoutServices = layoutServices;
        this.layoutTemplateAnalyzer = layoutTemplateAnalyzer;
    }

    @Override
    public Collection<Plugin> listPlugins() {
        return pluginServices.listPlugins(PluginType.PERSPECTIVE_LAYOUT);
    }

    @Override
    public Plugin getPerspectivePlugin(String perspectiveName) {
        if (perspectiveName == null) {
            return null;
        }
        for (Plugin plugin : listPlugins()) {
            if (PluginType.PERSPECTIVE_LAYOUT.equals(plugin.getType()) && plugin.getName().equals(perspectiveName)) {
                return plugin;
            }
        }
        return null;
    }

    @Override
    public LayoutTemplate getLayoutTemplate(String perspectiveName) {
        Plugin perspectivePlugin = getPerspectivePlugin(perspectiveName);
        return perspectivePlugin != null ? getLayoutTemplate(perspectivePlugin) : null;
    }

    @Override
    public LayoutTemplateInfo getLayoutTemplateInfo(String perspectiveName) {
        LayoutTemplate layoutTemplate = getLayoutTemplate(perspectiveName);
        return layoutTemplate != null ? getLayoutTemplateInfo(layoutTemplate) : null;
    }

    @Override
    public LayoutTemplate getLayoutTemplate(Plugin perspectivePlugin) {
        LayoutEditorModel layoutEditorModel = pluginServices.getLayoutEditor(perspectivePlugin.getPath(), PluginType.PERSPECTIVE_LAYOUT);
        return layoutServices.convertLayoutFromString(layoutEditorModel.getLayoutEditorModel());
    }

    @Override
    public LayoutTemplateInfo getLayoutTemplateInfo(Plugin perspectivePlugin, LayoutTemplateContext layoutCtx) {
        LayoutTemplate layoutTemplate = getLayoutTemplate(perspectivePlugin);
        LayoutRecursionIssue recursiveIssue = layoutTemplateAnalyzer.analyzeRecursion(layoutTemplate, layoutCtx);
        boolean hasNavComps = layoutTemplateAnalyzer.hasNavigationComponents(layoutTemplate);
        return new LayoutTemplateInfo(layoutTemplate, hasNavComps, recursiveIssue);
    }

    @Override
    public LayoutTemplateInfo getLayoutTemplateInfo(LayoutTemplate layoutTemplate) {
        LayoutRecursionIssue recursiveIssue = layoutTemplateAnalyzer.analyzeRecursion(layoutTemplate);
        boolean hasNavComps = layoutTemplateAnalyzer.hasNavigationComponents(layoutTemplate);
        return new LayoutTemplateInfo(layoutTemplate, hasNavComps, recursiveIssue);
    }
}
