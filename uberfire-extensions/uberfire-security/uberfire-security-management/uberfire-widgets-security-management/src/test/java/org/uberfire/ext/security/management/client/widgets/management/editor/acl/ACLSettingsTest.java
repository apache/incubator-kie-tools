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

package org.uberfire.ext.security.management.client.widgets.management.editor.acl;

import javax.enterprise.event.Event;

import org.jboss.errai.security.shared.api.GroupImpl;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.events.AuthorizationPolicySavedEvent;
import org.uberfire.client.authz.PerspectiveTreeProvider;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.ext.security.management.client.widgets.management.events.HomePerspectiveChangedEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.PriorityChangedEvent;
import org.uberfire.ext.widgets.common.client.dropdown.PerspectiveDropDown;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.impl.authz.DefaultPermissionManager;
import org.uberfire.security.impl.authz.DefaultPermissionTypeRegistry;

import static org.mockito.Mockito.*;
import static org.uberfire.security.impl.authz.DefaultAuthorizationEntry.DEFAULT_PRIORITY;

@RunWith(MockitoJUnitRunner.class)
public class ACLSettingsTest {

    @Mock
    ACLSettings.View view;

    @Mock
    PerspectiveDropDown homePerspectiveDropDown;

    @Mock
    PerspectiveTreeProvider perspectiveTreeProvider;

    @Mock
    PriorityDropDown priorityDropDown;

    @Mock
    Event<HomePerspectiveChangedEvent> homePerspectiveChangedEvent;

    @Mock
    Event<PriorityChangedEvent> priorityChangedEvent;

    @Mock
    PerspectiveActivity defaultPerspective;

    ACLSettings presenter;
    PermissionManager permissionManager;

    @Before
    public void setup() {
        permissionManager = spy(new DefaultPermissionManager(new DefaultPermissionTypeRegistry()));

        permissionManager.setAuthorizationPolicy(permissionManager.newAuthorizationPolicy()
                                                                  .role("admin").home("HomeAdmin").priority(10)
                                                                  .group("group1").home("HomeGroup1").priority(DEFAULT_PRIORITY)
                                                                  .build());

        presenter = new ACLSettings(view,
                                    permissionManager,
                                    homePerspectiveDropDown,
                                    perspectiveTreeProvider,
                                    priorityDropDown,
                                    homePerspectiveChangedEvent,
                                    priorityChangedEvent);

        when(defaultPerspective.getIdentifier()).thenReturn("DefaultPerspective");
        when(homePerspectiveDropDown.getDefaultPerspective()).thenReturn(defaultPerspective);
        when(homePerspectiveDropDown.getItemName("DefaultPerspective")).thenReturn("DefaultPerspective");
        when(homePerspectiveDropDown.getItemName("HomeAdmin")).thenReturn("HomeAdmin");
        when(homePerspectiveDropDown.getItemName("HomeGroup1")).thenReturn("HomeGroup1");
        when(priorityDropDown.getPriorityName(10)).thenReturn("High");
        when(priorityDropDown.getPriorityName(DEFAULT_PRIORITY)).thenReturn("Very Low");
    }

    @Test
    public void testShowRole() {
        presenter.show(new RoleImpl("admin"));

        verify(view).setHomePerspectiveSelectorEnabled(false);
        verify(view).setPrioritySelectorEnabled(false);
        verify(view).setHomePerspectiveSelector(any());
        verify(view).setPrioritySelector(any());
        verify(view).setHomePerspectiveName("HomeAdmin");
        verify(view).setHomePerspectiveTitle("HomeAdmin");
        verify(view).setPriorityName("High");
    }

    @Test
    public void testEditRole() {
        presenter.edit(new RoleImpl("admin"));

        verify(view).setHomePerspectiveSelectorEnabled(true);
        verify(view).setPrioritySelectorEnabled(true);
        verify(view).setHomePerspectiveSelector(any());
        verify(view).setPrioritySelector(any());
        verify(homePerspectiveDropDown).setSelectedPerspective("HomeAdmin");
        verify(priorityDropDown).setSelectedPriority(10);
    }

    @Test
    public void testShowGroup() {
        presenter.show(new GroupImpl("group1"));

        verify(view).setHomePerspectiveSelectorEnabled(false);
        verify(view).setPrioritySelectorEnabled(false);
        verify(view).setHomePerspectiveSelector(any());
        verify(view).setPrioritySelector(any());
        verify(view).setHomePerspectiveName("HomeGroup1");
        verify(view).setHomePerspectiveTitle("HomeGroup1");
        verify(view).setPriorityName("Very Low");
    }

    @Test
    public void testEditGroup() {
        presenter.edit(new GroupImpl("group1"));

        verify(view).setHomePerspectiveSelectorEnabled(true);
        verify(view).setPrioritySelectorEnabled(true);
        verify(view).setHomePerspectiveSelector(any());
        verify(view).setPrioritySelector(any());
        verify(homePerspectiveDropDown).setSelectedPerspective("HomeGroup1");
        verify(priorityDropDown).setSelectedPriority(DEFAULT_PRIORITY);
    }

    @Test
    public void testDefaultValues() {
        reset(view);

        permissionManager.setAuthorizationPolicy(permissionManager.newAuthorizationPolicy().build());
        presenter = new ACLSettings(view,
                                    permissionManager,
                                    homePerspectiveDropDown,
                                    perspectiveTreeProvider,
                                    priorityDropDown,
                                    homePerspectiveChangedEvent,
                                    priorityChangedEvent);

        presenter.show(new RoleImpl("admin"));

        verify(view).setHomePerspectiveSelectorEnabled(false);
        verify(view).setPrioritySelectorEnabled(false);
        verify(view).setHomePerspectiveSelector(any());
        verify(view).setPrioritySelector(any());
        verify(view).setHomePerspectiveName("DefaultPerspective");
        verify(view).setHomePerspectiveTitle("DefaultPerspective");
        verify(view).setPriorityName("Very Low");
    }

    @Test
    public void testOnHomePerspectiveChange() {
        presenter.onHomePerspectiveSelected();

        verify(homePerspectiveChangedEvent).fire(any());
    }

    @Test
    public void testOnPrioritySelected() {
        presenter.onPrioritySelected();

        verify(priorityChangedEvent).fire(any());
    }

    @Test
    public void testAuthorizationPolicyChange() {
        
        final int NEW_PRIORITY = 100;
        final String NEW_PERSPECTIVE = "NewHomeAdmin";

        final AuthorizationPolicy newPolicy = permissionManager.newAuthorizationPolicy()
                                                               .role("admin").home(NEW_PERSPECTIVE).priority(NEW_PRIORITY)
                                                               .group("group1").home("HomeGroup1").priority(DEFAULT_PRIORITY)
                                                               .build();
        
        presenter.updateAuthzPolicy(new AuthorizationPolicySavedEvent(newPolicy));

        presenter.edit(new RoleImpl("admin"));

        verify(homePerspectiveDropDown).setSelectedPerspective(NEW_PERSPECTIVE);
        verify(priorityDropDown).setSelectedPriority(NEW_PRIORITY);
    }
}
