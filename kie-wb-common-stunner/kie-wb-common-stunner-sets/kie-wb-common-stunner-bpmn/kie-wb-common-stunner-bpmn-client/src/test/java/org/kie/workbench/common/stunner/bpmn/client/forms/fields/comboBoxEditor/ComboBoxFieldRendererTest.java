/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.comboBoxEditor;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.forms.model.ComboBoxFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.ComboBoxFieldType;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ComboBoxFieldRendererTest {

    @Mock
    private ComboBoxWidgetView comboBoxWidgetView;

    @Mock
    private ComboBoxFieldDefinition comboBoxFieldDefinition;

    @Spy
    @InjectMocks
    private ComboBoxFieldRenderer comboBoxFieldRenderer = new ComboBoxFieldRenderer(comboBoxWidgetView);

    @Test
    public void getName() throws Exception {
        Assert.assertEquals(comboBoxFieldRenderer.getName(),
                            ComboBoxFieldType.NAME);
    }

    @Test
    public void getSupportedCode() throws Exception {
        Assert.assertEquals(comboBoxFieldRenderer.getSupportedCode(),
                            ComboBoxFieldType.NAME);
    }

    @Test
    public void getSupportedFieldDefinition() throws Exception {
        Assert.assertEquals(comboBoxFieldRenderer.getSupportedFieldDefinition(),
                            ComboBoxFieldDefinition.class);
    }
}
