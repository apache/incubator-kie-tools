/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.compiler.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.guvnor.common.services.project.backend.server.utils.configuration.ConfigurationKey;
import org.guvnor.common.services.project.backend.server.utils.configuration.ConfigurationStrategy;
import org.junit.Test;
import org.uberfire.java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class ConfigurationTest {

    private static final String PROPERTIES_FILE = "IncrementalCompiler.properties";
    private String removedPropValue;

    @Test
    public void loadConfig() {
        ConfigurationContextProvider provider = new ConfigurationContextProvider();
        assertThat(provider.isValid()).isTrue();
        Map<ConfigurationKey, String> conf = provider.loadConfiguration();
        assertThat(conf.keySet()).hasSize(14);
    }


    @Test
    public void loadPropertiesConfig() {
        ConfigurationStrategy strategy = new ConfigurationPropertiesStrategy();
        Map<ConfigurationKey, String> conf = strategy.loadConfiguration();
        assertThat(strategy.isValid()).isTrue();
        assertThat(conf.keySet()).hasSize(14);
    }

    @Test
    public void loadAlternativePropertiesConfig() {
        ConfigurationStrategy strategy = new ConfigurationPropertiesStrategy(Paths.get("src/test/resources/alternativeConfiguration.properties"));
        Map<ConfigurationKey, String> conf = strategy.loadConfiguration();
        assertThat(strategy.isValid()).isTrue();
        assertThat(conf.keySet()).hasSize(14);
        assertThat(conf.get(ConfigurationKey.TARGET_VERSION)).isEqualToIgnoringCase("1.9");
        assertThat(conf.get(ConfigurationKey.SOURCE_VERSION)).isEqualToIgnoringCase("1.9");
    }

    @Test
    public void loadAlternativeBrokenPropertiesConfig() {
        ConfigurationStrategy strategy = new ConfigurationPropertiesStrategy(Paths.get("src/test/resources/alternativeBrokenConfiguration.properties"));
        Map<ConfigurationKey, String> conf = strategy.loadConfiguration();
        assertThat(strategy.isValid()).isFalse();
    }

    @Test
    public void loadNotValidPropertiesConfig() {
        try {
            removePropertyFromFile();
        } catch (Exception ex) {
            fail("Removing property from the file failed.", ex);
        }

        ConfigurationStrategy strategy = new ConfigurationPropertiesStrategy();
        Map<ConfigurationKey, String> conf = strategy.loadConfiguration();
        assertThat(strategy.isValid()).isFalse();
        assertThat(conf.size()).isNotEqualTo(14);

        try {
            addPropertyBackToFile();
        } catch (Exception ex) {
            fail("Adding property back to the file failed.", ex);
        }
    }

    @Test
    public void loadEnvironmentConfig() {
        ConfigurationStrategy strategy = new ConfigurationEnvironmentStrategy();
        Map<ConfigurationKey, String> conf = strategy.loadConfiguration();
        assertThat(conf).isEmpty();

        strategy = new ConfigurationEnvironmentStrategy(getMapForEnv());
        conf = strategy.loadConfiguration();
        assertThat(conf).isNotEmpty();
        assertThat(strategy.isValid()).isTrue();
        assertThat(conf.keySet()).hasSize(14);
    }

    @Test
    public void loadNotValidEnvironmentConfiguration() {
        ConfigurationStrategy strategy = new ConfigurationEnvironmentStrategy();
        Map<ConfigurationKey, String> conf = strategy.loadConfiguration();
        assertThat(conf).isEmpty();

        Map<String, String> notValidEnv = getMapForEnv();
        notValidEnv.remove(ConfigurationKey.MAVEN_COMPILER_PLUGIN_VERSION.name());

        strategy = new ConfigurationEnvironmentStrategy(notValidEnv);
        conf = strategy.loadConfiguration();
        assertThat(conf).isNotNull();
        assertThat(strategy.isValid()).isFalse();
    }

    private Map<String, String> getMapForEnv() {
        ConfigurationUtil util = new ConfigurationUtil();
        Properties prop = util.loadKieVersionProperties();
        Map conf = new HashMap<>();
        conf.put(ConfigurationKey.COMPILER.name(), prop.get(ConfigurationKey.COMPILER.name()));
        conf.put(ConfigurationKey.SOURCE_VERSION.name(), prop.get(ConfigurationKey.SOURCE_VERSION.name()));
        conf.put(ConfigurationKey.TARGET_VERSION.name(), prop.get(ConfigurationKey.TARGET_VERSION.name()));
        conf.put(ConfigurationKey.MAVEN_COMPILER_PLUGIN_GROUP.name(), prop.get(ConfigurationKey.MAVEN_COMPILER_PLUGIN_GROUP.name()));
        conf.put(ConfigurationKey.MAVEN_COMPILER_PLUGIN_ARTIFACT.name(), prop.get(ConfigurationKey.MAVEN_COMPILER_PLUGIN_ARTIFACT.name()));
        conf.put(ConfigurationKey.MAVEN_COMPILER_PLUGIN_VERSION.name(), prop.get(ConfigurationKey.MAVEN_COMPILER_PLUGIN_VERSION.name()));
        conf.put(ConfigurationKey.FAIL_ON_ERROR.name(), prop.get(ConfigurationKey.FAIL_ON_ERROR.name()));
        conf.put(ConfigurationKey.TAKARI_COMPILER_PLUGIN_GROUP.name(), prop.get(ConfigurationKey.TAKARI_COMPILER_PLUGIN_GROUP.name()));
        conf.put(ConfigurationKey.TAKARI_COMPILER_PLUGIN_ARTIFACT.name(), prop.get(ConfigurationKey.TAKARI_COMPILER_PLUGIN_ARTIFACT.name()));
        conf.put(ConfigurationKey.TAKARI_COMPILER_PLUGIN_VERSION.name(), prop.get(ConfigurationKey.TAKARI_COMPILER_PLUGIN_VERSION.name()));
        conf.put(ConfigurationKey.KIE_PLUGIN_GROUP.name(), prop.get(ConfigurationKey.KIE_PLUGIN_GROUP.name()));
        conf.put(ConfigurationKey.KIE_MAVEN_PLUGIN_ARTIFACT.name(), prop.get(ConfigurationKey.KIE_MAVEN_PLUGIN_ARTIFACT.name()));
        conf.put(ConfigurationKey.KIE_TAKARI_PLUGIN_ARTIFACT.name(), prop.get(ConfigurationKey.KIE_TAKARI_PLUGIN_ARTIFACT.name()));
        conf.put(ConfigurationKey.KIE_VERSION.name(), prop.getProperty(ConfigurationUtil.KIE_VERSION_KEY));
        return conf;
    }

    private void removePropertyFromFile() throws Exception {
        Properties properties = loadPropertiesFile();

        try (FileOutputStream out = createOutputStream()) {
            removedPropValue = properties.getProperty(ConfigurationKey.KIE_VERSION.name());
            properties.remove(ConfigurationKey.KIE_VERSION.name());
            properties.store(out, PROPERTIES_FILE);
        }
    }

    private void addPropertyBackToFile() throws Exception {
        Properties properties = loadPropertiesFile();

        try (FileOutputStream out = createOutputStream()) {
            properties.put(ConfigurationKey.KIE_VERSION.name(), removedPropValue);
            properties.store(out, PROPERTIES_FILE);
        }
    }

    private Properties loadPropertiesFile() throws IOException {
        Properties properties = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            properties.load(in);
        }
        return properties;
    }

    private FileOutputStream createOutputStream() throws URISyntaxException, FileNotFoundException {
        URL url = getClass().getClassLoader().getResource(PROPERTIES_FILE);
        File fileObject = new File(url.toURI());
        return new FileOutputStream(fileObject);
    }

}
