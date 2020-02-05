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

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.toolbox.GroupItem;
import com.ait.lienzo.client.core.types.BoundingBox;

/**
 * A toolbox item that wraps a Group by using another Group.
 * The wrapped group instance is set to not listen for events,
 * on the other hand, on this instance's group, which do listen
 * for events, a path is added for being able to listen
 * as for its bounds.
 */
class FocusableGroup extends AbstractFocusableGroupItem<FocusableGroup> {

    private final MultiPath primitive;

    FocusableGroup(final Group group) {
        this(new GroupItem(),
             group);
    }

    FocusableGroup(final GroupItem groupItem,
                   final Group group) {
        super(groupItem);
        groupItem.asPrimitive().add(group);
        group.setListening(false);
        this.primitive = setUpGroupDecorator(new MultiPath(),
                                             getGroupItem().asPrimitive());
        getGroupItem().add(primitive);
        setupFocusingHandlers();
    }

    @Override
    public IPrimitive<?> getPrimitive() {
        return primitive;
    }

    private static MultiPath setUpGroupDecorator(final MultiPath primitive,
                                                 final Group group) {
        final BoundingBox boundingBox = group.getBoundingBox();
        final double width = boundingBox.getWidth();
        final double height = boundingBox.getHeight();
        return primitive
                .clear()
                .rect(0,
                      0,
                      width,
                      height)
                .setFillAlpha(0.01)
                .setStrokeAlpha(0.01)
                .setListening(true)
                .setFillBoundsForSelection(true)
                .moveToTop();
    }
}
