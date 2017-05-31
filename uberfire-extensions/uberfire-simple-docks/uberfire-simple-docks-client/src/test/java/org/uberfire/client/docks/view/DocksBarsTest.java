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

package org.uberfire.client.docks.view;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.client.docks.view.bars.DocksCollapsedBar;
import org.uberfire.client.docks.view.menu.MenuBuilder;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocksContainer;
import org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.toolbar.IconType;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DocksBarsTest {

    private final String SOME_PERSPECTIVE = "SomePerspective";
    @Mock
    private UberfireDocksContainer uberfireDocksContainer;
    @Mock
    private PlaceManager placeManager;
    @Mock
    private MenuBuilder menuBuilder;
    @Mock
    private EventSourceMock<UberfireDocksInteractionEvent> dockInteractionEvent;
    private DocksBars docksBars;
    private UberfireDock dock0 = new UberfireDock(UberfireDockPosition.SOUTH,
                                                  IconType.CHEVRON_RIGHT.name(),
                                                  new DefaultPlaceRequest("welcome"),
                                                  SOME_PERSPECTIVE)
            .withLabel("label");

    @Before
    public void setup() {
        docksBars = new DocksBars(placeManager,
                                  menuBuilder,
                                  dockInteractionEvent,
                                  uberfireDocksContainer,
                                  null);
    }

    @Test
    public void setupDocks() {
        docksBars.setup();

        assertEquals(3,
                     docksBars.getDocksBars().size());
        verify(uberfireDocksContainer,
               times(3))
                .add(eq(UberfireDockPosition.EAST),
                     any(Widget.class),
                     any(Double.class));
        verify(uberfireDocksContainer,
               times(3))
                .add(eq(UberfireDockPosition.WEST),
                     any(Widget.class),
                     any(Double.class));
        verify(uberfireDocksContainer,
               times(3))
                .add(eq(UberfireDockPosition.SOUTH),
                     any(Widget.class),
                     any(Double.class));
    }

    @Test
    public void addDock() {
        docksBars.setup();

        DocksBar dockBar = docksBars.getDockBar(dock0);
        DocksBar targetDockSpy = spy(dockBar);

        DocksBars dockBarsSpy = spy(docksBars);
        when(dockBarsSpy.getDockBar(dock0)).thenReturn(targetDockSpy);

        dockBarsSpy.addDock(dock0);

        verify(targetDockSpy)
                .addDock(eq(dock0),
                         any(ParameterizedCommand.class),
                         any(ParameterizedCommand.class));
    }

    @Test
    public void getDockBar() {
        docksBars.setup();
        DocksBar targetDockBar = docksBars.getDockBar(dock0);
        assertEquals(dock0.getDockPosition(),
                     targetDockBar.getPosition());
    }

    @Test
    public void clearAndCollapseAllDocks() {

        docksBars.setup();
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
        verify(uberfireDocksContainer,
               times(6)).hide(any(Widget.class));
    }

    @Test
    public void clearAndCollapse() {

        docksBars.setup();
        DocksBars docksBarsSpy = spy(docksBars);
        DocksBar dock1 = createDocksBarMock();
        when(docksBarsSpy.getDockBar(UberfireDockPosition.EAST)).thenReturn(dock1);

        docksBarsSpy.clearAndCollapse(UberfireDockPosition.EAST);

        verify(dock1).clearAll();
        verify(docksBarsSpy).resizeDeferred();
        verify(uberfireDocksContainer,
               times(3)).hide(any(Widget.class));
    }

    @Test
    public void expand() {
        docksBars.setup();
        docksBars.addDock(dock0);

        docksBars.expand(docksBars.getDockBar(dock0));

        verify(uberfireDocksContainer,
               times(1)).show(any(Widget.class));
    }

    @Test
    public void dockSelectCommand() {
        DocksBars docksBarsSpy = spy(docksBars);

        docksBarsSpy.setup();
        docksBarsSpy.addDock(dock0);

        Mockito.doNothing().when(docksBarsSpy).selectDock(dock0,
                                                          docksBars.getDockBar(dock0));

        ParameterizedCommand<String> dockSelectCommand = docksBarsSpy
                .createDockSelectCommand(dock0,
                                         docksBars.getDockBar(dock0));

        dockSelectCommand.execute(dock0.getIdentifier());

        verify(docksBarsSpy).selectDock(dock0,
                                        docksBars.getDockBar(dock0));
        verify(uberfireDocksContainer).resize();
        verify(dockInteractionEvent,
               times(1)).fire(new UberfireDocksInteractionEvent(dock0,
                                                                UberfireDocksInteractionEvent.InteractionType.SELECTED));
    }

    @Test
    public void dockSelectCommandSingleMode() {
        DocksBars docksBarsSpy = spy(docksBars);

        docksBarsSpy.setup();
        docksBarsSpy.addDock(dock0);

        DocksBar dockBar = spy(docksBars.getDockBar(dock0));

        Mockito.doNothing().when(docksBarsSpy).selectDock(dock0,
                                                          dockBar);
        when(dockBar.isCollapsedBarInSingleMode()).thenReturn(true);

        ParameterizedCommand<String> dockSelectCommand = docksBarsSpy.createDockSelectCommand(dock0,
                                                                                              dockBar);

        dockSelectCommand.execute(dock0.getIdentifier());

        verify(docksBarsSpy).selectDock(dock0,
                                        dockBar);
        verify(uberfireDocksContainer).resize();
        verify(docksBarsSpy).collapse(dockBar.getCollapsedBar());
        verify(uberfireDocksContainer,
               times(1)).hide(any(Widget.class));
        verify(dockInteractionEvent,
               times(1)).fire(new UberfireDocksInteractionEvent(dock0,
                                                                UberfireDocksInteractionEvent.InteractionType.SELECTED));
    }

    @Test
    public void dockDeSelectCommand() {
        DocksBars spy = spy(docksBars);

        spy.setup();
        spy.addDock(dock0);

        Mockito.doNothing().when(spy).deselectDock(docksBars.getDockBar(dock0));

        ParameterizedCommand<String> dockSelectCommand = spy
                .createDockDeselectCommand(dock0,
                                           docksBars.getDockBar(dock0));

        dockSelectCommand.execute(dock0.getIdentifier());

        verify(spy).deselectDock(docksBars.getDockBar(dock0));
        verify(uberfireDocksContainer).resize();
        verify(dockInteractionEvent,
               times(1)).fire(new UberfireDocksInteractionEvent(dock0,
                                                                UberfireDocksInteractionEvent.InteractionType.DESELECTED));
    }

    @Test
    public void dockDeSelectCommandSingleMode() {
        DocksBars docksBarsSpy = spy(docksBars);

        docksBarsSpy.setup();
        docksBarsSpy.addDock(dock0);

        DocksBar dockBar = spy(docksBars.getDockBar(dock0));

        Mockito.doNothing().when(docksBarsSpy).deselectDock(dockBar);
        when(dockBar.isCollapsedBarInSingleMode()).thenReturn(true);

        ParameterizedCommand<String> dockSelectCommand = docksBarsSpy.createDockDeselectCommand(dock0,
                                                                                                dockBar);

        dockSelectCommand.execute(dock0.getIdentifier());

        verify(docksBarsSpy).deselectDock(dockBar);
        verify(uberfireDocksContainer).resize();
        verify(docksBarsSpy).expand(dockBar.getCollapsedBar());
        verify(dockInteractionEvent,
               times(1)).fire(new UberfireDocksInteractionEvent(dock0,
                                                                UberfireDocksInteractionEvent.InteractionType.DESELECTED));
    }

    @Test
    public void dockResizeCommand() {
        final double simulatedSize = 0d;
        final DocksBars docksBarsSpy = spy(docksBars);

        docksBarsSpy.setup();
        docksBarsSpy.addDock(dock0);

        Mockito.doReturn(true).when(docksBarsSpy).sizeIsValid(any(Double.class),
                                                              any(DocksBar.class));

        final DocksBar dockBar = spy(docksBars.getDockBar(dock0));
        ParameterizedCommand<Double> dockResizeCommand = docksBarsSpy.createResizeCommand(dockBar);

        dockResizeCommand.execute(simulatedSize);
        verify(dockBar).setExpandedSize(simulatedSize);
        verify(uberfireDocksContainer).resize();
        verify(dockInteractionEvent,
               times(1)).fire(new UberfireDocksInteractionEvent(UberfireDockPosition.SOUTH,
                                                                UberfireDocksInteractionEvent.InteractionType.RESIZED));
    }

    private DocksBar createDocksBarMock() {
        DocksBar mock = mock(DocksBar.class);
        when(mock.getCollapsedBar()).thenReturn(mock(DocksCollapsedBar.class));
        return mock;
    }
}
