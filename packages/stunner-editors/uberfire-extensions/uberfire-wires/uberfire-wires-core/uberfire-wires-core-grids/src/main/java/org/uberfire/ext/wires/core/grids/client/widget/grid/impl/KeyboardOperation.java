/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.wires.core.grids.client.widget.grid.impl;

import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

/**
 * Defines a keyboard based operation.
 */
public interface KeyboardOperation {

    /**
     * Gets the @{link com.google.gwt.event.dom.client.KeyCodes} that this operation matches.
     * @return A key code.
     */
    int getKeyCode();

    /**
     * Gets the shift-key state that this operation matches.
     * @return true if the shift-key is down.
     */
    TriStateBoolean isShiftKeyDown();

    /**
     * Gets the control-key state that this operation matches.
     * @return true if the control-key is down.
     */
    TriStateBoolean isControlKeyDown();

    /**
     * Decides about @link{GridWidget} state for execution of the operation.
     * By implementing this @link(BaseGridWidgetKeyboardHandler}
     * will know which registered operations can be executed.
     * @param gridWidget The GridWidget on which to perform the operation.
     * @return true if the gridWidget is in an appropriate state, false otherwise
     */
    boolean isExecutable(final GridWidget gridWidget);

    /**
     * Performs the operation on the given @{link GridWidget}
     * @param gridWidget The GridWidget on which to perform the operation.
     * @param isShiftKeyDown True if the shift-key is down.
     * @param isControlKeyDown True if the control-key is down.
     * @return true if the view needs to be rendered.
     */
    boolean perform(final GridWidget gridWidget,
                    final boolean isShiftKeyDown,
                    final boolean isControlKeyDown);

    enum TriStateBoolean {
        TRUE,
        FALSE,
        DONT_CARE
    }
}
