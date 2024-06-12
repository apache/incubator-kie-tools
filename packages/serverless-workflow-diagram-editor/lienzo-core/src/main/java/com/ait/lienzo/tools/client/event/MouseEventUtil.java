/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.ait.lienzo.tools.client.event;

import elemental2.dom.HTMLElement;
import elemental2.dom.MouseEvent;
import jsinterop.base.Js;

public class MouseEventUtil {

    private MouseEventUtil() {

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

    private static double getSubPixelAbsoluteLeft(HTMLElement elem) {
        // Lifted from GWT
        double left = 0;
        HTMLElement curr = elem;
        // This intentionally excludes body which has a null offsetParent.
        while (curr.offsetParent != null) {
            left -= curr.scrollLeft;
            curr = Js.uncheckedCast(curr.parentNode);
        }
        while (elem != null) {
            left += elem.offsetLeft;
            elem = (HTMLElement) elem.offsetParent;
        }
        return left;
    }

    private static double getSubPixelAbsoluteTop(HTMLElement elem) {
        double left = 0;
        HTMLElement curr = elem;
        // This intentionally excludes body which has a null offsetParent.
        while (curr.offsetParent != null) {
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
        return (int) getSubPixelAbsoluteLeft(elem);
    }

    public static int getAbsoluteTop(HTMLElement elem) {
        return (int) getSubPixelAbsoluteTop(elem);
    }

    public static int getRelativeX(final double clientX, final HTMLElement target) {
        // Lifted from GWT
        return (int) (clientX - getAbsoluteLeft(target) + target.scrollLeft +
                target.ownerDocument.documentElement.scrollLeft);
    }

    public static int getRelativeY(final double clientY, final HTMLElement target) {
        // Lifted from GWT
        return (int) (clientY - getAbsoluteTop(target) + target.scrollTop +
                target.ownerDocument.documentElement.scrollTop);
    }

    public static final boolean isButtonLeft(final MouseEvent event) {
        if (null != event) {
            if (event.button == BUTTON_LEFT) {
                return true;
            }
        }
        return false;
    }

    public static final boolean isButtonMiddle(final MouseEvent event) {
        if (null != event) {
            if (event.button == BUTTON_MIDDLE) {
                return true;
            }
        }
        return false;
    }

    public static final boolean isButtonRight(final MouseEvent event) {
        if (null != event) {
            if (event.button == BUTTON_RIGHT) {
                return true;
            }
        }
        return false;
    }
}
