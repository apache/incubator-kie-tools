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

package org.kie.workbench.common.stunner.core.validation.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.ModelBeanViolation;
import org.kie.workbench.common.stunner.core.validation.ModelValidator;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DiagramValidatorlTest {

    public static final String MODEL_VIOLATION = "model violation";
    public static final String RULE_VIOLATION = "rule violation";
    @Mock
    ModelValidator modelValidator;

    @Mock
    Diagram diagram;

    @Mock
    Metadata metadata;

    private TestDiagramValidator tested;
    private TestingGraphMockHandler graphTestHandler;

    private class TestDiagramValidator extends AbstractDiagramValidator {

        private TestDiagramValidator() {
            super(graphTestHandler.definitionManager,
                  graphTestHandler.ruleManager,
                  new TreeWalkTraverseProcessorImpl(),
                  modelValidator);
        }
    }

    @Before
    public void setup() throws Exception {
        this.graphTestHandler = new TestingGraphMockHandler();
        when(diagram.getName()).thenReturn("Test diagram");
        when(diagram.getMetadata()).thenReturn(metadata);
        this.tested = new TestDiagramValidator();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testValidateDiagram1() {
        final TestingGraphInstanceBuilder.TestGraph1 graph1 = TestingGraphInstanceBuilder.newGraph1(graphTestHandler);
        when(diagram.getGraph()).thenReturn(graphTestHandler.graph);
        tested.validate(diagram,
                        this::assertNoErrors);
        verify(modelValidator,
               times(1)).validate(eq(graph1.startNode),
                                  any(Consumer.class));
        verify(modelValidator,
               times(1)).validate(eq(graph1.intermNode),
                                  any(Consumer.class));
        verify(modelValidator,
               times(1)).validate(eq(graph1.endNode),
                                  any(Consumer.class));
        verify(modelValidator,
               times(1)).validate(eq(graph1.edge1),
                                  any(Consumer.class));
        verify(modelValidator,
               times(1)).validate(eq(graph1.edge2),
                                  any(Consumer.class));
        verify(modelValidator,
               times(1)).validate(eq(graphTestHandler.graph),
                                  any(Consumer.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testValidateDiagram1InvalidBean() {
        final TestingGraphInstanceBuilder.TestGraph1 graph1 = TestingGraphInstanceBuilder.newGraph1(graphTestHandler);
        when(diagram.getGraph()).thenReturn(graphTestHandler.graph);

        //model violation
        final ModelBeanViolation beanViolation = mock(ModelBeanViolation.class);
        when(beanViolation.getViolationType()).thenReturn(Violation.Type.ERROR);
        when(beanViolation.getMessage()).thenReturn(MODEL_VIOLATION);
        doAnswer(invocationOnMock -> {
            final Consumer<Collection<ModelBeanViolation>> validationsConsumer =
                    (Consumer<Collection<ModelBeanViolation>>) invocationOnMock.getArguments()[1];
            validationsConsumer.accept(Collections.singleton(beanViolation));
            return null;
        }).when(modelValidator).validate(eq(graph1.intermNode),
                                         any(Consumer.class));

        //graph violation
        RuleViolation ruleViolation = mock(RuleViolation.class);
        when(ruleViolation.getViolationType()).thenReturn(Violation.Type.ERROR);
        when(ruleViolation.getMessage()).thenReturn(RULE_VIOLATION);
        when(graphTestHandler.ruleManager.evaluate(any(RuleSet.class),
                                                   any(RuleEvaluationContext.class))).thenReturn(new DefaultRuleViolations().addViolation(ruleViolation));

        tested.validate(diagram,
                        violations -> assertElementError(violations,
                                                         TestingGraphInstanceBuilder.INTERM_NODE_UUID));
        verify(modelValidator,
               times(1)).validate(eq(graph1.startNode),
                                  any(Consumer.class));
        verify(modelValidator,
               times(1)).validate(eq(graph1.intermNode),
                                  any(Consumer.class));
        verify(modelValidator,
               times(1)).validate(eq(graph1.endNode),
                                  any(Consumer.class));
        verify(modelValidator,
               times(1)).validate(eq(graph1.edge1),
                                  any(Consumer.class));
        verify(modelValidator,
               times(1)).validate(eq(graph1.edge2),
                                  any(Consumer.class));
        verify(modelValidator,
               times(1)).validate(eq(graphTestHandler.graph),
                                  any(Consumer.class));
    }

    private void assertNoErrors(final
                                Collection<DiagramElementViolation<RuleViolation>> violations) {
        assertNotNull(violations);
        assertFalse(violations.stream()
                            .filter(v -> Violation.Type.ERROR.equals(v.getViolationType()))
                            .findAny()
                            .isPresent());
    }

    private void assertElementError(final
                                    Collection<DiagramElementViolation<RuleViolation>> violations,
                                    final String uuid) {
        assertNotNull(violations);

        assertTrue(violations.stream()
                           .filter(v -> Violation.Type.ERROR.equals(v.getViolationType()))
                           .anyMatch(v -> v.getUUID().equals(uuid)));

        //model violations
        assertTrue(violations.stream()
                           .map(DiagramElementViolation::getModelViolations)
                           .flatMap(Collection::stream)
                           .allMatch(v -> v.getMessage().equals(MODEL_VIOLATION)));

        //graph violations
        assertTrue(violations.stream()
                           .map(DiagramElementViolation::getGraphViolations)
                           .flatMap(Collection::stream)
                           .allMatch(v -> v.getMessage().equals(RULE_VIOLATION)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testValidateDiagramWithNullBean() {
        final Node nullNode = graphTestHandler.newNode(UUID.uuid(), Optional.empty());
        nullNode.setContent(null);
        when(diagram.getGraph()).thenReturn(graphTestHandler.graph);
        tested.validate(diagram, this::assertNoErrors);
    }
}
