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

package org.kie.workbench.common.stunner.core.client.shape.view;

import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.shape.MutableShape;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.UUID;

public abstract class AbstractShapeGlyphBuilder<G> implements ShapeGlyphBuilder<G> {

    protected String id;
    protected ShapeFactory factory;
    protected double width;
    protected double height;

    protected abstract FactoryManager getFactoryManager();

    protected abstract ShapeGlyph<G> doBuild( Shape<?> shape );

    @Override
    public ShapeGlyphBuilder<G> definition( final String id ) {
        this.id = id;
        return this;
    }

    @Override
    public ShapeGlyphBuilder<G> glyphProxy( final GlyphDef<?> glyphProxy,
                                            final String id ) {
        this.id = glyphProxy.getGlyphDefinitionId( id );
        return this;
    }

    @Override
    public ShapeGlyphBuilder<G> factory( final ShapeFactory factory ) {
        this.factory = factory;
        return this;
    }

    @Override
    public ShapeGlyphBuilder<G> width( final double width ) {
        this.width = width;
        return this;
    }

    @Override
    public ShapeGlyphBuilder<G> height( final double height ) {
        this.height = height;
        return this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public ShapeGlyph<G> build() {
        final Element<View<Object>> element =
                ( Element<View<Object>> ) getFactoryManager().newElement( UUID.uuid(), id );
        final Object definition = element.getContent().getDefinition();
        Shape<?> shape = factory.build( definition, null );
        if ( shape instanceof MutableShape ) {
            ( ( MutableShape ) shape ).applyProperties( element, MutationContext.STATIC );

        }
        return doBuild( shape );
    }

}
