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
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * Specialised LienzoPanel that is overlaid with an AbsolutePanel
 * to support overlaying DOM elements on top of the Canvas element.
 */
public class GridLienzoPanel extends FocusPanel implements RequiresResize,
                                                           ProvidesResize {

    protected final AbsolutePanel domElementContainer = new AbsolutePanel();
    protected final LienzoPanel lienzoPanel;

    public GridLienzoPanel() {
        this.lienzoPanel = new LienzoPanel() {
            @Override
            public void onResize() {
                // Do nothing. Resize is handled by AttachHandler. LienzoPanel calls onResize() in
                // it's onAttach() method which causes the Canvas to be redrawn. However when LienzoPanel
                // is adopted by another Widget LienzoPanel's onAttach() is called before its children
                // have been attached. Should redraw require children to be attached errors arise.
            }
        };

        domElementContainer.add(lienzoPanel);
        add(domElementContainer);

        setupDefaultHandlers();
    }

    public GridLienzoPanel(final int width,
                           final int height) {
        this.lienzoPanel = new LienzoPanel(width,
                                           height) {
            @Override
            public void onResize() {
                // Do nothing. Resize is handled by AttachHandler. LienzoPanel calls onResize() in
                // it's onAttach() method which causes the Canvas to be redrawn. However when LienzoPanel
                // is adopted by another Widget LienzoPanel's onAttach() is called before its children
                // have been attached. Should redraw require children to be attached errors arise.
            }
        };
        this.domElementContainer.setPixelSize(width,
                                              height);

        domElementContainer.add(lienzoPanel);
        add(domElementContainer);

        setupDefaultHandlers();
    }

    private void setupDefaultHandlers() {
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
        addMouseDownHandler((e) -> setFocus(true));
    }

    @Override
    public void onResize() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                final Element e = getElement().getParentElement();
                final int width = e.getOffsetWidth();
                final int height = e.getOffsetHeight();
                if (width > 0 && height > 0) {
                    domElementContainer.setPixelSize(width,
                                                     height);
                    lienzoPanel.setPixelSize(width,
                                             height);
                }
            }
        });
    }

    public LienzoPanel add(final DefaultGridLayer layer) {
        layer.setDomElementContainer(domElementContainer);
        lienzoPanel.add(layer);
        return lienzoPanel;
    }

    public final Viewport getViewport() {
        return lienzoPanel.getViewport();
    }
}
