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
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import elemental2.promise.Promise;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.stunner.client.widgets.editor.StunnerEditor;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.resources.i18n.StunnerWidgetsConstants;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.event.OnSessionErrorEvent;
import org.kie.workbench.common.stunner.core.documentation.DocumentationPage;
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
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
import org.kie.workbench.common.stunner.project.service.ProjectDiagramResourceService;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.editor.commons.client.BaseEditorView;
import org.uberfire.ext.editor.commons.client.menu.common.SaveAndRenameCommandBuilder;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

public abstract class AbstractProjectDiagramEditor<R extends ClientResourceType>
        extends KieEditor<ProjectDiagram> {

    private static Logger LOGGER = Logger.getLogger(AbstractProjectDiagramEditor.class.getName());
    private static final String TITLE_FORMAT_TEMPLATE = "#title.#suffix - #type";

    public interface View extends BaseEditorView,
                                  RequiresResize,
                                  ProvidesResize,
                                  IsWidget,
                                  KieEditorView {

        void setWidget(IsWidget widget);
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
    private final StunnerEditor stunnerEditor;

    private String title = null;
    private boolean menuBarInitialized = false;

    public AbstractProjectDiagramEditor(final AbstractProjectDiagramEditor.View view,
                                        final Event<OnDiagramFocusEvent> onDiagramFocusEvent,
                                        final Event<OnDiagramLoseFocusEvent> onDiagramLostFocusEvent,
                                        final DocumentationView documentationView,
                                        final R resourceType,
                                        final AbstractDiagramEditorMenuSessionItems<?> menuSessionItems,
                                        final ProjectMessagesListener projectMessagesListener,
                                        final ClientTranslationService translationService,
                                        final ClientProjectDiagramService projectDiagramServices,
                                        final Caller<ProjectDiagramResourceService> projectDiagramResourceServiceCaller,
                                        final StunnerEditor stunnerEditor) {
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
        this.stunnerEditor = stunnerEditor;
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        title = translationService.getValue(StunnerProjectClientConstants.DIAGRAM_EDITOR_DEFAULT_TITLE);
        projectMessagesListener.enable();
        menuSessionItems
                .setLoadingStarts(this::showLoadingViews)
                .setLoadingCompleted(this::hideLoadingViews)
                .setErrorConsumer(this::logMenuItemError);
        getView().setWidget(stunnerEditor.getView());
    }

    protected void doStartUp(final ObservablePath path,
                             final PlaceRequest place) {
        init(path,
             place,
             resourceType);
        initializeStunnerEditor();
    }

    void initializeStunnerEditor() {
        stunnerEditor.setOnResetContentHashProcessor(h -> this.originalHash = h);
        stunnerEditor.setParsingExceptionProcessor(e -> {
            ProjectMetadata pm = (ProjectMetadata) e.getMetadata();
            updateTitle(pm.getTitle());
            resetEditorPagesOnLoadError(pm.getOverview());
            menuSessionItems.setEnabled(false);
            notification.fire(new NotificationEvent(translationService.getValue(StunnerWidgetsConstants.DiagramParsingError,
                                                                                Objects.toString(e.getMessage(), "")),
                                                    NotificationEvent.NotificationType.ERROR));
        });
        stunnerEditor.setExceptionProcessor(e -> {
            //close editor in case of error when opening the editor
            placeManager.forceClosePlace(new PathPlaceRequest(versionRecordManager.getCurrentPath(),
                                                              getEditorIdentifier()));
        });
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
                                                 AbstractProjectDiagramEditor.this.onError(error);
                                             }
                                         });
    }

    public void open(final ProjectDiagram diagram) {
        open(diagram,
             new SessionPresenter.SessionPresenterCallback() {
                 @Override
                 public void onSuccess() {
                 }

                 @Override
                 public void onError(ClientRuntimeError error) {
                 }
             });
    }

    public void open(final ProjectDiagram diagram,
                     final SessionPresenter.SessionPresenterCallback callback) {
        showLoadingViews();
        beforeOpen(diagram);
        stunnerEditor.open(diagram,
                           new SessionPresenter.SessionPresenterCallback() {
                               @Override
                               public void onSuccess() {
                                   initialiseKieEditorForSession(diagram);
                                   callback.onSuccess();
                               }

                               @Override
                               public void onError(ClientRuntimeError error) {
                                   hideLoadingViews();
                                   callback.onError(error);
                               }
                           });
    }

    protected void beforeOpen(final ProjectDiagram diagram) {
        stunnerEditor.setReadOnly(this.isReadOnly);
    }

    public void initialiseKieEditorForSession(final ProjectDiagram diagram) {
        resetEditorPages(diagram.getMetadata().getOverview());
        updateTitle(diagram.getName());
        addDocumentationPage(diagram);
        hideLoadingViews();
        menuSessionItems.bind(getSession());
        SaveAndRenameCommandBuilder saveAndRenameCommandBuilder = getSaveAndRenameCommandBuilder();
        saveAndRenameCommandBuilder.addContentSupplier(getContentSupplier());
    }

    @Override
    protected void onValidate(final Command finished) {
        hideLoadingViews();
        finished.execute();
    }

    void ifValidDiagram(final Command command) {
        getMenuSessionItems()
                .getCommands()
                .getValidateSessionCommand()
                .execute(new ClientSessionCommand.Callback<Collection<DiagramElementViolation<RuleViolation>>>() {
                    @Override
                    public void onSuccess() {
                        onValidate(command);
                    }

                    @Override
                    public void onError(final Collection<DiagramElementViolation<RuleViolation>> violations) {
                        final Violation.Type maxSeverity = ValidationUtils.getMaxSeverity(violations);
                        if (isSaveAllowedAfterValidationFailed(maxSeverity)) {
                            onValidate(command);
                        } else {
                            onValidate(() -> {
                            });
                        }
                    }
                });
    }

    public boolean isSaveAllowedAfterValidationFailed(final Violation.Type maxSeverity) {
        return !maxSeverity.equals(Violation.Type.ERROR);
    }

    @Override
    protected void save(final String commitMessage) {
        if (!stunnerEditor.isXmlEditorEnabled()) {
            ifValidDiagram(() -> saveOrUpdate(commitMessage));
        } else {
            saveAsXML(commitMessage);
        }
    }

    @Override
    protected void onSave() {
        if (hasUnsavedChanges()) {
            super.onSave();
        } else if (!versionRecordManager.isCurrentLatest()) {
            //If VersionRecordManager is not showing the latest the save represents a "Restore" operation.
            super.onSave();
        } else {
            showMessage(CommonConstants.INSTANCE.NoChangesSinceLastSave());
        }
    }

    private void saveOrUpdate(final String commitMessage) {
        final ObservablePath diagramPath = versionRecordManager.getCurrentPath();
        projectDiagramServices.saveOrUpdate(diagramPath,
                                            (ProjectDiagram) stunnerEditor.getDiagram(),
                                            metadata,
                                            commitMessage,
                                            new ServiceCallback<ProjectDiagram>() {
                                                @Override
                                                public void onSuccess(final ProjectDiagram item) {
                                                    getSaveSuccessCallback(item.hashCode()).callback(diagramPath);
                                                    onSaveSuccess();
                                                }

                                                @Override
                                                public void onError(final ClientRuntimeError error) {
                                                    AbstractProjectDiagramEditor.this.onError(error);
                                                }
                                            });
    }

    private void saveAsXML(final String commitMessage) {
        final ObservablePath diagramPath = versionRecordManager.getCurrentPath();
        projectDiagramServices.saveAsXml(diagramPath,
                                         stunnerEditor.getXmlEditorView().getContent(),
                                         metadata,
                                         commitMessage,
                                         new ServiceCallback<String>() {
                                             @Override
                                             public void onSuccess(final String xml) {
                                                 getSaveSuccessCallback(xml.hashCode()).callback(diagramPath);
                                                 showMessage(org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants.INSTANCE.ItemSavedSuccessfully());
                                                 onSaveSuccess();
                                             }

                                             @Override
                                             public void onError(final ClientRuntimeError error) {
                                                 AbstractProjectDiagramEditor.this.onError(error);
                                             }
                                         });
    }

    protected void onSaveSuccess() {
        showMessage(translationService.getValue(StunnerProjectClientConstants.DIAGRAM_SAVE_SUCCESSFUL));
        stunnerEditor.resetContentHash();
        hideLoadingViews();
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

    protected Annotation[] getDockQualifiers() {
        return new Annotation[]{DefinitionManager.DEFAULT_QUALIFIER};
    }

    @Override
    //Override visibility from KieEditor to allow inner class ProjectDiagramEditorCore access
    public abstract String getEditorIdentifier();

    @Override
    protected Promise<Void> makeMenuBar() {
        if (!menuBarInitialized) {
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
                    menuBarInitialized = true;

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
    protected ParameterizedCommand<Path> onSuccess() {
        return (path) -> {
            if (!stunnerEditor.isClosed()) {
                super.onSuccess().execute(path);
            }
        };
    }

    @Override
    protected Caller<? extends SupportsSaveAndRename<ProjectDiagram, org.guvnor.common.services.shared.metadata.model.Metadata>> getSaveAndRenameServiceCaller() {
        return projectDiagramResourceServiceCaller;
    }

    @Override
    protected Supplier<ProjectDiagram> getContentSupplier() {
        return () -> (ProjectDiagram) stunnerEditor.getCanvasHandler().getDiagram();
    }

    @Override
    protected Integer getCurrentContentHash() {
        return stunnerEditor.getCurrentContentHash();
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

    protected void updateTitle(final String title) {
        // Change editor's title.
        this.title = formatTitle(title);
        changeTitleNotification.fire(new ChangeTitleWidgetEvent(this.place,
                                                                this.title));
    }

    /**
     * Format the Diagram title to be displayed on the Editor.
     * This method can be override to customization and the default implementation just return the title from the diagram metadata.
     *
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
        return stunnerEditor.getSession();
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

    protected void destroySession() {
        stunnerEditor.close();
    }

    @SuppressWarnings("unchecked")
    public void addDocumentationPage(final ProjectDiagram diagram) {
        Optional.ofNullable(documentationView.isEnabled())
                .filter(Boolean.TRUE::equals)
                .ifPresent(enabled -> {
                    final String label = translationService.getValue(StunnerWidgetsConstants.Documentation);
                    addPage(new DocumentationPage(documentationView.initialize(diagram),
                                                  label,
                                                  //firing the OnDiagramFocusEvent will force the docks to be minimized
                                                  () -> onDiagramFocusEvent.fire(new OnDiagramFocusEvent(getDockQualifiers())),
                                                  //check the DocumentationPage is active, the index is 2
                                                  () -> Objects.equals(2, kieView.getSelectedTabIndex())));
                });
    }

    public void onError(final ClientRuntimeError error) {
        stunnerEditor.handleError(error);
        hideLoadingViews();
    }

    private void logMenuItemError(final String message) {
        LOGGER.log(Level.WARNING, message);
        hideLoadingViews();
    }

    private void showMessage(String message) {
        if (stunnerEditor.isXmlEditorEnabled()) {
            notification.fire(new NotificationEvent(message));
        } else {
            stunnerEditor.showMessage(message);
        }
    }

    @Override
    public void setOriginalHash(Integer originalHash) {
        super.setOriginalHash(originalHash);
        stunnerEditor.resetContentHash();
    }

    protected boolean hasUnsavedChanges() {
        return super.isDirty(getCurrentContentHash());
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

    public StunnerEditor getStunnerEditor() {
        return stunnerEditor;
    }
}
