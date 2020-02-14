/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.archetype.mgmt.backend.maven;

import java.io.File;
import java.util.Collections;

import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.project.ProjectBuildingException;
import org.appformer.maven.integration.embedder.MavenEmbedder;
import org.appformer.maven.integration.embedder.MavenEmbedderException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class BuildProjectCommandTest {

    private static final String BASE_DIRECTORY = "baseDirectory";

    @Test(expected = UnsupportedOperationException.class)
    public void buildMavenRequestTest() {
        final BuildProjectCommand command = new BuildProjectCommand(BASE_DIRECTORY);

        command.buildMavenRequest();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void buildUserPropertiesTest() {
        final BuildProjectCommand command = new BuildProjectCommand(BASE_DIRECTORY);

        command.buildUserProperties();
    }

    @Test
    public void executeSuccessTest() throws MavenEmbedderException, ProjectBuildingException {
        final BuildProjectCommand command = spy(new BuildProjectCommand(BASE_DIRECTORY));

        final MavenEmbedder mavenEmbedder = mock(MavenEmbedder.class);
        doReturn(Collections.emptyList()).when(mavenEmbedder).buildProjects(any(File.class), eq(false));
        doReturn(mavenEmbedder).when(command).createMavenEmbedder();

        final MavenExecutionResult executionResult = command.execute();

        assertFalse(executionResult.hasExceptions());
    }

    @Test
    public void executeFailedTest() throws MavenEmbedderException, ProjectBuildingException {
        final BuildProjectCommand command = spy(new BuildProjectCommand(BASE_DIRECTORY));

        final MavenEmbedder mavenEmbedder = mock(MavenEmbedder.class);
        doThrow(ProjectBuildingException.class).when(mavenEmbedder).buildProjects(any(File.class), eq(false));
        doReturn(mavenEmbedder).when(command).createMavenEmbedder();

        final MavenExecutionResult executionResult = command.execute();

        assertTrue(executionResult.hasExceptions());
    }
}
