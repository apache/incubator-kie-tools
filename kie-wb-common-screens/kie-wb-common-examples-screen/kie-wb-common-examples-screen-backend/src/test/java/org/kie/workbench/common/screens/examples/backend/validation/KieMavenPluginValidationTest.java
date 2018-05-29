/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.kie.workbench.common.screens.examples.backend.validation;

import java.util.ArrayList;
import java.util.Optional;

import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Plugin;
import org.guvnor.common.services.project.service.POMService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.model.ExampleProject;
import org.kie.workbench.common.screens.examples.model.ExampleProjectError;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class KieMavenPluginValidationTest {

    private KieMavenPluginValidator validator;

    @Mock
    private POMService pomService;

    @Mock
    private ExampleProject exampleProject;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private POM pom;

    @Before
    public void setUp() {
        Path path = mock(Path.class);
        when(path.toURI()).thenReturn("/");
        when(this.exampleProject.getRoot()).thenReturn(path);
        when(this.pomService.load(any())).thenReturn(this.pom);

        this.validator = new KieMavenPluginValidator(this.pomService);
        validator.kieGroupId = "org.kie";
        validator.kieArtifactId = "kie-maven-plugin";
        validator.kiePluginExample = "example";
    }

    @Test
    public void testContainsKieMavenPlugin() {

        ArrayList<Plugin> plugins = new ArrayList<>();
        plugins.add(createPlugin(this.validator.kieGroupId,
                                 this.validator.kieArtifactId));

        when(this.pom.getBuild().getPlugins()).thenReturn(plugins);
        Optional<ExampleProjectError> error = this.validator.validate(exampleProject);
        assertFalse(error.isPresent());
    }

    @Test
    public void testContainsNoPlugins() {

        ArrayList<Plugin> plugins = new ArrayList<>();

        when(this.pom.getBuild().getPlugins()).thenReturn(plugins);
        Optional<ExampleProjectError> error = this.validator.validate(exampleProject);
        assertTrue(error.isPresent());
        assertEquals(KieMavenPluginValidator.class.getCanonicalName(),
                     error.get().getId());
    }

    @Test
    public void testDoesNotContainKieMavenPlugin() {

        ArrayList<Plugin> plugins = new ArrayList<>();
        plugins.add(createPlugin("org.another.group",
                                 "another-maven-plugin"));

        when(this.pom.getBuild().getPlugins()).thenReturn(plugins);
        Optional<ExampleProjectError> error = this.validator.validate(exampleProject);
        assertTrue(error.isPresent());
        assertEquals(KieMavenPluginValidator.class.getCanonicalName(),
                     error.get().getId());
    }

    @Test
    public void testDoesNotContainBuildSection() {
        when(this.pom.getBuild()).thenReturn(null);
        Optional<ExampleProjectError> error = this.validator.validate(exampleProject);
        assertTrue(error.isPresent());
        assertEquals(KieMavenPluginValidator.class.getCanonicalName(),
                     error.get().getId());
    }

    @Test
    public void testContainsEmptyBuildSection() {
        ArrayList<Plugin> plugins = new ArrayList<>();
        when(this.pom.getBuild().getPlugins()).thenReturn(plugins);
        Optional<ExampleProjectError> error = this.validator.validate(exampleProject);
        assertTrue(error.isPresent());
        assertEquals(KieMavenPluginValidator.class.getCanonicalName(),
                     error.get().getId());
    }

    private Plugin createPlugin(String groupId,
                                String artifactId) {
        Plugin plugin = mock(Plugin.class);
        when(plugin.getGroupId()).thenReturn(groupId);
        when(plugin.getArtifactId()).thenReturn(artifactId);
        return plugin;
    }
}