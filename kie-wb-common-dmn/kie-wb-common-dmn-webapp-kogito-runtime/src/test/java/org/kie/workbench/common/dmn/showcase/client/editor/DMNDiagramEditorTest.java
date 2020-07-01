/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collections;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.AbstractDMNDiagramEditor;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.AbstractDMNDiagramEditorTest;
import org.uberfire.promise.SyncPromises;
import org.uberfire.workbench.model.menu.impl.DefaultMenus;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNDiagramEditorTest extends AbstractDMNDiagramEditorTest {

    @Override
    public void setup() {
        super.setup();

        when(fileMenuBuilder.build()).thenReturn(new DefaultMenus(Collections.emptyList(), 0));
    }

    @Override
    protected AbstractDMNDiagramEditor getEditor() {
        return new DMNDiagramEditor(view,
                                    fileMenuBuilder,
                                    placeManager,
                                    multiPageEditorContainerView,
                                    changeTitleWidgetEventSourceMock,
                                    notificationEventSourceMock,
                                    onDiagramFocusEventSourceMock,
                                    xmlEditorView,
                                    sessionEditorPresenters,
                                    sessionViewerPresenters,
                                    dmnEditorMenuSessionItems,
                                    errorPopupPresenter,
                                    diagramClientErrorHandler,
                                    clientTranslationService,
                                    documentationView,
                                    editorSearchIndex,
                                    searchBarComponent,
                                    sessionManager,
                                    sessionCommandManager,
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
                                    importsPageProvider,
                                    contextProvider,
                                    guidedTourBridgeInitializer) {
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

    @Override
    public void testOnClose() {
        openDiagram();

        editor.onClose();

        verify(dmnEditorMenuSessionItems, times(1)).destroy();
        verify(editorPresenter).destroy();

        verify(decisionNavigatorDock).destroy();
        verify(decisionNavigatorDock).resetContent();

        verify(diagramPropertiesDock).destroy();
        verify(diagramPreviewDock).destroy();

        verify(dataTypesPage).disableShortcuts();
    }
}
