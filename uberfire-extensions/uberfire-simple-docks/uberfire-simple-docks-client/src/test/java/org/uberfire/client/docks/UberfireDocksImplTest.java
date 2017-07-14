/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.client.docks;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.docks.view.DocksBar;
import org.uberfire.client.docks.view.DocksBars;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockContainerReadyEvent;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDockReadyEvent;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.toolbar.IconType;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class UberfireDocksImplTest {

    private final String SOME_PERSPECTIVE = "SomePerspective";
    private final String ANOTHER_PERSPECTIVE = "AnotherPerspective";
    @Mock
    private DocksBars docksBars;
    @Mock
    private DockLayoutPanel dockLayoutPanel;
    @Mock
    EventSourceMock<UberfireDockReadyEvent> dockReadyEvent;
    private UberfireDocksImpl uberfireDocks;
    private Command resizeCommand;
    private UberfireDock dock0 = new UberfireDock(UberfireDockPosition.SOUTH,
                                                  IconType.CHEVRON_RIGHT.name(),
                                                  new DefaultPlaceRequest("welcome"),
                                                  SOME_PERSPECTIVE)
            .withLabel("albel");
    private UberfireDock dock1 = new UberfireDock(UberfireDockPosition.SOUTH,
                                                  IconType.AMBULANCE.name(),
                                                  new DefaultPlaceRequest("another"),
                                                  SOME_PERSPECTIVE)
            .withLabel("Another").withSize(200);
    private UberfireDock dock2 = new UberfireDock(UberfireDockPosition.EAST,
                                                  IconType.ADJUST.name(),
                                                  new DefaultPlaceRequest("test"),
                                                  SOME_PERSPECTIVE);
    private UberfireDock dock3 = new UberfireDock(UberfireDockPosition.EAST,
                                                  IconType.BELL_ALT.name(),
                                                  new DefaultPlaceRequest("welcome"),
                                                  ANOTHER_PERSPECTIVE);
    private UberfireDock dock4 = new UberfireDock(UberfireDockPosition.WEST,
                                                  IconType.FACETIME_VIDEO.name(),
                                                  new DefaultPlaceRequest("welcome"),
                                                  ANOTHER_PERSPECTIVE)
            .withLabel("Welcome").withSize(200);

    @Before
    public void setup() {
        uberfireDocks = new UberfireDocksImpl(docksBars,
                                              dockReadyEvent);
        resizeCommand = () -> {
        };
    }

    @Test
    public void setupDocks() {
        uberfireDocks.setup(new UberfireDockContainerReadyEvent());
        verify(docksBars).setup();
    }

    @Test
    public void add() {

        uberfireDocks.add(dock0,
                          dock1,
                          dock2,
                          dock3,
                          dock4);

        List<UberfireDock> docksSomePerspective = uberfireDocks.docksPerPerspective.get(SOME_PERSPECTIVE);
        List<UberfireDock> docksAnotherPerspective = uberfireDocks.docksPerPerspective.get(ANOTHER_PERSPECTIVE);

        assertEquals(3,
                     docksSomePerspective.size());
        assertEquals(2,
                     docksAnotherPerspective.size());
    }

    @Test
    public void perspectiveChangeEvent() {

        when(docksBars.isReady(any())).thenReturn(true);
        List<DocksBar> docksBars = generateDocksBars();
        when(this.docksBars.getDocksBars()).thenReturn(docksBars);

        uberfireDocks.add(dock0,
                          dock1);

        uberfireDocks.perspectiveChangeEvent(new PerspectiveChange(null,
                                                                   null,
                                                                   null,
                                                                   SOME_PERSPECTIVE));

        assertEquals(SOME_PERSPECTIVE,
                     uberfireDocks.currentPerspective);
        verify(this.docksBars,
               times(1)).clearAndHideAllDocks();
        verify(this.docksBars,
               times(1)).addDock(dock0);
        verify(this.docksBars,
               times(1)).addDock(dock1);

        verify(this.docksBars,
               times(docksBars.size())).show(any(DocksBar.class));
    }

    @Test
    public void remove() {
        uberfireDocks.add(dock0,
                          dock1);

        when(docksBars.isReady(any())).thenReturn(true);
        uberfireDocks.currentPerspective = SOME_PERSPECTIVE;

        uberfireDocks.remove(dock0);

        verify(docksBars,
               times(2)).isReady(any());

        verify(docksBars,
               times(1)).clearAndCollapseDocks(any());

        verify(docksBars,
               never()).addDock(dock0);
        verify(docksBars,
               never()).addDock(dock1);
    }

    @Test
    public void disableDock() {
        when(docksBars.isReady(any())).thenReturn(true);
        uberfireDocks.add(dock0,
                          dock1);
        uberfireDocks.currentPerspective = SOME_PERSPECTIVE;

        when(docksBars.isReady(any())).thenReturn(true);
        List<DocksBar> docksBars = generateDocksBars();
        when(this.docksBars.getDocksBars()).thenReturn(docksBars);

        uberfireDocks.hide(UberfireDockPosition.WEST,
                           SOME_PERSPECTIVE);

        verify(this.docksBars).clearAndHide(UberfireDockPosition.WEST);
    }

    @Test
    public void enableDock() {
        when(docksBars.isReady(any())).thenReturn(true);
        uberfireDocks.add(dock0,
                          dock1);
        uberfireDocks.currentPerspective = SOME_PERSPECTIVE;

        when(docksBars.isReady(any())).thenReturn(true);
        List<DocksBar> docksBars = generateDocksBars();
        when(this.docksBars.getDocksBars()).thenReturn(docksBars);

        uberfireDocks.hide(UberfireDockPosition.WEST,
                           SOME_PERSPECTIVE);
        uberfireDocks.show(UberfireDockPosition.WEST,
                           SOME_PERSPECTIVE);
        verify(this.docksBars).show(UberfireDockPosition.WEST);
    }

    @Test
    public void closeDockTest() {
        when(docksBars.isReady(any())).thenReturn(true);
        uberfireDocks.add(dock0,
                          dock1);
        uberfireDocks.currentPerspective = SOME_PERSPECTIVE;

        when(docksBars.isReady(any())).thenReturn(true);
        List<DocksBar> docksBars = generateDocksBars();
        when(this.docksBars.getDocksBars()).thenReturn(docksBars);

        uberfireDocks.open(dock0);
        uberfireDocks.close(dock0);

        verify(this.docksBars).close(dock0);
    }

    @Test
    public void toggleDockTest() {
        when(docksBars.isReady(any())).thenReturn(true);
        uberfireDocks.add(dock0,
                          dock1);
        uberfireDocks.currentPerspective = SOME_PERSPECTIVE;

        when(docksBars.isReady(any())).thenReturn(true);
        List<DocksBar> docksBars = generateDocksBars();
        when(this.docksBars.getDocksBars()).thenReturn(docksBars);

        uberfireDocks.toggle(dock0);
        verify(this.docksBars).toggle(dock0);
    }

    @Test
    public void openCloseDelayedOperationsTest() {
        when(docksBars.isReady(any())).thenReturn(false);
        uberfireDocks.add(dock0,
                          dock1);
        uberfireDocks.currentPerspective = SOME_PERSPECTIVE;

        List<DocksBar> docksBars = generateDocksBars();
        when(this.docksBars.getDocksBars()).thenReturn(docksBars);

        uberfireDocks.open(dock0);
        verify(this.docksBars,
               never()).open(dock0);

        uberfireDocks.close(dock0);
        verify(this.docksBars,
               never()).close(dock0);

        assertEquals(2,
                     uberfireDocks.delayedCommandsPerPerspective.get(SOME_PERSPECTIVE).size());

        PerspectiveChange event = mock(PerspectiveChange.class);
        when(event.getIdentifier()).thenReturn("Another");

        uberfireDocks.perspectiveChangeEvent(event);

        verify(this.docksBars,
               never()).open(dock0);
        verify(this.docksBars,
               never()).close(dock0);

        when(event.getIdentifier()).thenReturn(SOME_PERSPECTIVE);
        uberfireDocks.perspectiveChangeEvent(event);

        verify(this.docksBars).open(dock0);
        verify(this.docksBars).close(dock0);

        assertNull(uberfireDocks.delayedCommandsPerPerspective.get(SOME_PERSPECTIVE));
    }

    private List<DocksBar> generateDocksBars() {

        List<DocksBar> docksBar = new ArrayList<DocksBar>();

        docksBar.add(createDocksBar(UberfireDockPosition.WEST));
        docksBar.add(createDocksBar(UberfireDockPosition.EAST));
        docksBar.add(createDocksBar(UberfireDockPosition.SOUTH));

        return docksBar;
    }

    private DocksBar createDocksBar(final UberfireDockPosition west) {
        return new DocksBar(west) {
            @Override
            protected void setupChildBars(UberfireDockPosition position) {
            }

            @Override
            public boolean equals(Object obj) {
                DocksBar obj1 = (DocksBar) obj;
                return getPosition().equals(obj1.getPosition());
            }
        };
    }
}