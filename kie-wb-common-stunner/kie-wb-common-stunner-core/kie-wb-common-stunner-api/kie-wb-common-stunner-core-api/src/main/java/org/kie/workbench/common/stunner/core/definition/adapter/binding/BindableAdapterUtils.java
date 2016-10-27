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

package org.kie.workbench.common.stunner.core.definition.adapter.binding;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.definition.adapter.exception.NotPojoTypeException;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;

import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

public class BindableAdapterUtils {

    private static Logger LOGGER = Logger.getLogger( BindableAdapterUtils.class.getName() );

    public static final String SHAPE_SET_SUFFIX = "ShapeSet";

    public static String getDefinitionId( final Class<?> type ) {
        return getDefinitionId( type, null );
    }

    public static String getDefinitionId( final Class<?> type,
                                          final AdapterRegistry registry ) {
        if ( null != registry &&
                !registry.getDefinitionAdapter( type ).isPojoModel() ) {
            throw new NotPojoTypeException( type );

        }
        return getGenericClassName( type );
    }

    public static String getDefinitionSetId( final Class<?> type ) {
        return getDefinitionSetId( type, null );
    }

    public static String getDefinitionSetId( final Class<?> type,
                                             final AdapterRegistry registry ) {
        if ( null != registry &&
                !registry.getDefinitionSetAdapter( type ).isPojoModel() ) {
            throw new NotPojoTypeException( type );

        }
        return getGenericClassName( type );
    }

    public static String getPropertySetId( final Class<?> type ) {
        return getPropertySetId( type, null );
    }

    public static String getPropertySetId( final Class<?> type,
                                           final DefinitionManager definitionManager ) {
        if ( null != definitionManager &&
                !definitionManager.adapters().registry().getPropertySetAdapter( type ).isPojoModel() ) {
            throw new NotPojoTypeException( type );

        }
        return getGenericClassName( type );
    }

    public static String getPropertyId( final Class<?> type,
                                        final DefinitionManager definitionManager ) {
        if ( null != definitionManager &&
                !definitionManager.adapters().registry().getPropertyAdapter( type ).isPojoModel() ) {
            throw new NotPojoTypeException( type );

        }
        return getGenericClassName( type );
    }

    public static String getPropertyId( final Class<?> type ) {
        return getPropertyId( type, null );
    }

    public static String getShapeSetId( final Class<?> shapeSetClass ) {
        return getGenericClassName( shapeSetClass );
    }

    public static String getGenericClassName( final Class<?> type ) {
        return type.getName();
    }

    private static String getGenericClassId( final Class<?> type ) {
        return type.getSimpleName();
    }

    public static <T> Collection<Class<?>> toClassCollection( final Iterable<T> source ) {
        if ( null != source && source.iterator().hasNext() ) {
            final LinkedList<Class<?>> result = new LinkedList<>();
            for ( final Object sourceObject : source ) {
                result.add( sourceObject.getClass() );

            }
            return result;

        }
        return null;

    }

}