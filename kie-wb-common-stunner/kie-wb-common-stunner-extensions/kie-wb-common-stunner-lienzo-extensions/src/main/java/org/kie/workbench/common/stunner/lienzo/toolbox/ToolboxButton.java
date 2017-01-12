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

package org.kie.workbench.common.stunner.lienzo.toolbox;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ait.lienzo.client.core.shape.IDrawable;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.WiresUtils;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.google.gwt.user.client.Timer;
import org.kie.workbench.common.stunner.lienzo.toolbox.event.ToolboxButtonEvent;
import org.kie.workbench.common.stunner.lienzo.toolbox.event.ToolboxButtonEventHandler;
import org.kie.workbench.common.stunner.lienzo.util.LienzoPaths;

public class ToolboxButton {

    private static Logger LOGGER = Logger.getLogger( ToolboxButton.class.getName() );

    private static final int CLICK_HANDLER_TIMER_DURATION = 100;
    private static final String DECORATOR_STROKE_COLOR = "#8c8c8c";

    private final WiresShape primitive;

    public static final double ANIMATION_DURATION = 200;
    private final Layer layer;
    private final HandlerRegistrationManager handlerRegistrationManager = new HandlerRegistrationManager();
    private ToolboxButtonEventHandler clickHandler;
    private ToolboxButtonEventHandler mouseDownHandler;
    private ToolboxButtonEventHandler mouseEnterHandler;
    private ToolboxButtonEventHandler mouseExitHandler;
    private Timer clickHandlerTimer;

    private MultiPath decorator;

    public ToolboxButton( final Layer layer,
                          final IPrimitive<?> shape,
                          final int padding,
                          final int iconSize,
                          final ToolboxButtonEventHandler clickHandler,
                          final ToolboxButtonEventHandler mouseDownHandler,
                          final ToolboxButtonEventHandler mouseEnterHandler,
                          final ToolboxButtonEventHandler mouseExitHandler ) {
        this.layer = layer;
        this.clickHandler = clickHandler;
        this.mouseDownHandler = mouseDownHandler;
        this.mouseEnterHandler = mouseEnterHandler;
        this.mouseExitHandler = mouseExitHandler;
        this.primitive = build( shape,
                                iconSize,
                                padding );
        this.clickHandlerTimer = null;
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

    private WiresShape build( final IPrimitive<?> shape,
                              final int padding,
                              final int iconSize ) {
        final WiresManager manager = WiresManager.get( layer );
        // Create the wires shape multipath that will be used as decorator.
        final float dPad4 = padding / 4;
        decorator = LienzoPaths.rectangle( iconSize + padding,
                                           iconSize + padding,
                                           0 )
                .setX( 0 )
                .setY( 0 )
                .setStrokeWidth( 1.5 )
                .setStrokeAlpha( 0 )
                .setStrokeColor( DECORATOR_STROKE_COLOR )
                .setDraggable( false )
                // TODO: This is a workaround to make the mouse over/exit event handlers registered here work - review.
                .setFillAlpha( 0.01 );
        // Create and register the wires shape.
        final WiresShape wiresShape = new WiresShape( decorator ).setDraggable( false ).setResizable( false );
        manager.register( wiresShape,
                          false );
        // Add the primitive shape as child.
        wiresShape.addChild( shape.setDraggable( false ) );
        // TODO: Ensure decorator is on top to receive the differnt events - review this.
        decorator.moveToTop();
        registerShapeHandlers( wiresShape,
                               decorator );
        return wiresShape;
    }

    private void registerShapeHandlers( final WiresShape wiresShape,
                                        final IDrawable<?> shape ) {
        // Add mouse enter event handlers for the wiresshape's multipath.
        handlerRegistrationManager.register(
                shape.addNodeMouseEnterHandler( event ->
                                                        onButtonMouseEnter( shape,
                                                                            getLocation( wiresShape ),
                                                                            wiresShape.getGroup().getAbsoluteLocation(),
                                                                            event.getHumanInputEvent().getClientX(),
                                                                            event.getHumanInputEvent().getClientY() )
                ) );
        // Add mouse exit event handlers for the wiresshape's multipath.
        handlerRegistrationManager.register(
                shape.addNodeMouseExitHandler( event ->
                                                       onButtonMouseExit( shape,
                                                                          getLocation( wiresShape ),
                                                                          wiresShape.getGroup().getAbsoluteLocation(),
                                                                          event.getHumanInputEvent().getClientX(),
                                                                          event.getHumanInputEvent().getClientY() )
                ) );
        if ( null != clickHandler ) {
            // Add mouse click event handlers for the primitive shape.
            handlerRegistrationManager.register(
                    shape.addNodeMouseClickHandler( event ->
                                                            ToolboxButton.this.onButtonMouseClick( shape,
                                                                                                   getLocation( wiresShape ),
                                                                                                   wiresShape.getGroup().getAbsoluteLocation(),
                                                                                                   event.getHumanInputEvent().getClientX(),
                                                                                                   event.getHumanInputEvent().getClientY() )
                    ) );
        }
        if ( null != mouseDownHandler ) {
            // Add mouse down event handlers for the primitive shape.
            handlerRegistrationManager.register(
                    shape.addNodeMouseDownHandler( event ->
                                                           ToolboxButton.this.onButtonMouseDown( shape,
                                                                                                 getLocation( wiresShape ),
                                                                                                 wiresShape.getGroup().getAbsoluteLocation(),
                                                                                                 event.getHumanInputEvent().getClientX(),
                                                                                                 event.getHumanInputEvent().getClientY() )
                    ) );
        }
    }

    private Point2D getLocation( final WiresShape shape ) {
        return WiresUtils.getLocation( shape.getGroup() );
    }

    private void onButtonMouseEnter( final IDrawable<?> shape,
                                     final Point2D location,
                                     final Point2D abs,
                                     final int clientX,
                                     final int clientY ) {
        LOGGER.log( Level.FINE,
                    "Entering into toolbox button..." );
        showDecorator();
        if ( null != mouseEnterHandler ) {
            mouseEnterHandler.fire( buildEvent( location,
                                                abs,
                                                clientX,
                                                clientY ) );
        }
        layer.batch();
    }

    private void onButtonMouseExit( final IDrawable<?> shape,
                                    final Point2D location,
                                    final Point2D abs,
                                    final int clientX,
                                    final int clientY ) {
        LOGGER.log( Level.FINE,
                    "Exiting from toolbox button..." );
        hideDecorator();
        if ( null != mouseExitHandler ) {
            mouseExitHandler.fire( buildEvent( location,
                                               abs,
                                               clientX,
                                               clientY ) );
        }
        layer.batch();
    }

    private void onButtonMouseClick( final IDrawable<?> shape,
                                     final Point2D location,
                                     final Point2D abs,
                                     final int clientX,
                                     final int clientY ) {
        LOGGER.log( Level.FINE,
                    "Clicking on toolbox button..." );
        hideDecorator();
        ToolboxButton.this.clearClickHandlerTimer();
        clickHandler.fire(
                buildEvent( location,
                            abs,
                            clientX,
                            clientY ) );
        layer.batch();
    }

    private void onButtonMouseDown( final IDrawable<?> shape,
                                    final Point2D location,
                                    final Point2D abs,
                                    final int clientX,
                                    final int clientY ) {
        if ( null == ToolboxButton.this.clickHandlerTimer ) {
            ToolboxButton.this.clickHandlerTimer = new Timer() {
                @Override
                public void run() {
                    LOGGER.log( Level.FINE,
                                "Mouse down on toolbox button..." );
                    hideDecorator();
                    layer.batch();
                    mouseDownHandler.fire(
                            buildEvent( location,
                                        abs,
                                        clientX,
                                        clientY ) );
                    ToolboxButton.this.clickHandlerTimer = null;
                }
            };
            ToolboxButton.this.clickHandlerTimer.schedule( CLICK_HANDLER_TIMER_DURATION );
        }
    }

    private void showDecorator() {
        decorator.setStrokeAlpha( 1 );
    }

    private void hideDecorator() {
        decorator.setStrokeAlpha( 0 );
    }

    private ToolboxButtonEvent buildEvent( final Point2D location,
                                           final Point2D abs,
                                           final int clientX,
                                           final int clientY ) {
        return new ToolboxButtonEvent() {

            @Override
            public int getX() {
                return ( int ) location.getX();
            }

            @Override
            public int getY() {
                return ( int ) location.getY();
            }

            @Override
            public int getAbsoluteX() {
                return ( int ) abs.getX();
            }

            @Override
            public int getAbsoluteY() {
                return ( int ) abs.getY();
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

    private void clearClickHandlerTimer() {
        if ( null != this.clickHandlerTimer ) {
            if ( this.clickHandlerTimer.isRunning() ) {
                this.clickHandlerTimer.cancel();
            }
            this.clickHandlerTimer = null;
        }
    }
}
