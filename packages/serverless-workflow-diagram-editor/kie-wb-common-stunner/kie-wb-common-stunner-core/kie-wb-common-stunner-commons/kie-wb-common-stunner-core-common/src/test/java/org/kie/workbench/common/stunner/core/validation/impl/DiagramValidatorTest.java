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


package org.kie.workbench.common.stunner.core.validation.impl;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import jakarta.enterprise.inject.Instance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.j2cl.tools.di.core.ManagedInstance;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.DomainValidator;
import org.kie.workbench.common.stunner.core.validation.DomainViolation;
import org.kie.workbench.common.stunner.core.validation.ModelBeanViolation;
import org.kie.workbench.common.stunner.core.validation.ModelValidator;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DiagramValidatorTest {

    public static final String MODEL_VIOLATION = "model violation";
    public static final String RULE_VIOLATION = "rule violation";

    public static final String DEF_SET_ID = "defSetId";
    public static final String UUID_0 = "uuid0";
    public static final String UUID_1 = "uuid1";

    @Mock
    ModelValidator modelValidator;

    @Mock
    Diagram diagram;

    @Mock
    Metadata metadata;

    Instance<DomainValidator> validators;

    @Mock
    private DomainValidator domainValidator;

    private List<DomainViolation> domainViolationList;

    @Mock
    private DomainViolation domainViolation;

    @Mock
    private DomainViolation domainViolation2;

    @Mock
    private DomainViolation domainViolation3;

    @Mock
    private DomainViolation domainViolation4;

    @Mock
    private DomainViolation domainViolationNull;

    @Mock
    private DomainViolation domainViolationNullStr;

    private TestDiagramValidator tested;
    private TestingGraphMockHandler graphTestHandler;

    private class TestDiagramValidator extends AbstractDiagramValidator {

        private TestDiagramValidator() {
            super(graphTestHandler.getDefinitionManager(),
                  graphTestHandler.getRuleManager(),
                  new TreeWalkTraverseProcessorImpl(),
                  modelValidator, new ManagedInstanceStub<>(domainValidator));
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

        domainViolationList = Arrays.asList(domainViolation,
                                            domainViolation2,
                                            domainViolation3,
                                            domainViolation4,
                                            domainViolationNull,
                                            domainViolationNullStr);
        domainValidator = new DomainValidator() {
            @Override
            public String getDefinitionSetId() {
                return DEF_SET_ID;
            }

            @Override
            public void validate(Diagram entity, Consumer<Collection<DomainViolation>> resultConsumer) {
                resultConsumer.accept(domainViolationList);
            }
        };

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
        when(graphTestHandler.getRuleManager().evaluate(any(),
                                                        any())).thenReturn(new DefaultRuleViolations().addViolation(ruleViolation));

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

    @Test
    @SuppressWarnings("unchecked")
    public void testValidateDiagramOneRuleViolationEmptyModelViolations() {
        final TestingGraphInstanceBuilder.TestGraph1 graph1 = TestingGraphInstanceBuilder.newGraph1(graphTestHandler);
        when(diagram.getGraph()).thenReturn(graphTestHandler.graph);

        //"Empty" model violation
        doAnswer(invocationOnMock -> {
            final Consumer<Collection<ModelBeanViolation>> validationsConsumer =
                    (Consumer<Collection<ModelBeanViolation>>) invocationOnMock.getArguments()[1];
            validationsConsumer.accept(Collections.emptyList());
            return null;
        }).when(modelValidator).validate(eq(graph1.intermNode),
                                         any());

        //graph violation
        RuleViolation ruleViolation = mock(RuleViolation.class);
        when(ruleViolation.getViolationType()).thenReturn(Violation.Type.ERROR);
        when(ruleViolation.getMessage()).thenReturn(RULE_VIOLATION);
        when(graphTestHandler.getRuleManager().evaluate(any(),
                                                        any())).thenReturn(new DefaultRuleViolations().addViolation(ruleViolation));

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

    @Test
    @SuppressWarnings("unchecked")
    public void testValidateDiagramZeroRuleViolationsEmptyModelViolations() {
        final TestingGraphInstanceBuilder.TestGraph1 graph1 = TestingGraphInstanceBuilder.newGraph1(graphTestHandler);
        when(diagram.getGraph()).thenReturn(graphTestHandler.graph);

        //"Empty" model violation
        doAnswer(invocationOnMock -> {
            final Consumer<Collection<ModelBeanViolation>> validationsConsumer =
                    (Consumer<Collection<ModelBeanViolation>>) invocationOnMock.getArguments()[1];
            validationsConsumer.accept(Collections.emptyList());
            return null;
        }).when(modelValidator).validate(eq(graph1.intermNode),
                                         any(Consumer.class));

        tested.validate(diagram,
                        violations -> assertTrue(violations.isEmpty()));

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
        assertFalse(violations.stream().anyMatch(v -> Violation.Type.ERROR.equals(v.getViolationType())));
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

    private void mockViolations(List<DomainViolation> violations) {
        violations.stream().forEach(v -> {
            when(v.getViolationType()).thenReturn(Violation.Type.ERROR);
            when(v.getUUID()).thenReturn(UUID_1);
        });

        DomainViolation first = violations.get(0);
        when(first.getUUID()).thenReturn(UUID_0);

        DomainViolation last1 = violations.get(violations.size() - 2);
        when(last1.getUUID()).thenReturn(null);

        DomainViolation last = violations.get(violations.size() - 1);
        when(last.getUUID()).thenReturn("null");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testValidateDiagramWithNullBean() {
        final Node nullNode = graphTestHandler.newNode(UUID.uuid(), Optional.empty());
        nullNode.setContent(null);
        when(diagram.getGraph()).thenReturn(graphTestHandler.graph);
        tested.validate(diagram, this::assertNoErrors);
    }

    public class ManagedInstanceStub<T> implements ManagedInstance<T> {

        private final T[] instances;

        public ManagedInstanceStub(T... instances) {
            this.instances = instances;
        }

        @Override
        public ManagedInstance<T> select(Annotation... annotations) {
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <U extends T> ManagedInstance<U> select(Class<U> aClass, Annotation... annotations) {
            return (ManagedInstance<U>) this;
        }

        @Override
        public boolean isUnsatisfied() {
            return false;
        }

        @Override
        public boolean isAmbiguous() {
            return false;
        }

        @Override
        public void destroy(T t) {
        }

        @Override
        public void destroyAll() {
        }

        @Override
        public Iterator<T> iterator() {
            return Arrays.asList(this.instances).iterator();
        }

        @Override
        public T get() {
            return instances[0];
        }
    }
}
