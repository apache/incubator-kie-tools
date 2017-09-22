/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.bpmn.backend.legacy.plugin;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

/**
 * A service to access the registered plugins and find plugins.
 * @author Antoine Toulme
 */
public interface IDiagramPluginService {

    /**
     * @param request the context in which the plugins are requested.
     * @return a unmodifiable collection of the registered plugins.
     */
    public Collection<IDiagramPlugin> getRegisteredPlugins(HttpServletRequest request);

    /**
     * @param request the context in which the plugin is requested
     * @param name the name of the plugin to find
     * @return the plugin object or null
     */
    public IDiagramPlugin findPlugin(HttpServletRequest request,
                                     String name);
}
