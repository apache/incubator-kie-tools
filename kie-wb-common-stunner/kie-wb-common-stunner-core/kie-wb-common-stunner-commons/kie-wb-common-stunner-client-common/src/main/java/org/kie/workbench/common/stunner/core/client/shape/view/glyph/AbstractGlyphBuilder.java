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

import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;

public abstract class AbstractGlyphBuilder<G, D extends GlyphDef<?>> implements GlyphBuilder<G, D> {

    protected String defId;
    protected Class<?> type;
    protected D glyphDefinition;
    protected ShapeFactory factory;
    protected double width;
    protected double height;

    @Override
    public GlyphBuilder<G, D> definitionType( final Class<?> type ) {
        this.type = type;
        this.defId = BindableAdapterUtils.getDefinitionId( type );
        return this;
    }

    public GlyphBuilder<G, D> definitionId( final String id ) {
        this.defId = id;
        return this;
    }

    @Override
    public GlyphBuilder<G, D> glyphDef( final D glyphDef ) {
        this.glyphDefinition = glyphDef;
        return this;
    }

    @Override
    public GlyphBuilder<G, D> factory( final ShapeFactory factory ) {
        this.factory = factory;
        return this;
    }

    @Override
    public GlyphBuilder<G, D> width( final double width ) {
        this.width = width;
        return this;
    }

    @Override
    public GlyphBuilder<G, D> height( final double height ) {
        this.height = height;
        return this;
    }
}
