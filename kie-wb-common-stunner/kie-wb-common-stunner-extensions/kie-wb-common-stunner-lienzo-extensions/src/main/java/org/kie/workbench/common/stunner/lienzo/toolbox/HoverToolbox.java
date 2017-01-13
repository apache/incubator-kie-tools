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

package org.kie.workbench.common.stunner.lienzo.toolbox;

import java.util.List;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.shared.core.types.Direction;
import com.google.gwt.event.shared.HandlerRegistration;
import org.kie.workbench.common.stunner.lienzo.toolbox.builder.AbstractBuilder;
import org.kie.workbench.common.stunner.lienzo.toolbox.grid.GridToolbox;

public class HoverToolbox extends AbstractToolbox {

    private final HoverTimer hoverTimer = new HoverTimer(new HoverTimer.Actions() {
        @Override
        public void onMouseEnter() {
            HoverToolbox.this.show();
        }

        @Override
        public void onMouseExit() {
            HoverToolbox.this.hide();
        }

        @Override
        public boolean isReadyToHide() {
            return HoverToolbox.this.showing;
        }
    });

    private boolean showing;

    @Override
    protected void registerButton(final ToolboxButton button) {
        super.registerButton(button);
        HandlerRegistration hr1 = button.getDecorator().addNodeMouseEnterHandler(hoverTimer);
        HandlerRegistration hr2 = button.getDecorator().addNodeMouseExitHandler(hoverTimer);
        handlerRegistrationManager.register(hr1);
        handlerRegistrationManager.register(hr2);
    }

    @Override
    public void show() {
        if (!showing) {
            super.show();
            showing = true;
        }
    }

    @Override
    public void hide() {
        if (showing) {
            super.hide();
            showing = false;
        }
    }

    private HoverToolbox(final Layer layer,
                         final WiresShape shape,
                         final Shape<?> attachTo,
                         final Direction anchor,
                         final Direction towards,
                         final int rows,
                         final int cols,
                         final int padding,
                         final int iconSize,
                         final List<ToolboxButton> buttons) {
        super(layer,
              shape,
              attachTo,
              anchor,
              towards,
              rows,
              cols,
              padding,
              iconSize,
              buttons);
    }

    @Override
    protected void registerHandlers(final Node<?> node) {
        super.registerHandlers(node);
        HandlerRegistration hr1 = node.addNodeMouseEnterHandler(this.hoverTimer);
        HandlerRegistration hr2 = node.addNodeMouseExitHandler(this.hoverTimer);
        handlerRegistrationManager.register(hr1);
        handlerRegistrationManager.register(hr2);
    }

    public static class HoverToolboxBuilder extends AbstractBuilder {

        public HoverToolboxBuilder(final Layer layer,
                                   final WiresShape shape) {
            super(layer,
                  shape);
        }

        @Override
        public GridToolbox register() {
            return new HoverToolbox(this.layer,
                                    this.shape,
                                    this.attachTo,
                                    this.anchor,
                                    this.towards,
                                    this.rows,
                                    this.cols,
                                    this.padding,
                                    this.iconSize,
                                    this.buttons);
        }
    }
}
