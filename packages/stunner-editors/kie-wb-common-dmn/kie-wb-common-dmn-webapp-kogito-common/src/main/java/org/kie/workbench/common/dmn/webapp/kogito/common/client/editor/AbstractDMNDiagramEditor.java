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
package org.kie.workbench.common.dmn.webapp.kogito.common.client.editor;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.event.Event;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import elemental2.dom.HTMLElement;
import elemental2.promise.Promise;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.commands.general.NavigateToExpressionEditorCommand;
import org.kie.workbench.common.dmn.client.common.KogitoChannelHelper;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorDock;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenter;
import org.kie.workbench.common.dmn.client.docks.preview.PreviewDiagramDock;
import org.kie.workbench.common.dmn.client.editors.drd.DRDNameChanger;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml.PMMLDocumentMetadataProvider;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPage;
import org.kie.workbench.common.dmn.client.editors.search.DMNEditorSearchIndex;
import org.kie.workbench.common.dmn.client.editors.search.DMNSearchableElement;
import org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent;
import org.kie.workbench.common.dmn.client.editors.types.DataTypesPage;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.toolbar.DMNLayoutHelper;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerPresenter;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerView;
import org.kie.workbench.common.stunner.client.widgets.editor.StunnerEditor;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.resources.i18n.StunnerWidgetsConstants;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.ConfirmationDialog;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.components.layout.OpenDiagramLayoutExecutor;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.documentation.DocumentationPage;
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.kie.workbench.common.stunner.kogito.client.docks.DiagramEditorPropertiesDock;
import org.kie.workbench.common.stunner.kogito.client.service.KogitoClientDiagramService;
import org.kie.workbench.common.widgets.client.search.component.SearchBarComponent;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorSelectedPageEvent;
import org.uberfire.ext.editor.commons.client.BaseEditorView;
import org.uberfire.mvp.PlaceRequest;

public abstract class AbstractDMNDiagramEditor extends MultiPageEditorContainerPresenter<Diagram> {

    public interface View extends BaseEditorView,
                                  RequiresResize,
                                  ProvidesResize,
                                  IsWidget {

        void setWidget(IsWidget widget);
    }

    public static final String EDITOR_ID = "DMNDiagramEditor";
    //Editor tabs: [0] Main editor, [1] Documentation, [2] Data-Types
    public static final int DATA_TYPES_PAGE_INDEX = 2;

    protected final StunnerEditor stunnerEditor;
    protected final SessionManager sessionManager;
    protected final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    protected final DocumentationView documentationView;
    protected final ClientTranslationService translationService;
    protected final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;
    protected final DecisionNavigatorDock decisionNavigatorDock;
    protected final DiagramEditorPropertiesDock diagramPropertiesDock;
    protected final PreviewDiagramDock diagramPreviewAndExplorerDock;
    protected final DMNLayoutHelper layoutHelper;
    protected final OpenDiagramLayoutExecutor openDiagramLayoutExecutor;
    protected final DataTypesPage dataTypesPage;
    protected final DMNEditorSearchIndex editorSearchIndex;
    protected final SearchBarComponent<DMNSearchableElement> searchBarComponent;
    protected final KogitoClientDiagramService diagramServices;
    protected final CanvasFileExport canvasFileExport;
    protected final Promises promises;
    protected final IncludedModelsPage includedModelsPage;
    protected final KogitoChannelHelper kogitoChannelHelper;
    protected final DRDNameChanger drdNameChanger;

    private final ConfirmationDialog confirmationDialog;
    private final DecisionNavigatorPresenter decisionNavigatorPresenter;

    private final PMMLDocumentMetadataProvider pmmlDocumentMetadataProvider;

    protected AbstractDMNDiagramEditor(final View view,
                                       final MultiPageEditorContainerView containerView,
                                       final StunnerEditor stunnerEditor,
                                       final DMNEditorSearchIndex editorSearchIndex,
                                       final SearchBarComponent<DMNSearchableElement> searchBarComponent,
                                       final SessionManager sessionManager,
                                       final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                       final DocumentationView documentationView,
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
                                       final ConfirmationDialog confirmationDialog,
                                       final DecisionNavigatorPresenter decisionNavigatorPresenter,
                                       final PMMLDocumentMetadataProvider pmmlDocumentMetadataProvider) {
        super(view, containerView);
        this.stunnerEditor = stunnerEditor;
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.documentationView = documentationView;
        this.translationService = translationService;
        this.refreshFormPropertiesEvent = refreshFormPropertiesEvent;
        this.decisionNavigatorDock = decisionNavigatorDock;
        this.diagramPropertiesDock = diagramPropertiesDock;
        this.diagramPreviewAndExplorerDock = diagramPreviewAndExplorerDock;
        this.layoutHelper = layoutHelper;
        this.openDiagramLayoutExecutor = openDiagramLayoutExecutor;
        this.dataTypesPage = dataTypesPage;
        this.editorSearchIndex = editorSearchIndex;
        this.searchBarComponent = searchBarComponent;
        this.diagramServices = diagramServices;
        this.canvasFileExport = canvasFileExport;
        this.promises = promises;
        this.includedModelsPage = includedModelsPage;
        this.kogitoChannelHelper = kogitoChannelHelper;
        this.drdNameChanger = drdNameChanger;
        this.confirmationDialog = confirmationDialog;
        this.decisionNavigatorPresenter = decisionNavigatorPresenter;
        this.pmmlDocumentMetadataProvider = pmmlDocumentMetadataProvider;
    }

    public void onStartup(final PlaceRequest place) {
        init(place);
        stunnerEditor.setReadOnly(this.isReadOnly());
        ensureDocksAreInitialized();
        ensureTabBarVisibility(true);
        setParsingErrorBehavior();
        searchBarComponent.setSearchButtonVisibility(true);
    }

    private void setParsingErrorBehavior() {
        stunnerEditor.setParsingExceptionProcessor(e -> {
            ensureDocksAreRemoved();
            ensureTabBarVisibility(false);
            searchBarComponent.setSearchButtonVisibility(false);
        });
    }

    @SuppressWarnings("unchecked")
    public void addDocumentationPage(final Diagram diagram) {
        Optional.ofNullable(documentationView.isEnabled())
                .filter(Boolean.TRUE::equals)
                .ifPresent(enabled -> {
                    final String label = translationService.getValue(StunnerWidgetsConstants.Documentation);
                    addPage(new DocumentationPage(documentationView.initialize(diagram),
                                                  label,
                                                  () -> {
                                                  },
                                                  //check the DocumentationPage is active, the index is 2
                                                  () -> Objects.equals(2, getSelectedTabIndex())));
                });
    }

    void setupSessionHeaderContainer() {
        SessionDiagramPresenter presenter = stunnerEditor.getPresenter();
        drdNameChanger.setSessionPresenterView(presenter.getView());
        presenter.getView().setSessionHeaderContainer(getWidget(drdNameChanger.getElement()));
    }

    private void setupEditorSearchIndex() {
        editorSearchIndex.setCurrentAssetHashcodeSupplier(stunnerEditor::getCurrentContentHash);
        editorSearchIndex.setIsDataTypesTabActiveSupplier(getIsDataTypesTabActiveSupplier());
    }

    Supplier<Boolean> getIsDataTypesTabActiveSupplier() {
        return () -> {
            final int selectedPageIndex = getWidget().getMultiPage().selectedPage();
            return selectedPageIndex == DATA_TYPES_PAGE_INDEX;
        };
    }

    void setupSearchComponent() {
        final HTMLElement element = searchBarComponent.getView().getElement();

        searchBarComponent.init(editorSearchIndex);
        getWidget().getMultiPage().addTabBarWidget(getWidget(element));
    }

    protected ElementWrapperWidget<?> getWidget(final HTMLElement element) {
        return ElementWrapperWidget.getWidget(element);
    }

    @SuppressWarnings("unused")
    public void onDataTypePageNavTabActiveEvent(final DataTypePageTabActiveEvent event) {
        getWidget().getMultiPage().selectPage(DATA_TYPES_PAGE_INDEX);
    }

    @SuppressWarnings("all")
    public void open(final Diagram diagram,
                     final SessionPresenter.SessionPresenterCallback callback) {
        ensureDocksAreInitialized();
        ensureTabBarVisibility(true);
        searchBarComponent.setSearchButtonVisibility(true);
        if (layoutHelper.hasLayoutInformation(diagram)) {
            executeOpen(diagram, callback);
        } else {
            showAutomaticLayoutDialog(diagram, callback);
        }
    }

    void showAutomaticLayoutDialog(final Diagram diagram,
                                   final SessionPresenter.SessionPresenterCallback callback) {
        confirmationDialog.show(translationService.getValue(DMNEditorConstants.AutomaticLayout_Label),
                                null,
                                translationService.getValue(DMNEditorConstants.AutomaticLayout_DiagramDoesNotHaveLayout),
                                () -> {
                                    layoutHelper.applyLayout(diagram, openDiagramLayoutExecutor);
                                    executeOpen(diagram, callback);
                                },
                                () -> executeOpen(diagram, callback));
    }

    void executeOpen(final Diagram diagram,
                     final SessionPresenter.SessionPresenterCallback callback) {
        final AbstractSession currentSession = stunnerEditor.isClosed()
                ? null
                : (AbstractSession) stunnerEditor.getSession();
        decisionNavigatorPresenter.setIsRefreshComponentsViewSuspended(true);
        stunnerEditor.open(diagram, getSessionPresenterCallback(diagram, callback, currentSession));
    }

    SessionPresenter.SessionPresenterCallback getSessionPresenterCallback(final Diagram diagram,
                                                                          final SessionPresenter.SessionPresenterCallback callback,
                                                                          final AbstractSession currentSession) {
        return new SessionPresenter.SessionPresenterCallback() {
            @Override
            public void afterCanvasInitialized() {
                callback.afterCanvasInitialized();
            }

            @Override
            public void onOpen(Diagram diagram) {
                callback.onOpen(diagram);
            }

            @Override
            public void afterSessionOpened() {
                callback.afterSessionOpened();
            }

            @Override
            public void onSuccess() {
                initialiseKieEditorForSession(diagram);
                setupSessionHeaderContainer();
                callback.onSuccess();
                if (null != currentSession) {
                    currentSession.close();
                }
                decisionNavigatorPresenter.setIsRefreshComponentsViewSuspended(false);
                decisionNavigatorPresenter.refreshComponentsView();
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                decisionNavigatorPresenter.setIsRefreshComponentsViewSuspended(false);
                callback.onError(error);
            }
        };
    }

    public void initialiseKieEditorForSession(final Diagram diagram) {
        resetEditorPages();
        onDiagramLoad();
        addDocumentationPage(diagram);
        getBaseEditorView().hideBusyIndicator();
        getWidget().getMultiPage().addPage(dataTypesPage);
        if (kogitoChannelHelper.isIncludedModelEnabled()) {
            getWidget().getMultiPage().addPage(includedModelsPage);
        }
        setupEditorSearchIndex();
        setupSearchComponent();
        pmmlDocumentMetadataProvider.loadPMMLIncludedDocuments();
    }

    protected void onDiagramLoad() {

    }

    public void onOpen() {
    }

    public void onClose() {
        stunnerEditor.close();

        ensureDocksAreRemoved();

        dataTypesPage.disableShortcuts();
    }

    @Override
    public IsWidget asWidget() {
        return super.asWidget();
    }

    public void onDataTypeEditModeToggle(final DataTypeEditModeToggleEvent event) {
    }

    protected void onMultiPageEditorSelectedPageEvent(final MultiPageEditorSelectedPageEvent event) {
        searchBarComponent.disableSearch();
    }

    protected void onRefreshFormPropertiesEvent(final RefreshFormPropertiesEvent event) {
        searchBarComponent.disableSearch();
    }

    protected void onEditExpressionEvent(final EditExpressionEvent event) {
        searchBarComponent.disableSearch();
        if (isSameSession(stunnerEditor.getSession(), event.getSession())) {
            final DMNSession session = sessionManager.getCurrentSession();
            final ExpressionEditorView.Presenter expressionEditor = session.getExpressionEditor();
            sessionCommandManager.execute(session.getCanvasHandler(),
                                          new NavigateToExpressionEditorCommand(expressionEditor,
                                                                                stunnerEditor.getPresenter(),
                                                                                sessionManager,
                                                                                sessionCommandManager,
                                                                                refreshFormPropertiesEvent,
                                                                                event.getNodeUUID(),
                                                                                event.getHasExpression(),
                                                                                event.getHasName(),
                                                                                event.isOnlyVisualChangeAllowed()));
        }
    }

    private static boolean isSameSession(ClientSession s1, ClientSession s2) {
        return null != s1 && null != s2 && s1.equals(s2);
    }

    @Override
    public Promise<String> getContent() {
        return diagramServices.transform(stunnerEditor.getDiagram());
    }

    @Override
    public Promise<Void> setContent(final String path,
                                    final String value) {
        // Close current editor, if any.
        stunnerEditor.close();
        Promise promise =
                promises.create((success, failure) -> {
                    getBaseEditorView().showLoading();
                    diagramServices.transform(path,
                                              value,
                                              new ServiceCallback<Diagram>() {

                                                  @Override
                                                  public void onSuccess(final Diagram diagram) {
                                                      open(diagram,
                                                           new SessionPresenter.SessionPresenterCallback() {
                                                               @Override
                                                               public void onSuccess() {
                                                                   success.onInvoke((Object) null);
                                                               }

                                                               @Override
                                                               public void onError(ClientRuntimeError error) {
                                                                   onEditorError(error);
                                                                   failure.onInvoke(error);
                                                               }
                                                           });
                                                  }

                                                  @Override
                                                  public void onError(final ClientRuntimeError error) {
                                                      onEditorError(error);
                                                      failure.onInvoke(error);
                                                  }
                                              });
                });

        return promise;
    }

    private void onEditorError(ClientRuntimeError error) {
        getBaseEditorView().hideBusyIndicator();
        stunnerEditor.handleError(error);
        resetEditorPages();
    }

    public Promise<String> getPreview() {
        final CanvasHandler canvasHandler = stunnerEditor.getCanvasHandler();
        if (canvasHandler != null) {
            return Promise.resolve(canvasFileExport.exportToSvg((AbstractCanvasHandler) canvasHandler));
        } else {
            return Promise.resolve("");
        }
    }


    private void ensureDocksAreInitialized() {
        decisionNavigatorDock.init();
        diagramPropertiesDock.init();
        diagramPreviewAndExplorerDock.init();
    }

    private void ensureDocksAreRemoved() {
        decisionNavigatorDock.destroy();
        decisionNavigatorDock.resetContent();
        diagramPropertiesDock.destroy();
        diagramPreviewAndExplorerDock.destroy();
    }

    private void ensureTabBarVisibility(boolean visible) {
        getWidget().getMultiPage().setTabBarVisible(visible);
    }

}
