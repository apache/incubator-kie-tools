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
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;
import org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditorMenuSessionItems;
import org.kie.workbench.common.stunner.kogito.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.kogito.client.editor.event.OnDiagramLoseFocusEvent;
import org.kie.workbench.common.stunner.project.client.screens.ProjectMessagesListener;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramResourceService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView;
import org.uberfire.workbench.events.NotificationEvent;

class ProjectDiagramEditorStub extends AbstractProjectDiagramEditor<ClientResourceType> {

    static final String EDITOR_ID = "ProjectDiagramEditorStub";

    public ProjectDiagramEditorStub(final View view,
                                    final TextEditorView xmlEditorView,
                                    final ManagedInstance<SessionEditorPresenter<EditorSession>> editorSessionPresenterInstances,
                                    final ManagedInstance<SessionViewerPresenter<ViewerSession>> viewerSessionPresenterInstances,
                                    final Event<OnDiagramFocusEvent> onDiagramFocusEvent,
                                    final Event<OnDiagramLoseFocusEvent> onDiagramLostFocusEvent,
                                    final Event<NotificationEvent> notificationEvent,
                                    final ErrorPopupPresenter errorPopupPresenter,
                                    final DiagramClientErrorHandler diagramClientErrorHandler,
                                    final DocumentationView documentationView,
                                    final ClientResourceType resourceType,
                                    final AbstractDiagramEditorMenuSessionItems menuSessionItems,
                                    final ProjectMessagesListener projectMessagesListener,
                                    final ClientTranslationService translationService,
                                    final ClientProjectDiagramService projectDiagramServices,
                                    final Caller<ProjectDiagramResourceService> projectDiagramResourceServiceCaller,
                                    final PlaceManager placeManager,
                                    final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent,
                                    final SavePopUpPresenter savePopUpPresenter) {
        super(view,
              xmlEditorView,
              editorSessionPresenterInstances,
              viewerSessionPresenterInstances,
              onDiagramFocusEvent,
              onDiagramLostFocusEvent,
              notificationEvent,
              errorPopupPresenter,
              diagramClientErrorHandler,
              documentationView,
              resourceType,
              menuSessionItems,
              projectMessagesListener,
              translationService,
              projectDiagramServices,
              projectDiagramResourceServiceCaller);
        this.placeManager = placeManager;
        this.changeTitleNotification = changeTitleNotificationEvent;
        this.savePopUpPresenter = savePopUpPresenter;
    }

    @Override
    public String getEditorIdentifier() {
        return EDITOR_ID;
    }
}
