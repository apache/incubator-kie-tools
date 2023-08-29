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


package org.kie.workbench.common.stunner.kogito.client.editor;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.promise.Promise;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.editor.EditorSessionCommands;
import org.kie.workbench.common.stunner.client.widgets.editor.StunnerEditor;
import org.kie.workbench.common.stunner.client.widgets.presenters.Viewer;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasDiagramValidator;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.forms.client.widgets.FormsFlushManager;
import org.kie.workbench.common.stunner.kogito.client.docks.DiagramEditorPreviewAndExplorerDock;
import org.kie.workbench.common.stunner.kogito.client.docks.DiagramEditorPropertiesDock;
import org.kie.workbench.common.stunner.kogito.client.service.AbstractKogitoClientDiagramService;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.uberfire.client.promise.Promises;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.promise.SyncPromises;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class BPMNDiagramEditorTest {

    @Mock
    private ReadOnlyProvider readOnlyProvider;
    @Mock
    private StunnerEditor stunnerEditor;
    @Mock
    private ClientTranslationService translationService;
    @Mock
    private AbstractKogitoClientDiagramService diagramServices;
    @Mock
    private CanvasFileExport canvasFileExport;
    @Mock
    private DiagramEditorPreviewAndExplorerDock diagramPreviewAndExplorerDock;
    @Mock
    private DiagramEditorPropertiesDock diagramPropertiesDock;
    @Mock
    private FormsFlushManager formsFlushManager;
    @Mock
    private EditorSessionCommands commands;
    @Mock
    private ClientSession session;
    @Mock
    private AbstractCanvasHandler canvasHandler;
    @Mock
    private CanvasDiagramValidator<AbstractCanvasHandler> validator;

    private BPMNDiagramEditor tested;
    private Promises promises;
    private DiagramImpl diagram;
    private Metadata metadata;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        promises = new SyncPromises();
        metadata = spy(new MetadataImpl.MetadataImplBuilder("testSet")
                               .setTitle("testDiagram")
                               .build());
        diagram = new DiagramImpl("testDiagram",
                                  mock(Graph.class),
                                  metadata);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        doReturn(stunnerEditor).when(stunnerEditor).close();
        when(stunnerEditor.getSession()).thenReturn(session);
        when(stunnerEditor.getCanvasHandler()).thenReturn(canvasHandler);
        when(stunnerEditor.getDiagram()).thenReturn(diagram);
        tested = new BPMNDiagramEditor(promises,
                                       readOnlyProvider,
                                       stunnerEditor,
                                       translationService,
                                       diagramServices,
                                       canvasFileExport,
                                       diagramPreviewAndExplorerDock,
                                       diagramPropertiesDock,
                                       formsFlushManager,
                                       commands,
                                       validator);
    }

    @Test
    public void testStartup() {
        when(readOnlyProvider.isReadOnlyDiagram()).thenReturn(false);
        tested.onStartup(new DefaultPlaceRequest());
        verify(stunnerEditor, times(1)).setReadOnly(eq(false));
        verifyDocksAreInit();
    }

    @Test
    public void testStartupReadOnly() {
        when(readOnlyProvider.isReadOnlyDiagram()).thenReturn(true);
        tested.onStartup(new DefaultPlaceRequest());
        verify(stunnerEditor, times(1)).setReadOnly(eq(true));
        verifyDocksAreInit();
    }

    private void verifyDocksAreInit() {
        verify(diagramPropertiesDock, times(1)).init();
        verify(diagramPreviewAndExplorerDock, times(1)).init();
    }

    @Test
    public void testOnClose() {
        tested.onClose();
        verify(commands, times(1)).clear();
        verify(diagramPropertiesDock, times(1)).close();
        verify(diagramPreviewAndExplorerDock, times(1)).close();
        verify(stunnerEditor, times(1)).close();
    }

    @Test
    public void testAsWidget() {
        IsWidget w = mock(IsWidget.class);
        when(stunnerEditor.getView()).thenReturn(w);
        assertEquals(w, tested.asWidget());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetContentFromDiagram() {
        Promise rawValue = mock(Promise.class);
        when(diagramServices.transform(eq(diagram))).thenReturn(rawValue);
        assertEquals(rawValue, tested.getContent());
        verify(formsFlushManager, times(1)).flush(eq(session));
    }

    @Test
    public void testGetPreview() {
        when(canvasFileExport.exportToSvg(eq(canvasHandler))).thenReturn("<svg/>");
        Promise content = tested.getPreview();
        final String[] result = {""};
        content.then(p -> {
            result[0] = p.toString();
            return null;
        });
        assertEquals("<svg/>", result[0]);
    }

    @Test
    public void testSetContent() {
        String path = "";
        String content = "";
        doAnswer(invocation -> {
            ServiceCallback callback = (ServiceCallback) invocation.getArguments()[2];
            callback.onSuccess(diagram);
            return null;
        }).when(diagramServices).transform(eq(path), eq(content), any());
        doAnswer(invocation -> {
            Viewer.Callback callback = (Viewer.Callback) invocation.getArguments()[1];
            callback.onSuccess();
            return null;
        }).when(stunnerEditor).open(eq(diagram), any());
        Promise completed = tested.setContent(path, content);
        verify(stunnerEditor, atLeastOnce()).close();
        verify(stunnerEditor, times(1)).open(eq(diagram), any());
        verify(metadata, times(1)).setPath(any());
        verify(commands, times(1)).bind(eq(session));
        verify(diagramPreviewAndExplorerDock, times(1)).open();
        verify(diagramPropertiesDock, times(1)).open();
    }

    @Test
    public void testSuperOnCloseOnSetContent() {
        //First setContent call context
        tested.setContent("", "");
        verify(commands, times(1)).clear();
        //Second setContent call context
        final String path = "/project/src/main/resources/diagrams/process.bpmn";
        tested.setContent(path, "");
        verify(commands, times(2)).clear();
    }

    @Test
    public void testDocksAndOrdering() {
        tested.docksInit();
        InOrder initOrder = inOrder(diagramPropertiesDock, diagramPreviewAndExplorerDock);
        initOrder.verify(diagramPropertiesDock).init();
        initOrder.verify(diagramPreviewAndExplorerDock).init();
        tested.docksOpen();
        initOrder.verify(diagramPropertiesDock).open();
        initOrder.verify(diagramPreviewAndExplorerDock).open();
        tested.docksClose();
        initOrder.verify(diagramPropertiesDock).close();
        initOrder.verify(diagramPreviewAndExplorerDock).close();
    }
}