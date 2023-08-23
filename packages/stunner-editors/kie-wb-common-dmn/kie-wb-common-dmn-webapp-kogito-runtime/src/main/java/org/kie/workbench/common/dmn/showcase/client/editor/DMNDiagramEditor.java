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
package org.kie.workbench.common.dmn.showcase.client.editor;

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.ait.lienzo.client.core.types.JsCanvas;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import elemental2.promise.Promise;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.common.KogitoChannelHelper;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorDock;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenter;
import org.kie.workbench.common.dmn.client.docks.navigator.common.LazyCanvasFocusUtils;
import org.kie.workbench.common.dmn.client.docks.preview.PreviewDiagramDock;
import org.kie.workbench.common.dmn.client.editors.drd.DRDNameChanger;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml.PMMLDocumentMetadataProvider;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPage;
import org.kie.workbench.common.dmn.client.editors.search.DMNEditorSearchIndex;
import org.kie.workbench.common.dmn.client.editors.search.DMNSearchableElement;
import org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent;
import org.kie.workbench.common.dmn.client.editors.types.DataTypesPage;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.widgets.toolbar.DMNLayoutHelper;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.AbstractDMNDiagramEditor;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerView;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;
import org.kie.workbench.common.stunner.client.lienzo.util.StunnerStateApplier;
import org.kie.workbench.common.stunner.client.widgets.editor.EditorSessionCommands;
import org.kie.workbench.common.stunner.client.widgets.editor.StunnerEditor;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.ConfirmationDialog;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.components.layout.OpenDiagramLayoutExecutor;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.util.WindowJSType;
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.kie.workbench.common.stunner.kogito.client.docks.DiagramEditorPropertiesDock;
import org.kie.workbench.common.stunner.kogito.client.service.KogitoClientDiagramService;
import org.kie.workbench.common.widgets.client.search.component.SearchBarComponent;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorSelectedPageEvent;

@ApplicationScoped
//@Named(AbstractDMNDiagramEditor.EDITOR_ID) uncomment after removing DMNDiagramEditorActivity
public class DMNDiagramEditor extends AbstractDMNDiagramEditor {

    private final ReadOnlyProvider readOnlyProvider;
    private final LazyCanvasFocusUtils lazyCanvasFocusUtils;
    private final EditorSessionCommands commands;

    @Inject
    public DMNDiagramEditor(final View view,
                            final MultiPageEditorContainerView containerView,
                            final StunnerEditor stunnerEditor,
                            final DMNEditorSearchIndex editorSearchIndex,
                            final SearchBarComponent<DMNSearchableElement> searchBarComponent,
                            final SessionManager sessionManager,
                            final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                            final @DMNEditor DocumentationView documentationView,
                            final ClientTranslationService translationService,
                            final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent,
                            final DecisionNavigatorDock decisionNavigatorDock,
                            final DiagramEditorPropertiesDock diagramPropertiesDock,
                            final PreviewDiagramDock diagramPreviewAndExplorerDock,
                            final @DMNEditor DMNLayoutHelper layoutHelper,
                            final OpenDiagramLayoutExecutor openDiagramLayoutExecutor,
                            final DataTypesPage dataTypesPage,
                            final KogitoClientDiagramService diagramServices,
                            final CanvasFileExport canvasFileExport,
                            final Promises promises,
                            final IncludedModelsPage includedModelsPage,
                            final KogitoChannelHelper kogitoChannelHelper,
                            final DRDNameChanger drdNameChanger,
                            final ReadOnlyProvider readOnlyProvider,
                            final LazyCanvasFocusUtils lazyCanvasFocusUtils,
                            final EditorSessionCommands commands,
                            final ConfirmationDialog confirmationDialog,
                            final DecisionNavigatorPresenter decisionNavigatorPresenter,
                            final PMMLDocumentMetadataProvider pmmlDocumentMetadataProvider) {
        super(view,
              containerView,
              stunnerEditor,
              editorSearchIndex,
              searchBarComponent,
              sessionManager,
              sessionCommandManager,
              documentationView,
              translationService,
              refreshFormPropertiesEvent,
              decisionNavigatorDock,
              diagramPropertiesDock,
              diagramPreviewAndExplorerDock,
              layoutHelper,
              openDiagramLayoutExecutor,
              dataTypesPage,
              diagramServices,
              canvasFileExport,
              promises,
              includedModelsPage,
              kogitoChannelHelper,
              drdNameChanger,
              confirmationDialog,
              decisionNavigatorPresenter,
              pmmlDocumentMetadataProvider);
        this.readOnlyProvider = readOnlyProvider;
        this.lazyCanvasFocusUtils = lazyCanvasFocusUtils;
        this.commands = commands;
    }

    @PostConstruct
    public void init() {
        getView().setWidget(stunnerEditor.getView());
    }

    @Override
    public void onDiagramLoad() {
        Optional.ofNullable(stunnerEditor.getCanvasHandler()).ifPresent(c -> {
            commands.bind(stunnerEditor.getSession());
            decisionNavigatorDock.reload();
            dataTypesPage.reload();
            lazyCanvasFocusUtils.releaseFocus();
            if (kogitoChannelHelper.isIncludedModelEnabled()) {
                includedModelsPage.reload();
            }
        });
        initLienzoType();
    }

    private void initLienzoType() {
        LienzoCanvas canvas = (LienzoCanvas) stunnerEditor.getCanvasHandler().getCanvas();
        if (canvas != null) {
            LienzoPanel panel = (LienzoPanel) canvas.getView().getPanel();
            LienzoBoundsPanel lienzoPanel = panel.getView();
            JsCanvas jsCanvas = new JsCanvas(lienzoPanel, lienzoPanel.getLayer(), new StunnerStateApplier() {
                @Override
                public Shape getShape(String uuid) {
                    return stunnerEditor.getCanvasHandler().getCanvas().getShape(uuid);
                }
            });
            setupJsCanvasTypeNative(jsCanvas);
        }
    }

    private static void setupJsCanvasTypeNative(JsCanvas jsCanvas) {
        WindowJSType.linkCanvasJS(jsCanvas);
    }

    @Override
    public void onClose() {
        commands.clear();
        super.onClose();
    }

    @Override
    public void onDataTypePageNavTabActiveEvent(final @Observes DataTypePageTabActiveEvent event) {
        super.onDataTypePageNavTabActiveEvent(event);
    }

    @Override
    public void onDataTypeEditModeToggle(final @Observes DataTypeEditModeToggleEvent event) {
        super.onDataTypeEditModeToggle(event);
    }

    @Override
    public void onEditExpressionEvent(final @Observes EditExpressionEvent event) {
        super.onEditExpressionEvent(event);
    }

    @Override
    public void onMultiPageEditorSelectedPageEvent(final @Observes MultiPageEditorSelectedPageEvent event) {
        super.onMultiPageEditorSelectedPageEvent(event);
    }

    @Override
    public void onRefreshFormPropertiesEvent(final @Observes RefreshFormPropertiesEvent event) {
        super.onRefreshFormPropertiesEvent(event);
    }

    @Override
    public boolean isReadOnly() {
        return readOnlyProvider.isReadOnlyDiagram();
    }

    public Promise<Void> undo() {
        return promises.create((resolve, reject) -> {
            commands.getUndoSessionCommand().execute();
        });
    }

    public Promise<Void> redo() {
        return promises.create((resolve, reject) -> {
            commands.getRedoSessionCommand().execute();
        });
    }

    public Promise<Void> searchDomainObject(final String uuid) {
        return promises.create((resolve, reject) -> {
            throw new UnsupportedOperationException("This diagram does not support search for domain objects");
        });
    }

    private DMNDiagramEditor.View getView() {
        return (DMNDiagramEditor.View) getBaseEditorView();
    }
}
