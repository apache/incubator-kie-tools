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

package org.kie.workbench.common.stunner.bpmn.backend.forms.gen.util;

import org.eclipse.emf.common.util.EList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.BaseDiagramMarshaller;
import org.kie.workbench.common.stunner.bpmn.backend.BaseDirectDiagramMarshaller;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.resource.JBPMBpmn2ResourceImpl;
import org.kie.workbench.common.stunner.core.backend.service.AbstractDefinitionSetService;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FormGenerationModelProviderHelperTest {

    @Mock
    private AbstractDefinitionSetService backendService;

    @Mock
    private BaseDiagramMarshaller oldMarshaller;

    @Mock
    private BaseDirectDiagramMarshaller newMarshaller;

    @Mock
    private Diagram diagram;

    private FormGenerationModelProviderHelper tested;

    @Before
    public void setUp() {
        tested = new FormGenerationModelProviderHelper(backendService);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGenerate_oldMasharller() throws Exception {
        when(backendService.getDiagramMarshaller()).thenReturn(oldMarshaller);

        final JBPMBpmn2ResourceImpl resource = mock(JBPMBpmn2ResourceImpl.class);
        when(resource.getContents()).thenReturn(mock(EList.class));
        when(oldMarshaller.marshallToBpmn2Resource(eq(diagram))).thenReturn(resource);

        tested.generate(diagram);

        verify(oldMarshaller, times(1)).marshallToBpmn2Resource(eq(diagram));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGenerate_newMasharller() throws Exception {
        when(backendService.getDiagramMarshaller()).thenReturn(newMarshaller);

        tested.generate(diagram);

        verify(newMarshaller, times(1)).marshallToBpmn2Definitions(eq(diagram));
    }
}