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

package org.uberfire.ext.security.management.client.widgets.management.explorer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.security.shared.api.Group;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.ext.security.management.client.widgets.management.AbstractSecurityManagementTest;
import org.uberfire.ext.security.management.client.widgets.management.events.RemoveUserGroupEvent;
import org.uberfire.ext.security.management.client.widgets.management.list.EntitiesList;
import org.uberfire.ext.security.management.client.widgets.management.list.GroupsList;
import org.uberfire.ext.security.management.client.widgets.popup.ConfirmBox;
import org.uberfire.mocks.EventSourceMock;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class UserGroupsExplorerTest extends AbstractSecurityManagementTest {

    @Mock
    GroupsList groupList;
    @Mock UserGroupsExplorer.View view;
    @Mock ConfirmBox confirmBox;
    @Mock EventSourceMock<RemoveUserGroupEvent> removeUserGroupEventEvent;
    
    private UserGroupsExplorer presenter;

    @Before
    public void setup() {
        super.setup();
        presenter = new UserGroupsExplorer(groupList, view, confirmBox, removeUserGroupEventEvent);
        assertEquals(view.asWidget(), presenter.asWidget());
    }

    @Test
    public void testClear() throws Exception {
        presenter.clear();
        assertFalse(presenter.canRemove);
        verify(view, times(1)).clear();
    }

    @Test
    public void show() {
        final boolean canRemove = false;
        final Set<Group> groups = new LinkedHashSet<Group>(buildGroupsList(10));
        presenter.show(new HashSet<Group>(groups), canRemove);
        
        assertFalse(presenter.canRemove);
        verify(view, times(1)).clear();
        
        // Verify groupList#show is called once and the generated callback argument. 
        final ArgumentCaptor<Set> responseArgumentCaptor =  ArgumentCaptor.forClass(Set.class);
        final ArgumentCaptor<EntitiesList.Callback> callbackArgumentCaptor = ArgumentCaptor.forClass(EntitiesList.Callback.class);
        verify(groupList, times(1)).show(responseArgumentCaptor.capture(), callbackArgumentCaptor.capture());
        final Set<Group> responseArgumentCaptured = responseArgumentCaptor.getValue();
        assertEquals(groups, responseArgumentCaptured);
        final EntitiesList.Callback callbackArgumentCaptured = callbackArgumentCaptor.getValue();
        assertEquals(false, callbackArgumentCaptured.canRead());
        assertEquals(false, callbackArgumentCaptured.canRemove());
        assertEquals(false, callbackArgumentCaptured.canSelect());

        int x = 0;
        for (Group _group : groups) {
            final String gname = getGroupIdentifier(x);
            assertEquals(gname, callbackArgumentCaptured.getIdentifier(_group));
            assertEquals(gname, callbackArgumentCaptured.getTitle(_group));
            assertEquals(false, callbackArgumentCaptured.isSelected(_group.getName()));
            x++;
        }
    }
}
