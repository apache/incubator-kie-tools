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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.comboBoxEditor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerBPMNConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.bpmn.forms.model.ComboBoxFieldDefinition;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractComboBoxFieldRendererTest {

    @Mock
    private ComboBoxWidgetView comboBoxWidgetView;

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private ComboBoxFieldDefinition comboBoxFieldDefinition;

    @Spy
    @InjectMocks
    private AbstractComboBoxFieldRenderer comboBoxFieldRenderer = new ComboBoxFieldRenderer(comboBoxWidgetView, translationService);

    private Map<String, String> options;

    @Before
    public void setUp() {
        options = new HashMap<String, String>();
        options.put("age",
                    "33");
        options.put("height",
                    "1.77");
        options.put("gender",
                    "male");

        when(translationService.getValue(StunnerBPMNConstants.EDIT)).thenReturn("Edit");
    }

    @Test
    public void testRefreshInput() {
        comboBoxFieldRenderer.refreshInput(options,
                                           null);
        verify(comboBoxWidgetView,
               times(1)).setComboBoxValues(any(ListBoxValues.class));
    }

    @Test
    public void testSetComboBoxValues() {
        List<String> values = Arrays.asList(new String[]{"age", "height", "sex"});
        comboBoxFieldRenderer.setComboBoxValues(values);
        verify(comboBoxWidgetView,
               times(1)).setComboBoxValues(any(ListBoxValues.class));
    }

    @Test
    public void setReadOnly() throws Exception {
        comboBoxFieldRenderer.setReadOnly(true);
        verify(comboBoxWidgetView,
               times(1)).setReadOnly(true);
    }
}
