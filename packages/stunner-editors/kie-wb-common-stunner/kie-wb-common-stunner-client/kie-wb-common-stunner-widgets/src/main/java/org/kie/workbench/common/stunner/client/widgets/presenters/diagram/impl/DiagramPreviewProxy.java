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


package org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl;

import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.BaseCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasPanel;
import org.kie.workbench.common.stunner.core.client.canvas.controls.MediatorsControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistries;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;

/**
 * An abstract DiagramViewer type that opens the diagram in a viewer which is scaled
 * to fit the given size, usually for previewing goals.
 *
 * @param <D> The diagram type.
 */
public abstract class DiagramPreviewProxy<D extends Diagram>
        extends AbstractDiagramPreview<D, AbstractCanvasHandler> {

    private final DiagramViewerProxy<D> viewer;

    protected abstract CanvasPanel getCanvasPanel();

    @SuppressWarnings("unchecked")
    public DiagramPreviewProxy(final WidgetWrapperView view,
                               final StunnerPreferencesRegistries preferencesRegistries) {
        this.viewer =
                new DiagramViewerProxy<D>(view) {

                    @Override
                    public SelectionControl<AbstractCanvasHandler, Element> getSelectionControl() {
                        return DiagramPreviewProxy.this.getSelectionControl();
                    }

                    @Override
                    public <C extends Canvas> MediatorsControl<C> getMediatorsControl() {
                        return DiagramPreviewProxy.this.getMediatorsControl();
                    }

                    @Override
                    protected void onOpen(final D diagram) {
                        DiagramPreviewProxy.this.onOpen(diagram);
                    }

                    @Override
                    protected AbstractCanvas getCanvas() {
                        return DiagramPreviewProxy.this.getCanvas();
                    }

                    @Override
                    public CanvasPanel getCanvasPanel() {
                        return DiagramPreviewProxy.this.getCanvasPanel();
                    }

                    @Override
                    protected StunnerPreferencesRegistries getPreferencesRegistry() {
                        return preferencesRegistries;
                    }

                    @Override
                    protected void enableControls() {
                        DiagramPreviewProxy.this.enableControls();
                    }

                    @Override
                    protected void destroyControls() {
                        DiagramPreviewProxy.this.destroyControls();
                    }

                    @Override
                    protected void destroyInstances() {
                        DiagramPreviewProxy.this.destroyInstances();
                    }

                    @Override
                    protected BaseCanvasHandler<D, ?> getCanvasHandler() {
                        return DiagramPreviewProxy.this.getCanvasHandler();
                    }

                    @Override
                    protected CanvasCommandFactory<AbstractCanvasHandler> getCanvasCommandFactory() {
                        return DiagramPreviewProxy.this.getCanvasCommandFactory();
                    }
                };
    }

    protected abstract void onOpen(D diagram);

    protected abstract AbstractCanvas getCanvas();

    protected abstract BaseCanvasHandler<D, ?> getCanvasHandler();

    protected abstract CanvasCommandFactory<AbstractCanvasHandler> getCanvasCommandFactory();

    protected abstract void enableControls();

    protected abstract void destroyControls();

    protected abstract void destroyInstances();

    @Override
    public DiagramViewerProxy<D> getViewer() {
        return viewer;
    }
}
