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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenterFactory;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.ClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ClearSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ClearStatesSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.DeleteSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToJpgSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToPdfSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToPngSessionCommand;
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
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.util.HashUtil;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.kie.workbench.common.stunner.core.validation.impl.ValidationUtils;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramLoseFocusEvent;
import org.kie.workbench.common.stunner.project.client.screens.ProjectMessagesListener;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;
import org.uberfire.backend.vfs.ObservablePath;
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
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static java.util.logging.Level.FINE;

// TODO: i18n.
public abstract class AbstractProjectDiagramEditor<R extends ClientResourceType> extends KieEditor {

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
    private ProjectDiagramEditorMenuItemsBuilder menuItemsBuilder;
    private ProjectMessagesListener projectMessagesListener;

    private ClearStatesSessionCommand sessionClearStatesCommand;
    private VisitGraphSessionCommand sessionVisitGraphCommand;
    private SwitchGridSessionCommand sessionSwitchGridCommand;
    private ClearSessionCommand sessionClearCommand;
    private DeleteSelectionSessionCommand sessionDeleteSelectionCommand;
    private UndoSessionCommand sessionUndoCommand;
    private RedoSessionCommand sessionRedoCommand;
    private ValidateSessionCommand sessionValidateCommand;
    private ExportToPngSessionCommand sessionExportImagePNGCommand;
    private ExportToJpgSessionCommand sessionExportImageJPGCommand;
    private ExportToPdfSessionCommand sessionExportPDFCommand;
    private Event<OnDiagramFocusEvent> onDiagramFocusEvent;
    private Event<OnDiagramLoseFocusEvent> onDiagramLostFocusEvent;
    protected SessionPresenter<AbstractClientFullSession, ?, Diagram> presenter;
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
                                        final ProjectMessagesListener projectMessagesListener) {
        super(view);
        this.placeManager = placeManager;
        this.errorPopupPresenter = errorPopupPresenter;
        this.changeTitleNotificationEvent = changeTitleNotificationEvent;
        this.savePopUpPresenter = savePopUpPresenter;
        this.resourceType = resourceType;
        this.projectDiagramServices = projectDiagramServices;
        this.sessionManager = sessionManager;
        this.sessionPresenterFactory = sessionPresenterFactory;
        this.menuItemsBuilder = menuItemsBuilder;
        this.projectMessagesListener = projectMessagesListener;

        this.sessionClearStatesCommand = sessionCommandFactory.newClearStatesCommand();
        this.sessionVisitGraphCommand = sessionCommandFactory.newVisitGraphCommand();
        this.sessionSwitchGridCommand = sessionCommandFactory.newSwitchGridCommand();
        this.sessionClearCommand = sessionCommandFactory.newClearCommand();
        this.sessionDeleteSelectionCommand = sessionCommandFactory.newDeleteSelectedElementsCommand();
        this.sessionUndoCommand = sessionCommandFactory.newUndoCommand();
        this.sessionRedoCommand = sessionCommandFactory.newRedoCommand();
        this.sessionValidateCommand = sessionCommandFactory.newValidateCommand();
        this.sessionExportImagePNGCommand = sessionCommandFactory.newExportToPngSessionCommand();
        this.sessionExportImageJPGCommand = sessionCommandFactory.newExportToJpgSessionCommand();
        this.sessionExportPDFCommand = sessionCommandFactory.newExportToPdfSessionCommand();
        this.onDiagramFocusEvent = onDiagramFocusEvent;
        this.onDiagramLostFocusEvent = onDiagramLostFocusEvent;
    }

    protected abstract int getCanvasWidth();

    protected abstract int getCanvasHeight();

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
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
                                                 showError(error);
                                             }
                                         });
    }

    protected void open(final ProjectDiagram diagram) {
        showLoadingViews();
        final AbstractClientFullSession session = newSession(diagram);
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
                          }

                          @Override
                          public void onError(final ClientRuntimeError error) {
                              showError(error);
                          }
                      });
    }

    private AbstractClientFullSession newSession(final Diagram diagram) {
        setOriginalHash(diagram.hashCode());
        return (AbstractClientFullSession) sessionManager.getSessionFactory(diagram,
                                                                            ClientFullSession.class).newSession();
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
        sessionValidateCommand
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
        showLoadingViews();
        // Update diagram's image data as thumbnail.
        final CanvasHandler canvasHandler = getSession().getCanvasHandler();
        final Diagram diagram = canvasHandler.getDiagram();
        // Perform update operation remote call.
        projectDiagramServices.saveOrUpdate(versionRecordManager.getCurrentPath(),
                                            getDiagram(),
                                            metadata,
                                            commitMessage,
                                            new ServiceCallback<ProjectDiagram>() {
                                                @Override
                                                public void onSuccess(final ProjectDiagram item) {
                                                    getSaveSuccessCallback(item.hashCode());
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
    protected void makeMenuBar() {
        // TODO: fix - menu items not getting disabled/enabled?
        final MenuItem clearItem = menuItemsBuilder.newClearItem(AbstractProjectDiagramEditor.this::menu_clear);
        sessionClearCommand.listen(() -> clearItem.setEnabled(sessionClearCommand.isEnabled()));
        final MenuItem clearStatesItem = menuItemsBuilder.newClearSelectionItem(AbstractProjectDiagramEditor.this::menu_clearStates);
        sessionClearStatesCommand.listen(() -> clearStatesItem.setEnabled(sessionClearStatesCommand.isEnabled()));
        final MenuItem visitGraphItem = menuItemsBuilder.newVisitGraphItem(AbstractProjectDiagramEditor.this::menu_visitGraph);
        sessionVisitGraphCommand.listen(() -> visitGraphItem.setEnabled(sessionVisitGraphCommand.isEnabled()));
        final MenuItem switchGridItem = menuItemsBuilder.newSwitchGridItem(AbstractProjectDiagramEditor.this::menu_switchGrid);
        sessionSwitchGridCommand.listen(() -> switchGridItem.setEnabled(sessionSwitchGridCommand.isEnabled()));
        final MenuItem deleteSelectionItem = menuItemsBuilder.newDeleteSelectionItem(AbstractProjectDiagramEditor.this::menu_deleteSelected);
        sessionDeleteSelectionCommand.listen(() -> deleteSelectionItem.setEnabled(sessionDeleteSelectionCommand.isEnabled()));
        final MenuItem undoItem = menuItemsBuilder.newUndoItem(AbstractProjectDiagramEditor.this::menu_undo);
        sessionUndoCommand.listen(() -> undoItem.setEnabled(sessionUndoCommand.isEnabled()));
        final MenuItem redoItem = menuItemsBuilder.newRedoItem(AbstractProjectDiagramEditor.this::menu_redo);
        sessionRedoCommand.listen(() -> redoItem.setEnabled(sessionRedoCommand.isEnabled()));
        final MenuItem validateItem = menuItemsBuilder.newValidateItem(AbstractProjectDiagramEditor.this::menu_validate);
        sessionValidateCommand.listen(() -> validateItem.setEnabled(sessionValidateCommand.isEnabled()));
        final MenuItem exportsItem = menuItemsBuilder.newExportsItem(AbstractProjectDiagramEditor.this::export_imagePNG,
                                                                     AbstractProjectDiagramEditor.this::export_imageJPG,
                                                                     AbstractProjectDiagramEditor.this::export_imagePDF);
        sessionExportImagePNGCommand.listen(() -> exportsItem.setEnabled(sessionExportImagePNGCommand.isEnabled()));
        sessionExportImageJPGCommand.listen(() -> exportsItem.setEnabled(sessionExportImageJPGCommand.isEnabled()));
        sessionExportPDFCommand.listen(() -> exportsItem.setEnabled(sessionExportPDFCommand.isEnabled()));

        // Build the menu.
        fileMenuBuilder
                // Specific Stunner toolbar items.
                .addNewTopLevelMenu(clearItem)
                .addNewTopLevelMenu(clearStatesItem)
                .addNewTopLevelMenu(visitGraphItem)
                .addNewTopLevelMenu(switchGridItem)
                .addNewTopLevelMenu(deleteSelectionItem)
                .addNewTopLevelMenu(undoItem)
                .addNewTopLevelMenu(redoItem)
                .addNewTopLevelMenu(validateItem)
                .addNewTopLevelMenu(exportsItem);
        if (menuItemsBuilder.isDevItemsEnabled()) {
            fileMenuBuilder.addNewTopLevelMenu(menuItemsBuilder.newDevItems());
        }

        if (canUpdateProject()) {
            fileMenuBuilder
                    .addSave(versionRecordManager.newSaveMenuItem(() -> onSave()))
                    .addCopy(versionRecordManager.getCurrentPath(),
                             fileNameValidator)
                    .addRename(versionRecordManager.getPathToLatest(),
                               fileNameValidator)
                    .addDelete(versionRecordManager.getPathToLatest());
        }

        fileMenuBuilder
                .addNewTopLevelMenu(versionRecordManager.buildMenu());
    }

    private void validate(final Command callback) {
        showLoadingViews();
        sessionValidateCommand.execute(new ClientSessionCommand.Callback<Collection<DiagramElementViolation<RuleViolation>>>() {
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
        sessionClearCommand.execute();
    }

    private void menu_clearStates() {
        sessionClearStatesCommand.execute();
    }

    private void menu_visitGraph() {
        sessionVisitGraphCommand.execute();
    }

    private void menu_switchGrid() {
        sessionSwitchGridCommand.execute();
    }

    private void menu_deleteSelected() {
        sessionDeleteSelectionCommand.execute();
    }

    private void menu_undo() {
        sessionUndoCommand.execute();
    }

    private void menu_redo() {
        sessionRedoCommand.execute();
    }

    private void export_imagePNG() {
        sessionExportImagePNGCommand.execute();
    }

    private void export_imageJPG() {
        sessionExportImageJPGCommand.execute();
    }

    private void export_imagePDF() {
        sessionExportPDFCommand.execute();
    }

    private void menu_validate() {
        this.validate(() -> hideLoadingViews());
    }

    protected void doOpen() {
        if (null != getSession()) {
            sessionManager.resume(getSession());
        }
    }

    protected void showLoadingViews() {
        getView().showLoading();
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
            executeWithConfirm("An error happened [" + errorEvent.getError() + "]. Do you want" +
                                       "to undo the last action?",
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

    protected boolean _onMayClose() {
        return super.mayClose(getCurrentDiagramHash());
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

    void bindCommands() {
        this.sessionClearStatesCommand.bind(getSession());
        this.sessionVisitGraphCommand.bind(getSession());
        this.sessionSwitchGridCommand.bind(getSession());
        this.sessionClearCommand.bind(getSession());
        this.sessionDeleteSelectionCommand.bind(getSession());
        this.sessionUndoCommand.bind(getSession());
        this.sessionRedoCommand.bind(getSession());
        this.sessionValidateCommand.bind(getSession());
        this.sessionExportImagePNGCommand.bind(getSession());
        this.sessionExportImageJPGCommand.bind(getSession());
        this.sessionExportPDFCommand.bind(getSession());
    }

    void unbindCommands() {
        this.sessionClearStatesCommand.unbind();
        this.sessionVisitGraphCommand.unbind();
        this.sessionSwitchGridCommand.unbind();
        this.sessionClearCommand.unbind();
        this.sessionDeleteSelectionCommand.unbind();
        this.sessionUndoCommand.unbind();
        this.sessionRedoCommand.unbind();
        this.sessionValidateCommand.unbind();
        this.sessionExportImagePNGCommand.unbind();
        this.sessionExportImageJPGCommand.unbind();
        this.sessionExportPDFCommand.unbind();
    }

    private void pauseSession() {
        sessionManager.pause();
    }

    private void destroySession() {
        unbindCommands();
        presenter.clear();
        presenter.destroy();
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
        if(Objects.isNull(resourceType)){
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

    private void onSaveSuccess() {
        final String message = "Diagram saved successfully.";
        log(Level.INFO,
            message);
        presenter.getView().showMessage(message);
        setOriginalHash(getCurrentDiagramHash());
    }

    private void onSaveError(final ClientRuntimeError error) {
        showError(error.toString());
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

    private void showError(final ClientRuntimeError error) {
        showError(error.toString());
    }

    private void showError(final String message) {
        log(Level.SEVERE,
            message);
        errorPopupPresenter.showMessage(message);
        hideLoadingViews();
    }

    private void log(final Level level,
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
}
