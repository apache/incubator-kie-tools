package com.ait.lienzo.client.core.event;

import elemental2.dom.MouseEvent;

public class OnEventHandlers
{
    private OnMouseEventHandler m_onMouseDownEventHandle = DefaultOnMouseEventHandler.INSTANCE;

    private OnMouseEventHandler m_onMouseUpEventHandle = DefaultOnMouseEventHandler.INSTANCE;

    private OnMouseEventHandler m_onMouseMoveEventHandle = DefaultOnMouseEventHandler.INSTANCE;

    private OnMouseEventHandler m_onMouseClickEventHandle = DefaultOnMouseEventHandler.INSTANCE;

    private OnMouseEventHandler m_onMouseDoubleClickEventHandle = DefaultOnMouseEventHandler.INSTANCE;

    public OnMouseEventHandler getOnMouseDownEventHandle()
    {
        return m_onMouseDownEventHandle;
    }

    public void setOnMouseDownEventHandle(OnMouseEventHandler onMouseDownEventHandle)
    {
        m_onMouseDownEventHandle = onMouseDownEventHandle;
    }

    public OnMouseEventHandler getOnMouseUpEventHandle()
    {
        return m_onMouseUpEventHandle;
    }

    public void setOnMouseUpEventHandle(OnMouseEventHandler onMouseUpEventHandle)
    {
        m_onMouseUpEventHandle = onMouseUpEventHandle;
    }

    public OnMouseEventHandler getOnMouseMoveEventHandle()
    {
        return m_onMouseMoveEventHandle;
    }

    public void setOnMouseMoveEventHandle(OnMouseEventHandler onMouseMoveEventHandle)
    {
        m_onMouseMoveEventHandle = onMouseMoveEventHandle;
    }

    public OnMouseEventHandler getOnMouseClickEventHandle()
    {
        return m_onMouseClickEventHandle;
    }

    public void setOnMouseClickEventHandle(OnMouseEventHandler onMouseClickEventHandle)
    {
        m_onMouseClickEventHandle = onMouseClickEventHandle;
    }

    public OnMouseEventHandler getOnMouseDoubleClickEventHandle()
    {
        return m_onMouseDoubleClickEventHandle;
    }

    public void setOnMouseDoubleClickEventHandle(OnMouseEventHandler onMouseDoubleClickEventHandle)
    {
        m_onMouseDoubleClickEventHandle = onMouseDoubleClickEventHandle;
    }

    public static class DefaultOnMouseEventHandler implements OnMouseEventHandler
    {
        static DefaultOnMouseEventHandler INSTANCE = new DefaultOnMouseEventHandler();

        @Override
        public boolean onMouseEventBefore(MouseEvent listener)
        {
            return true;
        }

        @Override
        public void onMouseEventAfter(MouseEvent listener)
        {

        }
    }

    public void destroy()
    {
        m_onMouseDownEventHandle = null;
        m_onMouseUpEventHandle = null;
        m_onMouseMoveEventHandle = null;
        m_onMouseClickEventHandle = null;
        m_onMouseDoubleClickEventHandle = null;
    }
}
