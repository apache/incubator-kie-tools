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


package org.uberfire.client.workbench;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.UIPart;
import org.uberfire.client.workbench.events.PanelFocusEvent;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.events.PlaceHiddenEvent;
import org.uberfire.client.workbench.events.PlaceLostFocusEvent;
import org.uberfire.client.workbench.events.PlaceMaximizedEvent;
import org.uberfire.client.workbench.events.PlaceMinimizedEvent;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.panels.impl.LayoutPanelPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class PanelManagerTest {

    @Mock
    BeanFactory beanFactory;
    @Mock
    StubPlaceGainFocusEvent placeGainFocusEvent;
    @Mock
    StubPlaceLostFocusEvent placeLostFocusEvent;
    @Mock
    StubSelectPlaceEvent selectPlaceEvent;
    @Mock
    StubPanelFocusEvent panelFocusEvent;
    @Mock
    StubPlaceMaximizedEvent placeMaximizedEvent;
    @Mock
    StubPlaceMinimizedEvent placeMinimizedEvent;
    @Mock
    StubPlaceHiddenEvent placeHidEvent;
    @Mock
    LayoutPanelPresenter workbenchPanelPresenter;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    LayoutSelection layoutSelection;
    @Mock
    Elemental2DomUtil elemental2DomUtil;

    private PanelManagerImpl panelManager;

    /**
     * This is the part presenter that will be returned by the mock BeanFactory in response to any newWorkbenchPart()
     * call. Tests that work with more than one part will have to create their own more specific when/then rules.
     */
    private WorkbenchPartPresenter partPresenter;

    /**
     * This perspective is set as the current perspective on the PanelManager before each test is run.
     */
    private PerspectiveDefinition testPerspectiveDef;

    /**
     * This is the Panel Presenter returned by the mock BeanFactory when asked for <tt>newWorkbenchPanel( testPerspectiveDef.getRoot() ) )</tt>.
     */
    private WorkbenchPanelPresenter testPerspectiveRootPanelPresenter;

    @Before
    public void setup() {
        when(layoutSelection.get().getPerspectiveContainer()).thenReturn(mock(HasWidgets.class));

        testPerspectiveDef = new PerspectiveDefinitionImpl(LayoutPanelPresenter.class.getName());
        testPerspectiveRootPanelPresenter = mock(WorkbenchPanelPresenter.class);

        when(beanFactory.newRootPanel(any(),
                                      eq(testPerspectiveDef.getRoot()))).thenReturn(testPerspectiveRootPanelPresenter);
        when(testPerspectiveRootPanelPresenter.getDefinition()).thenReturn(testPerspectiveDef.getRoot());
        when(testPerspectiveRootPanelPresenter.getPanelView()).thenReturn(mock(WorkbenchPanelView.class));
        when(testPerspectiveRootPanelPresenter.getDefaultChildType()).thenReturn(LayoutPanelPresenter.class.getName());

        partPresenter = mock(WorkbenchPartPresenter.class);
        when(beanFactory.newWorkbenchPart(any(),
                                          any(),
                                          any(),
                                          any())).thenReturn(partPresenter);

        when(beanFactory.newWorkbenchPanel(any())).thenAnswer(new Answer<WorkbenchPanelPresenter>() {
            @Override
            public WorkbenchPanelPresenter answer(InvocationOnMock invocation) throws Throwable {
                WorkbenchPanelPresenter newPanelPresenter = mock(WorkbenchPanelPresenter.class,
                                                                 RETURNS_DEEP_STUBS);
                when(newPanelPresenter.getDefinition()).thenReturn((PanelDefinition) invocation.getArguments()[0]);
                return newPanelPresenter;
            }
        });

        PerspectiveActivity testPerspectiveActivity = mock(PerspectiveActivity.class);

        panelManager = spy(new PanelManagerImpl(placeGainFocusEvent,
                                                placeLostFocusEvent,
                                                panelFocusEvent,
                                                selectPlaceEvent,
                                                placeMaximizedEvent,
                                                placeMinimizedEvent,
                                                placeHidEvent,
                                                null,
                                                null,
                                                layoutSelection,
                                                beanFactory,
                                                elemental2DomUtil));
        panelManager.setRoot(testPerspectiveActivity,
                             testPerspectiveDef.getRoot());

        doNothing().when(panelManager).appendWidgetToElement(any(HTMLElement.class),
                                                             any(Widget.class));
    }

    @Test
    public void addPartToRootPanelShouldWork() throws Exception {
        PlaceRequest rootPartPlace = new DefaultPlaceRequest("rootPartPlace");
        PartDefinition rootPart = new PartDefinitionImpl(rootPartPlace);
        UIPart rootUiPart = new UIPart("RootUiPart",
                null,
                mock(IsWidget.class));
        panelManager.addWorkbenchPart(rootPartPlace,
                rootPart,
                panelManager.getRoot(),
                rootUiPart,
                "rootContextId",
                100,
                200);

        // the presenter should have been created and configured for the rootPart
        verify(partPresenter).setWrappedWidget(rootUiPart.getWidget());
        verify(partPresenter).setContextId("rootContextId");

        // the panel manager should be aware of the place/part mapping for the added part
        assertEquals(rootPart,
                panelManager.getPartForPlace(rootPartPlace));

        // the panel manager should have modified the panel or part definitions (this is the responsibility of the parent panel)
        assertEquals(null,
                rootPart.getParentPanel());
        assertFalse(panelManager.getRoot().getParts().contains(rootPart));
    }

    @Test
    public void addPartToUnknownPanelShouldFail() throws Exception {
        PlaceRequest partPlace = new DefaultPlaceRequest("partPlace");
        PartDefinition part = new PartDefinitionImpl(partPlace);
        UIPart uiPart = new UIPart("uiPart",
                null,
                mock(IsWidget.class));
        PanelDefinition randomUnattachedPanel = new PanelDefinitionImpl(LayoutPanelPresenter.class.getName());

        try {
            panelManager.addWorkbenchPart(partPlace,
                    part,
                    randomUnattachedPanel,
                    uiPart,
                    "contextId",
                    null,
                    null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Target panel is not part of the layout",
                    e.getMessage());
        }

        // the presenter should not have been created and configured for the rootPart
        verify(partPresenter,
                never()).setWrappedWidget(uiPart.getWidget());
        verify(partPresenter,
                never()).setContextId("rootContextId");

        // the panel manager should not be aware of the place/part mapping for the failed add
        assertEquals(null,
                panelManager.getPartForPlace(partPlace));

    }

    @Test
    public void removingLastPartFromRootPanelShouldLeaveRootPanel() throws Exception {
        // add
        PlaceRequest rootPartPlace = new DefaultPlaceRequest("rootPartPlace");
        PartDefinition rootPart = new PartDefinitionImpl(rootPartPlace);
        UIPart rootUiPart = new UIPart("RootUiPart",
                null,
                mock(IsWidget.class));
        panelManager.addWorkbenchPart(rootPartPlace,
                rootPart,
                panelManager.getRoot(),
                rootUiPart,
                "rootContextId",
                null,
                null);

        panelManager.removePartForPlace(rootPartPlace);

        // the panel manager should not know about the part/place mapping anymore
        assertEquals(null,
                panelManager.getPartForPlace(rootPartPlace));

        // the part's presenter bean should be destroyed
        verify(beanFactory).destroy(partPresenter);

        // the root panel itself, although empty, should remain (because it is the root panel)
        verify(beanFactory,
                never()).destroy(testPerspectiveRootPanelPresenter);
    }

    @Test
    public void addPanelAtRootPositionShouldReturnRootPanel() throws Exception {
        when(beanFactory.newRootPanel(any(PerspectiveActivity.class),
                                      eq(testPerspectiveDef.getRoot()))).thenReturn(testPerspectiveRootPanelPresenter);
        when(testPerspectiveRootPanelPresenter.getDefaultChildType()).thenReturn(null);
        PerspectiveActivity testPerspectiveActivity = mock(PerspectiveActivity.class);
        panelManager.setRoot(testPerspectiveActivity,
                             testPerspectiveDef.getRoot());

        PanelDefinition notActuallyAdded = new PanelDefinitionImpl(LayoutPanelPresenter.class.getName());
        PanelDefinition result = panelManager.addWorkbenchPanel(testPerspectiveDef.getRoot(),
                                                                notActuallyAdded,
                                                                CompassPosition.ROOT);
        assertSame(result,
                   testPerspectiveDef.getRoot());
    }

    @Test
    public void addedPanelsShouldBeRemembered() throws Exception {
        PanelDefinition subPanel = new PanelDefinitionImpl(LayoutPanelPresenter.class.getName());
        testPerspectiveDef.getRoot().appendChild(CompassPosition.WEST,
                subPanel);

        panelManager.addWorkbenchPanel(panelManager.getRoot(),
                subPanel,
                CompassPosition.WEST);

        assertTrue(panelManager.mapPanelDefinitionToPresenter.containsKey(subPanel));
    }

    @Test
    public void addedCustomPanelsShouldBeRemembered() throws Exception {
        HasWidgets container = mock(HasWidgets.class);
        PanelDefinition customPanel = panelManager.addCustomPanel(container,
                LayoutPanelPresenter.class.getName());

        assertTrue(panelManager.mapPanelDefinitionToPresenter.containsKey(customPanel));
    }

    @Test
    public void addedCustomPanelsInsideHTMLElementsShouldBeRemembered() throws Exception {
        HTMLElement container = mock(HTMLElement.class);
        PanelDefinition customPanel = panelManager.addCustomPanel(container,
                LayoutPanelPresenter.class.getName());

        assertTrue(panelManager.mapPanelDefinitionToPresenter.containsKey(customPanel));
    }

    @Test
    public void explicitlyRemovedPanelsShouldBeForgotten() throws Exception {
        PanelDefinition subPanel = new PanelDefinitionImpl(LayoutPanelPresenter.class.getName());
        testPerspectiveDef.getRoot().appendChild(CompassPosition.WEST,
                subPanel);

        panelManager.addWorkbenchPanel(panelManager.getRoot(),
                subPanel,
                CompassPosition.WEST);
        panelManager.removeWorkbenchPanel(subPanel);

        assertFalse(panelManager.mapPanelDefinitionToPresenter.containsKey(subPanel));
    }

    @Test
    public void explicitlyRemovedCustomPanelsShouldBeForgotten() throws Exception {
        HasWidgets container = mock(HasWidgets.class);
        PanelDefinition customPanel = panelManager.addCustomPanel(container,
                LayoutPanelPresenter.class.getName());
        panelManager.removeWorkbenchPanel(customPanel);

        assertFalse(panelManager.mapPanelDefinitionToPresenter.containsKey(customPanel));
    }

    @Test
    public void explicitlyRemovedCustomPanelsInsideHTMLElementsShouldBeForgotten() throws Exception {
        HTMLElement container = mock(HTMLElement.class);
        PanelDefinition customPanel = panelManager.addCustomPanel(container,
                LayoutPanelPresenter.class.getName());
        panelManager.removeWorkbenchPanel(customPanel);

        assertFalse(panelManager.mapPanelDefinitionToPresenter.containsKey(customPanel));
    }

    @Test
    public void explicitlyRemovedCustomPanelsInsideElemental2HTMLElementsShouldBeForgotten() {
        elemental2.dom.HTMLElement container = mock(elemental2.dom.HTMLElement.class);
        PanelDefinition customPanel = panelManager.addCustomPanel(container,
                LayoutPanelPresenter.class.getName());
        panelManager.removeWorkbenchPanel(customPanel);

        assertFalse(panelManager.mapPanelDefinitionToPresenter.containsKey(customPanel));
    }

    @Test
    public void explicitlyRemovingRootPanelShouldFail() throws Exception {
        try {
            panelManager.removeWorkbenchPanel(testPerspectiveDef.getRoot());
            fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("root"));
        }
    }

    // After UF-117:
    // TODO test part disposal (not NORTH/SOUTH/EAST/WEST) side effect of AbstractPanelManagerImpl.removePart()
    // TODO test part reattachment (NORTH/SOUTH/EAST/WEST) side effect of AbstractPanelManagerImpl.removePart()

    static class StubPlaceGainFocusEvent extends EventSourceMock<PlaceGainFocusEvent> {

    }

    static class StubPlaceLostFocusEvent extends EventSourceMock<PlaceLostFocusEvent> {

    }

    static class StubSelectPlaceEvent extends EventSourceMock<SelectPlaceEvent> {

    }

    static class StubPanelFocusEvent extends EventSourceMock<PanelFocusEvent> {

    }

    static class StubPlaceMaximizedEvent extends EventSourceMock<PlaceMaximizedEvent> {

    }

    static class StubPlaceMinimizedEvent extends EventSourceMock<PlaceMinimizedEvent> {

    }

    static class StubPlaceHiddenEvent extends EventSourceMock<PlaceHiddenEvent> {

    }
}
