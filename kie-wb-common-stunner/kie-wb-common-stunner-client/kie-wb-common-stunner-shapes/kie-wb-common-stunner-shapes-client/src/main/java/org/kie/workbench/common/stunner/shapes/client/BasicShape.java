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

package org.kie.workbench.common.stunner.shapes.client;

import org.kie.workbench.common.stunner.core.client.animation.Animation;
import org.kie.workbench.common.stunner.core.client.shape.AbstractCompositeShape;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.view.HasFillGradient;
import org.kie.workbench.common.stunner.core.client.shape.view.HasRadius;
import org.kie.workbench.common.stunner.core.client.shape.view.HasSize;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.shapes.client.view.BasicShapeView;
import org.kie.workbench.common.stunner.shapes.client.view.animatiion.BasicShapeAnimation;
import org.kie.workbench.common.stunner.shapes.client.view.animatiion.BasicShapeDecoratorAnimation;

public abstract class BasicShape<W, V extends BasicShapeView>
        extends AbstractCompositeShape<W, Node<View<W>, Edge>, V> {

    private ShapeState state = ShapeState.NONE;
    private BasicShapeAnimation animation = null;
    /*
        The following instance members:
            - _strokeWidth
            - _strokeAlpha
            - _strokeColor
        Are used to keep the original stroke attributes from the
        domain model object because the behavior for changing
        this shape state is based on updating the shape's borders.
        Eg: when the shape is in SELECTED state, the borders are
        using different colors/sizes, so to be able to get
        back to NONE state, it just reverts the border attributes
        to these private instance members.
     */
    private Double _strokeWidth = null;
    private Double _strokeAlpha = null;
    private String _strokeColor = null;

    public BasicShape( final V shapeView ) {
        super( shapeView );
    }

    protected abstract String getBackgroundColor( final Node<View<W>, Edge> element );

    protected abstract Double getBackgroundAlpha( final Node<View<W>, Edge> element );

    protected abstract String getBorderColor( final Node<View<W>, Edge> element );

    protected abstract Double getBorderSize( final Node<View<W>, Edge> element );

    protected abstract Double getBorderAlpha( final Node<View<W>, Edge> element );

    protected W getDefinition( final Node<View<W>, Edge> element ) {
        return element.getContent().getDefinition();
    }

    @Override
    public void applyProperties( final Node<View<W>, Edge> element,
                                 final MutationContext mutationContext ) {
        super.applyProperties( element,
                               mutationContext );
        // Fill color.
        final String color = getBackgroundColor( element );
        super.applyFillColor( color,
                              mutationContext );
        // Fill alpha.
        final Double alpha = getBackgroundAlpha( element );
        super.applyFillAlpha( alpha,
                              mutationContext );
        // Apply border styles.
        _strokeColor = getBorderColor( element );
        _strokeWidth = getBorderSize( element );
        super.applyBorders( _strokeColor,
                            _strokeWidth,
                            mutationContext );
        // Apply border alpha.
        _strokeAlpha = getBorderAlpha( element );
        super.applyBorderAlpha( _strokeAlpha,
                                mutationContext );
    }

    @Override
    public void beforeDraw() {
        super.beforeDraw();
        if ( hasAnimation() ) {
            getAnimation().setCallback( new Animation.AnimationCallback() {
                @Override
                public void onStart() {
                }

                @Override
                public void onFrame() {
                }

                @Override
                public void onComplete() {
                    BasicShape.this.animation = null;
                }
            } ).run();
        }
    }

    @Override
    public void applyState( final ShapeState shapeState ) {
        if ( !this.state.equals( shapeState ) ) {
            this.state = shapeState;
            if ( ShapeState.SELECTED.equals( shapeState ) ) {
                applySelectedState();
            } else if ( ShapeState.HIGHLIGHT.equals( shapeState ) ) {
                applyHighlightState();
            } else if ( ShapeState.INVALID.equals( shapeState ) ) {
                applyInvalidState();
            } else {
                applyNoneState();
            }
        }
    }

    private void applySelectedState() {
        applyActiveState( ShapeState.SELECTED.getColor() );
    }

    private void applyInvalidState() {
        applyActiveState( ShapeState.INVALID.getColor() );
    }

    private void applyHighlightState() {
        applyActiveState( ShapeState.HIGHLIGHT.getColor() );
    }

    private void applyActiveState( final String color ) {
        new BasicShapeDecoratorAnimation( color,
                                          1.5,
                                          1 ).forShape( this ).run();
    }

    private void applyNoneState() {
        new BasicShapeDecoratorAnimation( this._strokeColor,
                                          null != this._strokeWidth ? this._strokeWidth : 1,
                                          null != this._strokeAlpha ? this._strokeAlpha : 1 )
                .forShape( this )
                .run();
    }

    @Override
    protected void applyFillColor( final String color,
                                   final MutationContext mutationContext ) {
        final boolean hasGradient = getShapeView() instanceof HasFillGradient;
        // Fill gradient cannot be animated for now in lienzo.
        if ( color != null && color.trim().length() > 0
                && !hasGradient
                && isAnimationMutation( mutationContext ) ) {
            getAnimation().animateFillColor( color );
        } else {
            super.applyFillColor( color,
                                  mutationContext );
        }
    }

    @Override
    protected void applyFontAlpha( final HasTitle hasTitle,
                                   final Double alpha,
                                   final MutationContext mutationContext ) {
        final boolean isAnimation = isAnimationMutation( mutationContext );
        if ( isAnimation ) {
            getAnimation().animateFontAlpha( alpha );
        } else {
            super.applyFontAlpha( hasTitle,
                                  alpha,
                                  mutationContext );
        }
    }

    protected void _applyWidthAndHeight( final Node<View<W>, Edge> element,
                                         final Double width,
                                         final Double height,
                                         final MutationContext mutationContext ) {
        applySize( ( HasSize ) getShapeView(),
                   width,
                   height,
                   mutationContext );
    }

    @Override
    protected void applySize( final HasSize hasSize,
                              final double width,
                              final double height,
                              final MutationContext mutationContext ) {
        // TODO: Shape (multipath) resize animations.
        if ( false && isAnimationMutation( mutationContext ) ) {
            getAnimation().animateSize( width,
                                        height );
        } else {
            super.applySize( hasSize,
                             width,
                             height,
                             mutationContext );
        }
    }

    protected void _applyRadius( final Node<View<W>, Edge> element,
                                 final Double radius,
                                 final MutationContext mutationContext ) {
        if ( null != radius ) {
            applyRadius( ( HasRadius ) getShapeView(),
                         radius,
                         mutationContext );
        }
    }

    @Override
    protected void applyRadius( final HasRadius hasRadius,
                                final double radius,
                                final MutationContext mutationContext ) {
        if ( radius > 0 ) {
            // TODO: Shape (multipath) resize animations.
            if ( false && isAnimationMutation( mutationContext ) ) {
                getAnimation().animateRadius( radius );
            } else {
                super.applyRadius( hasRadius,
                                   radius,
                                   mutationContext );
            }
        }
    }

    private boolean hasAnimation() {
        return null != animation;
    }

    private BasicShapeAnimation getAnimation() {
        if ( !hasAnimation() ) {
            this.animation = new BasicShapeAnimation();
            this.animation.forShape( this );
        }
        return animation;
    }
}
