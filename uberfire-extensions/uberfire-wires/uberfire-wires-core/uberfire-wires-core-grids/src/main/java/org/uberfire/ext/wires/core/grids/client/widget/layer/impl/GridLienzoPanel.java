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
package org.uberfire.ext.wires.core.grids.client.widget.layer.impl;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.scrollbars.GridLienzoScrollHandler;
import org.uberfire.ext.wires.core.grids.client.widget.scrollbars.GridLienzoScrollable;

/**
 * Specialised LienzoPanel that is overlaid with an AbsolutePanel
 * to support overlaying DOM elements on top of the Canvas element.
 */
public class GridLienzoPanel extends FocusPanel implements RequiresResize,
                                                           ProvidesResize,
                                                           GridLienzoScrollable {

    protected final LienzoPanel lienzoPanel;

    protected final AbsolutePanel domElementContainer = new AbsolutePanel();

    private final AbsolutePanel internalScrollPanel = new AbsolutePanel();

    private final AbsolutePanel scrollPanel = new AbsolutePanel();

    private final AbsolutePanel rootPanel = new AbsolutePanel();

    private final GridLienzoScrollHandler gridLienzoScrollHandler;

    private DefaultGridLayer defaultGridLayer;

    public GridLienzoPanel() {
        this(new LienzoPanel() {
            @Override
            public void onResize() {
                // Do nothing. Resize is handled by AttachHandler. LienzoPanel calls onResize() in
                // it's onAttach() method which causes the Canvas to be redrawn. However when LienzoPanel
                // is adopted by another Widget LienzoPanel's onAttach() is called before its children
                // have been attached. Should redraw require children to be attached errors arise.
            }
        });
    }

    public GridLienzoPanel(final int width,
                           final int height) {
        this(new LienzoPanel(width,
                             height) {
            @Override
            public void onResize() {
                // Do nothing. Resize is handled by AttachHandler. LienzoPanel calls onResize() in
                // it's onAttach() method which causes the Canvas to be redrawn. However when LienzoPanel
                // is adopted by another Widget LienzoPanel's onAttach() is called before its children
                // have been attached. Should redraw require children to be attached errors arise.
            }
        });

        updatePanelSize(width,
                        height);
    }

    public GridLienzoPanel(final DefaultGridLayer defaultGridLayer) {
        this(new LienzoPanel() {
                 @Override
                 public void onResize() {
                     // Do nothing. Resize is handled by AttachHandler. LienzoPanel calls onResize() in
                     // it's onAttach() method which causes the Canvas to be redrawn. However when LienzoPanel
                     // is adopted by another Widget LienzoPanel's onAttach() is called before its children
                     // have been attached. Should redraw require children to be attached errors arise.
                 }
             },
             defaultGridLayer);
    }

    public GridLienzoPanel(final int width,
                           final int height,
                           final DefaultGridLayer defaultGridLayer) {
        this(new LienzoPanel(width,
                             height) {
                 @Override
                 public void onResize() {
                     // Do nothing. Resize is handled by AttachHandler. LienzoPanel calls onResize() in
                     // it's onAttach() method which causes the Canvas to be redrawn. However when LienzoPanel
                     // is adopted by another Widget LienzoPanel's onAttach() is called before its children
                     // have been attached. Should redraw require children to be attached errors arise.
                 }
             },
             defaultGridLayer);

        updatePanelSize(width,
                        height);
    }

    protected GridLienzoPanel(final LienzoPanel lienzoPanel) {
        this.lienzoPanel = lienzoPanel;
        this.gridLienzoScrollHandler = new GridLienzoScrollHandler(this);

        setupPanels();
        setupScrollHandlers();
        setupDefaultHandlers();
    }

    protected GridLienzoPanel(final LienzoPanel lienzoPanel,
                              final DefaultGridLayer defaultGridLayer) {
        this.lienzoPanel = lienzoPanel;
        this.gridLienzoScrollHandler = new GridLienzoScrollHandler(this);

        add(defaultGridLayer);

        setupPanels();
        setupScrollHandlers();
        setupDefaultHandlers();
    }

    protected void setupPanels() {
        setupScrollPanel();
        setupDomElementContainer();
        setupRootPanel();

        add(getRootPanel());
        getElement().getStyle().setOutlineStyle(Style.OutlineStyle.NONE);
    }

    protected void setupScrollPanel() {
        getScrollPanel().add(getInternalScrollPanel());
    }

    protected void setupDomElementContainer() {
        getDomElementContainer().add(getLienzoPanel());
    }

    protected void setupRootPanel() {
        getRootPanel().add(getDomElementContainer());
        getRootPanel().add(getScrollPanel());
    }

    protected void setupScrollHandlers() {
        getGridLienzoScrollHandler().init();
        addMouseUpHandler();
    }

    protected void addMouseUpHandler() {
        addMouseUpHandler((e) -> refreshScrollPosition());
    }

    protected void setupDefaultHandlers() {
        //Prevent DOMElements scrolling into view when they receive the focus
        domElementContainer.addDomHandler(new ScrollHandler() {

                                              @Override
                                              public void onScroll(final ScrollEvent scrollEvent) {
                                                  domElementContainer.getElement().setScrollTop(0);
                                                  domElementContainer.getElement().setScrollLeft(0);
                                              }
                                          },
                                          ScrollEvent.getType());
        addAttachHandler(new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach(final AttachEvent event) {
                if (event.isAttached()) {
                    onResize();
                }
            }
        });
    }

    @Override
    public void onResize() {
        scheduleDeferred(() -> {
            updatePanelSize();
            refreshScrollPosition();
        });
    }

    protected void scheduleDeferred(final Scheduler.ScheduledCommand scheduledCommand) {
        Scheduler.get().scheduleDeferred(scheduledCommand);
    }

    @Override
    public void updatePanelSize() {
        final Element parentElement = getElement().getParentElement();
        final Integer width = parentElement.getOffsetWidth();
        final Integer height = parentElement.getOffsetHeight();

        if (width > 0 && height > 0) {
            updatePanelSize(width,
                            height);
        }
    }

    @Override
    public void updatePanelSize(final Integer width,
                                final Integer height) {
        updateScrollPanelSize(width,
                              height);
        updateInternalPanelsSizes(width,
                                  height);
    }

    protected void updateInternalPanelsSizes(final int width,
                                             final int height) {
        final Integer scrollbarWidth = getGridLienzoScrollHandler().scrollbarWidth();
        final Integer scrollbarHeight = getGridLienzoScrollHandler().scrollbarHeight();

        final int visibleWidth = width - scrollbarWidth;
        final int visibleHeight = height - scrollbarHeight;

        getDomElementContainer().setPixelSize(visibleWidth,
                                              visibleHeight);
        getLienzoPanel().setPixelSize(visibleWidth,
                                      visibleHeight);

        propagateNewPanelSize(visibleWidth, visibleHeight);
    }

    protected void updateScrollPanelSize(final int width,
                                         final int height) {
        getScrollPanel().setPixelSize(width,
                                      height);
    }

    protected void propagateNewPanelSize(int visibleWidth, int visibleHeight) {
        if (getDefaultGridLayer() == null) {
            return;
        }
        // propagate to all widgets the new visible width and refresh the layer if needed
        boolean toRefresh = false;
        for (GridWidget gridWidget : getDefaultGridLayer().getGridWidgets()) {
            toRefresh = toRefresh || gridWidget.getModel().setVisibleSizeAndRefresh(visibleWidth, visibleHeight);
        }
        if (toRefresh) {
            this.getDefaultGridLayer().batch();
        }
    }

    @Override
    public void refreshScrollPosition() {
        getGridLienzoScrollHandler().refreshScrollPosition();
    }

    @Override
    public void setBounds(final Bounds bounds) {
        getGridLienzoScrollHandler().setBounds(bounds);
    }

    public LienzoPanel add(final DefaultGridLayer layer) {
        defaultGridLayer = setupDefaultGridLayer(layer);

        layer.setDomElementContainer(domElementContainer);

        lienzoPanel.add(defaultGridLayer);

        return lienzoPanel;
    }

    protected DefaultGridLayer setupDefaultGridLayer(final DefaultGridLayer layer) {
        layer.addOnEnterPinnedModeCommand(this::refreshScrollPosition);
        layer.addOnExitPinnedModeCommand(this::refreshScrollPosition);

        return layer;
    }

    public final Viewport getViewport() {
        return lienzoPanel.getViewport();
    }

    public LienzoPanel getLienzoPanel() {
        return lienzoPanel;
    }

    public AbsolutePanel getScrollPanel() {
        return scrollPanel;
    }

    public AbsolutePanel getDomElementContainer() {
        return domElementContainer;
    }

    public AbsolutePanel getInternalScrollPanel() {
        return internalScrollPanel;
    }

    public DefaultGridLayer getDefaultGridLayer() {
        return defaultGridLayer;
    }

    protected AbsolutePanel getRootPanel() {
        return rootPanel;
    }

    protected GridLienzoScrollHandler getGridLienzoScrollHandler() {
        return gridLienzoScrollHandler;
    }
}
