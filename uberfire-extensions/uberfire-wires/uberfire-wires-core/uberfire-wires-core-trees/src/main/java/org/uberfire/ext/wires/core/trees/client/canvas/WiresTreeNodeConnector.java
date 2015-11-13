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
package org.uberfire.ext.wires.core.trees.client.canvas;

import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.shared.core.types.LineCap;
import org.uberfire.ext.wires.core.client.util.ShapesUtils;

/**
 * A connector between Tree Nodes
 */
public class WiresTreeNodeConnector extends Line {

    public WiresTreeNodeConnector() {
        setStrokeColor( ShapesUtils.RGB_STROKE_SHAPE )
                .setStrokeWidth( ShapesUtils.RGB_STROKE_WIDTH_SHAPE )
                .setFillColor( ShapesUtils.RGB_FILL_SHAPE )
                .setLineCap( LineCap.ROUND )
                .setStrokeWidth( 3 )
                .setDraggable( false );
    }

}
