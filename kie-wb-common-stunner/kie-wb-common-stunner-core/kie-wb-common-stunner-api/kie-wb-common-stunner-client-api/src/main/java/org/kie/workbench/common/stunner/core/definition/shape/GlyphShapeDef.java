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

package org.kie.workbench.common.stunner.core.definition.shape;

/**
 * Glyph is built from an existing shape.
 * @param <W> The bean type.
 */
public abstract class GlyphShapeDef<W> implements GlyphDef<W> {

    /**
     * Considering the resulting glyph as the thumbnail for a given shape,
     * this method must return the identifier for the shape's Definition.
     * <p/>
     * For example, when glyph for a task bean type has to be displayed
     * somewhere on the screen, the shape factory calls this method
     * using as argument the identifier for the Task definition - here
     * can return the identifier for any shape's definition that will
     * be displayed for a task.
     */
    public abstract String getGlyphDefinitionId( String id );

    @Override
    public Class<?> getType() {
        return GlyphShapeDef.class;
    }

    @Override
    public String getGlyphDescription( final W element ) {
        return null;
    }
}
