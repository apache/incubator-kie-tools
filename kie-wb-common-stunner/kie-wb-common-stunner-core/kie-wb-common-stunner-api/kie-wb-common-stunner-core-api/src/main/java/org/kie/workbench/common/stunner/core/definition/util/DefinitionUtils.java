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

package org.kie.workbench.common.stunner.core.definition.util;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.MorphAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.HasInheritance;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.definition.morph.MorphPolicy;
import org.kie.workbench.common.stunner.core.factory.graph.EdgeFactory;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.registry.factory.FactoryRegistry;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class DefinitionUtils {

    DefinitionManager definitionManager;
    FactoryManager factoryManager;

    protected DefinitionUtils() {
        this( null, null );
    }

    @Inject
    @SuppressWarnings( "all" )
    public DefinitionUtils( final DefinitionManager definitionManager,
                            final FactoryManager factoryManager ) {
        this.definitionManager = definitionManager;
        this.factoryManager = factoryManager;
    }

    public <T> Object getProperty( final T definition, final String propertyId ) {
        final Set<?> properties = definitionManager.adapters().forDefinition().getProperties( definition );
        if ( null != properties && !properties.isEmpty() ) {
            for ( final Object property : properties ) {
                final String pId = definitionManager.adapters().forProperty().getId( property );
                if ( pId.equals( propertyId ) ) {
                    return property;

                }

            }

        }
        return null;

    }

    public <T> String getName( final T definition ) {
        final Object name = definitionManager.adapters().forDefinition().getNameProperty( definition );
        if ( null != name ) {
            return ( String ) definitionManager.adapters().forProperty().getValue( name );

        }
        return null;
    }

    public <T> String getNameIdentifier( final T definition ) {
        final Object name = definitionManager.adapters().forDefinition().getNameProperty( definition );
        if ( null != name ) {
            return definitionManager.adapters().forProperty().getId( name );

        }
        return null;
    }

    public <T> MorphDefinition getMorphDefinition( final T definition ) {
        final MorphAdapter<Object> adapter = definitionManager.adapters().registry().getMorphAdapter( definition.getClass() );
        final Iterable<MorphDefinition> definitions = adapter.getMorphDefinitions( definition );
        if ( null != definitions && definitions.iterator().hasNext() ) {
            return definitions.iterator().next();

        }
        return null;

    }

    /**
     * Returns the identifiers for the defintion type and its parent, if any.
     */
    public <T> String[] getDefinitionIds( final T definition ) {
        final Class<?> type = definition.getClass();
        final DefinitionAdapter<Object> definitionAdapter = definitionManager.adapters().registry().getDefinitionAdapter( type );
        final String definitionId = definitionAdapter.getId( definition );
        String baseId = null;
        if ( definitionAdapter instanceof HasInheritance ) {
            baseId = ( ( HasInheritance ) definitionAdapter ).getBaseType( type );

        }
        return new String[]{ definitionId, baseId };

    }

    public String getDefaultConnectorId( final String definitionSetId ) {
        final Object defSet = getDefinitionManager().definitionSets().getDefinitionSetById( definitionSetId );
        if ( null != defSet ) {
            final Set<String> definitions = definitionManager.adapters().forDefinitionSet().getDefinitions( defSet );
            if ( null != definitions && !definitions.isEmpty() ) {
                for ( final String defId : definitions ) {
                    // TODO: Find a way to have a default connector for a DefSet or at least do not create objects here.
                    final Object def = factoryManager.newDefinition( defId );
                    if ( null != def ) {
                        final Class<? extends ElementFactory> graphElement = definitionManager.adapters().forDefinition().getGraphFactoryType( def );
                        if ( isEdgeFactory( graphElement, factoryManager.registry() ) ) {
                            return defId;

                        }

                    }

                }

            }

        }
        return null;
    }

    public boolean isAllPolicy( final MorphDefinition definition ) {
        return MorphPolicy.ALL.equals( definition.getPolicy() );
    }

    public boolean isNonePolicy( final MorphDefinition definition ) {
        return MorphPolicy.NONE.equals( definition.getPolicy() );
    }

    public boolean isDefaultPolicy( final MorphDefinition definition ) {
        return MorphPolicy.DEFAULT.equals( definition.getPolicy() );
    }

    public DefinitionManager getDefinitionManager() {
        return definitionManager;
    }

    /**
     * Returns all properties from Definition's property sets.
     */
    public Set<?> getPropertiesFromPropertySets( final Object definition ) {
        final Set<Object> properties = new HashSet<>();
        // And properties on each definition's PropertySet instance.
        final Set<?> propertySets = definitionManager.adapters().forDefinition().getPropertySets( definition );
        if ( null != propertySets && !propertySets.isEmpty() ) {
            for ( Object propertySet : propertySets ) {
                final Set<?> setProperties = definitionManager.adapters().forPropertySet().getProperties( propertySet );
                if ( null != setProperties && !setProperties.isEmpty() ) {
                    for ( final Object property : setProperties ) {
                        if ( null != property ) {
                            properties.add( property );
                        }
                    }
                }
            }
        }
        return properties;
    }

    @SuppressWarnings( "unchecked" )
    public Object getPropertyAllowedValue( final Object property,
                                           final String value ) {
        final Map<Object, String> allowedValues = definitionManager.adapters().forProperty().getAllowedValues( property );
        if ( null != value && null != allowedValues && !allowedValues.isEmpty() ) {
            for ( final Map.Entry<Object, String> entry : allowedValues.entrySet() ) {
                final String v = entry.getValue();
                if ( value.equals( v ) ) {
                    return entry.getKey();

                }

            }

        }
        return null;
    }

    public static boolean isNodeFactory( final Class<? extends ElementFactory> graphFactoryClass,
                                         final FactoryRegistry registry ) {
        if ( !graphFactoryClass.equals( NodeFactory.class ) ) {
            ElementFactory factory = registry.getElementFactory( graphFactoryClass );
            return factory instanceof NodeFactory;

        }
        return true;

    }

    public static boolean isEdgeFactory( final Class<? extends ElementFactory> graphFactoryClass,
                                         final FactoryRegistry registry ) {
        if ( !graphFactoryClass.equals( EdgeFactory.class ) ) {
            ElementFactory factory = registry.getElementFactory( graphFactoryClass );
            return factory instanceof EdgeFactory;

        }
        return true;

    }

}
