/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.uberfire.client.workbench.widgets.dnd;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.workbench.model.CompassPosition;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class CompassDropControllerTest {

    private CompassDropControllerUnitTestWrapper compassDropController;

    @GwtMock
    private WorkbenchPanelView view;

    @GwtMock
    private WorkbenchDragAndDropManager dndManager;
    @GwtMock
    private PanelManager panelManager;

    @Before
    public void setup() {
        compassDropController = new CompassDropControllerUnitTestWrapper();
        compassDropController.setupMocks(dndManager,
                panelManager);
    }

    @Test
    public void setupTest() {
        compassDropController.setup(view);
        assertEquals(view,
                compassDropController.dropTarget);
    }

    @Test
    public void compassDelegationTest() {
        CompassWidget compass = compassDropController.compass;

        DragContext dragContext = mock(DragContext.class);

        compassDropController.onEnter(dragContext);
        verify(compass).onEnter(dragContext);

        compassDropController.onLeave(dragContext);
        verify(compass).onLeave(dragContext);

        compassDropController.onMove(dragContext);
        verify(compass).onMove(dragContext);
    }

    @Test
    public void onDropDoesNothing() {
        //Mock If not dropTarget has been identified do nothing
        compassDropController.mockDropTargetPositionNone();

        DragContext dragContext = mock(DragContext.class);
        compassDropController.onDrop(dragContext);

        verify(compassDropController.compass,
                never()).onDrop(dragContext);
    }

    @Test
    public void onDropNoEffect() {
        compassDropController.mockDropTargetPosition(CompassPosition.SELF);

        compassDropController.mockSamePositionDrag(view);

        DragContext dragContext = mock(DragContext.class);

        compassDropController.onDrop(dragContext);

        verify(panelManager,
                never()).addWorkbenchPanel(any(),
                        any(),
                        any(),
                        any(),
                        any(),
                        any());
    }

    @Test
    public void onDropHappens() {
        compassDropController.mockDropTargetPosition(CompassPosition.WEST);

        compassDropController.mockSamePositionDrag(view);

        DragContext dragContext = mock(DragContext.class);

        compassDropController.onDrop(dragContext);

        verify(panelManager).addWorkbenchPanel(any(),
                any(),
                any(),
                any(),
                any(),
                any());

        verify(panelManager).addWorkbenchPart(any(),
                any(),
                any(),
                any(),
                any(),
                isNull(),
                isNull());
    }
}
