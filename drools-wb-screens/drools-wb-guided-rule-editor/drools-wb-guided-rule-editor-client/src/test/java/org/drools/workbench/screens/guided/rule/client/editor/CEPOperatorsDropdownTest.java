/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.screens.guided.rule.client.editor;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.rule.HasParameterizedOperator;
import org.gwtbootstrap3.client.ui.ListBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class CEPOperatorsDropdownTest {

    @Mock
    HasParameterizedOperator hasParameterizedOperator;

    @Mock
    ListBox box;

    @Mock
    CEPOperatorsDropdown dropdown;

    @Before
    public void setUp() throws Exception {
        dropdown.hop = hasParameterizedOperator;
        when(dropdown.getBox()).thenReturn(box);
        doCallRealMethod().when(dropdown).addPlaceholder(anyString(),
                                                         anyString());
        doCallRealMethod().when(dropdown).insertItem(anyString(),
                                                     anyString(),
                                                     anyInt());
    }

    @Test
    public void testSetPlaceHolderNullOperator() throws Exception {
        testSetPlaceholder(null,
                           "text",
                           "value",
                           1);
    }

    @Test
    public void testSetPlaceHolderEmptyOperator() throws Exception {
        testSetPlaceholder("",
                           "text",
                           "value",
                           1);
    }

    @Test
    public void testSetPlaceHolderEqualToOperator() throws Exception {
        testSetPlaceholder("equal to",
                           "text",
                           "value",
                           0);
    }

    private void testSetPlaceholder(String operator,
                                    String placeholderText,
                                    String placeholderValue,
                                    int setSelectedIndexCount) {
        when(hasParameterizedOperator.getOperator()).thenReturn(operator);

        dropdown.addPlaceholder(placeholderText,
                                placeholderValue);
        verify(box).insertItem(placeholderText,
                               placeholderValue,
                               0);
        verify(box,
               times(setSelectedIndexCount)).setSelectedIndex(0);
    }
}
