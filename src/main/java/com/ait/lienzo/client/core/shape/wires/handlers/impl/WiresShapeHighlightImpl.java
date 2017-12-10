package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeHighlight;
import com.ait.lienzo.client.core.types.FillGradient;
import com.ait.lienzo.client.core.types.LinearGradient;
import com.ait.lienzo.client.core.types.PatternGradient;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.RadialGradient;

public class WiresShapeHighlightImpl implements WiresShapeHighlight<PickerPart.ShapePart> {

    private final int borderSize;

    public WiresShapeHighlightImpl(int borderSize) {
        this.borderSize = borderSize;
    }

    @Override
    public void highlight(final WiresShape shape, 
                          final PickerPart.ShapePart part) {
        switch (part) {
            case BODY:
                highlightBody(shape);
                break;
            default:
                highlightBorder(shape);
        }
    }

    @Override
    public void restore() {
        doRestore();
    }

    private WiresShape parent;
    private String m_priorFill;
    private FillGradient m_priorFillGradient;
    private Double m_priorAlpha;
    private MultiPath m_path;

    private void highlightBody(final WiresShape parent) {
        if (!isBodyHighlight()) {
            m_priorFill = parent.getPath().getFillColor();
            m_priorFillGradient = parent.getPath().getFillGradient();
            m_priorAlpha = parent.getPath().getFillAlpha();
            parent.getPath().setFillColor("#CCCCCC");
            parent.getPath().setFillAlpha(0.8);
            this.parent = parent;
            drawLayer();
        }
    }

    private void highlightBorder(final WiresShape parent) {
        if (null == m_path) {
            MultiPath path = parent.getPath();
            m_path = path.copy();
            m_path.setStrokeWidth(borderSize);
            final Point2D absLoc = path.getComputedLocation();
            m_path.setX(absLoc.getX());
            m_path.setY(absLoc.getY());
            m_path.setStrokeColor("#CC1100");
            m_path.setStrokeAlpha(0.8);
            parent.getGroup().getOverLayer().add(m_path);
            this.parent = parent;
            drawOverLayer();
        }
    }

    private void doRestore() {
        restoreBody();
        restoreBorder();
        parent = null;
    }

    private void restoreBody() {
        if (isBodyHighlight()) {
            parent.getPath().setFillColor(m_priorFill);
            if (m_priorFillGradient instanceof LinearGradient) {
                parent.getPath().setFillGradient((LinearGradient) m_priorFillGradient);
            } else if (m_priorFillGradient instanceof PatternGradient) {
                parent.getPath().setFillGradient((PatternGradient) m_priorFillGradient);
            } else if (m_priorFillGradient instanceof RadialGradient) {
                parent.getPath().setFillGradient((RadialGradient) m_priorFillGradient);
            }
            parent.getPath().setFillAlpha(getPriorAlpha());
            m_priorFill = null;
            m_priorFillGradient = null;
            m_priorAlpha = null;
            drawLayer();
        }
    }

    private void restoreBorder() {
        if (null != m_path) {
            m_path.removeFromParent();
            m_path = null;
            drawOverLayer();
        }
    }

    private boolean isBodyHighlight() {
        return null != m_priorFill || null != m_priorFillGradient || null != m_priorAlpha;
    }

    private double getPriorAlpha() {
        return null != m_priorAlpha ? m_priorAlpha : 0d;
    }

    private void drawLayer() {
        if (null != parent) {
            parent.getGroup().getLayer().batch();
        }
    }

    private void drawOverLayer() {
        if (null != parent) {
            parent.getGroup().getOverLayer().batch();
        }
    }
}
