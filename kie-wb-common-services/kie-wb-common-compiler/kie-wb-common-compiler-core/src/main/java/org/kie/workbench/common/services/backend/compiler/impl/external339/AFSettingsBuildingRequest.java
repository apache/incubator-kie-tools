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
package org.kie.workbench.common.services.backend.compiler.impl.external339;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Properties;

import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsSource;

/**
 * Adapted class to use in our impl instead the original package private
 */
public class AFSettingsBuildingRequest implements SettingsBuildingRequest {

    private Path globalSettingsFile;
    private Path userSettingsFile;
    private SettingsSource globalSettingsSource;
    private SettingsSource userSettingsSource;
    private Properties systemProperties;
    private Properties userProperties;

    public AFSettingsBuildingRequest() {
    }

    public File getGlobalSettingsFile() {
        return this.globalSettingsFile.toFile();
    }

    public AFSettingsBuildingRequest setGlobalSettingsFile(File globalSettingsFile) {
        this.globalSettingsFile = Paths.get(globalSettingsFile.toURI());
        return this;
    }

    public Path getGlobalSettingsPath() {
        return this.globalSettingsFile;
    }

    public AFSettingsBuildingRequest setGlobalSettingsPath(Path globalSettingsFile) {
        this.globalSettingsFile = globalSettingsFile;
        return this;
    }

    public SettingsSource getGlobalSettingsSource() {
        return this.globalSettingsSource;
    }

    public AFSettingsBuildingRequest setGlobalSettingsSource(SettingsSource globalSettingsSource) {
        this.globalSettingsSource = globalSettingsSource;
        return this;
    }

    public File getUserSettingsFile() {
        return this.userSettingsFile.toFile();
    }

    public AFSettingsBuildingRequest setUserSettingsFile(File userSettingsFile) {
        this.userSettingsFile = Paths.get(userSettingsFile.toURI());
        return this;
    }

    public Path getUserSettingsPath() {
        return this.userSettingsFile;
    }

    public AFSettingsBuildingRequest setUserSettingsPath(Path userSettingsFile) {
        this.userSettingsFile = userSettingsFile;
        return this;
    }

    public SettingsSource getUserSettingsSource() {
        return this.userSettingsSource;
    }

    public AFSettingsBuildingRequest setUserSettingsSource(SettingsSource userSettingsSource) {
        this.userSettingsSource = userSettingsSource;
        return this;
    }

    public Properties getSystemProperties() {
        if (this.systemProperties == null) {
            this.systemProperties = new Properties();
        }

        return this.systemProperties;
    }

    public AFSettingsBuildingRequest setSystemProperties(Properties systemProperties) {
        if (systemProperties != null) {
            this.systemProperties = new Properties();
            Iterator i$ = System.getProperties().stringPropertyNames().iterator();

            while (i$.hasNext()) {
                String key = (String) i$.next();
                this.systemProperties.put(key,
                                          System.getProperty(key));
            }
        } else {
            this.systemProperties = null;
        }

        return this;
    }

    public Properties getUserProperties() {
        if (this.userProperties == null) {
            this.userProperties = new Properties();
        }

        return this.userProperties;
    }

    public AFSettingsBuildingRequest setUserProperties(Properties userProperties) {
        if (userProperties != null) {
            this.userProperties = new Properties();
            this.userProperties.putAll(userProperties);
        } else {
            this.userProperties = null;
        }

        return this;
    }
}
