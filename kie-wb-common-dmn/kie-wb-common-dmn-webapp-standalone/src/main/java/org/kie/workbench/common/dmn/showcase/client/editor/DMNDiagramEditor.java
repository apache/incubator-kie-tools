/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.ui.IsWidget;
import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.commands.general.NavigateToExpressionEditorCommand;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorDock;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPage;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsPageStateProviderImpl;
import org.kie.workbench.common.dmn.client.editors.search.DMNEditorSearchIndex;
import org.kie.workbench.common.dmn.client.editors.search.DMNSearchableElement;
import org.kie.workbench.common.dmn.client.editors.toolbar.ToolbarStateHandler;
import org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent;
import org.kie.workbench.common.dmn.client.editors.types.DataTypesPage;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELInitializer;
import org.kie.workbench.common.dmn.client.widgets.toolbar.DMNEditorToolbar;
import org.kie.workbench.common.dmn.showcase.client.perspectives.AuthoringPerspective;
import org.kie.workbench.common.dmn.showcase.client.services.DMNShowcaseDiagramService;
import org.kie.workbench.common.dmn.webapp.common.client.docks.preview.PreviewDiagramDock;
import org.kie.workbench.common.stunner.client.widgets.event.SessionFocusedEvent;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.MenuDevCommandsBuilder;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.views.session.ScreenErrorView;
import org.kie.workbench.common.stunner.client.widgets.views.session.ScreenPanelView;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.annotation.DiagramEditor;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.components.layout.LayoutHelper;
import org.kie.workbench.common.stunner.core.client.components.layout.OpenDiagramLayoutExecutor;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.event.OnSessionErrorEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.documentation.DocumentationPage;
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.kie.workbench.common.stunner.kogito.client.docks.DiagramEditorPropertiesDock;
import org.kie.workbench.common.widgets.client.search.component.SearchBarComponent;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.uberfire.client.annotations.WorkbenchContextId;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorSelectedPageEvent;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@DiagramEditor
@WorkbenchScreen(identifier = DMNDiagramEditor.EDITOR_ID)
public class DMNDiagramEditor implements KieEditorWrapperView.KieEditorWrapperPresenter {

    public static final String EDITOR_ID = "DMNDiagramEditor";

    //Editor tabs: [0] Main editor, [1] Documentation, [2] Data-Types, [3] Imported Models
    public static final int DATA_TYPES_PAGE_INDEX = 2;

    private static Logger LOGGER = Logger.getLogger(DMNDiagramEditor.class.getName());

    private final SessionManager sessionManager;
    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final SessionEditorPresenter<EditorSession> presenter;
    private final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;
    private final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent;
    private final Event<SessionFocusedEvent> sessionFocusedEvent;

    private final DecisionNavigatorDock decisionNavigatorDock;
    private final DiagramEditorPropertiesDock diagramPropertiesDock;
    private final PreviewDiagramDock diagramPreviewAndExplorerDock;

    private final LayoutHelper layoutHelper;
    private final OpenDiagramLayoutExecutor layoutExecutor;

    private final DataTypesPage dataTypesPage;
    private final IncludedModelsPage includedModelsPage;
    private final IncludedModelsPageStateProviderImpl importsPageProvider;
    private final DocumentationView<Diagram> documentationView;
    private final DMNEditorSearchIndex editorSearchIndex;
    private final SearchBarComponent<DMNSearchableElement> searchBarComponent;

    private final DefinitionManager definitionManager;
    private final ClientFactoryService clientFactoryServices;
    private final DMNShowcaseDiagramService diagramService;
    private final MenuDevCommandsBuilder menuDevCommandsBuilder;
    private final ScreenPanelView screenPanelView;
    private final ScreenErrorView screenErrorView;
    private final KieEditorWrapperView kieView;
    private final MonacoFEELInitializer feelInitializer;

    private PlaceRequest placeRequest;
    private String title = "Authoring Screen";
    private Menus menu = null;

    @Inject
    public DMNDiagramEditor(final SessionManager sessionManager,
                            final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                            final SessionEditorPresenter<EditorSession> presenter,
                            final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent,
                            final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent,
                            final Event<SessionFocusedEvent> sessionFocusedEvent,
                            final DecisionNavigatorDock decisionNavigatorDock,
                            final DiagramEditorPropertiesDock diagramPropertiesDock,
                            final PreviewDiagramDock diagramPreviewAndExplorerDock,
                            final LayoutHelper layoutHelper,
                            final OpenDiagramLayoutExecutor layoutExecutor,
                            final DataTypesPage dataTypesPage,
                            final IncludedModelsPage includedModelsPage,
                            final IncludedModelsPageStateProviderImpl importsPageProvider,
                            final @DMNEditor DocumentationView<Diagram> documentationView,
                            final DMNEditorSearchIndex editorSearchIndex,
                            final SearchBarComponent<DMNSearchableElement> searchBarComponent,
                            final DefinitionManager definitionManager,
                            final ClientFactoryService clientFactoryServices,
                            final DMNShowcaseDiagramService diagramService,
                            final MenuDevCommandsBuilder menuDevCommandsBuilder,
                            final ScreenPanelView screenPanelView,
                            final ScreenErrorView screenErrorView,
                            final KieEditorWrapperView kieView,
                            final MonacoFEELInitializer feelInitializer) {
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.presenter = presenter;
        this.refreshFormPropertiesEvent = refreshFormPropertiesEvent;
        this.changeTitleNotificationEvent = changeTitleNotificationEvent;
        this.sessionFocusedEvent = sessionFocusedEvent;

        this.decisionNavigatorDock = decisionNavigatorDock;
        this.diagramPropertiesDock = diagramPropertiesDock;
        this.diagramPreviewAndExplorerDock = diagramPreviewAndExplorerDock;

        this.layoutHelper = layoutHelper;
        this.layoutExecutor = layoutExecutor;

        this.dataTypesPage = dataTypesPage;
        this.includedModelsPage = includedModelsPage;
        this.importsPageProvider = importsPageProvider;
        this.documentationView = documentationView;
        this.editorSearchIndex = editorSearchIndex;
        this.searchBarComponent = searchBarComponent;

        this.definitionManager = definitionManager;
        this.clientFactoryServices = clientFactoryServices;
        this.diagramService = diagramService;
        this.menuDevCommandsBuilder = menuDevCommandsBuilder;
        this.screenPanelView = screenPanelView;
        this.screenErrorView = screenErrorView;
        this.kieView = kieView;
        this.feelInitializer = feelInitializer;
    }

    @PostConstruct
    public void init() {
        decisionNavigatorDock.init(AuthoringPerspective.PERSPECTIVE_ID);
        diagramPropertiesDock.init(AuthoringPerspective.PERSPECTIVE_ID);
        diagramPreviewAndExplorerDock.init(AuthoringPerspective.PERSPECTIVE_ID);

        kieView.setPresenter(this);
        kieView.clear();
        kieView.addMainEditorPage(screenPanelView.asWidget());
        kieView.getMultiPage().addPage(getDocumentationPage());
        kieView.getMultiPage().addPage(dataTypesPage);
        kieView.getMultiPage().addPage(includedModelsPage);

        setupEditorSearchIndex();
        setupSearchComponent();
    }

    private void setupEditorSearchIndex() {
        editorSearchIndex.setCurrentAssetHashcodeSupplier(getHashcodeSupplier());
        editorSearchIndex.setIsDataTypesTabActiveSupplier(getIsDataTypesTabActiveSupplier());
    }

    Supplier<Integer> getHashcodeSupplier() {
        return () -> getDiagram().hashCode();
    }

    Supplier<Boolean> getIsDataTypesTabActiveSupplier() {
        return () -> {
            final int selectedPageIndex = kieView.getMultiPage().selectedPage();
            return selectedPageIndex == DATA_TYPES_PAGE_INDEX;
        };
    }

    void setupSearchComponent() {
        final HTMLElement element = searchBarComponent.getView().getElement();

        searchBarComponent.init(editorSearchIndex);
        kieView.getMultiPage().addTabBarWidget(getWidget(element));
    }

    DocumentationPage getDocumentationPage() {
        return new DocumentationPage(documentationView, "Documentation", () -> { /* Nothing. */ }, () -> true);
    }

    public void onDataTypePageNavTabActiveEvent(final @Observes DataTypePageTabActiveEvent event) {
        kieView.getMultiPage().selectPage(DATA_TYPES_PAGE_INDEX);
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        this.placeRequest = placeRequest;
        this.menu = makeMenuBar();
        final String name = placeRequest.getParameter("name",
                                                      "");
        final boolean isCreate = name == null || name.trim().length() == 0;
        final Command callback = getOnStartupDiagramEditorCallback();
        if (isCreate) {
            final String defSetId = placeRequest.getParameter("defSetId",
                                                              "");
            final String shapeSetd = placeRequest.getParameter("shapeSetId",
                                                               "");
            final String title = placeRequest.getParameter("title",
                                                           "");
            // Create a new diagram.
            newDiagram(UUID.uuid(),
                       title,
                       defSetId,
                       shapeSetd,
                       callback);
        } else {
            // Load an existing diagram.
            load(name,
                 callback);
        }
    }

    Command getOnStartupDiagramEditorCallback() {
        return () -> {

            final Diagram diagram = getDiagram();

            if (null != diagram) {
                updateTitle(diagram.getMetadata().getTitle());
                documentationView.initialize(diagram);
            }
        };
    }

    public void onDataTypeEditModeToggle(final @Observes DataTypeEditModeToggleEvent editModeToggleEvent) {

        if (editModeToggleEvent.isEditModeEnabled()) {
            disableSaveMenuItem();
        } else {
            enableSaveMenuItem();
        }
    }

    private void disableSaveMenuItem() {
        getSaveMenuItem(saveMenuItem -> saveMenuItem.setEnabled(false));
    }

    private void enableSaveMenuItem() {
        getSaveMenuItem(saveMenuItem -> saveMenuItem.setEnabled(true));
    }

    private void getSaveMenuItem(final Consumer<MenuItem> saveMenuItemConsumer) {
        getMenu(menus -> saveMenuItemConsumer.accept(menus.getItems().get(0)));
    }

    private Menus makeMenuBar() {
        final MenuFactory.TopLevelMenusBuilder<MenuFactory.MenuBuilder> m =
                MenuFactory
                        .newTopLevelMenu("Save")
                        .respondsWith(getSaveCommand())
                        .endMenu();
        m.newTopLevelMenu(menuDevCommandsBuilder.build()).endMenu();
        return m.build();
    }

    private Command getSaveCommand() {
        return this::validateAndSave;
    }

    private void validateAndSave() {
        final Command save = this::save;
        final DMNEditorToolbar toolbar = (DMNEditorToolbar) presenter.getToolbar();
        toolbar
                .getValidateCommand()
                .execute(new ClientSessionCommand.Callback<Collection<DiagramElementViolation<RuleViolation>>>() {
                    @Override
                    public void onSuccess() {
                        save.execute();
                    }

                    @Override
                    public void onError(final Collection<DiagramElementViolation<RuleViolation>> violations) {
                        save.execute();
                    }
                });
    }

    private void save() {
        diagramService.save(getSession(),
                            new ServiceCallback<Diagram<Graph, Metadata>>() {
                                @Override
                                public void onSuccess(Diagram<Graph, Metadata> item) {
                                    log(Level.INFO, "Save operation finished for diagram [" + item.getName() + "].");
                                }

                                @Override
                                public void onError(ClientRuntimeError error) {
                                    showError(error);
                                }
                            });
    }

    private void newDiagram(final String uuid,
                            final String title,
                            final String definitionSetId,
                            final String shapeSetId,
                            final Command callback) {
        BusyPopup.showMessage("Loading");
        final Metadata metadata = buildMetadata(definitionSetId,
                                                shapeSetId,
                                                title);
        clientFactoryServices.newDiagram(uuid,
                                         definitionSetId,
                                         metadata,
                                         new ServiceCallback<Diagram>() {
                                             @Override
                                             public void onSuccess(final Diagram diagram) {
                                                 final Metadata metadata = diagram.getMetadata();
                                                 metadata.setShapeSetId(shapeSetId);
                                                 metadata.setTitle(title);
                                                 open(diagram,
                                                      callback);
                                             }

                                             @Override
                                             public void onError(final ClientRuntimeError error) {
                                                 showError(error);
                                                 callback.execute();
                                             }
                                         });
    }

    private Metadata buildMetadata(final String defSetId,
                                   final String shapeSetId,
                                   final String title) {
        return new MetadataImpl.MetadataImplBuilder(defSetId,
                                                    definitionManager)
                .setTitle(title)
                .setShapeSetId(shapeSetId)
                .build();
    }

    public void onMultiPageEditorSelectedPageEvent(final @Observes MultiPageEditorSelectedPageEvent event) {
        searchBarComponent.disableSearch();
    }

    public void onRefreshFormPropertiesEvent(final @Observes RefreshFormPropertiesEvent event) {
        searchBarComponent.disableSearch();
    }

    private void load(final String name,
                      final Command callback) {
        BusyPopup.showMessage("Loading");
        diagramService.loadByName(name,
                                  new ServiceCallback<Diagram>() {
                                      @Override
                                      public void onSuccess(final Diagram diagram) {
                                          open(diagram,
                                               callback);
                                      }

                                      @Override
                                      public void onError(final ClientRuntimeError error) {
                                          showError(error);
                                          callback.execute();
                                      }
                                  });
    }

    void open(final Diagram diagram,
              final Command callback) {
        screenPanelView.setWidget(presenter.getView());
        layoutHelper.applyLayout(diagram, layoutExecutor);
        feelInitializer.initializeFEELEditor();
        presenter
                .withToolbar(true)
                .withPalette(true)
                .displayNotifications(type -> true)
                .open(diagram,
                      new ScreenPresenterCallback(() -> {
                          final ToolbarStateHandler toolbarStateHandler = new DMNStandaloneToolbarStateHandler((DMNEditorToolbar) presenter.getToolbar());
                          final ExpressionEditorView.Presenter expressionEditor = ((DMNSession) sessionManager.getCurrentSession()).getExpressionEditor();
                          expressionEditor.setToolbarStateHandler(toolbarStateHandler);
                          dataTypesPage.reload();
                          dataTypesPage.enableShortcuts();
                          includedModelsPage.setup(importsPageProvider.withDiagram(diagram));
                          setupCanvasHandler(presenter.getInstance());
                          openDock();
                          callback.execute();
                      }));
    }

    @OnFocus
    public void onFocus() {
        final EditorSession session = presenter.getInstance();
        log(Level.INFO, "FOCUS [" + session + "]");
        if (null != session) {
            sessionFocusedEvent.fire(new SessionFocusedEvent(session));
        }
    }

    private boolean isSameSession(final ClientSession other) {
        return null != other && null != getSession() && other.equals(getSession());
    }

    @OnClose
    public void onClose() {
        destroyDock();
        destroySession();
        dataTypesPage.disableShortcuts();
    }

    void setupCanvasHandler(final EditorSession session) {
        decisionNavigatorDock.setupCanvasHandler(session.getCanvasHandler());
    }

    void openDock() {
        decisionNavigatorDock.open();
        diagramPropertiesDock.open();
        diagramPreviewAndExplorerDock.open();
    }

    void destroyDock() {
        decisionNavigatorDock.close();
        decisionNavigatorDock.resetContent();
        diagramPropertiesDock.close();
        diagramPreviewAndExplorerDock.close();
    }

    void destroySession() {
        presenter.destroy();
    }

    @WorkbenchMenu
    public void getMenu(final Consumer<Menus> menusConsumer) {
        menusConsumer.accept(menu);
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return title;
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return kieView.asWidget();
    }

    @WorkbenchContextId
    public String getMyContextRef() {
        return "sessionDiagramEditorScreenContext";
    }

    @Override
    public void onSourceTabSelected() {

    }

    @Override
    public void onEditTabSelected() {

    }

    @Override
    public void onEditTabUnselected() {

    }

    @Override
    public void onOverviewSelected() {

    }

    void updateTitle(final String title) {
        // Change screen title.
        DMNDiagramEditor.this.title = title;
        changeTitleNotificationEvent.fire(new ChangeTitleWidgetEvent(placeRequest,
                                                                     this.title));
    }

    private EditorSession getSession() {
        return null != presenter ? presenter.getInstance() : null;
    }

    private CanvasHandler getCanvasHandler() {
        return null != getSession() ? getSession().getCanvasHandler() : null;
    }

    Diagram getDiagram() {
        return null != getCanvasHandler() ? getCanvasHandler().getDiagram() : null;
    }

    private void showError(final ClientRuntimeError error) {
        screenErrorView.showError(error);
        screenPanelView.setWidget(screenErrorView.asWidget());
        log(Level.SEVERE,
            error.toString());
        BusyPopup.close();
    }

    private void onSessionErrorEvent(@Observes OnSessionErrorEvent errorEvent) {
        if (isSameSession(errorEvent.getSession())) {
            showError(errorEvent.getError());
        }
    }

    void onEditExpressionEvent(final @Observes EditExpressionEvent event) {
        searchBarComponent.disableSearch();
        if (isSameSession(event.getSession())) {
            final DMNSession session = sessionManager.getCurrentSession();
            final ExpressionEditorView.Presenter expressionEditor = session.getExpressionEditor();
            sessionCommandManager.execute(session.getCanvasHandler(),
                                          new NavigateToExpressionEditorCommand(expressionEditor,
                                                                                presenter,
                                                                                sessionManager,
                                                                                sessionCommandManager,
                                                                                refreshFormPropertiesEvent,
                                                                                event.getNodeUUID(),
                                                                                event.getHasExpression(),
                                                                                event.getHasName(),
                                                                                event.isOnlyVisualChangeAllowed()));
        }
    }

    private void log(final Level level,
                     final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level,
                       message);
        }
    }

    ElementWrapperWidget<?> getWidget(final HTMLElement element) {
        return ElementWrapperWidget.getWidget(element);
    }

    private final class ScreenPresenterCallback implements SessionPresenter.SessionPresenterCallback<Diagram> {

        private final Command callback;

        private ScreenPresenterCallback(final Command callback) {
            this.callback = callback;
        }

        @Override
        public void afterSessionOpened() {

        }

        @Override
        public void afterCanvasInitialized() {

        }

        @Override
        public void onSuccess() {
            BusyPopup.close();
            callback.execute();
        }

        @Override
        public void onError(final ClientRuntimeError error) {
            showError(error);
            callback.execute();
        }
    }
}
