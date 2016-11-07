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
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * A Command to update an element's property.
 */
@Portable
public final class UpdateElementPropertyValueCommand extends AbstractGraphCommand {

    private final String elementUUID;
    private final String propertyId;
    private final Object value;
    private Object oldValue;

    public UpdateElementPropertyValueCommand( @MapsTo( "elementUUID" ) String elementUUID,
                                              @MapsTo( "propertyId" ) String propertyId,
                                              @MapsTo( "value" ) Object value ) {
        this.elementUUID = PortablePreconditions.checkNotNull( "elementUUID",
                elementUUID );
        this.propertyId = PortablePreconditions.checkNotNull( "propertyId",
                propertyId );
        this.value = PortablePreconditions.checkNotNull( "value",
                value );
    }

    @Override
    public CommandResult<RuleViolation> allow( final GraphCommandExecutionContext context ) {
        return check( context );
    }

    @Override
    protected CommandResult<RuleViolation> doCheck( GraphCommandExecutionContext context ) {
        checkNodeNotNull( context, elementUUID );
        return GraphCommandResultBuilder.SUCCESS;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public CommandResult<RuleViolation> execute( final GraphCommandExecutionContext context ) {
        final Element<Definition<?>> element = ( Element<Definition<?>> ) checkNodeNotNull( context, elementUUID );
        final Object p = GraphUtils.getProperty( context.getDefinitionManager(), element, propertyId );
        final PropertyAdapter<Object, Object> adapter =
                ( PropertyAdapter<Object, Object> ) context.getDefinitionManager().adapters().registry().getPropertyAdapter( p.getClass() );
        oldValue = adapter.getValue( p );
        adapter.setValue( p, value );
        return GraphCommandResultBuilder.SUCCESS;
    }

    @Override
    public CommandResult<RuleViolation> undo( final GraphCommandExecutionContext context ) {
        final UpdateElementPropertyValueCommand undoCommand = new UpdateElementPropertyValueCommand( elementUUID, propertyId, oldValue );
        return undoCommand.execute( context );
    }

    public Object getOldValue() {
        return oldValue;
    }

    @Override
    public String toString() {
        return "UpdateElementPropertyValueCommand [element=" + elementUUID + ", property=" + propertyId + ", value=" + value + "]";
    }

}
