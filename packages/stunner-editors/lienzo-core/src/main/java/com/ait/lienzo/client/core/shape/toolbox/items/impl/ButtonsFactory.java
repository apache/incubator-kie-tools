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
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.toolbox.ToolboxVisibilityExecutors;
import com.ait.lienzo.client.core.shape.toolbox.items.ButtonGridItem;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.Direction;

public class ButtonsFactory {

    public static ButtonsFactory INSTANCE = new ButtonsFactory();

    private ButtonsFactory() {
    }

    public ButtonItemImpl button(final Shape<?> prim) {
        return new ButtonItemImpl(prim);
    }

    public ButtonItemImpl button(final Group group) {
        return new ButtonItemImpl(group);
    }

    public ButtonGridItem dropDown(final Shape<?> shape) {
        final ButtonGridItemImpl button = new ButtonGridItemImpl(shape);
        return setupAsDropDown(button);
    }

    public ButtonGridItem dropDown(final Group group) {
        final ButtonGridItemImpl button = new ButtonGridItemImpl(group);
        return setupAsDropDown(button);
    }

    public ButtonGridItem dropRight(final Shape<?> shape) {
        final ButtonGridItemImpl button = new ButtonGridItemImpl(shape);
        return setupAsDropRight(button);
    }

    public ButtonGridItem dropRight(final Group group) {
        final ButtonGridItemImpl button = new ButtonGridItemImpl(group);
        return setupAsDropRight(button);
    }

    private static ButtonGridItem setupAsDropDown(final ButtonGridItemImpl button) {
        button
                .at(Direction.SOUTH_WEST)
                .offset(new Point2D(0,
                                    5))
                .useShowExecutor(ToolboxVisibilityExecutors.upScaleY())
                .useHideExecutor(ToolboxVisibilityExecutors.downScaleY());
        return button;
    }

    private static ButtonGridItem setupAsDropRight(final ButtonGridItemImpl button) {
        button
                .at(Direction.NORTH_EAST)
                .offset(new Point2D(5,
                                    0))
                .useShowExecutor(ToolboxVisibilityExecutors.upScaleX())
                .useHideExecutor(ToolboxVisibilityExecutors.downScaleX());
        return button;
    }
}
