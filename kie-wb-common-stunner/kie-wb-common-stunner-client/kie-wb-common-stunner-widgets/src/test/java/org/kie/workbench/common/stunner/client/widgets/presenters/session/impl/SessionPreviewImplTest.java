/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.presenters.session.impl;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.presenters.AbstractCanvasHandlerViewerTest;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionViewer;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasFactory;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandlerProxy;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasValidationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.docking.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.drag.DragControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class SessionPreviewImplTest extends AbstractCanvasHandlerViewerTest {

    private static final String DEFINITION_SET_ID = "definitionSetId";

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private GraphUtils graphUtils;

    @Mock
    private ShapeManager shapeManager;

    @Mock
    private ManagedInstance<CanvasCommandFactory> canvasCommandFactories;

    @Mock
    private SelectionControl<CanvasHandlerProxy, ?> selectionControl;

    @Mock
    private CanvasCommandManager<CanvasHandlerProxy> canvasCommandManager;

    @Mock
    private WidgetWrapperView view;

    @Mock
    private AbstractClientFullSession session;

    @Mock
    private SessionViewer.SessionViewerCallback<AbstractClientSession, Diagram> callback;

    @Mock
    private ZoomControl<AbstractCanvas> zoomControl;

    @Mock
    private CanvasValidationControl<AbstractCanvasHandler> validationControl;

    @Mock
    private ConnectionAcceptorControl<AbstractCanvasHandler> connectionAcceptorControl;

    @Mock
    private ContainmentAcceptorControl<AbstractCanvasHandler> containmentAcceptorControl;

    @Mock
    private DockingAcceptorControl<AbstractCanvasHandler> dockingAcceptorControl;

    @Mock
    private DragControl<AbstractCanvasHandler, Element> dragControl;

    @Mock
    private CanvasFactory canvasFactory;

    @Mock
    private Metadata metaData;

    @Mock
    private Annotation qualifier;

    @Mock
    private ManagedInstance customInstance;

    @Mock
    private ManagedInstance defaultInstance;

    @Mock
    private CanvasCommandFactory customImplementation;

    @Mock
    private CanvasCommandFactory defaultImplementation;

    private SessionPreviewImpl preview;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        super.init();
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(session.getCanvas()).thenReturn(canvas);
        when(session.getZoomControl()).thenReturn(zoomControl);
        when(session.getValidationControl()).thenReturn(validationControl);
        when(session.getConnectionAcceptorControl()).thenReturn(connectionAcceptorControl);
        when(session.getContainmentAcceptorControl()).thenReturn(containmentAcceptorControl);
        when(session.getDockingAcceptorControl()).thenReturn(dockingAcceptorControl);
        when(session.getDragControl()).thenReturn(dragControl);
        when(shapeManager.getCanvasFactory(any(Diagram.class))).thenReturn(canvasFactory);
        when(canvasFactory.newCanvas()).thenReturn(canvas);
        when(canvasFactory.newControl(eq(ZoomControl.class))).thenReturn(zoomControl);
        when(diagram.getMetadata()).thenReturn(metaData);
        when(metaData.getDefinitionSetId()).thenReturn(DEFINITION_SET_ID);
        when(definitionUtils.getQualifier(eq(DEFINITION_SET_ID))).thenReturn(qualifier);
        when(customInstance.get()).thenReturn(customImplementation);
        when(defaultInstance.get()).thenReturn(defaultImplementation);
        when(canvasCommandFactories.select(eq(qualifier))).thenReturn(customInstance);
        when(canvasCommandFactories.select(eq(DefinitionManager.DEFAULT_QUALIFIER))).thenReturn(defaultInstance);
        when(customImplementation.draw()).thenReturn(mock(CanvasCommand.class));
        when(defaultImplementation.draw()).thenReturn(mock(CanvasCommand.class));

        this.preview = new SessionPreviewImpl(definitionManager,
                                              definitionUtils,
                                              graphUtils,
                                              shapeManager,
                                              canvasCommandFactories,
                                              selectionControl,
                                              canvasCommandManager,
                                              view);
    }

    @Test
    public void checkGetCanvasCommandFactory() {
        checkCanvasFactory(false,
                           (c) -> assertEquals(customImplementation,
                                               c));
        checkCanvasFactory(true,
                           (c) -> assertEquals(defaultImplementation,
                                               c));
    }

    private void checkCanvasFactory(final boolean isQualifierUnsatisfied,
                                    final Consumer<CanvasCommandFactory> assertion) {
        when(customInstance.isUnsatisfied()).thenReturn(isQualifierUnsatisfied);

        preview.open(session,
                     callback);

        final CanvasCommandFactory factory = preview.getCanvasCommandFactory();
        assertion.accept(factory);
    }
}
