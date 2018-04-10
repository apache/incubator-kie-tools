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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenterFactory;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.ClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ClearSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ClearStatesSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.CopySelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.CutSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.DeleteSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToBpmnSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToJpgSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToPdfSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToPngSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToSvgSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.PasteSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.RedoSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.SessionCommandFactory;
import org.kie.workbench.common.stunner.core.client.session.command.impl.SwitchGridSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.UndoSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ValidateSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.VisitGraphSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.event.OnSessionErrorEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientReadOnlySession;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
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
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.AbstractPlaceEvent;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.events.PlaceHiddenEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static java.util.logging.Level.FINE;

public abstract class AbstractProjectDiagramEditor<R extends ClientResourceType> extends KieEditor<ProjectDiagram> {

    private static Logger LOGGER = Logger.getLogger(AbstractProjectDiagramEditor.class.getName());
    private static final String TITLE_FORMAT_TEMPLATE = "#title.#suffix - #type";

    public interface View extends UberView<AbstractProjectDiagramEditor>,
                                  KieEditorView,
                                  IsWidget {

        void setWidget(IsWidget widget);
    }

    private PlaceManager placeManager;
    private ErrorPopupPresenter errorPopupPresenter;
    private Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent;
    private R resourceType;
    private ClientProjectDiagramService projectDiagramServices;
    private SessionManager sessionManager;
    private SessionPresenterFactory<Diagram, AbstractClientReadOnlySession, AbstractClientFullSession> sessionPresenterFactory;
    private SessionCommandFactory sessionCommandFactory;
    private ProjectDiagramEditorMenuItemsBuilder menuItemsBuilder;
    private ProjectMessagesListener projectMessagesListener;

    private Map<Class, ClientSessionCommand> commands;

    private Event<OnDiagramFocusEvent> onDiagramFocusEvent;
    private Event<OnDiagramLoseFocusEvent> onDiagramLostFocusEvent;
    protected SessionPresenter<AbstractClientFullSession, ?, Diagram> presenter;
    private final DiagramClientErrorHandler diagramClientErrorHandler;
    private final ClientTranslationService translationService;

    private String title = "Project Diagram Editor";

    @Inject
    public AbstractProjectDiagramEditor(final View view,
                                        final PlaceManager placeManager,
                                        final ErrorPopupPresenter errorPopupPresenter,
                                        final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent,
                                        final SavePopUpPresenter savePopUpPresenter,
                                        final R resourceType,
                                        final ClientProjectDiagramService projectDiagramServices,
                                        final SessionManager sessionManager,
                                        final SessionPresenterFactory<Diagram, AbstractClientReadOnlySession, AbstractClientFullSession> sessionPresenterFactory,
                                        final SessionCommandFactory sessionCommandFactory,
                                        final ProjectDiagramEditorMenuItemsBuilder menuItemsBuilder,
                                        final Event<OnDiagramFocusEvent> onDiagramFocusEvent,
                                        final Event<OnDiagramLoseFocusEvent> onDiagramLostFocusEvent,
                                        final ProjectMessagesListener projectMessagesListener,
                                        final DiagramClientErrorHandler diagramClientErrorHandler,
                                        final ClientTranslationService translationService) {
        super(view);
        this.placeManager = placeManager;
        this.errorPopupPresenter = errorPopupPresenter;
        this.changeTitleNotificationEvent = changeTitleNotificationEvent;
        this.savePopUpPresenter = savePopUpPresenter;
        this.resourceType = resourceType;
        this.projectDiagramServices = projectDiagramServices;
        this.sessionManager = sessionManager;
        this.sessionPresenterFactory = sessionPresenterFactory;
        this.sessionCommandFactory = sessionCommandFactory;
        this.menuItemsBuilder = menuItemsBuilder;
        this.projectMessagesListener = projectMessagesListener;
        this.diagramClientErrorHandler = diagramClientErrorHandler;
        this.onDiagramFocusEvent = onDiagramFocusEvent;
        this.onDiagramLostFocusEvent = onDiagramLostFocusEvent;
        this.translationService = translationService;
        this.commands = new HashMap<>();
    }

    protected abstract int getCanvasWidth();

    protected abstract int getCanvasHeight();

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        initializeCommands(commands);
        title = translationService.getValue(StunnerProjectClientConstants.DIAGRAM_EDITOR_DEFAULT_TITLE);
        getView().init(this);
        projectMessagesListener.enable();
    }

    protected void doStartUp(final ObservablePath path,
                             final PlaceRequest place) {
        init(path,
             place,
             resourceType);
    }

    @Override
    protected void loadContent() {
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
    protected void open(final ProjectDiagram diagram) {
        showLoadingViews();
        setOriginalHash(diagram.hashCode());
        final Metadata metadata = diagram.getMetadata();
        sessionManager.getSessionFactory(metadata,
                                         ClientFullSession.class)
                .newSession(metadata,
                            s -> {
                                final AbstractClientFullSession session = (AbstractClientFullSession) s;
                                presenter = sessionPresenterFactory.newPresenterEditor();
                                getView().setWidget(presenter.getView());
                                presenter
                                        .withToolbar(false)
                                        .withPalette(true)
                                        .displayNotifications(type -> true)
                                        .open(diagram,
                                              session,
                                              new SessionPresenter.SessionPresenterCallback<AbstractClientFullSession, Diagram>() {
                                                  @Override
                                                  public void afterSessionOpened() {

                                                  }

                                                  @Override
                                                  public void afterCanvasInitialized() {

                                                  }

                                                  @Override
                                                  public void onSuccess() {
                                                      bindCommands();
                                                      updateTitle(diagram.getMetadata().getTitle());
                                                      hideLoadingViews();
                                                      setOriginalHash(getCurrentDiagramHash());
                                                      onDiagramLoad();
                                                  }

                                                  @Override
                                                  public void onError(final ClientRuntimeError error) {
                                                      onLoadError(error);
                                                  }
                                              });
                            });
    }

    protected void onDiagramLoad() {
        /* Override this method to trigger some action after a Diagram is loaded. */
    }

    @Override
    protected Command onValidate() {
        return () -> validate(() -> {
            onValidationSuccess();
            hideLoadingViews();
        });
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
        getCommand(ValidateSessionCommand.class)
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
        showSavingViews();
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
    }

    @Override
    public RemoteCallback<Path> getSaveSuccessCallback(final int newHash) {
        return (path) -> {
            versionRecordManager.reloadVersions(path);
            setOriginalHash(newHash);
        };
    }

    public void hideDiagramEditorDocks(@Observes PlaceHiddenEvent event) {
        if (verifyEventIdentifier(event)) {
            onDiagramLostFocusEvent.fire(new OnDiagramLoseFocusEvent());
        }
    }

    public void showDiagramEditorDocks(@Observes PlaceGainFocusEvent event) {
        if (verifyEventIdentifier(event)) {
            onDiagramFocusEvent.fire(new OnDiagramFocusEvent());
        }
    }

    @Override
    protected void makeMenuBar() {
        final MenuItem clearItem = menuItemsBuilder.newClearItem(this::menu_clear);
        getCommand(ClearSessionCommand.class).listen(() -> clearItem.setEnabled(getCommand(ClearSessionCommand.class).isEnabled()));
        final MenuItem visitGraphItem = menuItemsBuilder.newVisitGraphItem(this::menu_visitGraph);
        getCommand(VisitGraphSessionCommand.class).listen(() -> visitGraphItem.setEnabled(getCommand(VisitGraphSessionCommand.class).isEnabled()));
        final MenuItem switchGridItem = menuItemsBuilder.newSwitchGridItem(this::menu_switchGrid);
        getCommand(SwitchGridSessionCommand.class).listen(() -> switchGridItem.setEnabled(getCommand(SwitchGridSessionCommand.class).isEnabled()));
        final MenuItem deleteSelectionItem = menuItemsBuilder.newDeleteSelectionItem(this::menu_deleteSelected);
        getCommand(DeleteSelectionSessionCommand.class).listen(() -> deleteSelectionItem.setEnabled(getCommand(DeleteSelectionSessionCommand.class).isEnabled()));
        final MenuItem undoItem = menuItemsBuilder.newUndoItem(this::menu_undo);
        getCommand(UndoSessionCommand.class).listen(() -> undoItem.setEnabled(getCommand(UndoSessionCommand.class).isEnabled()));
        final MenuItem redoItem = menuItemsBuilder.newRedoItem(this::menu_redo);
        getCommand(RedoSessionCommand.class).listen(() -> redoItem.setEnabled(getCommand(RedoSessionCommand.class).isEnabled()));

        final MenuItem validateItem = menuItemsBuilder.newValidateItem(() -> validate(this::hideLoadingViews));
        getCommand(ValidateSessionCommand.class).listen(() -> validateItem.setEnabled(getCommand(ValidateSessionCommand.class).isEnabled()));

        final MenuItem exportsItem = menuItemsBuilder.newExportsItem(this::export_imagePNG,
                                                                     this::export_imageJPG,
                                                                     this::export_imageSVG,
                                                                     this::export_imagePDF,
                                                                     this::export_fileBPMN);
        getCommand(ExportToPngSessionCommand.class).listen(() -> exportsItem.setEnabled(getCommand(ExportToPngSessionCommand.class).isEnabled()));
        getCommand(ExportToJpgSessionCommand.class).listen(() -> exportsItem.setEnabled(getCommand(ExportToJpgSessionCommand.class).isEnabled()));
        getCommand(ExportToSvgSessionCommand.class).listen(() -> exportsItem.setEnabled(getCommand(ExportToSvgSessionCommand.class).isEnabled()));
        getCommand(ExportToPdfSessionCommand.class).listen(() -> exportsItem.setEnabled(getCommand(ExportToPdfSessionCommand.class).isEnabled()));
        getCommand(ExportToBpmnSessionCommand.class).listen(() -> exportsItem.setEnabled(getCommand(ExportToBpmnSessionCommand.class).isEnabled()));

        final MenuItem pasteItem = menuItemsBuilder.newPasteItem(() -> getCommand(PasteSelectionSessionCommand.class).execute());
        getCommand(PasteSelectionSessionCommand.class).listen(() -> pasteItem.setEnabled(getCommand(PasteSelectionSessionCommand.class).isEnabled()));
        final MenuItem copyItem = menuItemsBuilder.newCopyItem(this::menu_copy);
        getCommand(CopySelectionSessionCommand.class).listen(() -> copyItem.setEnabled(getCommand(CopySelectionSessionCommand.class).isEnabled()));
        final MenuItem cutItem = menuItemsBuilder.newCutItem(this::menu_cut);
        getCommand(CutSelectionSessionCommand.class).listen(() -> cutItem.setEnabled(getCommand(CutSelectionSessionCommand.class).isEnabled()));

        deleteSelectionItem.setEnabled(false);
        undoItem.setEnabled(false);
        redoItem.setEnabled(false);
        copyItem.setEnabled(false);
        cutItem.setEnabled(false);
        pasteItem.setEnabled(false);

        // Build the menu.
        fileMenuBuilder
                // Specific Stunner toolbar items.
                .addNewTopLevelMenu(clearItem)
                .addNewTopLevelMenu(visitGraphItem)
                .addNewTopLevelMenu(switchGridItem)
                .addNewTopLevelMenu(deleteSelectionItem)
                .addNewTopLevelMenu(undoItem)
                .addNewTopLevelMenu(redoItem)
                .addNewTopLevelMenu(validateItem)
                .addNewTopLevelMenu(exportsItem)
                .addNewTopLevelMenu(copyItem)
                .addNewTopLevelMenu(cutItem)
                .addNewTopLevelMenu(pasteItem);

        makeAdditionalStunnerMenus(fileMenuBuilder);

        if (menuItemsBuilder.isDevItemsEnabled()) {
            fileMenuBuilder.addNewTopLevelMenu(menuItemsBuilder.newDevItems());
        }

        if (canUpdateProject()) {
            fileMenuBuilder
                    .addSave(versionRecordManager.newSaveMenuItem(this::saveAction))
                    .addCopy(versionRecordManager.getCurrentPath(),
                             assetUpdateValidator)
                    .addRename(versionRecordManager.getPathToLatest(),
                               assetUpdateValidator)
                    .addDelete(versionRecordManager.getPathToLatest(),
                               assetUpdateValidator);
        }

        fileMenuBuilder
                .addNewTopLevelMenu(versionRecordManager.buildMenu())
                .addNewTopLevelMenu(alertsButtonMenuItemBuilder.build());
    }

    @SuppressWarnings("unchecked")
    protected <T> T getCommand(Class<T> key) {
        return (T) commands.get(key);
    }

    private void validate(final Command callback) {
        showLoadingViews();
        getCommand(ValidateSessionCommand.class).execute(new ClientSessionCommand.Callback<Collection<DiagramElementViolation<RuleViolation>>>() {
            @Override
            public void onSuccess() {
                callback.execute();
            }

            @Override
            public void onError(final Collection<DiagramElementViolation<RuleViolation>> violations) {
                onValidationFailed(violations);
            }
        });
    }

    private void menu_clear() {
        getCommand(ClearSessionCommand.class).execute();
    }

    private void menu_visitGraph() {
        getCommand(VisitGraphSessionCommand.class).execute();
    }

    private void menu_switchGrid() {
        getCommand(SwitchGridSessionCommand.class).execute();
    }

    private void menu_deleteSelected() {
        getCommand(DeleteSelectionSessionCommand.class).execute();
    }

    private void menu_undo() {
        getCommand(UndoSessionCommand.class).execute();
    }

    private void menu_redo() {
        getCommand(RedoSessionCommand.class).execute();
    }

    private void export_imagePNG() {
        getCommand(ExportToPngSessionCommand.class).execute();
    }

    private void export_imageJPG() {
        getCommand(ExportToJpgSessionCommand.class).execute();
    }

    private void export_imagePDF() {
        getCommand(ExportToPdfSessionCommand.class).execute();
    }

    private void export_imageSVG() {
        getCommand(ExportToSvgSessionCommand.class).execute();
    }

    private void export_fileBPMN() {
        getCommand(ExportToBpmnSessionCommand.class).execute();
    }

    private void menu_copy() {
        getCommand(CopySelectionSessionCommand.class).execute();
    }

    private void menu_cut() {
        getCommand(CutSelectionSessionCommand.class).execute();
    }

    protected void doOpen() {
        if (null != getSession()) {
            sessionManager.resume(getSession());
        }
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

    protected void doClose() {
        destroySession();
    }

    protected void doFocus() {
        log(FINE,
            "Focusing Stunner Project Diagram Editor...");
        if (null != getSession() && !isSameSession(sessionManager.getCurrentSession())) {
            sessionManager.open(getSession());
        } else if (null != getSession()) {
            log(FINE,
                "Session already active, no action.");
        }
    }

    protected void doLostFocus() {
    }

    void onSessionErrorEvent(final @Observes OnSessionErrorEvent errorEvent) {
        if (isSameSession(errorEvent.getSession())) {
            executeWithConfirm(translationService.getValue(StunnerProjectClientConstants.ON_ERROR_CONFIRM_UNDO_LAST_ACTION,
                                                           errorEvent.getError()),
                               this::menu_undo);
        }
    }

    private boolean isSameSession(final ClientSession other) {
        return null != other && null != getSession() && other.equals(getSession());
    }

    protected abstract String getEditorIdentifier();

    public String getTitleText() {
        return title;
    }

    protected Menus getMenus() {
        if (menus == null) {
            makeMenuBar();
        }
        return menus;
    }

    @Override
    protected void onSave() {
        if (hasUnsavedChanges()) {
            super.onSave();
        } else {
            final String message = CommonConstants.INSTANCE.NoChangesSinceLastSave();
            log(Level.INFO,
                message);
            presenter.getView().showMessage(message);
        }
    }

    @SuppressWarnings("unchecked")
    void bindCommands() {
        commands.values().stream().forEach(command -> command.bind(getSession()));
    }

    void unbindCommands() {
        commands.values().stream().forEach(ClientSessionCommand::unbind);
    }

    private void destroySession() {
        unbindCommands();
        if (Objects.nonNull(presenter)) {
            presenter.destroy();
        }
    }

    private void updateTitle(final String title) {
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

    private AbstractClientFullSession getSession() {
        return null != presenter ? presenter.getInstance() : null;
    }

    @SuppressWarnings("unchecked")
    protected int getCurrentDiagramHash() {
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
    }

    protected CanvasHandler getCanvasHandler() {
        return null != sessionManager.getCurrentSession() ? sessionManager.getCurrentSession().getCanvasHandler() : null;
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
        presenter.getView().showMessage(message);
        setOriginalHash(getCurrentDiagramHash());
    }

    protected void onSaveError(final ClientRuntimeError error) {
        showError(error);
    }

    private void onValidationSuccess() {
        log(Level.INFO,
            "Validation SUCCESS.");
    }

    private void onValidationFailed(final Collection<DiagramElementViolation<RuleViolation>> violations) {
        log(Level.WARNING,
            "Validation FAILED [violations=" + violations.toString() + "]");
        hideLoadingViews();
    }

    private void onLoadError(final ClientRuntimeError error) {
        showError(error);

        //close editor in case of error when opening the editor
        placeManager.forceClosePlace(new PathPlaceRequest(versionRecordManager.getCurrentPath(),
                                                          getEditorIdentifier()));
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

    private boolean verifyEventIdentifier(AbstractPlaceEvent event) {
        return (Objects.equals(getEditorIdentifier(),
                               event.getPlace().getIdentifier()) &&
                Objects.equals(place,
                               event.getPlace()));
    }

    protected ClientTranslationService getTranslationService() {
        return translationService;
    }

    protected void makeAdditionalStunnerMenus(final FileMenuBuilder fileMenuBuilder) {
    }

    protected void initializeCommands(final Map<Class, ClientSessionCommand> commands) {
        commands.put(ClearStatesSessionCommand.class, sessionCommandFactory.newClearStatesCommand());
        commands.put(VisitGraphSessionCommand.class, sessionCommandFactory.newVisitGraphCommand());
        commands.put(SwitchGridSessionCommand.class, sessionCommandFactory.newSwitchGridCommand());
        commands.put(ClearSessionCommand.class, sessionCommandFactory.newClearCommand());
        commands.put(DeleteSelectionSessionCommand.class, sessionCommandFactory.newDeleteSelectedElementsCommand());
        commands.put(UndoSessionCommand.class, sessionCommandFactory.newUndoCommand());
        commands.put(RedoSessionCommand.class, sessionCommandFactory.newRedoCommand());
        commands.put(ValidateSessionCommand.class, sessionCommandFactory.newValidateCommand());
        commands.put(ExportToPngSessionCommand.class, sessionCommandFactory.newExportToPngSessionCommand());
        commands.put(ExportToJpgSessionCommand.class, sessionCommandFactory.newExportToJpgSessionCommand());
        commands.put(ExportToSvgSessionCommand.class, sessionCommandFactory.newExportToSvgSessionCommand());
        commands.put(ExportToPdfSessionCommand.class, sessionCommandFactory.newExportToPdfSessionCommand());
        commands.put(ExportToBpmnSessionCommand.class, sessionCommandFactory.newExportToBpmnSessionCommand());
        commands.put(CopySelectionSessionCommand.class, sessionCommandFactory.newCopySelectionCommand());
        commands.put(PasteSelectionSessionCommand.class, sessionCommandFactory.newPasteSelectionCommand());
        commands.put(CutSelectionSessionCommand.class, sessionCommandFactory.newCutSelectionCommand());
    }

    public SessionPresenter<AbstractClientFullSession, ?, Diagram> getSessionPresenter() {
        return presenter;
    }

    void setSessionPresenter(final SessionPresenter<AbstractClientFullSession, ?, Diagram> presenter) {
        this.presenter = presenter;
    }
}
