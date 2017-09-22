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

package org.kie.workbench.common.forms.editor.client.editor.dataProviders;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.StringSelectorOption;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.StringListBoxFieldDefinition;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SelectorOptionsProviderTest {

    public static final String VAL_1 = "val1";
    public static final String VAL_2 = "val2";
    public static final String VAL_3 = "val3";
    public static final String VAL_4 = "val4";

    @Mock
    private FormRenderingContext<StringListBoxFieldDefinition> context;

    private StringListBoxFieldDefinition field = new StringListBoxFieldDefinition();

    private SelectorOptionsProvider provider = new SelectorOptionsProvider();

    @Before
    public void init() {
        field.getOptions().add(new StringSelectorOption(VAL_1,
                                                        VAL_1));
        field.getOptions().add(new StringSelectorOption(VAL_2,
                                                        VAL_2));
        field.getOptions().add(new StringSelectorOption(VAL_3,
                                                        VAL_3));
        field.getOptions().add(new StringSelectorOption(VAL_4,
                                                        VAL_4));

        field = spy(field);
        when(context.getModel()).thenReturn(field);
    }

    @Test
    public void testListBoxWithExistingDefaultValue() {

        when(field.getDefaultValue()).thenReturn(VAL_2);

        SelectorData<String> data = provider.getSelectorData(context);

        doTestVerifications(data);

        assertNotNull(data.getSelectedValue());
        assertEquals(VAL_2,
                     data.getSelectedValue());
    }

    @Test
    public void testListBoxWithoutDefaultValue() {

        SelectorData<String> data = provider.getSelectorData(context);

        doTestVerifications(data);

        assertNull(data.getSelectedValue());
    }

    protected void doTestVerifications(SelectorData<String> data) {
        verify(field).getOptions();
        verify(field).getDefaultValue();

        assertNotNull(data);
        assertNotNull(data.getValues());
        assertFalse(data.getValues().isEmpty());
        assertEquals(field.getOptions().size(),
                     data.getValues().size());

        field.getOptions().forEach(option -> {
            String selectorText = data.getValues().get(option.getValue());
            assertNotNull(selectorText);
            assertEquals(option.getText(),
                         selectorText);
        });
    }
}
