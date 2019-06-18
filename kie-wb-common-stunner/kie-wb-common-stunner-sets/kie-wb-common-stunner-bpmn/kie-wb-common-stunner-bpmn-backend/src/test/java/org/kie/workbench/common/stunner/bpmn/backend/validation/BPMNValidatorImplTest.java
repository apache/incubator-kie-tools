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

package org.kie.workbench.common.stunner.bpmn.backend.validation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.validation.BPMNViolation;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.service.DiagramService;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.kie.workbench.common.stunner.core.validation.DomainViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BPMNValidatorImplTest {

    private static final String BPMN_VALID = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/validation/valid.bpmn";
    private static final String BPMN_VALIDATION_ISSUES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/validation/validation_issues.bpmn";
    public static final String PROCESS_UUID = "id";

    private BPMNValidatorImpl bpmnValidador;

    @Mock
    private DiagramService diagramService;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Before
    public void setUp() {
        when(diagram.getMetadata()).thenReturn(metadata);
        bpmnValidador = new BPMNValidatorImpl(diagramService);
        bpmnValidador.init();
    }

    private InputStream loadStream(String path) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }

    @Test
    public void validateSerialized() {
        Collection<BPMNViolation> violations = bpmnValidador.validate(getSerializedProcess(BPMN_VALID), PROCESS_UUID);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void validateWithExceptionsOnParsingXML() {
        final Collection<BPMNViolation> violations = bpmnValidador.validate("INVALID_XML", PROCESS_UUID);
        assertProcessException(violations);
    }

    private void assertProcessException(Collection<BPMNViolation> violations) {
        assertEquals(violations.size(), 1);
        assertEquals(1, violations.stream()
                .map(BPMNViolation::getUUID)
                .filter(PROCESS_UUID::equals)
                .count());
    }

    @Test
    public void validateWithException() {
        when(diagram.getMetadata()).thenThrow(new RuntimeException());

        final Collection<BPMNViolation> violations = bpmnValidador.validate(null, PROCESS_UUID);
        assertProcessException(violations);
    }

    private String getSerializedProcess(String path) {
        try {
            return IOUtils.toString(loadStream(path), "UTF-8");
        } catch (IOException e) {
            return null;
        }
    }

    @Test
    public void validateWithViolation() {
        when(diagramService.getRawContent(diagram)).thenReturn(getSerializedProcess(BPMN_VALIDATION_ISSUES));
        bpmnValidador.validate(diagram, result -> {
            assertNotNull(result);
            assertEquals(10, result.size());
            assertTrue(result.stream().map(DomainViolation::getViolationType).allMatch(t -> Violation.Type.WARNING.equals(t)));
            assertEquals(10, result.stream().map(DomainViolation::getUUID).filter(StringUtils::nonEmpty).count());

            //task1
            assertEquals(4, result.stream()
                    .map(DomainViolation::getUUID)
                    .filter("_426E32AD-E08B-4025-B201-9850EEC82254"::equals)
                    .count());
            //task2
            assertEquals(3, result.stream()
                    .map(DomainViolation::getUUID)
                    .filter("_3E6C197E-FF8A-41F4-AEB6-710454E8529C"::equals)
                    .count());

            //startEvent
            assertEquals(1, result.stream()
                    .map(DomainViolation::getUUID)
                    .filter("_0F455E77-669C-480F-A4A2-C5070EF1A83F"::equals)
                    .count());

            //endEvent
            assertEquals(2, result.stream()
                    .map(DomainViolation::getUUID)
                    .filter("_5B1068ED-1260-41FD-B7C1-226CB909E569"::equals)
                    .count());
        });
    }

    @Test
    public void validateNoViolations() {
        when(diagramService.getRawContent(diagram)).thenReturn(getSerializedProcess(BPMN_VALID));
        bpmnValidador.validate(diagram, result -> {
            assertNotNull(result);
            assertTrue(result.isEmpty());
        });
    }

    @Test
    public void getDefinitionSetId() {
        assertEquals(bpmnValidador.getDefinitionSetId(), BindableAdapterUtils.getDefinitionId(BPMNDefinitionSet.class));
    }
}