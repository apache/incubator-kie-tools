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

package org.kie.workbench.common.dmn.client.editors.types.listview.tooltip;

import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeKind;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeUtils;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class StructureTypesTooltipTest {

    @Mock
    private StructureTypesTooltip.View view;

    @Mock
    private DataTypeUtils dataTypeUtils;

    @Mock
    private DataTypeList dataTypeList;

    @Mock
    private DataTypeManager dataTypeManager;

    private StructureTypesTooltip presenter;

    @Before
    public void setup() {
        presenter = spy(new StructureTypesTooltip(view, dataTypeUtils, dataTypeList, dataTypeManager));
    }

    @Test
    public void testSetup() {
        presenter.setup();
        verify(view).init(presenter);
    }

    @Test
    public void testShow() {
        final HTMLElement refElement = mock(HTMLElement.class);
        final String typeName = "string";

        presenter.show(refElement, typeName);

        verify(view).show(refElement);
        assertEquals(typeName, presenter.getTypeName());
    }

    @Test
    public void testGetListItems() {
        final HTMLElement expected = mock(HTMLElement.class);
        when(dataTypeList.getListItems()).thenReturn(expected);

        final HTMLElement actual = presenter.getListItems();

        assertEquals(expected, actual);
    }

    @Test
    public void testGetTypeFields() {
        final String typeName = "tPerson";
        final DataType tPerson = mock(DataType.class);
        final DataType name = mock(DataType.class);
        final DataType age = mock(DataType.class);

        doReturn(typeName).when(presenter).getTypeName();
        when(tPerson.getSubDataTypes()).thenReturn(asList(name, age));
        when(dataTypeManager.getTopLevelDataTypeWithName(typeName)).thenReturn(Optional.of(tPerson));

        final List<DataType> typeFields = presenter.getTypeFields();

        assertEquals(2, typeFields.size());
        assertTrue(typeFields.contains(name));
        assertTrue(typeFields.contains(age));
    }

    @Test
    public void testGoToDataType() {

        final String typeName = "tPerson";
        final HTMLElement refElement = mock(HTMLElement.class);
        final DataType dataType = mock(DataType.class);
        final Optional<DataType> optDataType = Optional.of(dataType);
        final DataTypeListItem dataTypeListItem = mock(DataTypeListItem.class);
        final Optional<DataTypeListItem> optDataTypeListItem = Optional.of(dataTypeListItem);

        when(dataTypeManager.getTopLevelDataTypeWithName(typeName)).thenReturn(optDataType);
        when(dataTypeList.findItem(dataType)).thenReturn(optDataTypeListItem);

        presenter.show(refElement, typeName);
        presenter.goToDataType();

        verify(dataTypeListItem).enableShortcutsHighlight();
    }

    @Test
    public void testGetDataTypeKind() {
        final DataTypeKind expectedKind = DataTypeKind.CUSTOM;
        when(dataTypeUtils.getDataTypeKind(any())).thenReturn(expectedKind);

        final DataTypeKind actualKind = presenter.getDataTypeKind();

        assertEquals(expectedKind, actualKind);
    }
}
