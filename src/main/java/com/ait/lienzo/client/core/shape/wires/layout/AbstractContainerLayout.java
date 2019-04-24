/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ait.lienzo.client.core.shape.wires.layout;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.types.BoundingBox;

public abstract class AbstractContainerLayout<L> implements IContainerLayout<L>
{
    private final Map<IPrimitive, L> children;

    private final IPrimitive         parent;

    public AbstractContainerLayout(final IPrimitive parent)
    {
        if (parent == null)
        {
            throw new IllegalArgumentException("Parent cannot be null");
        }
        this.parent = parent;
        this.children = new HashMap<>();
    }

    private Map<IPrimitive, L> getChildren()
    {
        return children;
    }

    @Override
    public IContainerLayout add(final IPrimitive<?> child)
    {
        return add(child, getDefaultLayout());
    }

    @Override
    public IContainerLayout add(final IPrimitive<?> child, final L layout)
    {
        getChildren().put(child, getLayout(layout));
        return this;
    }

    @Override
    public void execute()
    {
        for (Entry<IPrimitive, L> e : getChildren().entrySet())
        {
            add(e.getKey(), e.getValue());
        }
    }

    @Override
    public L getLayout(final IPrimitive<?> child)
    {
        return getChildren().get(child);
    }

    @Override
    public IContainerLayout remove(final IPrimitive<?> child)
    {
        getChildren().remove(child);
        return this;
    }

    @Override
    public IContainerLayout clear()
    {
        getChildren().clear();
        return this;
    }

    public abstract L getDefaultLayout();

    protected L getLayout(final L layout)
    {
        return layout == null ? getDefaultLayout() : layout;
    }

    public BoundingBox getParentBoundingBox()
    {
        return parent.getBoundingBox();
    }
}
