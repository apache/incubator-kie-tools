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

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.Glyph;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractCompositeShapeFactory<W, S extends Shape>
        extends AbstractShapeFactory<W, S>
        implements CompositeShapeFactory<W, AbstractCanvasHandler, S, ShapeFactory<W, AbstractCanvasHandler, S>> {

    protected final List<ShapeFactory<W, AbstractCanvasHandler, S>> factories = new LinkedList<>();

    DefinitionManager definitionManager;

    private AbstractCompositeShapeFactory() {
    }

    public AbstractCompositeShapeFactory( final DefinitionManager definitionManager ) {
        this.definitionManager = definitionManager;
    }

    public void addFactory( final ShapeFactory<W, AbstractCanvasHandler, S> factory ) {
        factories.add( factory );
    }

    @Override
    public boolean accepts( final String definitionId ) {
        for ( final ShapeFactory<W, AbstractCanvasHandler, S> factory : factories ) {
            if ( factory.accepts( definitionId ) ) {
                return true;
            }

        }
        return false;
    }

    @Override
    public String getDescription( final String definitionId ) {
        for ( final ShapeFactory<W, AbstractCanvasHandler, S> factory : factories ) {
            if ( factory.accepts( definitionId ) ) {
                return factory.getDescription( definitionId );

            }

        }
        throw new RuntimeException( "This factory supports Definition [" + definitionId + "] but cannot obtain the description for it." );
    }

    @Override
    public S build( final W definition,
                    final AbstractCanvasHandler context ) {
        final String id = definitionManager.adapters().forDefinition().getId( definition );
        for ( final ShapeFactory<W, AbstractCanvasHandler, S> factory : factories ) {
            if ( factory.accepts( id ) ) {
                return factory.build( definition, context );

            }

        }
        throw new RuntimeException( "This factory supports Definition [" + id + "] but cannot build the sthape for it." );

    }

    @Override
    public Glyph glyph( final String definitionId,
                        final double width,
                        final double height ) {
        for ( final ShapeFactory<W, AbstractCanvasHandler, S> factory : factories ) {
            if ( factory.accepts( definitionId ) ) {
                return factory.glyph( definitionId, width, height );

            }

        }
        throw new RuntimeException( "This factory supports Definition [" + definitionId + "] but cannot obtain the description for it." );
    }
}
