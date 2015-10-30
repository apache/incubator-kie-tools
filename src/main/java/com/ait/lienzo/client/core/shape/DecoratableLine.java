
package com.ait.lienzo.client.core.shape;

import java.util.ArrayList;
import java.util.List;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

public class DecoratableLine extends AbstractMultiPathPartShape<DecoratableLine>
{
    private AbstractOffsetMultiPointShape<?> m_line;

    private EndDecorator<?>                  m_headDecorator;

    private EndDecorator<?>                  m_tailDecorator;

    public DecoratableLine(AbstractOffsetMultiPointShape<?> line, EndDecorator<?> headDecorator, EndDecorator<?> tailDecorator)
    {
        super(ShapeType.LINE);

        init(line, headDecorator, tailDecorator);
    }

    public DecoratableLine(ShapeType type, JSONObject node, ValidationContext ctx, AbstractOffsetMultiPointShape<?> line, EndDecorator<?> headDecorator, EndDecorator<?> tailDecorator) throws ValidationException
    {
        super(type, node, ctx);

        init(line, headDecorator, tailDecorator);
    }

    private void init(AbstractOffsetMultiPointShape<?> line, EndDecorator<?> headArrow, EndDecorator<?> tailArrow)
    {
        m_line = line;

        m_line.setParent(this);

        m_headDecorator = headArrow;

        if (headArrow != null)
        {
            m_headDecorator.setParent(this);
        }
        m_tailDecorator = tailArrow;

        if (tailArrow != null)
        {
            m_tailDecorator.setParent(this);
        }
    }

    @Override
    protected boolean prepare(Context2D context, Attributes attr, double alpha)
    {
        if (m_line.getPathPartList().size() > 0)
        {
            return true;
        }
        Point2DArray points = m_line.getPoint2DArray();
        getPathPartListArray().clear();

        Attributes lineAttr = m_line.getAttributes();
        lineAttr.setStrokeWidth(attr.getStrokeWidth());
        lineAttr.setStrokeColor(attr.getStrokeColor());
        lineAttr.setStrokeAlpha(attr.getStrokeAlpha());

        lineAttr.setFillColor(attr.getFillColor());
        lineAttr.setFillAlpha(attr.getFillAlpha());
        //lineAttr.setFillGradient(attr.getFillGradient());
        lineAttr.setX(getX());
        lineAttr.setY(getY());

        boolean prepared = m_line.prepareWithoutWrite(context, lineAttr, alpha);

        if (prepared)
        {
            add(m_line.getPathPartList());
            if (m_headDecorator != null)
            {
                Point2D p0 = m_line.getHeadOffsetPoint();
                Point2D p1 = points.get(0);
                prepared = prepareEndDecorator(context, attr, alpha, prepared, p0, p1, m_headDecorator);
                add(m_headDecorator.getPathPartList());
            }
            if (m_tailDecorator != null)
            {
                Point2D p0 = m_line.getTailOffsetPoint();
                Point2D p1 = points.get(points.size() - 1);
                prepared = prepareEndDecorator(context, attr, alpha, prepared, p0, p1, m_tailDecorator);
                add(m_tailDecorator.getPathPartList());
            }
        }
        return prepared;
    }

    private boolean prepareEndDecorator(Context2D context, Attributes attr, double alpha, boolean prepared, Point2D p0, Point2D p1, Shape<?> decorator)
    {
        Attributes headAttr = decorator.getAttributes();
        headAttr.setStrokeWidth(attr.getStrokeWidth());
        headAttr.setStrokeColor(attr.getStrokeColor());
        headAttr.setStrokeAlpha(attr.getStrokeAlpha());

        headAttr.setFillColor(attr.getFillColor());
        headAttr.setFillAlpha(attr.getFillAlpha());
        //lineAttr.setFillGradient(attr.getFillGradient());
        headAttr.setX(getX());
        headAttr.setY(getY());

        ((EndDecorator<?>) decorator).set(p0, p1);
        decorator.setStrokeColor(m_line.getStrokeColor());
        decorator.setStrokeWidth(m_line.getStrokeWidth());
        prepared = prepared & ((EndDecorator<?>) decorator).prepareWithoutWrite(context, decorator.getAttributes(), alpha);
        return prepared;
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes()
    {
        List<Attribute> list = new ArrayList<Attribute>();
        list.addAll(m_line.getBoundingBoxAttributes());
        if (m_headDecorator != null)
        {
            list.addAll(m_headDecorator.getBoundingBoxAttributes());
        }
        if (m_tailDecorator != null)
        {
            list.addAll(m_tailDecorator.getBoundingBoxAttributes());
        }
        return list;
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        BoundingBox box = new BoundingBox();
        if (m_headDecorator != null)
        {
            box.add(m_headDecorator.getBoundingBox());
        }
        if (m_tailDecorator != null)
        {
            box.add(m_tailDecorator.getBoundingBox());
        }
        box.add(m_line.getBoundingBox());
        return box;
    }

    public AbstractOffsetMultiPointShape<?> getLine()
    {
        return m_line;
    }

    public EndDecorator<?> getHeadDecorator()
    {
        return m_headDecorator;
    }

    public EndDecorator<?> getTailDecorator()
    {
        return m_tailDecorator;
    }
}
