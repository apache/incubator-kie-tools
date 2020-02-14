/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells;

import java.util.Arrays;
import java.util.Collection;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import org.gwtbootstrap3.client.ui.ListBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class AbstractProxyPopupDropDownListBoxTest {

    private final String operator;
    private final boolean isMultiSelect;

    @GwtMock
    private ListBox listBox;

    public AbstractProxyPopupDropDownListBoxTest(final String operator,
                                                 final boolean isMultiSelect) {

        this.operator = operator;
        this.isMultiSelect = isMultiSelect;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> caseSensitivity() {
        return Arrays.asList(
                new Object[][]{
                        // operator, is multiselect
                        {"==", false},
                        {"!=", false},
                        {"<", false},
                        {"<=", false},
                        {">", false},
                        {">=", false},
                        {"contains", false},
                        {"excludes", false},
                        {"memberOf", false},
                        {"matches", false},
                        {"soundslike", false},
                        {"in", true}
                }
        );
    }

    public void makeAbstractProxyPopupDropDownListBox(final String operator) {

        new AbstractProxyPopupDropDownListBox<Double>(mock(AbstractProxyPopupDropDownEditCell.class),
                                                      operator) {
            @Override
            public String convertToString(Double value) {
                return value.toString();
            }

            @Override
            public Double convertFromString(String value) {
                return new Double(value);
            }
        };
    }

    @Before
    public void setUp() {
        GwtMockito.initMocks(this);
    }

    @Test
    public void testIsMultiSelect() {
        makeAbstractProxyPopupDropDownListBox(operator);

        verify(listBox).setMultipleSelect(isMultiSelect);
    }
}