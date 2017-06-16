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

package org.kie.workbench.common.stunner.client.lienzo.components.toolbox.builder;

import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;
import org.kie.workbench.common.stunner.client.lienzo.components.toolbox.LienzoToolbox;
import org.kie.workbench.common.stunner.client.lienzo.components.toolbox.LienzoToolboxButtonGrid;
import org.kie.workbench.common.stunner.core.client.canvas.Layer;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxButton;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.lienzo.toolbox.Toolboxes;
import org.kie.workbench.common.stunner.lienzo.toolbox.builder.Button;
import org.kie.workbench.common.stunner.lienzo.toolbox.builder.ButtonGrid;
import org.kie.workbench.common.stunner.lienzo.toolbox.builder.ButtonsOrRegister;
import org.kie.workbench.common.stunner.lienzo.toolbox.builder.On;
import org.kie.workbench.common.stunner.lienzo.toolbox.event.ToolboxButtonEventHandler;

public class LienzoToolboxBuilderImpl
        implements LienzoToolboxBuilder<LienzoToolboxBuilderImpl> {

    private Layer layer;
    private LienzoToolbox toolbox;
    private On on;
    private ButtonGrid buttonGrid;
    private ButtonsOrRegister buttonsOrRegister;
    private int padding = 5;
    private int iconSize = 12;

    @Override
    public LienzoToolboxBuilderImpl forLayer(final Layer layer) {
        this.layer = layer;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public LienzoToolboxBuilderImpl forView(final ShapeView<?> view) {
        if (view instanceof WiresShape) {
            final LienzoLayer lienzoLayerImpl = (LienzoLayer) this.layer;
            final com.ait.lienzo.client.core.shape.Layer lienzoLayer = lienzoLayerImpl.getLienzoLayer();
            on = Toolboxes.staticToolBoxFor(lienzoLayer,
                                            (WiresShape) view);
            if (view instanceof HasEventHandlers) {
                final HasEventHandlers<?, Shape<?>> hasEventHandlers = (HasEventHandlers) view;
                if (null != hasEventHandlers.getAttachableShape()) {
                    on.attachTo(hasEventHandlers.getAttachableShape());
                }
            }
        }
        return this;
    }

    @Override
    public LienzoToolboxBuilderImpl direction(final Direction ond,
                                              final Direction towards) {
        final com.ait.lienzo.shared.core.types.Direction dOn = getDirection(ond);
        final com.ait.lienzo.shared.core.types.Direction dT = getDirection(towards);
        buttonGrid = on.on(dOn).towards(dT);
        return this;
    }

    @Override
    public LienzoToolboxBuilderImpl grid(final LienzoToolboxButtonGrid grid) {
        this.padding = grid.getPadding();
        this.iconSize = grid.getButtonSize();
        buttonsOrRegister = buttonGrid.grid(grid.getPadding(),
                                            grid.getButtonSize(),
                                            grid.getRows(),
                                            grid.getColumns());
        return this;
    }

    @Override
    public LienzoToolboxBuilderImpl add(final ToolboxButton<Shape<?>> button) {
        Button b = buttonsOrRegister.add(button.getIcon());
        b.setIconSize(iconSize).setPadding(padding);
        if (null != button.getClickHandler()) {
            b.setClickHandler(buildHandler(button.getClickHandler()));
        }
        if (null != button.getMouseDownHandler()) {
            b.setMouseDownHandler(buildHandler(button.getMouseDownHandler()));
        }
        if (null != button.getMouseEnterHandler()) {
            b.setMouseEnterHandler(buildHandler(button.getMouseEnterHandler()));
        }
        if (null != button.getMouseExitHandler()) {
            b.setMouseExitHandler(buildHandler(button.getMouseExitHandler()));
        }
        b.end();
        return this;
    }

    private ToolboxButtonEventHandler buildHandler(final org.kie.workbench.common.stunner.core.client.components.toolbox.event.ToolboxButtonEventHandler handler) {
        return event -> handler.fire(new org.kie.workbench.common.stunner.core.client.components.toolbox.event.ToolboxButtonEvent() {

            @Override
            public int getX() {
                return event.getX();
            }

            @Override
            public int getY() {
                return event.getY();
            }

            @Override
            public int getAbsoluteX() {
                return event.getAbsoluteX();
            }

            @Override
            public int getAbsoluteY() {
                return event.getAbsoluteY();
            }

            @Override
            public int getClientX() {
                return event.getClientX();
            }

            @Override
            public int getClientY() {
                return event.getClientY();
            }
        });
    }

    @Override
    public LienzoToolbox build() {
        if (null != buttonsOrRegister) {
            toolbox = new LienzoToolbox(buttonsOrRegister.register());
        } else {
            throw new RuntimeException("No buttons added for toolbox.");
        }
        return toolbox;
    }

    private com.ait.lienzo.shared.core.types.Direction getDirection(final Direction direction) {
        switch (direction) {
            case EAST:
                return com.ait.lienzo.shared.core.types.Direction.EAST;
            case WEST:
                return com.ait.lienzo.shared.core.types.Direction.WEST;
            case NORTH:
                return com.ait.lienzo.shared.core.types.Direction.NORTH;
            case SOUTH:
                return com.ait.lienzo.shared.core.types.Direction.SOUTH;
            case NORTH_EAST:
                return com.ait.lienzo.shared.core.types.Direction.NORTH_EAST;
            case NORTH_WEST:
                return com.ait.lienzo.shared.core.types.Direction.NORTH_WEST;
            case SOUTH_EAST:
                return com.ait.lienzo.shared.core.types.Direction.SOUTH_EAST;
            case SOUTH_WEST:
                return com.ait.lienzo.shared.core.types.Direction.SOUTH_WEST;
        }
        throw new UnsupportedOperationException("Toolbox direction [" + direction.name() + "] not supported.");
    }
}
