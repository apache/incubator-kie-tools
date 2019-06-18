/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.backend.service;

/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.DomainValidator;
import org.kie.workbench.common.stunner.core.validation.DomainViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.MockInstanceImpl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ValidationServiceImplTest {

    public static final String DEF_SET_ID = "defSetId";
    public static final String UUID_0 = "uuid0";
    public static final String UUID_1 = "uuid1";

    private ValidationServiceImpl validationService;

    @Mock
    private DomainValidator domainValidator;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

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

    @Mock
    private Graph graph;

    private static final String GRAPH_UUID = UUID.uuid();

    private List<DomainViolation> domainViolationList;

    @Before
    public void setUp() {
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

        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getDefinitionSetId()).thenReturn(DEF_SET_ID);
        when(diagram.getGraph()).thenReturn(graph);
        when(graph.getUUID()).thenReturn(GRAPH_UUID);
        mockViolations(domainViolationList);

        validationService = new ValidationServiceImpl(new MockInstanceImpl(domainValidator));
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
    public void validate() {
        final Collection<DiagramElementViolation<RuleViolation>> violations = validationService.validate(diagram);
        verify(diagram).getMetadata();
        verify(metadata).getDefinitionSetId();
        assertEquals(violations.size(), 2);

        List<DomainViolation> violations0 = Arrays.asList(domainViolation);
        List<DomainViolation> violations1 =
                Arrays.asList(domainViolation2, domainViolation3, domainViolation4);

        assertEquals(violations.stream()
                             .filter(v -> UUID_1.equals(v.getUUID()))
                             .findFirst()
                             .map(DiagramElementViolation::getDomainViolations)
                             .get(), violations1);

        assertEquals(violations.stream()
                             .filter(v -> UUID_0.equals(v.getUUID()))
                             .findFirst()
                             .map(DiagramElementViolation::getDomainViolations)
                             .get(), violations0);
    }
}