package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresCompositeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeHighlight;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragContext;

/**
 * This handler's goals are:
 * - Delegate some mouse interactions for multiple wires shapes or connectors to their respective control instances
 * - Displays some highlights to provide feedback for containment operations.
 */
public class WiresCompositeShapeHandler
        extends WiresManager.WiresDragHandler
        implements DragConstraintEnforcer,
                   NodeDragEndHandler {

    private final WiresCompositeControl shapeControl;
    private final WiresShapeHighlight<PickerPart.ShapePart> highlight;

    public WiresCompositeShapeHandler(final WiresCompositeControl shapeControl,
                                      final WiresShapeHighlight<PickerPart.ShapePart> highlight,
                                      final WiresManager manager) {
        super(manager);
        this.shapeControl = shapeControl;
        this.highlight = highlight;
    }

    @Override
    public void startDrag(final DragContext dragContext) {
        super.startDrag(dragContext);

        shapeControl.onMoveStart(dragContext.getDragStartX(),
                                 dragContext.getDragStartY());
    }

    @Override
    protected boolean doAdjust(final Point2D dxy) {

        final boolean adjusted = shapeControl.onMove(dxy.getX(),
                                                     dxy.getY());

        if (adjusted) {
            dxy.set(shapeControl.getAdjust());
            return true;
        }

        boolean shouldRestore = true;

        final WiresContainer parent = shapeControl.getSharedParent();
        if (null != parent && parent instanceof WiresShape) {
            if (shapeControl.isAllowed()) {
                highlight.highlight((WiresShape) parent,
                                    PickerPart.ShapePart.BODY);
                shouldRestore = false;
            } else {
                highlight.error((WiresShape) parent,
                                PickerPart.ShapePart.BODY);
                shouldRestore = false;
            }
        }
        if (shouldRestore) {
            highlight.restore();
        }

        return false;
    }

    @Override
    protected void doOnNodeDragEnd(NodeDragEndEvent event) {

        final Point2D distanceAdjusted = event.getDragContext().getDistanceAdjusted();
        final Double adjustedX = distanceAdjusted.getX();
        final Double adjustedY = distanceAdjusted.getY();
        final int dx = adjustedX.intValue();
        final int dy = adjustedY.intValue();

        shapeControl.onMove(dx, dy);

        if (shapeControl.onMoveComplete() && shapeControl.accept()) {
            shapeControl.execute();
        } else {
            reset();
        }

        // Highlights.
        highlight.restore();
    }

    @Override
    protected void doReset() {
        super.doReset();
        highlight.restore();
    }

    @Override
    public WiresControl getControl() {
        return shapeControl;
    }
}
