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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.ConstraintPlaceholderHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.DataTypeConstraintEnumeration;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItem.NULL;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeConstraintEnumerationItemTest {

    @Mock
    private DataTypeConstraintEnumerationItem.View view;

    @Mock
    private ConstraintPlaceholderHelper placeholderHelper;

    @Mock
    private DataTypeConstraintEnumeration dataTypeConstraintEnumeration;

    private DataTypeConstraintEnumerationItem enumerationItem;

    @Before
    public void setup() {
        enumerationItem = spy(new DataTypeConstraintEnumerationItem(view, placeholderHelper));
        enumerationItem.setDataTypeConstraintEnumeration(dataTypeConstraintEnumeration);
    }

    @Test
    public void testSetup() {
        enumerationItem.setup();
        verify(view).init(enumerationItem);
    }

    @Test
    public void testSetValue() {

        final String value = "123";

        enumerationItem.setValue(value);

        verify(view).setValue(value);
        assertEquals(value, enumerationItem.getValue());
    }

    @Test
    public void testSetValueWhenValueIsEmpty() {

        final String value = "";

        enumerationItem.setValue(value);

        verify(view).setValue(NULL);
        assertEquals(NULL, enumerationItem.getValue());
    }

    @Test
    public void testGetElement() {

        final HTMLElement expectedElement = mock(HTMLElement.class);

        when(view.getElement()).thenReturn(expectedElement);

        final Element actualElement = enumerationItem.getElement();

        assertEquals(expectedElement, actualElement);
    }

    @Test
    public void testEnableEditMode() {

        final String value = "123";

        doReturn(value).when(enumerationItem).getValue();

        enumerationItem.enableEditMode();

        verify(enumerationItem).setOldValue(value);
        verify(view).showValueInput();
        verify(view).focusValueInput();
        verify(view).enableHighlight();
        verify(view).showSaveButton();
        verify(view).showClearButton();
        verify(view).hideDeleteButton();
    }

    @Test
    public void testDiscardEditMode() {

        final String value = "123";

        doReturn(value).when(enumerationItem).getOldValue();

        enumerationItem.discardEditMode();

        verify(enumerationItem).setValue(value);
        verify(enumerationItem).disableEditMode();
    }

    @Test
    public void testDisableEditMode() {
        enumerationItem.disableEditMode();

        verify(view).showValueText();
        verify(view).disableHighlight();
        verify(view).hideSaveButton();
        verify(view).hideClearButton();
        verify(view).showDeleteButton();
    }

    @Test
    public void testSave() {

        final String value = "123";

        enumerationItem.save(value);

        final String actual = enumerationItem.getValue();
        final String expected = "123";

        assertEquals(expected, actual);
        verify(enumerationItem).disableEditMode();
    }

    @Test
    public void testSaveWhenTheValueIsBlank() {

        final String value = "";

        enumerationItem.save(value);

        final String actual = enumerationItem.getValue();
        final String expected = NULL;

        assertEquals(expected, actual);
        verify(enumerationItem).disableEditMode();
    }

    @Test
    public void testGetScrollToThisItemCallback() {

        final int order = 42;
        when(enumerationItem.getOrder()).thenReturn(order);

        enumerationItem.getScrollToThisItemCallback().execute();

        verify(dataTypeConstraintEnumeration).scrollToPosition(order);
    }

    @Test
    public void testRemove() {

        final List<DataTypeConstraintEnumerationItem> enumerationItems = spy(new ArrayList<>());

        when(dataTypeConstraintEnumeration.getEnumerationItems()).thenReturn(enumerationItems);

        enumerationItem.remove();

        verify(enumerationItems).remove(enumerationItem);
        verify(dataTypeConstraintEnumeration).refreshView();
    }

    @Test
    public void testSetConstraintValueType() {

        final String type = "string";
        final String placeholder = "placeholder";

        when(placeholderHelper.getPlaceholderSample(type)).thenReturn(placeholder);

        enumerationItem.setConstraintValueType(type);

        verify(view).setPlaceholder(placeholder);
        verify(view).setComponentSelector(type);
    }

    @Test
    public void testGetOrder() {

        final int expected = 1;

        when(view.getOrder()).thenReturn(1);

        final int actual = enumerationItem.getOrder();

        assertEquals(expected, actual);
    }
}
