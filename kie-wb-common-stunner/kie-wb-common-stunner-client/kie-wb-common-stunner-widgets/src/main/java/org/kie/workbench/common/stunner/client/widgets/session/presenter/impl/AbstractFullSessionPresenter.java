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
import com.google.gwt.user.client.Window;
import org.kie.workbench.common.stunner.client.widgets.session.presenter.FullSessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.session.toolbar.ToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.session.toolbar.ToolbarCommandCallback;
import org.kie.workbench.common.stunner.client.widgets.session.toolbar.command.*;
import org.kie.workbench.common.stunner.client.widgets.session.toolbar.impl.AbstractToolbar;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.factory.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.service.ClientDiagramServices;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryServices;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.CanvasFullSession;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultCanvasSessionManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Settings;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.mvp.Command;

import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractFullSessionPresenter<S extends CanvasFullSession<AbstractCanvas, AbstractCanvasHandler>>
        extends AbstractReadOnlySessionPresenter<S>
        implements FullSessionPresenter<AbstractCanvas, AbstractCanvasHandler, S> {

    protected ClientDefinitionManager clientDefinitionManager;
    protected ClientFactoryServices clientFactoryServices;
    protected CanvasCommandFactory commandFactory;
    protected ClearCommand clearCommand;
    protected DeleteSelectionCommand deleteSelectionCommand;
    protected SaveCommand saveCommand;
    protected UndoCommand undoCommand;
    protected ValidateCommand validateCommand;

    private static Logger LOGGER = Logger.getLogger( FullSessionPresenter.class.getName() );

    @Inject
    public AbstractFullSessionPresenter( final DefaultCanvasSessionManager canvasSessionManager,
                                         final ClientDefinitionManager clientDefinitionManager,
                                         final ClientFactoryServices clientFactoryServices,
                                         final CanvasCommandFactory commandFactory,
                                         final ClientDiagramServices clientDiagramServices,
                                         final AbstractToolbar<S> toolbar,
                                         final ClearSelectionCommand clearSelectionCommand,
                                         final ClearCommand clearCommand,
                                         final DeleteSelectionCommand deleteSelectionCommand,
                                         final SaveCommand saveCommand,
                                         final UndoCommand undoCommand,
                                         final ValidateCommand validateCommand,
                                         final VisitGraphCommand visitGraphCommand,
                                         final SwitchGridCommand switchGridCommand,
                                         final ErrorPopupPresenter errorPopupPresenter,
                                         final View view ) {
        super( canvasSessionManager, clientDiagramServices, toolbar, clearSelectionCommand, visitGraphCommand,
                switchGridCommand, errorPopupPresenter, view );
        this.commandFactory = commandFactory;
        this.clientDefinitionManager = clientDefinitionManager;
        this.clientFactoryServices = clientFactoryServices;
        this.clearCommand = clearCommand;
        this.deleteSelectionCommand = deleteSelectionCommand;
        this.saveCommand = saveCommand;
        this.undoCommand = undoCommand;
        this.validateCommand = validateCommand;

    }

    @Override
    public void doInitialize( final S session,
                              final int width,
                              final int height ) {
        super.doInitialize( session, width, height );
        // Enable canvas controls.
        final AbstractCanvasHandler canvasHandler = getCanvasHandler();
        enableControl( session.getResizeControl(), canvasHandler );
        enableControl( session.getConnectionAcceptorControl(), canvasHandler );
        enableControl( session.getContainmentAcceptorControl(), canvasHandler );
        enableControl( session.getDockingAcceptorControl(), canvasHandler );
        enableControl( session.getDragControl(), canvasHandler );
        enableControl( session.getToolboxControl(), canvasHandler );
        enableControl( session.getBuilderControl(), canvasHandler );
        enableControl( session.getCanvasValidationControl(), canvasHandler );
        enableControl( session.getCanvasSaveControl(), canvasHandler );
        enableControl( session.getCanvasPaletteControl(), canvasHandler );
        enableControl( session.getCanvasNameEditionControl(), canvasHandler );

    }

    @Override
    protected void setToolbarCommands() {
        super.setToolbarCommands();
        // Toolbar commands for canvas controls.
        super.addToolbarCommand( ( ToolbarCommand<S> ) clearCommand );
        super.addToolbarCommand( ( ToolbarCommand<S> ) deleteSelectionCommand );
        super.addToolbarCommand( ( ToolbarCommand<S> ) validateCommand );
        super.addToolbarCommand( ( ToolbarCommand<S> ) saveCommand );
        super.addToolbarCommand( ( ToolbarCommand<S> ) undoCommand );

    }

    @Override
    protected void onUpdateElement( final Element element ) {
        super.onUpdateElement( element );
        fireRegistrationUpdateListeners( session.getResizeControl(), element );
        fireRegistrationUpdateListeners( session.getConnectionAcceptorControl(), element );
        fireRegistrationUpdateListeners( session.getContainmentAcceptorControl(), element );
        fireRegistrationUpdateListeners( session.getDockingAcceptorControl(), element );
        fireRegistrationUpdateListeners( session.getDragControl(), element );
        fireRegistrationUpdateListeners( session.getToolboxControl(), element );
        fireRegistrationUpdateListeners( session.getBuilderControl(), element );
        fireRegistrationUpdateListeners( session.getCanvasPaletteControl(), element );
        fireRegistrationUpdateListeners( session.getCanvasNameEditionControl(), element );

    }

    @Override
    protected void onClear() {
        super.onClear();
        fireRegistrationClearListeners( session.getResizeControl() );
        fireRegistrationClearListeners( session.getConnectionAcceptorControl() );
        fireRegistrationClearListeners( session.getContainmentAcceptorControl() );
        fireRegistrationClearListeners( session.getDockingAcceptorControl() );
        fireRegistrationClearListeners( session.getDragControl() );
        fireRegistrationClearListeners( session.getToolboxControl() );
        fireRegistrationClearListeners( session.getBuilderControl() );
        fireRegistrationClearListeners( session.getCanvasValidationControl() );
        fireRegistrationClearListeners( session.getCanvasSaveControl() );
        fireRegistrationClearListeners( session.getCanvasPaletteControl() );
        fireRegistrationClearListeners( session.getCanvasNameEditionControl() );

    }

    protected void onElementRegistration( final Element element,
                                          final boolean add ) {
        super.onElementRegistration( element, add );
        fireRegistrationListeners( session.getResizeControl(), element, add );
        fireRegistrationListeners( session.getConnectionAcceptorControl(), element, add );
        fireRegistrationListeners( session.getContainmentAcceptorControl(), element, add );
        fireRegistrationListeners( session.getDockingAcceptorControl(), element, add );
        fireRegistrationListeners( session.getDragControl(), element, add );
        fireRegistrationListeners( session.getToolboxControl(), element, add );
        fireRegistrationListeners( session.getBuilderControl(), element, add );
        fireRegistrationListeners( session.getCanvasPaletteControl(), element, add );
        fireRegistrationListeners( session.getCanvasNameEditionControl(), element, add );

    }

    @Override
    @SuppressWarnings( "unchecked" )
    public void newDiagram( final String uuid,
                            final String title,
                            final String definitionSetId,
                            final String shapeSetId,
                            final Command callback ) {
        clientFactoryServices.newDiagram( uuid, definitionSetId, new ServiceCallback<Diagram>() {
            @Override
            public void onSuccess( final Diagram diagram ) {
                final Settings settings = diagram.getSettings();
                settings.setShapeSetId( shapeSetId );
                settings.setTitle( title );
                open( diagram, callback );
            }

            @Override
            public void onError( final ClientRuntimeError error ) {
                showError( error );
                callback.execute();
            }
        } );

    }

    @Override
    public void save( final Command callback ) {
        saveCommand.execute( new ToolbarCommandCallback<Diagram>() {

            @Override
            public void onCommandExecuted( final Diagram result ) {
                Window.alert( "Diagram saved successfully [UUID=" + result.getUUID() + "]" );
                callback.execute();
            }

            @Override
            public void onError( final ClientRuntimeError error ) {
                showError( error );
                callback.execute();
            }
        } );

    }

    @Override
    public void clear() {
        clearCommand.execute();

    }

    @Override
    public void undo() {
        undoCommand.execute();

    }

    @Override
    public void deleteSelected() {
        deleteSelectionCommand.execute();

    }

    @Override
    protected void disposeSession() {
        getCanvasHandler().clearRegistrationListeners();
        super.disposeSession();
        if ( null != clearCommand ) {
            this.clearCommand.destroy();
        }
        if ( null != deleteSelectionCommand ) {
            this.deleteSelectionCommand.destroy();
        }
        if ( null != saveCommand ) {
            this.saveCommand.destroy();
        }
        if ( null != undoCommand ) {
            this.undoCommand.destroy();
        }
        if ( null != validateCommand ) {
            this.validateCommand.destroy();
        }
        this.clientDefinitionManager = null;
        this.clientFactoryServices = null;
        this.commandFactory = null;
        this.clearCommand = null;
        this.deleteSelectionCommand = null;
        this.saveCommand = null;
        this.undoCommand = null;
        this.validateCommand = null;

    }

    @Override
    protected void pauseSession() {
    }

    /*
        PUBLIC UTILITY METHODS FOR CODING & TESTING
     */

    public void visitGraph() {
        visitGraphCommand.execute();

    }

    @Override
    protected void showError( String message ) {
        log( Level.SEVERE, message );
        super.showError( message );
    }

    private void log( final Level level, final String message ) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            LOGGER.log( level, message );
        }
    }

}
