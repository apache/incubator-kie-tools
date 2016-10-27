/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.util;

import com.google.gwt.core.client.GWT;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.EdgeShape;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

import java.util.Collection;

public class ShapeUtils {

    public static String getModuleAbsolutePath( final String path ) {
        return GWT.getModuleBaseURL() + path;
    }

    public static ShapeFactory getDefaultShapeFactory( final AbstractCanvasHandler context,
                                                       final Element<? extends Definition<?>> element ) {
        final ClientDefinitionManager manager = context.getClientDefinitionManager();
        final Object def = element.getContent().getDefinition();
        final String id = manager.adapters().forDefinition().getId( def );
        return context.getShapeManager().getFactory( id );
    }

    @SuppressWarnings( "unchecked" )
    public static void applyConnections( final Edge<?, ?> edge,
                                         final CanvasHandler canvasHandler,
                                         final MutationContext mutationContext ) {
        final Canvas<?> canvas = canvasHandler.getCanvas();
        final Node sourceNode = edge.getSourceNode();
        final Node targetNode = edge.getTargetNode();
        final Shape<?> source = sourceNode != null ? canvas.getShape( sourceNode.getUUID() ) : null;
        final Shape<?> target = targetNode != null ? canvas.getShape( targetNode.getUUID() ) : null;
        EdgeShape connector = ( EdgeShape ) canvas.getShape( edge.getUUID() );
        connector.applyConnections( edge,
                source != null ? source.getShapeView() : null,
                target != null ? target.getShapeView() : null,
                mutationContext );
    }

    public static boolean isStaticMutation( final MutationContext mutationContext ) {
        return mutationContext == null || MutationContext.Type.STATIC.equals( mutationContext.getType() );
    }

    public static boolean isAnimationMutation( final Object view, final MutationContext mutationContext ) {
        return mutationContext != null && MutationContext.Type.ANIMATION.equals( mutationContext.getType() );
    }

    public static double[] getContainerXY( final Shape shape ) {
        return new double[]{ shape.getShapeView().getShapeX(),
                shape.getShapeView().getShapeY() };
    }

    /**
     * Returns the distance between two points in a dual axis cartesian graph.
     */
    public static double dist( final double x0, final double y0, final double x1, final double y1 ) {
        final double dx = Math.abs( x1 - x0 );
        final double dy = Math.abs( y1 - y0 );
        return ( Math.sqrt( ( dx * dx ) + ( dy * dy ) ) );
    }
}