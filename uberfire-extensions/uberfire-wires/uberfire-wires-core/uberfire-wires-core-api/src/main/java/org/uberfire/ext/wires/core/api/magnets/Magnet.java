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
package org.uberfire.ext.wires.core.api.magnets;

import java.util.List;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Node;
import org.uberfire.ext.wires.core.api.controlpoints.ControlPoint;

public interface Magnet<T extends Node<T>> extends IPrimitive<T> {

    /**
     * Get UUID for Magnet
     * @return
     */
    String getId();

    /**
     * Attach a ControlPoint to the Magnet
     * @param controlPoint
     */
    void attachControlPoint( final ControlPoint controlPoint );

    /**
     * Detach a ControlPoint from the Magnet
     * @param controlPoint
     */
    void detachControlPoint( final ControlPoint controlPoint );

    /**
     * Get a list of ControlPoints attached to the Magnet
     * @return
     */
    List<ControlPoint> getAttachedControlPoints();

    /**
     * Activate the Magnet
     * @param isActive
     */
    void setActive( final boolean isActive );

    void move( final double dx,
               final double dy );

}
