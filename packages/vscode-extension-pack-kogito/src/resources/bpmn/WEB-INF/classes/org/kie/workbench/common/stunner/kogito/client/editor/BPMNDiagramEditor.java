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
package org.kie.workbench.common.stunner.kogito.client.editor;

import java.util.Optional;
import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import elemental2.promise.Promise;
import org.jboss.errai.ioc.client.api.ManagedInstance;
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
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.kie.workbench.common.stunner.kogito.client.menus.BPMNStandaloneEditorMenuSessionItems;
import org.kie.workbench.common.stunner.kogito.client.perspectives.AuthoringPerspective;
import org.kie.workbench.common.stunner.submarine.api.diagram.SubmarineDiagram;
import org.kie.workbench.common.stunner.submarine.client.docks.DiagramEditorPreviewAndExplorerDock;
import org.kie.workbench.common.stunner.submarine.client.docks.DiagramEditorPropertiesDock;
import org.kie.workbench.common.stunner.submarine.client.editor.AbstractDiagramEditor;
import org.kie.workbench.common.stunner.submarine.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.submarine.client.service.SubmarineClientDiagramService;
import org.kie.workbench.common.submarine.client.editor.MultiPageEditorContainerView;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.annotations.WorkbenchClientEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView;
import org.uberfire.lifecycle.GetContent;
import org.uberfire.lifecycle.IsDirty;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnLostFocus;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.lifecycle.SetContent;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

@ApplicationScoped
@DiagramEditor
@WorkbenchClientEditor(identifier = BPMNDiagramEditor.EDITOR_ID)
public class BPMNDiagramEditor extends AbstractDiagramEditor {

    public static final String EDITOR_ID = "BPMNDiagramEditor";

    private final SessionManager sessionManager;
    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;
    private final Event<NotificationEvent> notificationEvent;

    private final DiagramEditorPreviewAndExplorerDock diagramPreviewAndExplorerDock;
    private final DiagramEditorPropertiesDock diagramPropertiesDock;

    private final LayoutHelper layoutHelper;
    private final OpenDiagramLayoutExecutor openDiagramLayoutExecutor;

    private final SubmarineClientDiagramService diagramServices;

    @Inject
    public BPMNDiagramEditor(final View view,
                             final FileMenuBuilder fileMenuBuilder,
                             final PlaceManager placeManager,
                             final MultiPageEditorContainerView multiPageEditorContainerView,
                             final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent,
                             final Event<NotificationEvent> notificationEvent,
                             final Event<OnDiagramFocusEvent> onDiagramFocusEvent,
                             final TextEditorView xmlEditorView,
                             final ManagedInstance<SessionEditorPresenter<EditorSession>> editorSessionPresenterInstances,
                             final ManagedInstance<SessionViewerPresenter<ViewerSession>> viewerSessionPresenterInstances,
                             final BPMNStandaloneEditorMenuSessionItems menuSessionItems,
                             final ErrorPopupPresenter errorPopupPresenter,
                             final DiagramClientErrorHandler diagramClientErrorHandler,
                             final ClientTranslationService translationService,
                             final DocumentationView documentationView,
                             final SessionManager sessionManager,
                             final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                             final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent,
                             final DiagramEditorPreviewAndExplorerDock diagramPreviewAndExplorerDock,
                             final DiagramEditorPropertiesDock diagramPropertiesDock,
                             final LayoutHelper layoutHelper,
                             final OpenDiagramLayoutExecutor openDiagramLayoutExecutor,
                             final SubmarineClientDiagramService diagramServices) {
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
              documentationView);
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.refreshFormPropertiesEvent = refreshFormPropertiesEvent;
        this.notificationEvent = notificationEvent;

        this.diagramPreviewAndExplorerDock = diagramPreviewAndExplorerDock;
        this.diagramPropertiesDock = diagramPropertiesDock;

        this.layoutHelper = layoutHelper;
        this.openDiagramLayoutExecutor = openDiagramLayoutExecutor;

        this.diagramServices = diagramServices;
    }

    @OnStartup
    @SuppressWarnings("unused")
    public void onStartup(final PlaceRequest place) {
        superDoStartUp(place);

        diagramPreviewAndExplorerDock.init(AuthoringPerspective.PERSPECTIVE_ID);
        diagramPropertiesDock.init(AuthoringPerspective.PERSPECTIVE_ID);

        getWidget().init(this);
    }

    void superDoStartUp(final PlaceRequest place) {
        super.doStartUp(place);
    }

    @Override
    public void open(final SubmarineDiagram diagram) {
        this.layoutHelper.applyLayout(diagram, openDiagramLayoutExecutor);
        super.open(diagram);
    }

    @OnOpen
    @SuppressWarnings("unused")
    public void onOpen() {
        super.doOpen();
    }

    @OnClose
    @SuppressWarnings("unused")
    public void onClose() {
        superOnClose();
        diagramPreviewAndExplorerDock.close();
        diagramPropertiesDock.close();
    }

    void superOnClose() {
        super.doClose();
    }

    @Override
    public void onDiagramLoad() {
        final Optional<CanvasHandler> canvasHandler = Optional.ofNullable(getCanvasHandler());

        canvasHandler.ifPresent(c -> {
            final Metadata metadata = c.getDiagram().getMetadata();
            metadata.setPath(makeMetadataPath(metadata.getRoot(), metadata.getTitle()));
            diagramPreviewAndExplorerDock.open();
            diagramPropertiesDock.open();
        });
    }

    private Path makeMetadataPath(final Path root,
                                  final String title) {
        final String uri = root.toURI();
        return PathFactory.newPath(title, uri + "/" + title + ".bpmn");
    }

    @OnFocus
    @SuppressWarnings("unused")
    public void onFocus() {
        superDoFocus();
        onDiagramLoad();
    }

    void superDoFocus() {
        super.doFocus();
    }

    @OnLostFocus
    @SuppressWarnings("unused")
    public void onLostFocus() {
        super.doLostFocus();
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
    protected void makeMenuBar() {
        if (!menuBarInitialized) {
            getMenuSessionItems().populateMenu(getFileMenuBuilder());
            makeAdditionalStunnerMenus(getFileMenuBuilder());
            menuBarInitialized = true;
        }
    }

    @Override
    @WorkbenchPartView
    public IsWidget asWidget() {
        return super.asWidget();
    }

    @OnMayClose
    public boolean onMayClose() {
        return super.mayClose();
    }

    @Override
    public String getEditorIdentifier() {
        return EDITOR_ID;
    }

    @GetContent
    @Override
    public Promise getContent() {
        return diagramServices.transform(getEditor().getEditorProxy().getContentSupplier().get());
    }

    @Override
    @IsDirty
    public boolean isDirty() {
        return super.isDirty();
    }

    @SetContent
    @Override
    public void setContent(final String value) {
        diagramServices.transform(value,
                                  new ServiceCallback<SubmarineDiagram>() {

                                      @Override
                                      public void onSuccess(final SubmarineDiagram diagram) {
                                          getEditor().open(diagram);
                                      }

                                      @Override
                                      public void onError(final ClientRuntimeError error) {
                                          BPMNDiagramEditor.this.getEditor().onLoadError(error);
                                      }
                                  });
    }

    @Override
    public void resetContentHash() {
        setOriginalContentHash(getCurrentDiagramHash());
    }
}
