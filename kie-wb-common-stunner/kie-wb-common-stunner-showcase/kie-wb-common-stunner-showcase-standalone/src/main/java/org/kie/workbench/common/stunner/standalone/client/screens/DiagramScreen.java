/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.standalone.client.screens;

import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.MenuDevCommandsBuilder;
import org.kie.workbench.common.stunner.client.widgets.session.presenter.impl.AbstractClientSessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.toolbar.Toolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarCommandCallback;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.ToolbarFactory;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.service.ClientDiagramService;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSessionManager;
import org.kie.workbench.common.stunner.core.client.util.ClientSessionUtils;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasValidationViolation;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasValidatorCallback;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.lookup.LookupManager;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramLookupRequest;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramLookupRequestImpl;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramRepresentation;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.*;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.*;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

@Dependent
@WorkbenchScreen( identifier = DiagramScreen.SCREEN_ID )
public class DiagramScreen {

    private static Logger LOGGER = Logger.getLogger( DiagramScreen.class.getName() );

    public static final String SCREEN_ID = "DiagramScreen";

    private final DefinitionManager definitionManager;
    private final ClientFactoryService clientFactoryServices;
    private final ClientDiagramService clientDiagramServices;
    private final AbstractClientSessionManager canvasSessionManager;
    private final AbstractClientSessionPresenter clientSessionPresenter;
    private final PlaceManager placeManager;
    private final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent;
    private final ToolbarFactory toolbars;
    private final ClientSessionUtils sessionUtils;
    private final MenuDevCommandsBuilder menuDevCommandsBuilder;

    private PlaceRequest placeRequest;
    private String title = "Diagram Screen";
    private AbstractClientFullSession session;
    private Toolbar<AbstractClientFullSession> toolbar;
    private Menus menu = null;

    @Inject
    public DiagramScreen( final DefinitionManager definitionManager,
                          final ClientFactoryService clientFactoryServices,
                          final ClientDiagramService clientDiagramServices,
                          final AbstractClientSessionManager canvasSessionManager,
                          final AbstractClientSessionPresenter clientSessionPresenter,
                          final PlaceManager placeManager,
                          final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent,
                          final ToolbarFactory toolbars,
                          final ClientSessionUtils sessionUtils,
                          final MenuDevCommandsBuilder menuDevCommandsBuilder ) {
        this.definitionManager = definitionManager;
        this.clientFactoryServices = clientFactoryServices;
        this.clientDiagramServices = clientDiagramServices;
        this.canvasSessionManager = canvasSessionManager;
        this.clientSessionPresenter = clientSessionPresenter;
        this.placeManager = placeManager;
        this.changeTitleNotificationEvent = changeTitleNotificationEvent;
        this.toolbars = toolbars;
        this.sessionUtils = sessionUtils;
        this.menuDevCommandsBuilder = menuDevCommandsBuilder;
    }

    @PostConstruct
    @SuppressWarnings( "unchecked" )
    public void init() {
        // Create a new full control session.
        session = ( AbstractClientFullSession ) canvasSessionManager.newFullSession();
        // Initialize the session presenter.
        clientSessionPresenter.initialize( session, 1400, 650 );
        // Configure toolbar.
        this.toolbar = buildToolbar();
        clientSessionPresenter.getView().setToolbar( toolbar.getView() );
        toolbar.initialize( session, new ToolbarCommandCallback<Object>() {
            @Override
            public void onCommandExecuted( final Object result ) {
            }

            @Override
            public void onError( final ClientRuntimeError error ) {
                showError( error.toString() );
            }
        } );
    }

    private Toolbar<AbstractClientFullSession> buildToolbar() {
        return toolbars
                .withVisitGraphCommand()
                .withClearCommand()
                .withClearSelectionCommand()
                .withDeleteSelectedElementsCommand()
                .withSwitchGridCommand()
                .withUndoCommand()
                .withReddoCommand()
                .withValidateCommand()
                .withRefreshCommand()
                .build();
    }

    @OnStartup
    public void onStartup( final PlaceRequest placeRequest ) {
        this.placeRequest = placeRequest;
        this.menu = makeMenuBar();
        final String name = placeRequest.getParameter( "name", "" );
        final boolean isCreate = name == null || name.trim().length() == 0;
        final Command callback = () -> {
            final Diagram diagram = clientSessionPresenter.getCanvasHandler().getDiagram();
            if ( null != diagram ) {
                // Update screen title.
                updateTitle( diagram.getMetadata().getTitle() );

            }

        };
        if ( isCreate ) {
            final String defSetId = placeRequest.getParameter( "defSetId", "" );
            final String shapeSetd = placeRequest.getParameter( "shapeSetId", "" );
            final String title = placeRequest.getParameter( "title", "" );
            // Create a new diagram.
            newDiagram( UUID.uuid(), title, defSetId, shapeSetd, callback );

        } else {
            // Load an existing diagram.
            load( name, callback );

        }

    }

    private Menus makeMenuBar() {
        final MenuFactory.TopLevelMenusBuilder<MenuFactory.MenuBuilder> m =
                MenuFactory
                .newTopLevelMenu( "Save" )
                .respondsWith( getSaveCommand() )
                .endMenu();
        if ( menuDevCommandsBuilder.isEnabled() ) {
            m.newTopLevelMenu( menuDevCommandsBuilder.build() ).endMenu();
        }
        return m.build();
    }

    private Command getSaveCommand() {
        return this::save;
    }

    private Diagram getDiagram() {
        return null != clientSessionPresenter.getCanvasHandler() ? clientSessionPresenter.getCanvasHandler().getDiagram() : null;
    }

    private Graph getGraph() {
        return null != getDiagram() ? getDiagram().getGraph() : null;
    }

    private void save() {
        session.getCanvasValidationControl().validate( new CanvasValidatorCallback() {
            @Override
            public void onSuccess() {
                doSave( new ServiceCallback<Diagram>() {
                    @Override
                    public void onSuccess( Diagram item ) {
                        log( Level.INFO, "Save operation finished for diagram [" + item.getName() + "]." );
                    }

                    @Override
                    public void onError( ClientRuntimeError error ) {
                        showError( error.toString() );
                    }
                } );
            }

            @Override
            public void onFail( Iterable<CanvasValidationViolation> violations ) {
                log( Level.WARNING, "Validation failed [violations=" + violations.toString() + "]." );
            }
        } );
    }

    @SuppressWarnings( "unchecked" )
    private void doSave( final ServiceCallback<Diagram> diagramServiceCallback ) {
        // Update diagram's image data as thumbnail.
        final String thumbData = sessionUtils.canvasToImageData( session );
        final CanvasHandler canvasHandler = session.getCanvasHandler();
        final Diagram diagram = canvasHandler.getDiagram();
        diagram.getMetadata().setThumbData( thumbData );
        // Perform update operation remote call.
        clientDiagramServices.saveOrUpdate( diagram, diagramServiceCallback );
    }

    private void newDiagram( final String uuid,
                            final String title,
                            final String definitionSetId,
                            final String shapeSetId,
                            final Command callback ) {
        final Metadata metadata = buildMetadata( definitionSetId, shapeSetId, title );
        clientFactoryServices.newDiagram( uuid, definitionSetId, metadata, new ServiceCallback<Diagram>() {
            @Override
            public void onSuccess( final Diagram diagram ) {
                final Metadata metadata = diagram.getMetadata();
                metadata.setShapeSetId( shapeSetId );
                metadata.setTitle( title );
                open( diagram, callback );
            }

            @Override
            public void onError( final ClientRuntimeError error ) {
                showError( error.toString() );
                callback.execute();
            }
        } );

    }

    private Metadata buildMetadata( final String defSetId,
                                    final String shapeSetId,
                                    final String title ) {
        return new MetadataImpl.MetadataImplBuilder( defSetId, definitionManager )
                .setTitle( title )
                .setShapeSetId( shapeSetId )
                .build();
    }

    private void load( final String name,
                       final Command callback ) {
        final DiagramLookupRequest request = new DiagramLookupRequestImpl.Builder().withName( name ).build();
        clientDiagramServices.lookup( request, new ServiceCallback<LookupManager.LookupResponse<DiagramRepresentation>>() {
            @Override
            public void onSuccess( LookupManager.LookupResponse<DiagramRepresentation> diagramRepresentations ) {
                if ( null != diagramRepresentations && !diagramRepresentations.getResults().isEmpty() ) {
                    final Path path = diagramRepresentations.getResults().get( 0 ).getPath();
                    loadByPath( path, callback );
                }
            }

            @Override
            public void onError( ClientRuntimeError error ) {
                showError( error.toString() );
                callback.execute();
            }
        } );
    }

    private void loadByPath( final Path path, final Command callback ) {
        clientDiagramServices.getByPath( path, new ServiceCallback<Diagram>() {
            @Override
            public void onSuccess( final Diagram diagram ) {
                open( diagram, callback );
            }

            @Override
            public void onError( final ClientRuntimeError error ) {
                showError( error.toString() );
                callback.execute();
            }
        } );
    }

    private void open( final Diagram diagram,
                      final Command callback ) {
        clientSessionPresenter.open( diagram, callback );
    }

    private void updateTitle( final String title ) {
        // Change screen title.
        DiagramScreen.this.title = title;
        changeTitleNotificationEvent.fire( new ChangeTitleWidgetEvent( placeRequest, this.title ) );

    }

    @OnOpen
    public void onOpen() {
        resume();
    }

    @OnFocus
    public void onFocus() {

    }

    @OnLostFocus
    public void OnLostFocus() {

    }

    @OnClose
    public void onClose() {
        disposeSession();
    }

    @WorkbenchMenu
    public Menus getMenu() {
        return menu;
    }

    private void resume() {
        if ( null != session && session.isOpened() ) {
            canvasSessionManager.resume( session );
        }
    }

    private void disposeSession() {
        canvasSessionManager.dispose();
        if ( null != toolbar ) {
            toolbar.destroy();
        }
        this.toolbar = null;
        this.session = null;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return title;
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return clientSessionPresenter.getView();
    }

    @WorkbenchContextId
    public String getMyContextRef() {
        return "stunnerDiagramScreenContext";
    }

    protected void showError( String message ) {
        log( Level.SEVERE, message );
    }

    private void log( final Level level, final String message ) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            LOGGER.log( level, message );
        }
    }

}
