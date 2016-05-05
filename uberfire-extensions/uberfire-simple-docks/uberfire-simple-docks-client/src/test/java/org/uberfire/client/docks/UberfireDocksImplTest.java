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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.docks.view.DocksBar;
import org.uberfire.client.docks.view.DocksBars;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.toolbar.IconType;

import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class UberfireDocksImplTest {

    @Mock
    private DocksBars docksBars;

    @Mock
    private DockLayoutPanel dockLayoutPanel;

    private UberfireDocksImpl uberfireDocks;
    private Command resizeCommand;


    private final String SOME_PERSPECTIVE = "SomePerspective";
    private final String ANOTHER_PERSPECTIVE = "AnotherPerspective";

    private UberfireDock dock0 = new UberfireDock(UberfireDockPosition.SOUTH, IconType.CHEVRON_RIGHT.name(), new DefaultPlaceRequest("welcome"), SOME_PERSPECTIVE).withLabel("albel");
    private UberfireDock dock1 = new UberfireDock(UberfireDockPosition.SOUTH, IconType.AMBULANCE.name(), new DefaultPlaceRequest("another"), SOME_PERSPECTIVE).withLabel("Another").withSize(200);
    private UberfireDock dock2 = new UberfireDock(UberfireDockPosition.EAST, IconType.ADJUST.name(), new DefaultPlaceRequest("test"), SOME_PERSPECTIVE);
    private UberfireDock dock3 = new UberfireDock(UberfireDockPosition.EAST, IconType.BELL_ALT.name(), new DefaultPlaceRequest("welcome"), ANOTHER_PERSPECTIVE);
    private UberfireDock dock4 = new UberfireDock(UberfireDockPosition.WEST, IconType.FACETIME_VIDEO.name(), new DefaultPlaceRequest("welcome"), ANOTHER_PERSPECTIVE).withLabel("Welcome").withSize(200);


    @Before
    public void setup() {
        uberfireDocks = new UberfireDocksImpl(docksBars){
            @Override
            protected void fireEvent() {
            }
        };
        resizeCommand = new Command() {
            @Override
            public void execute() {
            }
        };
    }

    @Test
    public void setupDocks() {
        uberfireDocks.setup(dockLayoutPanel, resizeCommand);
        verify(docksBars).setup(dockLayoutPanel, resizeCommand);
    }

    @Test
    public void configure() {

        Map<String, String> configurations = new HashMap<String, String>();
        configurations.put(UberfireDocksImpl.IDE_DOCK, "true");

        uberfireDocks.configure(configurations);

        verify(docksBars).setIDEdock(true);
    }

    @Test
    public void add() {

        uberfireDocks.add(dock0, dock1, dock2, dock3, dock4);

        List<UberfireDock> docksSomePerspective = uberfireDocks.docksPerPerspective.get(SOME_PERSPECTIVE);
        List<UberfireDock> docksAnotherPerspective = uberfireDocks.docksPerPerspective.get(ANOTHER_PERSPECTIVE);

        assertEquals(3, docksSomePerspective.size());
        assertEquals(2, docksAnotherPerspective.size());

    }

    @Test
    public void perspectiveChangeEvent() {

        when(docksBars.isReady()).thenReturn(true);
        List<DocksBar> docksBars = generateDocksBars();
        when(this.docksBars.getDocksBars()).thenReturn(docksBars);

        uberfireDocks.add(dock0, dock1);

        uberfireDocks.perspectiveChangeEvent(new PerspectiveChange(null, null, null, SOME_PERSPECTIVE));

        assertEquals(SOME_PERSPECTIVE, uberfireDocks.currentSelectedPerspective);
        verify(this.docksBars, times(2)).clearAndCollapseAllDocks();
        verify(this.docksBars).addDock(dock0);
        verify(this.docksBars).addDock(dock1);

        verify(this.docksBars, times(docksBars.size())).expand(any(DocksBar.class));
    }

    @Test
    public void remove() {

        uberfireDocks.add(dock0, dock1);

        when(docksBars.isReady()).thenReturn(true);
        uberfireDocks.currentSelectedPerspective = SOME_PERSPECTIVE;

        uberfireDocks.remove(dock0);
        verify(docksBars).clearAndCollapseAllDocks();

        verify(docksBars, never()).addDock(dock0);
        verify(docksBars).addDock(dock1);
    }

    @Test
    public void disableDock() {
        when(docksBars.isReady()).thenReturn(true);
        uberfireDocks.add(dock0, dock1);
        uberfireDocks.currentSelectedPerspective = SOME_PERSPECTIVE;

        when(docksBars.isReady()).thenReturn(true);
        List<DocksBar> docksBars = generateDocksBars();
        when(this.docksBars.getDocksBars()).thenReturn(docksBars);

        uberfireDocks.disable(UberfireDockPosition.WEST, SOME_PERSPECTIVE);

        verify(this.docksBars).clearAndCollapse(UberfireDockPosition.WEST);


    }

    @Test
    public void enableDock() {
        when(docksBars.isReady()).thenReturn(true);
        uberfireDocks.add(dock0, dock1);
        uberfireDocks.currentSelectedPerspective = SOME_PERSPECTIVE;

        when(docksBars.isReady()).thenReturn(true);
        List<DocksBar> docksBars = generateDocksBars();
        when(this.docksBars.getDocksBars()).thenReturn(docksBars);

        uberfireDocks.disable(UberfireDockPosition.WEST, SOME_PERSPECTIVE);
        uberfireDocks.enable(UberfireDockPosition.WEST, SOME_PERSPECTIVE);
        verify(this.docksBars).expand(UberfireDockPosition.WEST);

    }

    private List<DocksBar> generateDocksBars() {

        List<DocksBar> docksBar = new ArrayList<DocksBar>();

        docksBar.add(createDocksBar(UberfireDockPosition.WEST));
        docksBar.add(createDocksBar(UberfireDockPosition.EAST));
        docksBar.add(createDocksBar(UberfireDockPosition.SOUTH));

        return docksBar;
    }

    private DocksBar createDocksBar(final UberfireDockPosition west) {
        return new DocksBar(west){
            @Override
            protected void setupChildBars(UberfireDockPosition position) {
            }

            @Override
            public boolean equals(Object obj) {
                DocksBar obj1 = (DocksBar) obj;
                return  getPosition().equals(obj1.getPosition());
            }
        };


    }
}