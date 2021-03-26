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
package org.kie.workbench.common.dmn.webapp.kogito.common.client.editor;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.enterprise.event.Event;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import elemental2.promise.Promise;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.kie.workbench.common.dmn.client.commands.general.NavigateToExpressionEditorCommand;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorDock;
import org.kie.workbench.common.dmn.client.editors.drd.DRDNameChanger;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPage;
import org.kie.workbench.common.dmn.client.editors.included.common.IncludedModelsContext;
import org.kie.workbench.common.dmn.client.editors.search.DMNEditorSearchIndex;
import org.kie.workbench.common.dmn.client.editors.search.DMNSearchableElement;
import org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent;
import org.kie.workbench.common.dmn.client.editors.types.DataTypesPage;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELInitializer;
import org.kie.workbench.common.dmn.webapp.common.client.docks.preview.PreviewDiagramDock;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.GuidedTourBridgeInitializer;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerPresenter;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerView;
import org.kie.workbench.common.stunner.client.widgets.editor.StunnerEditor;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.resources.i18n.StunnerWidgetsConstants;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.components.layout.LayoutHelper;
import org.kie.workbench.common.stunner.core.client.components.layout.OpenDiagramLayoutExecutor;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.documentation.DocumentationPage;
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.kie.workbench.common.stunner.kogito.client.docks.DiagramEditorPropertiesDock;
import org.kie.workbench.common.stunner.kogito.client.service.KogitoClientDiagramService;
import org.kie.workbench.common.widgets.client.search.component.SearchBarComponent;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorSelectedPageEvent;
import org.uberfire.ext.editor.commons.client.BaseEditorView;
import org.uberfire.ext.editor.commons.client.menu.MenuItems;
import org.uberfire.lifecycle.GetContent;
import org.uberfire.lifecycle.GetPreview;
import org.uberfire.lifecycle.IsDirty;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnLostFocus;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.lifecycle.SetContent;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

import static elemental2.dom.DomGlobal.setTimeout;

public abstract class AbstractDMNDiagramEditor extends MultiPageEditorContainerPresenter<Diagram> {

    public interface View extends BaseEditorView,
                                  RequiresResize,
                                  ProvidesResize,
                                  IsWidget {

        void setWidget(IsWidget widget);
    }

    public static final String PERSPECTIVE_ID = "AuthoringPerspective";
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
    protected final LayoutHelper layoutHelper;
    protected final OpenDiagramLayoutExecutor openDiagramLayoutExecutor;
    protected final DataTypesPage dataTypesPage;
    protected final DMNEditorSearchIndex editorSearchIndex;
    protected final SearchBarComponent<DMNSearchableElement> searchBarComponent;
    protected final KogitoClientDiagramService diagramServices;
    protected final MonacoFEELInitializer feelInitializer;
    protected final CanvasFileExport canvasFileExport;
    protected final Promises promises;
    protected final IncludedModelsPage includedModelsPage;
    protected final IncludedModelsContext includedModelContext;
    protected final GuidedTourBridgeInitializer guidedTourBridgeInitializer;
    protected final DRDNameChanger drdNameChanger;

    public AbstractDMNDiagramEditor(final View view,
                                    final PlaceManager placeManager,
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
                                    final DRDNameChanger drdNameChanger) {
        super(view, placeManager, containerView);
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
        this.feelInitializer = feelInitializer;
        this.canvasFileExport = canvasFileExport;
        this.promises = promises;
        this.includedModelsPage = includedModelsPage;
        this.includedModelContext = includedModelContext;
        this.guidedTourBridgeInitializer = guidedTourBridgeInitializer;
        this.drdNameChanger = drdNameChanger;
    }

    @OnStartup
    @SuppressWarnings("unused")
    public void onStartup(final PlaceRequest place) {
        init(place);
        stunnerEditor.setReadOnly(this.isReadOnly());
        decisionNavigatorDock.init(PERSPECTIVE_ID);
        diagramPropertiesDock.init(PERSPECTIVE_ID);
        diagramPreviewAndExplorerDock.init(PERSPECTIVE_ID);
        guidedTourBridgeInitializer.init();
    }

    @Override
    protected Supplier<Diagram> getContentSupplier() {
        return stunnerEditor::getDiagram;
    }

    @Override
    protected Integer getCurrentContentHash() {
        return stunnerEditor.getCurrentContentHash();
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

    private void setupSessionHeaderContainer() {
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

    public void open(final Diagram diagram,
                     final SessionPresenter.SessionPresenterCallback callback) {
        this.layoutHelper.applyLayout(diagram, openDiagramLayoutExecutor);
        feelInitializer.initializeFEELEditor();
        stunnerEditor.open(diagram, new SessionPresenter.SessionPresenterCallback() {
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
            }

            @Override
            public void onError(ClientRuntimeError error) {
                callback.onError(error);
            }
        });
    }

    public void initialiseKieEditorForSession(final Diagram diagram) {
        resetEditorPages();
        onDiagramLoad();
        resetContentHash();
        addDocumentationPage(diagram);
        getBaseEditorView().hideBusyIndicator();
        getWidget().getMultiPage().addPage(dataTypesPage);
        if (includedModelContext.isIncludedModelChannel()) {
            getWidget().getMultiPage().addPage(includedModelsPage);
        }
        setupEditorSearchIndex();
        setupSearchComponent();
    }

    protected void onDiagramLoad() {

    }

    @OnOpen
    @SuppressWarnings("unused")
    public void onOpen() {
    }

    @OnClose
    @SuppressWarnings("unused")
    public void onClose() {
        stunnerEditor.close();

        decisionNavigatorDock.destroy();
        decisionNavigatorDock.resetContent();

        diagramPropertiesDock.destroy();
        diagramPreviewAndExplorerDock.destroy();

        dataTypesPage.disableShortcuts();
    }

    @OnFocus
    @SuppressWarnings("unused")
    public void onFocus() {
        stunnerEditor.focus();
        dataTypesPage.onFocus();
        dataTypesPage.enableShortcuts();
    }

    @OnLostFocus
    @SuppressWarnings("unused")
    public void onLostFocus() {
        stunnerEditor.lostFocus();
        dataTypesPage.onLostFocus();
    }

    @Override
    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return "";
    }

    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        menusConsumer.accept(super.getMenus());
    }

    @Override
    protected void buildMenuBar() {
    }

    @Override
    protected void makeMenuBar() {
    }

    @Override
    @WorkbenchPartView
    public IsWidget asWidget() {
        return super.asWidget();
    }

    @OnMayClose
    @SuppressWarnings("unused")
    public boolean onMayClose() {
        return super.mayClose();
    }

    public void onDataTypeEditModeToggle(final DataTypeEditModeToggleEvent event) {
        /* Delaying the 'onDataTypeEditModeToggleCallback' since external events
         * refresh the menu widget and override this change. */
        scheduleOnDataTypeEditModeToggleCallback(event);
    }

    protected void scheduleOnDataTypeEditModeToggleCallback(final DataTypeEditModeToggleEvent event) {
        setTimeout(getOnDataTypeEditModeToggleCallback(event), 250);
    }

    protected DomGlobal.SetTimeoutCallbackFn getOnDataTypeEditModeToggleCallback(final DataTypeEditModeToggleEvent event) {
        return (e) -> {
            if (event.isEditModeEnabled()) {
                disableMenuItem(MenuItems.SAVE);
            } else {
                enableMenuItem(MenuItems.SAVE);
            }
        };
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
    @GetContent
    public Promise getContent() {
        if (stunnerEditor.isXmlEditorEnabled()) {
            return promises.resolve(stunnerEditor.getXmlEditorView().getContent());
        }
        return diagramServices.transform(stunnerEditor.getDiagram());
    }

    @Override
    @IsDirty
    public boolean isDirty() {
        return super.isDirty();
    }

    @Override
    @SetContent
    public Promise setContent(final String path,
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

    @Override
    public void resetContentHash() {
        setOriginalContentHash(stunnerEditor.getCurrentContentHash());
    }

    @GetPreview
    public Promise getPreview() {
        final CanvasHandler canvasHandler = stunnerEditor.getCanvasHandler();
        if (canvasHandler != null) {
            return Promise.resolve(canvasFileExport.exportToSvg((AbstractCanvasHandler) canvasHandler));
        } else {
            return Promise.resolve("");
        }
    }
}
