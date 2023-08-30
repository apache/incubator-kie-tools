/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.graph.processing.index.bounds;

import org.kie.workbench.common.stunner.core.graph.Element;

public interface BoundsIndexer<C, T> {

    /**
     * Builds a index of all the visible graph elements bounds for a given context ( usually a canvas or canvas handler ).
     */
    BoundsIndexer<C, T> build(final C context);

    /**
     * Return the graph element at the given x,y cartesian coordinate.
     */
    T getAt(final double x,
            final double y);

    /**
     * Return the graph element at the given area starting from x,y cartesian coordinate, checking 5 points.
     */
    T getAt(final double x,
            final double y,
            final double width,
            final double height,
            final Element parentNode);

    /**
     * Determines a rectangle area which area is given as:
     * - the top left position of the graph element found nearer to this position.
     * - the bottom right position of the graph element found nearer to this position.
     */
    double[] getTrimmedBounds();

    /**
     * Destroy this index.
     */
    void destroy();
}
