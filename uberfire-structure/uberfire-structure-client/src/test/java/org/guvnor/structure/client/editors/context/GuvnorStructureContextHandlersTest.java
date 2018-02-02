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

package org.guvnor.structure.client.editors.context;

import java.util.HashMap;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.NewRepositoryEvent;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GuvnorStructureContextHandlersTest {

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private WorkspaceProjectContext projContext;

    private GuvnorStructureContext context;

    @Before
    public void setUp() throws Exception {
        context = new GuvnorStructureContext(new CallerMock<>(repositoryService), projContext);
    }

    @Test
    public void testHandler() throws Exception {
        final GuvnorStructureContextChangeHandler handler1 = mock(GuvnorStructureContextChangeHandler.class);
        final GuvnorStructureContextChangeHandler handler2 = mock(GuvnorStructureContextChangeHandler.class);

        final GuvnorStructureContextChangeHandler.HandlerRegistration handlerRegistration1 = context.addGuvnorStructureContextChangeHandler(handler1);
        final GuvnorStructureContextChangeHandler.HandlerRegistration handlerRegistration2 = context.addGuvnorStructureContextChangeHandler(handler2);

        assertNotNull(handlerRegistration1);
        assertNotNull(handlerRegistration2);

        context.removeHandler(handlerRegistration2);

        final GitRepository newRepository = new GitRepository();

        context.onRepositoryRemoved(new RepositoryRemovedEvent(newRepository));

        verify(handler1).onRepositoryDeleted(newRepository);
        verify(handler2,
               never()).onRepositoryDeleted(newRepository);
    }

    @Test
    public void testNewRepository() throws Exception {
        final GuvnorStructureContextChangeHandler handler = mock(GuvnorStructureContextChangeHandler.class);

        context.addGuvnorStructureContextChangeHandler(handler);

        final GitRepository newRepository = new GitRepository();
        final HashMap<String, Branch> branches = new HashMap<>();
        branches.put("master",
                     new Branch("master",
                                mock(Path.class)));
        newRepository.setBranches(branches);

        context.onNewRepository(new NewRepositoryEvent(newRepository));

        verify(handler).onNewRepositoryAdded(newRepository);
    }

    @Test
    public void testBranchChange() throws Exception {
        final GuvnorStructureContextBranchChangeHandler handler = mock(GuvnorStructureContextBranchChangeHandler.class);

        context.addGuvnorStructureContextBranchChangeHandler(handler);

        context.changeBranch("your-repo",
                             "hello");

        verify(handler).onBranchChange("your-repo",
                                       "hello");
    }

    @Test
    public void testRemoveBranchChangeHandler() throws Exception {
        final GuvnorStructureContextBranchChangeHandler handler1 = mock(GuvnorStructureContextBranchChangeHandler.class);
        final GuvnorStructureContextBranchChangeHandler handler2 = mock(GuvnorStructureContextBranchChangeHandler.class);

        final GuvnorStructureContextBranchChangeHandler.HandlerRegistration handlerRegistration1 = context.addGuvnorStructureContextBranchChangeHandler(handler1);
        final GuvnorStructureContextBranchChangeHandler.HandlerRegistration handlerRegistration2 = context.addGuvnorStructureContextBranchChangeHandler(handler2);

        assertNotNull(handlerRegistration1);
        assertNotNull(handlerRegistration2);

        context.removeHandler(handlerRegistration1);

        context.changeBranch("my-repo",
                             "master");

        verify(handler1,
               never()).onBranchChange("my-repo",
                                       "master");
        verify(handler2).onBranchChange("my-repo",
                                        "master");
    }
}