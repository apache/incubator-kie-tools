/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.client.shape.factory;

import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeGlyph;

public abstract class AbstractShapeFactory<W, S extends Shape> implements ShapeFactory<W, S> {

    protected abstract Glyph getGlyphFor(final String definitionId);

    protected abstract Glyph getGlyphFor(final String definitionId,
                                         final Class<? extends GlyphConsumer> consumer);

    @Override
    public Glyph getGlyph(final String definitionId) {
        final Glyph glyph = getGlyphFor(definitionId);
        initialiseGlyph(glyph, definitionId);
        return glyph;
    }

    @Override
    public Glyph getGlyph(final String definitionId,
                          final Class<? extends GlyphConsumer> consumer) {
        final Glyph glyph = getGlyphFor(definitionId, consumer);
        initialiseGlyph(glyph, definitionId);
        return glyph;
    }

    private void initialiseGlyph(final Glyph glyph,
                                 final String definitionId) {
        if (ShapeGlyph.class.equals(glyph.getClass())) {
            final ShapeGlyph shapeGlyph = (ShapeGlyph) glyph;
            shapeGlyph.setDefinitionId(definitionId);
            shapeGlyph.setFactorySupplier(() -> this);
        }
    }
}
