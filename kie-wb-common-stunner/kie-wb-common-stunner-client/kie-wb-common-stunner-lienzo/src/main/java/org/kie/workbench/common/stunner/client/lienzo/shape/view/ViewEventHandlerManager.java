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

package org.kie.workbench.common.stunner.client.lienzo.shape.view;

import com.ait.lienzo.client.core.event.*;
import com.ait.lienzo.client.core.shape.Node;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import org.kie.workbench.common.stunner.core.client.shape.view.event.*;

import java.util.HashMap;
import java.util.Map;

public class ViewEventHandlerManager {

    private static final int CLICK_HANDLER_TIMER_DURATION = 150;

    protected final HandlerRegistrationImpl registrationManager = new HandlerRegistrationImpl();
    protected final Map<ViewEventType, HandlerRegistration[]> registrationMap = new HashMap<>();

    private final Node<?> node;
    private final ViewEventType[] supportedTypes;
    private Timer clickHandlerTimer;
    private boolean enabled;

    public ViewEventHandlerManager( final Node<?> node,
                                    final ViewEventType... supportedTypes ) {
        this.node = node;
        this.supportedTypes = supportedTypes;
        this.clickHandlerTimer = null;
        enable();
    }

    public void enable() {
        this.enabled = true;

    }

    public void disable() {
        this.enabled = false;

    }

    private boolean isEnabled() {
        return this.enabled;
    }

    public boolean supports( final ViewEventType type ) {
        if ( null != supportedTypes ) {
            for ( final ViewEventType type1 : supportedTypes ) {
                if ( type.equals( type1 ) ) {
                    return true;
                }
            }

        }
        return false;

    }

    @SuppressWarnings( "unchecked" )
    public void addHandler( final ViewEventType type,
                            final ViewHandler<? extends ViewEvent> eventHandler ) {
        if ( supports( type ) ) {
            final HandlerRegistration[] registrations = doAddHandler( type, eventHandler );
            addHandlersRegistration( type, registrations );

        }

    }

    @SuppressWarnings( "unchecked" )
    public void addHandlersRegistration( final ViewEventType type,
                                         final HandlerRegistration... registrations ) {
        if ( null != registrations && registrations.length > 0 ) {
            registrationMap.put( type, registrations );
            for ( final HandlerRegistration registration : registrations ) {
                registrationManager.register( registration );

            }

        }

    }

    @SuppressWarnings( "unchecked" )
    protected HandlerRegistration[] doAddHandler( final ViewEventType type,
                                                  final ViewHandler<? extends ViewEvent> eventHandler ) {
        if ( ViewEventType.MOUSE_CLICK.equals( type ) ) {
            return registerClickHandler( ( ViewHandler<ViewEvent> ) eventHandler );
        }
        if ( ViewEventType.MOUSE_DBL_CLICK.equals( type ) ) {
            return registerDoubleClickHandler( ( ViewHandler<ViewEvent> ) eventHandler );
        }
        if ( ViewEventType.TOUCH.equals( type ) ) {
            return registerTouchHandler( ( TouchHandler ) eventHandler );
        }
        if ( ViewEventType.GESTURE.equals( type ) ) {
            return registerGestureHandler( ( GestureHandler ) eventHandler );
        }
        return null;

    }

    @SuppressWarnings( "unchecked" )
    public void removeHandler( final ViewHandler<? extends ViewEvent> eventHandler ) {
        final ViewEventType type = eventHandler.getType();
        if ( registrationMap.containsKey( type ) ) {
            final HandlerRegistration[] registrations = registrationMap.get( type );
            if ( null != registrations && registrations.length > 0 ) {
                for ( final HandlerRegistration registration : registrations ) {
                    registrationManager.deregister( registration );

                }

            }

        }

    }

    @SuppressWarnings( "unchecked" )
    public void destroy() {
        clearClickHandlerTimer();
        registrationManager.removeHandler();
        registrationMap.clear();

    }

    protected HandlerRegistration[] registerGestureHandler( final GestureHandler gestureHandler ) {
        HandlerRegistration gestureStartReg = node.addNodeGestureStartHandler( new NodeGestureStartHandler() {
            @Override
            public void onNodeGestureStart( final NodeGestureStartEvent event ) {
                if ( isEnabled() ) {
                    final GestureEvent event1 = buildGestureEvent( event );
                    if ( null != event1 ) {
                        gestureHandler.start( event1 );

                    }

                }

            }
        } );
        HandlerRegistration gestureChangeReg = node.addNodeGestureChangeHandler( new NodeGestureChangeHandler() {
            @Override
            public void onNodeGestureChange( final NodeGestureChangeEvent event ) {
                if ( isEnabled() ) {
                    final GestureEvent event1 = buildGestureEvent( event );
                    if ( null != event1 ) {
                        gestureHandler.change( event1 );

                    }

                }

            }

        } );
        HandlerRegistration gestureEndReg = node.addNodeGestureEndHandler( new NodeGestureEndHandler() {
            @Override
            public void onNodeGestureEnd( final NodeGestureEndEvent event ) {
                if ( isEnabled() ) {
                    final GestureEvent event1 = buildGestureEvent( event );
                    if ( null != event1 ) {
                        gestureHandler.end( event1 );

                    }

                }

            }

        } );
        return new HandlerRegistration[]{
                gestureStartReg, gestureChangeReg, gestureEndReg
        };

    }

    protected GestureEventImpl buildGestureEvent( final AbstractNodeGestureEvent event ) {
        return new GestureEventImpl( event.getScale(), event.getRotation() );

    }

    protected HandlerRegistration[] registerClickHandler( final ViewHandler<ViewEvent> eventHandler ) {
        return new HandlerRegistration[]{
                node.addNodeMouseClickHandler( nodeMouseClickEvent -> {
                    if ( ViewEventHandlerManager.this.isEnabled() ) {
                        final int x = nodeMouseClickEvent.getX();
                        final int y = nodeMouseClickEvent.getY();
                        final int clientX = nodeMouseClickEvent.getMouseEvent().getClientX();
                        final int clientY = nodeMouseClickEvent.getMouseEvent().getClientY();
                        final boolean isShiftKeyDown = nodeMouseClickEvent.isShiftKeyDown();
                        final boolean isAltKeyDown = nodeMouseClickEvent.isAltKeyDown();
                        final boolean isMetaKeyDown = nodeMouseClickEvent.isMetaKeyDown();
                        final boolean isButtonLeft = nodeMouseClickEvent.isButtonLeft();
                        final boolean isButtonMiddle = nodeMouseClickEvent.isButtonMiddle();
                        final boolean isButtonRight = nodeMouseClickEvent.isButtonRight();
                        if ( null == ViewEventHandlerManager.this.clickHandlerTimer ) {
                            ViewEventHandlerManager.this.clickHandlerTimer = new Timer() {

                                @Override
                                public void run() {
                                    ViewEventHandlerManager.this.onMouseClick( eventHandler,
                                            x,
                                            y,
                                            clientX,
                                            clientY,
                                            isShiftKeyDown,
                                            isAltKeyDown,
                                            isMetaKeyDown,
                                            isButtonLeft,
                                            isButtonMiddle,
                                            isButtonRight );
                                    ViewEventHandlerManager.this.clickHandlerTimer = null;

                                }

                            };
                            ViewEventHandlerManager.this.clickHandlerTimer.schedule( CLICK_HANDLER_TIMER_DURATION );

                        }

                    }

                } )

        };

    }

    protected HandlerRegistration[] registerDoubleClickHandler( final ViewHandler<ViewEvent> eventHandler ) {
        return new HandlerRegistration[]{
                node.addNodeMouseDoubleClickHandler( nodeMouseDoubleClickEvent -> {
                    if ( isEnabled() ) {
                        clearClickHandlerTimer();
                        final MouseDoubleClickEvent event = new MouseDoubleClickEvent(
                                nodeMouseDoubleClickEvent.getX(),
                                nodeMouseDoubleClickEvent.getY(),
                                nodeMouseDoubleClickEvent.getMouseEvent().getClientX(),
                                nodeMouseDoubleClickEvent.getMouseEvent().getClientY() );
                        event.setShiftKeyDown( nodeMouseDoubleClickEvent.isShiftKeyDown() );
                        event.setAltKeyDown( nodeMouseDoubleClickEvent.isAltKeyDown() );
                        event.setMetaKeyDown( nodeMouseDoubleClickEvent.isMetaKeyDown() );
                        event.setButtonLeft( nodeMouseDoubleClickEvent.isButtonLeft() );
                        event.setButtonMiddle( nodeMouseDoubleClickEvent.isButtonMiddle() );
                        event.setButtonRight( nodeMouseDoubleClickEvent.isButtonRight() );
                        eventHandler.handle( event );

                    }

                } )

        };

    }

    private void onMouseClick( final ViewHandler<ViewEvent> eventHandler,
                               final int x,
                               final int y,
                               final int clientX,
                               final int clientY,
                               final boolean isShiftKeyDown,
                               final boolean isAltKeyDown,
                               final boolean isMetaKeyDown,
                               final boolean isButtonLeft,
                               final boolean isButtonMiddle,
                               final boolean isButtonRight ) {
        final MouseClickEvent event = new MouseClickEvent( x, y, clientX, clientY );
        event.setShiftKeyDown( isShiftKeyDown );
        event.setAltKeyDown( isAltKeyDown );
        event.setMetaKeyDown( isMetaKeyDown );
        event.setButtonLeft( isButtonLeft );
        event.setButtonMiddle( isButtonMiddle );
        event.setButtonRight( isButtonRight );
        eventHandler.handle( event );

    }

    protected HandlerRegistration[] registerTouchHandler( final TouchHandler touchHandler ) {
        HandlerRegistration touchStartReg = node.addNodeTouchStartHandler( event -> {
            if ( isEnabled() ) {
                final TouchEventImpl event1 = buildTouchEvent( event );
                if ( null != event1 ) {
                    touchHandler.start( event1 );

                }

            }

        } );
        HandlerRegistration touchMoveReg = node.addNodeTouchMoveHandler( event -> {
            if ( isEnabled() ) {
                final TouchEventImpl event1 = buildTouchEvent( event );
                if ( null != event1 ) {
                    touchHandler.move( event1 );

                }

            }

        } );
        HandlerRegistration touchEndReg = node.addNodeTouchEndHandler( event -> {
            if ( isEnabled() ) {
                final TouchEventImpl event1 = buildTouchEvent( event );
                if ( null != event1 ) {
                    touchHandler.end( event1 );

                }

            }

        } );
        HandlerRegistration touchCancelReg = node.addNodeTouchCancelHandler( event -> {
            if ( isEnabled() ) {
                final TouchEventImpl event1 = buildTouchEvent( event );
                if ( null != event1 ) {
                    touchHandler.cancel( event1 );

                }

            }

        } );
        return new HandlerRegistration[]{
                touchStartReg, touchMoveReg, touchEndReg, touchCancelReg
        };

    }

    private TouchEventImpl buildTouchEvent( final AbstractNodeTouchEvent event ) {
        final TouchPoint touchPoint = null != event.getTouches() && !event.getTouches().isEmpty() ?
                ( TouchPoint ) event.getTouches().get( 0 ) : null;
        if ( null != touchPoint ) {
            final int tx = touchPoint.getX();
            final int ty = touchPoint.getY();
            return new TouchEventImpl( event.getX(), event.getY(), tx, ty );
        }
        return null;
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
