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

package org.kie.workbench.common.stunner.core.client.components.palette.model;

public abstract class AbstractPaletteItemBuilder<B, I> implements PaletteItemBuilder<B, I> {

    protected final String id;
    protected String title;
    protected String description;
    protected String tooltip;

    public AbstractPaletteItemBuilder( final String id ) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public B title( final String title ) {
        this.title = title;
        return ( B ) this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public B description( final String description ) {
        this.description = description;
        return ( B ) this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public B tooltip( final String tooltip ) {
        this.tooltip = tooltip;
        return ( B ) this;
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof AbstractPaletteItemBuilder ) ) {
            return false;
        }
        AbstractPaletteItemBuilder that = ( AbstractPaletteItemBuilder ) o;
        return id != null && id.equals( that.id );
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : ~~id.hashCode();
    }
}
