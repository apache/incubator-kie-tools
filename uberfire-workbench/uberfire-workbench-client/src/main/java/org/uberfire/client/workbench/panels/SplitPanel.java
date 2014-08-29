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

    //The default initial size should the Panel not provide one
    public static final int DEFAULT_SIZE = 64;

    //The default minimum size should the Panel not provide one
    public static final int DEFAULT_MIN_SIZE = 32;

    public Widget getParent();

    public void setup( final WorkbenchPanelView eastWidget,
                       final WorkbenchPanelView westWidget,
                       final CompassPosition position,
                       final Integer preferredSize,
                       final Integer preferredMinSize );

    public void clear();

    /**
     * Returns the widget on the given side of this splitter.
     *
     * @param position
     *            which widget to get
     * @return the widget on the given side of the splitter; null if that side is empty.
     * @throws IllegalArgumentException
     *             if this splitter doesn't have the given side (eg. vertical splitters have NORTH and SOUTH but not
     *             EAST and WEST).
     */
    public Widget getWidget( CompassPosition position );

}
