/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.rest.backend;

import java.net.URI;
import java.util.HashSet;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.MavenRepositorySource;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JobRequestHelperCreateProjectTest {

    public SimpleFileSystemProvider fileSystemProvider;

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private ProjectService<? extends Project> projectService;

    @InjectMocks
    private JobRequestHelper jobRequestHelper = new JobRequestHelper();

    @Before
    public void setUp() throws Exception {
        fileSystemProvider = new SimpleFileSystemProvider();

        final Repository repository = mock(Repository.class);
        when(repositoryService.getRepository("myRepository")).thenReturn(repository);
        when(repository.getDefaultBranch()).thenReturn("master");
        final Path root = fileSystemProvider.getPath(URI.create("default://master@myRepository/"));
        when(repository.getBranchRoot("master")).thenReturn(Paths.convert(root));
    }

    @Test
    public void testRepositoryDoesNotExist() throws Exception {
        final JobResult jobResult = jobRequestHelper.createProject("jobId",
                                                                   "repositoryAlias",
                                                                   "projectName",
                                                                   "projectGroupId",
                                                                   "projectVersion",
                                                                   "projectDescription");

        assertEquals("jobId",
                     jobResult.getJobId());
        assertEquals(JobStatus.RESOURCE_NOT_EXIST,
                     jobResult.getStatus());
        assertEquals("Repository [repositoryAlias] does not exist",
                     jobResult.getResult());
    }

    @Test
    public void testRepositoryDoesExist() throws Exception {
        final JobResult jobResult = jobRequestHelper.createProject("jobId",
                                                                   "myRepository",
                                                                   "projectName",
                                                                   "projectGroupId",
                                                                   "projectVersion",
                                                                   "projectDescription");

        assertEquals("jobId",
                     jobResult.getJobId());
        assertEquals(JobStatus.SUCCESS,
                     jobResult.getStatus());
        assertNull(jobResult.getResult());
    }

    @Test
    public void testNewProjectWhenGAVAlreadyExists() throws Exception {

        final HashSet<MavenRepositoryMetadata> repositories = new HashSet<>();
        repositories.add(new MavenRepositoryMetadata("id",
                                                     "url",
                                                     MavenRepositorySource.LOCAL));

        doThrow(new GAVAlreadyExistsException(new GAV("projectGroupId:projectName:projectVersion"),
                                              repositories))
                .when(projectService).newProject(any(org.uberfire.backend.vfs.Path.class),
                                                 any(POM.class),
                                                 eq(JobRequestHelper.GUVNOR_BASE_URL));

        final JobResult jobResult = jobRequestHelper.createProject("jobId",
                                                                   "myRepository",
                                                                   "projectName",
                                                                   "projectGroupId",
                                                                   "projectVersion",
                                                                   "projectDescription");

        assertEquals("jobId",
                     jobResult.getJobId());
        assertEquals(JobStatus.DUPLICATE_RESOURCE,
                     jobResult.getStatus());
        assertEquals("Project's GAV [projectGroupId:projectName:projectVersion] already exists at [id : url : LOCAL ]",
                     jobResult.getResult());
    }

    @Test
    public void testNewProjectWhenFileAlreadyExists() throws Exception {

        doThrow(new FileAlreadyExistsException("myProject"))
                .when(projectService).newProject(any(org.uberfire.backend.vfs.Path.class),
                                                 any(POM.class),
                                                 eq(JobRequestHelper.GUVNOR_BASE_URL));

        final JobResult jobResult = jobRequestHelper.createProject("jobId",
                                                                   "myRepository",
                                                                   "myProject",
                                                                   "projectGroupId",
                                                                   "projectVersion",
                                                                   "projectDescription");

        assertEquals("jobId",
                     jobResult.getJobId());
        assertEquals(JobStatus.DUPLICATE_RESOURCE,
                     jobResult.getStatus());
        assertEquals("Project [myProject] already exists",
                     jobResult.getResult());
    }

    @Test
    public void testWeAreUsingCorrectGAV() throws Exception {

        jobRequestHelper.createProject("jobId",
                                       "myRepository",
                                       "myProject",
                                       "projectGroupId",
                                       "projectVersion",
                                       "projectDescription");

        ArgumentCaptor<POM> pomArgumentCaptor = ArgumentCaptor.forClass(POM.class);
        verify(projectService).newProject(any(org.uberfire.backend.vfs.Path.class),
                                          pomArgumentCaptor.capture(),
                                          eq(JobRequestHelper.GUVNOR_BASE_URL));

        final POM pom = pomArgumentCaptor.getValue();
        assertEquals("projectGroupId",
                     pom.getGav().getGroupId());
        assertEquals("myProject",
                     pom.getGav().getArtifactId());
        assertEquals("projectVersion",
                     pom.getGav().getVersion());
        assertEquals("myProject",
                     pom.getName());
        assertEquals("projectDescription",
                     pom.getDescription());
    }

    @Test
    public void testProjectGroupNull() throws Exception {
        jobRequestHelper.createProject("jobId",
                                       "myRepository",
                                       "myProject",
                                       null,
                                       "projectVersion",
                                       "projectDescription");

        ArgumentCaptor<POM> pomArgumentCaptor = ArgumentCaptor.forClass(POM.class);
        verify(projectService).newProject(any(org.uberfire.backend.vfs.Path.class),
                                          pomArgumentCaptor.capture(),
                                          eq(JobRequestHelper.GUVNOR_BASE_URL));

        final POM pom = pomArgumentCaptor.getValue();
        assertEquals("myProject",
                     pom.getGav().getGroupId());
    }

    @Test
    public void testProjectGroupEmpty() throws Exception {
        jobRequestHelper.createProject("jobId",
                                       "myRepository",
                                       "myProject",
                                       "             ",
                                       "projectVersion",
                                       "projectDescription");

        ArgumentCaptor<POM> pomArgumentCaptor = ArgumentCaptor.forClass(POM.class);
        verify(projectService).newProject(any(org.uberfire.backend.vfs.Path.class),
                                          pomArgumentCaptor.capture(),
                                          eq(JobRequestHelper.GUVNOR_BASE_URL));

        final POM pom = pomArgumentCaptor.getValue();
        assertEquals("myProject",
                     pom.getGav().getGroupId());
    }

    @Test
    public void testProjectVersionNull() throws Exception {

        jobRequestHelper.createProject("jobId",
                                       "myRepository",
                                       "myProject",
                                       "projectGroupId",
                                       null,
                                       "projectDescription");

        ArgumentCaptor<POM> pomArgumentCaptor = ArgumentCaptor.forClass(POM.class);
        verify(projectService).newProject(any(org.uberfire.backend.vfs.Path.class),
                                          pomArgumentCaptor.capture(),
                                          eq(JobRequestHelper.GUVNOR_BASE_URL));

        final POM pom = pomArgumentCaptor.getValue();
        assertEquals("1.0",
                     pom.getGav().getVersion());
    }

    @Test
    public void testProjectVersionEmpty() throws Exception {

        jobRequestHelper.createProject("jobId",
                                       "myRepository",
                                       "myProject",
                                       "projectGroupId",
                                       "               ",
                                       "projectDescription");

        ArgumentCaptor<POM> pomArgumentCaptor = ArgumentCaptor.forClass(POM.class);
        verify(projectService).newProject(any(org.uberfire.backend.vfs.Path.class),
                                          pomArgumentCaptor.capture(),
                                          eq(JobRequestHelper.GUVNOR_BASE_URL));

        final POM pom = pomArgumentCaptor.getValue();
        assertEquals("1.0",
                     pom.getGav().getVersion());
    }
}