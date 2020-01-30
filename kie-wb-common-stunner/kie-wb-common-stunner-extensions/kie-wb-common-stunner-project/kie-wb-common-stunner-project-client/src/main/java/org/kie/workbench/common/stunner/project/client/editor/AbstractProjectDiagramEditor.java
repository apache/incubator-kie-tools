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

package org.kie.workbench.common.stunner.project.client.editor;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.logging.client.LogConfiguration;
import elemental2.promise.Promise;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerPresenter;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.event.OnSessionErrorEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramParsingException;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.documentation.DocumentationPage;
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;
import org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditorCore;
import org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditorMenuSessionItems;
import org.kie.workbench.common.stunner.kogito.client.editor.DiagramEditorCore;
import org.kie.workbench.common.stunner.kogito.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.kogito.client.editor.event.OnDiagramLoseFocusEvent;
import org.kie.workbench.common.stunner.project.client.resources.i18n.StunnerProjectClientConstants;
import org.kie.workbench.common.stunner.project.client.screens.ProjectMessagesListener;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.stunner.project.diagram.editor.ProjectDiagramResource;
import org.kie.workbench.common.stunner.project.diagram.editor.impl.ProjectDiagramResourceImpl;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramResourceService;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
import org.uberfire.ext.widgets.common.client.ace.AceEditorMode;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.stunner.project.client.resources.i18n.StunnerProjectClientConstants.DIAGRAM_PARSING_ERROR;

public abstract class AbstractProjectDiagramEditor<R extends ClientResourceType> extends KieEditor<ProjectDiagramResource> implements DiagramEditorCore<ProjectMetadata, ProjectDiagram> {

    private static final Logger LOGGER = Logger.getLogger(AbstractProjectDiagramEditor.class.getName());

    private static final String TITLE_FORMAT_TEMPLATE = "#title.#suffix - #type";

    public interface View extends AbstractDiagramEditorCore.View,
                                  KieEditorView {

    }

    private final AbstractDiagramEditorMenuSessionItems<?> menuSessionItems;
    private final Event<OnDiagramFocusEvent> onDiagramFocusEvent;
    private final Event<OnDiagramLoseFocusEvent> onDiagramLostFocusEvent;
    private final ClientTranslationService translationService;
    private final DocumentationView documentationView;
    private final R resourceType;
    private final ProjectMessagesListener projectMessagesListener;
    private final ClientProjectDiagramService projectDiagramServices;
    private final Caller<ProjectDiagramResourceService> projectDiagramResourceServiceCaller;

    private String title = "Project Diagram Editor";

    private boolean menuBarInitialzed = false;

    public class ProjectDiagramEditorCore extends AbstractProjectDiagramEditorCore<ProjectMetadata, ProjectDiagram, ProjectDiagramResource, ProjectDiagramEditorProxy<ProjectDiagramResource>> {

        public ProjectDiagramEditorCore(final AbstractProjectDiagramEditor.View baseEditorView,
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
            return AbstractProjectDiagramEditor.this.isReadOnly();
        }

        @Override
        protected ProjectDiagramResourceImpl makeDiagramResourceImpl(final ProjectDiagram diagram) {
            return new ProjectDiagramResourceImpl(diagram);
        }

        @Override
        protected ProjectDiagramResourceImpl makeDiagramResourceImpl(final String xml) {
            return new ProjectDiagramResourceImpl(xml);
        }

        @Override
        protected ProjectDiagramEditorProxy<ProjectDiagramResource> makeEditorProxy() {
            return new ProjectDiagramEditorProxy<>();
        }

        @Override
        public Annotation[] getDockQualifiers() {
            return AbstractProjectDiagramEditor.this.getDockQualifiers();
        }

        @Override
        public void initialiseKieEditorForSession(final ProjectDiagram diagram) {
            AbstractProjectDiagramEditor.this.initialiseKieEditorForSession(diagram);
        }

        @Override
        protected void saveOrUpdate(final String commitMessage) {
            final ObservablePath diagramPath = versionRecordManager.getCurrentPath();
            projectDiagramServices.saveOrUpdate(diagramPath,
                                                getDiagram(),
                                                metadata,
                                                commitMessage,
                                                new ServiceCallback<ProjectDiagram>() {
                                                    @Override
                                                    public void onSuccess(final ProjectDiagram item) {
                                                        getSaveSuccessCallback(item.hashCode()).callback(diagramPath);
                                                        onSaveSuccess();
                                                        hideLoadingViews();
                                                    }

                                                    @Override
                                                    public void onError(final ClientRuntimeError error) {
                                                        onSaveError(error);
                                                    }
                                                });
        }

        @Override
        protected void saveAsXML(final String commitMessage) {
            final ObservablePath diagramPath = versionRecordManager.getCurrentPath();
            projectDiagramServices.saveAsXml(diagramPath,
                                             getXMLEditorView().getContent(),
                                             metadata,
                                             commitMessage,
                                             new ServiceCallback<String>() {
                                                 @Override
                                                 public void onSuccess(final String xml) {
                                                     getSaveSuccessCallback(xml.hashCode()).callback(diagramPath);
                                                     notification.fire(new NotificationEvent(org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants.INSTANCE.ItemSavedSuccessfully()));
                                                     hideLoadingViews();
                                                 }

                                                 @Override
                                                 public void onError(final ClientRuntimeError error) {
                                                     onSaveError(error);
                                                 }
                                             });
        }

        @Override
        public String getEditorIdentifier() {
            return AbstractProjectDiagramEditor.this.getEditorIdentifier();
        }

        @Override
        public void onLoadError(final ClientRuntimeError error) {
            final Throwable e = error.getThrowable();
            if (e instanceof DiagramParsingException) {
                final DiagramParsingException dpe = (DiagramParsingException) e;
                final Metadata metadata = dpe.getMetadata();
                final String xml = dpe.getXml();

                setOriginalHash(xml.hashCode());
                updateTitle(metadata.getTitle());
                resetEditorPagesOnLoadError(((ProjectMetadata) metadata).getOverview());
                menuSessionItems.setEnabled(false);

                getXMLEditorView().setReadOnly(isReadOnly);
                getXMLEditorView().setContent(xml, AceEditorMode.XML);
                getView().setWidget(getXMLEditorView().asWidget());
                setEditorProxy(makeXmlEditorProxy());
                hideLoadingViews();
                notification.fire(new NotificationEvent(getDiagramParsingErrorMessage(dpe),
                                                        NotificationEvent.NotificationType.ERROR));

                Scheduler.get().scheduleDeferred(getXMLEditorView()::onResize);
            } else {
                setEditorProxy(makeEditorProxy());
                showError(error);

                //close editor in case of error when opening the editor
                placeManager.forceClosePlace(new PathPlaceRequest(versionRecordManager.getCurrentPath(),
                                                                  getEditorIdentifier()));
            }
        }
    }

    protected String getDiagramParsingErrorMessage(final DiagramParsingException e) {
        return translationService.getValue(DIAGRAM_PARSING_ERROR, Objects.toString(e.getMessage(), ""));
    }

    private final AbstractProjectDiagramEditorCore<ProjectMetadata, ProjectDiagram, ProjectDiagramResource, ProjectDiagramEditorProxy<ProjectDiagramResource>> editor;

    public AbstractProjectDiagramEditor(final AbstractProjectDiagramEditor.View view,
                                        final TextEditorView xmlEditorView,
                                        final ManagedInstance<SessionEditorPresenter<EditorSession>> editorSessionPresenterInstances,
                                        final ManagedInstance<SessionViewerPresenter<ViewerSession>> viewerSessionPresenterInstances,
                                        final Event<OnDiagramFocusEvent> onDiagramFocusEvent,
                                        final Event<OnDiagramLoseFocusEvent> onDiagramLostFocusEvent,
                                        final Event<NotificationEvent> notificationEvent,
                                        final ErrorPopupPresenter errorPopupPresenter,
                                        final DiagramClientErrorHandler diagramClientErrorHandler,
                                        final DocumentationView documentationView,
                                        final R resourceType,
                                        final AbstractDiagramEditorMenuSessionItems<?> menuSessionItems,
                                        final ProjectMessagesListener projectMessagesListener,
                                        final ClientTranslationService translationService,
                                        final ClientProjectDiagramService projectDiagramServices,
                                        final Caller<ProjectDiagramResourceService> projectDiagramResourceServiceCaller) {
        super(view);

        this.menuSessionItems = menuSessionItems;
        this.onDiagramFocusEvent = onDiagramFocusEvent;
        this.onDiagramLostFocusEvent = onDiagramLostFocusEvent;
        this.translationService = translationService;
        this.documentationView = documentationView;
        this.resourceType = resourceType;
        this.projectMessagesListener = projectMessagesListener;
        this.projectDiagramServices = projectDiagramServices;
        this.projectDiagramResourceServiceCaller = projectDiagramResourceServiceCaller;

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

    protected AbstractProjectDiagramEditorCore<ProjectMetadata, ProjectDiagram, ProjectDiagramResource, ProjectDiagramEditorProxy<ProjectDiagramResource>> makeCore(final AbstractProjectDiagramEditor.View view,
                                                                                                                                                                    final TextEditorView xmlEditorView,
                                                                                                                                                                    final Event<NotificationEvent> notificationEvent,
                                                                                                                                                                    final ManagedInstance<SessionEditorPresenter<EditorSession>> editorSessionPresenterInstances,
                                                                                                                                                                    final ManagedInstance<SessionViewerPresenter<ViewerSession>> viewerSessionPresenterInstances,
                                                                                                                                                                    final AbstractDiagramEditorMenuSessionItems<?> menuSessionItems,
                                                                                                                                                                    final ErrorPopupPresenter errorPopupPresenter,
                                                                                                                                                                    final DiagramClientErrorHandler diagramClientErrorHandler,
                                                                                                                                                                    final ClientTranslationService translationService) {
        return new ProjectDiagramEditorCore(view,
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
        title = translationService.getValue(StunnerProjectClientConstants.DIAGRAM_EDITOR_DEFAULT_TITLE);
        projectMessagesListener.enable();
        menuSessionItems
                .setLoadingStarts(this::showLoadingViews)
                .setLoadingCompleted(this::hideLoadingViews)
                .setErrorConsumer(editor::showError);
    }

    protected void doStartUp(final ObservablePath path,
                             final PlaceRequest place) {
        init(path,
             place,
             resourceType);
    }

    @Override
    protected void loadContent() {
        destroySession();
        projectDiagramServices.getByPath(versionRecordManager.getCurrentPath(),
                                         new ServiceCallback<ProjectDiagram>() {
                                             @Override
                                             public void onSuccess(final ProjectDiagram item) {
                                                 editor.open(item);
                                             }

                                             @Override
                                             public void onError(final ClientRuntimeError error) {
                                                 editor.onLoadError(error);
                                             }
                                         });
    }

    protected boolean isReadOnly() {
        return isReadOnly;
    }

    @Override
    protected void onValidate(final Command finished) {
        log(Level.INFO, "Validation SUCCESS.");
        hideLoadingViews();
        finished.execute();
    }

    /**
     * This method is called just once clicking on save.
     * Before starting the save process, let's perform a diagram validation
     * to check all it's valid.
     * It's allowed to continue with the save process event if warnings found,
     * but cannot save if any error is present in order to
     * guarantee the diagram's consistency.
     */
    @Override
    protected void save() {
        final Command continueSaveOnceValid = () -> {
            if (saveWithComments) {
                super.save();
            } else {
                save("");
            }
        };
        doSave(continueSaveOnceValid);
    }

    protected void doSave(final Command continueSaveOnceValid) {
        editor.getEditorProxy().saveAfterValidation(continueSaveOnceValid);
    }

    /**
     * Considering the diagram valid at this point ,
     * it delegates the save operation to the diagram services bean.
     * @param commitMessage The commit's message.
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void save(final String commitMessage) {
        super.save(commitMessage);
        doSave(commitMessage);
    }

    protected void doSave(final String commitMessage) {
        editor.getEditorProxy().saveAfterUserConfirmation(commitMessage);
    }

    @Override
    public RemoteCallback<Path> getSaveSuccessCallback(final int newHash) {
        return (path) -> {
            versionRecordManager.reloadVersions(path);
            setOriginalHash(newHash);
        };
    }

    @Override
    public void hideDocks() {
        super.hideDocks();
        onDiagramLostFocusEvent.fire(new OnDiagramLoseFocusEvent());
    }

    @Override
    public void showDocks() {
        // Docks are shown by AppFormer before the session/diagram has been opened. It is therefore impossible to use
        // the ideal getDiagram().getMetadata().getDefinitionSetId() and use that as the qualifier for docks.
        onDiagramFocusEvent.fire(new OnDiagramFocusEvent(getDockQualifiers()));
        super.showDocks();
    }

    @Override
    //Override visibility from KieEditor to allow inner class ProjectDiagramEditorCore access
    public abstract String getEditorIdentifier();

    @Override
    protected Promise<Void> makeMenuBar() {
        if (!menuBarInitialzed) {
            menuSessionItems.populateMenu(fileMenuBuilder);
            makeAdditionalStunnerMenus(fileMenuBuilder);
            if (workbenchContext.getActiveWorkspaceProject().isPresent()) {
                final WorkspaceProject activeProject = workbenchContext.getActiveWorkspaceProject().get();
                return projectController.canUpdateProject(activeProject).then(canUpdateProject -> {
                    if (canUpdateProject) {
                        final ParameterizedCommand<Boolean> onSave = withComments -> {
                            saveWithComments = withComments;
                            saveAction();
                        };
                        fileMenuBuilder
                                .addSave(versionRecordManager.newSaveMenuItem(onSave))
                                .addCopy(versionRecordManager.getCurrentPath(),
                                         assetUpdateValidator)
                                .addRename(getSaveAndRename())
                                .addDelete(versionRecordManager.getPathToLatest(),
                                           assetUpdateValidator);
                    }

                    addDownloadMenuItem(fileMenuBuilder);

                    fileMenuBuilder
                            .addNewTopLevelMenu(versionRecordManager.buildMenu())
                            .addNewTopLevelMenu(alertsButtonMenuItemBuilder.build());
                    menuBarInitialzed = true;

                    return promises.resolve();
                });
            }
        }
        return promises.resolve();
    }

    @Override
    protected Command getSaveAndRename() {
        return super.getSaveAndRename();
    }

    @Override
    protected Caller<? extends SupportsSaveAndRename<ProjectDiagramResource, org.guvnor.common.services.shared.metadata.model.Metadata>> getSaveAndRenameServiceCaller() {
        return projectDiagramResourceServiceCaller;
    }

    @Override
    protected Supplier<ProjectDiagramResource> getContentSupplier() {
        return editor.getEditorProxy().getContentSupplier();
    }

    @Override
    protected Integer getCurrentContentHash() {
        return editor.getCurrentDiagramHash();
    }

    protected void doOpen() {
    }

    protected void doClose() {
        menuItems.clear();
        menuSessionItems.destroy();
        destroySession();
    }

    protected void showLoadingViews() {
        getView().showLoading();
    }

    protected void showSavingViews() {
        getView().showSaving();
    }

    protected void hideLoadingViews() {
        getView().hideBusyIndicator();
    }

    @SuppressWarnings("unused")
    void onSessionErrorEvent(final @Observes OnSessionErrorEvent errorEvent) {
        if (isSameSession(errorEvent.getSession())) {
            executeWithConfirm(translationService.getValue(StunnerProjectClientConstants.ON_ERROR_CONFIRM_UNDO_LAST_ACTION,
                                                           errorEvent.getError()),
                               () -> menuSessionItems.getCommands().getUndoSessionCommand().execute());
        }
    }

    protected boolean isSameSession(final ClientSession other) {
        return null != other && null != getSession() && other.equals(getSession());
    }

    @Override
    public String getTitleText() {
        return title;
    }

    @Override
    protected void onSave() {
        if (hasUnsavedChanges()) {
            super.onSave();
        } else if (!versionRecordManager.isCurrentLatest()) {
            //If VersionRecordManager is not showing the latest the save represents a "Restore" operation.
            super.onSave();
        } else {
            final String message = CommonConstants.INSTANCE.NoChangesSinceLastSave();
            log(Level.INFO, message);
            editor.doShowNoChangesSinceLastSaveMessage(message);
        }
    }

    protected void updateTitle(final String title) {
        // Change editor's title.
        this.title = formatTitle(title);
        changeTitleNotification.fire(new ChangeTitleWidgetEvent(this.place,
                                                                this.title));
    }

    /**
     * Format the Diagram title to be displayed on the Editor.
     * This method can be override to customization and the default implementation just return the title from the diagram metadata.
     * @param title diagram metadata title
     * @return formatted title
     */
    protected String formatTitle(final String title) {
        if (Objects.isNull(resourceType)) {
            return title;
        }
        return TITLE_FORMAT_TEMPLATE
                .replace("#title",
                         title)
                .replace("#suffix",
                         resourceType.getSuffix())
                .replace("#type",
                         resourceType.getShortName());
    }

    private ClientSession getSession() {
        return null != editor.getSessionPresenter() ? editor.getSessionPresenter().getInstance() : null;
    }

    private void executeWithConfirm(final String message,
                                    final Command command) {
        final Command yesCommand = command::execute;
        final Command noCommand = () -> {
        };
        final YesNoCancelPopup popup =
                YesNoCancelPopup.newYesNoCancelPopup(message,
                                                     null,
                                                     yesCommand,
                                                     noCommand,
                                                     noCommand);
        popup.show();
    }

    protected AbstractProjectDiagramEditor.View getView() {
        return (AbstractProjectDiagramEditor.View) baseView;
    }

    protected void onSaveSuccess() {
        final String message = translationService.getValue(StunnerProjectClientConstants.DIAGRAM_SAVE_SUCCESSFUL);
        log(Level.INFO,
            message);
        editor.getSessionPresenter().getView().showMessage(message);
        setOriginalHash(editor.getCurrentDiagramHash());
    }

    @Override
    public void open(final ProjectDiagram diagram) {
        editor.open(diagram);
    }

    @Override
    public Annotation[] getDockQualifiers() {
        return new Annotation[]{DefinitionManager.DEFAULT_QUALIFIER};
    }

    @Override
    public void initialiseKieEditorForSession(final ProjectDiagram diagram) {
        resetEditorPages(diagram.getMetadata().getOverview());
        updateTitle(diagram.getName());
        addDocumentationPage(diagram);
        setOriginalHash(getCurrentDiagramHash());
        hideLoadingViews();
        onDiagramLoad();
    }

    protected void destroySession() {
        editor.destroySession();
    }

    @SuppressWarnings("unchecked")
    public void addDocumentationPage(final ProjectDiagram diagram) {
        Optional.ofNullable(documentationView.isEnabled())
                .filter(Boolean.TRUE::equals)
                .ifPresent(enabled -> {
                    final String label = translationService.getValue(StunnerProjectClientConstants.DOCUMENTATION);
                    addPage(new DocumentationPage(documentationView.initialize(diagram),
                                                  label,
                                                  //firing the OnDiagramFocusEvent will force the docks to be minimized
                                                  () -> onDiagramFocusEvent.fire(new OnDiagramFocusEvent(getDockQualifiers())),
                                                  //check the DocumentationPage is active, the index is 2
                                                  () -> Objects.equals(2, kieView.getSelectedTabIndex())));
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

    protected void log(final Level level,
                       final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level, message);
        }
    }

    protected boolean hasUnsavedChanges() {
        return editor.getCurrentDiagramHash() != originalHash;
    }

    protected ClientTranslationService getTranslationService() {
        return translationService;
    }

    @SuppressWarnings("unused")
    protected void makeAdditionalStunnerMenus(final FileMenuBuilder fileMenuBuilder) {
    }

    public AbstractDiagramEditorMenuSessionItems getMenuSessionItems() {
        return menuSessionItems;
    }
}
