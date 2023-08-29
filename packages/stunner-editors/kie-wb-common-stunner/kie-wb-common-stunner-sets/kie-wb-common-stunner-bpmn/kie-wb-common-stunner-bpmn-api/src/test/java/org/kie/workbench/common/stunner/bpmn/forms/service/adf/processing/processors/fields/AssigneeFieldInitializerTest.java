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

package org.kie.workbench.common.stunner.bpmn.forms.service.adf.processing.processors.fields;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.FormGenerationContext;
import org.kie.workbench.common.forms.adf.service.definitions.elements.FieldElement;
import org.kie.workbench.common.stunner.bpmn.forms.model.AssigneeEditorFieldDefinition;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.forms.model.AssigneeEditorFieldType.MAX_PARAM;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssigneeFieldInitializerTest {

    private static final String MAX = "5";

    protected AssigneeEditorFieldDefinition field;

    protected AssigneeFieldInitializer initializer;

    @Mock
    protected FieldElement fieldElement;

    @Mock
    protected FormGenerationContext context;

    protected Map<String, String> fieldElementParams = new HashMap<>();

    @Before
    public void init() {
        initializer = new AssigneeFieldInitializer();
        field = new AssigneeEditorFieldDefinition();
        field = spy(field);
        when(fieldElement.getParams()).thenReturn(fieldElementParams);
    }

    @Test
    public void testInitializeWithParams() {
        fieldElementParams.clear();
        fieldElementParams.put(MAX_PARAM,
                               MAX);

        initializer.initialize(field,
                               fieldElement,
                               context);

        verify(field).setMax(any());

        assertEquals(Integer.valueOf(MAX),
                     field.getMax());
    }

    @Test
    public void testInitializeWithoutParams() {
        fieldElementParams.clear();
        initializer.initialize(field,
                               fieldElement,
                               context);

        verify(field).setMax(any());

        assertEquals(Integer.valueOf(-1),
                     field.getMax());
    }
}
