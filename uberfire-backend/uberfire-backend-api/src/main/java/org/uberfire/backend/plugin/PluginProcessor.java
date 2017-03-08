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

/**
 * A simple marker to keep track of all plugins processors.
 */
public interface PluginProcessor {

    /**
     * Checks if a plugin file has the valid extension
     * @param fileName the file name of the plugin
     */
    static boolean isAValidPluginFileExtension(String fileName) {

        for (PluginProcessorType type : PluginProcessorType.values()) {
            if (fileName.endsWith(type.getExtension())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this processor should process a given plugin
     * @param pluginName the name of the plugin, must not be null.
     */
    boolean shouldProcess(String pluginName);

    /**
     * Removes all plugins from this processor.
     */
    void removeAll();

    /**
     * Processes a deployed plugin and optionally notifies clients
     * @param pluginName the name of the plugin, must not be null.
     * @param pluginDeploymentDir the directory to deploy plugin contents to, must not be null.
     * @param notifyClients trigger a event after plugin processing
     */
    void process(String pluginName,
                 String pluginDeploymentDir,
                 boolean notifyClients);

    /**
     * Checks if a plugin with the given name is registered.
     * @param pluginName the name of the plugin, must not be null.
     * @return true if registered, otherwise false.
     */
    boolean isRegistered(String pluginName);

    enum PluginProcessorType {
        GWT("nocache.js",
            false),
        PERSPECTIVE_EDITOR("layout.json",
                           true),
        HTML_TEMPLATE(".html",
                      true),
        JS(".js",
           true);

        private String extension;
        private boolean isRuntimePlugin;

        PluginProcessorType(String extension,
                            boolean isRuntimePlugin) {
            this.extension = extension;
            this.isRuntimePlugin = isRuntimePlugin;
        }

        public String getExtension() {
            return extension;
        }

        public boolean isRuntimePlugin() {
            return isRuntimePlugin;
        }
    }
}
