/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.session.impl;

import org.jboss.errai.ioc.client.api.Disposer;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControlRegistrationHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.pan.PanControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.kie.workbench.common.stunner.core.client.session.ClientReadOnlySession;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.graph.Element;

public abstract class AbstractClientReadOnlySession extends AbstractClientSession
        implements ClientReadOnlySession<AbstractCanvas, AbstractCanvasHandler> {

    protected SelectionControl<AbstractCanvasHandler, Element> selectionControl;
    protected ZoomControl<AbstractCanvas> zoomControl;
    protected PanControl<AbstractCanvas> panControl;

    private final CanvasControlRegistrationHandler<AbstractCanvas, AbstractCanvasHandler, ClientSession> registrationHandler;

    public AbstractClientReadOnlySession(final AbstractCanvas canvas,
                                         final AbstractCanvasHandler canvasHandler,
                                         final SelectionControl<AbstractCanvasHandler, Element> selectionControl,
                                         final ZoomControl<AbstractCanvas> zoomControl,
                                         final PanControl<AbstractCanvas> panControl,
                                         final Disposer<CanvasControl> disposer) {
        super(canvas,
              canvasHandler);
        this.selectionControl = selectionControl;
        this.zoomControl = zoomControl;
        this.panControl = panControl;
        this.registrationHandler = new CanvasControlRegistrationHandler<AbstractCanvas, AbstractCanvasHandler, ClientSession>(canvas,
                                                                                                                              canvasHandler,
                                                                                                                              disposer);
        this.registrationHandler.registerCanvasHandlerControl(selectionControl);
        this.registrationHandler.registerCanvasControl(zoomControl);
        this.registrationHandler.registerCanvasControl(panControl);
    }

    @Override
    protected void doOpen() {
        registrationHandler.enable();
    }

    @Override
    public void doDestroy() {
        registrationHandler.disable();
        registrationHandler.destroy();
    }

    @Override
    protected void doPause() {
        // TODO: Performance improvements: Disable controls here ( all handlers etc will get disabled ).
        registrationHandler.disable();
    }

    @Override
    protected void doResume() {
        // TODO: Performance improvements: Re-enable controls here.
        registrationHandler.enable();
    }

    protected CanvasControlRegistrationHandler<AbstractCanvas, AbstractCanvasHandler, ClientSession> getRegistrationHandler() {
        return registrationHandler;
    }
}
