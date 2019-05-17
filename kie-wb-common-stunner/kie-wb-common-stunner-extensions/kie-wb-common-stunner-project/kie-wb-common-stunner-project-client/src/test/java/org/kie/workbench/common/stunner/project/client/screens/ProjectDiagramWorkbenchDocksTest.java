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

package org.kie.workbench.common.stunner.project.client.screens;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ProjectDiagramWorkbenchDocksTest {

    @Mock
    UberfireDocks uberfireDocks;

    private ProjectDiagramWorkbenchDocks tested;

    @Before
    public void setup() throws Exception {
        this.tested = new ProjectDiagramWorkbenchDocks(uberfireDocks);
    }

    @Test
    public void testSetup() {
        String pId = "p1";
        tested.setup(pId);
        assertEquals("p1",
                     tested.perspectiveId);
    }

    @Test
    public void testEnableDocks() {
        String pId = "p1";
        tested.perspectiveId = pId;
        tested.enableDocks();
        verify(uberfireDocks,
               times(1)).show(any(UberfireDockPosition.class),
                              eq(pId));
        assertTrue(tested.isEnabled());
    }

    @Test
    public void testDisableDocks() {
        String pId = "p1";
        tested.perspectiveId = pId;
        tested.enabled = true;
        tested.disableDocks();
        verify(uberfireDocks,
               times(1)).hide(any(UberfireDockPosition.class),
                              eq(pId));
        assertFalse(tested.isEnabled());
    }
}
