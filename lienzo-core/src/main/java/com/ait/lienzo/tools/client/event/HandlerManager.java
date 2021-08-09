package com.ait.lienzo.tools.client.event;

import com.ait.lienzo.gwtlienzo.event.shared.EventHandler;
import com.ait.lienzo.tools.client.event.INodeEvent.Type;
import elemental2.core.JsArray;
import elemental2.core.JsMap;
import elemental2.core.JsSet;

public class HandlerManager {

    private Object source;

    JsMap<Type<?>, JsArray<EventHandler>> map;

    private int firingDepth = 0;

    public HandlerManager(final Object source) {
        this.source = source;
    }

    public <H extends EventHandler> boolean isEventHandled(final Type<H> type) {
        return null != map && map.has(type);
    }

    public <H extends EventHandler> HandlerRegistration addHandler(final Type<H> type, final EventHandler handler) {
        if (map == null) {
            map = new JsMap<>();
        }
        JsArray<EventHandler> handlers = map.get(type);
        if (handlers == null) {
            handlers = new JsArray<>();
            map.set(type, handlers);
        }
        handlers.push(handler);

        HandlerRegistration reg = new HandlerRegistrationImpl(type, handler, this);

        return reg;
    }

    public <H extends EventHandler> H getHandler(Type<H> type, int index) {
        return (H) map.get(type).getAt(index);
    }

    public <H extends EventHandler> int getHandlerCount(Type<H> type) {
        return map.get(type).length;
    }

    public <H> void removeHandler(Type<H> type, EventHandler handler) {
        if (map != null) {
            JsArray<EventHandler> handlers = map.get(type);

            if (handlers != null) {
                int index = handlers.indexOf(handler);
                handlers.splice(index, 1);

                // no more handlers, so prune map.
                if (handlers.length == 0) {
                    map.delete(type);
                }
            }

            if (map.size == 0) {
                map = null;
            }
        }
    }

    public void fireEvent(final INodeEvent event) {
        if (map == null) {
            // map is only created, once handlers are added
            return;
        }

        JsArray<EventHandler> handlers = map.get(event.getAssociatedType());
        if (handlers == null) {
            // no handlers for this type have been added yet
            return;
        }
        JsSet<Throwable> causes = null;
        try {
            firingDepth++;
            for (int i = 0; i < handlers.length; i++) {
                try {
                    event.dispatch(handlers.getAt(i));
                } catch (Throwable var11) {
                    if (causes == null) {
                        causes = new JsSet<>();
                    }
                    causes.add(var11);
                }
            }
            if (causes != null) {
                // @FIXME make Umbrella Exception work with JsSet (mdp)
                throw new RuntimeException(causes.values().next().getValue());
            }
        } finally {
            firingDepth--;
            if (firingDepth == 0) {
                // @FIXME fire defered (mdp)
            }
        }
    }

    public static class HandlerRegistrationImpl<H extends EventHandler> implements HandlerRegistration {

        private Type<H> type;
        private H handler;
        private HandlerManager manager;

        public HandlerRegistrationImpl(final Type<H> type, final H handler, final HandlerManager manager) {
            this.type = type;
            this.handler = handler;
            this.manager = manager;
        }

        @Override
        public void removeHandler() {
            manager.removeHandler(type, handler);
        }
    }
}