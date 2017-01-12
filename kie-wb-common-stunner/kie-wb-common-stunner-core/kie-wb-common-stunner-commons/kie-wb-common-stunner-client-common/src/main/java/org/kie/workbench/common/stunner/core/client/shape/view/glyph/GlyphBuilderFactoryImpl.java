/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.shape.view.glyph;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;

@ApplicationScoped
public class GlyphBuilderFactoryImpl implements GlyphBuilderFactory {

    private final ManagedInstance<GlyphBuilder> glyphBuilderInstances;
    private final List<GlyphBuilder> builders = new LinkedList<>();

    protected GlyphBuilderFactoryImpl() {
        this( null );
    }

    @Inject
    public GlyphBuilderFactoryImpl( final ManagedInstance<GlyphBuilder> glyphBuilderInstances ) {
        this.glyphBuilderInstances = glyphBuilderInstances;
    }

    @PostConstruct
    public void init() {
        glyphBuilderInstances.forEach( builders::add );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <D extends GlyphDef<?>> GlyphBuilder<?, D> getBuilder( final D glyphDefinition ) {
        final GlyphBuilder<?, D> result = _getBuilder( glyphDefinition );
        if ( null == result ) {
            throw new UnsupportedOperationException( "No glyph builder instance for glyph definition type ["
                                                             + glyphDefinition.getClass() + "]" );
        }
        return result;
    }

    @SuppressWarnings( "unchecked" )
    private <D extends GlyphDef<?>> GlyphBuilder<?, D> _getBuilder( final D glyphDefinition ) {
        return builders.stream()
                .filter( builder -> builder.getType().equals( glyphDefinition.getType() ) )
                .findFirst()
                .orElse( null );
    }
}
