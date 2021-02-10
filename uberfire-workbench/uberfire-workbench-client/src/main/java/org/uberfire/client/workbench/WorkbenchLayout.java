/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Used by the workbench to construct the outer most DOM structure (header, footer and perspective container).
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
     */
    void onBootstrap();

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
    void resizeTo(int width,
                  int height);
}
