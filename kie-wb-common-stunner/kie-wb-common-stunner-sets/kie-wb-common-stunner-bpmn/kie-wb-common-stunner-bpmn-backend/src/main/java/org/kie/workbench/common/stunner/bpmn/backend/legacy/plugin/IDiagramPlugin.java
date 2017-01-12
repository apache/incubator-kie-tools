/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.bpmn.backend.legacy.plugin;

import java.io.InputStream;
import java.util.Map;

/**
 * The interface defining a plugin.
 * @author Antoine Toulme
 */
public interface IDiagramPlugin {

    /**
     * @return the name of the plugin.
     * The name of the plugin should be unique amongst all plugins, so you should make sure to qualify it.
     */
    public String getName();

    /**
     * @return the contents of a plugin.
     * The contents of the plugin file.
     * <p/>
     * The object returned by this method MUST be closed explicitely.
     */
    public InputStream getContents();

    /**
     * @return true if the plugin should be considered a core plugin and loaded for all profiles.
     */
    public boolean isCore();

    /**
     * @return the properties of the plugin
     */
    public Map<String, Object> getProperties();

    /**
     * @return true if the contents of the plugin can be compressed.
     */
    public boolean isCompressable();
}
