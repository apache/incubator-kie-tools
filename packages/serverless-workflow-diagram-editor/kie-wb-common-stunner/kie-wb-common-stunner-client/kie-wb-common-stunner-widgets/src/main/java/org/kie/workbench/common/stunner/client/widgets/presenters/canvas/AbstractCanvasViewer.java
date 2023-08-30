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


package org.kie.workbench.common.stunner.client.widgets.presenters.canvas;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.kie.workbench.common.stunner.client.lienzo.util.LienzoShapeUtils;
import org.kie.workbench.common.stunner.client.widgets.presenters.Viewer;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasPanel;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasSettings;

/**
 * A widget that displays a diagram into a canvas.
 */
public abstract class AbstractCanvasViewer<T, H extends AbstractCanvasHandler, V extends WidgetWrapperView, C extends Viewer.Callback>
        implements CanvasViewer<T, H, WidgetWrapperView, C> {

    private static Logger LOGGER = Logger.getLogger(AbstractCanvasViewer.class.getName());

    private final WidgetWrapperView view;

    public AbstractCanvasViewer(final WidgetWrapperView view) {
        this.view = view;
    }

    /**
     * Implementations must enable here the controls, at least, the zoom control instance.
     */
    protected abstract void enableControls();

    /**
     * Implementations must destroy here the controls, at least, the zoom control instance.
     */
    protected abstract void destroyControls();

    /**
     * Clears the widget state and view.
     * It can be used later on again.
     */
    @Override
    public void clear() {
        getHandler().clear();
        getView().clear();
    }

    /**
     * Destroy the instances and clears the view.
     * It releases components and states from memory.
     * Use it when the widget will be no longer used.
     */
    @Override
    public void destroy() {
        destroyControls();
        destroyInstances();
    }

    protected void destroyInstances() {
        if (getHandler() != null) {
            getHandler().destroy();
        }
        getView().clear();
    }

    /**
     * Returns the view as a widget instance.
     */
    @Override
    public WidgetWrapperView getView() {
        return view;
    }

    /**
     * Opens an item.
     * It initializes the canvas and the handler as provides a valid canvas view.
     * @param canvas The canvas instance.
     * @param settings The canvas settings.
     */
    @SuppressWarnings("unchecked")
    protected void openCanvas(final AbstractCanvas canvas,
                              final CanvasPanel panel,
                              final CanvasSettings settings) {
        canvas.initialize(panel, settings);
        // Initialize the canvas handler for the canvas.
        getHandler().handle(canvas);
        enableControls();

        // Use the canvas as view.
        getView().setWidget(canvas.getView());
    }

    protected abstract void scalePanel(final int width,
                                       final int height);

    protected void scale(final int width,
                         final int height,
                         final int toWidth,
                         final int toHeight,
                         final boolean keepAspectRatio) {
        if (null != getHandler() && null != getHandler().getDiagram()) {
            final double[] sfactor = LienzoShapeUtils.getScaleFactor(width,
                                                                     height,
                                                                     toWidth,
                                                                     toHeight);
            if (0 != Double.compare(1,
                                    sfactor[0])
                    || 0 != Double.compare(1,
                                           sfactor[1])) {
                final double[] factor = getScaleFactor(sfactor,
                                                       keepAspectRatio);
                // Scale the canvas/layer using scale method from the zoom control.
                getMediatorsControl().scale(factor[0],
                                            factor[1]);
                // Scale the panel widget - delegated to implementations.
                scalePanel(toWidth,
                           toHeight);
            }
        } else {
            LOGGER.log(Level.WARNING,
                       "Nothing to scale!");
        }
    }

    private double[] getScaleFactor(final double[] factor,
                                    final boolean keepAspectRatio) {
        if (!keepAspectRatio) {
            return factor;
        }
        final double max = factor[0] >= factor[1] ? factor[1] : factor[0];
        return new double[]{max, max};
    }
}
