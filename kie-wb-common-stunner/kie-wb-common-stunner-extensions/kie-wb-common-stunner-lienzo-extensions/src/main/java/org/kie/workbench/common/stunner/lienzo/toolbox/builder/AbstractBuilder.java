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

package org.kie.workbench.common.stunner.lienzo.toolbox.builder;

import java.util.ArrayList;
import java.util.List;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.shared.core.types.Direction;
import org.kie.workbench.common.stunner.lienzo.toolbox.ToolboxButton;
import org.kie.workbench.common.stunner.lienzo.toolbox.event.ToolboxButtonEventHandler;
import org.kie.workbench.common.stunner.lienzo.toolbox.grid.GridToolbox;

public abstract class AbstractBuilder implements On,
                                                 Towards,
                                                 ButtonsOrRegister,
                                                 ButtonGrid {

    protected final Layer layer;
    protected final WiresShape shape;
    protected Shape<?> attachTo;
    protected Direction anchor;
    protected Direction towards;
    protected List<ToolboxButton> buttons = new ArrayList<>();
    protected int cols;
    protected int rows;
    protected int padding;
    protected int iconSize;

    public AbstractBuilder( Layer layer,
                            WiresShape shape ) {
        this.layer = layer;
        this.shape = shape;
    }

    @Override
    public Towards on( Direction anchor ) {
        this.anchor = anchor;
        return this;
    }

    @Override
    public On attachTo( Shape<?> shape ) {
        this.attachTo = shape;
        return this;
    }

    @Override
    public ButtonGrid towards( Direction towards ) {
        this.towards = towards;
        return this;
    }

    @Override
    public ButtonsOrRegister add( ToolboxButton button ) {
        this.buttons.add( button );
        return this;
    }

    @Override
    public ButtonsOrRegister grid( int padding,
                                   int iconSize,
                                   int rows,
                                   int cols ) {
        this.rows = rows;
        this.cols = cols;
        this.padding = padding;
        this.iconSize = iconSize;
        return this;
    }

    @Override
    public Button add( IPrimitive<?> iconShape ) {
        return new ButtonBuilder( this,
                                  iconShape );
    }

    @Override
    public abstract GridToolbox register();

    public static class ButtonBuilder implements Button {

        private final AbstractBuilder builder;
        private final IPrimitive<?> shape;
        private int padding;
        private int iconSize;
        private ToolboxButtonEventHandler clickHandler;
        private ToolboxButtonEventHandler moveDownHandler;
        private ToolboxButtonEventHandler mouseEnterHandler;
        private ToolboxButtonEventHandler mouseExitHandler;

        public ButtonBuilder( final AbstractBuilder builder,
                              final IPrimitive<?> shape ) {
            this.builder = builder;
            this.shape = shape;
        }

        public ButtonBuilder setPadding( final int padding ) {
            this.padding = padding;
            return this;
        }

        public ButtonBuilder setIconSize( final int size ) {
            this.iconSize = size;
            return this;
        }

        @Override
        public Button setClickHandler( final ToolboxButtonEventHandler handler ) {
            this.clickHandler = handler;
            return this;
        }

        @Override
        public Button setMouseDownHandler( final ToolboxButtonEventHandler handler ) {
            this.moveDownHandler = handler;
            return this;
        }

        @Override
        public Button setMouseEnterHandler( final ToolboxButtonEventHandler handler ) {
            this.mouseEnterHandler = handler;
            return this;
        }

        @Override
        public Button setMouseExitHandler( final ToolboxButtonEventHandler handler ) {
            this.mouseExitHandler = handler;
            return this;
        }

        @Override
        public ButtonsOrRegister end() {
            builder.add( new ToolboxButton( builder.layer,
                                            shape,
                                            padding,
                                            iconSize,
                                            clickHandler,
                                            moveDownHandler,
                                            mouseEnterHandler,
                                            mouseExitHandler ) );
            return builder;
        }
    }
}
