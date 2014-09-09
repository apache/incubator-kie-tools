/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.workbench.panels;

import org.uberfire.workbench.model.CompassPosition;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * Common operations for UberFire's horizontal and vertical splitter panels.
 */
public interface SplitPanel extends IsWidget, RequiresResize, ProvidesResize {

    /**
     * The default initial size for a fixed-size child.
     */
    public static final int DEFAULT_SIZE = 64;

    /**
     * The default minimum size for a fixed-size child.
     */
    public static final int DEFAULT_MIN_SIZE = 32;

    public Widget getParent();

    /**
     * Sets up this split panel's contents and divider location.
     *
     * @param eastOrNorthWidget
     *            the widget to place in the east side a horizontal panel or north side of a vertical panel.
     * @param westOsSouthWidget
     *            the widget to place in the west side a horizontal panel or south side of a vertical panel.
     * @param fixedSizeComponent
     *            the component that should not change size when the whole split panel grows or shrinks (eg. because the
     *            browser window is being resized)
     * @param preferredSize
     *            the width or height that the fixed-size component should be given initially. If null, a default of
     *            {@value #DEFAULT_SIZE} will be used.
     * @param preferredMinSize
     *            the minimum width or height of the fixed-size component. If null, a default of
     *            {@value #DEFAULT_MIN_SIZE} will be used.
     */
    public void setup( final IsWidget eastOrNorthWidget,
                       final IsWidget westOsSouthWidget,
                       final CompassPosition fixedSizeComponent,
                       final Integer preferredSize,
                       final Integer preferredMinSize );

    public void clear();

    /**
     * Returns the widget on the given side of this splitter.
     *
     * @param position
     *            which widget to get
     * @return the widget on the given side of the splitter; null if that side is empty or the given position is not
     *         supported by this panel. (For example, NORTH is not a supported position of HorizontalSplitterPanel).
     */
    public Widget getWidget( CompassPosition position );

    /**
     * Gets the current pixel size of the component that doesn't expand/contract when the whole split pane's container
     * is resized.
     */
    public int getFixedWidgetSize();

    /**
     * Sets the pixel size of the component that doesn't expand/contract when the whole split pane's container
     * is resized.
     */
    void setFixedWidgetSize( int newSize );
}
