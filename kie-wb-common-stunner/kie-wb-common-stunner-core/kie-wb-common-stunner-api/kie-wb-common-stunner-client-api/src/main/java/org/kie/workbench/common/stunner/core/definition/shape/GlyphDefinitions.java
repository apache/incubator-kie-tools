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

import java.util.LinkedHashMap;
import java.util.Map;

public class GlyphDefinitions {

    public static <W> GlyphShapeDefImpl<W> GLYPH_SHAPE() {
        return new GlyphShapeDefImpl<W>();
    }

    public static <W> GlyphShapeDefMap<W> GLYPH_SHAPE_MAPPING() {
        return new GlyphShapeDefMap<W>();
    }

    public static class GlyphShapeDefImpl<W> extends AbstractGlyphShapeDef<W> {

    }

    public static class GlyphShapeDefMap<W> extends AbstractGlyphShapeDef<W> {

        private final Map<Class<?>, String> MAPPINGS = new LinkedHashMap<>();

        @SuppressWarnings( "unchecked" )
        public <W> GlyphShapeDefMap<W> addMapping( final Class<?> type,
                                                   final String id ) {
            MAPPINGS.put( type,
                          id );
            return ( GlyphShapeDefMap<W> ) this;
        }

        @Override
        public String getGlyphDefinitionId( final Class<?> clazz ) {
            final String id = MAPPINGS.get( clazz );
            return null != id ? id : super.getGlyphDefinitionId( clazz );
        }
    }
}
