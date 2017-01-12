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

package org.kie.workbench.common.stunner.core.client.components.palette.model;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractPaletteDefinitionBuilder<T, P, E>
        implements PaletteDefinitionBuilder<T, P, E> {

    private final List<String> definitionExclusions = new LinkedList<>();
    private final List<String> categoryExclusions = new LinkedList<>();

    public AbstractPaletteDefinitionBuilder() {
    }

    protected String toValidId( final String s ) {
        return s;
    }

    @Override
    public PaletteDefinitionBuilder<T, P, E> excludeCategory( final String categoryId ) {
        this.categoryExclusions.add( categoryId );
        return this;
    }

    @Override
    public PaletteDefinitionBuilder<T, P, E> excludeDefinition( final String definitionId ) {
        this.definitionExclusions.add( definitionId );
        return this;
    }

    protected boolean isCategoryExcluded( final String categoryId ) {
        return this.categoryExclusions.contains( categoryId );
    }

    protected boolean isDefinitionExcluded( final String definitionId ) {
        return this.definitionExclusions.contains( definitionId );
    }

    @SuppressWarnings( "unchecked" )
    protected <I extends PaletteItemBuilder> I getItemBuilder( final List<I> items,
                                                               final String id ) {
        for ( final PaletteItemBuilder item : items ) {
            if ( item.getId().equals( id ) ) {
                return ( I ) item;
            }
        }
        return null;
    }
}
