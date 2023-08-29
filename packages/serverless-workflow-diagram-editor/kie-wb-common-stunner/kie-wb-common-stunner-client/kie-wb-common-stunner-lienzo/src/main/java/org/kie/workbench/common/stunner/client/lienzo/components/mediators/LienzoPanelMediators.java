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


package org.kie.workbench.common.stunner.client.lienzo.components.mediators;

import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;
import org.kie.workbench.common.stunner.client.lienzo.components.views.LienzoPanelFocusHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasPanel;

@Dependent
public class LienzoPanelMediators {

    static final double MIN_SCALE = 0.25d;
    static final double MAX_SCALE = 2d;
    static final double ZOOM_FACTOR = 0.1d;

    final LienzoCanvasMediators mediators;
    final ZoomLevelSelectorPresenter selector;
    LienzoPanelFocusHandler focusHandler;

    @Inject
    public LienzoPanelMediators(final LienzoCanvasMediators mediators,
                                final ZoomLevelSelectorPresenter selector) {
        this.mediators = mediators;
        this.selector = selector;
    }

    public LienzoPanelMediators init(final Supplier<LienzoCanvas> canvas) {
        CanvasPanel panel = canvas.get().getView().getPanel();
        if (panel instanceof LienzoPanel) {
            focusHandler = new LienzoPanelFocusHandler()
                    .listen((LienzoPanel) canvas.get().getView().getPanel(),
                            this::enable,
                            this::disable);
            mediators.init(canvas);
            selector.init(canvas);
            setZoomFactor(ZOOM_FACTOR);
            setMinScale(MIN_SCALE);
            setMaxScale(MAX_SCALE);
            enable();
        }
        return this;
    }

    public LienzoPanelMediators setMinScale(final double minScale) {
        selector.setMinScale(minScale);
        mediators.setMinScale(minScale);
        return this;
    }

    public LienzoPanelMediators setMaxScale(final double maxScale) {
        selector.setMaxScale(maxScale);
        mediators.setMaxScale(maxScale);
        return this;
    }

    public LienzoPanelMediators setZoomFactor(final double factor) {
        selector.setZoomFactor(factor);
        mediators.setZoomFactor(factor);
        return this;
    }

    public void enable() {
        mediators.enable();
        selector.show();
    }

    public void disable() {
        mediators.disable();
        selector.scheduleHide();
    }

    public void destroy() {
        if (null != focusHandler) {
            focusHandler.clear();
            focusHandler = null;
        }
        mediators.destroy();
        selector.destroy();
    }
}
