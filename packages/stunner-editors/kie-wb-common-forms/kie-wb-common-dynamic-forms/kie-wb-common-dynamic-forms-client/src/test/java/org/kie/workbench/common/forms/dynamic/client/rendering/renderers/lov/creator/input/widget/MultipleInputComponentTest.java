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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class MultipleInputComponentTest {

    public static final Integer PAGE_SIZE = 10;

    @Mock
    private MultipleInputComponentView view;

    @Mock
    private EditableColumnGeneratorManager editableColumnGeneratorManager;

    @Mock
    private EditableColumnGenerator columnGenerator;

    @Mock
    private HasData displayer;

    @Mock
    private Command valueChangedCommand;

    private TestMultipleInputComponent component;

    @Before
    public void init() {
        doAnswer((Answer<Void>) invocationOnMock -> {
            component.getProvider().addDataDisplay(displayer);
            return null;
        }).when(view).render();

        when(displayer.getVisibleRange()).thenAnswer((Answer<Range>) invocation -> new Range(0, 0));

        when(editableColumnGeneratorManager.getGenerator(Mockito.<String>any())).thenReturn(columnGenerator);

        component = spy(new TestMultipleInputComponent(view, editableColumnGeneratorManager));

        component.init();

        component.init(String.class.getName());

        component.setValueChangedCommand(valueChangedCommand);

        component.setPageSize(PAGE_SIZE);

        when(view.getPageSize()).thenReturn(PAGE_SIZE);

        testLifeCycle();
    }

    public void testLifeCycle() {

        verify(view).init(any());

        verify(component).doInit();

        verify(view).render();

        verify(component).refresh(MultipleInputComponent.Action.REFRESH);

        component.getElement();

        verify(view).getElement();

        component.setReadOnly(true);
        verify(view).setReadOnly(true);

        component.setReadOnly(false);
        verify(view).setReadOnly(false);
    }

    @Test
    public void testAddElements() {
        component.newElement();

        int expectedSize = 1;

        verify(component).refresh(MultipleInputComponent.Action.ADD);

        verify(valueChangedCommand).execute();

        Assertions.assertThat(component.getValues())
                .isNotNull()
                .isNotEmpty()
                .hasSize(expectedSize);

        component.newElement();

        expectedSize ++;

        verify(component, times(expectedSize)).refresh(MultipleInputComponent.Action.ADD);

        verify(valueChangedCommand, times(expectedSize)).execute();

        Assertions.assertThat(component.getValues())
                .isNotNull()
                .isNotEmpty()
                .hasSize(expectedSize);

        component.newElement();

        expectedSize ++;

        verify(component, times(expectedSize)).refresh(MultipleInputComponent.Action.ADD);

        verify(valueChangedCommand, times(expectedSize)).execute();

        Assertions.assertThat(component.getValues())
                .isNotNull()
                .isNotEmpty()
                .hasSize(expectedSize);
    }

    @Test
    public void testSelectFirstElement() {
        testAddElements();

        TableEntry entry = component.getTableValues().get(0);

        testSelectElement(entry, false, true);
    }

    @Test
    public void testSelectSecondElement() {
        testAddElements();

        TableEntry entry = component.getTableValues().get(1);

        testSelectElement(entry, true, true);
    }

    @Test
    public void testSelectThirdElement() {
        testAddElements();

        TableEntry entry = component.getTableValues().get(2);

        testSelectElement(entry, true, false);
    }

    protected void testSelectElement(TableEntry entry, boolean promote, boolean degrade) {
        assertNotNull(entry);

        component.selectValue(entry);

        Assertions.assertThat(component.getSelectedValues())
                .isNotNull()
                .isNotEmpty()
                .contains(entry);

        assertTrue(component.isSelected(entry));

        verify(view).enableRemoveButton(true);

        verify(component).maybeEnablePromote();
        verify(view).enablePromoteButton(promote);

        verify(component).maybeEnableDegrade();
        verify(view).enableDegradeButton(degrade);
    }

    @Test
    public void testDeSelectFirstElement() {
        testSelectFirstElement();

        TableEntry entry = component.getTableValues().get(0);

        assertNotNull(entry);

        component.selectValue(entry);

        Assertions.assertThat(component.getSelectedValues())
                .isNotNull()
                .isEmpty();

        verify(view).enableRemoveButton(false);

        verify(component, times(2)).maybeEnablePromote();
        verify(view, times(2)).enablePromoteButton(false);

        verify(component, times(2)).maybeEnableDegrade();
        verify(view).enableDegradeButton(false);
    }

    @Test
    public void testRemoveSelectedElement() {
        testSelectFirstElement();

        component.removeSelectedValues();

        verify(view).enableRemoveButton(false);

        verify(component).refresh(MultipleInputComponent.Action.REMOVE);
        verify(component, times(2)).maybeEnablePromote();
        verify(view, times(2)).enablePromoteButton(false);

        verify(component, times(2)).maybeEnableDegrade();
        verify(view).enableDegradeButton(false);

        verify(component, times(4)).notifyValueChanged();

        verify(valueChangedCommand, times(4)).execute();

        Assertions.assertThat(component.getSelectedValues())
                .isNotNull()
                .isEmpty();
    }

    @Test
    public void testDegradeFirstElement() {
        testSelectFirstElement();

        TableEntry entry = getSelectedEntry();

        component.degradeSelectedValues();

        assertEquals(entry, component.getTableValues().get(1));

        verify(component, times(2)).refresh(MultipleInputComponent.Action.REFRESH);
        verify(component, times(2)).maybeEnablePromote();
        verify(view).enablePromoteButton(true);

        verify(component, times(2)).maybeEnableDegrade();
        verify(view, times(2)).enableDegradeButton(true);

        verify(component, times(4)).notifyValueChanged();

        verify(valueChangedCommand, times(4)).execute();

        Assertions.assertThat(component.getSelectedValues())
                .isNotNull()
                .isNotEmpty()
                .contains(entry);
    }

    @Test
    public void testDegradeSecondElement() {
        testSelectSecondElement();

        TableEntry entry = getSelectedEntry();

        component.degradeSelectedValues();

        assertEquals(entry, component.getTableValues().get(2));

        verify(component, times(2)).refresh(MultipleInputComponent.Action.REFRESH);
        verify(component, times(2)).maybeEnablePromote();
        verify(view, times(2)).enablePromoteButton(true);

        verify(component, times(2)).maybeEnableDegrade();
        verify(view, times(1)).enableDegradeButton(true);

        verify(component, times(4)).notifyValueChanged();

        verify(valueChangedCommand, times(4)).execute();

        Assertions.assertThat(component.getSelectedValues())
                .isNotNull()
                .isNotEmpty()
                .contains(entry);
    }

    @Test
    public void testPromoteSecondElement() {
        testSelectSecondElement();

        TableEntry entry = getSelectedEntry();

        component.promoteSelectedValues();

        assertEquals(entry, component.getTableValues().get(0));

        verify(component, times(2)).refresh(MultipleInputComponent.Action.REFRESH);
        verify(component, times(2)).maybeEnablePromote();
        verify(view, times(1)).enablePromoteButton(false);

        verify(component, times(2)).maybeEnableDegrade();
        verify(view, times(2)).enableDegradeButton(true);

        verify(component, times(4)).notifyValueChanged();

        verify(valueChangedCommand, times(4)).execute();

        Assertions.assertThat(component.getSelectedValues())
                .isNotNull()
                .isNotEmpty()
                .contains(entry);
    }

    @Test
    public void testPromoteThirdElement() {
        testSelectThirdElement();

        TableEntry entry = getSelectedEntry();

        component.promoteSelectedValues();

        assertEquals(entry, component.getTableValues().get(1));

        verify(component, times(2)).refresh(MultipleInputComponent.Action.REFRESH);
        verify(component, times(2)).maybeEnablePromote();
        verify(view, times(2)).enablePromoteButton(true);

        verify(component, times(2)).maybeEnableDegrade();
        verify(view, times(1)).enableDegradeButton(true);

        verify(component, times(4)).notifyValueChanged();

        verify(valueChangedCommand, times(4)).execute();

        Assertions.assertThat(component.getSelectedValues())
                .isNotNull()
                .isNotEmpty()
                .contains(entry);
    }

    protected TableEntry getSelectedEntry() {
        return component.getSelectedValues().get(0);
    }

    @Test
    public void testNotifyValueChange() {
        testAddElements();

        String value = "hey!";

        component.notifyChange(0, value);

        assertEquals(value, component.getTableValues().get(0).getValue());

        verify(component, times(2)).refresh(MultipleInputComponent.Action.REFRESH);

        verify(valueChangedCommand, times(4)).execute();
    }

    @Test
    public void testSetValues() {
        List<String> values = Arrays.asList("a",
                                            "b",
                                            "c");

        component.setValues(values);

        Assertions.assertThat(component.getSelectedValues())
                .isEmpty();
        Assertions.assertThat(component.getTableValues())
                .isNotEmpty()
                .hasSize(values.size());
        Assertions.assertThat(component.getValues())
                .isNotEmpty()
                .containsAll(values);

        verify(component, times(2)).doInit();
        verify(component, times(2)).init(Mockito.<String>any());

    }
}
