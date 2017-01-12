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
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;

public interface GlyphBuilder<G, D extends GlyphDef<?>> {

    Class<?> getType();

    GlyphBuilder<G, D> definitionType( final Class<?> type );

    GlyphBuilder<G, D> glyphDef( final D glyphDef );

    GlyphBuilder<G, D> factory( final ShapeFactory factory );

    GlyphBuilder<G, D> width( final double width );

    GlyphBuilder<G, D> height( final double height );

    Glyph<G> build();
}
