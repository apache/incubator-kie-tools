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

import java.util.List;
import java.util.Set;
import javax.enterprise.event.Event;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.EditMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.InsertMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.RadarMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.ViewMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorContent;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;
import org.kie.workbench.common.widgets.metadata.client.KieMultipleDocumentEditor;
import org.kie.workbench.common.widgets.metadata.client.KieMultipleDocumentEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.menu.RegisteredDocumentsMenuBuilder;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;

/**
 * Guided Decision Table Editor Presenter
 */
public abstract class BaseGuidedDecisionTableEditorPresenter extends KieMultipleDocumentEditor<GuidedDecisionTableView.Presenter> {

    public interface View extends RequiresResize,
                                  ProvidesResize,
                                  KieEditorView,
                                  IsWidget {

        void setModellerView( final GuidedDecisionTableModellerView view );

    }

    protected View view;
    protected Caller<GuidedDecisionTableEditorService> service;
    protected Event<NotificationEvent> notification;
    protected Event<DecisionTableSelectedEvent> decisionTableSelectedEvent;
    protected ClientResourceType resourceType;
    protected EditMenuBuilder editMenuBuilder;
    protected ViewMenuBuilder viewMenuBuilder;
    protected InsertMenuBuilder insertMenuBuilder;
    protected RadarMenuBuilder radarMenuBuilder;
    protected GuidedDecisionTableModellerView.Presenter modeller;

    protected ObservablePath editorPath;
    protected PlaceRequest editorPlaceRequest;

    protected MenuItem editMenuItem;
    protected MenuItem viewMenuItem;
    protected MenuItem insertMenuItem;
    protected MenuItem radarMenuItem;

    protected SyncBeanManager beanManager;
    protected PlaceManager placeManager;

    public BaseGuidedDecisionTableEditorPresenter( final View view,
                                                   final Caller<GuidedDecisionTableEditorService> service,
                                                   final Event<NotificationEvent> notification,
                                                   final Event<DecisionTableSelectedEvent> decisionTableSelectedEvent,
                                                   final ClientResourceType resourceType,
                                                   final EditMenuBuilder editMenuBuilder,
                                                   final ViewMenuBuilder viewMenuBuilder,
                                                   final InsertMenuBuilder insertMenuBuilder,
                                                   final RadarMenuBuilder radarMenuBuilder,
                                                   final GuidedDecisionTableModellerView.Presenter modeller,
                                                   final SyncBeanManager beanManager,
                                                   final PlaceManager placeManager ) {
        super( view );
        this.view = view;
        this.service = service;
        this.notification = notification;
        this.decisionTableSelectedEvent = decisionTableSelectedEvent;
        this.resourceType = resourceType;
        this.editMenuBuilder = editMenuBuilder;
        this.viewMenuBuilder = viewMenuBuilder;
        this.insertMenuBuilder = insertMenuBuilder;
        this.radarMenuBuilder = radarMenuBuilder;
        this.modeller = modeller;
        this.beanManager = beanManager;
        this.placeManager = placeManager;
    }

    @Override
    //Delegated to expose package-protected setter for Unit Tests
    protected void setupMenuBar() {
        super.setupMenuBar();
    }

    @Override
    //Delegated to expose package-protected setter for Unit Tests
    protected void setKieEditorWrapperView( final KieMultipleDocumentEditorWrapperView kieEditorWrapperView ) {
        super.setKieEditorWrapperView( kieEditorWrapperView );
    }

    @Override
    //Delegated to expose package-protected setter for Unit Tests
    protected void setOverviewWidget( final OverviewWidgetPresenter overviewWidget ) {
        super.setOverviewWidget( overviewWidget );
    }

    @Override
    //Delegated to expose package-protected setter for Unit Tests
    protected void setSavePopUpPresenter( final SavePopUpPresenter savePopUpPresenter ) {
        super.setSavePopUpPresenter( savePopUpPresenter );
    }

    @Override
    //Delegated to expose package-protected setter for Unit Tests
    protected void setImportsWidget( final ImportsWidgetPresenter importsWidget ) {
        super.setImportsWidget( importsWidget );
    }

    @Override
    //Delegated to expose package-protected setter for Unit Tests
    protected void setNotificationEvent( final Event<NotificationEvent> notificationEvent ) {
        super.setNotificationEvent( notificationEvent );
    }

    @Override
    //Delegated to expose package-protected setter for Unit Tests
    protected void setChangeTitleEvent( final Event<ChangeTitleWidgetEvent> changeTitleEvent ) {
        super.setChangeTitleEvent( changeTitleEvent );
    }

    @Override
    //Delegated to expose package-protected setter for Unit Tests
    protected void setWorkbenchContext( final ProjectContext workbenchContext ) {
        super.setWorkbenchContext( workbenchContext );
    }

    @Override
    //Delegated to expose package-protected setter for Unit Tests
    protected void setVersionRecordManager( final VersionRecordManager versionRecordManager ) {
        super.setVersionRecordManager( versionRecordManager );
    }

    @Override
    //Delegated to expose package-protected setter for Unit Tests
    protected void setFileMenuBuilder( final FileMenuBuilder fileMenuBuilder ) {
        super.setFileMenuBuilder( fileMenuBuilder );
    }

    @Override
    //Delegated to expose package-protected setter for Unit Tests
    protected void setRegisteredDocumentsMenuBuilder( final RegisteredDocumentsMenuBuilder registeredDocumentsMenuBuilder ) {
        super.setRegisteredDocumentsMenuBuilder( registeredDocumentsMenuBuilder );
    }

    @Override
    //Delegated to expose package-protected setter for Unit Tests
    protected void setFileNameValidator( final DefaultFileNameValidator fileNameValidator ) {
        super.setFileNameValidator( fileNameValidator );
    }

    protected void init() {
        viewMenuBuilder.setModeller( modeller );
        insertMenuBuilder.setModeller( modeller );
        radarMenuBuilder.setModeller( modeller );
        view.setModellerView( modeller.getView() );
    }

    protected void onStartup( final ObservablePath path,
                              final PlaceRequest placeRequest ) {
        this.editorPath = path;
        this.editorPlaceRequest = placeRequest;
    }

    protected String getTitleText() {
        return resourceType.getDescription();
    }

    @Override
    public String getDocumentTitle( final GuidedDecisionTableView.Presenter dtPresenter ) {
        return dtPresenter.getCurrentPath().getFileName() + " - " + resourceType.getDescription();
    }

    protected boolean mayClose() {
        for ( GuidedDecisionTableView.Presenter dtPresenter : modeller.getAvailableDecisionTables() ) {
            if ( !mayClose( dtPresenter ) ) {
                return false;
            }
        }
        return true;
    }

    protected boolean mayClose( final GuidedDecisionTableView.Presenter dtPresenter ) {
        final Integer originalHashCode = dtPresenter.getOriginalHashCode();
        final Integer currentHashCode = dtPresenter.getModel().hashCode();
        return mayClose( originalHashCode,
                         currentHashCode );
    }

    @Override
    public void onClose() {
        super.onClose();
        modeller.onClose();
    }

    protected void onDecisionTableSelected( final DecisionTableSelectedEvent event ) {
        final GuidedDecisionTableView.Presenter dtPresenter = event.getPresenter();
        if ( dtPresenter == null ) {
            return;
        }
        if ( !modeller.isDecisionTableAvailable( dtPresenter ) ) {
            return;
        }
        if ( dtPresenter.equals( getActiveDocument() ) ) {
            return;
        }
        activateDocument( dtPresenter );
    }

    protected void activateDocument( final GuidedDecisionTableView.Presenter dtPresenter ) {
        enableMenus( true );

        dtPresenter.activate();

        activateDocument( dtPresenter,
                          dtPresenter.getOverview(),
                          dtPresenter.getDataModelOracle(),
                          dtPresenter.getModel().getImports(),
                          !dtPresenter.getAccess().isEditable() );
    }

    @Override
    public void refreshDocument( final GuidedDecisionTableView.Presenter dtPresenter ) {
        final ObservablePath versionPath = dtPresenter.getCurrentPath();

        view.showLoading();
        service.call( getRefreshContentSuccessCallback( dtPresenter ),
                      getNoSuchFileExceptionErrorCallback() ).loadContent( versionPath );
    }

    private RemoteCallback<GuidedDecisionTableEditorContent> getRefreshContentSuccessCallback( final GuidedDecisionTableView.Presenter dtPresenter ) {
        final ObservablePath path = dtPresenter.getLatestPath();
        final PlaceRequest place = dtPresenter.getPlaceRequest();
        final boolean isReadOnly = dtPresenter.isReadOnly();

        return ( content ) -> {
            //Refresh Decision Table in modeller
            modeller.refreshDecisionTable( dtPresenter,
                                           path,
                                           place,
                                           content,
                                           isReadOnly );
            activateDocument( dtPresenter );

            view.hideBusyIndicator();
        };
    }

    @Override
    public void removeDocument( final GuidedDecisionTableView.Presenter dtPresenter ) {
        modeller.removeDecisionTable( dtPresenter );
        deregisterDocument( dtPresenter );
        dtPresenter.onClose();

        openOtherDecisionTable();
    }

    void openOtherDecisionTable() {
        final Set<GuidedDecisionTableView.Presenter> availableDecisionTables = modeller.getAvailableDecisionTables();
        if ( availableDecisionTables == null || availableDecisionTables.isEmpty() ) {
            placeManager.forceClosePlace( editorPlaceRequest );
        } else {
            final GuidedDecisionTableView.Presenter dtPresenter = availableDecisionTables.iterator().next();
            activateDocument( dtPresenter );
        }
    }

    @Override
    public void onValidate( final GuidedDecisionTableView.Presenter dtPresenter ) {
        final ObservablePath path = dtPresenter.getCurrentPath();
        final GuidedDecisionTable52 model = dtPresenter.getModel();

        service.call( new RemoteCallback<List<ValidationMessage>>() {
            @Override
            public void callback( final List<ValidationMessage> results ) {
                if ( results == null || results.isEmpty() ) {
                    notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemValidatedSuccessfully(),
                                                              NotificationEvent.NotificationType.SUCCESS ) );
                } else {
                    showValidationPopup( results );
                }
            }
        } ).validate( path,
                      model );
    }

    void showValidationPopup( final List<ValidationMessage> results ) {
        ValidationPopup.showMessages( results );
    }

    @Override
    public void onSave( final GuidedDecisionTableView.Presenter dtPresenter,
                        final String commitMessage ) {
        final ObservablePath path = dtPresenter.getCurrentPath();
        final GuidedDecisionTable52 model = dtPresenter.getModel();
        final Metadata metadata = dtPresenter.getOverview().getMetadata();

        service.call( getSaveSuccessCallback( dtPresenter,
                                              model.hashCode() ),
                      new HasBusyIndicatorDefaultErrorCallback( view ) ).save( path,
                                                                               model,
                                                                               metadata,
                                                                               commitMessage );
    }

    @Override
    public void onSourceTabSelected( final GuidedDecisionTableView.Presenter dtPresenter ) {
        final ObservablePath path = dtPresenter.getCurrentPath();
        final GuidedDecisionTable52 model = dtPresenter.getModel();

        service.call( new RemoteCallback<String>() {
                          @Override
                          public void callback( String source ) {
                              updateSource( source );
                          }
                      },
                      getCouldNotGenerateSourceErrorCallback() ).toSource( path,
                                                                           model );
    }

    protected MenuItem getEditMenuItem() {
        if ( editMenuItem == null ) {
            editMenuItem = editMenuBuilder.build();
        }
        return editMenuItem;
    }

    protected MenuItem getViewMenuItem() {
        if ( viewMenuItem == null ) {
            viewMenuItem = viewMenuBuilder.build();
        }
        return viewMenuItem;
    }

    protected MenuItem getInsertMenuItem() {
        if ( insertMenuItem == null ) {
            insertMenuItem = insertMenuBuilder.build();
        }
        return insertMenuItem;
    }

    protected MenuItem getRadarMenuItem() {
        if ( radarMenuItem == null ) {
            radarMenuItem = radarMenuBuilder.build();
        }
        return radarMenuItem;
    }

    @Override
    protected void enableMenus( final boolean enabled ) {
        super.enableMenus( enabled );
        getEditMenuItem().setEnabled( enabled );
        getViewMenuItem().setEnabled( enabled );
        getInsertMenuItem().setEnabled( enabled );
        getRadarMenuItem().setEnabled( enabled );
    }

}