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
package org.uberfire.ext.wires.core.grids.client.widget.layer.pinning;

import com.google.gwt.user.client.Command;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

/**
 * Interface defining operations of a Manager to handle "pinning" GridWidgets. A "pinned" GridWidget
 * is scaled to 100% and positioned in the top left of the visible range.
 */
public interface GridPinnedModeManager extends IsPinnedModeAware {

    /**
     * Enters "pinned" mode. This has no effect if another GridWidget is already "pinned".
     * @param gridWidget GridWidget to "pin"
     * @param onStartCommand Command to execute on start of entry to "pinned" mode
     */
    void enterPinnedMode(final GridWidget gridWidget,
                         final Command onStartCommand);

    /**
     * Exits "pinned" mode. This has no effect if a GridWidget is not already pinned.
     * @param onCompleteCommand Command to execute on completion of exiting "pinned" mode
     */
    void exitPinnedMode(final Command onCompleteCommand);

    /**
     * Updates the context to a different GridWidget and related translation.
     * @param gridWidget GridWidget to "pin"
     * @throws IllegalStateException If "pinned" mode has not been entered.
     */
    void updatePinnedContext(final GridWidget gridWidget) throws IllegalStateException;

    /**
     * Returns the pinned Context, or null if no GridWidget is pinned.
     * @return
     */
    PinnedContext getPinnedContext();

    /**
     * Returns the default restriction when in "unpinned" mode.
     * @return
     */
    TransformMediator getDefaultTransformMediator();

    void addOnEnterPinnedModeCommand(final Command command);

    void addOnExitPinnedModeCommand(final Command command);

    /**
     * Container for the previous Viewport state; to support "unpinning" to revert to the previous Viewport transformation.
     */
    class PinnedContext {

        private GridWidget gridWidget;
        private double translateX;
        private double translateY;
        private double scaleX;
        private double scaleY;

        public PinnedContext(final GridWidget gridWidget,
                             final double translateX,
                             final double translateY,
                             final double scaleX,
                             final double scaleY) {
            this.gridWidget = gridWidget;
            this.translateX = translateX;
            this.translateY = translateY;
            this.scaleX = scaleX;
            this.scaleY = scaleY;
        }

        public GridWidget getGridWidget() {
            return gridWidget;
        }

        public double getTranslateX() {
            return translateX;
        }

        public double getTranslateY() {
            return translateY;
        }

        public double getScaleX() {
            return scaleX;
        }

        public double getScaleY() {
            return scaleY;
        }
    }
}
