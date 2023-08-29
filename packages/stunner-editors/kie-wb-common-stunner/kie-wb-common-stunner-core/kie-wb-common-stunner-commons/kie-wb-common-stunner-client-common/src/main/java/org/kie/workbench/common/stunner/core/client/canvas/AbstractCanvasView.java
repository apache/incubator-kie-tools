/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.client.canvas;

import javax.annotation.PostConstruct;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.uberfire.client.workbench.widgets.ResizeFlowPanel;

public abstract class AbstractCanvasView<V extends AbstractCanvasView>
        extends Composite
        implements AbstractCanvas.CanvasView<V>,
                   ProvidesResize,
                   RequiresResize {

    public static final String CURSOR = "cursor";
    public static final String CURSOR_NOT_ALLOWED = "not-allowed";

    private final ResizeFlowPanel mainPanel = new ResizeFlowPanel();
    private CanvasPanel canvasPanel;

    protected abstract V doInitialize(final CanvasSettings canvasSettings);

    protected abstract void doDestroy();

    @PostConstruct
    public void init() {
        initWidget(mainPanel);
        mainPanel.getElement().getStyle().setWidth(100, Style.Unit.PCT);
        mainPanel.getElement().getStyle().setHeight(100, Style.Unit.PCT);
    }

    @Override
    public final V initialize(final CanvasPanel canvasPanel,
                              final CanvasSettings canvasSettings) {
        this.canvasPanel = canvasPanel;
        doInitialize(canvasSettings);
        mainPanel.add(canvasPanel);
        return cast();
    }

    @Override
    public void onResize() {
        mainPanel.onResize();
    }

    @Override
    public V setCursor(final AbstractCanvas.Cursors cursor) {
        if (AbstractCanvas.Cursors.NOT_ALLOWED.equals(cursor)) {
            Style style = canvasPanel.asWidget().getElement().getStyle();
            style.setProperty(CURSOR, CURSOR_NOT_ALLOWED);
        } else {
            setViewCursor(cursor);
        }
        return cast();
    }

    protected void setViewCursor(final AbstractCanvas.Cursors cursor) {
        final Style style = canvasPanel.asWidget().getElement().getStyle();
        style.setCursor(toViewCursor(cursor));
    }

    @Override
    public Point2D getAbsoluteLocation() {
        return new Point2D(getAbsoluteLeft(), getAbsoluteTop());
    }

    @Override
    public CanvasPanel getPanel() {
        return canvasPanel;
    }

    @Override
    public final void destroy() {
        doDestroy();
        canvasPanel.destroy();
        mainPanel.clear();
        canvasPanel = null;
    }

    public static Style.Cursor toViewCursor(final AbstractCanvas.Cursors cursor) {
        switch (cursor) {
            case DEFAULT:
                return Style.Cursor.DEFAULT;
            case AUTO:
                return Style.Cursor.AUTO;
            case MOVE:
                return Style.Cursor.MOVE;
            case TEXT:
                return Style.Cursor.TEXT;
            case POINTER:
                return Style.Cursor.POINTER;
            case WAIT:
                return Style.Cursor.WAIT;
            case CROSSHAIR:
                return Style.Cursor.CROSSHAIR;
            case ROW_RESIZE:
                return Style.Cursor.ROW_RESIZE;
            case COL_RESIZE:
                return Style.Cursor.COL_RESIZE;
        }
        return Style.Cursor.DEFAULT;
    }

    @SuppressWarnings("unchecked")
    private V cast() {
        return (V) this;
    }
}
