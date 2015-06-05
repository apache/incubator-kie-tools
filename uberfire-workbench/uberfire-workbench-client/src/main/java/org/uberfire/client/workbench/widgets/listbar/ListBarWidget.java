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
package org.uberfire.client.workbench.widgets.listbar;

import org.uberfire.client.workbench.panels.MaximizeToggleButtonPresenter;
import org.uberfire.client.workbench.panels.MultiPartWidget;
import org.uberfire.client.workbench.panels.impl.AbstractSimpleWorkbenchPanelView;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelView;
import org.uberfire.mvp.Command;

import com.google.gwt.user.client.ui.Widget;

/**
 * API contract for the header widget of panel views that extend {@link AbstractSimpleWorkbenchPanelView} and
 * {@link MultiListWorkbenchPanelView}. Each application needs exactly one implementation of this class at compile time
 * (usually this will come from the view module). The implementing type must be a Dependent-scoped CDI bean.
 */
public interface ListBarWidget extends MultiPartWidget {

    /**
     * When a part is added to the list bar, a special title widget is created for it. This title widget is draggable.
     * To promote testability, implementations of this interface must set the draggable title widget's debug ID using
     * the {@code Widget.ensureDebugId()} call. The debug ID must have the form
     * {@code DEBUG_ID_PREFIX + DEBUG_TITLE_PREFIX + partName}.
     * <p>
     * Note that debug IDs are only assigned when the app inherits the GWT Debug module. See
     * {@link Widget#ensureDebugId(com.google.gwt.dom.client.Element, String)} for details.
     */
    public static final String DEBUG_TITLE_PREFIX = "ListBar-title-";

    /**
     * Enable this list bar's to support drag and drop.
     *
     */
    public void enableDnd();

    /**
     * Disable this list bar's to support drag and drop.
     *
     */
    public void disableDnd();

    /**
     * Returns the toggle button, which is initially hidden, that can be used to trigger maximizing and unmaximizing
     * of the panel containing this list bar. Make the button visible by calling {@link Widget#setVisible(boolean)}
     * and set its maximize and unmaximize actions with {@link MaximizeToggleButtonPresenter#setMaximizeCommand(Command)} and
     * {@link MaximizeToggleButtonPresenter#setUnmaximizeCommand(Command)}.
     */
    public MaximizeToggleButtonPresenter getMaximizeButton();

    public boolean isDndEnabled();

}