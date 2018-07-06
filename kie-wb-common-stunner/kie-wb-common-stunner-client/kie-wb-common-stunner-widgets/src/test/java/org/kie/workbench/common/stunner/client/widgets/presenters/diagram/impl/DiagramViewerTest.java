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

package org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.presenters.AbstractCanvasHandlerViewerTest;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.client.ManagedInstanceStub;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasSettings;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistries;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.preferences.StunnerPreferences;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DiagramViewerTest extends AbstractCanvasHandlerViewerTest {

    private static final String DEFINITION_SET_ID = "definitionSetId";

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private ZoomControl<AbstractCanvas> zoomControlInstance;
    private ManagedInstance<ZoomControl<AbstractCanvas>> zoomControl;

    @Mock
    private SelectionControl<AbstractCanvasHandler, Element> selectionControlInstance;
    private ManagedInstance<SelectionControl<AbstractCanvasHandler, Element>> selectionControl;

    private ManagedInstance<AbstractCanvas> canvases;
    private ManagedInstance<AbstractCanvasHandler> canvasHandlers;

    @Mock
    WidgetWrapperView view;

    @Mock
    DiagramViewer.DiagramViewerCallback<Diagram> callback;

    @Mock
    CanvasSettings canvasSettings;

    @Mock
    StunnerPreferencesRegistries preferencesRegistries;

    @Mock
    StunnerPreferences stunnerPreferences;

    private DefaultDiagramViewer tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        super.init();
        canvases = spy(new ManagedInstanceStub<>(canvas));
        canvasHandlers = spy(new ManagedInstanceStub<>(canvasHandler));
        zoomControl = spy(new ManagedInstanceStub<>(zoomControlInstance));
        selectionControl = spy(new ManagedInstanceStub<>(selectionControlInstance));
        when(metadata.getDefinitionSetId()).thenReturn(DEFINITION_SET_ID);
        when(preferencesRegistries.get(DEFINITION_SET_ID)).thenReturn(stunnerPreferences);
        this.tested =
                new DefaultDiagramViewer(definitionUtils,
                                         canvases,
                                         canvasHandlers,
                                         zoomControl,
                                         selectionControl,
                                         view,
                                         preferencesRegistries);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOpen() {
        tested.open(diagram,
                    callback);
        assertEquals(diagram,
                     tested.getInstance());
        verify(canvas,
               times(1)).initialize(argThat(new ArgumentMatcher<CanvasSettings>() {
            @Override
            public boolean matches(Object emp) {
                CanvasSettings settings = (CanvasSettings)emp;
                return !settings.isHiDPIEnabled()
                        && settings.getHeight() == 100
                        && settings.getWidth() == 100;
            }
        }));
        verify(canvasHandler,
               times(1)).handle(eq(canvas));
        verify(canvasHandler,
               times(1)).draw(eq(diagram),
                              any(ParameterizedCommand.class));
        verify(callback,
               times(1)).afterCanvasInitialized();
        verify(zoomControlInstance,
               times(1)).init(eq(canvas));
        verify(selectionControlInstance,
               times(1)).init(eq(canvasHandler));
        verify(view,
               times(1)).setWidget(eq(canvasViewWidget));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testScale() {
        tested.open(diagram,
                    callback);
        tested.scale(50,
                     50);
        assertEquals(diagram,
                     tested.getInstance());
        verify(zoomControlInstance,
               times(1)).scale(eq(0.5d),
                               eq(0.5d));
        verify(lienzoPanel,
               times(1)).setPixelSize(50,
                                      50);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testClear() {
        tested.open(diagram,
                    callback);
        tested.clear();
        assertNull(tested.getInstance());
        verify(canvasHandler,
               times(1)).clear();
        verify(view,
               times(1)).clear();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDestroy() {
        tested.open(diagram,
                    callback);
        tested.destroy();
        assertNull(tested.getInstance());
        verify(zoomControl,
               times(1)).destroyAll();
        verify(selectionControl,
               times(1)).destroyAll();
        verify(canvases,
               times(1)).destroyAll();
        verify(canvasHandlers,
               times(1)).destroyAll();
        verify(view,
               times(1)).clear();
    }
}
