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

package org.kie.workbench.common.stunner.cm.project.client.editor;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.client.BPMNShapeSet;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerPresenter;
import org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet;
import org.kie.workbench.common.stunner.cm.client.CaseManagementShapeSet;
import org.kie.workbench.common.stunner.cm.project.client.resources.i18n.CaseManagementProjectClientConstants;
import org.kie.workbench.common.stunner.cm.project.client.type.CaseManagementDiagramResourceType;
import org.kie.workbench.common.stunner.cm.project.service.CaseManagementSwitchViewService;
import org.kie.workbench.common.stunner.core.client.annotation.DiagramEditor;
import org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectDiagramEditor;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramLoseFocusEvent;
import org.kie.workbench.common.stunner.project.client.screens.ProjectMessagesListener;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramResourceService;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnLostFocus;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@DiagramEditor
@WorkbenchEditor(identifier = CaseManagementDiagramEditor.EDITOR_ID, supportedTypes = {CaseManagementDiagramResourceType.class})
public class CaseManagementDiagramEditor extends AbstractProjectDiagramEditor<CaseManagementDiagramResourceType> {

    private static final Logger LOGGER = Logger.getLogger(CaseManagementDiagramEditor.class.getName());

    public static final String EDITOR_ID = "CaseManagementDiagramEditor";

    private Caller<CaseManagementSwitchViewService> caseManagementSwitchViewService;

    private AtomicBoolean switchedToProcess;

    private SwitchViewControl switchViewControl;

    @Inject
    public CaseManagementDiagramEditor(final View view,
                                       final DocumentationView documentationView,
                                       final PlaceManager placeManager,
                                       final ErrorPopupPresenter errorPopupPresenter,
                                       final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent,
                                       final SavePopUpPresenter savePopUpPresenter,
                                       final CaseManagementDiagramResourceType resourceType,
                                       final ClientProjectDiagramService projectDiagramServices,
                                       final ManagedInstance<SessionEditorPresenter<EditorSession>> editorSessionPresenterInstances,
                                       final ManagedInstance<SessionViewerPresenter<ViewerSession>> viewerSessionPresenterInstances,
                                       final CaseManagementProjectEditorMenuSessionItems menuSessionItems,
                                       final Event<OnDiagramFocusEvent> onDiagramFocusEvent,
                                       final Event<OnDiagramLoseFocusEvent> onDiagramLostFocusEvent,
                                       final ProjectMessagesListener projectMessagesListener,
                                       final DiagramClientErrorHandler diagramClientErrorHandler,
                                       final ClientTranslationService translationService,
                                       final TextEditorView xmlEditorView,
                                       final Caller<ProjectDiagramResourceService> projectDiagramResourceServiceCaller,
                                       final Caller<CaseManagementSwitchViewService> caseManagementSwitchViewService) {
        super(view,
              documentationView,
              placeManager,
              errorPopupPresenter,
              changeTitleNotificationEvent,
              savePopUpPresenter,
              resourceType,
              projectDiagramServices,
              editorSessionPresenterInstances,
              viewerSessionPresenterInstances,
              menuSessionItems,
              onDiagramFocusEvent,
              onDiagramLostFocusEvent,
              projectMessagesListener,
              diagramClientErrorHandler,
              translationService,
              xmlEditorView,
              projectDiagramResourceServiceCaller);

        this.caseManagementSwitchViewService = caseManagementSwitchViewService;
        this.switchedToProcess = new AtomicBoolean();
        this.switchViewControl = initSwitchViewControl();
    }

    private SwitchViewControl initSwitchViewControl() {
        final Runnable caseViewSwitchHandler = () -> {
            final boolean toProcess = CaseManagementDiagramEditor.this.switchedToProcess.getAndSet(false);
            if (toProcess) {
                updateSessionEditorPresenter(CaseManagementDefinitionSet.class.getName(),
                                             CaseManagementShapeSet.class.getName());
            }
        };

        final Runnable processViewSwitchHandler = () -> {
            final boolean toProcess = CaseManagementDiagramEditor.this.switchedToProcess.getAndSet(true);
            if (!toProcess) {
                updateSessionEditorPresenter(BPMNDefinitionSet.class.getName(),
                                             BPMNShapeSet.class.getName());
            }
        };

        final String caseViewTooltip = this.getTranslationService()
                .getValue(CaseManagementProjectClientConstants.CaseManagementEditorCaseViewTooltip);
        final String processViewTooltip = this.getTranslationService()
                .getValue(CaseManagementProjectClientConstants.CaseManagementEditorProcessViewTooltip);

        return new SwitchViewControl("COLUMNS", caseViewTooltip, caseViewSwitchHandler,
                                     "SITEMAP", processViewTooltip, processViewSwitchHandler);
    }

    @Override
    public void init() {
        super.init();

        this.addTabBarWidget(switchViewControl);
    }

    private void updateSessionEditorPresenter(final String defSetId, final String shapeSetId) {
        final SessionPresenter<? extends ClientSession, ?, Diagram> presenter = this.getSessionPresenter();
        if (presenter != null) {
            final Diagram d = presenter.getHandler().getDiagram();
            CaseManagementDiagramEditor.this.onSwitch(d, defSetId, shapeSetId);
        }
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        super.doStartUp(path,
                        place);
    }

    @Override
    protected String getEditorIdentifier() {
        return CaseManagementDiagramEditor.EDITOR_ID;
    }

    @OnOpen
    public void onOpen() {
        super.doOpen();
    }

    @OnClose
    @Override
    public void onClose() {
        super.doClose();
        super.onClose();
    }

    @OnFocus
    public void onFocus() {
        super.doFocus();
    }

    @OnLostFocus
    public void onLostFocus() {
        super.doLostFocus();
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        super.getMenus(menusConsumer);
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.getWidget();
    }

    @OnMayClose
    public boolean onMayClose() {
        return super.mayClose(getCurrentDiagramHash());
    }

    @Override
    protected SessionEditorPresenter<EditorSession> newSessionEditorPresenter() {
        return (SessionEditorPresenter<EditorSession>) super.newSessionEditorPresenter()
                .displayNotifications(type -> false);
    }

    @Override
    protected void loadContent() {
        this.switchedToProcess.getAndSet(false);
        this.switchViewControl.caseViewButton.click();
        super.loadContent();
    }

    void reopenSession(final ProjectDiagram diagram) {
        final Integer originalHash = this.originalHash;
        final boolean unsaved = this.hasUnsavedChanges();

        this.getMenuSessionItems().getCommands().getCommands().clearCommands();
        this.destroySession();

        this.open(diagram, Optional.of(new SessionPresenter.SessionPresenterCallback<Diagram>() {
            @Override
            public void afterSessionOpened() {

            }

            @Override
            public void afterCanvasInitialized() {

            }

            @Override
            public void onSuccess() {
                if (unsaved) {
                    CaseManagementDiagramEditor.this.setOriginalHash(originalHash);
                }
            }

            @Override
            public void onError(ClientRuntimeError error) {

            }
        }));
    }

    protected void onSwitch(final Diagram diagram, final String defSetId, final String shapeDefId) {
        this.showLoadingViews();

        caseManagementSwitchViewService.call(new RemoteCallback<Optional<ProjectDiagram>>() {
            @Override
            public void callback(Optional<ProjectDiagram> diagram) {
                diagram.ifPresent(d -> {
                    CaseManagementDiagramEditor.this.reopenSession(d);
                    CaseManagementDiagramEditor.this.hideLoadingViews();
                });
            }
        }).switchView(diagram, defSetId, shapeDefId);
    }
}
