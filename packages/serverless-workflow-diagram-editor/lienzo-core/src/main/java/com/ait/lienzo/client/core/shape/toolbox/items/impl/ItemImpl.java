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

package com.ait.lienzo.client.core.shape.toolbox.items.impl;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.toolbox.GroupItem;

class ItemImpl extends AbstractFocusableGroupItem<ItemImpl> {

    private final IPrimitive<?> primitive;

    ItemImpl(final Shape<?> shape) {
        this(new GroupItem(),
             shape);
    }

    ItemImpl(final GroupItem groupItem,
             final Shape<?> shape) {
        super(groupItem);
        this.primitive = shape;
        getGroupItem().add(primitive);
        setupFocusingHandlers();
    }

    @Override
    public IPrimitive<?> getPrimitive() {
        return primitive;
    }
}
