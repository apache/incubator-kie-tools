package com.ait.lienzo.tools.client.event;

import elemental2.dom.HTMLElement;
import elemental2.dom.MouseEvent;
import jsinterop.base.Js;

public class MouseEventUtil
{

    private MouseEventUtil()
    {

    }

    /**
     * The left mouse button.
     */
    public static final int BUTTON_LEFT = 0;

    /**
     * The middle mouse button.
     */
    public static final int BUTTON_MIDDLE = 1;

    /**
     * The right mouse button.
     */
    public static final int BUTTON_RIGHT = 2;

    private static double getSubPixelAbsoluteLeft(HTMLElement elem)
    {
        // Lifted from GWT
        double left = 0;
        HTMLElement curr = elem;
        // This intentionally excludes body which has a null offsetParent.
        while (curr.offsetParent != null)
        {
            left -= curr.scrollLeft;
            curr = Js.uncheckedCast(curr.parentNode);
        }
        while (elem != null) {
            left += elem.offsetLeft;
            elem = (HTMLElement) elem.offsetParent;
        }
        return left;
    }

    private static double getSubPixelAbsoluteTop(HTMLElement elem)
    {
        // Lifted from GWT  @TODO I might need checks, before assuming Element is HTMLElement (mdp)
        double left = 0;
        HTMLElement curr = elem;
        // This intentionally excludes body which has a null offsetParent.
        while (curr.offsetParent != null)
        {
            left -= curr.scrollTop;
            curr = Js.uncheckedCast(curr.parentNode);
        }
        while (elem != null) {
            left += elem.offsetTop;
            elem = (HTMLElement) elem.offsetParent;
        }
        return left;
    }

    public static int getAbsoluteLeft(HTMLElement elem) {
        return Js.coerceToInt(getSubPixelAbsoluteLeft(elem));
    }

    public static int getAbsoluteTop(HTMLElement elem) {
        return Js.coerceToInt(getSubPixelAbsoluteTop(elem));
    }

    public static int getRelativeX(final double clientX, final HTMLElement target)
    {
        // Lifted from GWT
        return Js.coerceToInt(clientX - getAbsoluteLeft(target) + target.scrollLeft +
                              target.ownerDocument.documentElement.scrollLeft);
    }

    public static int getRelativeY(final double clientY, final HTMLElement target)
    {
        // Lifted from GWT
        return Js.coerceToInt(clientY- getAbsoluteTop(target) + target.scrollTop +
                              target.ownerDocument.documentElement.scrollTop);
    }

    public static final boolean isButtonLeft(final MouseEvent event)
    {
        if (null != event)
        {
            if (event.button == BUTTON_LEFT)
            {
                return true;
            }
        }
        return false;
    }

    public static final boolean isButtonMiddle(final MouseEvent event)
    {
        if (null != event)
        {
            if (event.button == BUTTON_MIDDLE)
            {
                return true;
            }
        }
        return false;
    }

    public static final boolean isButtonRight(final MouseEvent event)
    {
        if (null != event)
        {
            if (event.button == BUTTON_RIGHT)
            {
                return true;
            }
        }
        return false;
    }
}
