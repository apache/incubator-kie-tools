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

import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.layout.LayoutRecursionIssue;
import org.dashbuilder.navigation.layout.LayoutTemplateContext;
import org.dashbuilder.navigation.layout.LayoutTemplateInfo;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.mvp.ParameterizedCommand;

public interface PerspectivePluginManager {
    
    void loadPlugins();

    void getPerspectivePlugins(ParameterizedCommand<Collection<Plugin>> callback);

    boolean isRuntimePerspective(Plugin plugin);

    boolean isRuntimePerspective(NavItem navItem);

    boolean isRuntimePerspective(String perspectiveId);

    String getRuntimePerspectiveId(NavItem navItem);

    boolean existsPerspectivePlugin(String perspectiveName);

    void getLayoutTemplateInfo(String perspectiveName, ParameterizedCommand<LayoutTemplateInfo> callback);

    void getLayoutTemplateInfo(LayoutTemplate layoutTemplate, ParameterizedCommand<LayoutTemplateInfo> callback);

    void buildPerspectiveWidget(String perspectiveName, LayoutTemplateContext layoutCtx, ParameterizedCommand<IsWidget> afterBuild, ParameterizedCommand<LayoutRecursionIssue> onInfiniteRecursion);

    default void buildPerspectiveWidget(String perspectiveName, ParameterizedCommand<IsWidget> afterBuild, ParameterizedCommand<LayoutRecursionIssue> onInfiniteRecursion) {
        buildPerspectiveWidget(perspectiveName, null, afterBuild, onInfiniteRecursion);
    }

    NavGroup getLastBuildPerspectiveNavGroup();

}