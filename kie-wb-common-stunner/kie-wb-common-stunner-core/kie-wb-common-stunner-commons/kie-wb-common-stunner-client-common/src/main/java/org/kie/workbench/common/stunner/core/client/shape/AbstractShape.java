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

import org.kie.workbench.common.stunner.core.client.canvas.Point2D;
import org.kie.workbench.common.stunner.core.client.shape.view.*;
import org.kie.workbench.common.stunner.core.client.util.ShapeUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

import java.util.logging.Logger;

/**
 * A base shape impl for handling contents of node graph elements.
 */
public abstract class AbstractShape<W, E extends Node<View<W>, Edge>, V extends ShapeView>
        implements
        NodeShape<W, View<W>, E, V>,
        Lifecycle {

    private static Logger LOGGER = Logger.getLogger( AbstractShape.class.getName() );

    private String uuid;
    private V view;

    public AbstractShape( final V view ) {
        this.view = view;
    }

    public void setUUID( final String uuid ) {
        this.uuid = uuid;
    }

    @Override
    public String getUUID() {
        return uuid;
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
        if ( view instanceof HasTitle ) {
            ( ( HasTitle ) view ).moveTitleToTop();
        }
    }

    @Override
    public void applyPosition( final E element, final MutationContext mutationContext ) {
        final Point2D position = GraphUtils.getPosition( element.getContent() );
        view.setShapeX( position.getX() );
        view.setShapeY( position.getY() );

    }

    @Override
    public void applyProperties( final E element, final MutationContext mutationContext ) {
        // The graph element's name.
        _applyElementName( element, mutationContext );

    }

    @Override
    public void applyProperty( final E element,
                               final String propertyId,
                               final Object value,
                               final MutationContext mutationContext ) {
        // TODO
    }

    protected void applyFillColor( final String color, final MutationContext mutationContext ) {
        if ( color != null && color.trim().length() > 0 ) {
            final boolean hasGradient = view instanceof HasFillGradient;
            if ( !hasGradient ) {
                view.setFillColor( color );

            } else {
                ( ( HasFillGradient ) view ).setFillGradient( HasFillGradient.Type.LINEAR, color, "#FFFFFF" );

            }

        }
    }

    protected void applyFillAlpha( final Double alpha, final MutationContext mutationContext ) {
        if ( null != alpha ) {
            view.setFillAlpha( alpha );
        }
    }

    protected void applyBorders( final String color, final Double width, final MutationContext mutationContext ) {
        if ( color != null && color.trim().length() > 0 ) {
            view.setStrokeColor( color );
        }
        if ( width != null ) {
            view.setStrokeWidth( width );
        }
    }

    protected void applyBorderAlpha( final Double alpha, final MutationContext mutationContext ) {
        if ( null != alpha ) {
            view.setStrokeAlpha( alpha );
        }
    }

    protected void applyFont( final String family,
                              final String color,
                              final Double size,
                              final Double borderSize,
                              final Double alpha,
                              final MutationContext mutationContext ) {
        if ( view instanceof HasTitle ) {
            final HasTitle hasTitle = ( HasTitle ) view;
            if ( family != null && family.trim().length() > 0 ) {
                hasTitle.setTitleFontFamily( family );
            }
            if ( color != null && color.trim().length() > 0 ) {
                hasTitle.setTitleStrokeColor( color );
            }
            if ( size != null && size > 0 ) {
                hasTitle.setTitleFontSize( size );

            }
            applyFontAlpha( hasTitle, alpha, mutationContext );
            if ( borderSize != null && borderSize > 0 ) {
                hasTitle.setTitleStrokeWidth( borderSize );
            }
            // Refresh to update size changes etc.
            hasTitle.refreshTitle();

        }
    }

    protected void applyFontAlpha( final HasTitle hasTitle,
                                   final Double alpha,
                                   final MutationContext mutationContext ) {
        if ( null != alpha ) {
            hasTitle.setTitleAlpha( alpha );
        }

    }

    protected void applySize( final HasSize hasSize, final double width, final double height, final MutationContext mutationContext ) {
        hasSize.setSize( width, height );

    }

    protected void applyRadius( final HasRadius hasRadius, final double radius, final MutationContext mutationContext ) {
        if ( radius > 0 ) {
            hasRadius.setRadius( radius );

        }

    }

    protected String getNamePropertyValue( final E element ) {
        return null;
    }

    protected void _applyElementName( final E element, final MutationContext mutationContext ) {
        if ( view instanceof HasTitle ) {
            final HasTitle hasTitle = ( HasTitle ) view;
            final String name = getNamePropertyValue( element );
            hasTitle.setTitle( name );
            hasTitle.refreshTitle();

        }

    }

    protected abstract void doDestroy();

    @Override
    public void destroy() {
        doDestroy();
        view.destroy();
    }

    protected boolean isStaticMutation( final MutationContext mutationContext ) {
        return ShapeUtils.isStaticMutation( mutationContext );
    }

    protected boolean isAnimationMutation( final MutationContext mutationContext ) {
        return ShapeUtils.isAnimationMutation( view, mutationContext );
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof AbstractShape ) ) {
            return false;
        }
        AbstractShape that = ( AbstractShape ) o;
        return uuid != null && uuid.equals( that.uuid );
    }

    @Override
    public int hashCode() {
        return uuid == null ? 0 : ~~uuid.hashCode();
    }
}
