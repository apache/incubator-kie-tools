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
package org.uberfire.ext.wires.core.api.containers;

import java.util.List;

import org.uberfire.ext.wires.core.api.shapes.WiresBaseShape;
import org.uberfire.ext.wires.core.api.shapes.WiresShape;

public interface WiresContainer extends WiresShape {

    /**
     * Attach a Shape to the Container
     * @param shape
     */
    void attachShape( final WiresBaseShape shape );

    /**
     * Detach a Shape from the Container
     * @param shape
     */
    void detachShape( final WiresBaseShape shape );

    /**
     * Get a list of WiresBaseShape contained within the Container
     * @return
     */
    List<WiresBaseShape> getContainedShapes();

    /**
     * Signal the Container is being hovered over
     */
    void setHover( final boolean isHover );

}
