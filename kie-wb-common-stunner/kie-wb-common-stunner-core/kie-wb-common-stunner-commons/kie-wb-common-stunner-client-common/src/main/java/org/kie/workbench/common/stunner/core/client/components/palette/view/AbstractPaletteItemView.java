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

package org.kie.workbench.common.stunner.core.client.components.palette.view;

import org.kie.workbench.common.stunner.core.client.components.palette.model.PaletteItem;

public abstract class AbstractPaletteItemView<I extends PaletteItem, V>
        implements PaletteItemView<I, V> {

    protected final I item;

    public AbstractPaletteItemView( final I item ) {
        this.item = item;
    }

    @Override
    public I getPaletteItem() {
        return item;
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof AbstractPaletteItemView ) ) {
            return false;
        }
        AbstractPaletteItemView that = ( AbstractPaletteItemView ) o;
        return item != null && item.getId().equals( that.item.getId() );
    }

    @Override
    public int hashCode() {
        return item.getId() == null ? 0 : ~~item.getId().hashCode();
    }
}
