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
package org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl;

import java.util.stream.IntStream;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.ListBox;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class BaseColumnConverterUtilitiesTest {

    @GwtMock
    private ListBox listBox;

    @Mock
    private GridCell<String> cell;

    @Mock
    private GridCellValue<String> cellValue;

    @Test
    public void checkToWidgetNoValue() {
        BaseColumnConverterUtilities.toWidget(false,
                                              cell,
                                              listBox);

        verify(listBox,
               never()).addItem(anyString());
        verify(listBox,
               never()).addItem(anyString(),
                                anyString());
    }

    @Test
    public void checkToWidgetNoValueValue() {
        when(cell.getValue()).thenReturn(cellValue);

        BaseColumnConverterUtilities.toWidget(false,
                                              cell,
                                              listBox);

        verify(listBox,
               never()).addItem(anyString());
        verify(listBox,
               never()).addItem(anyString(),
                                anyString());
    }

    @Test
    public void checkToWidgetNoValueWithListBoxValues() {
        when(listBox.getItemCount()).thenReturn(1);

        BaseColumnConverterUtilities.toWidget(false,
                                              cell,
                                              listBox);

        verify(listBox).setSelectedIndex(eq(0));
    }

    @Test
    public void checkToWidgetWithValueWithSingleSelectListBoxWithValues() {
        when(cell.getValue()).thenReturn(cellValue);
        when(cellValue.getValue()).thenReturn("value1");

        checkToWidget(new String[]{"value0", "value1"},
                      new ToWidget[]{
                              new ToWidget(1,
                                           true)
                      },
                      false);
    }

    @Test
    public void checkToWidgetWithValueWithMultipleSelectSingleValueListBoxWithValues() {
        when(cell.getValue()).thenReturn(cellValue);
        when(cellValue.getValue()).thenReturn("value1");

        checkToWidget(new String[]{"value0", "value1"},
                      new ToWidget[]{
                              new ToWidget(0,
                                           false),
                              new ToWidget(1,
                                           true)
                      },
                      true);
    }

    @Test
    public void checkToWidgetWithValueWithMultipleSelectMultipleValuesListBoxWithValues() {
        when(cell.getValue()).thenReturn(cellValue);
        when(cellValue.getValue()).thenReturn("value0,value1");

        checkToWidget(new String[]{"value0", "value1"},
                      new ToWidget[]{
                              new ToWidget(0,
                                           true),
                              new ToWidget(1,
                                           true)
                      },
                      true);
    }

    @Test
    public void checkFromWidgetWhenSingleSelect() {
        when(listBox.getSelectedIndex()).thenReturn(0);
        checkFromWidget(new FromWidget[]{
                                new FromWidget("value",
                                               true)
                        },
                        "value",
                        false);
    }

    @Test
    public void checkFromWidgetWhenMultipleSelectWithSingleValueSelected() {
        checkFromWidget(new FromWidget[]{
                                new FromWidget("value0",
                                               true),
                                new FromWidget("value1",
                                               false)
                        },
                        "value0",
                        true);
    }

    @Test
    public void checkFromWidgetWhenMultipleSelectWithMultipleValuesSelected() {
        checkFromWidget(new FromWidget[]{
                                new FromWidget("value0",
                                               true),
                                new FromWidget("value1",
                                               true)
                        },
                        "value0,value1",
                        true);
    }

    @Test
    public void checkFromWidgetWhenMultipleSelectWithNoValuesSelected() {
        checkFromWidget(new FromWidget[]{
                                new FromWidget("value0",
                                               false),
                                new FromWidget("value1",
                                               false)
                        },
                        "",
                        true);
    }

    private void checkToWidget(final String[] items,
                               final ToWidget[] expected,
                               final boolean isMultipleSelect) {
        when(listBox.getItemCount()).thenReturn(items.length);
        IntStream.range(0,
                        items.length).forEach(i -> when(listBox.getValue(eq(i))).thenReturn(items[i]));

        BaseColumnConverterUtilities.toWidget(isMultipleSelect,
                                              cell,
                                              listBox);

        if (expected.length == 0) {
            fail("There are no assertions to be made!");
        } else if (expected.length == 1) {
            verify(listBox).setSelectedIndex(eq(expected[0].index));
        } else {
            IntStream.range(0,
                            expected.length).forEach(i -> verify(listBox).setItemSelected(eq(expected[i].index),
                                                                                          eq(expected[i].selected)));
        }
    }

    private void checkFromWidget(final FromWidget[] items,
                                 final String expected,
                                 final boolean isMultipleSelect) {
        when(listBox.getItemCount()).thenReturn(items.length);
        IntStream.range(0,
                        items.length).forEach(i -> {
            when(listBox.getValue(eq(i))).thenReturn(items[i].value);
            when(listBox.isItemSelected(eq(i))).thenReturn(items[i].selected);
        });

        assertEquals(expected,
                     BaseColumnConverterUtilities.fromWidget(isMultipleSelect,
                                                             listBox));
    }

    private interface Pair<V1, V2> {

    }

    private static class ToWidget implements Pair<Integer, Boolean> {

        final Integer index;
        final Boolean selected;

        ToWidget(final Integer index,
                 final Boolean selected) {
            this.index = index;
            this.selected = selected;
        }
    }

    private static class FromWidget implements Pair<String, Boolean> {

        final String value;
        final Boolean selected;

        FromWidget(final String value,
                   final Boolean selected) {
            this.value = value;
            this.selected = selected;
        }
    }
}
