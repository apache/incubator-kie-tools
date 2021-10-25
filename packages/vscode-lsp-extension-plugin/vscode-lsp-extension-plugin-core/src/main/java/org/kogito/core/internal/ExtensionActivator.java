/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.kogito.core.internal;

import java.io.File;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */

public class ExtensionActivator implements BundleActivator {

//    private static final String LOGBACK_CONFIG_FILE_PROPERTY = "logback.configurationFile";
//    private static final String LOGBACK_DEFAULT_FILENAME = "logback.xml";

    // The plug-in ID
    public static final String PLUGIN_ID = "kogito-plugin.core";

    // The shared instance
    private static ExtensionActivator plugin;

    public void start(BundleContext context) throws Exception {
//        LogToFile.log("Plugin Started");
        plugin = this;
//
//        File configFile = new File(LOGBACK_DEFAULT_FILENAME);
//        System.setProperty(LOGBACK_CONFIG_FILE_PROPERTY, configFile.getAbsolutePath());
    }

    public void stop(BundleContext context) throws Exception {
        plugin = null;
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static ExtensionActivator getDefault() {
        return plugin;
    }
}
