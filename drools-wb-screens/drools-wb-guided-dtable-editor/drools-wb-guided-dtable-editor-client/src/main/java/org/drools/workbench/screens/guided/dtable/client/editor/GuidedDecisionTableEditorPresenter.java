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
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.EditMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.InsertMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.RadarMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.ViewMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.type.GuidedDTableResourceType;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.AddDecisionTableToEditorEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorContent;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;
import org.kie.workbench.common.widgets.metadata.client.KieMultipleDocumentEditor;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.client.annotations.WorkbenchEditor.LockingStrategy.*;

/**
 * Guided Decision Table Editor Presenter
 */
@Dependent
@WorkbenchEditor(identifier = "GuidedDecisionTableEditor", supportedTypes = { GuidedDTableResourceType.class }, lockingStrategy = EDITOR_PROVIDED)
public class GuidedDecisionTableEditorPresenter extends KieMultipleDocumentEditor<GuidedDecisionTableView.Presenter> {

    public interface View extends RequiresResize,
                                  ProvidesResize,
                                  KieEditorView,
                                  IsWidget {

        void setModellerView( final GuidedDecisionTableModellerView view );

    }

    private View view;
    private Caller<GuidedDecisionTableEditorService> service;
    private Event<NotificationEvent> notification;
    private Event<DecisionTableSelectedEvent> decisionTableSelectedEvent;
    private GuidedDTableResourceType type;
    private EditMenuBuilder editMenuBuilder;
    private ViewMenuBuilder viewMenuBuilder;
    private InsertMenuBuilder insertMenuBuilder;
    private RadarMenuBuilder radarMenuBuilder;
    private GuidedDecisionTableModellerView.Presenter modeller;

    private ObservablePath rootPath;
    private PlaceRequest rootPlaceRequest;

    private MenuItem editMenuItem;
    private MenuItem viewMenuItem;
    private MenuItem insertMenuItem;
    private MenuItem radarMenuItem;

    private SyncBeanManager beanManager;
    private PlaceManager placeManager;

    @Inject
    public GuidedDecisionTableEditorPresenter( final View view,
                                               final Caller<GuidedDecisionTableEditorService> service,
                                               final Event<NotificationEvent> notification,
                                               final Event<DecisionTableSelectedEvent> decisionTableSelectedEvent,
                                               final GuidedDTableResourceType type,
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
        this.type = type;
        this.editMenuBuilder = editMenuBuilder;
        this.viewMenuBuilder = viewMenuBuilder;
        this.insertMenuBuilder = insertMenuBuilder;
        this.radarMenuBuilder = radarMenuBuilder;
        this.modeller = modeller;
        this.beanManager = beanManager;
        this.placeManager = placeManager;
    }

    @PostConstruct
    public void init() {
        viewMenuBuilder.setModeller( modeller );
        insertMenuBuilder.setModeller( modeller );
        radarMenuBuilder.setModeller( modeller );
        view.setModellerView( modeller.getView() );

        //Selecting a Decision Table in the document selector fires a selection event
        registeredDocumentsMenuBuilder.setActivateDocumentCommand( ( document ) -> {
            final GuidedDecisionTablePresenter dtPresenter = ( (GuidedDecisionTablePresenter) document );
            decisionTableSelectedEvent.fire( new DecisionTableSelectedEvent( dtPresenter ) );
        } );

        //Removing a Decision Table from the document selector is equivalent to closing the editor
        registeredDocumentsMenuBuilder.setRemoveDocumentCommand( ( document ) -> {
            final GuidedDecisionTablePresenter dtPresenter = ( (GuidedDecisionTablePresenter) document );
            if ( mayClose( dtPresenter ) ) {
                removeDocument( dtPresenter );
            }
        } );
    }

    @OnStartup
    @SuppressWarnings("unused")
    public void onStartup( final ObservablePath path,
                           final PlaceRequest placeRequest ) {
        loadDocument( path,
                      placeRequest );
        this.rootPath = path;
        this.rootPlaceRequest = placeRequest;
    }

    void onAddDecisionTableToEditor( final @Observes AddDecisionTableToEditorEvent event ) {
        if ( event == null ) {
            return;
        }
        final PathPlaceRequest existingEditorPlaceRequest = event.getExistingEditorPlaceRequest();
        final ObservablePath newDecisionTablePath = event.getNewDecisionTablePath();
        if ( existingEditorPlaceRequest == null ) {
            return;
        }
        if ( newDecisionTablePath == null ) {
            return;
        }
        if ( !existingEditorPlaceRequest.equals( this.rootPlaceRequest ) ) {
            return;
        }
        loadDocument( newDecisionTablePath,
                      existingEditorPlaceRequest );
    }

    @WorkbenchPartTitle
    @SuppressWarnings("unused")
    public String getTitleText() {
        return type.getDescription();
    }

    @Override
    public String getDocumentTitle( final GuidedDecisionTableView.Presenter dtPresenter ) {
        return dtPresenter.getCurrentPath().getFileName() + " - " + type.getDescription();
    }

    @WorkbenchPartTitleDecoration
    @SuppressWarnings("unused")
    public IsWidget getTitleWidget() {
        return null;
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

    @OnMayClose
    @SuppressWarnings("unused")
    public boolean mayClose() {
        for ( GuidedDecisionTableView.Presenter dtPresenter : modeller.getAvailableDecisionTables() ) {
            if ( !mayClose( dtPresenter ) ) {
                return false;
            }
        }
        return true;
    }

    private boolean mayClose( final GuidedDecisionTableView.Presenter dtPresenter ) {
        final Integer originalHashCode = dtPresenter.getOriginalHashCode();
        final Integer currentHashCode = dtPresenter.getModel().hashCode();
        return mayClose( originalHashCode,
                         currentHashCode );
    }

    @Override
    @OnClose
    public void onClose() {
        this.modeller.onClose();
        super.onClose();
    }

    @Override
    public void loadDocument( final ObservablePath path,
                              final PlaceRequest placeRequest ) {
        view.showLoading();
        service.call( getLoadContentSuccessCallback( path,
                                                     placeRequest ),
                      getNoSuchFileExceptionErrorCallback() ).loadContent( path );
    }

    private RemoteCallback<GuidedDecisionTableEditorContent> getLoadContentSuccessCallback( final ObservablePath path,
                                                                                            final PlaceRequest placeRequest ) {
        return new RemoteCallback<GuidedDecisionTableEditorContent>() {

            @Override
            public void callback( final GuidedDecisionTableEditorContent content ) {
                //Path is set to null when the Editor is closed (which can happen before async calls complete).
                if ( path == null ) {
                    return;
                }

                //Add Decision Table to modeller
                final GuidedDecisionTableView.Presenter dtPresenter = modeller.addDecisionTable( path,
                                                                                                 placeRequest,
                                                                                                 content,
                                                                                                 placeRequest.getParameter( "readOnly", null ) != null );
                registerDocument( dtPresenter );

                decisionTableSelectedEvent.fire( new DecisionTableSelectedEvent( dtPresenter ) );

                view.hideBusyIndicator();
            }

        };
    }

    void onDecisionTableSelected( final @Observes DecisionTableSelectedEvent event ) {
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

    private void activateDocument( final GuidedDecisionTableView.Presenter dtPresenter ) {
        enableMenus( true );

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

        return new RemoteCallback<GuidedDecisionTableEditorContent>() {

            @Override
            public void callback( final GuidedDecisionTableEditorContent content ) {
                //Refresh Decision Table in modeller
                modeller.refreshDecisionTable( dtPresenter,
                                               path,
                                               place,
                                               content,
                                               isReadOnly );

                decisionTableSelectedEvent.fire( new DecisionTableSelectedEvent( dtPresenter ) );

                view.hideBusyIndicator();
            }

        };
    }

    @Override
    public void removeDocument( final GuidedDecisionTableView.Presenter dtPresenter ) {
        modeller.removeDecisionTable( dtPresenter );
        deregisterDocument( dtPresenter );
        dtPresenter.onClose();

        if ( modeller.getAvailableDecisionTables().isEmpty() ) {
            placeManager.forceClosePlace( rootPlaceRequest );
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
                                  ValidationPopup.showMessages( results );
                              }
                          }
                      },
                      new DefaultErrorCallback() ).validate( path,
                                                             model );
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
                .addNewTopLevelMenu( getRegisteredDocumentsMenuItem() )
                .addNewTopLevelMenu( getVersionManagerMenuItem() )
                .build();
    }

    private MenuItem getEditMenuItem() {
        if ( editMenuItem == null ) {
            editMenuItem = editMenuBuilder.build();
        }
        return editMenuItem;
    }

    private MenuItem getViewMenuItem() {
        if ( viewMenuItem == null ) {
            viewMenuItem = viewMenuBuilder.build();
        }
        return viewMenuItem;
    }

    private MenuItem getInsertMenuItem() {
        if ( insertMenuItem == null ) {
            insertMenuItem = insertMenuBuilder.build();
        }
        return insertMenuItem;
    }

    private MenuItem getRadarMenuItem() {
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

    @Override
    public void getAvailableDocumentPaths( final Callback<List<Path>> callback ) {
        view.showLoading();
        service.call( new RemoteCallback<List<Path>>() {
                          @Override
                          public void callback( final List<Path> paths ) {
                              view.hideBusyIndicator();
                              callback.callback( paths );
                          }
                      },
                      new HasBusyIndicatorDefaultErrorCallback( view ) ).listDecisionTablesInProject( rootPath );
    }

    @Override
    public void onOpenDocumentInEditor( final Path path ) {
        final ObservablePath oPath = beanManager.lookupBean( ObservablePath.class ).newInstance();
        loadDocument( oPath.wrap( path ),
                      rootPlaceRequest );
    }

}