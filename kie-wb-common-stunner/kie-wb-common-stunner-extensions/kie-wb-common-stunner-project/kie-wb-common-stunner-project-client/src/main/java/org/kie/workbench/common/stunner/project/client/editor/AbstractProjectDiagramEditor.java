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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import elemental2.promise.Promise;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.presenters.Viewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.AbstractSessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerPresenter;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.event.OnSessionErrorEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramParsingException;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.documentation.DocumentationPage;
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.util.HashUtil;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.kie.workbench.common.stunner.core.validation.impl.ValidationUtils;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramLoseFocusEvent;
import org.kie.workbench.common.stunner.project.client.resources.i18n.StunnerProjectClientConstants;
import org.kie.workbench.common.stunner.project.client.screens.ProjectMessagesListener;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.stunner.project.editor.ProjectDiagramResource;
import org.kie.workbench.common.stunner.project.editor.impl.ProjectDiagramResourceImpl;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramResourceService;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
import org.uberfire.ext.widgets.common.client.ace.AceEditorMode;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.stunner.project.client.resources.i18n.StunnerProjectClientConstants.DIAGRAM_PARSING_ERROR;

public abstract class AbstractProjectDiagramEditor<R extends ClientResourceType> extends KieEditor<ProjectDiagramResource> {

    private static final Logger LOGGER = Logger.getLogger(AbstractProjectDiagramEditor.class.getName());
    private static final String TITLE_FORMAT_TEMPLATE = "#title.#suffix - #type";
    private final ManagedInstance<SessionEditorPresenter<EditorSession>> editorSessionPresenterInstances;
    private final ManagedInstance<SessionViewerPresenter<ViewerSession>> viewerSessionPresenterInstances;
    private final DiagramClientErrorHandler diagramClientErrorHandler;
    private final ClientTranslationService translationService;
    private final Caller<ProjectDiagramResourceService> projectDiagramResourceServiceCaller;
    private final TextEditorView xmlEditorView;
    protected ClientProjectDiagramService projectDiagramServices;
    protected ProjectDiagramEditorProxy editorProxy = ProjectDiagramEditorProxy.NULL_EDITOR;
    private ErrorPopupPresenter errorPopupPresenter;
    private Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent;
    private R resourceType;
    private AbstractProjectEditorMenuSessionItems<?> menuSessionItems;
    private ProjectMessagesListener projectMessagesListener;
    private Event<OnDiagramFocusEvent> onDiagramFocusEvent;
    private Event<OnDiagramLoseFocusEvent> onDiagramLostFocusEvent;
    private Optional<SessionEditorPresenter<EditorSession>> editorSessionPresenter = Optional.empty();
    private Optional<SessionViewerPresenter<ViewerSession>> viewerSessionPresenter = Optional.empty();
    private String title = "Project Diagram Editor";
    private boolean menuBarInitialzed = false;
    private DocumentationView documentationView;

    @Inject
    public AbstractProjectDiagramEditor(final View view,
                                        final DocumentationView documentationView,
                                        final PlaceManager placeManager,
                                        final ErrorPopupPresenter errorPopupPresenter,
                                        final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent,
                                        final SavePopUpPresenter savePopUpPresenter,
                                        final R resourceType,
                                        final ClientProjectDiagramService projectDiagramServices,
                                        final ManagedInstance<SessionEditorPresenter<EditorSession>> editorSessionPresenterInstances,
                                        final ManagedInstance<SessionViewerPresenter<ViewerSession>> viewerSessionPresenterInstances,
                                        final AbstractProjectEditorMenuSessionItems<?> menuSessionItems,
                                        final Event<OnDiagramFocusEvent> onDiagramFocusEvent,
                                        final Event<OnDiagramLoseFocusEvent> onDiagramLostFocusEvent,
                                        final ProjectMessagesListener projectMessagesListener,
                                        final DiagramClientErrorHandler diagramClientErrorHandler,
                                        final ClientTranslationService translationService,
                                        final TextEditorView xmlEditorView,
                                        final Caller<ProjectDiagramResourceService> projectDiagramResourceServiceCaller) {
        super(view);
        this.documentationView = documentationView;
        this.placeManager = placeManager;
        this.errorPopupPresenter = errorPopupPresenter;
        this.changeTitleNotificationEvent = changeTitleNotificationEvent;
        this.savePopUpPresenter = savePopUpPresenter;
        this.resourceType = resourceType;
        this.projectDiagramServices = projectDiagramServices;
        this.editorSessionPresenterInstances = editorSessionPresenterInstances;
        this.viewerSessionPresenterInstances = viewerSessionPresenterInstances;
        this.menuSessionItems = menuSessionItems;
        this.projectMessagesListener = projectMessagesListener;
        this.diagramClientErrorHandler = diagramClientErrorHandler;
        this.onDiagramFocusEvent = onDiagramFocusEvent;
        this.onDiagramLostFocusEvent = onDiagramLostFocusEvent;
        this.translationService = translationService;
        this.xmlEditorView = xmlEditorView;
        this.projectDiagramResourceServiceCaller = projectDiagramResourceServiceCaller;
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        title = translationService.getValue(StunnerProjectClientConstants.DIAGRAM_EDITOR_DEFAULT_TITLE);
        getView().init(this);
        projectMessagesListener.enable();
        menuSessionItems
                .setLoadingStarts(this::showLoadingViews)
                .setLoadingCompleted(this::hideLoadingViews)
                .setErrorConsumer(this::showError);
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
                                                 open(item);
                                             }

                                             @Override
                                             public void onError(final ClientRuntimeError error) {
                                                 onLoadError(error);
                                             }
                                         });
    }

    @SuppressWarnings("unchecked")
    public void open(final ProjectDiagram diagram) {
        open(diagram, Optional.empty());
    }

    @SuppressWarnings("unchecked")
    public void open(final ProjectDiagram diagram,
                     final Optional<SessionPresenter.SessionPresenterCallback<Diagram>> callback) {
        editorProxy = makeStunnerEditorProxy();
        showLoadingViews();

        //Open applicable SessionPresenter
        if (!isReadOnly()) {
            openSession(diagram, callback);
        } else {
            openReadOnlySession(diagram, callback);
        }
    }

    @SuppressWarnings("unchecked")
    protected ProjectDiagramEditorProxy makeStunnerEditorProxy() {
        final ProjectDiagramEditorProxy proxy = new ProjectDiagramEditorProxy();
        proxy.setContentSupplier(() -> new ProjectDiagramResourceImpl(getDiagram()));
        proxy.setSaveAfterValidationConsumer((continueSaveOnceValid) -> {
            menuSessionItems
                    .getCommands()
                    .getValidateSessionCommand()
                    .execute(new ClientSessionCommand.Callback<Collection<DiagramElementViolation<RuleViolation>>>() {
                        @Override
                        public void onSuccess() {
                            continueSaveOnceValid.execute();
                        }

                        @Override
                        public void onError(final Collection<DiagramElementViolation<RuleViolation>> violations) {
                            final Violation.Type maxSeverity = ValidationUtils.getMaxSeverity(violations);
                            if (maxSeverity.equals(Violation.Type.ERROR)) {
                                onValidationFailed(violations);
                            } else {
                                // Allow saving when only warnings founds.
                                continueSaveOnceValid.execute();
                            }
                        }
                    });
        });
        proxy.setSaveAfterUserConfirmationConsumer((commitMessage) -> {
            // Perform update operation remote call.
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
        });
        proxy.setShowNoChangesSinceLastSaveMessageConsumer((message) -> getSessionPresenter().getView().showMessage(message));
        proxy.setHashCodeSupplier(() -> {
            if (null == getDiagram()) {
                return 0;
            }
            int hash = getDiagram().hashCode();
            if (null == getCanvasHandler() ||
                    null == getCanvasHandler().getCanvas() ||
                    null == getCanvasHandler().getCanvas().getShapes()) {
                return hash;
            }
            Collection<Shape> collectionOfShapes = getCanvasHandler().getCanvas().getShapes();
            ArrayList<Shape> shapes = new ArrayList<>();
            shapes.addAll(collectionOfShapes);
            shapes.sort((a, b) -> (a.getShapeView().getShapeX() == b.getShapeView().getShapeX()) ?
                    (int) Math.round(a.getShapeView().getShapeY() - b.getShapeView().getShapeY()) :
                    (int) Math.round(a.getShapeView().getShapeX() - b.getShapeView().getShapeX()));
            for (Shape shape : shapes) {
                hash = HashUtil.combineHashCodes(hash,
                                                 Double.hashCode(shape.getShapeView().getShapeX()),
                                                 Double.hashCode(shape.getShapeView().getShapeY()));
            }
            return hash;
        });

        return proxy;
    }

    protected ProjectDiagramEditorProxy makeXmlEditorProxy() {
        final ProjectDiagramEditorProxy proxy = new ProjectDiagramEditorProxy();
        proxy.setContentSupplier(() -> new ProjectDiagramResourceImpl(xmlEditorView.getContent()));
        proxy.setSaveAfterValidationConsumer(Command::execute);
        proxy.setSaveAfterUserConfirmationConsumer((commitMessage) -> {
            final ObservablePath diagramPath = versionRecordManager.getCurrentPath();
            projectDiagramServices.saveAsXml(diagramPath,
                                             xmlEditorView.getContent(),
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
        });
        proxy.setShowNoChangesSinceLastSaveMessageConsumer((message) -> notification.fire(new NotificationEvent(message)));
        proxy.setHashCodeSupplier(() -> xmlEditorView.getContent().hashCode());
        return proxy;
    }

    protected boolean isReadOnly() {
        return isReadOnly;
    }

    private void openSession(final Optional<? extends AbstractSessionPresenter> presenter,
                             final ProjectDiagram diagram,
                             final Optional<SessionPresenter.SessionPresenterCallback<Diagram>> callback) {
        presenter.get()
                .open(diagram,
                      new SessionPresenter.SessionPresenterCallback<Diagram>() {
                          @Override
                          public void afterSessionOpened() {
                              callback.ifPresent(SessionPresenter.SessionPresenterCallback::afterSessionOpened);
                          }

                          @Override
                          public void afterCanvasInitialized() {
                              callback.ifPresent(DiagramViewer.DiagramViewerCallback::afterCanvasInitialized);
                          }

                          @Override
                          public void onSuccess() {
                              initialiseKieEditorForSession(diagram);
                              menuSessionItems.bind(getSession());
                              callback.ifPresent(Viewer.Callback::onSuccess);
                          }

                          @Override
                          public void onError(final ClientRuntimeError error) {
                              onLoadError(error);
                              callback.ifPresent(c -> c.onError(error));
                          }
                      });
    }

    protected void openSession(final ProjectDiagram diagram,
                               final Optional<SessionPresenter.SessionPresenterCallback<Diagram>> callback) {
        editorSessionPresenter = Optional.of(newSessionEditorPresenter());
        openSession(editorSessionPresenter, diagram, callback);
    }

    protected void openReadOnlySession(final ProjectDiagram diagram,
                                       final Optional<SessionPresenter.SessionPresenterCallback<Diagram>> callback) {
        viewerSessionPresenter = Optional.of(newSessionViewerPresenter());
        openSession(viewerSessionPresenter, diagram, callback);
    }

    protected void initialiseKieEditorForSession(final ProjectDiagram diagram) {
        resetEditorPages(diagram.getMetadata().getOverview());
        updateTitle(diagram.getMetadata().getTitle());
        addDocumentationPage(diagram);
        setOriginalHash(getCurrentDiagramHash());
        hideLoadingViews();
        onDiagramLoad();
    }

    protected void addDocumentationPage(ProjectDiagram diagram) {
        Optional.ofNullable(documentationView.isEnabled())
                .filter(Boolean.TRUE::equals)
                .ifPresent(enabled -> {
                    final String label = translationService.getValue(StunnerProjectClientConstants.DOCUMENTATION);
                    addPage(new DocumentationPage(documentationView.initialize(diagram),
                                                  label,
                                                  //firing the OnDiagramFocusEvent will force the docks to be minimized
                                                  () -> onDiagramFocusEvent.fire(new OnDiagramFocusEvent()),
                                                  //check the DocumentationPage is active, the index is 2
                                                  () -> Objects.equals(2, kieView.getSelectedTabIndex())));
                });
    }

    protected void onDiagramLoad() {
        /* Override this method to trigger some action after a Diagram is loaded. */
    }

    protected SessionEditorPresenter<EditorSession> newSessionEditorPresenter() {
        final SessionEditorPresenter<EditorSession> presenter =
                (SessionEditorPresenter<EditorSession>) editorSessionPresenterInstances.get()
                        .withToolbar(false)
                        .withPalette(true)
                        .displayNotifications(type -> true);
        getView().setWidget(presenter.getView());
        return presenter;
    }

    protected SessionViewerPresenter<ViewerSession> newSessionViewerPresenter() {
        final SessionViewerPresenter<ViewerSession> presenter =
                (SessionViewerPresenter<ViewerSession>) viewerSessionPresenterInstances.get()
                        .withToolbar(false)
                        .withPalette(false)
                        .displayNotifications(type -> true);
        getView().setWidget(presenter.getView());
        return presenter;
    }

    @Override
    protected void onValidate(final Command finished) {
        onValidationSuccess();
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
        final Command continueSaveOnceValid = () -> super.save();
        doSave(continueSaveOnceValid);
    }

    protected void doSave(final Command continueSaveOnceValid) {
        editorProxy.saveAfterValidation(continueSaveOnceValid);
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
        editorProxy.saveAfterUserConfirmation(commitMessage);
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
        onDiagramFocusEvent.fire(new OnDiagramFocusEvent());
        super.showDocks();
    }

    @Override
    protected Promise<Void> makeMenuBar() {
        if (!menuBarInitialzed) {
            menuSessionItems.populateMenu(fileMenuBuilder);
            makeAdditionalStunnerMenus(fileMenuBuilder);
            if (workbenchContext.getActiveWorkspaceProject().isPresent()) {
                final WorkspaceProject activeProject = workbenchContext.getActiveWorkspaceProject().get();
                return projectController.canUpdateProject(activeProject).then(canUpdateProject -> {
                    if (canUpdateProject) {
                        fileMenuBuilder
                                .addSave(versionRecordManager.newSaveMenuItem(this::saveAction))
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
        return () -> getEditorProxy().getContentSupplier().get();
    }

    protected ProjectDiagramEditorProxy getEditorProxy() {
        return editorProxy;
    }

    @Override
    protected Integer getCurrentContentHash() {
        return getCurrentDiagramHash();
    }

    protected void doOpen() {
    }

    protected void doFocus() {
        if (null != getSessionPresenter()) {
            getSessionPresenter().focus();
        }
    }

    protected void doLostFocus() {
        if (null != getSessionPresenter()) {
            getSessionPresenter().lostFocus();
        }
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
            doShowNoChangesSinceLastSaveMessage(message);
        }
    }

    protected void doShowNoChangesSinceLastSaveMessage(final String message) {
        editorProxy.showNoChangesSinceLastSaveMessage(message);
    }

    protected void destroySession() {
        //Release existing SessionPresenter
        editorSessionPresenter.ifPresent(session -> {
            session.destroy();
            editorSessionPresenter = Optional.empty();
        });
        viewerSessionPresenter.ifPresent(session -> {
            session.destroy();
            viewerSessionPresenter = Optional.empty();
        });
        editorSessionPresenterInstances.destroyAll();
        viewerSessionPresenterInstances.destroyAll();
    }

    protected void updateTitle(final String title) {
        // Change editor's title.
        this.title = formatTitle(title);
        changeTitleNotificationEvent.fire(new ChangeTitleWidgetEvent(this.place,
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
        return null != getSessionPresenter() ? getSessionPresenter().getInstance() : null;
    }

    @SuppressWarnings("unchecked")
    protected int getCurrentDiagramHash() {
        return editorProxy.getEditorHashCode();
    }

    protected CanvasHandler getCanvasHandler() {
        return null != getSession() ? getSession().getCanvasHandler() : null;
    }

    protected ProjectDiagram getDiagram() {
        return null != getCanvasHandler() ? (ProjectDiagram) getCanvasHandler().getDiagram() : null;
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

    protected View getView() {
        return (View) baseView;
    }

    protected void onSaveSuccess() {
        final String message = translationService.getValue(StunnerProjectClientConstants.DIAGRAM_SAVE_SUCCESSFUL);
        log(Level.INFO,
            message);
        getSessionPresenter().getView().showMessage(message);
        setOriginalHash(getCurrentDiagramHash());
    }

    protected void onSaveError(final ClientRuntimeError error) {
        showError(error);
    }

    private void onValidationSuccess() {
        log(Level.INFO,
            "Validation SUCCESS.");
    }

    protected void onValidationFailed(final Collection<DiagramElementViolation<RuleViolation>> violations) {
        log(Level.WARNING,
            "Validation FAILED [violations=" + violations.toString() + "]");
        hideLoadingViews();
    }

    protected void onLoadError(final ClientRuntimeError error) {
        final Throwable e = error.getThrowable();
        if (e instanceof DiagramParsingException) {
            final DiagramParsingException dpe = (DiagramParsingException) e;
            final Metadata metadata = dpe.getMetadata();
            final String xml = dpe.getXml();

            setOriginalHash(xml.hashCode());
            updateTitle(metadata.getTitle());
            resetEditorPages(((ProjectMetadata) metadata).getOverview());
            menuSessionItems.setEnabled(false);

            xmlEditorView.setReadOnly(isReadOnly);
            xmlEditorView.setContent(xml, AceEditorMode.XML);
            getView().setWidget(xmlEditorView.asWidget());
            editorProxy = makeXmlEditorProxy();
            hideLoadingViews();
            notification.fire(new NotificationEvent(getDiagramParsingErrorMessage(dpe),
                                                    NotificationEvent.NotificationType.ERROR));

            Scheduler.get().scheduleDeferred(xmlEditorView::onResize);
        } else {
            editorProxy = ProjectDiagramEditorProxy.NULL_EDITOR;
            showError(error);

            //close editor in case of error when opening the editor
            placeManager.forceClosePlace(new PathPlaceRequest(versionRecordManager.getCurrentPath(),
                                                              getEditorIdentifier()));
        }
    }

    protected String getDiagramParsingErrorMessage(final DiagramParsingException e) {
        return translationService.getValue(DIAGRAM_PARSING_ERROR, Objects.toString(e.getMessage(), ""));
    }

    protected void showError(final ClientRuntimeError error) {
        diagramClientErrorHandler.handleError(error, this::showError);
        log(Level.SEVERE, error.toString());
    }

    protected void showError(final String message) {
        errorPopupPresenter.showMessage(message);
        hideLoadingViews();
    }

    protected void log(final Level level,
                       final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level,
                       message);
        }
    }

    protected boolean hasUnsavedChanges() {
        return getCurrentDiagramHash() != originalHash;
    }

    protected ClientTranslationService getTranslationService() {
        return translationService;
    }

    protected void makeAdditionalStunnerMenus(final FileMenuBuilder fileMenuBuilder) {
    }

    public SessionPresenter<? extends ClientSession, ?, Diagram> getSessionPresenter() {
        if (editorSessionPresenter.isPresent()) {
            return editorSessionPresenter.get();
        } else if (viewerSessionPresenter.isPresent()) {
            return viewerSessionPresenter.get();
        }
        return null;
    }

    public AbstractProjectEditorMenuSessionItems getMenuSessionItems() {
        return menuSessionItems;
    }

    //For Unit Testing
    protected void setEditorSessionPresenter(final SessionEditorPresenter<EditorSession> presenter) {
        this.editorSessionPresenter = Optional.ofNullable(presenter);
    }

    //For Unit Testing
    protected void setReadOnlySessionPresenter(final SessionViewerPresenter<ViewerSession> presenter) {
        this.viewerSessionPresenter = Optional.ofNullable(presenter);
    }

    public interface View extends UberView<AbstractProjectDiagramEditor>,
                                  KieEditorView,
                                  RequiresResize,
                                  ProvidesResize,
                                  IsWidget {

        void setWidget(IsWidget widget);
    }
}
