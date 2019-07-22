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

package org.kie.workbench.common.stunner.bpmn.backend.forms.gen;

import org.eclipse.bpmn2.Definitions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.backend.BPMNBackendService;
import org.kie.workbench.common.stunner.bpmn.backend.BPMNDirectDiagramMarshaller;
import org.kie.workbench.common.stunner.bpmn.backend.forms.gen.util.BPMNFormGenerationModelProviderHelper;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BPMNFormGenerationModelProviderTest {

    private static final String ID = "id";

    @Mock
    private BPMNDirectDiagramMarshaller bpmnDirectDiagramMarshaller;

    @Mock
    private BPMNBackendService bpmnBackendService;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private Diagram diagram;

    @Mock
    private Definitions definitions;

    @Mock
    private Metadata metadata;

    private BPMNFormGenerationModelProvider tested;

    private BPMNFormGenerationModelProviderHelper formGenerationModelProviderHelper;

    @Before
    public void init() {
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getDefinitionSetId()).thenReturn(ID);
        when(definitionUtils.getDefinitionSetId(eq(BPMNDefinitionSet.class))).thenReturn(ID);
        formGenerationModelProviderHelper = new BPMNFormGenerationModelProviderHelper(bpmnBackendService);
        tested = new BPMNFormGenerationModelProvider(definitionUtils, formGenerationModelProviderHelper);
        tested.init();
        verify(definitionUtils).getDefinitionSetId(eq(BPMNDefinitionSet.class));
    }

    @Test
    public void testAccepts() {
        assertTrue(tested.accepts(diagram));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGenerateForBPMNDDirectDiagramMarshaller() throws Exception {
        when(bpmnDirectDiagramMarshaller.marshallToBpmn2Definitions(diagram)).thenReturn(definitions);
        when(bpmnBackendService.getDiagramMarshaller()).thenReturn(bpmnDirectDiagramMarshaller);
        Definitions result = tested.generate(diagram);
        verify(bpmnDirectDiagramMarshaller, times(1)).marshallToBpmn2Definitions(eq(diagram));
        assertEquals(result, definitions);
    }
}
