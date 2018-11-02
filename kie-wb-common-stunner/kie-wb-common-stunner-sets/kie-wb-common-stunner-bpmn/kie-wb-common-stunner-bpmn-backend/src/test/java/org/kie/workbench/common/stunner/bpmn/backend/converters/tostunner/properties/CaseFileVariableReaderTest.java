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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Property;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseFileVariables;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CaseFileVariableReaderTest {

    private List<Property> properties;

    @Mock
    protected Property property1;
    @Mock
    protected Property property2;
    @Mock
    protected Property property3;
    @Mock
    protected Property property4;

    @Mock
    protected ItemDefinition definition;

    @Before
    public void setup() {
        properties = new ArrayList<>();
        properties.add(property1);
        properties.add(property2);
        properties.add(property3);
        properties.add(property4);

        when(property1.getName()).thenReturn(CaseFileVariables.CASE_FILE_PREFIX + "CFV1");
        when(property1.getId()).thenReturn(CaseFileVariables.CASE_FILE_PREFIX + "CFV1");
        when(property1.getItemSubjectRef()).thenReturn(definition);

        when(property2.getName()).thenReturn(null);
        when(property2.getId()).thenReturn(CaseFileVariables.CASE_FILE_PREFIX + "CFV2");
        when(property2.getItemSubjectRef()).thenReturn(definition);

        when(definition.getStructureRef()).thenReturn("Boolean");

        when(property3.getName()).thenReturn("PV1");
        when(property3.getId()).thenReturn("PV1");

        when(property4.getName()).thenReturn(null);
        when(property4.getId()).thenReturn("PV2");
    }

    @Test
    public void getCaseFileVariables() {
        String caseFileVariables = CaseFileVariableReader.getCaseFileVariables(properties);
        assertEquals(caseFileVariables, "CFV1:Boolean,CFV2:Boolean");
    }

    @Test
    public void isCaseFileVariable() {
        boolean isCaseFile1 = CaseFileVariableReader.isCaseFileVariable(property1);
        assertTrue(isCaseFile1);

        boolean isCaseFile2 = CaseFileVariableReader.isCaseFileVariable(property2);
        assertTrue(isCaseFile2);

        boolean isCaseFile3 = CaseFileVariableReader.isCaseFileVariable(property3);
        assertFalse(isCaseFile3);

        boolean isCaseFile4 = CaseFileVariableReader.isCaseFileVariable(property4);
        assertFalse(isCaseFile4);
    }
}
