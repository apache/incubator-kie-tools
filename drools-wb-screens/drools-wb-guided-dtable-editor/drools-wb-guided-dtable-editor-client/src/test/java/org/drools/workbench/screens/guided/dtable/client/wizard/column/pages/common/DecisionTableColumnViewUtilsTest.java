/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTableColumnViewUtils;
import org.gwtbootstrap3.client.ui.ListBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableColumnViewUtilsTest {

    private static final String SECOND_OPTION = "second option";
    private static final String FIRST_OPTION = "first option";
    private static final String PLEASE_CHOOSE = "please choose";

    @Mock
    ListBox listBox;

    @Before
    public void setUp() throws Exception {
        when(listBox.getItemCount()).thenReturn(3);
        when(listBox.getValue(0)).thenReturn(PLEASE_CHOOSE);
        when(listBox.getValue(1)).thenReturn(FIRST_OPTION);
        when(listBox.getValue(2)).thenReturn(SECOND_OPTION);
    }

    @Test
    public void testGetIndexWithoutDefaultSelectNull() throws Exception {
        assertEquals(-1,
                     DecisionTableColumnViewUtils.getCurrentIndexFromListWithoutDefaultSelect(null,
                                                                                              listBox));
    }

    @Test
    public void testGetIndexWithoutDefaultSelectEmpty() throws Exception {
        assertEquals(-1,
                     DecisionTableColumnViewUtils.getCurrentIndexFromListWithoutDefaultSelect("",
                                                                                              listBox));
    }

    @Test
    public void testGetIndexWithoutDefaultSelectPlaceholder() throws Exception {
        assertEquals(0,
                     DecisionTableColumnViewUtils.getCurrentIndexFromListWithoutDefaultSelect(PLEASE_CHOOSE,
                                                                                              listBox));
    }

    @Test
    public void testGetIndexWithoutDefaultSelectFirstOption() throws Exception {
        assertEquals(1,
                     DecisionTableColumnViewUtils.getCurrentIndexFromListWithoutDefaultSelect(FIRST_OPTION,
                                                                                              listBox));
    }

    @Test
    public void testGetIndexWithoutDefaultSelectSecondOption() throws Exception {
        assertEquals(2,
                     DecisionTableColumnViewUtils.getCurrentIndexFromListWithoutDefaultSelect(SECOND_OPTION,
                                                                                              listBox));
    }

    @Test
    public void testGetIndexWithDefaultSelectNull() throws Exception {
        assertEquals(0,
                     DecisionTableColumnViewUtils.getCurrentIndexFromList(null,
                                                                          listBox));
    }

    @Test
    public void testGetIndexWithDefaultSelectEmpty() throws Exception {
        assertEquals(0,
                     DecisionTableColumnViewUtils.getCurrentIndexFromList("",
                                                                          listBox));
    }

    @Test
    public void testGetIndexWithDefaultSelectPlaceholder() throws Exception {
        assertEquals(0,
                     DecisionTableColumnViewUtils.getCurrentIndexFromList(PLEASE_CHOOSE,
                                                                          listBox));
    }

    @Test
    public void testGetIndexWithDefaultSelectFirstOption() throws Exception {
        assertEquals(1,
                     DecisionTableColumnViewUtils.getCurrentIndexFromList(FIRST_OPTION,
                                                                          listBox));
    }

    @Test
    public void testGetIndexWithDefaultSelectSecondOption() throws Exception {
        assertEquals(2,
                     DecisionTableColumnViewUtils.getCurrentIndexFromList(SECOND_OPTION,
                                                                          listBox));
    }
}
