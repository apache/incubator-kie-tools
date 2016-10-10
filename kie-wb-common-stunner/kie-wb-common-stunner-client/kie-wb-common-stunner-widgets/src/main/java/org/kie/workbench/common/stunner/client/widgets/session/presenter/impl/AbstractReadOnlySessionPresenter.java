/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.session.presenter.impl;

import com.google.gwt.logging.client.LogConfiguration;
import org.kie.workbench.common.stunner.client.widgets.session.presenter.ReadOnlySessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.session.toolbar.ToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.session.toolbar.ToolbarCommandCallback;
import org.kie.workbench.common.stunner.client.widgets.session.toolbar.command.ClearSelectionCommand;
import org.kie.workbench.common.stunner.client.widgets.session.toolbar.command.SwitchGridCommand;
import org.kie.workbench.common.stunner.client.widgets.session.toolbar.command.VisitGraphCommand;
import org.kie.workbench.common.stunner.client.widgets.session.toolbar.impl.AbstractToolbar;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasElementListener;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasShapeListener;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLoadingObserver;
import org.kie.workbench.common.stunner.core.client.service.ClientDiagramServices;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.CanvasReadOnlySession;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultCanvasSessionManager;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.mvp.Command;

import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractReadOnlySessionPresenter<S extends CanvasReadOnlySession<AbstractCanvas, AbstractCanvasHandler>>
        extends AbstractCanvasSessionPresenter<S>
        implements ReadOnlySessionPresenter<AbstractCanvas, AbstractCanvasHandler, S> {

    private static Logger LOGGER = Logger.getLogger( AbstractReadOnlySessionPresenter.class.getName() );

    protected DefaultCanvasSessionManager canvasSessionManager;
    protected ClientDiagramServices clientDiagramServices;
    protected AbstractToolbar<S> toolbar;
    protected ClearSelectionCommand clearSelectionCommand;
    protected VisitGraphCommand visitGraphCommand;
    protected SwitchGridCommand switchGridCommand;

    @Inject
    public AbstractReadOnlySessionPresenter( final DefaultCanvasSessionManager canvasSessionManager,
                                             final ClientDiagramServices clientDiagramServices,
                                             final AbstractToolbar<S> toolbar,
                                             final ClearSelectionCommand clearSelectionCommand,
                                             final VisitGraphCommand visitGraphCommand,
                                             final SwitchGridCommand switchGridCommand,
                                             final ErrorPopupPresenter errorPopupPresenter,
                                             final View view ) {
        super( errorPopupPresenter, view );
        this.canvasSessionManager = canvasSessionManager;
        this.clientDiagramServices = clientDiagramServices;
        this.toolbar = toolbar;
        this.clearSelectionCommand = clearSelectionCommand;
        this.visitGraphCommand = visitGraphCommand;
        this.switchGridCommand = switchGridCommand;
    }

    @Override
    public void doInitialize( S session, int width, int height ) {
        super.doInitialize( session, width, height );
        final AbstractCanvas canvas = getCanvasHandler().getCanvas();
        canvas.addRegistrationListener( new CanvasShapeListener() {

            @Override
            public void register( final Shape item ) {
                onRegisterShape( item );

            }

            @Override
            public void deregister( final Shape item ) {
                onDeregisterShape( item );

            }

            @Override
            public void clear() {
                onClear();

            }

        } );
        getCanvasHandler().addRegistrationListener( new CanvasElementListener() {

            @Override
            public void update( final Element item ) {
                onUpdateElement( item );

            }

            @Override
            public void register( final Element item ) {
                onRegisterElement( item );

            }

            @Override
            public void deregister( final Element item ) {
                onDeregisterElement( item );

            }

            @Override
            public void clear() {
                onClear();

            }

        } );
        // Enable canvas controls.
        enableControl( session.getShapeSelectionControl(), session.getCanvasHandler() );
        enableControl( session.getZoomControl(), session.getCanvas() );
        enableControl( session.getPanControl(), session.getCanvas() );
        // Toolbar read-only commands.
        setToolbarCommands();
        // Enable canvas loading callback.
        session.getCanvas().setLoadingObserverCallback( new CanvasLoadingObserver.Callback() {

            @Override
            public void onLoadingStarted() {
                fireProcessingStarted();
            }

            @Override
            public void onLoadingCompleted() {
                fireProcessingCompleted();
            }

        } );

    }

    protected void onClear() {
        fireRegistrationClearListeners( session.getShapeSelectionControl() );

    }

    protected void onRegisterShape( final Shape shape ) {
        onShapeRegistration( shape, true );

    }

    protected void onDeregisterShape( final Shape shape ) {
        onShapeRegistration( shape, false );

    }

    protected void onShapeRegistration( final Shape shape,
                                        final boolean add ) {
        fireRegistrationListeners( session.getZoomControl(), shape, add );
        fireRegistrationListeners( session.getPanControl(), shape, add );

    }

    protected void onRegisterElement( final Element element ) {
        onElementRegistration( element, true );

    }

    protected void onDeregisterElement( final Element element ) {
        onElementRegistration( element, false );

    }

    protected void onElementRegistration( final Element element,
                                          final boolean add ) {
        fireRegistrationListeners( session.getShapeSelectionControl(), element, add );

    }

    protected void onUpdateElement( final Element element ) {
        fireRegistrationUpdateListeners( session.getShapeSelectionControl(), element );

    }

    protected void setToolbarCommands() {
        addToolbarCommand( ( ToolbarCommand<S> ) this.clearSelectionCommand );
        addToolbarCommand( ( ToolbarCommand<S> ) this.visitGraphCommand );
        addToolbarCommand( ( ToolbarCommand<S> ) this.switchGridCommand );
    }

    @Override
    public void showToolbar() {
        toolbar.show();
    }

    @Override
    public void hideToolbar() {
        toolbar.hide();
    }

    @Override
    protected void initializeView() {
        super.initializeView();
        view.setToolbar( toolbar.asWidget() );
    }

    @Override
    protected void afterInitialize( S session, int width, int height ) {
        super.afterInitialize( session, width, height );
        toolbar.initialize( session, new ToolbarCommandCallback<Object>() {
            @Override
            public void onCommandExecuted( final Object result ) {
            }

            @Override
            public void onError( final ClientRuntimeError error ) {
                showError( error );
            }
        } );
    }

    protected void addToolbarCommand( final ToolbarCommand<S> command ) {
        toolbar.addCommand( command );
    }

    @Override
    public void load( final String diagramUUID,
                      final Command callback ) {
        // Notify processing starts.
        fireProcessingStarted();
        clientDiagramServices.get( diagramUUID, new ServiceCallback<Diagram>() {
            @Override
            public void onSuccess( final Diagram diagram ) {
                open( diagram, callback );
            }

            @Override
            public void onError( final ClientRuntimeError error ) {
                showError( error );
                callback.execute();
            }
        } );

    }

    protected void open( final Diagram diagram,
                         final Command callback ) {
        // Draw the graph on the canvas.
        getCanvasHandler().draw( diagram );
        canvasSessionManager.open( session );
        // Notify processing ends.
        fireProcessingCompleted();
        callback.execute();

    }

    @Override
    public void clearSelection() {
        clearSelectionCommand.execute( new ToolbarCommandCallback<Void>() {
            @Override
            public void onCommandExecuted( final Void result ) {
            }

            @Override
            public void onError( final ClientRuntimeError error ) {
                showError( error );
            }
        } );
    }

    @Override
    protected void disposeSession() {
        if ( null != toolbar ) {
            this.toolbar.destroy();
        }
        if ( null != clearSelectionCommand ) {
            this.clearSelectionCommand.destroy();
        }
        if ( null != visitGraphCommand ) {
            this.visitGraphCommand.destroy();
        }
        if ( null != switchGridCommand ) {
            this.switchGridCommand.destroy();
        }
        this.canvasSessionManager = null;
        this.clientDiagramServices = null;
        this.toolbar = null;
        this.clearSelectionCommand = null;
        this.visitGraphCommand = null;
        this.switchGridCommand = null;

    }

    private void fireProcessingStarted() {
        view.setLoading( true );
    }

    private void fireProcessingCompleted() {
        view.setLoading( false );
    }

    @Override
    protected void showError( String message ) {
        fireProcessingCompleted();
        log( Level.SEVERE, message );
        super.showError( message );
    }

    private void log( final Level level, final String message ) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            LOGGER.log( level, message );
        }
    }

}
