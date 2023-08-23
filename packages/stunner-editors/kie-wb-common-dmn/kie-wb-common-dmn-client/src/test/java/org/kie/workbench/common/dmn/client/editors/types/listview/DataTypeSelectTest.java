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

package org.kie.workbench.common.dmn.client.editors.types.listview;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeUtils;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeSelectTest {

    private static final String STRUCTURE = "Structure";

    @Mock
    private DataTypeSelect.View view;

    @Mock
    private DataTypeUtils dataTypeUtils;

    @Mock
    private DataTypeManager dataTypeManager;

    @Mock
    private DataTypeListItem listItem;

    private DataTypeSelect dataTypeSelect;

    @Before
    public void setup() {
        dataTypeSelect = spy(new DataTypeSelect(view, dataTypeUtils, dataTypeManager));
        when(dataTypeManager.structure()).thenReturn(STRUCTURE);
    }

    @Test
    public void testInit() {

        final DataType dataType = mock(DataType.class);

        dataTypeSelect.init(listItem, dataType);

        assertEquals(dataType, dataTypeSelect.getDataType());
        verify(view).setDataType(dataType);
    }

    @Test
    public void testSetup() {
        dataTypeSelect.setup();

        verify(view).init(dataTypeSelect);
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

        assertThat(actualDataTypes).hasSameElementsAs(expectedDataTypes);
    }

    @Test
    public void testGetCustomDataTypes() {

        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataType dataType3 = mock(DataType.class);
        final List<DataType> customDataTypes = asList(dataType1, dataType2, dataType3);

        when(dataType1.getName()).thenReturn("tUUID");
        when(dataType2.getName()).thenReturn("tPerson");
        when(dataType3.getName()).thenReturn("tCity");
        when(dataTypeUtils.customDataTypes()).thenReturn(customDataTypes);
        doReturn(dataType2).when(dataTypeSelect).getDataType();

        final List<DataType> actualDataTypes = dataTypeSelect.getCustomDataTypes();
        final List<DataType> expectedDataTypes = asList(dataType1, dataType3);

        assertEquals(expectedDataTypes, actualDataTypes);
    }

    @Test
    public void testEnableEditMode() {
        dataTypeSelect.enableEditMode();

        verify(dataTypeSelect).refresh();
        verify(view).enableEditMode();
    }

    @Test
    public void testDisableEditMode() {
        dataTypeSelect.disableEditMode();

        verify(view).disableEditMode();
    }

    @Test
    public void testRefresh() {
        dataTypeSelect.refresh();

        verify(view).setupDropdown();
    }

    @Test
    public void testGetValue() {

        final String expectedValue = "typeName";

        when(view.getValue()).thenReturn(expectedValue);

        final String actualValue = dataTypeSelect.getValue();

        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testClearDataTypesList() {

        final DataType parent = mock(DataType.class);
        doReturn(parent).when(dataTypeSelect).getDataType();
        when(dataTypeManager.from(parent)).thenReturn(dataTypeManager);

        dataTypeSelect.init(listItem, parent);
        dataTypeSelect.clearDataTypesList();

        verify(listItem).cleanSubDataTypes();
        verify(listItem).refreshConstraintComponent();
    }

    @Test
    public void testStructure() {
        assertEquals(STRUCTURE, dataTypeSelect.structure());
    }
}
