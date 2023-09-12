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

import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.TextBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBox;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
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
        doCallRealMethod().when(view).setValue(any());
        doCallRealMethod().when(view).setValue(any(),
                                               anyBoolean());
        doCallRealMethod().when(view).setComboBoxValues(Mockito.<ListBoxValues>any());
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
                              any(),
                              any());
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
