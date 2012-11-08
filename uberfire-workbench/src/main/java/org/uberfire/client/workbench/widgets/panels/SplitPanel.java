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
package org.uberfire.client.workbench.widgets.panels;

import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.RootWorkbenchPanelPresenter;

import com.google.gwt.user.client.ui.Widget;

/**
 * Common operations for Workbench Split Panels.
 */
public interface SplitPanel {

    //The default initial size should the Panel not provide one
    public static final int DEFAULT_SIZE     = 64;

    //The default minimum size should the Panel not provide one
    public static final int DEFAULT_MIN_SIZE = 32;

    public void setup(final WorkbenchPanelView eastWidget,
                      final WorkbenchPanelView westWidget,
                      final Position position,
                      final Integer preferredSize,
                      final Integer preferredMinSize);

    public void clear();

    public Widget getWidget(Position position);

}
