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
package org.kie.workbench.common.stunner.bpmn.backend.legacy.plugin.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.kie.workbench.common.stunner.bpmn.backend.legacy.plugin.IDiagramPlugin;

/**
 * @author Antoine Toulme
 * A default implementation of a plugin for plugins defined inside the Process Designer web application
 */
public class LocalPluginImpl extends LocalResource implements IDiagramPlugin {

    private boolean _core;
    private Map<String, Object> _properties = new HashMap<String, Object>();

    public LocalPluginImpl(String name,
                           String path,
                           ServletContext context,
                           boolean core,
                           Map<String, Object> props) {
        super(name,
              path,
              context);
        StringBuilder localPath = new StringBuilder();
        localPath.append("js").append("/");
        localPath.append("Plugins").append("/").append(path);
        this._core = core;
        this._properties.putAll(props);
    }

    public boolean isCore() {
        return _core;
    }

    public Map<String, Object> getProperties() {
        return _properties;
    }

    public boolean isCompressable() {
        return true;
    }
}
