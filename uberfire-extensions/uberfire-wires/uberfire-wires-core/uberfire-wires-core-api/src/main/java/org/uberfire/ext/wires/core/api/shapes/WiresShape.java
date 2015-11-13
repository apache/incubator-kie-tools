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
package org.uberfire.ext.wires.core.api.shapes;

import java.util.List;

import com.ait.lienzo.client.core.shape.Group;

public interface WiresShape {

    /**
     * Get UUID for Shape
     * @return
     */
    String getId();

    /**
     * Select the shape. Implementations may choose to change their appearance
     */
    void setSelected( final boolean isSelected );

    /**
     * Destroy the shape and any related components
     */
    void destroy();

    /**
     * Check whether the Shape contains a point
     * @param cx Canvas X coordinate
     * @param cy Canvas Y coordinate
     * @return
     */
    boolean contains( final double cx,
                      final double cy );

    /**
     * Add a Control to the WiresShape
     * @param ctrl
     */
    void addControl( final Group ctrl );

    /**
     * Remove a Control from the WiresShape
     * @param ctrl
     */
    void removeControl( final Group ctrl );

    /**
     * Set the Controls for the WiresShape
     * @param controls
     */
    void setControls( final List<Group> controls );

    /**
     * Show Controls related to this WiresShape
     */
    void showControls();

    /**
     * Hide Controls related to this WiresShape
     */
    void hideControls();

    /**
     * Are the Controls associated with this WireShape visible
     * @return true is the Controls are visible
     */
    boolean isControlsVisible();

}
