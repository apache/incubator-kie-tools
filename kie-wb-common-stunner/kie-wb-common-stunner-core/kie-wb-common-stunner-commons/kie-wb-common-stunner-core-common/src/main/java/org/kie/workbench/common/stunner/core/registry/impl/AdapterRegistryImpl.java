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

package org.kie.workbench.common.stunner.core.registry.impl;

import org.kie.workbench.common.stunner.core.api.AbstractDefinitionManager;
import org.kie.workbench.common.stunner.core.definition.adapter.*;
import org.kie.workbench.common.stunner.core.definition.adapter.exception.AdapterNotFoundException;
import org.kie.workbench.common.stunner.core.registry.DynamicRegistry;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;

import java.util.*;
import java.util.logging.Logger;

public class AdapterRegistryImpl implements AdapterRegistry, DynamicRegistry<Adapter> {

    private static Logger LOGGER = Logger.getLogger( AbstractDefinitionManager.class.getName() );

    private final List<DefinitionSetAdapter> definitionSetAdapters = new LinkedList<>();
    private final List<DefinitionSetRuleAdapter> definitionSetRuleAdapters = new LinkedList<>();
    private final List<DefinitionAdapter> definitionAdapters = new LinkedList<>();
    private final List<PropertySetAdapter> propertySetAdapters = new ArrayList<PropertySetAdapter>();
    private final List<PropertyAdapter> propertyAdapters = new LinkedList<>();
    private final List<MorphAdapter> morphAdapters = new LinkedList<>();

    @Override
    @SuppressWarnings( "unchecked" )
    public <T> DefinitionSetAdapter<T> getDefinitionSetAdapter( final Class<?> type ) {
        for ( DefinitionSetAdapter adapter : definitionSetAdapters ) {
            if ( adapter.accepts( type ) ) {
                return adapter;
            }
        }
        return nullHandling( DefinitionSetAdapter.class, type );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T> DefinitionSetRuleAdapter<T> getDefinitionSetRuleAdapter( final Class<?> type ) {
        for ( DefinitionSetRuleAdapter adapter : definitionSetRuleAdapters ) {
            if ( adapter.accepts( type ) ) {
                return adapter;
            }
        }
        return nullHandling( DefinitionSetRuleAdapter.class, type );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T> DefinitionAdapter<T> getDefinitionAdapter( final Class<?> type ) {
        for ( DefinitionAdapter adapter : definitionAdapters ) {
            if ( adapter.accepts( type ) ) {
                return adapter;
            }
        }
        return nullHandling( DefinitionAdapter.class, type );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T> PropertySetAdapter<T> getPropertySetAdapter( final Class<?> type ) {
        for ( PropertySetAdapter adapter : propertySetAdapters ) {
            if ( adapter.accepts( type ) ) {
                return adapter;
            }
        }
        return nullHandling( PropertySetAdapter.class, type );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T> PropertyAdapter<T, ?> getPropertyAdapter( final Class<?> type ) {
        for ( PropertyAdapter adapter : propertyAdapters ) {
            if ( adapter.accepts( type ) ) {
                return adapter;
            }
        }
        return nullHandling( PriorityAdapter.class, type );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T> MorphAdapter<T> getMorphAdapter( final Class<?> type ) {
        for ( MorphAdapter adapter : morphAdapters ) {
            if ( adapter.accepts( type ) ) {
                return adapter;

            }

        }
        return null;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public void register( final Adapter item ) {
        boolean registered = false;
        if ( item instanceof DefinitionSetAdapter ) {
            definitionSetAdapters.add( ( DefinitionSetAdapter ) item );
            sortAdapters( definitionSetAdapters );
            registered = true;

        } else if ( item instanceof DefinitionSetRuleAdapter ) {
            definitionSetRuleAdapters.add( ( DefinitionSetRuleAdapter ) item );
            sortAdapters( definitionSetRuleAdapters );
            registered = true;

        } else if ( item instanceof DefinitionAdapter ) {
            definitionAdapters.add( ( DefinitionAdapter ) item );
            sortAdapters( definitionAdapters );
            registered = true;

        } else if ( item instanceof PropertySetAdapter ) {
            propertySetAdapters.add( ( PropertySetAdapter ) item );
            sortAdapters( propertySetAdapters );
            registered = true;

        } else if ( item instanceof PropertyAdapter ) {
            propertyAdapters.add( ( PropertyAdapter ) item );
            sortAdapters( propertyAdapters );
            registered = true;

        } else if ( item instanceof MorphAdapter ) {
            morphAdapters.add( ( MorphAdapter ) item );
            registered = true;

        }
        if ( !registered ) {
            final String em = "Cannot register Adapter for type [" + item.getClass().getName() + "]. Type not supported.";
            LOGGER.severe( em );
            throw new IllegalArgumentException( em );

        }

    }

    @Override
    @SuppressWarnings( "unchecked" )
    public boolean contains( final Adapter item ) {
        if ( item instanceof DefinitionSetAdapter ) {
            return definitionSetAdapters.contains( item );

        } else if ( item instanceof DefinitionSetRuleAdapter ) {
            return definitionSetRuleAdapters.contains( item );

        } else if ( item instanceof DefinitionAdapter ) {
            return definitionAdapters.contains( item );

        } else if ( item instanceof PropertySetAdapter ) {
            return propertySetAdapters.contains( item );

        } else if ( item instanceof PropertyAdapter ) {
            return propertyAdapters.contains( item );

        } else if ( item instanceof MorphAdapter ) {
            return morphAdapters.contains( item );

        }
        return false;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public boolean remove( final Adapter item ) {
        if ( item instanceof DefinitionSetAdapter ) {
            return definitionSetAdapters.remove( item );

        } else if ( item instanceof DefinitionSetRuleAdapter ) {
            return definitionSetRuleAdapters.remove( item );

        } else if ( item instanceof DefinitionAdapter ) {
            return definitionAdapters.remove( item );

        } else if ( item instanceof PropertySetAdapter ) {
            return propertySetAdapters.remove( item );

        } else if ( item instanceof PropertyAdapter ) {
            return propertyAdapters.remove( item );

        } else if ( item instanceof MorphAdapter ) {
            return morphAdapters.remove( item );

        }
        return false;

    }

    private static <T extends PriorityAdapter> void sortAdapters( List<T> adapters ) {
        Collections.sort( adapters, ( o1, o2 ) -> o1.getPriority() - o2.getPriority() );
    }

    private <T> T nullHandling( Class<? extends Adapter> adapterType, Class<?> type ) {
        final AdapterNotFoundException exception = new AdapterNotFoundException( adapterType, type );
        LOGGER.severe( exception.getMessage() );
        throw exception;
    }

}
