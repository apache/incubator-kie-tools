/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.workbench;

import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Used by the workbench to construct the outer most DOM structure (header, footer and perspective container).
 * Uberfire provides a default {@link org.uberfire.client.workbench.WorkbenchLayoutImpl} that can be replaced
 * through the mechanism described in {@link org.uberfire.client.workbench.LayoutSelection}.
 */
public interface WorkbenchLayout {

    /**
     * Gives access to the root container element that will be attached to the {@link com.google.gwt.user.client.ui.RootLayoutPanel}.
     * @return the outer most workbench widget
     */
    IsWidget getRoot();

    /**
     * Gives access to the element of the workbench that hosts perspective widgets.
     * @return the perspective container element
     */
    HasWidgets getPerspectiveContainer();

    /**
     * Will be invoked by the {@link org.uberfire.client.workbench.Workbench}
     * when the discovery of header and footer elements is completed.
     *
     * @see {@link #setHeaderContents(java.util.List)}
     * @see {@link #setFooterContents(java.util.List)}
     */
    public void onBootstrap();

    /**
     * The {@link org.uberfire.client.workbench.Workbench} listens for resize events and hands them off
     * to the layout. Not needed if your layout is based on {@link com.google.gwt.user.client.ui.LayoutPanel}'s.
     * Kept for backwards compatibility.
     */
    void onResize();

    /**
     * See {@link #onResize()}
     * @param width
     * @param height
     */
    void resizeTo(int width, int height);

    /**
     * Makes the given widget fill the entire space normally dedicated to the perspective container. Has no effect if
     * the given widget is already maximized.
     * <p>
     * <b>Important:</b> this feature is used by panels to maximize themselves. You should not pass a WorkbenchPanelView
     * to this method yourself; instead, you should use the panel's own API to maximize it. You are free to use this method
     * to maximize your own widgets that are not workbench panels.
     *
     * @param w the Widget to maximize.
     */
    void maximize( Widget w );

    /**
     * Restores a previously maximized widget to its original size and position. Has no effect if the given widget is
     * not currently in a maximized state set up by {@link #maximize(Widget)}.
     * <p>
     * <b>Important:</b> this feature is used by panels to unmaximize themselves. You should not pass a WorkbenchPanelView
     * to this method yourself; instead, you should use the panel's own API to unmaximize it. You are free to use this method
     * to unmaximize your own widgets that have previously been passed to {@link #maximize(Widget)}.
     *
     * @param w the Widget to restore to its original size and location.
     */
    void unmaximize( Widget w );

    /**
     * Will insert the implementations of Header and Footer in the Workbench
     * @see {@link #setHeaderContents(java.util.List)}
     * @see {@link #setFooterContents(java.util.List)}
     */
    void setMarginWidgets( boolean isStandaloneMode, Set<String> headersToKeep );
}
