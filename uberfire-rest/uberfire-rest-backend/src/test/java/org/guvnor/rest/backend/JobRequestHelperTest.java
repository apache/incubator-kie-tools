/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.rest.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.ModuleService;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.common.services.shared.test.Failure;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.guvnor.common.services.shared.test.TestRunnerService;
import org.guvnor.rest.client.CloneProjectRequest;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.spaces.Space;
import org.uberfire.spaces.SpacesAPI;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JobRequestHelperTest {

    @InjectMocks
    JobRequestHelper helper;
    @Mock
    Repository repository;
    @Captor
    ArgumentCaptor<Event<TestResultMessage>> captor;
    @Mock
    private TestRunnerService testService;
    @Mock
    private BuildService buildService;
    @Mock
    private RepositoryService repositoryService;
    @Mock
    private ModuleService<MyModule> moduleService;
    @Mock
    private WorkspaceProjectService workspaceProjectService;
    @Mock
    private WorkspaceProject workspaceProject;
    @Mock
    private SpacesAPI spaces;
    @Mock
    private OrganizationalUnitService organizationalUnitService;
    @Mock
    private SessionInfo sessionInfo;
    private Space space = new Space("space");

    @Before
    public void setUp() throws Exception {
        when(workspaceProjectService.resolveProject(eq(space), eq("project"))).thenReturn(workspaceProject);
        when(workspaceProjectService.resolveProject(eq(space), eq("project"), any())).thenReturn(workspaceProject);
        when(repositoryService.getRepositoryFromSpace(eq(space), eq("repositoryAlias"))).thenReturn(repository);
        when(spaces.getSpace(eq("space"))).thenReturn(space);
    }

    @Captor
    ArgumentCaptor<RepositoryEnvironmentConfigurations> repositoryEnvironmentConfigurationsCaptor;

    @Test
    public void cloneProject() throws Exception {

        doReturn(mock(OrganizationalUnit.class)).when(organizationalUnitService).getOrganizationalUnit("space");

        final CloneProjectRequest cloneProjectRequest = new CloneProjectRequest();
        cloneProjectRequest.setName("myName");
        helper.cloneProject("jobId",
                            "space",
                            cloneProjectRequest);

        verify(repositoryService).createRepository(
                any(),
                eq("git"),
                eq("myName"),
                repositoryEnvironmentConfigurationsCaptor.capture()
        );
        RepositoryEnvironmentConfigurations config = repositoryEnvironmentConfigurationsCaptor.getValue();
        assertEquals(false, config.getMirror());
        assertEquals(false, config.getInit());
    }

    @Test
    public void resourceDoesNotExist() throws Exception {
        final JobResult jobResult = helper.testProject(null,
                                                       null,
                                                       null,
                                                       null);
        assertEquals(JobStatus.RESOURCE_NOT_EXIST,
                     jobResult.getStatus());
    }

    @Test
    public void projectDoesNotExist() throws Exception {

        final Path path = mock(Path.class);
        when(path.getFileName()).thenReturn("");
        when(path.toURI()).thenReturn("file://project/");

        final Branch masterBranch = new Branch("master",
                                               path);

        when(repository.getDefaultBranch()).thenReturn(Optional.of(masterBranch));

        when(repository.getBranch("master")).thenReturn(Optional.of(masterBranch));

        final JobResult jobResult = helper.testProject(null,
                                                       space.getName(),
                                                       "project",
                                                       null);
        assertEquals(JobStatus.RESOURCE_NOT_EXIST,
                     jobResult.getStatus());
    }

    @Test
    public void allTestsPass() throws Exception {

        whenProjectExists();

        thenExpectMessageWithStatus(new TestResultMessage("JobRequestHelper",
                                                          10,
                                                          1000,
                                                          Collections.emptyList()),
                                    JobStatus.SUCCESS);
    }

    @Test
    public void testsFail() throws Exception {

        whenProjectExists();

        final ArrayList<Failure> failures = new ArrayList<>();
        failures.add(new Failure());

        thenExpectMessageWithStatus(new TestResultMessage("JobRequestHelper",
                                                          10,
                                                          1000,
                                                          failures),
                                    JobStatus.FAIL);
    }

    @Test
    public void testCompileProject() {
        whenProjectExists();

        BuildResults buildResults = mock(BuildResults.class);
        when(buildResults.getMessages()).thenReturn(new ArrayList());
        when(buildService.build(any())).thenReturn(buildResults);

        JobResult jobResult = helper.compileProject(null,
                                                    space.getName(),
                                                    "project",
                                                    null);

        assertEquals(JobStatus.SUCCESS,
                     jobResult.getStatus());
    }

    @Test
    public void testCompileProjectFail() {
        whenProjectExists();

        BuildResults buildResults = mock(BuildResults.class);
        when(buildResults.getMessages()).thenReturn(new ArrayList());
        when(buildResults.getErrorMessages()).thenReturn(Arrays.asList(mock(BuildMessage.class)));
        when(buildService.build(any())).thenReturn(buildResults);

        JobResult jobResult = helper.compileProject(null,
                                                    space.getName(),
                                                    "project",
                                                    null);

        assertEquals(JobStatus.FAIL,
                     jobResult.getStatus());
    }

    @Test
    public void testInstallProject() {
        whenProjectExists();

        doReturn(mock(OrganizationalUnit.class)).when(organizationalUnitService).getOrganizationalUnit("space");
        BuildResults buildResults = mock(BuildResults.class);
        when(buildResults.getGAV()).thenReturn(mock(GAV.class));
        when(buildService.buildAndDeploy(any())).thenReturn(buildResults);

        JobResult jobResult = helper.installProject("job123",
                                                    space.getName(),
                                                    "project",
                                                    null);

        assertEquals(JobStatus.SUCCESS,
                     jobResult.getStatus());
    }

    @Test
    public void testInstallProjectFail() {
        whenProjectExists();

        doReturn(mock(OrganizationalUnit.class)).when(organizationalUnitService).getOrganizationalUnit("space");
        BuildResults buildResults = mock(BuildResults.class);
        when(buildResults.getGAV()).thenReturn(mock(GAV.class));
        when(buildResults.getErrorMessages()).thenReturn(Arrays.asList(mock(BuildMessage.class)));
        when(buildService.buildAndDeploy(any())).thenReturn(buildResults);

        JobResult jobResult = helper.installProject("job123",
                                                    space.getName(),
                                                    "project",
                                                    null);

        assertEquals(JobStatus.FAIL,
                     jobResult.getStatus());
    }

    @Test
    public void testDeployProject() {
        whenProjectExists();

        BuildResults buildResults = mock(BuildResults.class);
        when(buildResults.getGAV()).thenReturn(mock(GAV.class));
        when(buildService.buildAndDeploy(any())).thenReturn(buildResults);

        JobResult jobResult = helper.deployProject(null,
                                                   space.getName(),
                                                   "project",
                                                   null);

        assertEquals(JobStatus.SUCCESS,
                     jobResult.getStatus());
    }

    @Test
    public void testDeployProjectFail() {
        whenProjectExists();

        BuildResults buildResults = mock(BuildResults.class);
        when(buildResults.getGAV()).thenReturn(mock(GAV.class));
        when(buildResults.getErrorMessages()).thenReturn(Arrays.asList(mock(BuildMessage.class)));
        when(buildService.buildAndDeploy(any())).thenReturn(buildResults);

        JobResult jobResult = helper.deployProject(null,
                                                   space.getName(),
                                                   "project",
                                                   null);

        assertEquals(JobStatus.FAIL,
                     jobResult.getStatus());
    }

    @Test
    public void testAddBranchProjectDoesNotExists() {
        when(workspaceProjectService.resolveProject(eq(space), eq("project"))).thenReturn(null);

        JobResult jobResult = helper.addBranch(null,
                                               space.getName(),
                                               "project",
                                               "new-branch",
                                               "ref-branch",
                                               "user");

        assertEquals(JobStatus.RESOURCE_NOT_EXIST,
                     jobResult.getStatus());
    }

    @Test
    public void testAddBranchFail() {
        doThrow(Exception.class).when(workspaceProjectService).addBranch(any(), any(), any(), any());

        JobResult jobResult = helper.addBranch(null,
                                               space.getName(),
                                               "project",
                                               "new-branch",
                                               "ref-branch",
                                               "user");

        assertEquals(JobStatus.FAIL,
                     jobResult.getStatus());
    }

    @Test
    public void testAddBranch() {
        JobResult jobResult = helper.addBranch(null,
                                               space.getName(),
                                               "project",
                                               "new-branch",
                                               "ref-branch",
                                               "user");

        assertEquals(JobStatus.SUCCESS,
                     jobResult.getStatus());
    }

    @Test
    public void testCreateSpace() {
        when(organizationalUnitService.isValidGroupId("org.space.newspace")).thenReturn(true);
        when(organizationalUnitService.createOrganizationalUnit(any(), any(), any(), any(), any())).thenReturn(mock(OrganizationalUnit.class));
        JobResult jobResult = helper.createSpace(null,
                                                 space.getName(),
                                                 "this is a new space",
                                                 "admin",
                                                 "org.space.newspace");

        assertEquals(JobStatus.SUCCESS,
                     jobResult.getStatus());
    }

    @Test
    public void testUpdateSpace() {
        when(organizationalUnitService.isValidGroupId("org.space.newspace")).thenReturn(true);
        when(organizationalUnitService.getOrganizationalUnit(space.getName())).thenReturn(mock(OrganizationalUnit.class));
        when(organizationalUnitService.updateOrganizationalUnit(anyString(), anyString(), any(), anyString())).thenReturn(mock(OrganizationalUnit.class));
        JobResult jobResult = helper.updateSpace(null,
                                                 space.getName(),
                                                 "this is a new space",
                                                 "admin",
                                                 "org.space.newspace");

        assertEquals(JobStatus.SUCCESS,
                     jobResult.getStatus());
    }

    @Test
    public void testRemoveSpace() {
        JobResult jobResult = helper.removeSpace(null,
                                                 space.getName());

        assertEquals(JobStatus.SUCCESS,
                     jobResult.getStatus());
    }

    @Test
    public void testRemoveBranchProjectDoesNotExists() {
        when(workspaceProjectService.resolveProject(eq(space), eq("project"))).thenReturn(null);

        JobResult jobResult = helper.removeBranch(null,
                                                  space.getName(),
                                                  "project",
                                                  "new-branch",
                                                  "user");

        assertEquals(JobStatus.RESOURCE_NOT_EXIST,
                     jobResult.getStatus());
    }

    @Test
    public void testRemoveBranchFail() {
        doThrow(Exception.class).when(workspaceProjectService).removeBranch(any(), any(), any());

        JobResult jobResult = helper.removeBranch(null,
                                                  space.getName(),
                                                  "project",
                                                  "new-branch",
                                                  "user");

        assertEquals(JobStatus.FAIL,
                     jobResult.getStatus());
    }

    @Test
    public void testRemoveBranch() {
        JobResult jobResult = helper.removeBranch(null,
                                                  space.getName(),
                                                  "project",
                                                  "new-branch",
                                                  "user");

        assertEquals(JobStatus.SUCCESS,
                     jobResult.getStatus());
    }

    private void thenExpectMessageWithStatus(final TestResultMessage message,
                                             final JobStatus status) {
        final JobResult jobResult = helper.testProject(null,
                                                       space.getName(),
                                                       "project",
                                                       null);

        verify(testService).runAllTests(eq("JobRequestHelper"),
                                        any(Path.class),
                                        captor.capture());

        captor.getValue().fire(message);

        assertEquals(status,
                     jobResult.getStatus());
    }

    private void whenProjectExists() {
        when(workspaceProject.getMainModule()).thenReturn(mock(MyModule.class));
    }

    class MyModule extends Module {

    }
}
