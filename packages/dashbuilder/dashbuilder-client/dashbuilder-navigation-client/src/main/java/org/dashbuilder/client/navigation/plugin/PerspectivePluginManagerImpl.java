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
package org.dashbuilder.client.navigation.plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.client.navigation.NavigationManager;
import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.event.PerspectivePluginsChangedEvent;
import org.dashbuilder.navigation.layout.LayoutRecursionIssue;
import org.dashbuilder.navigation.layout.LayoutTemplateContext;
import org.dashbuilder.navigation.layout.LayoutTemplateInfo;
import org.dashbuilder.navigation.service.PerspectivePluginServices;
import org.dashbuilder.navigation.workbench.NavWorkbenchCtx;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.generator.LayoutGenerator;
import org.uberfire.ext.plugin.event.PluginAdded;
import org.uberfire.ext.plugin.event.PluginDeleted;
import org.uberfire.ext.plugin.event.PluginRenamed;
import org.uberfire.ext.plugin.event.PluginSaved;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.model.ActivityResourceType;

@EntryPoint
@ApplicationScoped
public class PerspectivePluginManagerImpl implements PerspectivePluginManager {

    private LayoutGenerator layoutGenerator;
    private NavigationManager navigationManager;
    private Caller<PerspectivePluginServices> pluginServices;
    private Event<PerspectivePluginsChangedEvent> perspectivesChangedEvent;
    private Map<String, Plugin> pluginMap = new HashMap<>();
    private boolean pluginsLoaded = false;
    private Stack<NavGroup> navGroupStack = new Stack<>();

    @Inject
    public PerspectivePluginManagerImpl(LayoutGenerator layoutGenerator,
                                        NavigationManager navigationManager,
                                        Caller<PerspectivePluginServices> pluginServices,
                                        Event<PerspectivePluginsChangedEvent> perspectivesChangedEvent) {
        this.layoutGenerator = layoutGenerator;
        this.navigationManager = navigationManager;
        this.pluginServices = pluginServices;
        this.perspectivesChangedEvent = perspectivesChangedEvent;
    }

    @Override
    public void getPerspectivePlugins(ParameterizedCommand<Collection<Plugin>> callback) {
        loadPlugins(callback);
    }

    @Override
    public boolean isRuntimePerspective(Plugin plugin) {
        return plugin.getType() == PluginType.PERSPECTIVE_LAYOUT;
    }

    @Override
    public boolean isRuntimePerspective(NavItem navItem) {
        return getRuntimePerspectiveId(navItem) != null;
    }

    @Override
    public boolean isRuntimePerspective(String perspectiveId) {
        return pluginMap.containsKey(perspectiveId);
    }

    @Override
    public String getRuntimePerspectiveId(NavItem navItem) {
        var navCtx = NavWorkbenchCtx.get(navItem);
        var resourceId = navCtx.getResourceId();
        var resourceType = navCtx.getResourceType();
        var isRuntimePerspective = resourceId != null && ActivityResourceType.PERSPECTIVE.equals(resourceType) && isRuntimePerspective(resourceId);
        return isRuntimePerspective ? resourceId : null;
    }

    @Override
    public boolean existsPerspectivePlugin(String perspectiveName) {
        return perspectiveName != null && pluginMap.get(perspectiveName) != null;
    }

    @Override
    public void getLayoutTemplateInfo(String perspectiveName, ParameterizedCommand<LayoutTemplateInfo> callback) {
        pluginServices.call((RemoteCallback<LayoutTemplateInfo>) callback::execute).getLayoutTemplateInfo(perspectiveName);
    }

    @Override
    public void getLayoutTemplateInfo(LayoutTemplate layoutTemplate, ParameterizedCommand<LayoutTemplateInfo> callback) {
        pluginServices.call((RemoteCallback<LayoutTemplateInfo>) callback::execute).getLayoutTemplateInfo(layoutTemplate);
    }

    @Override
    public void buildPerspectiveWidget(String perspectiveName, LayoutTemplateContext layoutCtx, ParameterizedCommand<IsWidget> afterBuild, ParameterizedCommand<LayoutRecursionIssue> onInfiniteRecursion) {
        var plugin = pluginMap.get(perspectiveName);
        pluginServices.call((LayoutTemplateInfo layoutInfo) -> {

            if (!layoutInfo.getRecursionIssue().isEmpty()) {
                onInfiniteRecursion.execute(layoutInfo.getRecursionIssue());
            } else {
                String navGroupId = layoutCtx != null && layoutCtx.getNavGroupId() != null ? layoutCtx.getNavGroupId() : null;
                NavGroup navGroup = navGroupId != null ? (NavGroup) navigationManager.getNavTree().getItemById(navGroupId) : null;
                try {
                    if (navGroup != null) {
                        navGroupStack.push(navGroup);
                    }
                    var result = layoutGenerator.build(layoutInfo.getLayoutTemplate());
                    var widget = ElementWrapperWidget.getWidget(result.getElement());
                    afterBuild.execute(widget);
                } finally {
                    if (navGroup != null) {
                        navGroupStack.pop();
                    }
                }
            }
        }).getLayoutTemplateInfo(plugin, layoutCtx);
    }

    /**
     * Get the last nav group instance passed to the execution of a {@link #buildPerspectiveWidget(String, LayoutTemplateContext, ParameterizedCommand, ParameterizedCommand)} call.
     *
     * @return The {@link NavGroup} instance passed to the build method or null if none.
     */
    @Override
    public NavGroup getLastBuildPerspectiveNavGroup() {
        return navGroupStack.isEmpty() ? null : navGroupStack.peek();
    }

    // Sync up both the internals plugin & widget registry

    public void onPlugInAdded(@Observes final PluginAdded event) {
        var plugin = event.getPlugin();
        if (isRuntimePerspective(plugin)) {
            pluginMap.put(plugin.getName(), plugin);
            perspectivesChangedEvent.fire(new PerspectivePluginsChangedEvent());
        }
    }

    public void onPlugInSaved(@Observes final PluginSaved event) {
        var plugin = event.getPlugin();
        if (isRuntimePerspective(plugin)) {
            pluginMap.put(plugin.getName(), plugin);
            perspectivesChangedEvent.fire(new PerspectivePluginsChangedEvent());
        }
    }

    public void onPlugInRenamed(@Observes final PluginRenamed event) {
        var plugin = event.getPlugin();
        if (isRuntimePerspective(plugin)) {
            pluginMap.remove(event.getOldPluginName());
            pluginMap.put(plugin.getName(), plugin);

            var ctx = NavWorkbenchCtx.perspective(event.getOldPluginName());
            var newCtx = NavWorkbenchCtx.perspective(event.getPlugin().getName());
            var itemsToRename = navigationManager.getNavTree().searchItems(ctx);
            for (var navItem : itemsToRename) {
                navItem.setContext(newCtx.toString());
            }
            if (!itemsToRename.isEmpty()) {
                navigationManager.saveNavTree(navigationManager.getNavTree(), () -> {
                });
            }
            perspectivesChangedEvent.fire(new PerspectivePluginsChangedEvent());
        }
    }

    public void onPlugInDeleted(@Observes final PluginDeleted event) {
        var pluginName = event.getPluginName();
        pluginMap.remove(pluginName);

        var ctx = NavWorkbenchCtx.perspective(pluginName);
        var navTree = navigationManager.getNavTree();
        var itemsToDelete = navTree.searchItems(ctx);
        for (var item : itemsToDelete) {
            navTree.deleteItem(item.getId());
        }
        if (!itemsToDelete.isEmpty()) {
            navigationManager.saveNavTree(navTree, null);
        }
        perspectivesChangedEvent.fire(new PerspectivePluginsChangedEvent());
    }

    @Override
    public void loadPlugins() {
        loadPlugins(plugins -> {
        });
    }

    private void loadPlugins(ParameterizedCommand<Collection<Plugin>> callback) {
        if (pluginsLoaded) {
            callback.execute(pluginMap.values());
        } else {
            pluginServices.call(((Collection<Plugin> plugins) -> {
                pluginMap.clear();
                plugins.stream().filter(this::isRuntimePerspective).forEach(p -> pluginMap.put(p.getName(), p));
                pluginsLoaded = true;
                callback.execute(pluginMap.values());
            })).listPlugins();
        }
    }

}