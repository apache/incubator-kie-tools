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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.tab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.promise.Promises;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.promise.SyncPromises;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContributorsListItemPresenterTest {

    @Mock
    private ContributorsListItemPresenter.View view;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    private ContributorsListService contributorsListService;

    @Captor
    private ArgumentCaptor<List<Contributor>> contributorsArgumentCaptor;

    private Promises promises;

    private ContributorsListItemPresenter presenter;

    private Contributor persistedContributor;

    @Before
    public void setup() {
        promises = new SyncPromises();
        presenter = new ContributorsListItemPresenter(view,
                                                      libraryPlaces,
                                                      notificationEvent,
                                                      promises);

        persistedContributor = new Contributor("admin", ContributorType.OWNER);
        doAnswer(invocationOnMock -> {
            final List<Contributor> contributors = new ArrayList<>();
            contributors.add(persistedContributor);
            invocationOnMock.getArgumentAt(0, Consumer.class).accept(contributors);
            return null;
        }).when(contributorsListService).getContributors(any());
    }

    @Test
    public void setupNewTest() {
        presenter.setupNew(mock(ContributorsListPresenter.class),
                           mock(ContributorsListService.class));

        verify(view).init(presenter);
        verify(view).setupAddMode();
    }

    @Test
    public void setupTest() {
        final Contributor contributor = mock(Contributor.class);
        presenter.setup(contributor,
                        mock(ContributorsListPresenter.class),
                        mock(ContributorsListService.class));

        verify(view).init(presenter);
        verify(view).setupViewMode(contributor);
    }

    @Test
    public void editTest() {
        final ContributorsListPresenter parentPresenter = mock(ContributorsListPresenter.class);
        presenter.setup(mock(Contributor.class),
                        parentPresenter,
                        mock(ContributorsListService.class));
        presenter.edit();

        verify(parentPresenter).itemIsBeingEdited();
        verify(view).editMode();
    }

    @Test
    public void saveNewContributorTest() {
        doReturn("newContributor").when(view).getName();
        doReturn(ContributorType.CONTRIBUTOR).when(view).getRole();

        final ContributorsListPresenter parentPresenter = mock(ContributorsListPresenter.class);
        doReturn(Arrays.asList("admin", "newContributor")).when(parentPresenter).getValidUsernames();

        doReturn(promises.resolve(true)).when(contributorsListService).canEditContributors(any(), any());

        presenter.setupNew(parentPresenter,
                           contributorsListService);
        presenter.save();

        verify(contributorsListService).saveContributors(contributorsArgumentCaptor.capture(),
                                                         any(),
                                                         any());
        final List<Contributor> savedContributors = contributorsArgumentCaptor.getValue();
        assertEquals(2, savedContributors.size());
        assertEquals("admin", savedContributors.get(0).getUsername());
        assertEquals(ContributorType.OWNER, savedContributors.get(0).getType());
        assertEquals("newContributor", savedContributors.get(1).getUsername());
        assertEquals(ContributorType.CONTRIBUTOR, savedContributors.get(1).getType());
    }

    @Test
    public void saveExistentContributorTest() {
        doReturn("newName").when(view).getName();
        doReturn(ContributorType.OWNER).when(view).getRole();

        final ContributorsListPresenter parentPresenter = mock(ContributorsListPresenter.class);
        doReturn(Arrays.asList("admin", "newName")).when(parentPresenter).getValidUsernames();

        doReturn(promises.resolve(true)).when(contributorsListService).canEditContributors(any(), any());

        presenter.setup(persistedContributor,
                        parentPresenter,
                        contributorsListService);
        presenter.save();

        verify(contributorsListService).saveContributors(contributorsArgumentCaptor.capture(),
                                                         any(),
                                                         any());
        final List<Contributor> savedContributors = contributorsArgumentCaptor.getValue();
        assertEquals(1, savedContributors.size());
        assertEquals("newName", savedContributors.get(0).getUsername());
        assertEquals(ContributorType.OWNER, savedContributors.get(0).getType());
    }

    @Test
    public void saveEmptyContributorTest() {
        doReturn("").when(view).getName();
        doReturn(ContributorType.OWNER).when(view).getRole();

        final ContributorsListPresenter parentPresenter = mock(ContributorsListPresenter.class);
        doReturn(Arrays.asList("admin")).when(parentPresenter).getValidUsernames();

        doReturn(promises.resolve(true)).when(contributorsListService).canEditContributors(any(), any());

        presenter.setup(persistedContributor,
                        parentPresenter,
                        contributorsListService);
        presenter.save();

        verify(contributorsListService, never()).saveContributors(any(), any(), any());
    }

    @Test
    public void saveNonUserWhenValidUserNotRequiredContributorTest() {
        doReturn("notUser").when(view).getName();
        doReturn(ContributorType.OWNER).when(view).getRole();

        final ContributorsListPresenter parentPresenter = mock(ContributorsListPresenter.class);
        doReturn(Arrays.asList("admin")).when(parentPresenter).getValidUsernames();

        doReturn(false).when(contributorsListService).requireValidUsername();
        doReturn(promises.resolve(true)).when(contributorsListService).canEditContributors(any(), any());

        presenter.setup(persistedContributor,
                        parentPresenter,
                        contributorsListService);
        presenter.save();

        verify(contributorsListService).saveContributors(any(), any(), any());
    }

    @Test
    public void saveNonUserWhenValidUserRequiredContributorTest() {
        doReturn("notUser").when(view).getName();
        doReturn(ContributorType.OWNER).when(view).getRole();

        final ContributorsListPresenter parentPresenter = mock(ContributorsListPresenter.class);
        doReturn(Arrays.asList("admin")).when(parentPresenter).getValidUsernames();

        doReturn(true).when(contributorsListService).requireValidUsername();
        doReturn(promises.resolve(true)).when(contributorsListService).canEditContributors(any(), any());

        presenter.setup(persistedContributor,
                        parentPresenter,
                        contributorsListService);
        presenter.save();

        verify(contributorsListService, never()).saveContributors(any(), any(), any());
    }

    @Test
    public void removeTest() {
        final Contributor contributor1 = new Contributor("admin", ContributorType.OWNER);
        final Contributor contributor2 = new Contributor("other", ContributorType.CONTRIBUTOR);
        final ContributorsListService contributorsListService = mock(ContributorsListService.class);

        doAnswer(invocationOnMock -> {
            final List<Contributor> contributors = new ArrayList<>();
            contributors.add(contributor1);
            contributors.add(contributor2);
            invocationOnMock.getArgumentAt(0, Consumer.class).accept(contributors);
            return null;
        }).when(contributorsListService).getContributors(any());

        final ContributorsListPresenter parentPresenter = mock(ContributorsListPresenter.class);
        doReturn(promises.resolve(true)).when(parentPresenter).canEditContributors(any());

        presenter.setup(contributor2,
                        parentPresenter,
                        contributorsListService);
        presenter.remove();

        verify(view).showBusyIndicator(anyString());
        verify(contributorsListService).saveContributors(any(), any(), any());
    }

    @Test
    public void removeLastOwnerTest() {
        final Contributor contributor = new Contributor("admin", ContributorType.OWNER);
        final ContributorsListService contributorsListService = mock(ContributorsListService.class);

        doAnswer(invocationOnMock -> {
            invocationOnMock.getArgumentAt(0, Consumer.class).accept(Collections.singletonList(contributor));
            return null;
        }).when(contributorsListService).getContributors(any());

        final ContributorsListPresenter parentPresenter = mock(ContributorsListPresenter.class);
        doReturn(promises.resolve(true)).when(parentPresenter).canEditContributors(any());

        presenter.setup(contributor,
                        parentPresenter,
                        contributorsListService);
        presenter.remove();

        verify(notificationEvent).fire(any());
        verify(view, never()).showBusyIndicator(anyString());
    }

    @Test
    public void cancelPersistedContributorTest() {
        final Contributor contributor = mock(Contributor.class);
        final ContributorsListPresenter parentPresenter = mock(ContributorsListPresenter.class);
        presenter.setup(contributor,
                        parentPresenter,
                        mock(ContributorsListService.class));
        presenter.cancel();

        verify(view, times(2)).setupViewMode(contributor);
        verify(view, never()).removeContributor();
        verify(parentPresenter).itemIsNotBeingEdited();
    }

    @Test
    public void cancelNotPersistedContributorTest() {
        final ContributorsListPresenter parentPresenter = mock(ContributorsListPresenter.class);
        presenter.setupNew(parentPresenter,
                           mock(ContributorsListService.class));
        presenter.cancel();

        verify(view, never()).setupViewMode(any());
        verify(view).removeContributor();
        verify(parentPresenter).itemIsNotBeingEdited();
    }
}
