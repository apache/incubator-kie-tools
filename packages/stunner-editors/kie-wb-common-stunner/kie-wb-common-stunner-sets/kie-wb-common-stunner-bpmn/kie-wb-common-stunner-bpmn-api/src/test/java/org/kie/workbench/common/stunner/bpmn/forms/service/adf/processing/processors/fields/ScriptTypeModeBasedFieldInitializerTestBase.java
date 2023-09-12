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
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.FieldInitializer;
import org.kie.workbench.common.forms.adf.service.definitions.elements.FieldElement;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.ScriptTypeMode;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public abstract class ScriptTypeModeBasedFieldInitializerTestBase {

    @Mock
    protected FieldElement fieldElement;

    protected FieldDefinition fieldDefinition;

    @Mock
    protected FormGenerationContext context;

    protected Map<String, String> params = new HashMap<>();

    protected FieldInitializer initializer;

    @Before
    public void setUp() {
        fieldDefinition = mockFieldDefinition();
        initializer = newFieldInitializer();
        when(fieldElement.getParams()).thenReturn(params);
    }

    protected abstract FieldDefinition mockFieldDefinition();

    protected abstract FieldInitializer newFieldInitializer();

    protected abstract ScriptTypeMode getDefaultMode();

    protected void checkModeWasSet(ScriptTypeMode mode) {
        fail("checkModeWasSet was probalby not implemented in a child class");
    }

    @Test
    public void testSupports() {
        assertTrue(initializer.supports(fieldDefinition));
    }

    @Test
    public void testInitializeWithMode() {
        ScriptTypeMode arbitraryMode = ScriptTypeMode.COMPLETION_CONDITION;
        params.put("mode",
                   arbitraryMode.name());
        initializer.initialize(fieldDefinition,
                               fieldElement,
                               context);
        checkModeWasSet(arbitraryMode);
    }

    @Test
    public void testInitializeWithoutMode() {
        params.clear();
        initializer.initialize(fieldDefinition,
                               fieldElement,
                               context);
        checkModeWasSet(getDefaultMode());
    }
}