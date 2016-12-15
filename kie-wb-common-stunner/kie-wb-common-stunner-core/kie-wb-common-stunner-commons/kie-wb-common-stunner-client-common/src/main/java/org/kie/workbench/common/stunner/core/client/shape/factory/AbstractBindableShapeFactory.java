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

package org.kie.workbench.common.stunner.core.client.shape.factory;

import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.Glyph;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;

import java.util.Set;

public abstract class AbstractBindableShapeFactory<W, S extends Shape> extends AbstractShapeFactory<W, S> {

    public abstract Set<Class<?>> getSupportedModelClasses();

    protected abstract String getDescription( Class<?> clazz );

    protected abstract Glyph glyph( Class<?> clazz, double width, double height );

    @Override
    public Glyph glyph( final String definitionId,
                        final double width,
                        final double height ) {
        return glyph( getDefinitionClass( definitionId ), width, height );
    }

    @Override
    public boolean accepts( final String definitionId ) {
        final Set<Class<?>> supportedClasses = getSupportedModelClasses();
        if ( null != supportedClasses && !supportedClasses.isEmpty() ) {
            for ( final Class<?> supportedClass : supportedClasses ) {
                final String _id = BindableAdapterUtils.getDefinitionId( supportedClass );
                if ( _id.equals( definitionId ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getDescription( final String definitionId ) {
        final Class<?> clazz = getDefinitionClass( definitionId );
        return getDescription( clazz );
    }

    protected String getDefinitionId( final Class<?> definitionClazz ) {
        return BindableAdapterUtils.getDefinitionId( definitionClazz );
    }

    protected Class<?> getDefinitionClass( final String definitionId ) {
        final Set<Class<?>> supportedClasses = getSupportedModelClasses();
        if ( null != supportedClasses && !supportedClasses.isEmpty() ) {
            for ( final Class<?> supportedClass : supportedClasses ) {
                final String _id = BindableAdapterUtils.getDefinitionId( supportedClass );
                if ( _id.equals( definitionId ) ) {
                    return supportedClass;
                }
            }
        }
        return null;
    }

}
