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


package org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl;

import java.util.Objects;
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
import org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor.InlineTextEditEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationMessages;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.rule.RuleManager;

@Default
@Observer
@Dependent
public class ObserverBuilderControl extends AbstractElementBuilderControl
        implements ElementBuilderControl<AbstractCanvasHandler> {

    private static final Logger LOGGER = Logger.getLogger(ObserverBuilderControl.class.getName());

    private final Event<CanvasSelectionEvent> canvasSelectionEvent;

    private final Event<InlineTextEditEvent> inlineTextEditEventEvent;

    @Inject
    public ObserverBuilderControl(final ClientDefinitionManager clientDefinitionManager,
                                  final ClientFactoryService clientFactoryServices,
                                  final RuleManager ruleManager,
                                  final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory,
                                  final ClientTranslationMessages translationMessages,
                                  final GraphBoundsIndexer graphBoundsIndexer,
                                  final Event<CanvasSelectionEvent> canvasSelectionEvent,
                                  final Event<InlineTextEditEvent> inlineTextEditEventEvent) {
        super(clientDefinitionManager,
              clientFactoryServices,
              ruleManager,
              canvasCommandFactory,
              translationMessages,
              graphBoundsIndexer);
        this.canvasSelectionEvent = canvasSelectionEvent;
        this.inlineTextEditEventEvent = inlineTextEditEventEvent;
    }

    public void buildShapeAt(final Object definition,
                             final double x,
                             final double y) {
        final ElementBuildRequest<AbstractCanvasHandler> request =
                new ElementBuildRequestImpl(x,
                                            y,
                                            definition);
        ObserverBuilderControl.this.build(request,
                                          new BuildCallback() {
                                              @Override
                                              public void onSuccess(final String uuid) {
                                                  canvasSelectionEvent.fire(new CanvasSelectionEvent(canvasHandler,
                                                                                                     uuid));
                                                  inlineTextEditEventEvent.fire(new InlineTextEditEvent(uuid));
                                              }

                                              @Override
                                              public void onError(final ClientRuntimeError error) {
                                                  LOGGER.log(Level.WARNING,
                                                             error.toString());
                                              }
                                          });
    }

    @SuppressWarnings("unchecked")
    void onBuildCanvasShape(final @Observes BuildCanvasShapeEvent event) {
        Objects.requireNonNull(event, "Parameter named 'event' should be not null!");
        if (null != canvasHandler) {
            final CanvasHandler context = event.getCanvasHandler();
            if (null != context && context.equals(canvasHandler)) {
                final Point2D transformed = getTransformedLocation(event.getClientX(), event.getClientY());
                buildShapeAt(event.getDefinition(),
                             transformed.getX(),
                             transformed.getY());
            }
        }
    }

    /**
     * Gets canvas transformed location.
     * @param clientX The clientX coordinate value.
     * @param clientY The clientY coordinate value.
     * @return the transformed location into the canvas.
     */
    public Point2D getTransformedLocation(final double clientX,
                                          final double clientY) {
        final double x = getRelativeX(clientX);
        final double y = getRelativeY(clientY);
        return canvasHandler.getAbstractCanvas().getTransform().inverse(x, y);
    }

    /**
     * Gets the mouse x-position relative to the canvas element.
     * @param clientX The event's clientX value.
     * @return the relative x-position
     */
    private double getRelativeX(final double clientX) {
        return clientX - getCanvasElement().getAbsoluteLeft() + getCanvasElement().getScrollLeft() +
                getCanvasElement().getOwnerDocument().getScrollLeft();
    }

    /**
     * Gets the mouse y-position relative to the canvas element.
     * @param clientY The event's clienty value.
     * @return the relative y-position
     */
    private double getRelativeY(final double clientY) {
        return clientY - getCanvasElement().getAbsoluteTop() + getCanvasElement().getScrollTop() +
                getCanvasElement().getOwnerDocument().getScrollTop();
    }

    private com.google.gwt.user.client.Element getCanvasElement() {
        return canvasHandler.getAbstractCanvas().getView().asWidget().getElement();
    }
}
