/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ait.lienzo.client.widget.panel.scrollbars;

import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.event.ViewportTransformChangedEvent;
import com.ait.lienzo.client.core.event.ViewportTransformChangedHandler;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.panel.mediators.RestrictedMousePanMediator;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class ScrollablePanelHandler
{
    private final ScrollablePanel panel;

    private final ScrollBounds    scrollBounds;

    static final int DEFAULT_INTERNAL_SCROLL_HEIGHT = 1;

    static final int DEFAULT_INTERNAL_SCROLL_WIDTH  = 1;

    private RestrictedMousePanMediator mousePanMediator;

    public ScrollablePanelHandler(final ScrollablePanel panel)
    {
        this.panel = panel;
        this.scrollBounds = new ScrollBounds(this);
    }

    ScrollablePanelHandler(final ScrollablePanel panel,
                           final ScrollBounds scrollBounds)
    {
        this.panel = panel;
        this.scrollBounds = scrollBounds;
    }

    public void init()
    {
        setupLienzoScrollStyle();
        setupScrollBarSynchronization();
        setupMouseDragSynchronization();
        setupContextSwitcher();
    }

    void setupContextSwitcher()
    {
        getPanel().register(
                getDomElementContainer().addDomHandler(disablePointerEvents(), MouseWheelEvent.getType())
                           );
        getPanel().register(
                getPanel().addMouseMoveHandler(enablePointerEvents())
                           );
    }

    MouseWheelHandler disablePointerEvents()
    {
        return new MouseWheelHandler()
        {
            @Override
            public void onMouseWheel(MouseWheelEvent event)
            {
                ScrollablePanelHandler.this.scrollUI().disablePointerEvents(ScrollablePanelHandler.this.getDomElementContainer());
            }
        };
    }

    MouseMoveHandler enablePointerEvents()
    {
        return new MouseMoveHandler()
        {
            @Override
            public void onMouseMove(MouseMoveEvent event)
            {
                ScrollablePanelHandler.this.scrollUI().enablePointerEvents(ScrollablePanelHandler.this.getDomElementContainer());
            }
        };
    }

    public int scrollbarWidth()
    {
        return getScrollPanel().getElement().getOffsetWidth() - getScrollPanel().getElement().getClientWidth();
    }

    public int scrollbarHeight()
    {
        return getScrollPanel().getElement().getOffsetHeight() - getScrollPanel().getElement().getClientHeight();
    }

    void setupLienzoScrollStyle()
    {
        scrollUI().setup();
    }

    ScrollUI scrollUI()
    {
        return new ScrollUI(this);
    }

    void setupScrollBarSynchronization()
    {
        getPanel().register(
                getPanel().addScrollHandler(onScroll()));
        synchronizeScrollSize();
    }

    void setupMouseDragSynchronization()
    {
        if (null != getViewport())
        {
            mousePanMediator = makeRestrictedMousePanMediator();
            getViewport().getMediators().push(mousePanMediator);

            getPanel().register(
                    getViewport()
                            .addViewportTransformChangedHandler(new ViewportScaleChangeHandler(ScrollablePanelHandler.this,
                                                                                               getViewport().getTransform()))
                               );
        }
    }

    RestrictedMousePanMediator makeRestrictedMousePanMediator()
    {
        return new RestrictedMousePanMediator(panel)
        {
            @Override
            protected void onMouseMove(final NodeMouseMoveEvent event)
            {
                refreshScrollPosition();
            }

        };
    }

    ScrollHandler onScroll()
    {
        return new ScrollHandler()
        {
            @Override
            public void onScroll(ScrollEvent event)
            {
                final ScrollBars scrollBars = scrollBars();
                ScrollablePanelHandler.this.updateLienzoPosition(scrollBars.getHorizontalScrollPosition(),
                                                                 scrollBars.getVerticalScrollPosition());
            }
        };
    }

    public void refresh()
    {
        synchronizeScrollSize();
        refreshScrollPosition();
    }

    void refreshScrollPosition()
    {
        final ScrollPosition position = scrollPosition();
        setScrollBarsPosition(position.currentRelativeX(),
                              position.currentRelativeY());
    }

    public void setScrollBarsPosition(final Double xPercentage,
                                      final Double yPercentage)
    {
        final ScrollBars scrollBars = scrollBars();
        scrollBars.setHorizontalScrollPosition(xPercentage);
        scrollBars.setVerticalScrollPosition(yPercentage);
    }

    void synchronizeScrollSize()
    {
        final int width  = calculateInternalScrollPanelWidth();
        final int height = calculateInternalScrollPanelHeight();
        getInternalScrollPanel().setPixelSize(width,
                                              height);
        getPanel().fireLienzoPanelBoundsChangedEvent();
    }

    Integer calculateInternalScrollPanelWidth()
    {
        final ScrollBounds bounds   = scrollBounds();
        final double       absWidth = bounds.maxBoundX() - bounds.minBoundX();

        if (getViewport() != null && scrollPosition().deltaX() != 0)
        {
            final Double scaleX = getViewport().getTransform().getScaleX();
            final Double width  = absWidth * scaleX;

            return width.intValue();
        }

        return DEFAULT_INTERNAL_SCROLL_WIDTH;
    }

    Integer calculateInternalScrollPanelHeight()
    {
        final ScrollBounds bounds    = scrollBounds();
        final Double       absHeight = bounds.maxBoundY() - bounds.minBoundY();

        if (getViewport() != null && scrollPosition().deltaY() != 0)
        {
            final Double scaleY = getViewport().getTransform().getScaleY();
            final Double height = absHeight * scaleY;

            return height.intValue();
        }

        return DEFAULT_INTERNAL_SCROLL_HEIGHT;
    }

    public void updateLienzoPosition(final double percentageX,
                                     final double percentageY)
    {
        final ScrollPosition position         = scrollPosition();
        final double         currentXPosition = position.currentPositionX(percentageX);
        final double         currentYPosition = position.currentPositionY(percentageY);

        updateLayerLienzoTransform(currentXPosition,
                                   currentYPosition);
        getPanel().fireLienzoPanelScrollEvent(percentageX, percentageY);
    }

    private void updateLayerLienzoTransform(final double currentXPosition,
                                            final double currentYPosition)
    {

        final Transform oldTransform = getViewport().getTransform();
        final double    dx           = currentXPosition - (oldTransform.getTranslateX() / oldTransform.getScaleX());
        final double    dy           = currentYPosition - (oldTransform.getTranslateY() / oldTransform.getScaleY());

        final Transform newTransform = oldTransform.copy().translate(dx,
                                                                     dy);

        getViewport().setTransform(newTransform);
        getLayer().batch();
    }

    RestrictedMousePanMediator getMousePanMediator()
    {
        return mousePanMediator;
    }

    AbsolutePanel getScrollPanel()
    {
        return getPanel().getScrollPanel();
    }

    AbsolutePanel getInternalScrollPanel()
    {
        return getPanel().getInternalScrollPanel();
    }

    AbsolutePanel getDomElementContainer()
    {
        return getPanel().getDomElementContainer();
    }

    ScrollablePanel getPanel()
    {
        return panel;
    }

    Layer getLayer()
    {
        return panel.getLayer();
    }

    Viewport getViewport()
    {
        return null != getLayer() ? getLayer().getViewport() : null;
    }

    public ScrollBars scrollBars()
    {
        return new ScrollBars(this);
    }

    ScrollPosition scrollPosition()
    {
        return new ScrollPosition(this);
    }

    ScrollBounds scrollBounds()
    {
        return scrollBounds;
    }

    public static class ViewportScaleChangeHandler implements ViewportTransformChangedHandler
    {
        private final ScrollablePanelHandler panelHandler;

        private double scaleX;

        private double scaleY;

        public ViewportScaleChangeHandler(final ScrollablePanelHandler panelHandler)
        {
            this(panelHandler, 1, 1);
        }

        public ViewportScaleChangeHandler(final ScrollablePanelHandler panelHandler,
                                          final double scaleX,
                                          final double scaleY)
        {
            this.panelHandler = panelHandler;
            this.scaleX = scaleX;
            this.scaleY = scaleY;
        }

        public ViewportScaleChangeHandler(final ScrollablePanelHandler panelHandler,
                                          final Transform transform)
        {
            this.panelHandler = panelHandler;
            if (null == transform)
            {
                this.scaleX = 1;
                this.scaleY = 1;
            }
            else
            {
                this.scaleX = transform.getScaleX();
                this.scaleY = transform.getScaleY();
            }
        }

        @Override
        public void onViewportTransformChanged(final ViewportTransformChangedEvent event)
        {
            final Transform newTransform = event.getViewport().getTransform();
            if (scaleX != newTransform.getScaleX() ||
                scaleY != newTransform.getScaleY())
            {
                panelHandler.refresh();
                scaleX = newTransform.getScaleX();
                scaleY = newTransform.getScaleY();
                getPanel().fireLienzoPanelScaleChangedEvent();
                final ScrollBars scrollBars = panelHandler.scrollBars();
                getPanel().fireLienzoPanelScrollEvent(scrollBars.getHorizontalScrollPosition(),
                                                 scrollBars.getVerticalScrollPosition());
            }
        }

        private ScrollablePanel getPanel() {
            return panelHandler.getPanel();
        }
    }
}
