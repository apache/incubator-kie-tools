/*
 * Copyright 2015 JBoss Inc
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

package org.uberfire.client.docks.view;

import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.docks.view.bars.DocksCollapsedBar;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.toolbar.IconType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DocksBarsTest {

    @Mock
    private DockLayoutPanel dockLayoutPanel;

    private DocksBars docksBars;

    private final String SOME_PERSPECTIVE = "SomePerspective";

    private UberfireDock dock0 = new UberfireDock(UberfireDockPosition.SOUTH, IconType.CHEVRON_RIGHT.name(), new DefaultPlaceRequest("welcome"), SOME_PERSPECTIVE).withLabel("albel");


    @Before
    public void setup() {
        docksBars = new DocksBars();

    }

    @Test
    public void setupDocks() {
        docksBars.setup(dockLayoutPanel);

        assertEquals(3, docksBars.getDocksBars().size());
        verify(dockLayoutPanel, times(3)).addEast(any(Widget.class), any(Double.class));
        verify(dockLayoutPanel, times(3)).addWest(any(Widget.class), any(Double.class));
        verify(dockLayoutPanel, times(3)).addSouth(any(Widget.class), any(Double.class));

    }

    @Test
    public void addDock() {
        docksBars.setup(dockLayoutPanel);

        DocksBar dockBar = docksBars.getDockBar(dock0);
        DocksBar targetDockSpy = spy(dockBar);

        DocksBars dockBarsSpy = spy(docksBars);
        when(dockBarsSpy.getDockBar(dock0)).thenReturn(targetDockSpy);

        dockBarsSpy.addDock(dock0);

        verify(targetDockSpy).addDock(eq(dock0), any(ParameterizedCommand.class), any(ParameterizedCommand.class));

    }

    @Test
    public void getDockBar() {
        docksBars.setup(dockLayoutPanel);
        DocksBar targetDockBar = docksBars.getDockBar(dock0);
        assertEquals(dock0.getDockPosition(), targetDockBar.getPosition());
    }

    @Test
    public void clearAndCollapseAllDocks() {

        docksBars.setup(dockLayoutPanel);
        DocksBars docksBarsSpy = spy(docksBars);
        List<DocksBar> dockBars = new ArrayList<DocksBar>();
        DocksBar dock1 = createDocksBarMock();
        dockBars.add(dock1);
        DocksBar dock2 = createDocksBarMock();
        dockBars.add(dock2);
        when(docksBarsSpy.getDocksBars()).thenReturn(dockBars);

        docksBarsSpy.clearAndCollapseAllDocks();

        verify(dock1).clearAll();
        verify(dock2).clearAll();
        //2 for each dock(collapsed/expanded/resize)
        verify(dockLayoutPanel, times(6)).setWidgetHidden(any(Widget.class), eq(true));

    }

    @Test
    public void expand() {
        docksBars.setup(dockLayoutPanel);
        docksBars.addDock(dock0);

        docksBars.expand(docksBars.getDockBar(dock0));

        verify(dockLayoutPanel, times(1)).setWidgetHidden(any(Widget.class), eq(false));
    }


    @Test
    public void isReady() {
        assertFalse(docksBars.isReady());
        docksBars.setup(dockLayoutPanel);
        assertTrue(docksBars.isReady());
    }

    private DocksBar createDocksBarMock() {
        DocksBar mock = mock(DocksBar.class);
        when(mock.getCollapsedBar()).thenReturn(mock(DocksCollapsedBar.class));
        return mock;
    }
}