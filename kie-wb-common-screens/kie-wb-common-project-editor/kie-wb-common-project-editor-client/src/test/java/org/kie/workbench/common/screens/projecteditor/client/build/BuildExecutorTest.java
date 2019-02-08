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

package org.kie.workbench.common.screens.projecteditor.client.build;

import org.assertj.core.api.Assertions;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.BuildExecutionContext;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.BuildExecutionManager;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.BuildType;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BuildExecutorTest {

    private static final String GROUP = "groupId";
    private static final String ARTIFACT = "artifactId";
    private static final String VERSION = "version";

    private static final String SNAPSHOT = "-SNAPSHOT";

    @Mock
    private Repository repository;

    @Mock
    private KieModule module;

    @Mock
    private Path pomPath;

    @Mock
    private BuildExecutionManager buildExecutionManager;

    @Captor
    private ArgumentCaptor<BuildExecutionContext> contextCaptor;

    @Mock
    private WorkspaceProjectContext context;

    private BuildExecutor buildExecutor;

    @Before
    public void setup() {

        final POM pom = new POM(new GAV(GROUP, ARTIFACT, VERSION));

        when(context.getActiveWorkspaceProject()).thenReturn(Optional.of(new WorkspaceProject(mock(OrganizationalUnit.class),
                repository, new Branch("master", mock(Path.class)), module)));

        when(repository.getAlias()).thenReturn("repository");

        when(module.getModuleName()).thenReturn("module");
        when(module.getPomXMLPath()).thenReturn(pomPath);
        when(module.getPom()).thenReturn(pom);
        when(module.getRootPath()).thenReturn(mock(Path.class));
        when(pomPath.getFileName()).thenReturn("pom.xml");
        when(context.getActiveModule()).thenReturn(Optional.of(module));

        buildExecutor = spy(new BuildExecutor(context, buildExecutionManager));
    }

    @Test
    public void testDefaultBuild() {
        buildExecutor.triggerBuild();

        verify(buildExecutionManager).execute(eq(BuildType.BUILD), contextCaptor.capture());

        BuildExecutionContext context = contextCaptor.getValue();

        Assertions.assertThat(context)
                .returns(buildExecutor.defaultContainerId(), BuildExecutionContext::getContainerId)
                .returns(buildExecutor.defaultContainerAlias(), BuildExecutionContext::getContainerAlias)
                .returns(module, BuildExecutionContext::getModule);
    }

    @Test
    public void testDefaultInstall() {
        buildExecutor.triggerBuildAndInstall();

        verify(buildExecutionManager).execute(eq(BuildType.INSTALL), contextCaptor.capture());

        BuildExecutionContext context = contextCaptor.getValue();

        Assertions.assertThat(context)
                .returns(buildExecutor.defaultContainerId(), BuildExecutionContext::getContainerId)
                .returns(buildExecutor.defaultContainerAlias(), BuildExecutionContext::getContainerAlias)
                .returns(module, BuildExecutionContext::getModule);
    }

    @Test
    public void testDefaultBuildAndDeploy() {
        buildExecutor.triggerBuildAndDeploy();

        verify(buildExecutionManager).execute(eq(BuildType.DEPLOY), contextCaptor.capture());

        BuildExecutionContext context = contextCaptor.getValue();

        Assertions.assertThat(context)
                .returns(buildExecutor.defaultContainerId(), BuildExecutionContext::getContainerId)
                .returns(buildExecutor.defaultContainerAlias(), BuildExecutionContext::getContainerAlias)
                .returns(module, BuildExecutionContext::getModule);
    }

    @Test
    public void testSnapshotBuild() {
        module.getPom().getGav().setVersion(VERSION + SNAPSHOT);

        buildExecutor.triggerBuild();

        verify(buildExecutionManager).execute(eq(BuildType.BUILD), contextCaptor.capture());

        BuildExecutionContext context = contextCaptor.getValue();

        Assertions.assertThat(context)
                .returns(buildExecutor.defaultContainerId(), BuildExecutionContext::getContainerId)
                .returns(buildExecutor.defaultContainerAlias(), BuildExecutionContext::getContainerAlias)
                .returns(module, BuildExecutionContext::getModule);
    }

    @Test
    public void testSnapshotInstall() {
        module.getPom().getGav().setVersion(VERSION + SNAPSHOT);

        buildExecutor.triggerBuildAndInstall();

        verify(buildExecutionManager).execute(eq(BuildType.INSTALL), contextCaptor.capture());

        BuildExecutionContext context = contextCaptor.getValue();

        Assertions.assertThat(context)
                .returns(buildExecutor.defaultContainerId(), BuildExecutionContext::getContainerId)
                .returns(buildExecutor.defaultContainerAlias(), BuildExecutionContext::getContainerAlias)
                .returns(module, BuildExecutionContext::getModule);
    }

    @Test
    public void testSnapshotBuildAndDeploy() {
        module.getPom().getGav().setVersion(VERSION + SNAPSHOT);

        buildExecutor.triggerBuildAndDeploy();

        verify(buildExecutionManager).execute(eq(BuildType.DEPLOY), contextCaptor.capture());

        BuildExecutionContext context = contextCaptor.getValue();

        Assertions.assertThat(context)
                .returns(buildExecutor.defaultContainerId(), BuildExecutionContext::getContainerId)
                .returns(buildExecutor.defaultContainerAlias(), BuildExecutionContext::getContainerAlias)
                .returns(module, BuildExecutionContext::getModule);
    }

    @Test
    public void testSnapshotRedeploy() {
        module.getPom().getGav().setVersion(VERSION + SNAPSHOT);

        buildExecutor.triggerRedeploy();

        verify(buildExecutionManager).execute(eq(BuildType.REDEPLOY), contextCaptor.capture());

        BuildExecutionContext context = contextCaptor.getValue();

        Assertions.assertThat(context)
                .returns(buildExecutor.defaultContainerId(), BuildExecutionContext::getContainerId)
                .returns(buildExecutor.defaultContainerAlias(), BuildExecutionContext::getContainerAlias)
                .returns(module, BuildExecutionContext::getModule);
    }
}
