/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.widget;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.ListBox;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.DropDownData;
import org.uberfire.backend.vfs.Path;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class EnumDropDownUtilitiesTest {

    @Test
    public void testCommaSeparatedValuesWithBracketsAndMultiValue() {
        final DropDownData downData = mock(DropDownData.class);
        doReturn(new String[]{"a", "\"b, c\"", "d"}).when(downData).getFixedList();

        final ListBox listBox = mock(ListBox.class);

        new EnumDropDownUtilities().setDropDownData(" ( \"a\",\"\"b, c\"\",\"d\" )",
                                                    downData,
                                                    true,
                                                    mock(Path.class),
                                                    listBox);

        verify(listBox).clear();

        verify(listBox).addItem("a");
        verify(listBox).addItem("\"b, c\"");
        verify(listBox).addItem("d");

        verify(listBox).setItemSelected(0, true);
        verify(listBox).setItemSelected(1, true);
        verify(listBox).setItemSelected(2, true);
    }

    @Test
    public void testCommaSeparatedValuesWithBrackets() {
        final DropDownData downData = mock(DropDownData.class);
        doReturn(new String[]{"a", "\"b, c\"", "d"}).when(downData).getFixedList();

        final ListBox listBox = mock(ListBox.class);

        new EnumDropDownUtilities().setDropDownData(" ( \"a\",\"d\" )",
                                                    downData,
                                                    true,
                                                    mock(Path.class),
                                                    listBox);

        verify(listBox).clear();

        verify(listBox).addItem("a");
        verify(listBox).addItem("\"b, c\"");
        verify(listBox).addItem("d");

        verify(listBox).setItemSelected(0, true);
        verify(listBox, never()).setItemSelected(1, true);
        verify(listBox).setItemSelected(2, true);
    }

    @Test
    public void testCommaSeparatedValuesWithBracketsWhereLastItemIsMultiValue() {
        final DropDownData downData = mock(DropDownData.class);
        doReturn(new String[]{"a", "\"b, c\"", "d"}).when(downData).getFixedList();

        final ListBox listBox = mock(ListBox.class);

        new EnumDropDownUtilities().setDropDownData(" ( \"a\",\"\"b, c\"\" )",
                                                    downData,
                                                    true,
                                                    mock(Path.class),
                                                    listBox);

        verify(listBox).clear();

        verify(listBox).addItem("a");
        verify(listBox).addItem("\"b, c\"");
        verify(listBox).addItem("d");

        verify(listBox).setItemSelected(0, true);
        verify(listBox).setItemSelected(1, true);
        verify(listBox, never()).setItemSelected(2, true);
    }

    @Test
    public void testCommaSeparatedValuesWithBracketsWhereFirstItemIsMultiValue() {
        final DropDownData downData = mock(DropDownData.class);
        doReturn(new String[]{"a", "\"b, c\"", "d"}).when(downData).getFixedList();

        final ListBox listBox = mock(ListBox.class);

        new EnumDropDownUtilities().setDropDownData(" ( \"\"b, c\"\",\"d\" )",
                                                    downData,
                                                    true,
                                                    mock(Path.class),
                                                    listBox);

        verify(listBox).clear();

        verify(listBox).addItem("a");
        verify(listBox).addItem("\"b, c\"");
        verify(listBox).addItem("d");

        verify(listBox, never()).setItemSelected(0, true);
        verify(listBox).setItemSelected(1, true);
        verify(listBox).setItemSelected(2, true);
    }

    @Test
    public void testCommaSeparatedValuesWithBracketsAndOneMultiValue() {
        final DropDownData downData = mock(DropDownData.class);
        doReturn(new String[]{"a", "\"b, c\"", "d"}).when(downData).getFixedList();

        final ListBox listBox = mock(ListBox.class);

        new EnumDropDownUtilities().setDropDownData(" ( \"\"b, c\"\" )",
                                                    downData,
                                                    true,
                                                    mock(Path.class),
                                                    listBox);

        verify(listBox).clear();

        verify(listBox).addItem("a");
        verify(listBox).addItem("\"b, c\"");
        verify(listBox).addItem("d");

        verify(listBox, never()).setItemSelected(0, true);
        verify(listBox).setItemSelected(1, true);
        verify(listBox, never()).setItemSelected(2, true);
    }
}