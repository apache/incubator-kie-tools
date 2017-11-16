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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.edit;

import java.util.ArrayList;

import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.guvnor.structure.events.AfterEditOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.widget.ContributorsManagementPresenter;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EditContributorsPopUpPresenterTest {

    @Mock
    private EditContributorsPopUpPresenter.View view;

    @Mock
    private ContributorsManagementPresenter contributorsManagementPresenter;

    @Mock
    private EventSourceMock<AfterEditOrganizationalUnitEvent> afterEditOrganizationalUnitEvent;

    @Mock
    private EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    private OrganizationalUnitController organizationalUnitController;

    @Mock
    private OrganizationalUnitService organizationalUnitService;
    private Caller<OrganizationalUnitService> organizationalUnitServiceCaller;

    private EditContributorsPopUpPresenter presenter;

    @Before
    public void setup() {
        doReturn(true).when(organizationalUnitController).canReadOrgUnits();
        doReturn(true).when(organizationalUnitController).canReadOrgUnit(any());
        doReturn(true).when(organizationalUnitController).canUpdateOrgUnit(any());
        doReturn(true).when(organizationalUnitController).canCreateOrgUnits();
        doReturn(true).when(organizationalUnitController).canDeleteOrgUnit(any());

        organizationalUnitServiceCaller = new CallerMock<>(organizationalUnitService);
        doReturn(null).when(organizationalUnitService).getOrganizationalUnit(anyString());
        doAnswer(invocationOnMock -> new OrganizationalUnitImpl((String) invocationOnMock.getArguments()[0],
                                                                (String) invocationOnMock.getArguments()[1],
                                                                (String) invocationOnMock.getArguments()[2]))
                .when(organizationalUnitService).updateOrganizationalUnit(anyString(),
                                                                          anyString(),
                                                                          anyString(),
                                                                          any());

        doReturn(mock(ContributorsManagementPresenter.View.class)).when(contributorsManagementPresenter).getView();

        presenter = spy(new EditContributorsPopUpPresenter(view,
                                                           contributorsManagementPresenter,
                                                           afterEditOrganizationalUnitEvent,
                                                           notificationEvent,
                                                           organizationalUnitController,
                                                           organizationalUnitServiceCaller));
    }

    @Test
    public void showWithPermissionTest() {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);

        presenter.show(organizationalUnit);

        verify(contributorsManagementPresenter).setup(organizationalUnit);
        verify(view).append(any());
        verify(view).show(organizationalUnit);

        assertEquals(organizationalUnit,
                     presenter.organizationalUnit);
    }

    @Test
    public void showWithoutPermissionTest() {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        doReturn(false).when(organizationalUnitController).canUpdateOrgUnit(organizationalUnit);

        presenter.show(organizationalUnit);

        verify(contributorsManagementPresenter,
               never()).setup();
        verify(view,
               never()).append(any());
        verify(view,
               never()).show(organizationalUnit);

        assertNull(presenter.organizationalUnit);
    }

    @Test
    public void saveTest() {
        final ArrayList contributors = mock(ArrayList.class);

        doReturn(contributors).when(contributorsManagementPresenter).getSelectedContributorsUserNames();
        presenter.organizationalUnit = mock(OrganizationalUnit.class);

        presenter.save();

        verify(view).showBusyIndicator(anyString());
        verify(afterEditOrganizationalUnitEvent).fire(any());
        verify(view).hideBusyIndicator();
        verify(notificationEvent).fire(any());
        verify(view).hide();
        verify(organizationalUnitService).updateOrganizationalUnit(anyString(),
                                                                   anyString(),
                                                                   anyString(),
                                                                   any());
    }

    @Test
    public void cancelTest() {
        presenter.cancel();

        verify(view).hide();
    }
}
