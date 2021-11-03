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

import org.dashbuilder.navigation.layout.LayoutTemplateContext;
import org.dashbuilder.navigation.layout.LayoutTemplateInfo;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.plugin.model.Plugin;

/**
 * Runtime perspective plugins related services.
 */
@Remote
public interface PerspectivePluginServices {

    Collection<Plugin> listPlugins();

    Plugin getPerspectivePlugin(String perspectiveName);

    LayoutTemplate getLayoutTemplate(String perspectiveName);

    LayoutTemplate getLayoutTemplate(Plugin perspectivePlugin);

    LayoutTemplateInfo getLayoutTemplateInfo(String perspectiveName);

    LayoutTemplateInfo getLayoutTemplateInfo(Plugin perspectivePlugin, LayoutTemplateContext layoutCtx);

    LayoutTemplateInfo getLayoutTemplateInfo(LayoutTemplate layoutTemplate);
}
