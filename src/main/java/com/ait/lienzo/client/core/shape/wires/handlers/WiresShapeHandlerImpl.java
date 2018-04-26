package com.ait.lienzo.client.core.shape.wires.handlers;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeHandler;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DragContext;

/**
 * This handler's goals are:
 * - Delegate some mouse interactions for a single wires shape to its wires shape control instance
 * - Displays some highlights to provide feedback for containment and docking operations.
 */
public class WiresShapeHandlerImpl extends WiresManager.WiresDragHandler implements WiresShapeHandler {

    private final WiresShapeControl control;
    private final WiresShapeHighlight<PickerPart.ShapePart> highlight;

    public WiresShapeHandlerImpl(final WiresShapeControl control,
                             final WiresShapeHighlight<PickerPart.ShapePart> highlight,
                             final WiresManager manager) {
        super(manager);
        this.control = control;
        this.highlight = highlight;
    }

    @Override
    public void startDrag(DragContext dragContext) {
        super.startDrag(dragContext);

        // Delegate start dragging to shape control.
        control.onMoveStart(dragContext.getDragStartX(),
                            dragContext.getDragStartY());

        // Highlights.
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
        batch();
    }

    @Override
    protected boolean doAdjust(Point2D dxy) {

        // Keep parent shape and part instances before moving to another location.
        final WiresShape parent = getParentShape();
        final PickerPart.ShapePart parentPart = getParentShapePart();

        boolean adjusted = false;
        // Delegate drag adjustments to shape control.
        if (control.onMove(dxy.getX(),
                           dxy.getY())) {
            dxy.set(control.getAdjust());
            adjusted = true;
        }

        // Check acceptors' allow methods.
        final boolean isDockAllowed = null != getControl().getDockingControl() &&
                getControl().getDockingControl().isAllow();
        final boolean isContAllow = null != getControl().getContainmentControl() &&
                getControl().getContainmentControl().isAllow();

        // Highlights.
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

        batch();

        return adjusted;
    }

    @Override
    protected void doOnNodeDragEnd(NodeDragEndEvent event) {

        final Point2D distanceAdjusted = event.getDragContext().getDistanceAdjusted();
        final Double adjustedX = distanceAdjusted.getX();
        final Double adjustedY = distanceAdjusted.getY();
        final int dx = adjustedX.intValue();
        final int dy = adjustedY.intValue();

        control.onMove(dx, dy);

        // Complete the control operation.
        if (control.onMoveComplete() && control.accept()) {
            control.execute();
        } else {
            reset();
        }

        // Restore highlights, if any.
        highlight.restore();

        batch();
    }

    @Override
    protected void doReset() {
        super.doReset();
        highlight.restore();
    }

    @Override
    public void onNodeMouseClick(NodeMouseClickEvent event) {
        control.onMouseClick(new MouseEvent(event.getX(),
                                            event.getY(),
                                            event.isShiftKeyDown(),
                                            event.isAltKeyDown(),
                                            event.isControlKeyDown()));
    }

    @Override
    public void onNodeMouseDown(NodeMouseDownEvent event) {
        control.onMouseDown(new MouseEvent(event.getX(),
                                           event.getY(),
                                           event.isShiftKeyDown(),
                                           event.isAltKeyDown(),
                                           event.isControlKeyDown()));
    }

    @Override
    public void onNodeMouseUp(NodeMouseUpEvent event) {
        control.onMouseUp(new MouseEvent(event.getX(),
                                         event.getY(),
                                         event.isShiftKeyDown(),
                                         event.isAltKeyDown(),
                                         event.isControlKeyDown()));
    }

    @Override
    public WiresShapeControl getControl() {
        return control;
    }

    private WiresShape getShape() {
        return getControl().getParentPickerControl().getShape();
    }

    private WiresShape getParentShape() {
        final WiresContainer parent = getControl().getParentPickerControl().getParent();
        return null != parent && parent instanceof WiresShape ? (WiresShape) parent : null;
    }

    private PickerPart.ShapePart getParentShapePart() {
        return getControl().getParentPickerControl().getParentShapePart();
    }

    private boolean isDocked(final WiresShape shape) {
        return null != shape.getDockedTo();
    }

    void batch() {
        getShape().getGroup().getLayer().batch();
    }
}
