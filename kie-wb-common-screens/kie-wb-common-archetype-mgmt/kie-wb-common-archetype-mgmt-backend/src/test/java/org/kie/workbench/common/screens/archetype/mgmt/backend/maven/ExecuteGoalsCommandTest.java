/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.nio.file.FileSystems;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.appformer.maven.integration.embedder.MavenEmbedder;
import org.appformer.maven.integration.embedder.MavenEmbedderException;
import org.appformer.maven.integration.embedder.MavenRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class ExecuteGoalsCommandTest {

    private static final String BASE_DIRECTORY = "baseDirectory";

    @Test(expected = IllegalArgumentException.class)
    public void invalidBaseDirectoryTest() {
        new ExecuteGoalsCommand(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidGoalsTest() {
        new ExecuteGoalsCommand("baseDirectory",
                                Collections.emptyList());
    }

    @Test
    public void useDefaultGoalsTest() {
        final ExecuteGoalsCommand command = new ExecuteGoalsCommand(BASE_DIRECTORY);

        final MavenRequest request = command.buildMavenRequest();

        assertThat(request.getGoals()).isEqualTo(ExecuteGoalsCommand.DEFAULT_GOALS);
    }

    @Test
    public void useCustomGoalsTest() {
        final List<String> customGoals = Collections.singletonList("compile");

        final ExecuteGoalsCommand command = new ExecuteGoalsCommand(BASE_DIRECTORY,
                                                                    customGoals);

        final MavenRequest request = command.buildMavenRequest();

        assertThat(request.getGoals()).isEqualTo(customGoals);
    }

    @Test
    public void buildMavenRequestTest() {
        final String pomPath = BASE_DIRECTORY + FileSystems.getDefault().getSeparator() + ExecuteGoalsCommand.POM_XML;
        final ExecuteGoalsCommand command = new ExecuteGoalsCommand(BASE_DIRECTORY);

        final MavenRequest request = command.buildMavenRequest();

        assertThat(request.getPom()).isEqualTo(pomPath);
        assertThat(request.getGoals()).isEqualTo(ExecuteGoalsCommand.DEFAULT_GOALS);
    }

    @Test
    public void buildUserPropertiesTest() {
        final ExecuteGoalsCommand command = new ExecuteGoalsCommand(BASE_DIRECTORY);

        final Properties properties = command.buildUserProperties();

        assertThat(properties).isEqualTo(new Properties());
    }

    @Test
    public void prepareExecutionTest() {
        final String pomPath = BASE_DIRECTORY + FileSystems.getDefault().getSeparator() + ExecuteGoalsCommand.POM_XML;
        final ExecuteGoalsCommand command = new ExecuteGoalsCommand(BASE_DIRECTORY);

        final MavenRequest request = command.buildMavenRequest();

        assertThat(request.getPom()).isEqualTo(pomPath);
        assertThat(request.getGoals()).isEqualTo(ExecuteGoalsCommand.DEFAULT_GOALS);

        final MavenRequest finalRequest = command.prepareExecution();

        final Properties properties = command.buildUserProperties();

        assertThat(finalRequest.getBaseDirectory()).isEqualTo(BASE_DIRECTORY);
        assertThat(finalRequest.getLoggingLevel()).isEqualTo(MavenExecutionRequest.LOGGING_LEVEL_ERROR);
        assertThat(finalRequest.getUserProperties()).isEqualTo(properties);
    }

    @Test
    public void executeTest() throws MavenEmbedderException {
        final ExecuteGoalsCommand command = spy(new ExecuteGoalsCommand(BASE_DIRECTORY));

        final MavenRequest mavenRequest = mock(MavenRequest.class);
        doReturn(mavenRequest).when(command).prepareExecution();

        final MavenEmbedder mavenEmbedder = mock(MavenEmbedder.class);
        final MavenExecutionResult result = mock(MavenExecutionResult.class);
        doReturn(result).when(mavenEmbedder).execute(mavenRequest);
        doReturn(mavenEmbedder).when(command).createMavenEmbedder();

        final MavenExecutionResult executionResult = command.execute();

        assertSame(result, executionResult);
    }

    @Test
    public void createMavenEmbedderTest() throws MavenEmbedderException {
        final ExecuteGoalsCommand command = new ExecuteGoalsCommand(BASE_DIRECTORY);

        final MavenEmbedder mavenEmbedder = command.createMavenEmbedder();

        assertFalse(mavenEmbedder.getMavenExecutionRequest().isOffline());
    }
}