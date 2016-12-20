/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.OryxIdMappings;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.Bpmn2OryxPropertyManager;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.definition.property.PropertyType;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractObjectBuilder<W, T extends Element<View<W>>> implements GraphObjectBuilder<W, T> {

    private static final Logger LOG = LoggerFactory.getLogger( AbstractObjectBuilder.class );

    protected String nodeId;
    protected Map<String, String> properties;
    protected Set<String> outgoingResourceIds;
    protected Double[] boundUL;
    protected Double[] boundLR;
    protected final List<Double[]> dockers = new LinkedList<>();
    protected T result;

    public AbstractObjectBuilder() {
        this.properties = new HashMap<String, String>();
        this.outgoingResourceIds = new LinkedHashSet<String>();
        this.boundUL = null;
        this.boundLR = null;
    }

    @Override
    public GraphObjectBuilder<W, T> nodeId( String nodeId ) {
        this.nodeId = nodeId;
        return this;
    }

    @Override
    public GraphObjectBuilder<W, T> property( String key, String value ) {
        properties.put( key, value );
        return this;
    }

    @Override
    public GraphObjectBuilder<W, T> out( String nodeId ) {
        outgoingResourceIds.add( nodeId );
        return this;
    }

    @Override
    public GraphObjectBuilder<W, T> stencil( String stencilId ) {
        return this;
    }

    @Override
    public GraphObjectBuilder<W, T> boundUL( Double x, Double y ) {
        this.boundUL = new Double[]{ x, y };
        return this;
    }

    @Override
    public GraphObjectBuilder<W, T> boundLR( Double x, Double y ) {
        this.boundLR = new Double[]{ x, y };
        return this;
    }

    @Override
    public GraphObjectBuilder<W, T> docker( Double x, Double y ) {
        this.dockers.add( new Double[]{ x, y } );
        return this;
    }

    protected abstract T doBuild( BuilderContext context );

    @Override
    public T build( BuilderContext context ) {
        if ( null == this.result ) {
            this.result = doBuild( context );
        }
        return this.result;
    }

    protected boolean hasErrors( CommandResult<RuleViolation> results ) {
        return CommandResult.Type.ERROR.equals( results.getType() );
    }

    protected GraphObjectBuilder<?, ?> getBuilder( BuilderContext context, String nodeId ) {
        Collection<GraphObjectBuilder<?, ?>> builders = context.getBuilders();
        if ( builders != null && !builders.isEmpty() ) {
            for ( GraphObjectBuilder<?, ?> builder : builders ) {
                AbstractObjectBuilder<?, ?> abstractBuilder = ( AbstractObjectBuilder<?, ?> ) builder;
                if ( abstractBuilder.nodeId.equals( nodeId ) ) {
                    return builder;
                }
            }
        }
        return null;
    }

    @SuppressWarnings( "unchecked" )
    protected void setProperties( BuilderContext context, BPMNDefinition definition ) {
        assert definition != null;
        Bpmn2OryxPropertyManager propertyManager = context.getOryxManager().getPropertyManager();
        OryxIdMappings idMappings = context.getOryxManager().getMappingsManager();
        Set<?> defProperties = context.getDefinitionManager().adapters().forDefinition().getProperties( definition );
        for ( Map.Entry<String, String> entry : properties.entrySet() ) {
            final String oryxId = entry.getKey();
            if ( !idMappings.isSkipProperty( definition.getClass(), oryxId ) ) {
                final String pValue = entry.getValue();
                final String pId = idMappings.getPropertyId( definition, oryxId );
                boolean found = false;
                if ( null != pId ) {
                    final Object property = context.getGraphUtils().getProperty( defProperties, pId );
                    if ( null != property ) {
                        try {
                            PropertyType propertyType = context.getDefinitionManager().adapters().forProperty().getType( property );
                            Object value = propertyManager.parse( property, propertyType, pValue );
                            context.getDefinitionManager().adapters().forProperty().setValue( property, value );
                            found = true;

                        } catch ( Exception e ) {
                            LOG.error( "Cannot parse value [" + pValue + "] for property [" + pId + "]", e );

                        }

                    }

                }
                if ( !found && null != pId ) {
                    //LOG.warn( "Property [" + pId + "] not found for definition [" + definition.getClass().getName() + "]" );

                }

            }

        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( " [NodeId=" ).append( nodeId ).append( "] " );
        builder.append( " [properties=" ).append( properties ).append( "] " );
        builder.append( " [outgoingResourceIds=" ).append( outgoingResourceIds ).append( "] " );
        // Bounds.
        builder.append( " [boundUL=" ).append( null != boundUL ? ( "{" + boundUL[ 0 ] + ", " + boundUL[ 1 ] + "}" ) : "null" ).append( "] " );
        builder.append( " [boundLR=" ).append( null != boundLR ? ( "{" + boundLR[ 0 ] + ", " + boundLR[ 1 ] + "}" ) : "null" ).append( "] " );
        // Dockers.
        if ( !dockers.isEmpty() ) {
            builder.append( " [dockers=" );
            for ( Double[] docker : dockers ) {
                builder.append( " {" ).append( docker[ 0 ] ).append( ", " ).append( docker[ 1 ] ).append( "}" );
            }
            builder.append( "] " );
        } else {
            builder.append( " [dockers=null] " );
        }
        return builder.toString();
    }
}
