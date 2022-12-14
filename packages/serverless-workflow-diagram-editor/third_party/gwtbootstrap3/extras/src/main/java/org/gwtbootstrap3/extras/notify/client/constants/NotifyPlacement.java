package org.gwtbootstrap3.extras.notify.client.constants;

import org.gwtbootstrap3.client.ui.constants.Type;

/*
 * #%L
 * GwtBootstrap3
 * %%
 * Copyright (C) 2013 - 2015 GwtBootstrap3
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/**
 * Enumeration of possible Notify's screen locations.
 *
 * @author Xiaodong SUN
 */
public enum NotifyPlacement implements Type {

    TOP_LEFT("top", "left"),
    TOP_CENTER("top", "center"),
    TOP_RIGHT("top", "right"),
    BOTTOM_LEFT("bottom", "left"),
    BOTTOM_CENTER("bottom", "center"),
    BOTTOM_RIGHT("bottom", "right");

    private final String from;
    private final String align;

    private NotifyPlacement(final String from, final String align) {
        this.from = from;
        this.align = align;
    }

    /**
     * Returns the vertical placement : top or bottom.
     *
     * @return the vertical placement
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the horizontal placement : left, center, or right.
     *
     * @return the horizontal placement
     */
    public String getAlign() {
        return align;
    }

    /**
     * Returns the string representation of placement.
     *
     * @return String representation of placement
     */
    public String getPlacement() {
        return getFrom() + "-" + getAlign();
    }

}
