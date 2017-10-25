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
import java.util.Collections;
import javax.enterprise.event.Event;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.common.services.shared.test.Failure;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.guvnor.common.services.shared.test.TestService;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.structure.repositories.Repository;
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

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JobRequestHelperTest {

    @InjectMocks
    JobRequestHelper helper;
    @Mock
    Repository repository;
    @Captor
    ArgumentCaptor<Event<TestResultMessage>> captor;
    @Mock
    private TestService testService;
    @Mock
    private RepositoryService repositoryService;
    @Mock
    private ProjectService<MyProject> projectService;

    @Before
    public void setUp() throws Exception {
        when(repositoryService.getRepository("repositoryAlias")).thenReturn(repository);
    }

    @Test
    public void resourceDoesNotExist() throws Exception {
        final JobResult jobResult = helper.testProject(null,
                                                       "repositoryAlias",
                                                       null);
        assertEquals(JobStatus.RESOURCE_NOT_EXIST,
                     jobResult.getStatus());
    }

    @Test
    public void projectDoesNotExist() throws Exception {
        when(repository.getDefaultBranch()).thenReturn("master");

        Path path = mock(Path.class);
        when(path.getFileName()).thenReturn("");
        when(path.toURI()).thenReturn("file://project/");

        when(repository.getBranchRoot("master")).thenReturn(path);

        final JobResult jobResult = helper.testProject(null,
                                                       "repositoryAlias",
                                                       "project");
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

    private void thenExpectMessageWithStatus(final TestResultMessage message,
                                             final JobStatus status) {
        final JobResult jobResult = helper.testProject(null,
                                                       "repositoryAlias",
                                                       "project");

        verify(testService).runAllTests(eq("JobRequestHelper"),
                                        any(Path.class),
                                        captor.capture());

        captor.getValue().fire(message);

        assertEquals(status,
                     jobResult.getStatus());
    }

    private void whenProjectExists() {
        when(repository.getDefaultBranch()).thenReturn("master");

        Path path = mock(Path.class);
        when(path.getFileName()).thenReturn("");
        when(path.toURI()).thenReturn("file://project/");

        when(repository.getBranchRoot("master")).thenReturn(path);

        when(projectService.resolveProject(any(Path.class))).thenReturn(mock(MyProject.class));
    }

    class MyProject extends Project {

    }
}