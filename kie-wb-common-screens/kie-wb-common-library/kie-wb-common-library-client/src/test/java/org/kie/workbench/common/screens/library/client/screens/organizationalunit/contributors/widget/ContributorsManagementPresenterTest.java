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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.ext.uberfire.social.activities.model.SocialUser;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.SessionInfoMock;
import org.uberfire.rpc.SessionInfo;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContributorsManagementPresenterTest {

    @Mock
    private ContributorsManagementPresenter.View view;

    @Mock
    private OrganizationalUnitService organizationalUnitService;
    private Caller<OrganizationalUnitService> organizationalUnitServiceCaller;

    @Mock
    private LibraryService libraryService;
    private Caller<LibraryService> libraryServiceCaller;

    @Mock
    private ManagedInstance<ContributorsManagementListItemPresenter> contributorsManagementListItemPresenters;

    private SessionInfo sessionInfo;

    @Mock
    private ContributorsManagementListItemPresenter contributorsManagementListItemPresenter;

    private ContributorsManagementPresenter presenter;

    private SocialUser userA = new SocialUser("admin");
    private SocialUser userB = new SocialUser("analyst");
    private SocialUser userC = new SocialUser("director");

    @Before
    public void setup() {
        organizationalUnitServiceCaller = new CallerMock<>(organizationalUnitService);
        libraryServiceCaller = new CallerMock<>(libraryService);
        sessionInfo = new SessionInfoMock();

        List<SocialUser> allUsers = new ArrayList<>();
        allUsers.add(userA);
        allUsers.add(userB);
        allUsers.add(userC);
        doReturn(allUsers).when(libraryService).getAllUsers();

        doReturn(contributorsManagementListItemPresenter).when(contributorsManagementListItemPresenters).get();

        presenter = new ContributorsManagementPresenter(view,
                                                        organizationalUnitServiceCaller,
                                                        libraryServiceCaller,
                                                        contributorsManagementListItemPresenters,
                                                        sessionInfo);
    }

    @Test
    public void setupTest() {
        final List<SocialUser> users = new ArrayList<>();
        users.add(userA);
        users.add(userB);

        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        doReturn(users.stream().map(SocialUser::getUserName).collect(Collectors.toList())).when(organizationalUnit).getContributors();
        doReturn(userB.getUserName()).when(organizationalUnit).getOwner();

        presenter.setup(organizationalUnit);

        verify(view).init(presenter);
        verify(view).clearFilter();
        verify(contributorsManagementListItemPresenter).setup(userA);
        verify(contributorsManagementListItemPresenter).setup(userB);
        verify(contributorsManagementListItemPresenter).setup(userC);
        verify(contributorsManagementListItemPresenter,
               times(3)).setSelected(true);
        verify(contributorsManagementListItemPresenter,
               times(2)).setEnabled(true);
        verify(contributorsManagementListItemPresenter).setEnabled(false);
        verify(view).clearUsers();
        verify(view,
               times(3)).addUser(contributorsManagementListItemPresenter);
    }
}
