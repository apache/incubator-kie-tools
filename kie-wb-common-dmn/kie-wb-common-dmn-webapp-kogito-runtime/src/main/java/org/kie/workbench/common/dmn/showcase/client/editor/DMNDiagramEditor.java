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

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorDock;
import org.kie.workbench.common.dmn.client.docks.navigator.common.LazyCanvasFocusUtils;
import org.kie.workbench.common.dmn.client.editors.drd.DRDNameChanger;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPage;
import org.kie.workbench.common.dmn.client.editors.included.common.IncludedModelsContext;
import org.kie.workbench.common.dmn.client.editors.search.DMNEditorSearchIndex;
import org.kie.workbench.common.dmn.client.editors.search.DMNSearchableElement;
import org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent;
import org.kie.workbench.common.dmn.client.editors.types.DataTypesPage;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELInitializer;
import org.kie.workbench.common.dmn.webapp.common.client.docks.preview.PreviewDiagramDock;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.AbstractDMNDiagramEditor;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.GuidedTourBridgeInitializer;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerView;
import org.kie.workbench.common.stunner.client.widgets.editor.EditorSessionCommands;
import org.kie.workbench.common.stunner.client.widgets.editor.StunnerEditor;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.components.layout.LayoutHelper;
import org.kie.workbench.common.stunner.core.client.components.layout.OpenDiagramLayoutExecutor;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
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
                            final LayoutHelper layoutHelper,
                            final OpenDiagramLayoutExecutor openDiagramLayoutExecutor,
                            final DataTypesPage dataTypesPage,
                            final KogitoClientDiagramService diagramServices,
                            final MonacoFEELInitializer feelInitializer,
                            final CanvasFileExport canvasFileExport,
                            final Promises promises,
                            final IncludedModelsPage includedModelsPage,
                            final IncludedModelsContext includedModelContext,
                            final GuidedTourBridgeInitializer guidedTourBridgeInitializer,
                            final DRDNameChanger drdNameChanger,
                            final ReadOnlyProvider readOnlyProvider,
                            final LazyCanvasFocusUtils lazyCanvasFocusUtils,
                            final EditorSessionCommands commands) {
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
              feelInitializer,
              canvasFileExport,
              promises,
              includedModelsPage,
              includedModelContext,
              guidedTourBridgeInitializer,
              drdNameChanger);
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
            dataTypesPage.onFocus();
            dataTypesPage.enableShortcuts();
            lazyCanvasFocusUtils.releaseFocus();
            if (includedModelContext.isIncludedModelChannel()) {
                includedModelsPage.reload();
            }
        });
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

    private DMNDiagramEditor.View getView() {
        return (DMNDiagramEditor.View) getBaseEditorView();
    }
}