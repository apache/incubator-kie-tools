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

import com.ait.lienzo.client.core.shape.toolbox.items.decorator.DecoratorsFactory;
import com.ait.lienzo.client.core.shape.toolbox.items.tooltip.TooltipFactory;
import com.ait.lienzo.client.core.shape.wires.WiresShape;

public class ToolboxFactory {

    public static ToolboxFactory INSTANCE = new ToolboxFactory();

    private ToolboxFactory() {
    }

    public WiresShapeToolbox forWiresShape(final WiresShape shape) {
        return new WiresShapeToolbox(shape);
    }

    public ButtonsFactory buttons() {
        return ButtonsFactory.INSTANCE;
    }

    public DecoratorsFactory decorators() {
        return DecoratorsFactory.INSTANCE;
    }

    public TooltipFactory tooltips() {
        return TooltipFactory.INSTANCE;
    }
}
