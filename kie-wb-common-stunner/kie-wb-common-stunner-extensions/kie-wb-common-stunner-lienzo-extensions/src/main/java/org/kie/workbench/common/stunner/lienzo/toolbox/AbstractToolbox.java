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

import java.util.Iterator;
import java.util.List;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.animation.AnimationCallback;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimation;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.event.AnimationFrameAttributesChangedBatcher;
import com.ait.lienzo.client.core.event.AttributesChangedEvent;
import com.ait.lienzo.client.core.event.AttributesChangedHandler;
import com.ait.lienzo.client.core.event.IAttributesChangedBatcher;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.WiresUtils;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.tooling.common.api.flow.Flows;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import org.kie.workbench.common.stunner.lienzo.grid.Grid;
import org.kie.workbench.common.stunner.lienzo.toolbox.grid.GridToolbox;

import static com.ait.lienzo.client.core.AttributeOp.any;

public abstract class AbstractToolbox implements GridToolbox {

    private static final double ANIMATION_DURATION = 50;

    private static final Flows.BooleanOp XYWH_OP = any(Attribute.X,
                                                       Attribute.Y,
                                                       Attribute.WIDTH,
                                                       Attribute.HEIGHT);

    protected final Layer layer;
    protected final WiresShape shape;
    protected final Direction anchor;
    protected final Direction towards;
    protected final List<ToolboxButton> buttons;
    protected final HandlerRegistrationManager handlerRegistrationManager = new HandlerRegistrationManager();
    protected IAttributesChangedBatcher attributesChangedBatcher = new AnimationFrameAttributesChangedBatcher();

    protected final Grid grid;
    protected Group group;

    protected void initialize() {
        if (grid.size() < buttons.size()) {
            throw new IllegalStateException("added more buttons than the grid can contain");
        }
        this.group = new Group().setDraggable(false);
        Iterator<Grid.Point> gridIterator = grid.iterator();
        for (ToolboxButton button : buttons) {
            Grid.Point point = gridIterator.next();
            button.getShape().setX(point.getX()).setY(point.getY());
            button.getShape().getGroup().setAlpha(0);
            registerButton(button);
            group.add(button.getShape().getGroup());
        }
        getLayer().add(group);
        reposition();
        initHandlers();
        batch();
    }

    protected void registerButton(final ToolboxButton button) {
    }

    protected void initHandlers() {
        shape.getGroup().setAttributesChangedBatcher(attributesChangedBatcher);
        final AttributesChangedHandler handler = new AttributesChangedHandler() {
            @Override
            public void onAttributesChanged(final AttributesChangedEvent event) {
                if (event.evaluate(XYWH_OP)) {
                    reposition();
                }
            }
        };
        // Attribute change handlers.
        handlerRegistrationManager.register(
                shape.getGroup().addAttributesChangedHandler(Attribute.X,
                                                             handler)
        );
        handlerRegistrationManager.register(
                shape.getGroup().addAttributesChangedHandler(Attribute.Y,
                                                             handler)
        );
        handlerRegistrationManager.register(
                shape.getPath().addAttributesChangedHandler(Attribute.WIDTH,
                                                            handler)
        );
        handlerRegistrationManager.register(
                shape.getPath().addAttributesChangedHandler(Attribute.HEIGHT,
                                                            handler)
        );
        // Shape resize handlers.
        handlerRegistrationManager.register(
                shape.addWiresResizeStartHandler(event -> reposition(true))
        );
        handlerRegistrationManager.register(
                shape.addWiresResizeStepHandler(event -> reposition(true))
        );
        handlerRegistrationManager.register(
                shape.addWiresResizeEndHandler(event -> reposition(true))
        );
    }

    private void reposition() {
        reposition(false);
    }

    private void reposition(final boolean batch) {
        final double gx = WiresUtils.getLocation(shape.getGroup()).getX();
        final double gy = WiresUtils.getLocation(shape.getGroup()).getY();
        final Point2D anchorPoint = Positioning.anchorFor(this.shape.getPath().getBoundingPoints().getBoundingBox(),
                                                          this.anchor);
        final Grid.Point toolboxPosition = this.grid.findPosition(new Grid.Point((int) anchorPoint.getX(),
                                                                                 (int) anchorPoint.getY()),
                                                                  this.towards);
        group.setX(gx + toolboxPosition.getX());
        group.setY(gy + toolboxPosition.getY());
        if (batch) {
            batch();
        }
    }

    @Override
    public void show() {
        reposition(true);
        for (ToolboxButton button : buttons) {
            button.getShape().getGroup().animate(AnimationTweener.LINEAR,
                                                 AnimationProperties.toPropertyList(AnimationProperty.Properties.ALPHA(1)),
                                                 ANIMATION_DURATION,
                                                 new AnimationCallback());
        }
    }

    @Override
    public void remove() {
        attributesChangedBatcher.cancelAttributesChangedBatcher();
        handlerRegistrationManager.removeHandler();
        for (ToolboxButton button : buttons) {
            button.remove();
        }
        group.removeFromParent();
    }

    @Override
    public void hide() {
        for (final ToolboxButton button : buttons) {
            button.getShape().getGroup().animate(
                    AnimationTweener.LINEAR,
                    AnimationProperties.toPropertyList(AnimationProperty.Properties.ALPHA(0)),
                    ANIMATION_DURATION,
                    new AnimationCallback() {

                        @Override
                        public void onClose(final IAnimation animation,
                                            final IAnimationHandle handle) {
                        }
                    });
        }
    }

    Layer getLayer() {
        return layer;
    }

    protected AbstractToolbox(final Layer layer,
                              final WiresShape shape,
                              final Shape<?> attachTo,
                              final Direction anchor,
                              final Direction towards,
                              final int rows,
                              final int cols,
                              final int padding,
                              final int iconSize,
                              final List<ToolboxButton> buttons) {
        this.layer = layer;
        this.shape = shape;
        this.anchor = anchor;
        this.towards = towards;
        this.buttons = buttons;
        this.grid = new Grid(padding,
                             iconSize,
                             rows,
                             cols);
        initialize();
        registerHandlers(attachTo);
    }

    private void registerHandlers(final Shape<?> attachTo) {
        Node<?> node = attachTo != null ? attachTo : shape.getPath();
        registerHandlers(node);
    }

    protected void registerHandlers(final Node<?> node) {
    }

    private void batch() {
        this.getLayer().batch();
    }
}
