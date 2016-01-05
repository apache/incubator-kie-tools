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

package org.uberfire.client.workbench.panels;

/**
 * Interface for workbench panel views that support "docking" nested panels along their edges.
 */
public interface DockingWorkbenchPanelView <P extends WorkbenchPanelPresenter> extends WorkbenchPanelView<P> {

    /**
     * Sets the size (width for EAST or WEST children; height for NORTH and SOUTH children) allocated to the specified
     * child by moving its splitter bar on the screen. If the requested space isn't available (for instance because it
     * is larger than the browser window, or it would make the central panel of this view smaller than its minimum size)
     * then the largest possible amount will be given to the requested child. Similarly, if the requested size is less
     * than the child's minimum width or height (as appropriate) then the child will be set to its minimum.
     *
     * @param childPanel
     *            the panel whose size to change
     * @return true if the child was found and its size was adjusted (even if the requested amount was out of range);
     *         false if the given child was not found as a child of this panel.
     */
    boolean setChildSize( WorkbenchPanelView<?> childPanel, int size );
}
