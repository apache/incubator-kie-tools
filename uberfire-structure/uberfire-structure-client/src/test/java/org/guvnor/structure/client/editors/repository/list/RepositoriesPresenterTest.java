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

package org.guvnor.structure.client.editors.repository.list;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.structure.client.editors.context.GuvnorStructureContext;
import org.guvnor.structure.client.editors.context.GuvnorStructureContextChangeHandler;
import org.guvnor.structure.client.security.RepositoryController;
import org.guvnor.structure.repositories.NewBranchEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class RepositoriesPresenterTest {

    private RepositoriesPresenter presenter;

    @Mock
    private RepositoriesView view;

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private RepositoryController repositoryController;

    @Mock
    private EventSourceMock<NotificationEvent> notification;

    private GuvnorStructureContext guvnorStructureContext;

    private Collection<Repository> repositories;

    @Mock
    private Path branchPath;

    private Repository r1 = createRepository("r1");
    private Repository r2 = createRepository("r2");
    private Repository r3 = createRepository("r3");
    private Repository r4 = createRepository("r4");

    @Mock
    private RepositoryItemView itemView1;
    @Mock
    private RepositoryItemView itemView2;
    @Mock
    private RepositoryItemView itemView3;
    @Mock
    private RepositoryItemView itemView4;

    private RepositoryItemPresenter itemPresenter1;
    private RepositoryItemPresenter itemPresenter2;
    private RepositoryItemPresenter itemPresenter3;
    private RepositoryItemPresenter itemPresenter4;

    private GuvnorStructureContextChangeHandler changeHandler;

    private GuvnorStructureContextChangeHandler.HandlerRegistration changeHandlerRegistration;

    private GuvnorStructureContextChangeHandler.HandlerRegistration removedChangeHandlerRegistration;

    @Before
    public void init() {
        repositories = new ArrayList<>();

        repositories.add(r1);
        repositories.add(r2);
        repositories.add(r3);

        when(repositoryController.canReadRepository(any())).thenReturn(true);

        this.guvnorStructureContext = new GuvnorStructureContext(new CallerMock<RepositoryService>(
                repositoryService)) {
            @Override
            public GuvnorStructureContextChangeHandler.HandlerRegistration addGuvnorStructureContextChangeHandler(
                    GuvnorStructureContextChangeHandler handler) {
                //remember the handler that was added to the context.
                changeHandler = handler;
                //remember the registration.
                changeHandlerRegistration = super.addGuvnorStructureContextChangeHandler(handler);
                return changeHandlerRegistration;
            }

            @Override
            public void removeHandler(GuvnorStructureContextChangeHandler.HandlerRegistration handlerRegistration) {
                //remember the removed registration.
                removedChangeHandlerRegistration = handlerRegistration;
                super.removeHandler(handlerRegistration);
            }
        };

        itemPresenter1 = createItemPresenter(itemView1,
                                             guvnorStructureContext,
                                             r1,
                                             "master");
        itemPresenter2 = createItemPresenter(itemView2,
                                             guvnorStructureContext,
                                             r2,
                                             "master");
        itemPresenter3 = createItemPresenter(itemView3,
                                             guvnorStructureContext,
                                             r3,
                                             "master");
        itemPresenter4 = createItemPresenter(itemView4,
                                             guvnorStructureContext,
                                             r4,
                                             "master");

        presenter = new RepositoriesPresenter(view,
                                              guvnorStructureContext,
                                              new CallerMock<>(repositoryService),
                                              repositoryController);

        when(repositoryService.getRepositories()).thenReturn(repositories);

        when(view.addRepository(r1,
                                r1.getDefaultBranch())).thenReturn(itemPresenter1);
        when(view.addRepository(r2,
                                r2.getDefaultBranch())).thenReturn(itemPresenter2);
        when(view.addRepository(r3,
                                r3.getDefaultBranch())).thenReturn(itemPresenter3);
        when(view.addRepository(r4,
                                r4.getDefaultBranch())).thenReturn(itemPresenter4);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(final InvocationOnMock invocationOnMock) throws Throwable {
                throw new RuntimeException("Should remove a valid repository item.");
            }
        }).when(view).removeIfExists(eq((RepositoryItemPresenter) null));
    }

    @Test
    public void testSetHandlers() throws Exception {
        //the presenter should have been added as a change handler.
        assertTrue(changeHandler == presenter);
    }

    @Test
    public void testRemoveHandlers() throws Exception {
        assertTrue(removedChangeHandlerRegistration == null);
        presenter.shutdown();
        //after the shutdown the change handler registration should have been removed.
        assertTrue(changeHandlerRegistration == removedChangeHandlerRegistration);
    }

    @Test
    public void removeIfExistsTest() {
        when(view.confirmDeleteRepository(r1)).thenReturn(true);
        when(view.confirmDeleteRepository(r3)).thenReturn(false);

        presenter.onStartup();

        presenter.removeRepository(r1);
        presenter.removeRepository(r3);

        verify(repositoryService).removeRepository("r1");
        verify(repositoryService,
               never()).removeRepository("r2");
        verify(repositoryService,
               never()).removeRepository("r3");
    }

    @Test
    public void addAndRemoveTest() {
        when(view.confirmDeleteRepository(r4)).thenReturn(true);
        presenter.onStartup();

        presenter.onNewRepositoryAdded(r4);
        presenter.removeRepository(r4);
        presenter.onRepositoryDeleted(r4);

        verify(repositoryService).removeRepository("r4");
    }

    @Test
    public void testNewBranchTest() {

        presenter.onStartup();

        //Emulates the master branch was selected for the given repository prior the new branch was created.
        when(itemView1.getSelectedBranch()).thenReturn("master");

        //Emulates the context receiving the new branch event for a branch created in r1.
        guvnorStructureContext.onNewBranch(new NewBranchEvent("r1",
                                                              "theNewBranch",
                                                              branchPath,
                                                              System.currentTimeMillis()));

        //Verify that the view was properly populated including the new branch.
        //one time at initialization + one time when the new branch was loaded.
        verify(itemView1,
               times(2)).addBranch("master");
        //only one invocation when the new branch was loaded.
        verify(itemView1).addBranch("theNewBranch");
        //one time during initialization + one time when the new branch was loaded
        verify(itemView1,
               times(2)).setSelectedBranch("master");
        //the new branch should have been added to the repository.
        assertTrue(r1.getBranches().contains("theNewBranch"));
    }

    private RepositoryItemPresenter createItemPresenter(RepositoryItemView itemView,
                                                        GuvnorStructureContext guvnorStructureContext,
                                                        Repository repository,
                                                        String branch) {
        //reproduces the initialization of the RepositoryItems performed by the view.
        RepositoryItemPresenter itemPresenter = new RepositoryItemPresenter(itemView,
                                                                            guvnorStructureContext,
                                                                            repositoryController,
                                                                            notification);
        itemPresenter.setRepository(repository,
                                    branch);
        return itemPresenter;
    }

    private Repository createRepository(String alias) {
        GitRepository repository = new GitRepository(alias);
        repository.addBranch("master",
                             branchPath);
        return repository;
    }
}