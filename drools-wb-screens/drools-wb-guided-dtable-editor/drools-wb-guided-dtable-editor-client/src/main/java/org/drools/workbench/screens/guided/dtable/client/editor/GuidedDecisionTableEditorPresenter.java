/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.editor;

import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.EditMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.InsertMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.RadarMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.ViewMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.type.GuidedDTableResourceType;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.client.annotations.WorkbenchEditor.LockingStrategy.*;

/**
 * Guided Decision Table Editor Presenter
 */
@Dependent
@WorkbenchEditor(identifier = "GuidedDecisionTableEditor", supportedTypes = { GuidedDTableResourceType.class }, lockingStrategy = EDITOR_PROVIDED)
public class GuidedDecisionTableEditorPresenter extends BaseGuidedDecisionTableEditorPresenter {

    @Inject
    public GuidedDecisionTableEditorPresenter( final View view,
                                               final Caller<GuidedDecisionTableEditorService> service,
                                               final Event<NotificationEvent> notification,
                                               final Event<DecisionTableSelectedEvent> decisionTableSelectedEvent,
                                               final GuidedDTableResourceType resourceType,
                                               final EditMenuBuilder editMenuBuilder,
                                               final ViewMenuBuilder viewMenuBuilder,
                                               final InsertMenuBuilder insertMenuBuilder,
                                               final RadarMenuBuilder radarMenuBuilder,
                                               final GuidedDecisionTableModellerView.Presenter modeller,
                                               final SyncBeanManager beanManager,
                                               final PlaceManager placeManager ) {
        super( view,
               service,
               notification,
               decisionTableSelectedEvent,
               resourceType,
               editMenuBuilder,
               viewMenuBuilder,
               insertMenuBuilder,
               radarMenuBuilder,
               modeller,
               beanManager,
               placeManager );
    }

    @Override
    @PostConstruct
    public void init() {
        super.init();
    }

    @Override
    @OnStartup
    public void onStartup( final ObservablePath path,
                           final PlaceRequest placeRequest ) {
        super.onStartup( path,
                         placeRequest );
    }

    @Override
    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @Override
    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.getWidget();
    }

    @Override
    @WorkbenchMenu
    public Menus getMenus() {
        return super.getMenus();
    }

    @Override
    @OnMayClose
    public boolean mayClose() {
        return super.mayClose();
    }

    @Override
    @OnClose
    public void onClose() {
        super.onClose();
    }

    @Override
    protected void onDecisionTableSelected( final @Observes DecisionTableSelectedEvent event ) {
        super.onDecisionTableSelected( event );
    }

    @Override
    protected void activateDocument( final GuidedDecisionTableView.Presenter dtPresenter ) {
        super.activateDocument( dtPresenter );
        dtPresenter.initialiseAnalysis();
    }

    @Override
    public void makeMenuBar() {
        this.menus = fileMenuBuilder
                .addSave( getSaveMenuItem() )
                .addCopy( () -> getActiveDocument().getCurrentPath(),
                          fileNameValidator )
                .addRename( () -> getActiveDocument().getLatestPath(),
                            fileNameValidator )
                .addDelete( () -> getActiveDocument().getLatestPath() )
                .addValidate( () -> onValidate( getActiveDocument() ) )
                .addNewTopLevelMenu( getEditMenuItem() )
                .addNewTopLevelMenu( getViewMenuItem() )
                .addNewTopLevelMenu( getInsertMenuItem() )
                .addNewTopLevelMenu( getRadarMenuItem() )
                .addNewTopLevelMenu( getVersionManagerMenuItem() )
                .build();
    }

    @Override
    public void onOpenDocumentsInEditor( final List<Path> selectedDocumentPaths ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getAvailableDocumentPaths( final Callback<List<Path>> callback ) {
        callback.callback( Collections.<Path>emptyList() );
    }
}