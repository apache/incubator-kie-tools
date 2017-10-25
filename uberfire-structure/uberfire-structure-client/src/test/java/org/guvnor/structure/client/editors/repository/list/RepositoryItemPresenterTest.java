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
import java.util.Collections;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.structure.client.editors.context.GuvnorStructureContext;
import org.guvnor.structure.client.security.RepositoryController;
import org.guvnor.structure.repositories.PublicURI;
import org.guvnor.structure.repositories.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class RepositoryItemPresenterTest {

    @Mock
    private GuvnorStructureContext guvnorStructureContext;

    @Mock
    private RepositoryController repositoryController;

    @Mock
    private EventSourceMock<NotificationEvent> notification;

    @Mock
    private RepositoryItemView view;

    private RepositoryItemPresenter presenter;

    @Mock
    private Repository repository;

    List<PublicURI> publicURIs = new ArrayList<PublicURI>();

    List<String> branches = new ArrayList<String>();

    @Mock
    PublicURI uri1;

    @Mock
    PublicURI uri2;

    @Before
    public void init() {

        //master branch always present.
        branches.add("master");

        when(uri1.getProtocol()).thenReturn("test-protocol1");
        when(uri1.getURI()).thenReturn("uri1");

        when(uri2.getProtocol()).thenReturn("test-protocol2");
        when(uri2.getURI()).thenReturn("uri2");

        presenter = new RepositoryItemPresenter(view,
                                                guvnorStructureContext,
                                                repositoryController,
                                                notification);
        when(repository.getAlias()).thenReturn("TestRepo");

        when(repositoryController.canUpdateRepository(repository)).thenReturn(false);
        when(repositoryController.canDeleteRepository(repository)).thenReturn(false);
    }

    @Test
    public void repositoryWithPublicUrisAndBranchesLoadTest() {
        publicURIs.add(uri1);
        publicURIs.add(uri2);
        branches.add("development");
        branches.add("release");
        repositoryLoadTest(publicURIs,
                           branches);
    }

    @Test
    public void repositoryWithPublicUrisAndNoBranchesLoadTest() {
        publicURIs.add(uri1);
        publicURIs.add(uri2);
        repositoryLoadTest(publicURIs,
                           branches);
    }

    @Test
    public void repositoryWithNoPublicUrisAndBranchesLoadTest() {
        branches.add("development");
        branches.add("release");
        repositoryLoadTest(publicURIs,
                           branches);
    }

    @Test
    public void repositoryWithNoPublicUrisAndNoBranchesLoadTest() {
        repositoryLoadTest(publicURIs,
                           branches);
    }

    private void repositoryLoadTest(List<PublicURI> uris,
                                    List<String> branches) {

        when(repository.getAlias()).thenReturn("TestRepo");
        when(repository.getPublicURIs()).thenReturn(publicURIs);
        when(repository.getBranches()).thenReturn(branches);

        presenter.setRepository(repository,
                                "master");

        verify(view).setPresenter(presenter);
        verify(view).setRepositoryName("TestRepo");
        verify(view).setUpdateEnabled(false);
        verify(view).setDeleteEnabled(false);

        //protocols configuration
        if (uris.size() > 0) {
            verify(view).showAvailableProtocols();

            verify(view).setDaemonURI(uris.get(0).getURI());
            for (PublicURI publicURI : uris) {
                verify(view).addProtocol(publicURI.getProtocol());
            }
        }

        verify(view).setUriId("view-uri-for-" + "TestRepo");

        //branches configuration
        verify(view).clearBranches();
        Collections.reverse(branches);
        for (String branch : branches) {
            verify(view).addBranch(branch);
        }
        verify(view).setSelectedBranch("master");
        verify(view).refresh();
    }

    @Test
    public void refreshBranchesTest() {

        branches.add("development");
        branches.add("release");

        //emulates development branch was selected at the moment of the refresh
        when(view.getSelectedBranch()).thenReturn("development");

        //check that the repository was loaded properly
        repositoryLoadTest(publicURIs,
                           branches);

        //now e.g. an additional branch was added to the repository.
        branches.add("NewBranch");

        presenter.refreshBranches();

        //at this point the view should have been refreshed a second time.
        verify(view,
               times(2)).clearBranches();
        //existing branches should have been added for a second time as part of the refresh.
        verify(view,
               times(2)).addBranch("master");
        verify(view,
               times(2)).addBranch("development");
        verify(view,
               times(2)).addBranch("release");
        //the new branch should have been loaded for the first time.
        verify(view,
               times(1)).addBranch("NewBranch");
        //the previously selected branch should have been set again.
        verify(view,
               times(1)).setSelectedBranch("development");
    }

    @Test
    public void testNotificationFiredWhenGitUriCopied() {
        presenter.onGitUrlCopied("uri");

        verify(notification,
               times(1)).fire(any(NotificationEvent.class));
    }
}