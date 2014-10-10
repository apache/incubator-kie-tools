/*
 * Copyright 2012 JBoss Inc
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
package org.kie.uberfire.wires.core.api.layout;

import java.util.Map;

import com.emitrom.lienzo.client.core.shape.Layer;
import com.emitrom.lienzo.client.core.types.Point2D;
import org.kie.uberfire.wires.core.api.shapes.WiresBaseShape;

/**
 * Manager for Layout related operations
 */
public interface LayoutManager {

    /**
     * Get the layout information for the shapes
     * @param root Root element of the shapes to layout
     * @param layer The Layer containing the elements to layout
     * @return Map of Shape-to-Location information
     */
    Map<WiresBaseShape, Point2D> getLayoutInformation( final WiresBaseShape root,
                                                       final Layer layer );

}
