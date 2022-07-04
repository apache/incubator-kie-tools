/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.sw.client.editor;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.client.widget.panel.PostResizeCallback;
import com.ait.lienzo.client.widget.panel.impl.ScrollablePanel;
import elemental2.promise.Promise;
import org.gwtproject.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvasView;
import org.kie.workbench.common.stunner.client.widgets.canvas.ScrollableLienzoPanel;
import org.kie.workbench.common.stunner.client.widgets.editor.StunnerEditor;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.sw.client.services.ClientDiagramService;
import org.kie.workbench.common.stunner.sw.client.services.IncrementalMarshaller;
import org.mockito.Mock;
import org.uberfire.client.promise.Promises;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.promise.SyncPromises;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//@RunWith(LienzoMockitoTestRunner.class) TODO: fix this test
public class DiagramEditorTest {

    @Mock
    private WiresCanvas canvas;

    @Mock
    private WiresCanvasView canvasView;

    @Mock
    private ScrollableLienzoPanel scrollableLienzoPanel;

    @Mock
    private ScrollablePanel lienzoPanel;

    @Mock
    private CanvasHandler canvasHandler;

    @Mock
    private StunnerEditor stunnerEditor;

    @Mock
    private Viewport viewport;

    @Mock
    private Transform transform;

    @Mock
    private ReadOnlyProvider readOnlyProvider;

    @Mock
    private CanvasFileExport canvasFileExport;

    @Mock
    private ClientDiagramService diagramServices;

    @Mock
    private ClientSession session;

    @Mock
    private AbstractCanvasHandler canvasHandler2;

    @Mock
    private StunnerEditor stunnerEditor2;

    @Mock
    private IncrementalMarshaller incrementalMarshaller;

    private DiagramEditor tested;
    private Promises promises;
    private DiagramImpl diagram;
    private Metadata metadata;

    //@Before
    public void setUp() {
        when(stunnerEditor.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvas.getView()).thenReturn(canvasView);
        when(canvasView.getLienzoPanel()).thenReturn(scrollableLienzoPanel);
        when(scrollableLienzoPanel.getView()).thenReturn(lienzoPanel);
        when(lienzoPanel.getViewport()).thenReturn(viewport);
        when(transform.getTranslateX()).thenReturn(200d);
        when(transform.getTranslateY()).thenReturn(200d);
        doCallRealMethod().when(lienzoPanel).setPostResizeCallback(any(PostResizeCallback.class));
        doCallRealMethod().when(lienzoPanel).getPostResizeCallback();
        doCallRealMethod().when(viewport).setTransform(any(Transform.class));
        doCallRealMethod().when(viewport).getTransform();
        viewport.setTransform(transform);

        promises = new SyncPromises();
        metadata = spy(new MetadataImpl.MetadataImplBuilder("testSet")
                .setTitle("testDiagram")
                .build());
        diagram = new DiagramImpl("testDiagram",
                mock(Graph.class),
                metadata);
        when(session.getCanvasHandler()).thenReturn(canvasHandler2);
        when(canvasHandler2.getDiagram()).thenReturn(diagram);
        doReturn(stunnerEditor2).when(stunnerEditor2).close();
        when(stunnerEditor2.getSession()).thenReturn(session);
        when(stunnerEditor2.getCanvasHandler()).thenReturn(canvasHandler2);
        when(stunnerEditor2.getDiagram()).thenReturn(diagram);
        tested = new DiagramEditor(promises,
                stunnerEditor2,
                diagramServices,
                incrementalMarshaller,
                canvasFileExport);
    }

    //@Test
    public void testScaleToFitWorkflow() {
        when(lienzoPanel.getWidePx()).thenReturn(500);
        when(lienzoPanel.getHighPx()).thenReturn(500);
        when(lienzoPanel.getLayerBounds()).thenReturn(Bounds.build(0d, 0d, 1000d, 1000d));

        DiagramEditor.scaleToFitWorkflow(stunnerEditor);

        verify(lienzoPanel, times(1)).setPostResizeCallback(any(PostResizeCallback.class));
        assertNotNull(lienzoPanel.getPostResizeCallback());
        // Run callback
        lienzoPanel.getPostResizeCallback().execute(lienzoPanel);
        // New transform is created
        assertNotEquals(transform, viewport.getTransform());
        verify(lienzoPanel, times(1)).setPostResizeCallback(null);
    }

    //@Test
    public void testScaleToFitWorkflowScaleLessThanZero() {
        when(lienzoPanel.getWidePx()).thenReturn(0);
        when(lienzoPanel.getHighPx()).thenReturn(0);
        when(lienzoPanel.getLayerBounds()).thenReturn(Bounds.build(0d, 0d, 0d, 0d));

        DiagramEditor.scaleToFitWorkflow(stunnerEditor);

        verify(lienzoPanel, times(1)).setPostResizeCallback(any(PostResizeCallback.class));
        assertNotNull(lienzoPanel.getPostResizeCallback());
        // Run callback
        lienzoPanel.getPostResizeCallback().execute(lienzoPanel);
        // Same transform
        assertEquals(transform, viewport.getTransform());
        verify(lienzoPanel, times(1)).setPostResizeCallback(null);
    }

    //@Test
    public void testStartupReadOnly() {
        when(readOnlyProvider.isReadOnlyDiagram()).thenReturn(true);
        tested.onStartup(new DefaultPlaceRequest());
        verify(stunnerEditor2, times(1)).setReadOnly(eq(true));
    }

    //@Test
    public void testAsWidget() {
        IsWidget w = mock(IsWidget.class);
        when(stunnerEditor2.getView()).thenReturn(w);
        assertEquals(w, tested.asWidget());
    }

    //@Test
    public void testGetPreview() {
        when(canvasFileExport.exportToSvg(eq(canvasHandler2))).thenReturn("<svg/>");
        Promise content = tested.getPreview();
        final String[] result = {""};
        content.then(p -> {
            result[0] = p.toString();
            return null;
        });
        assertEquals("<svg/>", result[0]);
    }
}
