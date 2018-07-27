package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorHandler;
import com.ait.tooling.common.api.java.util.function.Consumer;
import com.google.gwt.user.client.Timer;

public class WiresConnectorHandlerImpl implements WiresConnectorHandler {

    private final WiresConnector m_connector;
    private final WiresManager m_wiresManager;
    private final Consumer<Event> clickEventConsumer;
    private final Consumer<Event> doubleClickEventConsumer;
    private final Timer clickTimer;
    private Event event;

    public static class Event {
        final double x;
        final double y;
        final boolean isShiftKeyDown;

        public Event(final double x,
                     final double y,
                     final boolean isShiftKeyDown)
        {
            this.x = x;
            this.y = y;
            this.isShiftKeyDown = isShiftKeyDown;
        }
    }

    public static WiresConnectorHandlerImpl build(final WiresConnector connector,
                                                   final WiresManager wiresManager) {
        final WiresConnectorEventConsumers consumers = new WiresConnectorEventConsumers(connector);
        return new WiresConnectorHandlerImpl(connector,
                                              wiresManager,
                                              consumers.switchVisibility(),
                                              consumers.addControlPoint());
    }

    public WiresConnectorHandlerImpl(final WiresConnector connector,
                                      final WiresManager wiresManager,
                                      final Consumer<Event> clickEventConsumer,
                                      final Consumer<Event> doubleClickEventConsumer) {
        this.m_connector = connector;
        this.m_wiresManager = wiresManager;
        this.clickEventConsumer = clickEventConsumer;
        this.doubleClickEventConsumer = doubleClickEventConsumer;
        this.clickTimer = new Timer() {
            @Override
            public void run()
            {
                if (getWiresManager().getSelectionManager() != null) {
                    getWiresManager().getSelectionManager().selected(connector,
                                                                     event.isShiftKeyDown);
                }
                clickEventConsumer.accept(event);
                event = null;
            }
        };
    }

    WiresConnectorHandlerImpl(final WiresConnector connector,
                              final WiresManager wiresManager,
                              final Consumer<Event> clickEventConsumer,
                              final Consumer<Event> doubleClickEventConsumer,
                              final Timer clickTimer) {
        this.m_connector = connector;
        this.m_wiresManager = wiresManager;
        this.clickEventConsumer = clickEventConsumer;
        this.doubleClickEventConsumer = doubleClickEventConsumer;
        this.clickTimer = clickTimer;
    }

    @Override
    public void onNodeDragStart(final NodeDragStartEvent event) {
        this.getControl().onMoveStart(event.getDragContext().getDragStartX(),
                                      event.getDragContext().getDragStartY());
    }

    @Override
    public void onNodeDragMove(final NodeDragMoveEvent event) {
        this.getControl().onMove(event.getDragContext().getDragStartX(),
                                 event.getDragContext().getDragStartY());
    }

    @Override
    public void onNodeDragEnd(final NodeDragEndEvent event) {
        if (getControl().onMoveComplete()) {
            getControl().execute();
        } else {
            getControl().reset();
        }
    }

    @Override
    public void onNodeMouseClick(final NodeMouseClickEvent event) {
        if (clickTimer.isRunning()) {
            clickTimer.cancel();
        }
        this.event = new Event(event.getX(),
                               event.getY(),
                               event.isShiftKeyDown());
        clickTimer.schedule(150);
    }

    @Override
    public void onNodeMouseDoubleClick(final NodeMouseDoubleClickEvent event) {
        clickTimer.cancel();
        doubleClickEventConsumer.accept(new Event(event.getX(),
                                                  event.getY(),
                                                  event.isShiftKeyDown()));
    }

    public static class WiresConnectorEventConsumers {
        private final WiresConnector connector;

        public WiresConnectorEventConsumers(final WiresConnector connector) {
            this.connector = connector;
        }

        public Consumer<Event> switchVisibility() {
            return new Consumer<Event>() {
                @Override
                public void accept(Event event) {
                    final WiresConnectorControl control = connector.getControl();
                    if (control.areControlPointsVisible()) {
                        control.hideControlPoints();
                    } else {
                        control.showControlPoints();
                    }
                }
            };
        }

        public Consumer<Event> addControlPoint() {
            return new Consumer<Event>() {
                @Override
                public void accept(Event event) {
                    connector.getControl().addControlPoint(event.x, event.y);
                }
            };
        }

    }

    public WiresConnectorControl getControl() {
        return m_connector.getControl();
    }

    WiresConnector getConnector() {
        return m_connector;
    }

    WiresManager getWiresManager() {
        return m_wiresManager;
    }

}