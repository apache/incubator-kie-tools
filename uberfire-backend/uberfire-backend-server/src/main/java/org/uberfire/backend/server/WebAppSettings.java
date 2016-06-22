/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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

package org.uberfire.backend.server;

import java.io.File;

/**
 * It holds some settings regarding the WebApp execution context like, for instance, the home directory where the
 * webapp has been deployed.
 */
public class WebAppSettings {

    private String rootDir = null;

    private static WebAppSettings instance = null;

    private WebAppSettings() {
    }

    public static WebAppSettings get() {
        if ( instance == null ) {
            instance = new WebAppSettings();
        }
        return instance;
    }

    /**
     * Overwrites the webapp's root directory.
     *
     * <p>This method is only intended to be called at bootstrap time by the
     * {@link WebAppListener}. Changing the root directory may cause the webapp to severely fail.</p>
     */
    public void setRootDir(String dir) {
        this.rootDir = dir != null ? dir.trim() : null;
        if (rootDir != null) {
            if (rootDir.endsWith("/")) {
                this.rootDir = rootDir.substring(0, dir.length() - 1);
            }
            if (rootDir.endsWith("\\")) {
                this.rootDir = rootDir.substring(0, dir.length() - 1);
            }
        }
    }

    /**
     * Retrieve the webapp's root directory => The directory where the container deploys the WAR content.
     *
     * @return An absolute path.
     */
    public String getRootDir() {
        return rootDir;
    }

    /**
     * Calculate the absolute path of a directory placed under the the webapp's directory structure.
     *
     * @param relativePath The relative path
     * @return An absolute path
     */
    public String getAbsolutePath(String relativePath) {
        return rootDir != null ? rootDir + File.separator + relativePath : null;
    }
}
