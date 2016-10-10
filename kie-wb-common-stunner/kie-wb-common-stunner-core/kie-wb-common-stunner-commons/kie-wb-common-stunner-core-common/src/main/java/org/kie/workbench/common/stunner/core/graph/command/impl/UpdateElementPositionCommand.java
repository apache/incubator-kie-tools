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
package org.kie.workbench.common.stunner.core.graph.command.impl;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.uberfire.commons.validation.PortablePreconditions;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Command to update an element's bounds.
 */
@Portable
public final class UpdateElementPositionCommand extends AbstractGraphCommand {

    private static Logger LOGGER = Logger.getLogger( UpdateElementPositionCommand.class.getName() );

    private Element element;
    private Double x;
    private Double y;
    private Double oldX;
    private Double oldY;

    public UpdateElementPositionCommand( @MapsTo( "element" ) Element element,
                                         @MapsTo( "x" ) Double x,
                                         @MapsTo( "y" ) Double y ) {
        this.element = PortablePreconditions.checkNotNull( "element",
                element );
        ;
        this.x = PortablePreconditions.checkNotNull( "x",
                x );
        this.y = PortablePreconditions.checkNotNull( "y",
                y );
    }

    @Override
    public CommandResult<RuleViolation> allow( final GraphCommandExecutionContext context ) {
        return check( context );
    }

    @Override
    protected CommandResult<RuleViolation> doCheck( GraphCommandExecutionContext context ) {
        return GraphCommandResultBuilder.RESULT_OK;
    }

    @Override
    public CommandResult<RuleViolation> execute( final GraphCommandExecutionContext context ) {
        final Double[] oldPosition = GraphUtils.getPosition( ( View ) element.getContent() );
        final Double[] oldSize = GraphUtils.getSize( ( View ) element.getContent() );
        this.oldX = oldPosition[ 0 ];
        this.oldY = oldPosition[ 1 ];
        final double w = oldSize[ 0 ];
        final double h = oldSize[ 1 ];
        final BoundsImpl newBounds = new BoundsImpl(
                new BoundImpl( x, y ),
                new BoundImpl( x + w, y + h )
        );
        ( ( View ) element.getContent() ).setBounds( newBounds );
        LOGGER.log( Level.FINE, "Moving element bounds to [" + x + "," + y + "] [" + ( x + w ) + "," + ( y + h ) + "]" );
        return GraphCommandResultBuilder.RESULT_OK;
    }

    @Override
    public CommandResult<RuleViolation> undo( final GraphCommandExecutionContext context ) {
        final UpdateElementPositionCommand undoCommand = new UpdateElementPositionCommand( element, oldX, oldY );
        return undoCommand.execute( context );
    }

    public Double getOldX() {
        return oldX;
    }

    public Double getOldY() {
        return oldY;
    }

    @Override
    public String toString() {
        return "UpdateElementPositionCommand [element=" + element.getUUID() + ", x=" + x + ", y=" + y + "]";
    }

}
