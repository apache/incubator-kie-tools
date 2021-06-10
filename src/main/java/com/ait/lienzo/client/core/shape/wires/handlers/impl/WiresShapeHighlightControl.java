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

package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.AlignAndDistributeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.MouseEvent;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresContainmentControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlFactory;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresDockingControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresLayerIndex;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresMagnetsControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresParentPickerControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeHighlight;
import com.ait.lienzo.client.core.types.Point2D;

import static com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeControlUtils.excludeFromIndex;

public class WiresShapeHighlightControl implements WiresShapeControl {

    private final WiresManager wiresManager;
    private final Supplier<WiresLayerIndex> indexBuilder;
    private final WiresShapeHighlight<PickerPart.ShapePart> highlight;
    private final Supplier<WiresShapeControl> delegate;

    public static WiresShapeHighlightControl create(final WiresManager wiresManager,
                                                    final Supplier<WiresShapeControl> delegate) {

        final WiresControlFactory controlFactory = wiresManager.getControlFactory();
        return new WiresShapeHighlightControl(wiresManager,
                                              new Supplier<WiresLayerIndex>() {
                                                  @Override
                                                  public WiresLayerIndex get() {
                                                      return controlFactory.newIndex(wiresManager);
                                                  }
                                              },
                                              controlFactory.newShapeHighlight(wiresManager),
                                              delegate);

    }

    public WiresShapeHighlightControl(final WiresManager wiresManager,
                                      final Supplier<WiresLayerIndex> indexBuilder,
                                      final WiresShapeHighlight<PickerPart.ShapePart> highlight,
                                      final Supplier<WiresShapeControl> delegate) {
        this.wiresManager = wiresManager;
        this.indexBuilder = indexBuilder;
        this.highlight = highlight;
        this.delegate = delegate;
    }

    @Override
    public void onMoveStart(final double x,
                            final double y) {

        final WiresLayerIndex index = indexBuilder.get();
        excludeFromIndex(index, getShape());
        index.build(wiresManager.getLayer());
        getDelegate().useIndex(new Supplier<WiresLayerIndex>() {
            @Override
            public WiresLayerIndex get() {
                return index;
            }
        });

        getDelegate().onMoveStart(x, y);

        final WiresShape parent = getParentShape();
        if (null != parent) {
            if (isDocked(getShape())) {
                highlight.highlight(getParentShape(),
                                    PickerPart.ShapePart.BORDER);
            } else {
                highlight.highlight(getParentShape(),
                                    PickerPart.ShapePart.BODY);
            }
        }
    }

    @Override
    public boolean onMove(final double dx,
                          final double dy) {

        final WiresShape parent = getParentShape();
        final PickerPart.ShapePart parentPart = getParentShapePart();

        boolean adjusted = getDelegate().onMove(dx, dy);

        final boolean isDockAllowed = null != getDelegate().getDockingControl() && getDelegate().getDockingControl().isAllow();
        final boolean isContAllow = null != getDelegate().getContainmentControl() && getDelegate().getContainmentControl().isAllow();

        final WiresShape newParent = getParentShape();
        final PickerPart.ShapePart newParentPart = getParentShapePart();
        if (parent != newParent || parentPart != newParentPart) {
            highlight.restore();
        }
        if (null != newParent) {
            if (isDockAllowed) {
                highlight.highlight(newParent,
                                    PickerPart.ShapePart.BORDER);
            } else if (isContAllow) {
                highlight.highlight(newParent,
                                    PickerPart.ShapePart.BODY);
            } else {
                highlight.error(newParent,
                                PickerPart.ShapePart.BODY);
            }
        }
        return adjusted;
    }

    @Override
    public void onMoveComplete() {

        getDelegate().onMoveComplete();

        // Complete the control operation.
        if (accept()) {
            getDelegate().execute();
        } else {
            reset();
        }

        // Restore highlights, if any.
        highlight.restore();

        // Clear the index once operations are complete.
        clearIndex();
    }

    @Override
    public boolean accept() {
        return getDelegate().accept();
    }

    @Override
    public boolean isAccepted() {
        return getDelegate().isAccepted();
    }

    @Override
    public void execute() {
        getDelegate().execute();
    }

    @Override
    public void clear() {
        getDelegate().clear();
    }

    @Override
    public void reset() {
        getDelegate().reset();
        highlight.restore();
        clearIndex();
    }

    @Override
    public void onMouseClick(final MouseEvent event) {
        getDelegate().onMouseClick(event);
    }

    @Override
    public void onMouseDown(final MouseEvent event) {
        getDelegate().onMouseDown(event);
    }

    @Override
    public void onMouseUp(final MouseEvent event) {
        getDelegate().onMouseUp(event);
    }

    @Override
    public Point2D getAdjust() {
        return getDelegate().getAdjust();
    }

    @Override
    public boolean isOutOfBounds(final double dx,
                                 final double dy) {
        return getDelegate().isOutOfBounds(dx, dy);
    }

    @Override
    public WiresShapeControl useIndex(final Supplier<WiresLayerIndex> index) {
        getDelegate().useIndex(index);
        return this;
    }

    @Override
    public WiresShapeControl setAlignAndDistributeControl(final AlignAndDistributeControl control) {
        getDelegate().setAlignAndDistributeControl(control);
        return this;
    }

    @Override
    public WiresMagnetsControl getMagnetsControl() {
        return getDelegate().getMagnetsControl();
    }

    @Override
    public AlignAndDistributeControl getAlignAndDistributeControl() {
        return getDelegate().getAlignAndDistributeControl();
    }

    @Override
    public WiresDockingControl getDockingControl() {
        return getDelegate().getDockingControl();
    }

    @Override
    public WiresContainmentControl getContainmentControl() {
        return getDelegate().getContainmentControl();
    }

    @Override
    public WiresParentPickerControl getParentPickerControl() {
        return getDelegate().getParentPickerControl();
    }

    @Override
    public void destroy() {
        getDelegate().destroy();
    }

    public WiresShapeControl getDelegate() {
        return delegate.get();
    }

    private WiresShape getShape() {
        return getDelegate().getParentPickerControl().getShape();
    }

    private WiresShape getParentShape() {
        final WiresContainer parent = getDelegate().getParentPickerControl().getParent();
        return parent instanceof WiresShape ? (WiresShape) parent : null;
    }

    private PickerPart.ShapePart getParentShapePart() {
        return getDelegate().getParentPickerControl().getParentShapePart();
    }

    private  boolean isDocked(final WiresShape shape) {
        return null != shape.getDockedTo();
    }

    private void clearIndex() {
        getDelegate().getParentPickerControl().getIndex().clear();
    }

}
