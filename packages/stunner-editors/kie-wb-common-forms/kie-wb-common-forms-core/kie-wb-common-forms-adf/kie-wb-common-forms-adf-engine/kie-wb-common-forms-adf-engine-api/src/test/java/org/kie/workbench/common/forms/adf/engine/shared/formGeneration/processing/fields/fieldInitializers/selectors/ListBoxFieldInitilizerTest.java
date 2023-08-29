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

package org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.selectors;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.FormGenerationContext;
import org.kie.workbench.common.forms.adf.service.definitions.elements.FieldElement;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.ListBoxBaseDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.StringListBoxFieldDefinition;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ListBoxFieldInitilizerTest {

    private static final String ADD_EMPTY_OPTION = "addEmptyOption";

    private ListBoxFieldInitializer initializer;

    private ListBoxBaseDefinition field;

    @Mock
    private FieldElement fieldElement;

    @Mock
    private FormGenerationContext context;

    private Map<String, String> fieldElementParams = new HashMap<>();

    @Before
    public void init() {
        initializer = new ListBoxFieldInitializer();
        field = spy(new StringListBoxFieldDefinition());
        when(fieldElement.getParams()).thenReturn(fieldElementParams);
    }

    @Test
    public void testInitializeTrueValue() {

        fieldElementParams.put(ADD_EMPTY_OPTION,
                               "true");

        initializer.initialize(field,
                               fieldElement,
                               context);

        verify(field).setAddEmptyOption(true);
        assertTrue(field.getAddEmptyOption());
    }

    @Test
    public void testInitializeFalseValue() {

        fieldElementParams.put(ADD_EMPTY_OPTION,
                               "false");

        initializer.initialize(field,
                               fieldElement,
                               context);

        verify(field).setAddEmptyOption(false);
        assertFalse(field.getAddEmptyOption());
    }

    @Test
    public void testInitializeNullValue() {
        initializer.initialize(field,
                               fieldElement,
                               context);

        verify(field, never()).setAddEmptyOption(anyBoolean());
        assertTrue(field.getAddEmptyOption());
    }

    @Test
    public void testInitializeWrongValue() {

        fieldElementParams.put(ADD_EMPTY_OPTION,
                               "");

        initializer.initialize(field,
                               fieldElement,
                               context);

        verify(field).setAddEmptyOption(false);
        assertFalse(field.getAddEmptyOption());
    }
}
