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


package org.drools.workbench.screens.scenariosimulation.kogito.client.docks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.docks.AbstractWorkbenchDocksHandler;
import org.kie.workbench.common.widgets.client.docks.WorkbenchDocksHandler;
import org.mockito.Mock;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class KogitoEditorDockTest {

    public static final String AUTHORING_PERSPECTIVE = "authoring";

    @Mock
    private UberfireDocks uberfireDocks;

    @Mock
    private PlaceRequest placeRequest;

    @Mock
    private ManagedInstance<WorkbenchDocksHandler> handlers;

    private KogitoEditorDock kogitoEditorDockSpy;

    private TestWorkbenchDocksHandler handler;

    @Before
    public void setup() {
        handler = spy(createNewWorkbenchDocksHandler());

        List<WorkbenchDocksHandler> list = new ArrayList<>();
        list.add(handler);
        when(handlers.iterator()).thenReturn(list.iterator());

        kogitoEditorDockSpy = spy(new KogitoEditorDock(uberfireDocks, handlers));

        kogitoEditorDockSpy.initialize();

        verify(handlers).iterator();
        verify(handler, times(1)).init(isA(Command.class));

        assertFalse(kogitoEditorDockSpy.isSetup());

        kogitoEditorDockSpy.setup(AUTHORING_PERSPECTIVE, placeRequest);

        assertTrue(kogitoEditorDockSpy.isSetup());
    }

    @Test
    public void expandAuthoringDockEmpty() {
        kogitoEditorDockSpy.expandAuthoringDock(null);

        verify(uberfireDocks, times(1)).show(UberfireDockPosition.EAST);
        verify(uberfireDocks, never()).open(any());
    }

    @Test
    public void expandAuthoringDock() {
        final UberfireDock dockToOpen = mock(UberfireDock.class);
        kogitoEditorDockSpy.expandAuthoringDock(dockToOpen);

        verify(uberfireDocks, times(1)).show(UberfireDockPosition.EAST);
        verify(uberfireDocks, times(1)).open(dockToOpen);
    }

    @Test
    public void setActiveHandler() {
        kogitoEditorDockSpy.setActiveHandler(handler);
        assertSame(kogitoEditorDockSpy.activeHandler, handler);
        verify(handler, times(1)).provideDocks(AUTHORING_PERSPECTIVE);
        assertEquals(2, kogitoEditorDockSpy.activeDocks.length);
        verify(uberfireDocks, never()).remove(any());
        verify(uberfireDocks, times(1)).add(isA(UberfireDock.class), isA(UberfireDock.class));
        verify(uberfireDocks, times(1)).show(UberfireDockPosition.EAST);
    }

    @Test
    public void setActiveHandlerWithSameActiveHandlerToRefresh() {
        kogitoEditorDockSpy.activeHandler = handler;
        handler.refresh(true, false);
        kogitoEditorDockSpy.setActiveHandler(handler);
        assertSame(kogitoEditorDockSpy.activeHandler, handler);
        verify(handler, atLeastOnce()).provideDocks(AUTHORING_PERSPECTIVE);
        assertEquals(2, kogitoEditorDockSpy.activeDocks.length);
        verify(uberfireDocks, atLeastOnce()).remove(any());
        verify(uberfireDocks, atLeastOnce()).add(isA(UberfireDock.class), isA(UberfireDock.class));
        verify(uberfireDocks, atLeastOnce()).show(UberfireDockPosition.EAST);
    }

    @Test
    public void setActiveHandlerWithSameActiveHandlerToNotRefresh() {
        kogitoEditorDockSpy.activeHandler = handler;
        handler.refresh(false, false);
        kogitoEditorDockSpy.setActiveHandler(handler);
        verify(handler, never()).provideDocks(any());
        verify(uberfireDocks, never()).remove(any());
        verify(uberfireDocks, never()).add(any());
        verify(uberfireDocks, never()).show(any());
    }

    @Test
    public void setActiveHandlerWithActiveDock() {
        UberfireDock activeDock = mock(UberfireDock.class);
        UberfireDock[] activeDocks = new UberfireDock[]{activeDock};
        kogitoEditorDockSpy.activeDocks = activeDocks;
        kogitoEditorDockSpy.setActiveHandler(handler);
        assertSame(kogitoEditorDockSpy.activeHandler, handler);
        verify(handler, atLeastOnce()).provideDocks(AUTHORING_PERSPECTIVE);
        assertEquals(2, kogitoEditorDockSpy.activeDocks.length);
        verify(uberfireDocks, times(1)).remove(activeDock);
        verify(uberfireDocks, times(1)).add(isA(UberfireDock.class), isA(UberfireDock.class));
        verify(uberfireDocks, times(1)).show(UberfireDockPosition.EAST);
    }

    @Test
    public void setActiveHandlerShouldDisableDock() {
        handler.refresh(false, true);
        kogitoEditorDockSpy.setActiveHandler(handler);
        assertSame(kogitoEditorDockSpy.activeHandler, handler);
        verify(handler, never()).provideDocks(any());
        verify(uberfireDocks, never()).remove(kogitoEditorDockSpy.activeDocks);
        verify(uberfireDocks, never()).add(any(), any());
        verify(uberfireDocks, never()).show(any());
    }

    private TestWorkbenchDocksHandler createNewWorkbenchDocksHandler() {
        List<UberfireDock> docks = new ArrayList<>();

        docks.add(new UberfireDock(UberfireDockPosition.EAST,
                                   "RANDOM",
                                   placeRequest));
        docks.add(new UberfireDock(UberfireDockPosition.EAST,
                                   "RANDOM",
                                   placeRequest));

        return new TestWorkbenchDocksHandler(docks);
    }

    public class TestWorkbenchDocksHandler extends AbstractWorkbenchDocksHandler {

        private List<UberfireDock> docks;

        public TestWorkbenchDocksHandler(List<UberfireDock> docks) {
            this.docks = docks;
        }

        @Override
        public Collection<UberfireDock> provideDocks(String perspectiveIdentifier) {
            return docks;
        }

        public void refresh(boolean shouldRefresh,
                            boolean shouldDisable) {
            refreshDocks(shouldRefresh,
                         shouldDisable);
        }
    }
}
