/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types.treegrid;

import java.util.ArrayList;
import java.util.List;

import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataTypeSelectTest {

    @Mock
    private DataTypeSelect.View view;

    @Mock
    private DataTypeUtils dataTypeUtils;

    private DataTypeSelect dataTypeSelect;

    @Before
    public void setup() {
        dataTypeSelect = new DataTypeSelect(view, dataTypeUtils);
    }

    @Test
    public void testInit() {

        final DataType dataType = mock(DataType.class);

        dataTypeSelect.init(dataType);

        assertEquals(dataType, dataTypeSelect.getDataType());
        verify(view).setDataType(dataType);
    }

    @Test
    public void testGetElement() {

        final HTMLElement expected = mock(HTMLElement.class);
        when(view.getElement()).thenReturn(expected);

        final HTMLElement actual = dataTypeSelect.getElement();

        assertEquals(actual, expected);
    }

    @Test
    public void testGetDefaultDataTypes() {

        final List<DataType> expectedDataTypes = new ArrayList<DataType>() {{
            add(mock(DataType.class));
        }};

        when(dataTypeUtils.defaultDataTypes()).thenReturn(expectedDataTypes);

        final List<DataType> actualDataTypes = dataTypeSelect.getDefaultDataTypes();

        assertEquals(actualDataTypes, expectedDataTypes);
    }

    @Test
    public void testGetCustomDataTypes() {

        final List<DataType> expectedDataTypes = new ArrayList<DataType>() {{
            add(mock(DataType.class));
        }};

        when(dataTypeUtils.customDataTypes()).thenReturn(expectedDataTypes);

        final List<DataType> actualDataTypes = dataTypeSelect.getCustomDataTypes();

        assertEquals(actualDataTypes, expectedDataTypes);
    }
}
