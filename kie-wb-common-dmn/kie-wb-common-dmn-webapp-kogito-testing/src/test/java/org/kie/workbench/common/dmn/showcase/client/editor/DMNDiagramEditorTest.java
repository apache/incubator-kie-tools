/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.showcase.client.editor;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent;
import org.kie.workbench.common.dmn.showcase.client.navigator.DMNVFSService;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.AbstractDMNDiagramEditor;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.AbstractDMNDiagramEditorTest;
import org.kie.workbench.common.stunner.core.client.PromiseMock;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.promise.SyncPromises;
import org.uberfire.util.URIUtil;
import org.uberfire.workbench.events.NotificationEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DMNDiagramEditorTest extends AbstractDMNDiagramEditorTest {

    private final String FILE_NAME = "file name.dmn";

    private final String CONTENT = "xml-content-of-dmn-file";

    @Mock
    private DMNVFSService vfsService;

    @Mock
    private EventSourceMock<NotificationEvent> notificationEventSourceMock;

    @Mock
    private CanvasFileExport canvasFileExport;

    @Captor
    private ArgumentCaptor<ServiceCallback<String>> serviceCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<Path> pathArgumentCaptor;

    @Override
    protected AbstractDMNDiagramEditor getEditor() {
        return new DMNDiagramEditor(view,
                                    placeManager,
                                    multiPageEditorContainerView,
                                    stunnerEditor,
                                    editorSearchIndex,
                                    searchBarComponent,
                                    sessionManager,
                                    sessionCommandManager,
                                    documentationView,
                                    clientTranslationService,
                                    refreshFormPropertiesEventSourceMock,
                                    decisionNavigatorDock,
                                    diagramPropertiesDock,
                                    diagramPreviewDock,
                                    layoutHelper,
                                    layoutExecutor,
                                    dataTypesPage,
                                    clientDiagramService,
                                    feelInitializer,
                                    canvasFileExport,
                                    new SyncPromises(),
                                    includedModelsPage,
                                    includedModelContext,
                                    guidedTourBridgeInitializer,
                                    drdNameChanger,
                                    notificationEventSourceMock,
                                    vfsService,
                                    readonlyProvider,
                                    lazyCanvasFocusUtils) {

            @Override
            protected PlaceRequest getPlaceRequest() {
                return place;
            }

            @Override
            protected ElementWrapperWidget<?> getWidget(final HTMLElement element) {
                return searchBarComponentWidget;
            }

            @Override
            protected void scheduleOnDataTypeEditModeToggleCallback(final DataTypeEditModeToggleEvent event) {
                //Override deferral to DomGlobal's timer for Unit Tests
                getOnDataTypeEditModeToggleCallback(event).onInvoke(event);
            }
        };
    }

    @Test
    @Override
    public void testOnStartup() {
        super.testOnStartup();

        verify(editor).setContent(eq(""), eq(""));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDoSave() {
        final PromiseMock promise = new PromiseMock();
        doReturn(promise.asPromise()).when(editor).getContent();
        promise.then(() -> CONTENT);

        openDiagram();

        reset(view);

        ((DMNDiagramEditor) editor).doSave();

        verify(vfsService).saveFile(pathArgumentCaptor.capture(),
                                    eq(CONTENT),
                                    serviceCallbackArgumentCaptor.capture());

        final Path path = pathArgumentCaptor.getValue();
        assertThat(path).isNotNull();
        assertThat(path).isEqualTo(metadata.getPath());

        final ServiceCallback<String> serviceCallback = serviceCallbackArgumentCaptor.getValue();
        assertThat(serviceCallback).isNotNull();

        serviceCallback.onSuccess(CONTENT);

        verify(editor, atLeastOnce()).resetContentHash();
        verify(notificationEventSourceMock).fire(any(NotificationEvent.class));
        verify(view).hideBusyIndicator();
    }

    @Override
    public void testSetContentSuccess() {
        place.addParameter(DMNDiagramEditor.FILE_NAME_PARAMETER_NAME, FILE_NAME);

        super.testSetContentSuccess();

        assertMetadataPath();
    }

    @Override
    protected void openDiagram() {
        place.addParameter(DMNDiagramEditor.FILE_NAME_PARAMETER_NAME, FILE_NAME);

        super.openDiagram();

        assertMetadataPath();
    }

    private void assertMetadataPath() {
        final Path path = metadata.getPath();
        assertThat(path).isNotNull();
        assertThat(path.getFileName()).isEqualTo(FILE_NAME);
        assertThat(path.toURI()).isEqualTo(ROOT + "/" + URIUtil.encode(FILE_NAME));
    }
}
