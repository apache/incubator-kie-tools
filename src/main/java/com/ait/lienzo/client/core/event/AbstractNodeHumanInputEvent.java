package com.ait.lienzo.client.core.event;

import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.tools.client.event.INodeXYEvent;
import com.ait.lienzo.tools.client.event.MouseEventUtil;

import elemental2.core.JsArray;
import elemental2.dom.HTMLElement;
import elemental2.dom.MouseEvent;
import elemental2.dom.Touch;
import elemental2.dom.TouchEvent;
import elemental2.dom.TouchList;
import elemental2.dom.UIEvent;

public abstract class AbstractNodeHumanInputEvent<H, S> extends AbstractNodeEvent<H, S> implements INodeXYEvent<H, S>
{
    protected TouchEvent          m_touchEvent;

    protected MouseEvent          m_mouseEvent;

    private   int                 m_x;

    private   int                 m_y;

    private   JsArray<TouchPoint> m_touches;

    private   DragContext         m_drag ;

    protected AbstractNodeHumanInputEvent(final HTMLElement relativeElement)
    {
        super(relativeElement);
    }

    public void kill()
    {
        m_touchEvent = null;
        m_mouseEvent = null;

        setSource(null);
        setDead(true);
        m_touches = null;
        m_drag = null;
    }

    public void revive()
    {
        m_touchEvent = null;
        m_mouseEvent = null;
        setSource(null);
        setDead(false);
        m_touches = null;
        m_drag = null;
    }

    public void override(S source, MouseEvent mouseEvent, TouchEvent touchEvent, final int x, final int y, DragContext drag)
    {
        setSource(source);

        if (mouseEvent != null)
        {
            m_mouseEvent = mouseEvent;
        }
        else
        {
            m_touchEvent = touchEvent;
            m_touches = getTouches(touchEvent, getRelativeElement());
        }

        m_x = x;

        m_y = y;

        m_drag = drag;
    }

    private static final JsArray<TouchPoint> getTouches(final TouchEvent event, final HTMLElement target)
    {
        TouchList touchList = event.touches;
        JsArray<TouchPoint> jsArray = new JsArray<TouchPoint>();

        if ( touchList != null && touchList.length > 0 )
        {
            for(int i=0, length=touchList.length; i < length; i++)
            {
                Touch t = touchList.getAt(i);
                int   x = MouseEventUtil.getRelativeX(t.clientX, target);
                int   y = MouseEventUtil.getRelativeX(t.clientY, target);
                jsArray.push(new TouchPoint(x, y));
            }
        }

        return jsArray;
    }


    @Override
    public int getX()
    {
        return m_x;
    }

    @Override
    public int getY()
    {
        return m_y;
    }

    public UIEvent getNativeEvent()
    {
        return this.m_mouseEvent != null ? m_mouseEvent : m_touchEvent;
    }

    public DragContext getDragContext()
    {
        return m_drag;
    }


    public final boolean isShiftKeyDown()
    {
        return isShiftKeyDown(getNativeEvent());
    }

    public final boolean isAltKeyDown()
    {
        return isAltKeyDown(getNativeEvent());
    }

    public final boolean isMetaKeyDown()
    {
        return isMetaKeyDown(getNativeEvent());
    }


    public final boolean isCtrlKeyDown()
    {
        return isCtrlKeyDown(getNativeEvent());
    }

    public boolean isButtonLeft()
    {
        final UIEvent event = getNativeEvent();

        if (event instanceof  MouseEvent)
        {
            if (((MouseEvent)event).button == MouseEventUtil.BUTTON_LEFT)
            {
                return true;
            }
        }
        return false;
    }

    public boolean isButtonMiddle()
    {
        final UIEvent event = getNativeEvent();
        if (event instanceof  MouseEvent)
        {
            if (((MouseEvent)event).button == MouseEventUtil.BUTTON_MIDDLE)
            {
                return true;
            }
        }
        return false;
    }

    public boolean isButtonRight()
    {
        final UIEvent event = getNativeEvent();
        if (event instanceof  MouseEvent)
        {
            if (((MouseEvent)event).button == MouseEventUtil.BUTTON_RIGHT)
            {
                return true;
            }
        }
        return false;
    }

}
