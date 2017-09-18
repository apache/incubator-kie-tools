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
package org.uberfire.ext.wires.core.grids.client.widget.dom.impl;

import java.util.Iterator;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.types.Transform;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.wires.core.grids.client.util.MathUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

/**
 * The base of all DOMElements, providing common services such as Browser event propagation. MouseEvents do not bubble
 * from an absolutely positioned DIV to other DOM elements; such as the Canvas. This class therefore emulates event
 * propagation by passing MouseEvents to canvas Layer and Grid Widget.
 * @param <T> The data-type represented by the DOMElement.
 * @param <W> The Widget to be wrapped by the DOMElement.
 */
public abstract class BaseDOMElement<T, W extends Widget> {

    private static final NumberFormat FORMAT = NumberFormat.getFormat("0.0");

    protected final W widget;
    protected final SimplePanel widgetContainer = GWT.create(SimplePanel.class);

    protected final GridLayer gridLayer;
    protected final GridWidget gridWidget;
    protected final AbsolutePanel domElementContainer;

    protected GridBodyCellRenderContext context;

    public BaseDOMElement(final W widget,
                          final GridLayer gridLayer,
                          final GridWidget gridWidget) {
        this.widget = widget;
        this.gridLayer = gridLayer;
        this.gridWidget = gridWidget;
        this.domElementContainer = gridLayer.getDomElementContainer();

        final Style style = widgetContainer.getElement().getStyle();
        style.setPosition(Style.Position.ABSOLUTE);

        //MouseEvents over absolutely positioned elements do not bubble through the DOM.
        //Consequentially Event Handlers on GridLayer do not receive notification of MouseMove
        //Events used during column resizing. Therefore we manually bubble events to GridLayer.
        setupDelegatingMouseDownHandler();
        setupDelegatingMouseMoveHandler();
        setupDelegatingMouseUpHandler();
        setupDelegatingClickHandler();
    }

    protected void setupDelegatingMouseDownHandler() {
        widgetContainer.addDomHandler(new MouseDownHandler() {
                                          @Override
                                          public void onMouseDown(final MouseDownEvent event) {
                                              gridLayer.onNodeMouseDown(new NodeMouseDownEvent(event) {

                                                  @Override
                                                  public int getX() {
                                                      //Adjust the x-coordinate (relative to the DOM Element) to be relative to the GridCanvas.
                                                      return super.getX() + widgetContainer.getElement().getOffsetLeft();
                                                  }

                                                  @Override
                                                  public int getY() {
                                                      //Adjust the y-coordinate (relative to the DOM Element) to be relative to the GridCanvas.
                                                      return super.getY() + widgetContainer.getElement().getOffsetTop();
                                                  }
                                              });
                                          }
                                      },
                                      MouseDownEvent.getType());
    }

    protected void setupDelegatingMouseMoveHandler() {
        final Style style = widgetContainer.getElement().getStyle();
        widgetContainer.addDomHandler(new MouseMoveHandler() {
                                          @Override
                                          public void onMouseMove(final MouseMoveEvent event) {
                                              //The DOM Element changes the Cursor, so set to the state determined by the MouseEvent Handlers on GridLayer
                                              style.setCursor(gridLayer.getGridWidgetHandlersState().getCursor());

                                              gridLayer.onNodeMouseMove(new NodeMouseMoveEvent(event) {

                                                  @Override
                                                  public int getX() {
                                                      //Adjust the x-coordinate (relative to the DOM Element) to be relative to the GridCanvas.
                                                      return super.getX() + widgetContainer.getElement().getOffsetLeft();
                                                  }

                                                  @Override
                                                  public int getY() {
                                                      //Adjust the y-coordinate (relative to the DOM Element) to be relative to the GridCanvas.
                                                      return super.getY() + widgetContainer.getElement().getOffsetTop();
                                                  }
                                              });
                                          }
                                      },
                                      MouseMoveEvent.getType());
    }

    protected void setupDelegatingMouseUpHandler() {
        widgetContainer.addDomHandler(new MouseUpHandler() {
                                          @Override
                                          public void onMouseUp(final MouseUpEvent event) {
                                              gridLayer.onNodeMouseUp(new NodeMouseUpEvent(event) {

                                                  @Override
                                                  public int getX() {
                                                      //Adjust the x-coordinate (relative to the DOM Element) to be relative to the GridCanvas.
                                                      return super.getX() + widgetContainer.getElement().getOffsetLeft();
                                                  }

                                                  @Override
                                                  public int getY() {
                                                      //Adjust the y-coordinate (relative to the DOM Element) to be relative to the GridCanvas.
                                                      return super.getY() + widgetContainer.getElement().getOffsetTop();
                                                  }
                                              });
                                          }
                                      },
                                      MouseUpEvent.getType());
    }

    protected void setupDelegatingClickHandler() {
        widgetContainer.addDomHandler(new ClickHandler() {
                                          @Override
                                          public void onClick(final ClickEvent event) {
                                              gridWidget.onNodeMouseClick(new NodeMouseClickEvent(event) {

                                                  @Override
                                                  public int getX() {
                                                      //Adjust the x-coordinate (relative to the DOM Element) to be relative to the GridCanvas.
                                                      return super.getX() + widgetContainer.getElement().getOffsetLeft();
                                                  }

                                                  @Override
                                                  public int getY() {
                                                      //Adjust the y-coordinate (relative to the DOM Element) to be relative to the GridCanvas.
                                                      return super.getY() + widgetContainer.getElement().getOffsetTop();
                                                  }
                                              });
                                          }
                                      },
                                      ClickEvent.getType());
    }

    /**
     * Set the Cell context this DOMElement is representing.
     * @param context
     */
    public void setContext(final GridBodyCellRenderContext context) {
        this.context = context;
    }

    /**
     * Initialise the DOMElement for the given cell and render context.
     * @param context The render context for the cell.
     */
    public abstract void initialise(final GridBodyCellRenderContext context);

    /**
     * Flush the state of the GWT Widget to the underlying GridWidget.
     * @param value The cell value requiring a DOMElement.
     */
    public abstract void flush(final T value);

    /**
     * Get a GWT Widget for the DOMElement.
     * @return
     */
    public W getWidget() {
        return widget;
    }

    /**
     * Get the container for the GWT Widget.
     * @return
     */
    protected SimplePanel getContainer() {
        return widgetContainer;
    }

    /**
     * Transform the DOMElement based on the render context, such as scale and position.
     * @param context
     */
    protected void transform(final GridBodyCellRenderContext context) {
        final Transform transform = context.getTransform();
        final double width = context.getCellWidth();
        final double height = context.getCellHeight();

        final Style style = widgetContainer.getElement().getStyle();

        //Copy across GridWidget's opacity to DOMElements
        style.setOpacity(gridWidget.getAlpha());

        //Reposition and transform the DOM Element
        style.setLeft((context.getAbsoluteCellX() * transform.getScaleX()) + transform.getTranslateX(),
                      Style.Unit.PX);
        style.setTop((context.getAbsoluteCellY() * transform.getScaleY()) + transform.getTranslateY(),
                     Style.Unit.PX);
        style.setWidth(width,
                       Style.Unit.PX);
        style.setHeight(height,
                        Style.Unit.PX);

        //If the DOMElement overlaps a fixed header clip content
        style.clearProperty("clip");
        final double top = context.getAbsoluteCellY() + transform.getTranslateY();
        final double left = context.getAbsoluteCellX() + transform.getTranslateX();
        final boolean isFloating = context.isFloating();
        boolean clip = false;
        double ct = 0.0;
        double cr = width;
        double cb = height;
        double cl = 0.0;

        final Group header = gridWidget.getHeader();
        final double clipMinY = context.getClipMinY() + transform.getTranslateY();
        final double clipMinX = context.getClipMinX() + transform.getTranslateX();
        if (header != null) {
            if (top < clipMinY) {
                ct = clipMinY - top;
                clip = true;
            }
        }
        if (!isFloating && left < clipMinX) {
            cl = clipMinX - left;
            clip = true;
        }
        if (clip) {
            style.setProperty("clip",
                              "rect(" + (int) ct + "px," + (int) cr + "px," + (int) cb + "px," + (int) cl + "px)");
        }

        // --- Workaround for BS2 ---
        style.setProperty("WebkitBoxSizing",
                          "border-box");
        style.setProperty("MozBoxSizing",
                          "border-box");
        style.setProperty("boxSizing",
                          "border-box");
        style.setProperty("lineHeight",
                          "normal");
        // --- End workaround ---

        if (MathUtilities.isOne(transform.getScaleX()) && MathUtilities.isOne(transform.getScaleY())) {
            style.clearProperty("WebkitTransform");
            style.clearProperty("MozTransform");
            style.clearProperty("Transform");
            style.clearProperty("MsTransform");
            return;
        }

        final String scale = "scale(" + FORMAT.format(transform.getScaleX()) + ", " + FORMAT.format(transform.getScaleY()) + ")";
        final String translate = "translate(" + FORMAT.format(((width - width * transform.getScaleX()) / -2.0)) + "px, " + FORMAT.format(((height - height * transform.getScaleY()) / -2.0)) + "px)";
        style.setProperty("WebkitTransform",
                          translate + " " + scale);
        style.setProperty("MozTransform",
                          translate + " " + scale);
        style.setProperty("Transform",
                          translate + " " + scale);
        style.setProperty("MsTransform",
                          translate + " " + scale);
    }

    /**
     * Attach the DOMElement to the GWT container, if not already attached.
     */
    public void attach() {
        final Iterator<Widget> itr = domElementContainer.iterator();
        while (itr.hasNext()) {
            if (itr.next().equals(widgetContainer)) {
                return;
            }
        }
        //When an Element is detached it's Position configuration is cleared, so reset it
        final Style style = widgetContainer.getElement().getStyle();
        style.setPosition(Style.Position.ABSOLUTE);
        style.setProperty("WebkitUserSelect",
                          "none");
        style.setProperty("MozUserSelect",
                          "none");
        style.setProperty("MsUserSelect",
                          "none");

        domElementContainer.add(widgetContainer);
    }

    /**
     * Detach the DOMElement from the GWT container, if already attached.
     */
    public void detach() {
        final Iterator<Widget> itr = domElementContainer.iterator();
        while (itr.hasNext()) {
            if (itr.next().equals(widgetContainer)) {
                itr.remove();
                return;
            }
        }
    }
}
