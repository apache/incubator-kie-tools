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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.popup;

import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.guvnor.structure.events.AfterCreateOrganizationalUnitEvent;
import org.guvnor.structure.events.AfterEditOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrganizationalUnitPopUpPresenterTest {

    @Mock
    private OrganizationalUnitPopUpPresenter.View view;

    @Mock
    private OrganizationalUnitController organizationalUnitController;

    @Mock
    private OrganizationalUnitService organizationalUnitService;
    private Caller<OrganizationalUnitService> organizationalUnitServiceCaller;

    @Mock
    private EventSourceMock<AfterCreateOrganizationalUnitEvent> afterCreateOrganizationalUnitEvent;

    @Mock
    private EventSourceMock<AfterEditOrganizationalUnitEvent> afterEditOrganizationalUnitEvent;

    @Mock
    private EventSourceMock<NotificationEvent> notificationEvent;

    private OrganizationalUnitPopUpPresenter presenter;

    @Before
    public void setup() {
        doReturn(true).when(organizationalUnitController).canReadOrgUnits();
        doReturn(true).when(organizationalUnitController).canReadOrgUnit(any());
        doReturn(true).when(organizationalUnitController).canUpdateOrgUnit(any());
        doReturn(true).when(organizationalUnitController).canCreateOrgUnits();
        doReturn(true).when(organizationalUnitController).canDeleteOrgUnit(any());

        organizationalUnitServiceCaller = new CallerMock<>(organizationalUnitService);
        doReturn(true).when(organizationalUnitService).isValidGroupId(anyString());
        doReturn(null).when(organizationalUnitService).getOrganizationalUnit(anyString());
        doAnswer(invocationOnMock -> new OrganizationalUnitImpl((String) invocationOnMock.getArguments()[0],
                                                                (String) invocationOnMock.getArguments()[1],
                                                                (String) invocationOnMock.getArguments()[2]))
                .when(organizationalUnitService).createOrganizationalUnit(anyString(),
                                                                          anyString(),
                                                                          anyString(),
                                                                          any());
        doAnswer(invocationOnMock -> new OrganizationalUnitImpl((String) invocationOnMock.getArguments()[0],
                                                                (String) invocationOnMock.getArguments()[1],
                                                                (String) invocationOnMock.getArguments()[2]))
                .when(organizationalUnitService).updateOrganizationalUnit(anyString(),
                                                                          anyString(),
                                                                          anyString());

        presenter = spy(new OrganizationalUnitPopUpPresenter(view,
                                                             organizationalUnitServiceCaller,
                                                             afterCreateOrganizationalUnitEvent,
                                                             afterEditOrganizationalUnitEvent,
                                                             notificationEvent,
                                                             organizationalUnitController));
    }

    @Test
    public void showAddPopUpWithPermissionTest() {
        presenter.showAddPopUp();

        verify(view).clear();
        verify(view).showAddPopUp();
    }

    @Test
    public void showAddPopUpWithoutPermissionTest() {
        doReturn(false).when(organizationalUnitController).canCreateOrgUnits();

        presenter.showAddPopUp();

        verify(view,
               never()).clear();
        verify(view,
               never()).showAddPopUp();
    }

    @Test
    public void showEditPopUpWithPermissionTest() {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);

        presenter.showEditPopUp(organizationalUnit);

        verify(view).showEditPopUp(organizationalUnit);
    }

    @Test
    public void showEditPopUpWithoutPermissionTest() {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);

        doReturn(false).when(organizationalUnitController).canUpdateOrgUnit(organizationalUnit);

        presenter.showEditPopUp(organizationalUnit);

        verify(view,
               never()).showEditPopUp(organizationalUnit);
    }

    @Test
    public void saveWithEmptyNameTest() {
        doReturn("").when(view).getName();

        presenter.save();

        verify(view).showBusyIndicator(anyString());
        verify(view).getEmptyNameValidationMessage();
        verify(view).hideBusyIndicator();
        verify(view).showError(anyString());

        verify(organizationalUnitService,
               never()).createOrganizationalUnit(anyString(),
                                                 anyString(),
                                                 anyString(),
                                                 any());
        verify(organizationalUnitService,
               never()).updateOrganizationalUnit(anyString(),
                                                 anyString(),
                                                 anyString());
    }

    @Test
    public void saveWithEmptyDefaultGroupIdTest() {
        doReturn("name").when(view).getName();
        doReturn("").when(view).getDefaultGroupId();

        presenter.save();

        verify(view).showBusyIndicator(anyString());
        verify(view).getEmptyDefaultGroupIdValidationMessage();
        verify(view).hideBusyIndicator();
        verify(view).showError(anyString());

        verify(organizationalUnitService,
               never()).createOrganizationalUnit(anyString(),
                                                 anyString(),
                                                 anyString(),
                                                 any());
        verify(organizationalUnitService,
               never()).updateOrganizationalUnit(anyString(),
                                                 anyString(),
                                                 anyString());
    }

    @Test
    public void saveWithInvalidDefaultGroupIdTest() {
        doReturn(false).when(organizationalUnitService).isValidGroupId(anyString());

        doReturn("name").when(view).getName();
        doReturn("defaultGroupId").when(view).getDefaultGroupId();

        presenter.save();

        verify(view).showBusyIndicator(anyString());
        verify(view).getInvalidDefaultGroupIdValidationMessage();
        verify(view).hideBusyIndicator();
        verify(view).showError(anyString());

        verify(organizationalUnitService,
               never()).createOrganizationalUnit(anyString(),
                                                 anyString(),
                                                 anyString(),
                                                 any());
        verify(organizationalUnitService,
               never()).updateOrganizationalUnit(anyString(),
                                                 anyString(),
                                                 anyString());
    }

    @Test
    public void saveWithEmptyOwnerTest() {
        doReturn("name").when(view).getName();
        doReturn("defaultGroupId").when(view).getDefaultGroupId();
        doReturn("").when(view).getOwner();

        presenter.save();

        verify(view).showBusyIndicator(anyString());
        verify(view).hideBusyIndicator();

        verify(organizationalUnitService).createOrganizationalUnit(eq("name"),
                                                                   eq(""),
                                                                   eq("defaultGroupId"),
                                                                   any());

        verify(organizationalUnitService,
               never()).updateOrganizationalUnit(anyString(),
                                                 anyString(),
                                                 anyString());
    }

    @Test
    public void saveCreationWithDuplicatedNameTest() {
        doReturn(mock(OrganizationalUnit.class)).when(organizationalUnitService).getOrganizationalUnit(anyString());

        doReturn("name").when(view).getName();
        doReturn("defaultGroupId").when(view).getDefaultGroupId();
        doReturn("").when(view).getOwner();

        presenter.save();

        verify(view).showBusyIndicator(anyString());
        verify(view).getDuplicatedOrganizationalUnitValidationMessage();
        verify(view).hideBusyIndicator();
        verify(view).showError(anyString());

        verify(organizationalUnitService,
               never()).createOrganizationalUnit(anyString(),
                                                 anyString(),
                                                 anyString(),
                                                 any());
        verify(organizationalUnitService,
               never()).updateOrganizationalUnit(anyString(),
                                                 anyString(),
                                                 anyString());
    }

    @Test
    public void saveEditionWithSameNameTest() {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        doReturn(organizationalUnit).when(organizationalUnitService).getOrganizationalUnit(anyString());

        doReturn("name").when(view).getName();
        doReturn("defaultGroupId").when(view).getDefaultGroupId();
        doReturn("owner").when(view).getOwner();

        presenter.showEditPopUp(organizationalUnit);
        presenter.save();

        verify(view).showBusyIndicator(anyString());
        verify(view).hideBusyIndicator();

        verify(organizationalUnitService,
               never()).createOrganizationalUnit(anyString(),
                                                 anyString(),
                                                 anyString(),
                                                 any());
        verify(organizationalUnitService).updateOrganizationalUnit("name",
                                                                   "owner",
                                                                   "defaultGroupId");
    }

    @Test
    public void saveEditionTest() {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);

        doReturn("name").when(view).getName();
        doReturn("defaultGroupId").when(view).getDefaultGroupId();
        doReturn("owner").when(view).getOwner();

        presenter.showEditPopUp(organizationalUnit);
        presenter.save();

        verify(view).showBusyIndicator(anyString());
        verify(view).hideBusyIndicator();

        verify(organizationalUnitService,
               never()).createOrganizationalUnit(anyString(),
                                                 anyString(),
                                                 anyString(),
                                                 any());
        verify(organizationalUnitService).updateOrganizationalUnit("name",
                                                                   "owner",
                                                                   "defaultGroupId");
    }

    @Test
    public void saveCreationTest() {
        doReturn("name").when(view).getName();
        doReturn("defaultGroupId").when(view).getDefaultGroupId();
        doReturn("owner").when(view).getOwner();

        presenter.save();

        verify(view).showBusyIndicator(anyString());
        verify(view).hideBusyIndicator();

        verify(organizationalUnitService).createOrganizationalUnit(anyString(),
                                                                   anyString(),
                                                                   anyString(),
                                                                   any());
        verify(organizationalUnitService,
               never()).updateOrganizationalUnit(anyString(),
                                                 anyString(),
                                                 anyString());
    }

    @Test
    public void cancelTest() {
        presenter.cancel();

        verify(view).hide();
    }
}
