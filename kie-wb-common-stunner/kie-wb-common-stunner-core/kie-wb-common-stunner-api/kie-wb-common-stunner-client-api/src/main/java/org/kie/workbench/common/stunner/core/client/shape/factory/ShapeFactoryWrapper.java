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

package org.kie.workbench.common.stunner.core.client.shape.factory;

import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.Glyph;

public abstract class ShapeFactoryWrapper<W, C, S extends Shape> implements ShapeFactory<W, C, S> {

    protected abstract ShapeFactory getFactory();

    @Override
    public boolean accepts( final String definitionId ) {
        return getFactory().accepts( definitionId );
    }

    @Override
    public String getDescription( final String definitionId ) {
        return getFactory().getDescription( definitionId );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public S build( final W definition,
                    final C context ) {
        return ( S ) getFactory().build( definition, context );
    }

    @Override
    public Glyph glyph( final String definitionId,
                        final double width,
                        final double height ) {
        return getFactory().glyph( definitionId, width, height );
    }

}
