/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.screens.projecteditor.client.wizard;

import java.util.List;
import java.util.HashMap;

import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Plugin;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.projecteditor.client.util.KiePOMDefaultOptions;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;

import static org.junit.Assert.*;

public class POMBuilderTest {

    private POMBuilder pomBuilder;

    private KiePOMDefaultOptions pomDefaultOptions;

    @Before
    public void setUp() throws Exception {
        HashMap<String, String> preferences = new HashMap<String, String>();
        preferences.put(ApplicationPreferences.KIE_VERSION_PROPERTY_NAME,
                        "1.2.3");
        ApplicationPreferences.setUp(preferences);
        pomDefaultOptions = new KiePOMDefaultOptions();
        pomBuilder = new POMBuilder();
    }

    @Test
    public void testDefaultVersion() throws Exception {
        assertEquals("1.0",
                     pomBuilder.build().getGav().getVersion());
    }

    @Test
    public void testDefaultPackaging() throws Exception {
        assertEquals("kjar",
                     pomBuilder.build().getPackaging());
    }

    @Test
    public void testContainsKieMavenPlugin() throws Exception {
        pomBuilder.setBuildPlugins(pomDefaultOptions.getBuildPlugins());

        List<Plugin> plugins = pomBuilder.build().getBuild().getPlugins();

        assertEquals(1,
                     plugins.size());

        assertEquals("org.kie",
                     plugins.get(0).getGroupId());
        assertEquals("kie-maven-plugin",
                     plugins.get(0).getArtifactId());
        assertEquals("1.2.3",
                     plugins.get(0).getVersion());
    }

    @Test
    public void testSetGAV() throws Exception {
        POM pom = pomBuilder
                .setModuleName("moduleName")
                .setGroupId("my.group")
                .setVersion("2.0")
                .build();

        assertEquals("moduleName",
                     pom.getName());
        assertEquals("moduleName",
                     pom.getGav().getArtifactId());
        assertEquals("my.group",
                     pom.getGav().getGroupId());
        assertEquals("2.0",
                     pom.getGav().getVersion());
    }

    @Test
    public void testSetName() throws Exception {
        POM pom = pomBuilder
                .setModuleName("module name with spaces!")
                .build();

        assertEquals("module name with spaces!",
                     pom.getName());
        assertEquals("modulenamewithspaces",
                     pom.getGav().getArtifactId());
    }
}