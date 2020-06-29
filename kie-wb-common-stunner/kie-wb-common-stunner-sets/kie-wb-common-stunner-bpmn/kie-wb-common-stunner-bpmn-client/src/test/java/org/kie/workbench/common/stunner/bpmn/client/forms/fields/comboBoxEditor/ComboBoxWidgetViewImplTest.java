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

import com.google.gwtmockito.GwtMockito;
import org.gwtbootstrap3.client.ui.TextBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBox;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ComboBoxWidgetViewImplTest {

    TextBox customValueField;

    ComboBox valueComboBox;

    ComboBoxWidgetViewImpl.DataModel dataModel;

    ComboBoxWidgetViewImpl view;

    @Before
    public void setUp() throws Exception {
        GwtMockito.initMocks(this);
        customValueField = mock(TextBox.class);
        valueComboBox = mock(ComboBox.class);
        dataModel = mock(ComboBoxWidgetViewImpl.DataModel.class);
        view = mock(ComboBoxWidgetViewImpl.class);
        view.customValueField = customValueField;

        view.valueComboBox = valueComboBox;
        view.dataModel = dataModel;
        doCallRealMethod().when(view).init();
        doCallRealMethod().when(view).setValue(anyString());
        doCallRealMethod().when(view).setValue(anyString(),
                                               anyBoolean());
        doCallRealMethod().when(view).setComboBoxValues(any(ListBoxValues.class));
    }

    @Test
    public void testInit() {
        view.init();

        verify(valueComboBox,
               times(1)).init(any(),
                              anyBoolean(),
                              any(),
                              any(),
                              anyBoolean(),
                              anyBoolean(),
                              anyString(),
                              anyString());
    }

    @Test
    public void testSetValue() {
        ListBoxValues listBoxValues = new ListBoxValues(null,
                                                        null,
                                                        null);
        view.setComboBoxValues(listBoxValues);
        String value = "test";
        view.setValue(value);

        verify(view,
               times(1)).setValue(value,
                                  false);
        verify(view,
               times(1)).initView();
    }

    @Test
    public void testSetComboBoxValues() {
        ListBoxValues listBoxValues = new ListBoxValues(null,
                                                        null,
                                                        null);
        view.setComboBoxValues(listBoxValues);

        verify(valueComboBox,
               times(1)).setCurrentTextValue("");
        verify(valueComboBox,
               times(1)).setListBoxValues(listBoxValues);
        verify(valueComboBox,
               times(1)).setShowCustomValues(true);
    }
}
