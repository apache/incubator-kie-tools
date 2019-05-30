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

package org.kie.workbench.common.stunner.core.client.validation.canvas;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramValidator;
import org.kie.workbench.common.stunner.core.validation.ElementViolation;

@Dependent
public class CanvasDiagramValidator<H extends AbstractCanvasHandler> {

    private final DiagramValidator<Diagram, RuleViolation> diagramValidator;
    private final Event<CanvasValidationSuccessEvent> validationSuccessEvent;
    private final Event<CanvasValidationFailEvent> validationFailEvent;

    protected CanvasDiagramValidator() {
        this(null,
             null,
             null);
    }

    @Inject
    public CanvasDiagramValidator(final DiagramValidator<Diagram, RuleViolation> diagramValidator,
                                  final Event<CanvasValidationSuccessEvent> validationSuccessEvent,
                                  final Event<CanvasValidationFailEvent> validationFailEvent) {
        this.diagramValidator = diagramValidator;
        this.validationSuccessEvent = validationSuccessEvent;
        this.validationFailEvent = validationFailEvent;
    }

    @SuppressWarnings("unchecked")
    public void validate(final H canvasHandler,
                         final Consumer<Collection<DiagramElementViolation<RuleViolation>>> callback) {
        diagramValidator.validate(canvasHandler.getDiagram(),
                                  violations -> {
                                      checkViolations(canvasHandler,
                                                      violations);
                                      callback.accept(violations);
                                  });
    }

    @SuppressWarnings("unchecked")
    private void checkViolations(final H canvasHandler,
                                 final Collection<DiagramElementViolation<RuleViolation>> elementViolations) {
        final String uuid = canvasHandler.getUuid();
        final Diagram diagram = canvasHandler.getDiagram();
        final String name = diagram.getName();
        final String title = diagram.getMetadata().getTitle();
        final boolean valid = elementViolations
                .stream()
                .flatMap(v -> Stream.of(v.getDomainViolations(), v.getGraphViolations(), v.getModelViolations()))
                .flatMap(Collection::stream)
                .filter(v -> v instanceof ElementViolation)
                .map(v -> (ElementViolation) v)
                .map(v -> applyViolation(canvasHandler, v))
                .anyMatch(Boolean.FALSE::equals);

        if (valid) {
            validationSuccessEvent.fire(new CanvasValidationSuccessEvent(uuid, name, title));
        } else {
            validationFailEvent.fire(new CanvasValidationFailEvent(uuid, name, title, elementViolations));
        }
    }

    private boolean applyViolation(final H canvasHandler,
                                   final ElementViolation violation) {
        if (hasViolations(violation)) {
            final Shape shape = getShape(canvasHandler,
                                         violation.getUUID());
            if (null != shape) {
                shape.applyState(ShapeState.INVALID);
            }
            return true;
        }
        return false;
    }

    private Shape getShape(final H canvasHandler,
                           final String uuid) {
        return canvasHandler.getCanvas().getShape(uuid);
    }

    private boolean hasViolations(final ElementViolation violation) {
        return RuleViolation.Type.ERROR.equals(violation.getViolationType()) ||
                RuleViolation.Type.WARNING.equals(violation.getViolationType());
    }
}
