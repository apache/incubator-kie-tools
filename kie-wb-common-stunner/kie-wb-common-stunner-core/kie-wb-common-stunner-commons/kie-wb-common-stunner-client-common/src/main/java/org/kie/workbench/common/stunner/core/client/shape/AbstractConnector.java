/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.shape;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.logging.client.LogConfiguration;
import org.kie.workbench.common.stunner.core.client.shape.view.IsConnector;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.util.ShapeUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

/**
 * A base shape impl for handling contents of edge (connector) graph elements.
 * @param <W>
 */
public abstract class AbstractConnector<W, E extends Edge<ViewConnector<W>, Node>, V extends ShapeView>
        implements EdgeShape<W, ViewConnector<W>, E, V>,
                   Lifecycle {

    private static Logger LOGGER = Logger.getLogger( AbstractConnector.class.getName() );

    protected String uuid;
    protected V view;

    public AbstractConnector( final V view ) {
        this.view = view;
    }

    @Override
    public String getUUID() {
        return uuid;
    }

    @Override
    public void setUUID( final String uuid ) {
        this.uuid = uuid;
    }

    @Override
    public V getShapeView() {
        return view;
    }

    /*
        ****************************************************************************************
        *                       CONTEXTUAL SHAPE DRAWING & UPDATES
        ****************************************************************************************
     */

    @Override
    public void beforeDraw() {
    }

    @Override
    public void afterDraw() {
    }

    @Override
    public void applyPosition( final E element,
                               final MutationContext mutationContext ) {
    }

    @Override
    public void applyProperties( final E element,
                                 final MutationContext mutationContext ) {
    }

    @Override
    public void applyProperty( final E element,
                               final String propertyId,
                               final Object value,
                               final MutationContext mutationContext ) {
    }

    @Override
    public void destroy() {
        view.destroy();
    }

    protected void _applyFillColor( final String color,
                                    final MutationContext mutationContext ) {
        if ( color != null && color.trim().length() > 0 ) {
            getShapeView().setFillColor( color );
        }
    }

    protected void _applyFillAlpha( final double alpha,
                                    final MutationContext mutationContext ) {
        getShapeView().setFillAlpha( alpha );
    }

    protected void _applyBorders( final String color,
                                  final Double width,
                                  final MutationContext mutationContext ) {
        final boolean isAnimation = isAnimationMutation( mutationContext );
        if ( color != null && color.trim().length() > 0 ) {
            _applyBorderColor( color,
                               mutationContext );
        }
        if ( width != null ) {
            _applyBorderWidth( width,
                               mutationContext );
        }
    }

    protected void _applyBorderColor( final String color,
                                      final MutationContext mutationContext ) {
        getShapeView().setStrokeColor( color );
    }

    protected void _applyBorderWidth( final double width,
                                      final MutationContext mutationContext ) {
        getShapeView().setStrokeWidth( width );
    }

    protected void _applyBorderAlpha( final double alpha,
                                      final MutationContext mutationContext ) {
        getShapeView().setStrokeAlpha( alpha );
    }

    @Override
    public void applyConnections( final E element,
                                  final ShapeView<?> source,
                                  final ShapeView<?> target,
                                  final MutationContext mutationContext ) {
        final ViewConnector connectionContent = ( ViewConnector ) element.getContent();
        final int sourceMagnet = connectionContent.getSourceMagnetIndex();
        final int targetMagnet = connectionContent.getTargetMagnetIndex();
        if ( null != source ) {
            final int z = source.getZIndex();
            view.setZIndex( z );
        }
        if ( null != source && null != target ) {
            ( ( IsConnector ) view ).connect( source,
                                              sourceMagnet,
                                              target,
                                              targetMagnet,
                                              true,
                                              false );
        }
    }

    protected boolean isStaticMutation( final MutationContext mutationContext ) {
        return ShapeUtils.isStaticMutation( mutationContext );
    }

    protected boolean isAnimationMutation( final MutationContext mutationContext ) {
        return ShapeUtils.isAnimationMutation( view,
                                               mutationContext );
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof AbstractConnector ) ) {
            return false;
        }
        AbstractConnector that = ( AbstractConnector ) o;
        return uuid != null && uuid.equals( that.uuid );
    }

    @Override
    public int hashCode() {
        return uuid == null ? 0 : ~~uuid.hashCode();
    }

    private void log( final Level level,
                      final String message ) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            LOGGER.log( level,
                        message );
        }
    }
}
