package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

public abstract class EndDecorator<T extends EndDecorator<T>> extends AbstractMultiPointShape<T>
{
    public EndDecorator(ShapeType type)
    {
        super(type);
    }

    public EndDecorator(ShapeType type, JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(type, node, ctx);
    }

    /**
     * Sets the base and head points of th decorator
     * @param base
     * @param head
     */
    public abstract void set(final Point2D base, final Point2D head);

    public abstract double getLength();

    @Override
    protected boolean prepare(final Context2D context, final Attributes attr, final double alpha)
    {
        boolean prepared = prepareWithoutWrite(context, attr, alpha);
        if ( prepared )
        {
            context.path(m_list);
        }

        return prepared;
    }

    protected boolean prepareWithoutWrite(final Context2D context, final Attributes attr, final double alpha)
    {
        if (m_list.size() < 1)
        {
            if (false == parse(attr))
            {
                return false;
            }
        }
        if (m_list.size() < 1)
        {
            return false;
        }

        return true;
    }

    protected abstract boolean parse(Attributes attr);
}
