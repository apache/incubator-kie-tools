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

package org.kie.workbench.common.stunner.lienzo.toolbox;

import com.ait.lienzo.client.core.animation.*;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.google.gwt.user.client.Timer;
import org.kie.workbench.common.stunner.lienzo.toolbox.builder.Button;
import org.kie.workbench.common.stunner.lienzo.toolbox.event.ToolboxButtonEvent;
import org.kie.workbench.common.stunner.lienzo.toolbox.event.ToolboxButtonEventHandler;

import java.util.List;

public class ToolboxButton {

    private static final int CLICK_HANDLER_TIMER_DURATION = 100;

    private final WiresShape primitive;

    public static final double ANIMATION_DURATION = 200;
    private final Layer layer;
    private final HandlerRegistrationManager handlerRegistrationManager = new HandlerRegistrationManager();
    private ToolboxButtonEventHandler clickHandler;
    private ToolboxButtonEventHandler mouseDownHandler;
    private ToolboxButtonEventHandler mouseEnterHandler;
    private ToolboxButtonEventHandler mouseExitHandler;
    private double iScaleX;
    private double iScaleY;
    private String iFillColor;
    private String iStrokeColor;
    private boolean isHovering;
    private boolean isHoverComplete;
    private boolean isOutRequested;
    private HoverAnimation animation;
    private Timer clickHandlerTimer;

    private MultiPath decorator;

    public enum HoverAnimation {
        ELASTIC, HOVER_COLOR;
    }

    public ToolboxButton( final Layer layer,
                          final IPrimitive<?> shape,
                          final List<Button.WhenReady> callbacks,
                          final ToolboxButtonEventHandler clickHandler,
                          final ToolboxButtonEventHandler mouseDownHandler,
                          final ToolboxButtonEventHandler mouseEnterHandler,
                          final ToolboxButtonEventHandler mouseExitHandler,
                          final HoverAnimation animation ) {
        this.layer = layer;
        this.clickHandler = clickHandler;
        this.mouseDownHandler = mouseDownHandler;
        this.mouseEnterHandler = mouseEnterHandler;
        this.mouseExitHandler = mouseExitHandler;
        this.primitive = build( shape );
        this.isHovering = false;
        this.isHoverComplete = false;
        this.isOutRequested = false;
        this.animation = animation;
        this.clickHandlerTimer = null;
        for ( Button.WhenReady callback : callbacks ) {
            callback.whenReady( this );
        }

    }

    public WiresShape getShape() {
        return primitive;
    }

    public MultiPath getDecorator() {
        return decorator;
    }

    public void remove() {
        clearClickHandlerTimer();
        handlerRegistrationManager.removeHandler();
        decorator.removeFromParent();
        primitive.removeFromParent();
        layer.batch();
    }

    private WiresShape build( final IPrimitive<?> shape ) {
        final BoundingBox bb = shape.getBoundingBox();
        decorator = new MultiPath().rect( 0.5, 0.5, bb.getWidth() + 1, bb.getHeight() + 1 )
                .setFillAlpha( 0.01 )
                .setStrokeWidth( 0 )
                .setStrokeAlpha( 0 )
                .setDraggable( false );
        final Point2D scale = shape.getScale();
        this.iFillColor = shape.getAttributes().getFillColor();
        this.iStrokeColor = shape.getAttributes().getStrokeColor();
        this.iFillColor = null != this.iFillColor ? this.iFillColor : "#000000";
        this.iStrokeColor = null != this.iStrokeColor ? this.iStrokeColor : "#000000";
        if ( null != scale ) {
            this.iScaleX = scale.getX();
            this.iScaleY = scale.getY();
            decorator.setScale( scale );

        } else {
            this.iScaleX = 1;
            this.iScaleY = 1;

        }
        WiresManager manager = WiresManager.get( layer );
        WiresShape wiresShape = new WiresShape( decorator ).setDraggable( false );
        manager.register( wiresShape, false );
        wiresShape.getContainer().add( shape.setDraggable( false ) );
        decorator.moveToTop();
        handlerRegistrationManager.register(
                wiresShape.getPath().addNodeMouseEnterHandler( event -> {
                    onButtonMouseEnter( shape );
                    if ( null != mouseEnterHandler ) {
                        mouseEnterHandler.fire( buildEvent( event.getX(), event.getY(), event.getHumanInputEvent().getClientX(), event.getHumanInputEvent().getClientY() ) );
                    }

                } )
        );
        handlerRegistrationManager.register(
                wiresShape.getPath().addNodeMouseExitHandler( event -> {
                    onButtonMouseExit( shape );
                    if ( null != mouseExitHandler ) {
                        mouseExitHandler.fire(
                                buildEvent(
                                        event.getX(),
                                        event.getY(),
                                        event.getHumanInputEvent().getClientX(),
                                        event.getHumanInputEvent().getClientY()
                                )
                        );
                    }

                } )
        );
        if ( null != clickHandler ) {
            handlerRegistrationManager.register(
                    wiresShape.getGroup().addNodeMouseClickHandler( event -> {
                                ToolboxButton.this.clearClickHandlerTimer();
                                final int x = event.getX();
                                final int y = event.getY();
                                final int clientX = event.getHumanInputEvent().getClientX();
                                final int clientY = event.getHumanInputEvent().getClientY();
                                clickHandler.fire(
                                        buildEvent( x, y, clientX, clientY ) );

                            }
                    )
            );

        }
        if ( null != mouseDownHandler ) {
            handlerRegistrationManager.register(
                    wiresShape.getGroup().addNodeMouseDownHandler( event -> {
                                final int x = event.getX();
                                final int y = event.getY();
                                final int clientX = event.getHumanInputEvent().getClientX();
                                final int clientY = event.getHumanInputEvent().getClientY();
                                if ( null == ToolboxButton.this.clickHandlerTimer ) {
                                    ToolboxButton.this.clickHandlerTimer = new Timer() {

                                        @Override
                                        public void run() {
                                            mouseDownHandler.fire(
                                                    buildEvent( x, y, clientX, clientY ) );
                                            ToolboxButton.this.clickHandlerTimer = null;

                                        }

                                    };
                                    ToolboxButton.this.clickHandlerTimer.schedule( CLICK_HANDLER_TIMER_DURATION );

                                }

                            }
                    ) );

        }
        return wiresShape;
    }

    private void onButtonMouseEnter( final IPrimitive<?> shape ) {
        if ( !isHoverComplete && !isHovering ) {
            this.isHovering = true;
            doButtonAnimate( shape, iScaleX * 2, iScaleY * 2, 2, ColorName.BLUE.getColorString(), ColorName.BLUE.getColorString(), true );

        }

    }

    private void onButtonMouseExit( final IPrimitive<?> shape ) {
        if ( isHoverComplete ) {
            doButtonAnimate( shape, iScaleX, iScaleY, 1, iFillColor, iStrokeColor, false );

        } else {
            isOutRequested = true;

        }

    }

    private void doButtonAnimate( final IPrimitive<?> shape,
                                  final double scaleX,
                                  final double scaleY,
                                  final double decoratorScale,
                                  final String fillColor,
                                  final String strokeColor,
                                  final boolean isHover ) {
        if ( HoverAnimation.ELASTIC.equals( animation ) ) {
            animateElastic( shape, scaleX, scaleY, decoratorScale, isHover );

        } else {
            animateHoverColor( shape, fillColor, strokeColor, isHover );

        }

    }

    private ToolboxButtonEvent buildEvent( final int x, final int y, final int clientX, final int clientY ) {
        return new ToolboxButtonEvent() {

            @Override
            public int getX() {
                return x;
            }

            @Override
            public int getY() {
                return y;
            }

            @Override
            public int getClientX() {
                return clientX;
            }

            @Override
            public int getClientY() {
                return clientY;
            }
        };

    }

    private void animateElastic( final IPrimitive<?> shape,
                                 final double scaleX,
                                 final double scaleY,
                                 final double decoratorScale,
                                 final boolean isHover ) {
        shape.animate(
                AnimationTweener.LINEAR,
                AnimationProperties.toPropertyList(
                        AnimationProperty.Properties.SCALE( scaleX, scaleY )
                ),
                ANIMATION_DURATION
        );
        decorator.animate(
                AnimationTweener.LINEAR,
                AnimationProperties.toPropertyList(
                        AnimationProperty.Properties.SCALE( decoratorScale )
                ),
                ANIMATION_DURATION,
                new HoverAnimationCallback( isHover, shape )
        );

    }

    private void animateHoverColor( final IPrimitive<?> shape,
                                    final String fillColor,
                                    final String strokeColor,
                                    final boolean isHover ) {
        shape.animate(
                AnimationTweener.LINEAR,
                AnimationProperties.toPropertyList(
                        AnimationProperty.Properties.FILL_COLOR( fillColor ),
                        AnimationProperty.Properties.STROKE_COLOR( strokeColor )
                ),
                ANIMATION_DURATION,
                new HoverAnimationCallback( isHover, shape )
        );

    }

    private final class HoverAnimationCallback extends AnimationCallback {

        private final boolean isHoverComplete;
        private final IPrimitive<?> shape;

        private HoverAnimationCallback( final boolean isHoverComplete,
                                        final IPrimitive<?> shape ) {
            this.isHoverComplete = isHoverComplete;
            this.shape = shape;
        }

        @Override
        public void onClose( final IAnimation animation, final IAnimationHandle handle ) {
            super.onClose( animation, handle );
            ToolboxButton.this.isHovering = false;
            ToolboxButton.this.isHoverComplete = isHoverComplete;
            if ( ToolboxButton.this.isOutRequested ) {
                onButtonMouseExit( shape );
                ToolboxButton.this.isOutRequested = false;

            }

        }

    }

    private void clearClickHandlerTimer() {
        if ( null != this.clickHandlerTimer ) {
            if ( this.clickHandlerTimer.isRunning() ) {
                this.clickHandlerTimer.cancel();
            }
            this.clickHandlerTimer = null;
        }

    }

}
