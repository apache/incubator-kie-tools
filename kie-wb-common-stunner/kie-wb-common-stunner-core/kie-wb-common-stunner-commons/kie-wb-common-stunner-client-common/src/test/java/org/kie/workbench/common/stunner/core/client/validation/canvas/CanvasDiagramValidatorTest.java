/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramValidator;
import org.kie.workbench.common.stunner.core.validation.DomainViolation;
import org.kie.workbench.common.stunner.core.validation.ModelBeanViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CanvasDiagramValidatorTest {

    public static final String UUID = "uuid";
    public static final String NAME = "name";
    public static final String TITLE = "title";
    private CanvasDiagramValidator tested;

    @Mock
    private DiagramValidator<Diagram, RuleViolation> diagramValidator;

    @Mock
    private EventSourceMock<CanvasValidationSuccessEvent> successEvent;

    @Mock
    private EventSourceMock<CanvasValidationFailEvent> failEvent;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Consumer<Collection<DiagramElementViolation<RuleViolation>>> callback;

    @Mock
    private Diagram diagram;

    private Collection<DiagramElementViolation<RuleViolation>> violations;

    @Mock
    private Metadata metadata;

    @Mock
    private DiagramElementViolation<RuleViolation> violation;

    @Mock
    private DomainViolation domainViolation;

    @Mock
    private RuleViolation ruleViolation;

    @Mock
    private ModelBeanViolation modelViolation;

    @Mock
    private Canvas canvas;

    @Mock
    private Shape shape;

    @Before
    public void setUp() throws Exception {
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvas.getShape(anyString())).thenReturn(shape);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getTitle()).thenReturn(TITLE);
        when(diagram.getName()).thenReturn(NAME);
        when(violation.getUUID()).thenReturn(UUID);
        when(violation.getDomainViolations()).thenReturn(Arrays.asList(domainViolation));
        when(violation.getGraphViolations()).thenReturn(Arrays.asList(ruleViolation));
        when(violation.getModelViolations()).thenReturn(Arrays.asList(modelViolation));
        when(domainViolation.getViolationType()).thenReturn(Violation.Type.ERROR);
        when(ruleViolation.getViolationType()).thenReturn(Violation.Type.ERROR);
        when(modelViolation.getViolationType()).thenReturn(Violation.Type.ERROR);

        violations = Arrays.asList(violation);
        tested = new CanvasDiagramValidator(diagramValidator, successEvent, failEvent);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void validateFailed() {
        ArgumentCaptor<Consumer> captor = ArgumentCaptor.forClass(Consumer.class);
        ArgumentCaptor<CanvasValidationFailEvent> captorEvent = ArgumentCaptor.forClass(CanvasValidationFailEvent.class);

        tested.validate(canvasHandler, callback);
        verify(diagramValidator).validate(eq(diagram), captor.capture());
        captor.getValue().accept(violations);
        verify(callback).accept(violations);
        verify(failEvent).fire(captorEvent.capture());
        CanvasValidationFailEvent event = captorEvent.getValue();
        assertEquals(event.getViolations(), violations);
        assertEquals(event.getDiagramName(), NAME);
        assertEquals(event.getDiagramTitle(), TITLE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void validateSuccess() {
        when(domainViolation.getViolationType()).thenReturn(Violation.Type.INFO);
        when(ruleViolation.getViolationType()).thenReturn(Violation.Type.INFO);
        when(modelViolation.getViolationType()).thenReturn(Violation.Type.INFO);

        ArgumentCaptor<Consumer> captor = ArgumentCaptor.forClass(Consumer.class);
        ArgumentCaptor<CanvasValidationSuccessEvent> captorEvent = ArgumentCaptor.forClass(CanvasValidationSuccessEvent.class);

        tested.validate(canvasHandler, callback);
        verify(diagramValidator).validate(eq(diagram), captor.capture());
        captor.getValue().accept(violations);
        verify(callback).accept(violations);
        verify(successEvent).fire(captorEvent.capture());
        CanvasValidationSuccessEvent event = captorEvent.getValue();
        assertEquals(event.getDiagramName(), NAME);
        assertEquals(event.getDiagramTitle(), TITLE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void validateSuccessNoElementViolations() {
        final ArgumentCaptor<Consumer> captor = ArgumentCaptor.forClass(Consumer.class);
        final ArgumentCaptor<CanvasValidationSuccessEvent> captorEvent = ArgumentCaptor.forClass(CanvasValidationSuccessEvent.class);
        violations = Collections.emptyList();

        tested.validate(canvasHandler, callback);

        verify(diagramValidator).validate(eq(diagram), captor.capture());

        captor.getValue().accept(violations);

        verify(callback).accept(violations);
        verify(successEvent).fire(captorEvent.capture());
        CanvasValidationSuccessEvent event = captorEvent.getValue();
        assertEquals(event.getDiagramName(), NAME);
        assertEquals(event.getDiagramTitle(), TITLE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void validateSuccessNoDomainGraphModelViolations() {
        final ArgumentCaptor<Consumer> captor = ArgumentCaptor.forClass(Consumer.class);
        final ArgumentCaptor<CanvasValidationSuccessEvent> captorEvent = ArgumentCaptor.forClass(CanvasValidationSuccessEvent.class);
        reset(violation);

        when(violation.getUUID()).thenReturn(UUID);
        when(violation.getDomainViolations()).thenReturn(Collections.emptyList());
        when(violation.getGraphViolations()).thenReturn(Collections.emptyList());
        when(violation.getModelViolations()).thenReturn(Collections.emptyList());

        tested.validate(canvasHandler, callback);

        verify(diagramValidator).validate(eq(diagram), captor.capture());

        captor.getValue().accept(violations);

        verify(callback).accept(violations);
        verify(successEvent).fire(captorEvent.capture());
        CanvasValidationSuccessEvent event = captorEvent.getValue();
        assertEquals(event.getDiagramName(), NAME);
        assertEquals(event.getDiagramTitle(), TITLE);
    }
}