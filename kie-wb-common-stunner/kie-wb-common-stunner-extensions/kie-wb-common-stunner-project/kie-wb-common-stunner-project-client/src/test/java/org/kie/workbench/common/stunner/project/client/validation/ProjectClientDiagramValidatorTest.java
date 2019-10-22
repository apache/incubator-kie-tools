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

package org.kie.workbench.common.stunner.project.client.validation;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.ModelValidator;
import org.kie.workbench.common.stunner.core.validation.impl.ElementViolationImpl;
import org.kie.workbench.common.stunner.project.service.ProjectValidationService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectClientDiagramValidatorTest {

    private ProjectClientDiagramValidator clientDiagramValidator;

    @Mock
    private TreeWalkTraverseProcessor treeWalkTraverseProcessor;

    @Mock
    private ModelValidator modelValidator;

    @Mock
    private ProjectValidationService validationService;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    private TestingGraphMockHandler graphTestHandler;

    private String uuid = UUID.uuid();

    private ElementViolationImpl backendViolation;

    @Before
    public void setUp() throws Exception {
        this.graphTestHandler = new TestingGraphMockHandler();
        treeWalkTraverseProcessor = new TreeWalkTraverseProcessorImpl();
        backendViolation = new ElementViolationImpl.Builder().setUuid(uuid).build();
        Collection<DiagramElementViolation<RuleViolation>> violations = Collections.singletonList(backendViolation);
        when(diagram.getName()).thenReturn("Test diagram");
        when(diagram.getMetadata()).thenReturn(metadata);
        when(validationService.validate(diagram)).thenReturn(violations);
        clientDiagramValidator = new ProjectClientDiagramValidator(graphTestHandler.getDefinitionManager(),
                                                                   graphTestHandler.getRuleManager(),
                                                                   treeWalkTraverseProcessor,
                                                                   modelValidator,
                                                                   new CallerMock<>(validationService));
    }

    @Test
    public void validate() {
        when(diagram.getGraph()).thenReturn(graphTestHandler.graph);
        clientDiagramValidator.validate(diagram, result -> assertTrue(result.stream().anyMatch(v -> Objects.equals(backendViolation, v))));
        verify(validationService).validate(diagram);
    }
}