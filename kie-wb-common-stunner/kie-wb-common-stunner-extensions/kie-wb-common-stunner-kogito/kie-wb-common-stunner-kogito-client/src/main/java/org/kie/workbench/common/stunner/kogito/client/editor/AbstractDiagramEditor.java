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

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.logging.client.LogConfiguration;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerPresenter;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerView;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerPresenter;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramParsingException;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.documentation.DocumentationPage;
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;
import org.kie.workbench.common.stunner.kogito.api.editor.impl.KogitoDiagramResourceImpl;
import org.kie.workbench.common.stunner.kogito.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.kogito.client.resources.i18n.KogitoClientConstants;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.widgets.common.client.ace.AceEditorMode;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

public abstract class AbstractDiagramEditor extends MultiPageEditorContainerPresenter<KogitoDiagramResourceImpl> implements DiagramEditorCore<Metadata, Diagram> {

    private static final Logger LOGGER = Logger.getLogger(AbstractDiagramEditor.class.getName());

    private final AbstractDiagramEditorMenuSessionItems<?> menuSessionItems;
    private final Event<ChangeTitleWidgetEvent> changeTitleEvent;
    private final Event<OnDiagramFocusEvent> onDiagramFocusEvent;
    private final ClientTranslationService translationService;
    private final DocumentationView documentationView;
    private final FileMenuBuilder fileMenuBuilder;

    private String title = "Diagram Editor";

    protected boolean menuBarInitialized = false;

    public class DiagramEditorCore extends AbstractDiagramEditorCore<Metadata, Diagram, KogitoDiagramResourceImpl, DiagramEditorProxy<KogitoDiagramResourceImpl>> {

        public DiagramEditorCore(final View baseEditorView,
                                 final TextEditorView xmlEditorView,
                                 final Event<NotificationEvent> notificationEvent,
                                 final ManagedInstance<SessionEditorPresenter<EditorSession>> editorSessionPresenterInstances,
                                 final ManagedInstance<SessionViewerPresenter<ViewerSession>> viewerSessionPresenterInstances,
                                 final AbstractDiagramEditorMenuSessionItems<?> menuSessionItems,
                                 final ErrorPopupPresenter errorPopupPresenter,
                                 final DiagramClientErrorHandler diagramClientErrorHandler,
                                 final ClientTranslationService translationService) {
            super(baseEditorView,
                  xmlEditorView,
                  notificationEvent,
                  editorSessionPresenterInstances,
                  viewerSessionPresenterInstances,
                  menuSessionItems,
                  errorPopupPresenter,
                  diagramClientErrorHandler,
                  translationService);
        }

        @Override
        protected boolean isReadOnly() {
            return AbstractDiagramEditor.this.isReadOnly();
        }

        @Override
        protected KogitoDiagramResourceImpl makeDiagramResourceImpl(final Diagram diagram) {
            return new KogitoDiagramResourceImpl(diagram);
        }

        @Override
        protected KogitoDiagramResourceImpl makeDiagramResourceImpl(final String xml) {
            return new KogitoDiagramResourceImpl(xml);
        }

        @Override
        protected DiagramEditorProxy<KogitoDiagramResourceImpl> makeEditorProxy() {
            return new DiagramEditorProxy<>();
        }

        @Override
        public Annotation[] getDockQualifiers() {
            return AbstractDiagramEditor.this.getDockQualifiers();
        }

        @Override
        public void initialiseKieEditorForSession(final Diagram diagram) {
            AbstractDiagramEditor.this.initialiseKieEditorForSession(diagram);
        }

        @Override
        public String getEditorIdentifier() {
            return AbstractDiagramEditor.this.getEditorIdentifier();
        }

        @Override
        public void onLoadError(final ClientRuntimeError error) {
            final Throwable e = error.getThrowable();
            if (e instanceof DiagramParsingException) {
                final DiagramParsingException dpe = (DiagramParsingException) e;
                final Metadata metadata = dpe.getMetadata();
                final String xml = dpe.getXml();

                setOriginalContentHash(xml.hashCode());
                updateTitle(metadata.getTitle());
                resetEditorPages();
                menuSessionItems.setEnabled(false);
                getXMLEditorView().setReadOnly(isReadOnly());
                getXMLEditorView().setContent(xml, AceEditorMode.XML);
                getView().setWidget(getXMLEditorView().asWidget());
                setEditorProxy(makeXmlEditorProxy());
                hideLoadingViews();
                getNotificationEvent().fire(new NotificationEvent(translationService.getValue(KogitoClientConstants.DIAGRAM_PARSING_ERROR, Objects.toString(e.getMessage(), "")),
                                                                  NotificationEvent.NotificationType.ERROR));

                Scheduler.get().scheduleDeferred(getXMLEditorView()::onResize);
            } else {
                setEditorProxy(new DiagramEditorProxy<>());
                showError(error);

                //close editor in case of error when opening the editor
                getPlaceManager().forceClosePlace(getPlaceRequest());
            }
        }
    }

    private final AbstractDiagramEditorCore<Metadata, Diagram, KogitoDiagramResourceImpl, DiagramEditorProxy<KogitoDiagramResourceImpl>> editor;

    public AbstractDiagramEditor(final View view,
                                 final FileMenuBuilder fileMenuBuilder,
                                 final PlaceManager placeManager,
                                 final MultiPageEditorContainerView multiPageEditorContainerView,
                                 final Event<ChangeTitleWidgetEvent> changeTitleEvent,
                                 final Event<NotificationEvent> notificationEvent,
                                 final Event<OnDiagramFocusEvent> onDiagramFocusEvent,
                                 final TextEditorView xmlEditorView,
                                 final ManagedInstance<SessionEditorPresenter<EditorSession>> editorSessionPresenterInstances,
                                 final ManagedInstance<SessionViewerPresenter<ViewerSession>> viewerSessionPresenterInstances,
                                 final AbstractDiagramEditorMenuSessionItems<?> menuSessionItems,
                                 final ErrorPopupPresenter errorPopupPresenter,
                                 final DiagramClientErrorHandler diagramClientErrorHandler,
                                 final ClientTranslationService translationService,
                                 final DocumentationView documentationView) {
        super(view,
              placeManager,
              multiPageEditorContainerView);
        this.menuSessionItems = menuSessionItems;
        this.changeTitleEvent = changeTitleEvent;
        this.onDiagramFocusEvent = onDiagramFocusEvent;
        this.translationService = translationService;
        this.documentationView = documentationView;
        this.fileMenuBuilder = fileMenuBuilder;

        this.editor = makeCore(view,
                               xmlEditorView,
                               notificationEvent,
                               editorSessionPresenterInstances,
                               viewerSessionPresenterInstances,
                               menuSessionItems,
                               errorPopupPresenter,
                               diagramClientErrorHandler,
                               translationService);
    }

    protected AbstractDiagramEditorCore<Metadata, Diagram, KogitoDiagramResourceImpl, DiagramEditorProxy<KogitoDiagramResourceImpl>> makeCore(final View view,
                                                                                                                                              final TextEditorView xmlEditorView,
                                                                                                                                              final Event<NotificationEvent> notificationEvent,
                                                                                                                                              final ManagedInstance<SessionEditorPresenter<EditorSession>> editorSessionPresenterInstances,
                                                                                                                                              final ManagedInstance<SessionViewerPresenter<ViewerSession>> viewerSessionPresenterInstances,
                                                                                                                                              final AbstractDiagramEditorMenuSessionItems<?> menuSessionItems,
                                                                                                                                              final ErrorPopupPresenter errorPopupPresenter,
                                                                                                                                              final DiagramClientErrorHandler diagramClientErrorHandler,
                                                                                                                                              final ClientTranslationService translationService) {
        return new DiagramEditorCore(view,
                                     xmlEditorView,
                                     notificationEvent,
                                     editorSessionPresenterInstances,
                                     viewerSessionPresenterInstances,
                                     menuSessionItems,
                                     errorPopupPresenter,
                                     diagramClientErrorHandler,
                                     translationService);
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        title = translationService.getValue(KogitoClientConstants.DIAGRAM_EDITOR_DEFAULT_TITLE);
        menuSessionItems
                .setLoadingStarts(this::showLoadingViews)
                .setLoadingCompleted(this::hideLoadingViews)
                .setErrorConsumer(editor::showError);
    }

    protected void doStartUp(final PlaceRequest place) {
        init(place);
    }

    @Override
    protected void makeMenuBar() {
        if (!menuBarInitialized) {
            menuSessionItems.populateMenu(fileMenuBuilder);
            makeAdditionalStunnerMenus(fileMenuBuilder);
            menuBarInitialized = true;
        }
    }

    @Override
    protected void buildMenuBar() {
        if (fileMenuBuilder != null) {
            setMenus(fileMenuBuilder.build());
        }
    }

    @Override
    protected Supplier<KogitoDiagramResourceImpl> getContentSupplier() {
        return editor.getEditorProxy().getContentSupplier();
    }

    @Override
    protected Integer getCurrentContentHash() {
        return editor.getCurrentDiagramHash();
    }

    protected void doOpen() {
    }

    protected void doClose() {
        menuSessionItems.destroy();
        editor.destroySession();
    }

    protected void showLoadingViews() {
        getView().showLoading();
    }

    protected void hideLoadingViews() {
        getView().hideBusyIndicator();
    }

    @Override
    protected Menus getMenus() {
        if (super.getMenus() == null) {
            makeMenuBar();
        }
        return super.getMenus();
    }

    protected View getView() {
        return (View) getBaseEditorView();
    }

    @Override
    public void open(final Diagram diagram) {
        editor.open(diagram);
    }

    @Override
    public Annotation[] getDockQualifiers() {
        return new Annotation[]{DefinitionManager.DEFAULT_QUALIFIER};
    }

    @Override
    public void initialiseKieEditorForSession(final Diagram diagram) {
        resetEditorPages();
        updateTitle(diagram.getMetadata().getTitle());
        onDiagramLoad();

        //Set original hash after onDiagramLoad that may have modified the Diagram
        setOriginalContentHash(getCurrentDiagramHash());

        addDocumentationPage(diagram);
        hideLoadingViews();
    }

    protected void updateTitle(final String title) {
        this.title = title;
        changeTitleEvent.fire(new ChangeTitleWidgetEvent(getPlaceRequest(),
                                                         this.title));
    }

    @SuppressWarnings("unchecked")
    protected void addDocumentationPage(final Diagram diagram) {
        Optional.of(documentationView.isEnabled())
                .filter(Boolean.TRUE::equals)
                .ifPresent(enabled -> {
                    final String label = translationService.getValue(KogitoClientConstants.DOCUMENTATION);
                    addPage(new DocumentationPage(documentationView.initialize(diagram),
                                                  label,
                                                  //firing the OnDiagramFocusEvent will force the docks to be minimized
                                                  () -> onDiagramFocusEvent.fire(new OnDiagramFocusEvent(getDockQualifiers())),
                                                  //check the DocumentationPage is active, the index is 2
                                                  () -> Objects.equals(2, getSelectedTabIndex())));
                });
    }

    protected void onDiagramLoad() {
        /* Override this method to trigger some action after a Diagram is loaded. */
    }

    @Override
    public SessionEditorPresenter<EditorSession> newSessionEditorPresenter() {
        return editor.newSessionEditorPresenter();
    }

    @Override
    public SessionViewerPresenter<ViewerSession> newSessionViewerPresenter() {
        return editor.newSessionViewerPresenter();
    }

    @Override
    public int getCurrentDiagramHash() {
        return editor.getCurrentDiagramHash();
    }

    @Override
    public CanvasHandler getCanvasHandler() {
        return editor.getCanvasHandler();
    }

    @Override
    public void onSaveError(final ClientRuntimeError error) {
        editor.onSaveError(error);
    }

    @Override
    public SessionPresenter<? extends ClientSession, ?, Diagram> getSessionPresenter() {
        return editor.getSessionPresenter();
    }

    @Override
    public void doFocus() {
        editor.doFocus();
    }

    @Override
    public void doLostFocus() {
        editor.doLostFocus();
    }

    protected boolean isSameSession(final ClientSession other) {
        return editor.isSameSession(other);
    }

    protected void log(final Level level,
                       final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level, message);
        }
    }

    @SuppressWarnings("unused")
    protected void makeAdditionalStunnerMenus(final FileMenuBuilder fileMenuBuilder) {
    }

    public AbstractDiagramEditorMenuSessionItems getMenuSessionItems() {
        return menuSessionItems;
    }

    public FileMenuBuilder getFileMenuBuilder() {
        return fileMenuBuilder;
    }

    protected AbstractDiagramEditorCore<Metadata, Diagram, KogitoDiagramResourceImpl, DiagramEditorProxy<KogitoDiagramResourceImpl>> getEditor() {
        return editor;
    }
}
