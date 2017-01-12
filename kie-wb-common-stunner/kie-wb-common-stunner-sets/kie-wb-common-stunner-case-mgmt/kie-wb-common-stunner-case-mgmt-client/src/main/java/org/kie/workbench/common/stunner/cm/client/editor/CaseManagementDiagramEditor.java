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

package org.kie.workbench.common.stunner.cm.client.editor;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.client.widgets.palette.bs3.factory.BS3PaletteFactory;
import org.kie.workbench.common.stunner.client.widgets.session.presenter.impl.AbstractClientSessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.session.view.ScreenErrorView;
import org.kie.workbench.common.stunner.cm.client.type.CaseManagementDiagramResourceType;
import org.kie.workbench.common.stunner.cm.factory.CaseManagementGraphFactory;
import org.kie.workbench.common.stunner.core.client.session.command.impl.SessionCommandFactory;
import org.kie.workbench.common.stunner.core.client.util.ClientSessionUtils;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectDiagramEditor;
import org.kie.workbench.common.stunner.project.client.editor.ProjectDiagramEditorMenuItemsBuilder;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.kie.workbench.common.stunner.project.client.session.impl.ClientProjectSessionManager;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.*;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.lifecycle.*;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Dependent
@WorkbenchEditor( identifier = CaseManagementDiagramEditor.EDITOR_ID, supportedTypes = { CaseManagementDiagramResourceType.class } )
public class CaseManagementDiagramEditor extends AbstractProjectDiagramEditor<CaseManagementDiagramResourceType> {

    public static final String EDITOR_ID = "CaseManagementDiagramEditor";

    @Inject
    public CaseManagementDiagramEditor( final View view,
                                        final PlaceManager placeManager,
                                        final ErrorPopupPresenter errorPopupPresenter,
                                        final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent,
                                        final SavePopUpPresenter savePopUpPresenter,
                                        final CaseManagementDiagramResourceType resourceType,
                                        final ClientProjectDiagramService projectDiagramServices,
                                        final ClientProjectSessionManager clientSessionManager,
                                        final AbstractClientSessionPresenter clientSessionPresenter,
                                        final ScreenErrorView editorErrorView,
                                        final BS3PaletteFactory paletteFactory,
                                        final ClientSessionUtils sessionUtils,
                                        final SessionCommandFactory sessionCommandFactory,
                                        final ProjectDiagramEditorMenuItemsBuilder menuItemsBuilder ) {
        super( view,
                placeManager,
                errorPopupPresenter,
                changeTitleNotificationEvent,
                savePopUpPresenter,
                resourceType,
                projectDiagramServices,
                clientSessionManager,
                clientSessionPresenter,
                editorErrorView,
                paletteFactory,
                sessionUtils,
                sessionCommandFactory,
                menuItemsBuilder );
    }

    @OnStartup
    public void onStartup( final ObservablePath path,
                           final PlaceRequest place ) {
        super.doStartUp( path,
                place );
    }

    @Override
    protected int getCanvasWidth() {
        return ( int ) CaseManagementGraphFactory.GRAPH_DEFAULT_WIDTH;
    }

    @Override
    protected int getCanvasHeight() {
        return ( int ) CaseManagementGraphFactory.GRAPH_DEFAULT_HEIGHT;
    }

    @OnOpen
    public void onOpen() {
        super.doOpen();
    }

    @OnClose
    public void onClose() {
        super.doClose();
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
    public Menus getMenus() {
        return super.getMenus();
    }

    @WorkbenchPartView
    public Widget getWidget() {
        return getView().asWidget();
    }

    @OnMayClose
    public boolean onMayClose() {
        return super.mayClose( getCurrentDiagramHash() );
    }
}
