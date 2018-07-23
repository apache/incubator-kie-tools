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

import javax.enterprise.event.Event;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerPresenter;
import org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramLoseFocusEvent;
import org.kie.workbench.common.stunner.project.client.screens.ProjectMessagesListener;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramResourceService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView;

class ProjectDiagramEditorStub extends AbstractProjectDiagramEditor<ClientResourceType> {

    static final String EDITOR_ID = "ProjectDiagramEditorStub";

    public ProjectDiagramEditorStub(final View view,
                                    final PlaceManager placeManager,
                                    final ErrorPopupPresenter errorPopupPresenter,
                                    final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent,
                                    final SavePopUpPresenter savePopUpPresenter,
                                    final ClientResourceType resourceType,
                                    final ClientProjectDiagramService projectDiagramServices,
                                    final ManagedInstance<SessionEditorPresenter<EditorSession>> editorSessionPresenterInstances,
                                    final ManagedInstance<SessionViewerPresenter<ViewerSession>> viewerSessionPresenterInstances,
                                    final AbstractProjectEditorMenuSessionItems menuSessionItems,
                                    final Event<OnDiagramFocusEvent> onDiagramFocusEvent,
                                    final Event<OnDiagramLoseFocusEvent> onDiagramLostFocusEvent,
                                    final ProjectMessagesListener projectMessagesListener,
                                    final DiagramClientErrorHandler diagramClientErrorHandler,
                                    final ClientTranslationService translationService,
                                    final TextEditorView xmlEditorView,
                                    final Caller<ProjectDiagramResourceService> projectDiagramResourceServiceCaller) {
        super(view,
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
    }

    @Override
    protected int getCanvasWidth() {
        return 100;
    }

    @Override
    protected int getCanvasHeight() {
        return 100;
    }

    @Override
    protected String getEditorIdentifier() {
        return EDITOR_ID;
    }
}
