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

package org.kie.workbench.common.stunner.client.widgets.session.toolbar.command;

import com.google.gwt.logging.client.LogConfiguration;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.kie.workbench.common.stunner.client.widgets.session.toolbar.ToolbarCommandCallback;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.factory.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultCanvasFullSession;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

@Dependent
public class DeleteSelectionCommand extends AbstractSelectionToolbarCommand<DefaultCanvasFullSession> {

    private static Logger LOGGER = Logger.getLogger( DeleteSelectionCommand.class.getName() );

    CanvasCommandFactory canvasCommandFactory;

    @Inject
    public DeleteSelectionCommand( final CanvasCommandFactory canvasCommandFactory ) {
        this.canvasCommandFactory = canvasCommandFactory;
    }

    @Override
    public IconType getIcon() {
        return IconType.TRASH_O;
    }

    @Override
    public String getCaption() {
        return null;
    }

    @Override
    public String getTooltip() {
        return "Delete the selected elements";
    }

    @Override
    public <T> void execute( final ToolbarCommandCallback<T> callback ) {
        if ( null != session.getShapeSelectionControl() ) {
            executeWithConfirm( () -> {
                final AbstractCanvasHandler canvasHandler = session.getCanvasHandler();
                final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager = session.getCanvasCommandManager();
                final SelectionControl<AbstractCanvasHandler, Element> selectionControl = session.getShapeSelectionControl();
                final Collection<String> selectedItems = selectionControl.getSelectedItems();
                if ( selectedItems != null && !selectedItems.isEmpty() ) {
                    for ( final String selectedItemUUID : selectedItems ) {
                        Element element = canvasHandler.getGraphIndex().getNode( selectedItemUUID );
                        if ( element == null ) {
                            element = canvasHandler.getGraphIndex().getEdge( selectedItemUUID );
                            if ( element != null ) {
                                log( Level.FINE, "Deleting edge with id " + element.getUUID() );
                                canvasCommandManager.execute( canvasHandler, canvasCommandFactory.DELETE_EDGE( ( Edge ) element ) );
                            }

                        } else {
                            log( Level.FINE, "Deleting node with id " + element.getUUID() );
                            canvasCommandManager.execute( canvasHandler, canvasCommandFactory.DELETE_NODE( ( Node ) element ) );

                        }

                    }

                } else {
                    log( Level.FINE, "Cannot delete element, no element selected on canvas." );

                }
                if ( null != callback ) {
                    callback.onCommandExecuted( null );
                }

            } );

        }

    }

    private void log( final Level level, final String message ) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            LOGGER.log( level, message );
        }
    }

}
