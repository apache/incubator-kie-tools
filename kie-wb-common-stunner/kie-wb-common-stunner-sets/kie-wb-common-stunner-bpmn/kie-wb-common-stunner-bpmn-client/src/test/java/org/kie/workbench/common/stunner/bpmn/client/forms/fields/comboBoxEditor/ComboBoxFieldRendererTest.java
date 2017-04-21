/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.comboBoxEditor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.bpmn.forms.model.ComboBoxFieldDefinition;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ComboBoxFieldRendererTest {

    @Mock
    private ComboBoxWidgetView comboBoxWidgetView;

    @Mock
    ComboBoxFieldDefinition comboBoxFieldDefinition;

    @Spy
    @InjectMocks
    ComboBoxFieldRenderer comboBoxFieldRenderer = new ComboBoxFieldRenderer(comboBoxWidgetView);

    @Before
    public void setUp() {
    }

    @Test
    public void testRefreshInput() {
        Map<String, String> options = new HashMap<String, String>();
        options.put("age",
                    "age");
        options.put("height",
                    "height");
        options.put("sex",
                    "sex");
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
}
