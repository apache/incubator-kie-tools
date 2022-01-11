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

import org.kie.workbench.common.stunner.client.widgets.presenters.canvas.AbstractCanvasViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasPanel;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasSettings;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistries;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.preferences.StunnerDiagramEditorPreferences;
import org.kie.workbench.common.stunner.core.preferences.StunnerPreferences;
import org.uberfire.mvp.ParameterizedCommand;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public abstract class AbstractDiagramViewer<D extends Diagram, H extends AbstractCanvasHandler>
        extends AbstractCanvasViewer<D, H, WidgetWrapperView, DiagramViewer.DiagramViewerCallback<D>>
        implements DiagramViewer<D, H> {

    public AbstractDiagramViewer(final WidgetWrapperView view) {
        super(view);
    }

    protected abstract void onOpen(D diagram);

    protected abstract AbstractCanvas getCanvas();

    public abstract CanvasPanel getCanvasPanel();

    protected abstract StunnerPreferencesRegistries getPreferencesRegistry();

    public StunnerDiagramEditorPreferences getPreferences(final D diagram) {
        final String definitionSetId = diagram.getMetadata().getDefinitionSetId();
        final StunnerPreferences preferences = getPreferencesRegistry().get(definitionSetId, StunnerPreferences.class);
        return preferences.getDiagramEditorPreferences();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void open(final D diagram,
                     final DiagramViewer.DiagramViewerCallback<D> callback) {
        final StunnerDiagramEditorPreferences editorPreferences = getPreferences(diagram);
        final boolean isHiDPIEnabled = null != editorPreferences && editorPreferences.isHiDPIEnabled();
        final CanvasSettings settings = new CanvasSettings(isHiDPIEnabled);
        this.open(diagram,
                  settings,
                  callback);
    }

    protected void open(final D diagram,
                        final CanvasSettings settings,
                        final DiagramViewerCallback<D> callback) {
        onOpen(diagram);
        callback.onOpen(diagram);

        // Open and initialize the canvas and its handler.
        openCanvas(getCanvas(),
                   getCanvasPanel(),
                   settings);
        // Notify listeners that the canvas and the handler are ready.
        callback.afterCanvasInitialized();
        // Loads and draw the diagram into the canvas handled instance.
        getHandler().draw(diagram,
                          new ParameterizedCommand<CommandResult<?>>() {
                              @Override
                              public void execute(CommandResult<?> result) {
                                  if (!CommandUtils.isError(result)) {
                                      callback.onSuccess();
                                  } else {
                                      callback.onError(new ClientRuntimeError("An error occurred while drawing the diagram " +
                                                                                      "[result=" + result + "]"));
                                  }
                              }
                          });
    }

    @Override
    public void scale(final int toWidth,
                      final int toHeight) {
        this.scale(toWidth,
                   toHeight,
                   false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public D getInstance() {
        return null != getHandler() ? (D) getHandler().getDiagram() : null;
    }

    protected void scale(final int toWidth,
                         final int toHeight,
                         final boolean keepAspectRatio) {
        checkNotNull("item",
                     getInstance());
        scale(getCanvas().getWidthPx(),
              getCanvas().getHeightPx(),
              toWidth,
              toHeight,
              keepAspectRatio);
    }
}
