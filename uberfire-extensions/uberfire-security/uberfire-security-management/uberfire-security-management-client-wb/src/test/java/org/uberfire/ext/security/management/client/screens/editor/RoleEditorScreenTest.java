/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.screens.editor;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.widgets.management.editor.role.workflow.RoleEditorWorkflow;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class RoleEditorScreenTest {

    @Mock
    ClientUserSystemManager clientUserSystemManager;

    @Mock
    RoleEditorWorkflow roleEditorWorkflow;

    @InjectMocks
    RoleEditorScreen tested;

    @Before
    public void setup() {
        when(clientUserSystemManager.isUserCapabilityEnabled(any(Capability.class))).thenReturn(true);
    }

    @Test
    public void testOnMayCloseSuccess() {
        when(roleEditorWorkflow.isDirty()).thenReturn(false);
        assertTrue(tested.onMayClose());
    }

    @Test
    public void testOnMayCloseFailed() {
        when(roleEditorWorkflow.isDirty()).thenReturn(true);
        assertFalse(tested.onMayClose());
    }
}
