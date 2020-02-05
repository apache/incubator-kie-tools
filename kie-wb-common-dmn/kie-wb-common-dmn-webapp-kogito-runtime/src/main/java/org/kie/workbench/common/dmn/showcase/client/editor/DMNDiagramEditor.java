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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorDock;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.dmn.client.editors.search.DMNEditorSearchIndex;
import org.kie.workbench.common.dmn.client.editors.search.DMNSearchableElement;
import org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent;
import org.kie.workbench.common.dmn.client.editors.types.DataTypesPage;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELInitializer;
import org.kie.workbench.common.dmn.webapp.common.client.docks.preview.PreviewDiagramDock;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.AbstractDMNDiagramEditor;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.DMNEditorMenuSessionItems;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.DMNProjectToolbarStateHandler;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerView;
import org.kie.workbench.common.kogito.webapp.base.client.editor.KogitoScreen;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerPresenter;
import org.kie.workbench.common.stunner.core.client.annotation.DiagramEditor;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.components.layout.LayoutHelper;
import org.kie.workbench.common.stunner.core.client.components.layout.OpenDiagramLayoutExecutor;
import org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.kie.workbench.common.stunner.kogito.client.docks.DiagramEditorPropertiesDock;
import org.kie.workbench.common.stunner.kogito.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.kogito.client.service.KogitoClientDiagramService;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.search.component.SearchBarComponent;
import org.uberfire.client.annotations.WorkbenchClientEditor;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorSelectedPageEvent;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

@DiagramEditor
@ApplicationScoped
@WorkbenchClientEditor(identifier = AbstractDMNDiagramEditor.EDITOR_ID)
@Default
public class DMNDiagramEditor extends AbstractDMNDiagramEditor implements KogitoScreen {

    private static final PlaceRequest DMN_KOGITO_RUNTIME_SCREEN_DEFAULT_REQUEST = new DefaultPlaceRequest(AbstractDMNDiagramEditor.EDITOR_ID);


    @Inject
    public DMNDiagramEditor(final View view,
                            final FileMenuBuilder fileMenuBuilder,
                            final PlaceManager placeManager,
                            final MultiPageEditorContainerView multiPageEditorContainerView,
                            final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent,
                            final Event<NotificationEvent> notificationEvent,
                            final Event<OnDiagramFocusEvent> onDiagramFocusEvent,
                            final TextEditorView xmlEditorView,
                            final ManagedInstance<SessionEditorPresenter<EditorSession>> editorSessionPresenterInstances,
                            final ManagedInstance<SessionViewerPresenter<ViewerSession>> viewerSessionPresenterInstances,
                            final DMNEditorMenuSessionItems menuSessionItems,
                            final ErrorPopupPresenter errorPopupPresenter,
                            final DiagramClientErrorHandler diagramClientErrorHandler,
                            final ClientTranslationService translationService,
                            final @DMNEditor DocumentationView<Diagram> documentationView,
                            final DMNEditorSearchIndex editorSearchIndex,
                            final SearchBarComponent<DMNSearchableElement> searchBarComponent,
                            final SessionManager sessionManager,
                            final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                            final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent,
                            final DecisionNavigatorDock decisionNavigatorDock,
                            final DiagramEditorPropertiesDock diagramPropertiesDock,
                            final PreviewDiagramDock diagramPreviewAndExplorerDock,
                            final LayoutHelper layoutHelper,
                            final OpenDiagramLayoutExecutor openDiagramLayoutExecutor,
                            final DataTypesPage dataTypesPage,
                            final KogitoClientDiagramService diagramServices,
                            final MonacoFEELInitializer feelInitializer) {
        super(view,
              fileMenuBuilder,
              placeManager,
              multiPageEditorContainerView,
              changeTitleNotificationEvent,
              notificationEvent,
              onDiagramFocusEvent,
              xmlEditorView,
              editorSessionPresenterInstances,
              viewerSessionPresenterInstances,
              menuSessionItems,
              errorPopupPresenter,
              diagramClientErrorHandler,
              translationService,
              documentationView,
              editorSearchIndex,
              searchBarComponent,
              sessionManager,
              sessionCommandManager,
              refreshFormPropertiesEvent,
              decisionNavigatorDock,
              diagramPropertiesDock,
              diagramPreviewAndExplorerDock,
              layoutHelper,
              openDiagramLayoutExecutor,
              dataTypesPage,
              diagramServices,
              feelInitializer);
    }

    @Override
    public PlaceRequest getPlaceRequest() {
        return DMN_KOGITO_RUNTIME_SCREEN_DEFAULT_REQUEST;
    }

    @Override
    public void onDiagramLoad() {
        final Optional<CanvasHandler> canvasHandler = Optional.ofNullable(getCanvasHandler());

        canvasHandler.ifPresent(c -> {
            final ExpressionEditorView.Presenter expressionEditor = ((DMNSession) sessionManager.getCurrentSession()).getExpressionEditor();
            expressionEditor.setToolbarStateHandler(new DMNProjectToolbarStateHandler(getMenuSessionItems()));
            decisionNavigatorDock.setupCanvasHandler(c);
            dataTypesPage.reload();
        });
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
}
