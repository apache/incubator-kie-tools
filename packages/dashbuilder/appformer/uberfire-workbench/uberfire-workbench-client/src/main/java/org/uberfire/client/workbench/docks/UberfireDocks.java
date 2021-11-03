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

package org.uberfire.client.workbench.docks;

import org.uberfire.mvp.PlaceRequest;

/**
 * Uberfire Dock Support
 */
public interface UberfireDocks {

    /**
     * Adds a {@link UberfireDock} to UberfireDocks.
     * Each {@link UberfireDock} is displayed after the
     * associated Perspective is loaded.
     * @param docks one or more docks to be added.
     */
    void add(UberfireDock... docks);

    /**
     * Removes a {@link UberfireDock} from UberfireDocks.
     * @param docks one or more docks to be added.
     */
    void remove(UberfireDock... docks);

    /**
     * Open a {@link UberfireDock} from a given perspective.
     * This opens the {@link PlaceRequest} associated with the
     * given dock on the docks panel.
     * @param dock that will be selected.
     */
    void open(UberfireDock dock);

    /**
     * Close a {@link UberfireDock} from a given perspective.
     * This closes the {@link PlaceRequest} associated with the
     * given dock and the docks panel.
     * @param dock that will be deselected.
     */
    void close(UberfireDock dock);

    /**
     * Toggle a {@link UberfireDock} from a given perspective.
     * Switch from open/close a dock
     * @param dock that will be deselected.
     */
    void toggle(UberfireDock dock);

    /**
     * Hides the docks bar associated with {@link UberfireDockPosition}
     * for a given perspective.
     * @param position the dock position that will be hidden.
     * @param perspectiveName pespective that the dock will be hidden.
     */
    void hide(UberfireDockPosition position,
              String perspectiveName);

    /**
     * Shows the docks bar associated with {@link UberfireDockPosition}
     * for a given perspective.
     * @param position the dock position that will be shown.
     * @param perspectiveName perspective that the dock will be shown.
     */
    void show(UberfireDockPosition position,
              String perspectiveName);
}
