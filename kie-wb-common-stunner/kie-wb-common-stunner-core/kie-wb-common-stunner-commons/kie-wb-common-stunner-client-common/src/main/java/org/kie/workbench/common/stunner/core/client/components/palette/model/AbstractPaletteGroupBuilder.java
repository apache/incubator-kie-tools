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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractPaletteGroupBuilder<B, G, I> extends AbstractPaletteItemBuilder<B, G>
        implements PaletteGroupBuilder<B, G, PaletteItemBuilder<?, I>> {

    protected String definitionId;
    protected final List<PaletteItemBuilder<?, I>> items = new ArrayList<PaletteItemBuilder<?, I>>();

    public AbstractPaletteGroupBuilder(final String id) {
        super(id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public B addItem(final PaletteItemBuilder item) {
        items.add(item);
        return (B) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public B addItem(final int index,
                     final PaletteItemBuilder item) {
        if (index < items.size()) {
            items.add(index,
                      item);
            return (B) this;
        } else {
            return addItem(item);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public PaletteItemBuilder getItem(final String id) {
        for (final PaletteItemBuilder<?, I> item : items) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    public AbstractPaletteGroupBuilder<B, G, I> definitionId(final String definitionId) {
        this.definitionId = definitionId;
        return this;
    }

    protected abstract G doBuild(List<I> items);

    @Override
    public G build() {
        final List<I> result = new LinkedList<>();
        for (final PaletteItemBuilder<?, I> itemBuilder : items) {
            result.add(itemBuilder.build());
        }
        return doBuild(result);
    }
}
