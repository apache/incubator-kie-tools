/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.DataTypeConstraintEnumeration;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItem.NULL;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doNothing;
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
    private DataTypeConstraintEnumeration dataTypeConstraintEnumeration;

    private DataTypeConstraintEnumerationItem enumerationItem;

    @Before
    public void setup() {
        enumerationItem = spy(new DataTypeConstraintEnumerationItem(view));
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
    }

    @Test
    public void testSave() {

        final String value = "123";

        enumerationItem.save(value);

        verify(enumerationItem).setValue(value);
        verify(dataTypeConstraintEnumeration).refreshView();
    }

    @Test
    public void testMoveUp() {
        doNothing().when(enumerationItem).moveEnumerationItem(anyInt());
        enumerationItem.moveUp();
        verify(enumerationItem).moveEnumerationItem(-1);
    }

    @Test
    public void testMoveDown() {
        doNothing().when(enumerationItem).moveEnumerationItem(anyInt());
        enumerationItem.moveDown();
        verify(enumerationItem).moveEnumerationItem(1);
    }

    @Test
    public void testMoveEnumerationItemWhenReferenceValueIsOneNegative() {

        final DataTypeConstraintEnumerationItem item1 = mock(DataTypeConstraintEnumerationItem.class);
        final DataTypeConstraintEnumerationItem item2 = mock(DataTypeConstraintEnumerationItem.class);
        final List<DataTypeConstraintEnumerationItem> enumerationItems = asList(item1, enumerationItem, item2);

        when(dataTypeConstraintEnumeration.getEnumerationItems()).thenReturn(enumerationItems);

        enumerationItem.moveEnumerationItem(-1);

        verify(dataTypeConstraintEnumeration).refreshView();
        assertEquals(asList(enumerationItem, item1, item2), enumerationItems);
    }

    @Test
    public void testMoveEnumerationItemWhenReferenceValueIsOnePositive() {

        final DataTypeConstraintEnumerationItem item1 = mock(DataTypeConstraintEnumerationItem.class);
        final DataTypeConstraintEnumerationItem item2 = mock(DataTypeConstraintEnumerationItem.class);
        final List<DataTypeConstraintEnumerationItem> enumerationItems = asList(item1, enumerationItem, item2);

        when(dataTypeConstraintEnumeration.getEnumerationItems()).thenReturn(enumerationItems);

        enumerationItem.moveEnumerationItem(1);

        verify(dataTypeConstraintEnumeration).refreshView();
        assertEquals(asList(item1, item2, enumerationItem), enumerationItems);
    }

    @Test
    public void testMoveEnumerationItemWhenReferenceValueIsOnePositiveAndTheElementIsInTheEndOfTheList() {

        final DataTypeConstraintEnumerationItem item1 = mock(DataTypeConstraintEnumerationItem.class);
        final DataTypeConstraintEnumerationItem item2 = mock(DataTypeConstraintEnumerationItem.class);
        final List<DataTypeConstraintEnumerationItem> enumerationItems = asList(item1, item2, enumerationItem);

        when(dataTypeConstraintEnumeration.getEnumerationItems()).thenReturn(enumerationItems);

        enumerationItem.moveEnumerationItem(1);

        verify(dataTypeConstraintEnumeration).refreshView();
        assertEquals(asList(enumerationItem, item2, item1), enumerationItems);
    }

    @Test
    public void testMoveEnumerationItemWhenReferenceValueIsOneNegativeAndTheElementIsInTheBeginningOfTheList() {

        final DataTypeConstraintEnumerationItem item1 = mock(DataTypeConstraintEnumerationItem.class);
        final DataTypeConstraintEnumerationItem item2 = mock(DataTypeConstraintEnumerationItem.class);
        final List<DataTypeConstraintEnumerationItem> enumerationItems = asList(enumerationItem, item1, item2);

        when(dataTypeConstraintEnumeration.getEnumerationItems()).thenReturn(enumerationItems);

        enumerationItem.moveEnumerationItem(-1);

        verify(dataTypeConstraintEnumeration).refreshView();
        assertEquals(asList(item2, item1, enumerationItem), enumerationItems);
    }

    @Test
    public void testRemove() {

        final List<DataTypeConstraintEnumerationItem> enumerationItems = spy(new ArrayList<>());

        when(dataTypeConstraintEnumeration.getEnumerationItems()).thenReturn(enumerationItems);

        enumerationItem.remove();

        verify(enumerationItems).remove(enumerationItem);
        verify(dataTypeConstraintEnumeration).refreshView();
    }
}
