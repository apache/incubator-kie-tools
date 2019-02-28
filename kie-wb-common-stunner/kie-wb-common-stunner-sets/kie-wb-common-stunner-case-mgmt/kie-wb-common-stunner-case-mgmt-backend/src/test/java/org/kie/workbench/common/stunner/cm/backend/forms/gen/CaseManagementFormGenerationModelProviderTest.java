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

package org.kie.workbench.common.stunner.cm.backend.forms.gen;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.resource.JBPMBpmn2ResourceImpl;
import org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet;
import org.kie.workbench.common.stunner.cm.backend.CaseManagementBackendService;
import org.kie.workbench.common.stunner.cm.backend.CaseManagementDiagramMarshaller;
import org.kie.workbench.common.stunner.cm.backend.CaseManagementDirectDiagramMarshaller;
import org.kie.workbench.common.stunner.cm.backend.forms.gen.util.CaseManagementFormGenerationModelProviderHelper;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementFormGenerationModelProviderTest {

    private static final String ID = "id";

    @Mock
    private CaseManagementDiagramMarshaller cmDiagramMarshaller;

    @Mock
    private CaseManagementDirectDiagramMarshaller cmDirectDiagramMarshaller;

    @Mock
    private CaseManagementBackendService cmBackendService;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private Diagram diagram;

    @Mock
    private Definitions definitions;

    @Mock
    private Metadata metadata;

    private CaseManagementFormGenerationModelProvider tested;

    private CaseManagementFormGenerationModelProviderHelper formGenerationModelProviderHelper;

    @Before
    public void setUp() throws Exception {
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getDefinitionSetId()).thenReturn(ID);
        when(definitionUtils.getDefinitionSetId(eq(CaseManagementDefinitionSet.class))).thenReturn(ID);

        formGenerationModelProviderHelper = new CaseManagementFormGenerationModelProviderHelper(cmBackendService);
        tested = new CaseManagementFormGenerationModelProvider(definitionUtils, formGenerationModelProviderHelper);
        tested.init();
        verify(definitionUtils).getDefinitionSetId(eq(CaseManagementDefinitionSet.class));
    }

    @Test
    public void testAccepts() throws Exception {
        assertTrue(tested.accepts(diagram));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGenerateForCaseManagementDiagramMarshaller() throws Exception {
        JBPMBpmn2ResourceImpl bpmn2Resource = mock(JBPMBpmn2ResourceImpl.class);
        EList<EObject> contents = mock(EList.class);

        when(cmDiagramMarshaller.marshallToBpmn2Resource(diagram)).thenReturn(bpmn2Resource);
        when(bpmn2Resource.getContents()).thenReturn(contents);
        when(contents.get(0)).thenReturn(definitions);

        when(cmBackendService.getDiagramMarshaller()).thenReturn(cmDiagramMarshaller);
        Definitions result = tested.generate(diagram);
        verify(cmDiagramMarshaller).marshallToBpmn2Resource(diagram);
        assertEquals(result, definitions);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGenerateForCaseManagementDirectDiagramMarshaller() throws Exception {
        when(cmDirectDiagramMarshaller.marshallToBpmn2Definitions(diagram)).thenReturn(definitions);
        when(cmBackendService.getDiagramMarshaller()).thenReturn(cmDirectDiagramMarshaller);
        Definitions result = tested.generate(diagram);
        verify(cmDirectDiagramMarshaller, times(1)).marshallToBpmn2Definitions(eq(diagram));
        assertEquals(result, definitions);
    }
}