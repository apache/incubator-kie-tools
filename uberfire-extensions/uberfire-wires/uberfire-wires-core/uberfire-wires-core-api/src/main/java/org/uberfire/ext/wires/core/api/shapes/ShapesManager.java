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

public interface ShapesManager {

    /**
     * Get a list of all Shapes on the Canvas
     * @return
     */
    List<WiresBaseShape> getShapesInCanvas();

    /**
     * Add a Shape to the Canvas
     * @param shape
     */
    void addShape( final WiresBaseShape shape );

    /**
     * Delete a Shape from the Canvas. Implementations may prompt the User for confirmation.
     * @param shape
     */
    void deleteShape( final WiresBaseShape shape );

    /**
     * Forcefully delete a Shape from the Canvas. This allows deletion of Shapes without prompting the User for confirmation.
     * @param shape
     */
    void forceDeleteShape( final WiresBaseShape shape );

}
