/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client;

import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.security.management.api.*;
import org.uberfire.ext.security.management.api.service.GroupManagerService;
import org.uberfire.ext.security.management.api.service.RoleManagerService;
import org.uberfire.ext.security.management.api.service.UserManagerService;
import org.uberfire.ext.security.management.impl.GroupManagerSettingsImpl;
import org.uberfire.ext.security.management.impl.UserManagerSettingsImpl;
import org.uberfire.mocks.CallerMock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClientUserSystemManagerTest {

    private static final String ATTRIBUTE_USER_ID = "user.id";
    private static final String ATTRIBUTE_USER_FIRST_NAME = "user.firstName";
    private static final String ATTRIBUTE_USER_LAST_NAME = "user.lastName";
    private static final String ATTRIBUTE_USER_ENABLED = "user.enabled";
    private static final String ATTRIBUTE_USER_EMAIL = "user.email";
    
    @Mock ErrorPopupPresenter errorPopupPresenter;
    @Mock UserManagerService userManagerService;
    @Mock GroupManagerService groupsManagerService;
    @Mock RoleManagerService rolesManagerService;
    Caller<UserManagerService> usersManagerServiceCaller;
    Caller<GroupManagerService> groupsManagerServiceCaller;
    Caller<RoleManagerService> rolesManagerServiceCaller;
    
    private ClientUserSystemManager tested;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final UserManagerSettings userManagerSettings = getUserSettings();
        when(userManagerService.getSettings()).thenReturn(userManagerSettings);
        final GroupManagerSettings groupManagerSettings = getGroupSettings();
        when(groupsManagerService.getSettings()).thenReturn(groupManagerSettings);
        usersManagerServiceCaller = new CallerMock<UserManagerService>(userManagerService);
        groupsManagerServiceCaller = new CallerMock<GroupManagerService>( groupsManagerService );
        rolesManagerServiceCaller = new CallerMock<RoleManagerService>( rolesManagerService );
        tested = spy(new ClientUserSystemManager(usersManagerServiceCaller,
                groupsManagerServiceCaller, rolesManagerServiceCaller, errorPopupPresenter));
    }
    
    @Test
    public void testInitCache() {
        tested.initCache();
        verify(userManagerService, times(1)).getSettings();
        verify(groupsManagerService, times(1)).getSettings();
    }

    @Test
    public void testIsUserCapabilityEnabled() {
        tested.userManagerSettings  = getUserSettings();
        assertTrue(tested.isUserCapabilityEnabled(Capability.CAN_READ_USER));
        assertTrue(tested.isUserCapabilityEnabled(Capability.CAN_ASSIGN_GROUPS));
        assertFalse(tested.isUserCapabilityEnabled(Capability.CAN_ASSIGN_ROLES));
    }

    @Test
    public void testIsGroupCapabilityEnabled() {
        tested.groupManagerSettings = getGroupSettings();
        assertTrue(tested.isGroupCapabilityEnabled(Capability.CAN_READ_GROUP));
        assertTrue(tested.isGroupCapabilityEnabled(Capability.CAN_ADD_GROUP));
        assertFalse(tested.isGroupCapabilityEnabled(Capability.CAN_DELETE_GROUP));
    }

    @Test
    public void testGetUserSupportedAttributes() {
        tested.userManagerSettings  = getUserSettings();
        assertNotNull(tested.getUserSupportedAttribute(ATTRIBUTE_USER_ID));
        assertNull(tested.getUserSupportedAttribute("custom-attr"));
    }

    @Test
    public void testGetConstrainedGroups() {
        tested.groupManagerSettings = getGroupSettings();
        Collection<String> cGroups = new ArrayList<String>(1);
        cGroups.add("admin");
        tested.groupManagerSettings.setConstrainedGroups(cGroups);
        assertEquals(cGroups, tested.getConstrainedGroups());
    }

    @Test
    public void testShowError() {
        tested.showError("error-message");
        verify(errorPopupPresenter, times(1)).showMessage("error-message");
    }
    
    private UserManagerSettings getUserSettings() {
        return new UserManagerSettingsImpl(getUserCapabilities(), getUserAttributes());
    }

    private GroupManagerSettings getGroupSettings() {
        return new GroupManagerSettingsImpl(getGroupCapabilities(), true);
    }
    
    private Map<Capability, CapabilityStatus> getUserCapabilities() {
        Map<Capability, CapabilityStatus> userCapabilityStatusMap = new HashMap<Capability, CapabilityStatus>();
        userCapabilityStatusMap.put(Capability.CAN_SEARCH_USERS, CapabilityStatus.ENABLED);
        userCapabilityStatusMap.put(Capability.CAN_READ_USER, CapabilityStatus.ENABLED);
        userCapabilityStatusMap.put(Capability.CAN_ADD_USER, CapabilityStatus.ENABLED);
        userCapabilityStatusMap.put(Capability.CAN_UPDATE_USER, CapabilityStatus.ENABLED);
        userCapabilityStatusMap.put(Capability.CAN_DELETE_USER, CapabilityStatus.ENABLED);
        userCapabilityStatusMap.put(Capability.CAN_MANAGE_ATTRIBUTES, CapabilityStatus.ENABLED);
        userCapabilityStatusMap.put(Capability.CAN_ASSIGN_GROUPS, CapabilityStatus.ENABLED);
        userCapabilityStatusMap.put(Capability.CAN_CHANGE_PASSWORD, CapabilityStatus.ENABLED);
        userCapabilityStatusMap.put(Capability.CAN_ASSIGN_ROLES, CapabilityStatus.UNSUPPORTED);
        return userCapabilityStatusMap;
    }

    private Map<Capability, CapabilityStatus> getGroupCapabilities() {
        Map<Capability, CapabilityStatus> groupCapabilityStatusMap = new HashMap<Capability, CapabilityStatus>();
        groupCapabilityStatusMap.put(Capability.CAN_SEARCH_GROUPS, CapabilityStatus.ENABLED);
        groupCapabilityStatusMap.put(Capability.CAN_READ_GROUP, CapabilityStatus.ENABLED);
        groupCapabilityStatusMap.put(Capability.CAN_ADD_GROUP, CapabilityStatus.ENABLED);
        groupCapabilityStatusMap.put(Capability.CAN_UPDATE_GROUP, CapabilityStatus.ENABLED);
        groupCapabilityStatusMap.put(Capability.CAN_DELETE_GROUP, CapabilityStatus.UNSUPPORTED);
        return groupCapabilityStatusMap;
    }
    
    protected Collection<UserManager.UserAttribute> getUserAttributes() {
        Collection<UserManager.UserAttribute> attributes = new ArrayList<UserManager.UserAttribute>();

        final UserManager.UserAttribute USER_ID = mock(UserManager.UserAttribute.class);
        when(USER_ID.getName()).thenReturn(ATTRIBUTE_USER_ID);
        when(USER_ID.isMandatory()).thenReturn(true);
        when(USER_ID.isEditable()).thenReturn(false);
        when(USER_ID.getDefaultValue()).thenReturn(null);
        attributes.add(USER_ID);

        final UserManager.UserAttribute USER_FIST_NAME = mock(UserManager.UserAttribute.class);
        when(USER_FIST_NAME.getName()).thenReturn(ATTRIBUTE_USER_FIRST_NAME);
        when(USER_FIST_NAME.isMandatory()).thenReturn(true);
        when(USER_FIST_NAME.isEditable()).thenReturn(true);
        when(USER_FIST_NAME.getDefaultValue()).thenReturn("First name");
        attributes.add(USER_FIST_NAME);

        final UserManager.UserAttribute USER_LAST_NAME = mock(UserManager.UserAttribute.class);
        when(USER_LAST_NAME.getName()).thenReturn(ATTRIBUTE_USER_LAST_NAME);
        when(USER_LAST_NAME.isMandatory()).thenReturn(true);
        when(USER_LAST_NAME.isEditable()).thenReturn(true);
        when(USER_LAST_NAME.getDefaultValue()).thenReturn("Last name");
        attributes.add(USER_LAST_NAME);

        final UserManager.UserAttribute USER_ENABLED = mock(UserManager.UserAttribute.class);
        when(USER_ENABLED.getName()).thenReturn(ATTRIBUTE_USER_ENABLED);
        when(USER_ENABLED.isMandatory()).thenReturn(true);
        when(USER_ENABLED.isEditable()).thenReturn(true);
        when(USER_ENABLED.getDefaultValue()).thenReturn("true");
        attributes.add(USER_ENABLED);

        final UserManager.UserAttribute USER_EMAIL = mock(UserManager.UserAttribute.class);
        when(USER_EMAIL.getName()).thenReturn(ATTRIBUTE_USER_EMAIL);
        when(USER_EMAIL.isMandatory()).thenReturn(false);
        when(USER_EMAIL.isEditable()).thenReturn(true);
        when(USER_EMAIL.getDefaultValue()).thenReturn("");
        attributes.add(USER_EMAIL);

        return attributes;
    }
}
