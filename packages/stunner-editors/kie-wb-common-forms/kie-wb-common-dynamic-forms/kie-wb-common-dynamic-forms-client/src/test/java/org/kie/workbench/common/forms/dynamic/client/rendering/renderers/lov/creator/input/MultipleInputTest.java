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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponent;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class MultipleInputTest {

    public static final Integer PAGE_SIZE = 10;

    @Mock
    private MultipleInputComponent component;

    @Mock
    private MultipleInputView view;

    private MultipleInput input;

    @Before
    public void init() {
        input = new MultipleInput(view, component);
    }

    @Test
    public void testFunctionality() {
        verify(view).init(input);

        verify(component).setValueChangedCommand(any());

        input.asWidget();

        verify(view).asWidget();

        input.init(String.class.getName());

        verify(component).init(String.class.getName());

        assertEquals(component, input.getComponent());

        input.setPageSize(PAGE_SIZE);

        verify(component).setPageSize(PAGE_SIZE);

        input.setReadOnly(false);

        verify(component).setReadOnly(anyBoolean());

        input.getValue();

        verify(component).getValues();
    }

    @Test
    public void testSetSameComponentValues() {
        List<String> values = new ArrayList<>();

        input.setValue(values);

        verify(component, never()).setValues(values);
    }

    @Test
    public void testSetComponentValues() {
        List<String> values = Arrays.asList("a", "b", "c");

        input.setValue(values);

        verify(component).setValues(values);
    }
}
