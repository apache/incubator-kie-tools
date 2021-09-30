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

package org.dashbuilder.client.plugins;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.client.navigation.plugin.PerspectivePluginManager;
import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.layout.LayoutRecursionIssue;
import org.dashbuilder.navigation.layout.LayoutTemplateContext;
import org.dashbuilder.navigation.layout.LayoutTemplateInfo;
import org.dashbuilder.navigation.workbench.NavWorkbenchCtx;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.uberfire.ext.layout.editor.api.editor.LayoutInstance;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.generator.LayoutGenerator;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.mvp.ParameterizedCommand;

/**
 * A specific Runtime perspective plugin manager. This is used by Navigation Components to load a custom perspective.
 *
 */
@Alternative
@ApplicationScoped
public class RuntimePerspectivePluginManager implements PerspectivePluginManager {

    @Inject
    LayoutGenerator layoutGenerator;

    List<LayoutTemplate> templates = new ArrayList<>();

    @Override
    public void loadPlugins() {
        // not used in Runtime
    }

    @Override
    public void getPerspectivePlugins(ParameterizedCommand<Collection<Plugin>> callback) {
        List<Plugin> plugins = templates.stream()
                                        .map(lt -> new Plugin(lt.getName(),
                                                              PluginType.PERSPECTIVE,
                                                              null))
                                        .collect(Collectors.toList());
        callback.execute(plugins);
    }

    @Override
    public boolean isRuntimePerspective(Plugin plugin) {
        return searchLayoutTemplate(plugin.getName());
    }

    @Override
    public boolean isRuntimePerspective(NavItem navItem) {
        NavWorkbenchCtx navCtx = NavWorkbenchCtx.get(navItem);
        String resourceId = navCtx.getResourceId();
        return searchLayoutTemplate(resourceId);
    }

    @Override
    public boolean isRuntimePerspective(String perspectiveId) {
        return searchLayoutTemplate(perspectiveId);
    }

    @Override
    public String getRuntimePerspectiveId(NavItem navItem) {
        NavWorkbenchCtx navCtx = NavWorkbenchCtx.get(navItem);
        return navCtx.getResourceId();
    }

    @Override
    public boolean existsPerspectivePlugin(String perspectiveName) {
        return searchLayoutTemplate(perspectiveName);
    }

    @Override
    public void getLayoutTemplateInfo(String perspectiveName, ParameterizedCommand<LayoutTemplateInfo> callback) {
        // not used in runtime
    }

    @Override
    public void getLayoutTemplateInfo(LayoutTemplate layoutTemplate, ParameterizedCommand<LayoutTemplateInfo> callback) {
        // not used in runtime
    }

    @Override
    public void buildPerspectiveWidget(String perspectiveName, LayoutTemplateContext layoutCtx, ParameterizedCommand<IsWidget> afterBuild, ParameterizedCommand<LayoutRecursionIssue> onInfiniteRecursion) {
        templates.stream().filter(lt -> lt.getName().equals(perspectiveName)).findFirst().ifPresent(lt -> {
            LayoutInstance result = layoutGenerator.build(lt);
            IsWidget widget = ElementWrapperWidget.getWidget(result.getElement());
            afterBuild.execute(widget);
        });
    }

    @Override
    public NavGroup getLastBuildPerspectiveNavGroup() {
        return null;
    }

    public void setTemplates(List<LayoutTemplate> templates) {
        this.templates = templates;
    }

    private boolean searchLayoutTemplate(String name) {
        return templates.stream().anyMatch(lt -> lt.getName().equals(name));
    }

}