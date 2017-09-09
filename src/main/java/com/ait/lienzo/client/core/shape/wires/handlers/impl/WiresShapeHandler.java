package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseDownHandler;
import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpHandler;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.MouseEvent;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DragContext;

/**
 * This handler's goals are:
 * - Delegate some mouse interactions for a single wires shape to its wires shape control instance
 * - Displays some highlights to provide feedback for containment and docking operations.
 */
public class WiresShapeHandler
        extends WiresManager.WiresDragHandler
        implements NodeMouseDownHandler,
                   NodeMouseUpHandler,
                   NodeMouseClickHandler {

    private final WiresShapeControl control;
    private final WiresShapePartHighlight highlight;

    public WiresShapeHandler(WiresShapeControl control,
                             WiresManager manager) {
        super(manager);
        this.control = control;
        highlight = new WiresShapePartHighlight();
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
                highlight.highlightBorder(getParentShape(),
                                          getWiresManager().getDockingAcceptor().getHotspotSize());
            } else {
                highlight.highlightBody(getParentShape());
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
                highlight.highlightBorder(newParent,
                                          getWiresManager().getDockingAcceptor().getHotspotSize());
            } else if (isContAllow) {
                highlight.highlightBody(newParent);
            }
        }

        batch();

        return adjusted;
    }

    @Override
    protected void doOnNodeDragEnd(NodeDragEndEvent event) {
        // Restore highlights, if any.
        highlight.restore();

        Point2D location = null;
        boolean c_accept = false;
        boolean d_accept = false;
        boolean l_accept = false;
        if (control.onMoveComplete()) {
            d_accept = null != control.getDockingControl() &&
                    control.getDockingControl().accept();
            c_accept = !d_accept && null != control.getContainmentControl() &&
                    control.getContainmentControl().accept();

            if (c_accept) {
                location = control.getContainmentControl().getCandidateLocation();
            } else if (d_accept) {
                location = control.getDockingControl().getCandidateLocation();
            } else {
                location = control.getParentPickerControl().getShapeLocation();
            }
            l_accept = getShape().getWiresManager()
                    .getLocationAcceptor()
                    .accept(new WiresShape[]{getShape()},
                            new Point2D[]{location});
        }

        if ((c_accept || d_accept) && l_accept) {
            if (d_accept) {
                control.getDockingControl().execute();
            } else {
                control.getContainmentControl().execute();
            }
            control.getParentPickerControl().setShapeLocation(location);
            control.execute();
        } else {
            reset();
        }
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

    private void batch() {
        getShape().getGroup().getLayer().batch();
    }
}
