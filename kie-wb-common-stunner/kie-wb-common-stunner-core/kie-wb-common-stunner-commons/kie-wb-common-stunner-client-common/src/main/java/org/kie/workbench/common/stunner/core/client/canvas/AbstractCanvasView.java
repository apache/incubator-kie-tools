/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.canvas;

import javax.annotation.PostConstruct;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.uberfire.client.workbench.widgets.listbar.ResizeFlowPanel;

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
        Style style = canvasPanel.asWidget().getElement().getStyle();
        switch (cursor) {
            case AUTO:
                style.setCursor(Style.Cursor.AUTO);
                break;
            case MOVE:
                style.setCursor(Style.Cursor.MOVE);
                break;
            case TEXT:
                style.setCursor(Style.Cursor.TEXT);
                break;
            case POINTER:
                style.setCursor(Style.Cursor.POINTER);
                break;
            case NOT_ALLOWED:
                style.setProperty(CURSOR, CURSOR_NOT_ALLOWED);
                break;
            case WAIT:
                style.setCursor(Style.Cursor.WAIT);
                break;
            case CROSSHAIR:
                style.setCursor(Style.Cursor.CROSSHAIR);
                break;
        }
        return cast();
    }

    @Override
    public Point2D getAbsoluteLocation() {
        return new Point2D(getAbsoluteLeft(), getAbsoluteTop());
    }

    protected CanvasPanel getCanvasPanel() {
        return canvasPanel;
    }

    @Override
    public final void destroy() {
        doDestroy();
        canvasPanel.destroy();
        mainPanel.clear();
        canvasPanel = null;
    }

    @SuppressWarnings("unchecked")
    private V cast() {
        return (V) this;
    }
}
