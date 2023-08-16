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


package org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl;

import java.util.Iterator;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.canvas.ScrollableLienzoPanel;
import org.kie.workbench.common.stunner.client.widgets.presenters.AbstractCanvasHandlerViewerTest;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramViewer;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasPanel;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.LineSpliceAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.LocationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ResizeControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.EdgeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ControlPointControl;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasElementListener;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultCanvasElementListener;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.stubs.ManagedInstanceStub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DiagramEditorTest extends AbstractCanvasHandlerViewerTest {

    @Mock
    DefinitionUtils definitionUtils;

    @Mock
    DiagramViewer viewer;

    @Mock
    ScrollableLienzoPanel canvasPanel;

    @Mock
    CanvasCommandManager<AbstractCanvasHandler> commandManagerInstance;
    private ManagedInstance<CanvasCommandManager<AbstractCanvasHandler>> commandManager;

    @Mock
    LocationControl<AbstractCanvasHandler, Element> locationControlInstance;
    private ManagedInstance<LocationControl<AbstractCanvasHandler, Element>> locationControl;

    @Mock
    ResizeControl<AbstractCanvasHandler, Element> resizeControlInstance;
    private ManagedInstance<ResizeControl<AbstractCanvasHandler, Element>> resizeControl;

    @Mock
    ElementBuilderControl<AbstractCanvasHandler> builderControlInstance;
    private ManagedInstance<ElementBuilderControl<AbstractCanvasHandler>> builderControl;

    @Mock
    NodeBuilderControl<AbstractCanvasHandler> nodeBuilderControlInstance;
    private ManagedInstance<NodeBuilderControl<AbstractCanvasHandler>> nodeBuilderControl;

    @Mock
    EdgeBuilderControl<AbstractCanvasHandler> edgeBuilderControlInstance;
    private ManagedInstance<EdgeBuilderControl<AbstractCanvasHandler>> edgeBuilderControl;

    @Mock
    ControlPointControl<AbstractCanvasHandler> cpControlInstance;
    private ManagedInstance<ControlPointControl<AbstractCanvasHandler>> cpControl;

    @Mock
    ConnectionAcceptorControl<AbstractCanvasHandler> connectionAcceptorControlInstance;
    private ManagedInstance<ConnectionAcceptorControl<AbstractCanvasHandler>> connectionAcceptorControl;

    @Mock
    ContainmentAcceptorControl<AbstractCanvasHandler> containmentAcceptorControlInstance;
    private ManagedInstance<ContainmentAcceptorControl<AbstractCanvasHandler>> containmentAcceptorControl;

    @Mock
    DockingAcceptorControl<AbstractCanvasHandler> dockingAcceptorControlInstance;
    private ManagedInstance<DockingAcceptorControl<AbstractCanvasHandler>> dockingAcceptorControl;

    @Mock
    LineSpliceAcceptorControl<AbstractCanvasHandler> lineSpliceAcceptorControlInstance;
    private ManagedInstance<LineSpliceAcceptorControl<AbstractCanvasHandler>> lineSpliceAcceptorControl;


    @Mock
    DiagramViewer.DiagramViewerCallback<Diagram> callback;

    private DefaultDiagramEditor tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        super.init();
        when(metadata.getDefinitionSetId()).thenReturn("ds1");
        commandManager = spy(new ManagedInstanceStub<>(commandManagerInstance));
        locationControl = spy(new ManagedInstanceStub<>(locationControlInstance));
        resizeControl = spy(new ManagedInstanceStub<>(resizeControlInstance));
        builderControl = spy(new ManagedInstanceStub<>(builderControlInstance));
        nodeBuilderControl = spy(new ManagedInstanceStub<>(nodeBuilderControlInstance));
        edgeBuilderControl = spy(new ManagedInstanceStub<>(edgeBuilderControlInstance));
        cpControl = spy(new ManagedInstanceStub<>(cpControlInstance));
        connectionAcceptorControl = spy(new ManagedInstanceStub<>(connectionAcceptorControlInstance));
        containmentAcceptorControl = spy(new ManagedInstanceStub<>(containmentAcceptorControlInstance));
        dockingAcceptorControl = spy(new ManagedInstanceStub<>(dockingAcceptorControlInstance));
        lineSpliceAcceptorControl = spy(new ManagedInstanceStub<>(lineSpliceAcceptorControlInstance));
        when(viewer.getHandler()).thenReturn(canvasHandler);
        doAnswer(invocationOnMock -> {
            when(viewer.getInstance()).thenReturn(diagram);
            final DiagramViewer.DiagramViewerCallback c = (DiagramViewer.DiagramViewerCallback) invocationOnMock.getArguments()[1];
            c.onOpen(diagram);
            c.afterCanvasInitialized();
            c.onSuccess();
            return diagram;
        }).when(viewer).open(any(Diagram.class),
                             any(DiagramViewer.DiagramViewerCallback.class));
        doAnswer(invocationOnMock -> {
            when(viewer.getInstance()).thenReturn(null);
            return null;
        }).when(viewer).clear();
        doAnswer(invocationOnMock -> {
            when(viewer.getInstance()).thenReturn(null);
            return null;
        }).when(viewer).destroy();
        this.tested =
                new DefaultDiagramEditor(definitionUtils,
                                         viewer,
                                         commandManager,
                                         locationControl,
                                         resizeControl,
                                         builderControl,
                                         nodeBuilderControl,
                                         edgeBuilderControl,
                                         cpControl,
                                         connectionAcceptorControl,
                                         containmentAcceptorControl,
                                         dockingAcceptorControl,
                                         lineSpliceAcceptorControl);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOpen() {
        tested.open(diagram,
                    callback);
        assertEquals(diagram,
                     tested.getInstance());
        verify(viewer,
               times(1)).open(eq(diagram),
                              any(DiagramViewer.DiagramViewerCallback.class));
        verify(locationControlInstance,
               times(1)).init(eq(canvasHandler));
        verify(resizeControlInstance,
               times(1)).init(eq(canvasHandler));
        verify(builderControlInstance,
               times(1)).init(eq(canvasHandler));
        verify(nodeBuilderControlInstance,
               times(1)).init(eq(canvasHandler));
        verify(edgeBuilderControlInstance,
               times(1)).init(eq(canvasHandler));
        verify(cpControlInstance,
               times(1)).init(eq(canvasHandler));
        verify(connectionAcceptorControlInstance,
               times(1)).init(eq(canvasHandler));
        verify(containmentAcceptorControlInstance,
               times(1)).init(eq(canvasHandler));
        verify(dockingAcceptorControlInstance,
               times(1)).init(eq(canvasHandler));
        verify(lineSpliceAcceptorControlInstance,
               times(1)).init(eq(canvasHandler));
        ArgumentCaptor<CanvasElementListener> elementListenerArgumentCaptor = ArgumentCaptor.forClass(CanvasElementListener.class);
        verify(canvasHandler, times(1)).addRegistrationListener(elementListenerArgumentCaptor.capture());
        DefaultCanvasElementListener elementListener = (DefaultCanvasElementListener) elementListenerArgumentCaptor.getValue();
        Iterator<CanvasControl<AbstractCanvasHandler>> canvasHandlerControls1 = elementListener.getCanvasControls().iterator();
        assertTrue(canvasHandlerControls1.next() instanceof LocationControl);
        assertTrue(canvasHandlerControls1.next() instanceof ResizeControl);
        assertTrue(canvasHandlerControls1.next() instanceof ElementBuilderControl);
        assertTrue(canvasHandlerControls1.next() instanceof NodeBuilderControl);
        assertTrue(canvasHandlerControls1.next() instanceof EdgeBuilderControl);
        assertTrue(canvasHandlerControls1.next() instanceof ControlPointControl);
        assertTrue(canvasHandlerControls1.next() instanceof ContainmentAcceptorControl);
        assertTrue(canvasHandlerControls1.next() instanceof ConnectionAcceptorControl);
        assertTrue(canvasHandlerControls1.next() instanceof DockingAcceptorControl);
        assertTrue(canvasHandlerControls1.next() instanceof LineSpliceAcceptorControl);
        assertFalse(canvasHandlerControls1.hasNext());
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
        verify(viewer,
               times(1)).scale(50,
                               50);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testClear() {
        tested.open(diagram,
                    callback);
        tested.clear();
        assertNull(tested.getInstance());
        verify(viewer,
               times(1)).clear();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDestroy() {
        tested.open(diagram,
                    callback);
        tested.destroy();
        assertNull(tested.getInstance());
        verify(viewer,
               times(1)).destroy();
        verify(commandManager,
               times(1)).destroyAll();
        verify(locationControl,
               times(1)).destroyAll();
        verify(resizeControl,
               times(1)).destroyAll();
        verify(builderControl,
               times(1)).destroyAll();
        verify(nodeBuilderControl,
               times(1)).destroyAll();
        verify(edgeBuilderControl,
               times(1)).destroyAll();
        verify(cpControl,
               times(1)).destroyAll();
        verify(connectionAcceptorControl,
               times(1)).destroyAll();
        verify(containmentAcceptorControl,
               times(1)).destroyAll();
        verify(dockingAcceptorControl,
               times(1)).destroyAll();
        verify(lineSpliceAcceptorControl,
               times(1)).destroyAll();
    }

    @Override
    protected CanvasPanel getCanvasPanel() {
        return canvasPanel;
    }
}
