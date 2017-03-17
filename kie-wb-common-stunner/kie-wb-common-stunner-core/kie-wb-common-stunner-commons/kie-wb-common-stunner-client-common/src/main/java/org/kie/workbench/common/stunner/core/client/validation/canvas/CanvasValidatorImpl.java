/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.validation.canvas;

import java.util.Collection;
import java.util.LinkedList;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.validation.AbstractValidator;
import org.kie.workbench.common.stunner.core.validation.graph.AbstractGraphValidatorCallback;
import org.kie.workbench.common.stunner.core.validation.graph.GraphValidationViolation;
import org.kie.workbench.common.stunner.core.validation.graph.GraphValidator;

@ApplicationScoped
public class CanvasValidatorImpl
        extends AbstractValidator<AbstractCanvasHandler, CanvasValidatorCallback>
        implements CanvasValidator<AbstractCanvasHandler> {

    GraphValidator graphValidator;

    private final RuleManager ruleManager;

    protected CanvasValidatorImpl() {
        this(null,
             null);
    }

    @Inject
    public CanvasValidatorImpl(final RuleManager ruleManager,
                               final GraphValidator graphValidator) {
        this.ruleManager = ruleManager;
        this.graphValidator = graphValidator;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void validate(final AbstractCanvasHandler canvasHandler,
                         final CanvasValidatorCallback callback) {
        final Graph graph = canvasHandler.getDiagram().getGraph();
        graphValidator
                .withRuleSet(canvasHandler.getRuleSet())
                .validate(graph,
                          new AbstractGraphValidatorCallback() {

                              @Override
                              public void afterValidateNode(final Node node,
                                                            final Iterable<GraphValidationViolation> violations) {
                                  super.afterValidateNode(node,
                                                          violations);
                                  checkViolations(canvasHandler,
                                                  node,
                                                  violations);
                              }

                              @Override
                              public void afterValidateEdge(final Edge edge,
                                                            final Iterable<GraphValidationViolation> violations) {
                                  super.afterValidateEdge(edge,
                                                          violations);
                                  checkViolations(canvasHandler,
                                                  edge,
                                                  violations);
                              }

                              @Override
                              public void onSuccess() {
                                  callback.onSuccess();
                              }

                              @Override
                              public void onFail(final Iterable<GraphValidationViolation> violations) {
                                  final Collection<CanvasValidationViolation> canvasValidationViolations = new LinkedList<CanvasValidationViolation>();
                                  for (final GraphValidationViolation violation : violations) {
                                      final CanvasValidationViolation canvasValidationViolation = new CanvasValidationViolationImpl(canvasHandler,
                                                                                                                                    violation);
                                      canvasValidationViolations.add(canvasValidationViolation);
                                  }
                                  callback.onFail(canvasValidationViolations);
                              }
                          });
    }

    private void checkViolations(final CanvasHandler canvasHandler,
                                 final Element element,
                                 final Iterable<GraphValidationViolation> violations) {
        if (hasViolations(violations)) {
            final Canvas canvas = canvasHandler.getCanvas();
            final Shape shape = getShape(canvasHandler,
                                         element.getUUID());
            shape.applyState(ShapeState.INVALID);
            canvas.draw();
        }
    }

    private Shape getShape(final CanvasHandler canvasHandler,
                           final String uuid) {
        return canvasHandler.getCanvas().getShape(uuid);
    }

    private boolean hasViolations(final Iterable<GraphValidationViolation> violations) {
        return violations != null && violations.iterator().hasNext();
    }
}
