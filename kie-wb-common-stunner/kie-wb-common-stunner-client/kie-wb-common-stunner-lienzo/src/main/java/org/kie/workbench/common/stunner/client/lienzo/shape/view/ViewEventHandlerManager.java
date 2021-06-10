/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.lienzo.shape.view;

import com.ait.lienzo.client.core.event.AbstractNodeHumanInputEvent;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import elemental2.core.JsArray;
import elemental2.core.JsIteratorIterable;
import elemental2.core.JsMap;
import elemental2.dom.MouseEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseDoubleClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseEnterEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseExitEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextDoubleClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewHandler;
import org.uberfire.mvp.Command;

public class ViewEventHandlerManager {

    private static final int TIMER_DELAY = 50;

    private final JsMap<ViewEventType, JsArray<HandlerRegistration>> registrationsByType = new JsMap<>();
    private final Node<?> node;
    private final Shape<?> shape;
    private final ViewEventType[] supportedTypes;
    private final GWTTimer timer;
    private boolean enabled;

    /**
     * This is a flag used to distinguish between click / double click events fired for same node.
     * When doing mouse click on the node, this implementation schedules a timer to trigger the click handler/s, if any.
     * If just another click is done, which produces the double click event to fire, the double click handler added
     * by this implementation set the <code>fireClickHandler</code> to false, to when the previously scheduled timer
     * tries to fire the click event, it'll be fired depending on this boolean's value.
     */
    private boolean fireClickHandler;

    public ViewEventHandlerManager(final Node<?> node,
                                   final ViewEventType... supportedTypes) {
        this(node,
             null,
             supportedTypes);
    }

    public ViewEventHandlerManager(final Node<?> node,
                                   final Shape<?> shape,
                                   final ViewEventType... supportedTypes) {
        this(node,
             shape,
             new GWTTimer(TIMER_DELAY),
             supportedTypes);
    }

    ViewEventHandlerManager(final Node<?> node,
                            final Shape<?> shape,
                            final GWTTimer timer,
                            final ViewEventType... supportedTypes) {
        this.node = node;
        this.shape = shape;
        this.supportedTypes = supportedTypes;
        this.timer = timer;
        this.fireClickHandler = true;
        enable();
    }

    public void enable() {
        listen(true);
        this.enabled = true;
    }

    public void disable() {
        listen(false);
        this.enabled = false;
    }

    public boolean supports(final ViewEventType type) {
        if (null != supportedTypes) {
            for (final ViewEventType type1 : supportedTypes) {
                if (type.equals(type1)) {
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public void addHandler(final ViewEventType type,
                           final ViewHandler<? extends ViewEvent> eventHandler) {
        if (supports(type)) {
            final HandlerRegistration[] registrations = doAddHandler(type,
                                                                     eventHandler);
            addHandlersRegistration(type,
                                    registrations);
        }
    }

    @SuppressWarnings("unchecked")
    public void addHandlersRegistration(final ViewEventType type,
                                        final HandlerRegistration... registrations) {
        if (null != registrations && registrations.length > 0) {

            JsArray<HandlerRegistration> regs = registrationsByType.get(type);
            if (null == regs) {
                regs = new JsArray<>();
                registrationsByType.set(type, regs);
            }
            regs.push(registrations);
        }
    }

    @SuppressWarnings("unchecked")
    protected HandlerRegistration[] doAddHandler(final ViewEventType type,
                                                 final ViewHandler<? extends ViewEvent> eventHandler) {

        if ((ViewEventType.TEXT_DBL_CLICK.equals(type))) {
            return registerTextDoubleClickHandler((ViewHandler<ViewEvent>) eventHandler);
        }
        if (ViewEventType.MOUSE_CLICK.equals(type)) {
            return registerClickHandler((ViewHandler<ViewEvent>) eventHandler);
        }
        if (ViewEventType.MOUSE_DBL_CLICK.equals(type)) {
            return registerDoubleClickHandler((ViewHandler<ViewEvent>) eventHandler);
        }
        if (ViewEventType.MOUSE_ENTER.equals(type)) {
            return registerEnterHandler((ViewHandler<ViewEvent>) eventHandler);
        }
        if (ViewEventType.MOUSE_EXIT.equals(type)) {
            return registerExitHandler((ViewHandler<ViewEvent>) eventHandler);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public void removeHandler(final ViewHandler<? extends ViewEvent> eventHandler) {
        final ViewEventType type = eventHandler.getType();
        if (registrationsByType.has(type)) {
            removeHandlers(type);
        }
    }

    private void removeAllHandlers() {
        JsIteratorIterable<JsArray<HandlerRegistration>> regs = registrationsByType.values();
        registrationsByType.forEach(new JsMap.ForEachCallbackFn<ViewEventType, JsArray<HandlerRegistration>>() {
            @Override
            public Object onInvoke(JsArray<HandlerRegistration> p0, ViewEventType p1, JsMap<? extends ViewEventType, ? extends JsArray<HandlerRegistration>> p2) {
                removeHandlers(p0);
                return null;
            }
        });
    }

    private void removeHandlers(final ViewEventType type) {
        JsArray<HandlerRegistration> regs = registrationsByType.get(type);
        removeHandlers(regs);
    }

    private void removeHandlers(JsArray<HandlerRegistration> regs) {
        while (regs.length > 0) {
            HandlerRegistration reg = regs.pop();
            reg.removeHandler();
        }
    }

    @SuppressWarnings("unchecked")
    public void destroy() {
        timer.cancel();
        restoreClickHandler();
        removeAllHandlers();
    }

    public static MouseEvent getMouseEvent(AbstractNodeHumanInputEvent event) {
        return (MouseEvent) event.getNativeEvent();
    }

    public static double getClientX(AbstractNodeHumanInputEvent event) {
        return getMouseEvent(event).clientX;
    }

    public static double getClientY(AbstractNodeHumanInputEvent event) {
        return getMouseEvent(event).clientY;
    }

    protected HandlerRegistration[] registerEnterHandler(final ViewHandler<ViewEvent> eventHandler) {
        return new HandlerRegistration[]{
                shape.addNodeMouseEnterHandler(e -> {
                    if (isEnabled()) {
                        final MouseEnterEvent event = new MouseEnterEvent(e.getX(),
                                                                          e.getY(),
                                                                          getClientX(e),
                                                                          getClientY(e));
                        event.setShiftKeyDown(e.isShiftKeyDown());
                        event.setAltKeyDown(e.isAltKeyDown());
                        event.setMetaKeyDown(e.isMetaKeyDown());
                        eventHandler.handle(event);
                    }
                })
        };
    }

    protected HandlerRegistration[] registerExitHandler(final ViewHandler<ViewEvent> eventHandler) {
        return new HandlerRegistration[]{
                shape.addNodeMouseExitHandler(e -> {
                    if (isEnabled()) {
                        final MouseExitEvent event = new MouseExitEvent(e.getX(),
                                                                        e.getY(),
                                                                        getClientX(e),
                                                                        getClientY(e));
                        event.setShiftKeyDown(e.isShiftKeyDown());
                        event.setAltKeyDown(e.isAltKeyDown());
                        event.setMetaKeyDown(e.isMetaKeyDown());
                        eventHandler.handle(event);
                    }
                })
        };
    }

    protected HandlerRegistration[] registerClickHandler(final ViewHandler<ViewEvent> eventHandler) {
        return new HandlerRegistration[]{
                node.addNodeMouseClickHandler(nodeMouseClickEvent -> {
                    if (ViewEventHandlerManager.this.isEnabled()) {
                        restoreClickHandler();
                        final int x = nodeMouseClickEvent.getX();
                        final int y = nodeMouseClickEvent.getY();
                        final double clientX = getClientX(nodeMouseClickEvent);
                        final double clientY = getClientY(nodeMouseClickEvent);
                        final boolean isShiftKeyDown = nodeMouseClickEvent.isShiftKeyDown();
                        final boolean isAltKeyDown = nodeMouseClickEvent.isAltKeyDown();
                        final boolean isMetaKeyDown = nodeMouseClickEvent.isMetaKeyDown();
                        final boolean isButtonLeft = nodeMouseClickEvent.isButtonLeft();
                        final boolean isButtonMiddle = nodeMouseClickEvent.isButtonMiddle();
                        final boolean isButtonRight = nodeMouseClickEvent.isButtonRight();
                        timer.run(() -> {
                            if (fireClickHandler) {
                                ViewEventHandlerManager.this.onMouseClick(eventHandler,
                                                                          x,
                                                                          y,
                                                                          clientX,
                                                                          clientY,
                                                                          isShiftKeyDown,
                                                                          isAltKeyDown,
                                                                          isMetaKeyDown,
                                                                          isButtonLeft,
                                                                          isButtonMiddle,
                                                                          isButtonRight);
                            }
                        });
                    }
                })
        };
    }

    protected HandlerRegistration[] registerDoubleClickHandler(final ViewHandler<ViewEvent> eventHandler) {
        return new HandlerRegistration[]{
                node.addNodeMouseDoubleClickHandler(nodeMouseDoubleClickEvent -> {
                    if (isEnabled()) {
                        skipClickHandler();
                        final MouseDoubleClickEvent event = new MouseDoubleClickEvent(nodeMouseDoubleClickEvent.getX(),
                                                                                      nodeMouseDoubleClickEvent.getY(),
                                                                                      getClientX(nodeMouseDoubleClickEvent),
                                                                                      getClientY(nodeMouseDoubleClickEvent));
                        event.setShiftKeyDown(nodeMouseDoubleClickEvent.isShiftKeyDown());
                        event.setAltKeyDown(nodeMouseDoubleClickEvent.isAltKeyDown());
                        event.setMetaKeyDown(nodeMouseDoubleClickEvent.isMetaKeyDown());
                        event.setButtonLeft(nodeMouseDoubleClickEvent.isButtonLeft());
                        event.setButtonMiddle(nodeMouseDoubleClickEvent.isButtonMiddle());
                        event.setButtonRight(nodeMouseDoubleClickEvent.isButtonRight());
                        eventHandler.handle(event);
                        restoreClickHandler();
                    }
                })
        };
    }

    protected HandlerRegistration[] registerTextDoubleClickHandler(final ViewHandler<ViewEvent> eventHandler) {
        return new HandlerRegistration[]{
                node.addNodeMouseDoubleClickHandler(nodeMouseDoubleClickEvent -> {
                    if (isEnabled()) {
                        skipClickHandler();
                        final TextDoubleClickEvent event = new TextDoubleClickEvent(nodeMouseDoubleClickEvent.getX(),
                                                                                    nodeMouseDoubleClickEvent.getY(),
                                                                                    getMouseEvent(nodeMouseDoubleClickEvent).clientX,
                                                                                    getMouseEvent(nodeMouseDoubleClickEvent).clientY);
                        event.setShiftKeyDown(nodeMouseDoubleClickEvent.isShiftKeyDown());
                        event.setAltKeyDown(nodeMouseDoubleClickEvent.isAltKeyDown());
                        event.setMetaKeyDown(nodeMouseDoubleClickEvent.isMetaKeyDown());
                        eventHandler.handle(event);
                        restoreClickHandler();
                    }
                })
        };
    }

    public void skipClickHandler() {
        this.fireClickHandler = false;
    }

    public void restoreClickHandler() {
        this.fireClickHandler = true;
    }

    private void onMouseClick(final ViewHandler<ViewEvent> eventHandler,
                              final int x,
                              final int y,
                              final double clientX,
                              final double clientY,
                              final boolean isShiftKeyDown,
                              final boolean isAltKeyDown,
                              final boolean isMetaKeyDown,
                              final boolean isButtonLeft,
                              final boolean isButtonMiddle,
                              final boolean isButtonRight) {
        final MouseClickEvent event = new MouseClickEvent(x,
                                                          y,
                                                          clientX,
                                                          clientY);
        event.setShiftKeyDown(isShiftKeyDown);
        event.setAltKeyDown(isAltKeyDown);
        event.setMetaKeyDown(isMetaKeyDown);
        event.setButtonLeft(isButtonLeft);
        event.setButtonMiddle(isButtonMiddle);
        event.setButtonRight(isButtonRight);
        eventHandler.handle(event);
    }

    JsMap<ViewEventType, JsArray<HandlerRegistration>> getRegistrationsByType() {
        return registrationsByType;
    }

    private void listen(final boolean listen) {
        if (null != shape) {
            shape.setListening(listen);
        }
    }

    private boolean isEnabled() {
        return this.enabled;
    }

    static class GWTTimer {

        private final int delay;
        private Timer timer;

        GWTTimer(final int delay) {
            this.delay = delay;
        }

        public void run(final Command callback) {
            cancel();
            timer = new Timer() {
                @Override
                public void run() {
                    callback.execute();
                    timer = null;
                }
            };
            timer.schedule(delay);
        }

        public void cancel() {
            if (null != timer) {
                timer.cancel();
                timer = null;
            }
        }
    }
}
