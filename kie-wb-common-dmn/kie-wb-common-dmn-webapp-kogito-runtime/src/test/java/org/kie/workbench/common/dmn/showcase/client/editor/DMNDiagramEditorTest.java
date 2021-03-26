/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.AbstractDMNDiagramEditor;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.AbstractDMNDiagramEditorTest;
import org.uberfire.promise.SyncPromises;

@RunWith(GwtMockitoTestRunner.class)
public class DMNDiagramEditorTest extends AbstractDMNDiagramEditorTest {

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
                                    new SyncPromises(), includedModelsPage,
                                    includedModelContext,
                                    guidedTourBridgeInitializer,
                                    drdNameChanger,
                                    readonlyProvider,
                                    lazyCanvasFocusUtils,
                                    sessionCommands) {
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
}
