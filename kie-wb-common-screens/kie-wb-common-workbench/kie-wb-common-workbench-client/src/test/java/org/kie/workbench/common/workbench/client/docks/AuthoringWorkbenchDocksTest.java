/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.workbench.client.docks;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.preferences.LibraryInternalPreferences;
import org.kie.workbench.common.workbench.client.docks.test.TestWorkbenchDocksHandler;
import org.kie.workbench.common.workbench.client.events.LayoutEditorFocusEvent;
import org.mockito.Mock;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDockReadyEvent;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent;
import org.uberfire.client.workbench.events.PlaceHiddenEvent;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AuthoringWorkbenchDocksTest {

    public static final String AUTHORING_PERSPECTIVE = "authoring";

    @Mock
    private UberfireDocks uberfireDocks;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private PlaceRequest placeRequest;

    @Mock
    private LibraryInternalPreferences libraryInternalPreferences;

    @Mock
    private ManagedInstance<WorkbenchDocksHandler> handlers;

    private TestWorkbenchDocksHandler handler;

    private AuthoringWorkbenchDocks authoringWorkbenchDocks;

    @Before
    public void initTest() {

        handler = createNewWorkbenchDocksHandler();

        List<WorkbenchDocksHandler> list = new ArrayList<>();

        list.add(handler);

        when(handlers.iterator()).thenReturn(list.iterator());

        authoringWorkbenchDocks = spy(new AuthoringWorkbenchDocks(uberfireDocks,
                                                                  handlers,
                                                                  libraryInternalPreferences));

        authoringWorkbenchDocks.initialize();

        verify(handlers).iterator();

        authoringWorkbenchDocks.setup(AUTHORING_PERSPECTIVE,
                                      placeRequest);

        verify(uberfireDocks).add(any());
        verify(uberfireDocks).hide(any(),
                                   any());

        authoringWorkbenchDocks.perspectiveChangeEvent(new UberfireDockReadyEvent(AUTHORING_PERSPECTIVE));

        verify(uberfireDocks).hide(any(),
                                   anyString());
    }

    @Test
    public void testLoadHandler() {
        handler.refresh(true,
                        false);

        verify(uberfireDocks,
               times(1)).hide(any(UberfireDockPosition.class),
                              anyString());
        //no other docks operations should have been invoked.
        verify(uberfireDocks).show(any(UberfireDockPosition.class),
                                   anyString());
        verify(uberfireDocks,
               times(1)).add(any(),
                             any());
    }

    @Test
    public void testShowComponentPalette() {
        authoringWorkbenchDocks.onLayoutEditorFocus(new LayoutEditorFocusEvent());
        verify(uberfireDocks).add(authoringWorkbenchDocks.componentPaletteDock);
        verify(uberfireDocks).open(authoringWorkbenchDocks.componentPaletteDock);
        assertTrue(authoringWorkbenchDocks.componentPaletteEnabled);
    }

    @Test
    public void testCloseComponentPalette() {
        authoringWorkbenchDocks.onLayoutEditorFocus(new LayoutEditorFocusEvent());
        reset(uberfireDocks);

        authoringWorkbenchDocks.onLayoutEditorClose(new PlaceHiddenEvent(new DefaultPlaceRequest("FormEditor")));
        verify(uberfireDocks).remove(authoringWorkbenchDocks.componentPaletteDock);
        assertFalse(authoringWorkbenchDocks.componentPaletteEnabled);
        verify(uberfireDocks, never()).open(any());
    }

    @Test
    public void testLoadHandlerReloadingDocks() {
        testLoadHandler();

        handler.refresh(true,
                        false);

        verify(uberfireDocks).hide(UberfireDockPosition.EAST,
                                   "authoring");
    }

    @Test
    public void hideTest() {
        authoringWorkbenchDocks.hide();

        verify(uberfireDocks).hide(UberfireDockPosition.WEST,
                                   "authoring");
        verify(uberfireDocks).hide(UberfireDockPosition.EAST,
                                   "authoring");
    }

    @Test
    public void testLoadHandlerDisablingDocks() {
        testLoadHandler();

        handler.refresh(true,
                        true);

        verify(uberfireDocks,
               never()).remove(any(),
                               any());
        verify(uberfireDocks,
               times(1)).show(any(),
                              any());
        verify(uberfireDocks,
               times(1)).add(any(),
                             any());

        verify(uberfireDocks).show(UberfireDockPosition.EAST,
                                   "authoring");
        // it's also disabled on setup!
        verify(uberfireDocks,
               times(2)).hide(any(),
                              any());
    }

    @Test
    public void testVerifyShowAndHide() {
        authoringWorkbenchDocks.show();

        verify(uberfireDocks).show(UberfireDockPosition.WEST,
                                   "authoring");
        verify(uberfireDocks,
               never()).show(UberfireDockPosition.EAST,
                             "authoring");

        authoringWorkbenchDocks.hide();

        // it's also disabled on setup!
        verify(uberfireDocks,
               times(2)).hide(any(),
                              any());
    }

    protected TestWorkbenchDocksHandler createNewWorkbenchDocksHandler() {
        List<UberfireDock> docks = new ArrayList<>();

        docks.add(new UberfireDock(UberfireDockPosition.EAST,
                                   "RANDOM",
                                   placeRequest,
                                   AUTHORING_PERSPECTIVE));
        docks.add(new UberfireDock(UberfireDockPosition.EAST,
                                   "RANDOM",
                                   placeRequest,
                                   AUTHORING_PERSPECTIVE));

        return new TestWorkbenchDocksHandler(docks);
    }

    @After
    public void afterTest() {
        authoringWorkbenchDocks.clear();
    }

    @Test
    public void projectExplorerExpandedEvent_NotProjectExplorerDock() {
        final UberfireDocksInteractionEvent uberfireDocksInteractionEvent = createUberfireDocksInteractionEvent(mock(UberfireDock.class),
                                                                                                                UberfireDocksInteractionEvent.InteractionType.OPENED);

        authoringWorkbenchDocks.projectExplorerExpandedEvent(uberfireDocksInteractionEvent);

        verify(authoringWorkbenchDocks,
               never()).setProjectExplorerExpandedPreference(anyBoolean());
    }

    @Test
    public void projectExplorerExpandedEvent_SelectedInteraction() {
        final UberfireDocksInteractionEvent uberfireDocksInteractionEvent = createUberfireDocksInteractionEvent(authoringWorkbenchDocks.projectExplorerDock,
                                                                                                                UberfireDocksInteractionEvent.InteractionType.OPENED);

        authoringWorkbenchDocks.projectExplorerExpandedEvent(uberfireDocksInteractionEvent);

        verify(authoringWorkbenchDocks).setProjectExplorerExpandedPreference(true);
    }

    @Test
    public void projectExplorerExpandedEvent_DeselectedInteraction() {
        final UberfireDocksInteractionEvent uberfireDocksInteractionEvent = createUberfireDocksInteractionEvent(authoringWorkbenchDocks.projectExplorerDock,
                                                                                                                UberfireDocksInteractionEvent.InteractionType.CLOSED);

        authoringWorkbenchDocks.projectExplorerExpandedEvent(uberfireDocksInteractionEvent);

        verify(authoringWorkbenchDocks).setProjectExplorerExpandedPreference(false);
    }

    @Test
    public void projectExplorerExpandedEvent_ResizeInteraction() {
        final UberfireDocksInteractionEvent uberfireDocksInteractionEvent = createUberfireDocksInteractionEvent(authoringWorkbenchDocks.projectExplorerDock,
                                                                                                                UberfireDocksInteractionEvent.InteractionType.RESIZED);

        authoringWorkbenchDocks.projectExplorerExpandedEvent(uberfireDocksInteractionEvent);

        verify(authoringWorkbenchDocks,
               never()).setProjectExplorerExpandedPreference(anyBoolean());
    }

    @Test
    public void projectExplorerExpandedEvent_WithNullTargetDock() {
        final UberfireDocksInteractionEvent uberfireDocksInteractionEvent = createUberfireDocksInteractionEvent(UberfireDockPosition.WEST,
                                                                                                                UberfireDocksInteractionEvent.InteractionType.RESIZED);

        authoringWorkbenchDocks.projectExplorerExpandedEvent(uberfireDocksInteractionEvent);

        verify(authoringWorkbenchDocks,
               never()).setProjectExplorerExpandedPreference(anyBoolean());
    }

    @Test
    public void expandAuthoringDock() {
        final UberfireDock dockToOpen = mock(UberfireDock.class);
        authoringWorkbenchDocks.expandAuthoringDock(dockToOpen);

        verify(uberfireDocks).show(UberfireDockPosition.EAST, AUTHORING_PERSPECTIVE);
        verify(uberfireDocks).open(dockToOpen);
    }

    @Test
    public void doNotExpandAuthoringDockWhenTheDockIsNull() {
        reset(uberfireDocks);

        authoringWorkbenchDocks.expandAuthoringDock(null);

        verify(uberfireDocks).show(UberfireDockPosition.EAST, AUTHORING_PERSPECTIVE);
        verify(uberfireDocks, never()).open(any());
    }

    private UberfireDocksInteractionEvent createUberfireDocksInteractionEvent(final UberfireDock uberfireDock,
                                                                              final UberfireDocksInteractionEvent.InteractionType interactionType) {
        return new UberfireDocksInteractionEvent(uberfireDock,
                                                 interactionType);
    }

    private UberfireDocksInteractionEvent createUberfireDocksInteractionEvent(final UberfireDockPosition position,
                                                                              final UberfireDocksInteractionEvent.InteractionType interactionType) {
        return new UberfireDocksInteractionEvent(position,
                                                 interactionType);
    }
}