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
package org.uberfire.backend.plugin;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RuntimePlugin {

    private PluginProcessor.PluginProcessorType type;
    private String pluginName;
    private String pluginContent;

    public RuntimePlugin() {
    }

    public RuntimePlugin(PluginProcessor.PluginProcessorType type,
                         String pluginName,
                         String pluginContent) {

        this.type = type;
        this.pluginName = pluginName;
        this.pluginContent = pluginContent;
    }

    public PluginProcessor.PluginProcessorType getType() {
        return type;
    }

    public String getPluginContent() {
        return pluginContent;
    }

    public String getPluginName() {
        return pluginName;
    }
}