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

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.impl.AddNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.SetConnectionTargetNodeCommand;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

// TODO: Improve error handling.
public abstract class AbstractEdgeBuilder<W, T extends Edge<View<W>, Node>>
        extends AbstractObjectBuilder<W, T> implements EdgeObjectBuilder<W, T> {

    protected final Class<?> definitionClass;

    public AbstractEdgeBuilder( Class<?> definitionClass ) {
        this.definitionClass = definitionClass;
    }

    @Override
    public Class<?> getDefinitionClass() {
        return definitionClass;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected T doBuild( BuilderContext context ) {
        FactoryManager factoryManager = context.getFactoryManager();
        String definitionId = context.getOryxManager().getMappingsManager().getDefinitionId( definitionClass );
        T result = ( T ) factoryManager.newElement( this.nodeId, definitionId );
        setProperties( context, ( BPMNDefinition ) result.getContent().getDefinition() );
        afterEdgeBuild( context, result );
        return result;
    }

    @SuppressWarnings( "unchecked" )
    protected void afterEdgeBuild( BuilderContext context, T edge ) {
        // Outgoing connections.
        if ( outgoingResourceIds != null && !outgoingResourceIds.isEmpty() ) {
            for ( String outgoingNodeId : outgoingResourceIds ) {
                GraphObjectBuilder<?, ?> outgoingNodeBuilder = getBuilder( context, outgoingNodeId );
                if ( outgoingNodeBuilder == null ) {
                    throw new RuntimeException( "No edge for " + outgoingNodeId );
                }
                Node node = ( Node ) outgoingNodeBuilder.build( context );
                // Command - Add the node into the graph store.
                AddNodeCommand addNodeCommand = context.getCommandFactory().ADD_NODE( context.getGraph(), node );
                // Command - Set the edge connection's target node.
                int magnetIdx = ( ( AbstractNodeBuilder ) outgoingNodeBuilder ).getTargetConnectionMagnetIndex( context, node, edge );
                SetConnectionTargetNodeCommand setTargetNodeCommand = context.getCommandFactory().SET_TARGET_NODE( node, edge, magnetIdx );
                CommandResult<RuleViolation> results1 = context.execute( addNodeCommand );
                if ( hasErrors( results1 ) ) {
                    throw new RuntimeException( "Error building BPMN graph. Command 'addNodeCommand' execution failed." );
                }
                CommandResult<RuleViolation> results2 = context.execute( setTargetNodeCommand );
                if ( hasErrors( results2 ) ) {
                    throw new RuntimeException( "Error building BPMN graph. Command 'SetConnectionTargetNodeCommand' execution failed." );
                }
            }
        }

    }

    @Override
    public String toString() {
        return new StringBuilder( super.toString() ).append( " [defClass=" ).append( definitionClass.getName() ).append( "] " ).toString();
    }
}
