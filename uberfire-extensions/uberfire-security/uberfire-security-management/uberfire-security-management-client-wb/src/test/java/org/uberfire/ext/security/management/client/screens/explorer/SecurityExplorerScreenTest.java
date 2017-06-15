/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.screens.explorer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.security.management.client.ClientSecurityExceptionMessageResolver;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.screens.editor.GroupEditorScreen;
import org.uberfire.ext.security.management.client.screens.editor.RoleEditorScreen;
import org.uberfire.ext.security.management.client.screens.editor.UserEditorScreen;
import org.uberfire.ext.security.management.client.widgets.management.events.NewGroupEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.NewUserEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.ReadGroupEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.ReadRoleEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.ReadUserEvent;
import org.uberfire.ext.security.management.client.widgets.management.explorer.GroupsExplorer;
import org.uberfire.ext.security.management.client.widgets.management.explorer.RolesExplorer;
import org.uberfire.ext.security.management.client.widgets.management.explorer.UsersExplorer;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class SecurityExplorerScreenTest {

    @Mock
    private SecurityExplorerScreen.View view;
    @Mock
    private RolesExplorer rolesExplorer;
    @Mock
    private GroupsExplorer groupsExplorer;
    @Mock
    private UsersExplorer usersExplorer;
    @Mock
    private ErrorPopupPresenter errorPopupPresenter;
    @Mock
    private PlaceManager placeManager;
    @Mock
    private ClientUserSystemManager userSystemManager;

    private SecurityExplorerScreen tested;

    @Before
    public void setup() {
        doAnswer(invocationOnMock -> {
            ((Command) invocationOnMock.getArguments()[0]).execute();
            return null;
        }).when(userSystemManager).waitForInitialization(any(Command.class));
        when(userSystemManager.isActive()).thenReturn(true);
        this.tested = new SecurityExplorerScreen(view,
                                                 rolesExplorer,
                                                 groupsExplorer,
                                                 usersExplorer,
                                                 errorPopupPresenter,
                                                 placeManager,
                                                 userSystemManager,
                                                 new ClientSecurityExceptionMessageResolver());
    }

    @Test
    public void testInit() {
        tested.init();
        verify(view,
               times(1)).init(eq(tested),
                              eq(rolesExplorer),
                              eq(groupsExplorer),
                              eq(usersExplorer));
        verify(rolesExplorer,
               times(1)).show();
        verify(view,
               times(1)).rolesEnabled(eq(true));
        verify(view,
               times(1)).groupsEnabled(eq(false));
        verify(view,
               times(1)).usersEnabled(eq(false));
    }

    @Test
    public void testOnStartupUsersTab() {
        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        when(placeRequest.getParameter(eq(SecurityExplorerScreen.ACTIVE_TAB),
                                       anyString())).thenReturn(SecurityExplorerScreen.USERS_TAB);
        tested.onStartup(placeRequest);
        verify(usersExplorer,
               times(1)).show();
        verify(groupsExplorer,
               times(1)).show();
        verify(view,
               times(1)).groupsEnabled(eq(true));
        verify(view,
               times(1)).usersEnabled(eq(true));
        verify(view,
               times(1)).rolesActive(eq(false));
        verify(view,
               times(1)).groupsActive(eq(false));
        verify(view,
               times(1)).usersActive(eq(true));
    }

    @Test
    public void testOnStartupGroupsTab() {
        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        when(placeRequest.getParameter(eq(SecurityExplorerScreen.ACTIVE_TAB),
                                       anyString())).thenReturn(SecurityExplorerScreen.GROUPS_TAB);
        tested.onStartup(placeRequest);
        verify(usersExplorer,
               times(1)).show();
        verify(groupsExplorer,
               times(1)).show();
        verify(view,
               times(1)).groupsEnabled(eq(true));
        verify(view,
               times(1)).usersEnabled(eq(true));
        verify(view,
               times(1)).rolesActive(eq(false));
        verify(view,
               times(1)).usersActive(eq(false));
        verify(view,
               times(1)).groupsActive(eq(true));
    }

    @Test
    public void testOnClose() {
        tested.onClose();
        verify(usersExplorer,
               times(1)).clear();
        verify(groupsExplorer,
               times(1)).clear();
        verify(rolesExplorer,
               times(1)).clear();
    }

    @Test
    public void testOnRoleRead() {
        final ReadRoleEvent event = mock(ReadRoleEvent.class);
        when(event.getName()).thenReturn("someRoleName");
        final ArgumentCaptor<DefaultPlaceRequest> placeRequestArgumentCaptor =
                ArgumentCaptor.forClass(DefaultPlaceRequest.class);
        tested.onRoleRead(event);
        verify(placeManager,
               times(1)).goTo(placeRequestArgumentCaptor.capture());
        final DefaultPlaceRequest placeRequest = placeRequestArgumentCaptor.getValue();
        assertEquals(RoleEditorScreen.SCREEN_ID,
                     placeRequest.getIdentifier());
        assertEquals("someRoleName",
                     placeRequest.getParameters().get(RoleEditorScreen.ROLE_NAME));
    }

    @Test
    public void testOnGroupRead() {
        final ReadGroupEvent event = mock(ReadGroupEvent.class);
        when(event.getName()).thenReturn("someGroupName");
        final ArgumentCaptor<DefaultPlaceRequest> placeRequestArgumentCaptor =
                ArgumentCaptor.forClass(DefaultPlaceRequest.class);
        tested.onGroupRead(event);
        verify(placeManager,
               times(1)).goTo(placeRequestArgumentCaptor.capture());
        final DefaultPlaceRequest placeRequest = placeRequestArgumentCaptor.getValue();
        assertEquals(GroupEditorScreen.SCREEN_ID,
                     placeRequest.getIdentifier());
        assertEquals("someGroupName",
                     placeRequest.getParameters().get(GroupEditorScreen.GROUP_NAME));
        assertFalse(placeRequest.getParameters().containsKey(GroupEditorScreen.ADD_GROUP));
    }

    @Test
    public void testOnUserRead() {
        final ReadUserEvent event = mock(ReadUserEvent.class);
        when(event.getIdentifier()).thenReturn("someUserId");
        final ArgumentCaptor<DefaultPlaceRequest> placeRequestArgumentCaptor =
                ArgumentCaptor.forClass(DefaultPlaceRequest.class);
        tested.onUserRead(event);
        verify(placeManager,
               times(1)).goTo(placeRequestArgumentCaptor.capture());
        final DefaultPlaceRequest placeRequest = placeRequestArgumentCaptor.getValue();
        assertEquals(UserEditorScreen.SCREEN_ID,
                     placeRequest.getIdentifier());
        assertEquals("someUserId",
                     placeRequest.getParameters().get(UserEditorScreen.USER_ID));
        assertFalse(placeRequest.getParameters().containsKey(UserEditorScreen.ADD_USER));
    }

    @Test
    public void testOnGroupCreate() {
        final NewGroupEvent event = mock(NewGroupEvent.class);
        final ArgumentCaptor<DefaultPlaceRequest> placeRequestArgumentCaptor =
                ArgumentCaptor.forClass(DefaultPlaceRequest.class);
        tested.onGroupCreate(event);
        verify(placeManager,
               times(1)).goTo(placeRequestArgumentCaptor.capture());
        final DefaultPlaceRequest placeRequest = placeRequestArgumentCaptor.getValue();
        assertEquals(GroupEditorScreen.SCREEN_ID,
                     placeRequest.getIdentifier());
        assertTrue(Boolean.parseBoolean(placeRequest.getParameters().get(GroupEditorScreen.ADD_GROUP)));
        assertFalse(placeRequest.getParameters().containsKey(GroupEditorScreen.GROUP_NAME));
    }

    @Test
    public void testOnUserCreate() {
        final NewUserEvent event = mock(NewUserEvent.class);
        final ArgumentCaptor<DefaultPlaceRequest> placeRequestArgumentCaptor =
                ArgumentCaptor.forClass(DefaultPlaceRequest.class);
        tested.onUserCreate(event);
        verify(placeManager,
               times(1)).goTo(placeRequestArgumentCaptor.capture());
        final DefaultPlaceRequest placeRequest = placeRequestArgumentCaptor.getValue();
        assertEquals(UserEditorScreen.SCREEN_ID,
                     placeRequest.getIdentifier());
        assertTrue(Boolean.parseBoolean(placeRequest.getParameters().get(UserEditorScreen.ADD_USER)));
        assertFalse(placeRequest.getParameters().containsKey(UserEditorScreen.USER_ID));
    }
}
