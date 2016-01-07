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

package org.uberfire.ext.security.management.client.widgets.management.editor.group;

import org.jboss.errai.security.shared.api.Group;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.widgets.management.events.OnDeleteEvent;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class GroupViewerTest {

    @Mock ClientUserSystemManager userSystemManager;
    @Mock EventSourceMock<OnDeleteEvent> onDeleteEvent;
    @Mock GroupViewer.View view;
    
    private GroupViewer tested;
    @Mock Group group;
    
    @Before
    public void setup() {
        when(userSystemManager.isGroupCapabilityEnabled(any(Capability.class))).thenReturn(true);
        when(group.getName()).thenReturn("group1");
        tested = new GroupViewer(userSystemManager, onDeleteEvent, view);
    }
    
    @Test
    public void testInit() {
        tested.init();
        verify(view, times(1)).init(tested);
        verify(view, times(0)).setShowDeleteButton(anyBoolean());
        verify(view, times(0)).show(anyString());
        verify(view, times(0)).clear();
    }

    @Test
    public void testClear() {
        tested.group = group;
        tested.clear();
        assertNull(tested.group);
        verify(view, times(0)).init(tested);
        verify(view, times(0)).setShowDeleteButton(anyBoolean());
        verify(view, times(0)).show(anyString());
        verify(view, times(1)).clear();
    }
    
    @Test
    public void testShow() {
        tested.show(group);
        assertEquals(group, tested.group);
        verify(view, times(1)).clear();
        verify(view, times(1)).show("group1");
        verify(view, times(1)).setShowDeleteButton(true);
    }

    @Test
    public void testOnDelete() {
        tested.onDelete();
        verify(onDeleteEvent, times(1)).fire(any(OnDeleteEvent.class));
        verify(view, times(0)).init(tested);
        verify(view, times(0)).setShowDeleteButton(anyBoolean());
        verify(view, times(0)).show(anyString());
        verify(view, times(0)).clear();
    }
    
    
}
