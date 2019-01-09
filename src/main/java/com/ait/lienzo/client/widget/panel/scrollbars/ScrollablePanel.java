/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.client.widget.panel.BoundsProvider;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.client.widget.panel.event.*;
import com.ait.lienzo.client.widget.panel.impl.LienzoPanelImpl;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;

import java.util.Objects;

public class ScrollablePanel extends LienzoBoundsPanel
{
    private final AbsolutePanel domElementContainer = new AbsolutePanel();

    private final AbsolutePanel internalScrollPanel = new AbsolutePanel();

    private final AbsolutePanel scrollPanel         = new AbsolutePanel();

    private final AbsolutePanel rootPanel           = new AbsolutePanel();

    private final HandlerRegistrationManager handlers;

    private final HandlerManager             m_events;

    private final ScrollablePanelHandler     scrollHandler;

    private       int                        wide;

    private       int                        high;

    private boolean isMouseDown = false;

    public ScrollablePanel(final BoundsProvider layerBoundsProvider)
    {
        this(new LienzoPanelImpl()
             {
                 @Override
                 public void onResize()
                 {
                     // Do nothing. Resize is handled by AttachHandler. LienzoPanel calls onResize() in
                     // it's onAttach() method which causes the Canvas to be redrawn. However when LienzoPanel
                     // is adopted by another Widget LienzoPanel's onAttach() is called before its children
                     // have been attached. Should redraw require children to be attached errors arise.
                 }
             },
             layerBoundsProvider);
    }

    public ScrollablePanel(final BoundsProvider layerBoundsProvider,
                           final int width,
                           final int height)
    {
        this(new LienzoPanelImpl(width,
                                 height)
             {
                 @Override
                 public void onResize()
                 {
                     // Do nothing. Resize is handled by AttachHandler. LienzoPanel calls onResize() in
                     // it's onAttach() method which causes the Canvas to be redrawn. However when LienzoPanel
                     // is adopted by another Widget LienzoPanel's onAttach() is called before its children
                     // have been attached. Should redraw require children to be attached errors arise.
                 }
             },
             layerBoundsProvider);

        updateSize(width,
                   height);
    }

    private ScrollablePanel(final LienzoPanel lienzoPanel,
                            final BoundsProvider layerBoundsProvider)
    {
        super(lienzoPanel,
              layerBoundsProvider);
        this.m_events = new HandlerManager(this);
        this.scrollHandler = new ScrollablePanelHandler(this);
        this.handlers = new HandlerRegistrationManager();
        setupPanels();
        setupHandlers();
    }

    ScrollablePanel(final LienzoPanel lienzoPanel,
                    final BoundsProvider layerBoundsProvider,
                    final HandlerManager handlerManager,
                    final ScrollablePanelHandler panelHandler,
                    final HandlerRegistrationManager handlers)
    {
        super(lienzoPanel,
              layerBoundsProvider);
        this.m_events = handlerManager;
        this.scrollHandler = panelHandler;
        this.handlers = handlers;
        setupPanels();
        setupHandlers();
    }

    @Override
    public LienzoBoundsPanel set(final Layer layer)
    {
        super.set(layer);
        getScrollHandler().init();
        return this;
    }

    public Bounds getVisibleBounds()
    {
        if (null != getLayer())
        {
            final Viewport viewport  = getLayer().getViewport();
            Transform      transform = viewport.getTransform();
            if (transform == null)
            {
                viewport.setTransform(transform = new Transform());
            }
            final double x      = transform.getTranslateX() / transform.getScaleX();
            final double y      = transform.getTranslateY() / transform.getScaleY();
            final Bounds bounds = Bounds.empty();
            bounds.setX(x);
            bounds.setY(y);
            bounds.setHeight(Math.max(0,
                                      viewport.getHeight() / transform.getScaleX()));
            bounds.setWidth(Math.max(0,
                                     viewport.getWidth() / transform.getScaleY()));
            return bounds;
        }
        return null;
    }

    HandlerRegistration addScrollHandler(final ScrollHandler handler)
    {
        return getScrollPanel().addDomHandler(handler,
                                              ScrollEvent.getType());
    }

    public final HandlerRegistration addLienzoPanelBoundsChangedEventHandler(final LienzoPanelBoundsChangedEventHandler handler)
    {
        Objects.requireNonNull(handler);

        return m_events.addHandler(LienzoPanelBoundsChangedEvent.TYPE, handler);
    }

    void fireLienzoPanelBoundsChangedEvent()
    {
        m_events.fireEvent(new LienzoPanelBoundsChangedEvent());
    }

    public final HandlerRegistration addLienzoPanelScrollEventHandler(final LienzoPanelScrollEventHandler handler)
    {
        Objects.requireNonNull(handler);

        return m_events.addHandler(LienzoPanelScrollEvent.TYPE, handler);
    }

    void fireLienzoPanelScrollEvent(final double pctX,
                                    final double pctY)
    {
        m_events.fireEvent(new LienzoPanelScrollEvent(pctX, pctY));
    }

    public final HandlerRegistration addLienzoPanelResizeEventHandler(final LienzoPanelResizeEventHandler handler)
    {
        Objects.requireNonNull(handler);

        return m_events.addHandler(LienzoPanelResizeEvent.TYPE, handler);
    }

    void fireLienzoPanelResizeEvent(final double width,
                                    final double height)
    {
        m_events.fireEvent(new LienzoPanelResizeEvent(width, height));
    }

    public final HandlerRegistration addLienzoPanelScaleChangedEventHandler(final LienzoPanelScaleChangedEventHandler handler)
    {
        Objects.requireNonNull(handler);

        return m_events.addHandler(LienzoPanelScaleChangedEvent.TYPE, handler);
    }

    void fireLienzoPanelScaleChangedEvent()
    {
        final Transform transform = getLayer().getViewport().getTransform();
        m_events.fireEvent(new LienzoPanelScaleChangedEvent(new Point2D(transform.getScaleX(),
                                                                        transform.getScaleY())));
    }

    public void updateSize()
    {
        final Element parentElement = getElement().getParentElement();
        final int width         = parentElement.getOffsetWidth();
        final int height        = parentElement.getOffsetHeight();

        if (width > 0 && height > 0)
        {
            updateSize(width,
                       height);
        }
    }

    public void updateSize(final int width,
                           final int height)
    {
        this.wide = width;
        this.high = height;
        updateScrollPanelSize(width,
                              height);
        updateInternalPanelsSizes(width,
                                  height);
        fireLienzoPanelResizeEvent(width, height);
    }

    @Override
    public void setPixelSize(int width, int height)
    {
        this.wide = width;
        this.high = height;
        super.setPixelSize(width, height);
    }

    @Override
    public int getWidthPx()
    {
        return wide;
    }

    @Override
    public int getHeightPx()
    {
        return high;
    }

    @Override
    public LienzoBoundsPanel onRefresh()
    {
        getScrollHandler().refresh();
        batch();
        return this;
    }

    @Override
    public void onResize()
    {
        scheduleDeferred(new Scheduler.ScheduledCommand()
        {
            @Override
            public void execute()
            {
                onSroll();
                ScrollablePanel.this.updateSize();
                ScrollablePanel.this.refresh();
            }
        });
    }

    @Override
    protected void doDestroy()
    {
        handlers.removeHandler();
        isMouseDown = false;
    }

    private void setupPanels()
    {
        setupScrollPanel();
        setupDomElementContainer();
        setupRootPanel();

        add(getRootPanel());
        getElement().getStyle().setOutlineStyle(Style.OutlineStyle.NONE);
    }

    private void setupScrollPanel()
    {
        getScrollPanel().add(getInternalScrollPanel());
    }

    private void setupDomElementContainer()
    {
        getDomElementContainer().add(getLienzoPanel());
    }

    private void setupRootPanel()
    {
        getRootPanel().add(getDomElementContainer());
        getRootPanel().add(getScrollPanel());
    }

    void setupHandlers()
    {
        // Mouse handlers.
        register(
                addMouseDownHandler(new MouseDownHandler()
                {
                    @Override
                    public void onMouseDown(MouseDownEvent e)
                    {
                        ScrollablePanel.this.onStart();
                    }
                })
                );
        register(
                addMouseUpHandler(new MouseUpHandler()
                {
                    @Override
                    public void onMouseUp(MouseUpEvent e)
                    {
                        ScrollablePanel.this.onComplete();
                    }
                })
                );
        register(
                addMouseOutHandler(new MouseOutHandler()
                {
                    @Override
                    public void onMouseOut(MouseOutEvent event)
                    {
                        ScrollablePanel.this.onComplete();
                    }
                })
                );
        register(
                addScrollHandler(new ScrollHandler()
                {
                    @Override
                    public void onScroll(final ScrollEvent scrollEvent)
                    {
                        ScrollablePanel.this.onSroll();
                    }
                })
                );
        register(
                addAttachHandler(new AttachEvent.Handler()
                {
                    @Override
                    public void onAttachOrDetach(final AttachEvent event)
                    {
                        if (event.isAttached())
                        {
                            onResize();
                        }
                    }
                })
                );
    }

    private void onStart()
    {
        isMouseDown = true;
        setFocus(true);
    }

    private void onComplete()
    {
        if (isMouseDown)
        {
            isMouseDown = false;
            refresh();
        }
    }

    private void onSroll()
    {
        // Prevent DOMElements scrolling into view when they receive the focus
        domElementContainer.getElement().setScrollTop(0);
        domElementContainer.getElement().setScrollLeft(0);
    }

    private void scheduleDeferred(final Scheduler.ScheduledCommand scheduledCommand)
    {
        Scheduler.get().scheduleDeferred(scheduledCommand);
    }

    private void updateInternalPanelsSizes(final int width,
                                           final int height)
    {
        final int scrollbarWidth  = getScrollHandler().scrollbarWidth();
        final int scrollbarHeight = getScrollHandler().scrollbarHeight();
        final int w               = width - scrollbarWidth;
        final int h               = height - scrollbarHeight;
        getDomElementContainer().setPixelSize(w, h);
        getLienzoPanel().setPixelSize(w, h);
    }

    private void updateScrollPanelSize(final int width,
                                       final int height)
    {
        getScrollPanel().setPixelSize(width,
                                      height);
    }

    public HandlerRegistration register(final HandlerRegistration handler)
    {
        return handlers.register(handler);
    }

    public AbsolutePanel getScrollPanel()
    {
        return scrollPanel;
    }

    public AbsolutePanel getDomElementContainer()
    {
        return domElementContainer;
    }

    public AbsolutePanel getInternalScrollPanel()
    {
        return internalScrollPanel;
    }

    public ScrollablePanelHandler getScrollHandler()
    {
        return scrollHandler;
    }

    AbsolutePanel getRootPanel()
    {
        return rootPanel;
    }
}
