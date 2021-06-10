/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl;

import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.BaseCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasPanel;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasSettings;
import org.kie.workbench.common.stunner.core.client.canvas.controls.MediatorsControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistries;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.preferences.StunnerDiagramEditorPreferences;
import org.uberfire.mvp.ParameterizedCommand;

/**
 * An abstract DiagramViewer type that opens the diagram in a viewer which is scaled
 * to fit the given size, usually for previewing goals.
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
                    public void open(D item, int width, int height, DiagramViewerCallback<D> callback) {
                        onOpen(item);
                        callback.onOpen(item);

                        final StunnerDiagramEditorPreferences editorPreferences = getPreferences(item);
                        final boolean isHiDPIEnabled = null != editorPreferences && editorPreferences.isHiDPIEnabled();
                        final CanvasSettings settings = new CanvasSettings(isHiDPIEnabled);

                        // Open and initialize the canvas and its handler.
                        openCanvas(getCanvas(),
                                   getCanvasPanel(),
                                   settings);
                        // Notify listeners that the canvas and the handler are ready.
                        callback.afterCanvasInitialized();

                        // Loads and draw the diagram into the canvas handled instance.
                        getHandler().draw(item,
                                          (ParameterizedCommand<CommandResult<?>>) result -> {
                                              if (!CommandUtils.isError(result)) {
                                                  callback.onSuccess();
                                              } else {
                                                  callback.onError(new ClientRuntimeError("An error occurred while drawing the diagram " +
                                                                                                  "[result=" + result + "]"));
                                              }
                                          });
                    }

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
