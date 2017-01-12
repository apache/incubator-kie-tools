/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.lienzo.canvas.wires;

import com.ait.lienzo.client.core.shape.IDrawable;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Point2D;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;

public final class WiresUtils {

    public static Point2D getAbsolute( final IDrawable<?> shape ) {
        final com.ait.lienzo.client.core.types.Point2D p = com.ait.lienzo.client.core.shape.wires.WiresUtils.getLocation( shape );
        return new Point2D( p.getX(),
                            p.getY() );
    }

    public static Node getNode( final AbstractCanvasHandler canvasHandler,
                                final WiresContainer shape ) {
        if ( null == shape ) {
            return null;
        }
        if ( shape instanceof ShapeView ) {
            final ShapeView view = ( ShapeView ) shape;
            return canvasHandler.getGraphIndex().getNode( view.getUUID() );
        } else if ( shape instanceof WiresLayer ) {
            final String canvasRoot = canvasHandler.getDiagram().getMetadata().getCanvasRootUUID();
            if ( null != canvasRoot ) {
                return canvasHandler.getGraphIndex().getNode( canvasRoot );
            }
        }
        return null;
    }

    public static Node getNode( final AbstractCanvasHandler canvasHandler,
                                final WiresMagnet magnet ) {
        if ( null == magnet ) {
            return null;
        }
        final WiresShape shape = magnet.getMagnets().getWiresShape();
        return getNode( canvasHandler,
                        shape );
    }

    public static Edge getEdge( final AbstractCanvasHandler canvasHandler,
                                final WiresConnector connector ) {
        if ( connector instanceof ShapeView ) {
            final ShapeView view = ( ShapeView ) connector;
            return canvasHandler.getGraphIndex().getEdge( view.getUUID() );
        }
        return null;
    }

    public static boolean isWiresShape( final ShapeView<?> shapeView ) {
        return shapeView instanceof WiresShape;
    }

    public static boolean isWiresContainer( final ShapeView<?> shapeView ) {
        return shapeView instanceof WiresContainer;
    }

    public static boolean isWiresConnector( final ShapeView<?> shapeView ) {
        return shapeView instanceof WiresConnector;
    }
}
