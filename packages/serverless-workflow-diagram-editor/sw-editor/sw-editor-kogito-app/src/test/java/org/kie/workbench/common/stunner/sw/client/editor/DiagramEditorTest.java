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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Event;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.JsCanvas;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.client.widget.panel.PostResizeCallback;
import com.ait.lienzo.client.widget.panel.impl.ScrollablePanel;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.IsWidget;
import elemental2.core.JsRegExp;
import elemental2.core.RegExpResult;
import elemental2.promise.Promise;
import org.appformer.kogito.bridge.client.diagramApi.DiagramApi;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvasView;
import org.kie.workbench.common.stunner.client.widgets.canvas.ScrollableLienzoPanel;
import org.kie.workbench.common.stunner.client.widgets.editor.StunnerEditor;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.api.JsStunnerEditor;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.AbstractSelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.ClearAllCommand;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.sw.SWDomainInitializer;
import org.kie.workbench.common.stunner.sw.client.services.ClientDiagramService;
import org.kie.workbench.common.stunner.sw.client.services.IncrementalMarshaller;
import org.kie.workbench.common.stunner.sw.marshall.Context;
import org.kie.workbench.common.stunner.sw.marshall.DocType;
import org.kie.workbench.common.stunner.sw.marshall.Marshaller;
import org.mockito.Mock;
import org.uberfire.client.promise.Promises;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.promise.SyncPromises;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
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

    @Mock
    private ViewerSession viewerSession;

    @Mock
    private JsRegExp jsRegExp;

    @Mock
    private RegExpResult regExpResult;

    @Mock
    private Graph graph;

    @Mock
    private JsStunnerEditor jsEditor;

    @Mock
    private JsCanvas jsCanvas;

    @Mock
    private AbstractSelectionControl selectionControl;

    @Mock
    private CanvasCommandManager commandManager;

    @Mock
    private Event togglePreviewEvent;

    @Mock
    private SWDomainInitializer domainInitializer;

    @Mock
    private DiagramApi diagramApi;

    @Mock
    private Marshaller marshaller;

    @Mock
    private Context context;

    private DiagramEditor tested;
    private Promises promises;
    private DiagramImpl diagram;
    private Metadata metadata;

    private static String rawJSON = "{\n" +
            " \"id\": \"injectExample\",\n" +
            " \"version\": \"1.0\",\n" +
            " \"specVersion\": \"0.8\",\n" +
            " \"name\": \"Inject State Example\",\n" +
            " \"description\": \"Inject Example\",\n" +
            " \"start\": \"Inject State\",\n" +
            " \"states\": [\n" +
            "  {\n" +
            "   \"name\": \"Inject State\",\n" +
            "   \"type\": \"inject\",\n" +
            "   \"data\": {\n" +
            "    \"person\": {\n" +
            "     \"fname\": \"John\",\n" +
            "     \"lname\": \"Doe\"\n" +
            "    }\n" +
            "   },\n" +
            "   \"stateDataFilter\": {\n" +
            "    \"input\": \"${ {vegetables: .vegetables} }\",\n" +
            "    \"output\": \"${ {vegetables: [.vegetables[] | select(.veggieLike == true)]} }\"\n" +
            "   },\n" +
            "   \"usedForCompensation\": false,\n" +
            "   \"metadata\": {\n" +
            "    \"prop1\": \"value1\",\n" +
            "    \"prop2\": \"value2\"\n" +
            "   },\n" +
            "   \"end\": true\n" +
            "  }\n" +
            " ]\n" +
            "}";

    @Before
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
        DiagramImpl d = new DiagramImpl("testDiagram", metadata);
        d.setGraph(graph);
        diagram = spy(d);
        when(session.getCanvasHandler()).thenReturn(canvasHandler2);
        when(canvasHandler2.getDiagram()).thenReturn(diagram);
        doReturn(stunnerEditor2).when(stunnerEditor2).close();
        when(stunnerEditor2.getSession()).thenReturn(viewerSession);
        when(selectionControl.clearSelection()).thenReturn(selectionControl);
        when(viewerSession.getSelectionControl()).thenReturn(selectionControl);
        when(selectionControl.getSelectedItems()).thenReturn(new ArrayList<>(Arrays.asList("uuid")));
        when(viewerSession.getCanvasHandler()).thenReturn(canvasHandler2);
        when(stunnerEditor2.getCanvasHandler()).thenReturn(canvasHandler2);
        when(stunnerEditor2.getDiagram()).thenReturn(diagram);
        tested = spy(new DiagramEditor(promises,
                                       stunnerEditor2,
                                       diagramServices,
                                       incrementalMarshaller,
                                       canvasFileExport,
                                       togglePreviewEvent,
                                       diagramApi));
        tested.jsRegExpJson = jsRegExp;
        tested.domainInitializer = domainInitializer;
        doReturn(jsCanvas).when(tested).getJsCanvas();
    }

    @Test
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

    @Test
    public void testScaleToFitWorkflowFits() {
        when(lienzoPanel.getWidePx()).thenReturn(500);
        when(lienzoPanel.getHighPx()).thenReturn(500);
        // No need to scale if workflow fits
        when(lienzoPanel.getLayerBounds()).thenReturn(Bounds.build(0d, 0d, 400d, 400d));

        DiagramEditor.scaleToFitWorkflow(stunnerEditor);

        verify(lienzoPanel, times(1)).setPostResizeCallback(any(PostResizeCallback.class));
        assertNotNull(lienzoPanel.getPostResizeCallback());
        // Run callback
        lienzoPanel.getPostResizeCallback().execute(lienzoPanel);
        // Keep the transform
        assertEquals(transform, viewport.getTransform());
        verify(lienzoPanel, times(1)).setPostResizeCallback(null);
    }

    @Test
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

    @Test
    public void testStartupReadOnly() {
        when(readOnlyProvider.isReadOnlyDiagram()).thenReturn(true);
        tested.onStartup(new DefaultPlaceRequest());
        verify(stunnerEditor2, times(1)).setReadOnly(eq(true));
    }

    @Test
    public void testDomainInitializer() {
        tested.onStartup(new DefaultPlaceRequest());
        verify(domainInitializer, times(1)).initialize();
    }

    @Test
    public void testAsWidget() {
        IsWidget w = mock(IsWidget.class);
        when(stunnerEditor2.getView()).thenReturn(w);
        assertEquals(w, tested.asWidget());
    }

    @Test
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

    @Test
    public void testSetNewContent() {
        when(jsRegExp.exec(rawJSON)).thenReturn(regExpResult);
        when(regExpResult.getAt(2)).thenReturn("injectExample");
        when(graph.getUUID()).thenReturn("SomeOtherStuff");

        doReturn(promises.create((success, failure) -> success.onInvoke((Void) null))).when(tested).setNewContent(anyString(), anyString(), any(DocType.class));

        tested.setContent("", rawJSON);

        verify(tested, times(1)).setNewContent("", rawJSON, DocType.JSON);
        verify(tested, never()).updateContent("", rawJSON, DocType.JSON);
        verify(tested, never()).close();
        verify(diagramApi).setContentSuccess();
    }

    @Test
    public void testUpdateContent() {
        when(jsRegExp.exec(rawJSON)).thenReturn(regExpResult);
        when(regExpResult.getAt(2)).thenReturn("injectExample");
        when(graph.getUUID()).thenReturn("injectExample");

        doReturn(promises.create((success, failure) -> success.onInvoke((Void) null))).when(tested).updateContent(anyString(), anyString(), any(DocType.class));

        tested.setContent("", rawJSON);

        verify(tested, times(1)).updateContent("", rawJSON, DocType.JSON);
        verify(tested, never()).setNewContent("", rawJSON, DocType.JSON);
        verify(diagramApi).setContentSuccess();
    }

    @Test
    public void testRegex() {
        RegExp regExp = RegExp.compile(DiagramEditor.ID_SEARCH_PATTERN_JSON);
        MatchResult matcher = regExp.exec(rawJSON);

        assertNotNull(matcher);
        assertEquals("id", matcher.getGroup(1));
        assertEquals("injectExample", matcher.getGroup(2));
    }

    @Test
    public void testClose() {
        when(jsRegExp.exec(rawJSON)).thenReturn(regExpResult);
        when(regExpResult.getAt(2)).thenReturn("injectExample");
        when(graph.getUUID()).thenReturn("SomeOtherStuff");

        tested.setContent("", rawJSON);
        tested.close();

        verify(stunnerEditor2, times(1)).close();
        verify(jsCanvas, times(1)).close();
    }

    @Test
    public void testUpdateDiagramAndSelection() {
        DiagramImpl newDiagram = mock(DiagramImpl.class);
        Node node = mock(Node.class);
        Node newNode = mock(Node.class);
        ArrayList<Node> uuids = new ArrayList<>(Arrays.asList(newNode));

        when(graph.getNode("uuid")).thenReturn(node);
        when(node.getUUID()).thenReturn("uuid");
        when(newNode.getUUID()).thenReturn("uuid"); //must have same uuid to reapply selection
        when(viewerSession.getCommandManager()).thenReturn(commandManager);
        when(graph.nodes()).thenReturn(uuids);

        tested.updateDiagram(newDiagram);

        // Clean up
        verify(selectionControl, never()).clear();
        verify(commandManager, times(1)).execute(eq(canvasHandler2), any(ClearAllCommand.class));
        // Keep selection
        verify(selectionControl, times(1)).addSelection("uuid");
        // Center selected node
        verify(jsCanvas, times(1)).center("uuid");
    }

    @Test
    public void testSelectStateByName() {
        Map<String, String> nameToUUIDBindings = new HashMap<>();
        nameToUUIDBindings.put("name", "uuid");

        doReturn(nameToUUIDBindings).when(context).getNameToUUIDBindings();
        doReturn(context).when(marshaller).getContext();
        doReturn(marshaller).when(diagramServices).getMarshaller();

        tested.selectStateByName("name");

        verify(selectionControl).addSelection("uuid");
        verify(jsCanvas).center("uuid");
    }
}
