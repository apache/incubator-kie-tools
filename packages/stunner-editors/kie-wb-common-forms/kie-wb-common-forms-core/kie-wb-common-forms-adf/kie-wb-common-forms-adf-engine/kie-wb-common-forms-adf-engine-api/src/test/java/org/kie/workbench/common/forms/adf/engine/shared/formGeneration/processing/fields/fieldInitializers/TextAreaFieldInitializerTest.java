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

package org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.FormGenerationContext;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.I18nHelper;
import org.kie.workbench.common.forms.adf.service.definitions.elements.FieldElement;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TextAreaFieldInitializerTest {

    private static final String ROWS_PARAM = "rows";
    private static final String ROWS = "10";
    private static final String PLACEHOLDER_PARAM = "placeHolder";
    private static final String PLACEHOLDER = "placeholder";

    private HasPlaceHolderFieldInitializer placeholderInitializer;
    private HasRowsFieldInitializer rowsInitializer;

    private TextAreaFieldDefinition field;

    @Mock
    private FieldElement fieldElement;

    @Mock
    private FormGenerationContext context;

    @Mock
    private I18nHelper i18nHelper;

    private Map<String, String> fieldElementParams = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        placeholderInitializer = new HasPlaceHolderFieldInitializer();
        rowsInitializer = new HasRowsFieldInitializer();
        field = spy(new TextAreaFieldDefinition());
        when(fieldElement.getParams()).thenReturn(fieldElementParams);
        when(context.getI18nHelper()).thenReturn(i18nHelper);
        when(i18nHelper.getTranslation(PLACEHOLDER)).thenReturn(PLACEHOLDER);
    }

    @Test
    public void testInitializeWithParams() throws Exception {
        fieldElementParams.put(PLACEHOLDER_PARAM,
                               PLACEHOLDER);
        fieldElementParams.put(ROWS_PARAM,
                               ROWS);

        placeholderInitializer.initialize(field,
                                          fieldElement,
                                          context);
        rowsInitializer.initialize(field,
                                   fieldElement,
                                   context);

        verify(field).setPlaceHolder(any());
        verify(field).setRows(any());

        assertEquals(PLACEHOLDER,
                     field.getPlaceHolder());
        assertEquals(Integer.valueOf(ROWS),
                     field.getRows());
    }
}
