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
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BPMNValidatorImplTest {

    private static final String BPMN_NO_END = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/validation/no-end.bpmn";
    private static final String BPMN_VALID = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/validation/valid.bpmn";

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
        Collection<BPMNViolation> violations = bpmnValidador.validate(getSerializedProcess(BPMN_VALID), "id");
        assertTrue(violations.isEmpty());
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
        when(diagramService.getRawContent(diagram)).thenReturn(getSerializedProcess(BPMN_NO_END));
        bpmnValidador.validate(diagram, result -> {
            assertNotNull(result);
            assertEquals(result.size(), 2);
            assertTrue(result.stream().map(Violation::getViolationType).allMatch(t -> Violation.Type.WARNING.equals(t)));
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