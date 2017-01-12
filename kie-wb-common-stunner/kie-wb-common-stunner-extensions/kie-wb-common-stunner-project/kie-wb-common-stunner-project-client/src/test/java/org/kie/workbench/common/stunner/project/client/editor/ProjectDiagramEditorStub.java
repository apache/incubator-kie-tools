/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.project.client.editor;

import org.kie.workbench.common.stunner.client.widgets.palette.bs3.factory.BS3PaletteFactory;
import org.kie.workbench.common.stunner.client.widgets.session.presenter.impl.AbstractClientSessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.session.view.ScreenErrorView;
import org.kie.workbench.common.stunner.core.client.session.command.impl.SessionCommandFactory;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSessionManager;
import org.kie.workbench.common.stunner.core.client.util.ClientSessionUtils;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;

import javax.enterprise.event.Event;

class ProjectDiagramEditorStub extends AbstractProjectDiagramEditor<ClientResourceType> {

    public ProjectDiagramEditorStub( View view,
                                     PlaceManager placeManager,
                                     ErrorPopupPresenter errorPopupPresenter,
                                     Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent,
                                     SavePopUpPresenter savePopUpPresenter,
                                     ClientResourceType resourceType,
                                     ClientProjectDiagramService projectDiagramServices,
                                     AbstractClientSessionManager clientSessionManager,
                                     AbstractClientSessionPresenter clientSessionPresenter,
                                     ScreenErrorView editorErrorView,
                                     BS3PaletteFactory paletteFactory,
                                     ClientSessionUtils sessionUtils,
                                     SessionCommandFactory sessionCommandFactory,
                                     ProjectDiagramEditorMenuItemsBuilder menuItemsBuilder ) {
        super( view, placeManager, errorPopupPresenter, changeTitleNotificationEvent, savePopUpPresenter,
                resourceType, projectDiagramServices, clientSessionManager, clientSessionPresenter, editorErrorView,
                paletteFactory, sessionUtils, sessionCommandFactory, menuItemsBuilder );
    }

    @Override
    protected int getCanvasWidth() {
        return 100;
    }

    @Override
    protected int getCanvasHeight() {
        return 100;
    }
}
