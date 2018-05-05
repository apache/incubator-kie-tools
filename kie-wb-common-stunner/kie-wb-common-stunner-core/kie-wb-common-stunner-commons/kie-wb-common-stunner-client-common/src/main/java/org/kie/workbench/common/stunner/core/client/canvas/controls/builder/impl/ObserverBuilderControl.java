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

package org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request.ElementBuildRequest;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request.ElementBuildRequestImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.event.BuildCanvasShapeEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.rule.RuleManager;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Default
@Observer
@Dependent
public class ObserverBuilderControl extends AbstractElementBuilderControl
        implements ElementBuilderControl<AbstractCanvasHandler> {

    private static Logger LOGGER = Logger.getLogger(ObserverBuilderControl.class.getName());

    private final Event<CanvasSelectionEvent> canvasSelectionEvent;

    @Inject
    public ObserverBuilderControl(final ClientDefinitionManager clientDefinitionManager,
                                  final ClientFactoryService clientFactoryServices,
                                  final RuleManager ruleManager,
                                  final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory,
                                  final GraphBoundsIndexer graphBoundsIndexer,
                                  final Event<CanvasSelectionEvent> canvasSelectionEvent) {
        super(clientDefinitionManager,
              clientFactoryServices,
              ruleManager,
              canvasCommandFactory,
              graphBoundsIndexer);
        this.canvasSelectionEvent = canvasSelectionEvent;
    }

    @SuppressWarnings("unchecked")
    void onBuildCanvasShape(final @Observes BuildCanvasShapeEvent buildCanvasShapeEvent) {
        checkNotNull("buildCanvasShapeEvent",
                     buildCanvasShapeEvent);
        if (null != canvasHandler) {
            final CanvasHandler context = buildCanvasShapeEvent.getCanvasHandler();
            if (null != context && context.equals(canvasHandler)) {
                final Object definition = buildCanvasShapeEvent.getDefinition();
                final double x = buildCanvasShapeEvent.getX();
                final double y = buildCanvasShapeEvent.getY();
                final double _x = x >= 0 ? x - canvasHandler.getAbstractCanvas().getAbsoluteX() : -1;
                final double _y = y >= 0 ? y - canvasHandler.getAbstractCanvas().getAbsoluteY() : -1;
                final ElementBuildRequest<AbstractCanvasHandler> request =
                        new ElementBuildRequestImpl(_x,
                                                    _y,
                                                    definition);
                ObserverBuilderControl.this.build(request,
                                                  new BuildCallback() {
                                                      @Override
                                                      public void onSuccess(final String uuid) {
                                                          canvasHandler.getCanvas().draw();
                                                          canvasSelectionEvent.fire(new CanvasSelectionEvent(canvasHandler,
                                                                                                             uuid));
                                                      }

                                                      @Override
                                                      public void onError(final ClientRuntimeError error) {
                                                          LOGGER.log(Level.SEVERE,
                                                                     error.toString());
                                                      }
                                                  });
            }
        }
    }
}
