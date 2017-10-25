/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.project.context;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectContextTest {

    @Spy
    private EventSourceMock<ProjectContextChangeEvent> changeEvent = new EventSourceMock<ProjectContextChangeEvent>();

    private ProjectContext context;

    @Before
    public void setUp() throws Exception {
        context = new ProjectContext(changeEvent);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                context.onProjectContextChanged((ProjectContextChangeEvent) invocationOnMock.getArguments()[0]);
                return null;
            }
        }).when(changeEvent).fire(any(ProjectContextChangeEvent.class));
    }

    @Test
    public void testGetActiveRepositoryRoot() throws Exception {
        final Repository repository = mock(Repository.class);

        context.setActiveRepository(repository);
        context.setActiveBranch("dev");

        final Path devRoot = mock(Path.class);
        when(repository.getBranchRoot("dev")).thenReturn(devRoot);

        assertEquals(devRoot,
                     context.getActiveRepositoryRoot());
    }

    @Test
    public void testRepositoryDeleted() throws Exception {
        OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        GitRepository repository = new GitRepository();

        context.setActiveOrganizationalUnit(organizationalUnit);
        context.setActiveRepository(repository);

        RepositoryRemovedEvent repositoryRemovedEvent = new RepositoryRemovedEvent(repository);

        context.onRepositoryRemoved(repositoryRemovedEvent);

        assertEquals(organizationalUnit,
                     context.getActiveOrganizationalUnit());
        assertNull(context.getActiveRepository());
    }

    @Test
    public void testIgnoreRepositoryDeletedEventIfTheActiveRepositoryWasNotDeleted() throws Exception {

        GitRepository activeRepository = new GitRepository("active repo");
        GitRepository deletedRepository = new GitRepository("deleted repo");

        context.setActiveRepository(activeRepository);

        RepositoryRemovedEvent repositoryRemovedEvent = new RepositoryRemovedEvent(deletedRepository);

        context.onRepositoryRemoved(repositoryRemovedEvent);

        assertEquals(activeRepository,
                     context.getActiveRepository());
    }

    @Test
    public void testContextChanged() throws Exception {
        OrganizationalUnit oldOrganizationalUnit = mock(OrganizationalUnit.class);
        Repository oldRepository = mock(Repository.class);
        Package oldPackage = new Package();
        Project oldProject = new Project();

        context.setActiveOrganizationalUnit(oldOrganizationalUnit);
        context.setActiveRepository(oldRepository);
        context.setActivePackage(oldPackage);
        context.setActiveProject(oldProject);

        OrganizationalUnit newOrganizationalUnit = mock(OrganizationalUnit.class);
        Repository newRepository = mock(Repository.class);
        String newBranch = "master";
        Package newPackage = new Package();
        Project newProject = new Project();

        ProjectContextChangeHandler changeHandler = mock(ProjectContextChangeHandler.class);
        context.addChangeHandler(changeHandler);

        context.onProjectContextChanged(new ProjectContextChangeEvent(newOrganizationalUnit,
                                                                      newRepository,
                                                                      newBranch,
                                                                      newProject,
                                                                      newPackage));

        assertEquals(newOrganizationalUnit,
                     context.getActiveOrganizationalUnit());
        assertEquals(newRepository,
                     context.getActiveRepository());
        assertEquals(newProject,
                     context.getActiveProject());
        assertEquals(newPackage,
                     context.getActivePackage());
        verify(changeHandler).onChange();
    }

    @Test
    public void testContextChangeHandlerGetsRemoved() throws Exception {
        ProjectContextChangeHandler changeHandler = mock(ProjectContextChangeHandler.class);
        ProjectContextChangeHandle handle = context.addChangeHandler(changeHandler);

        context.onProjectContextChanged(new ProjectContextChangeEvent());

        verify(changeHandler).onChange();

        context.removeChangeHandler(handle);

        reset(changeHandler);

        context.onProjectContextChanged(new ProjectContextChangeEvent());

        verify(changeHandler,
               never()).onChange();
    }
}