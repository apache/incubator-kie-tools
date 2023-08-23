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

package org.kie.workbench.common.dmn.client.editors.search;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.shortcuts.DataTypeShortcuts;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNDataTypesSubIndexTest {

    @Mock
    private DataTypeList dataTypeList;

    @Mock
    private DataTypeShortcuts dataTypeShortcuts;

    @Mock
    private DataTypeStore dataTypeStore;

    private DMNDataTypesSubIndex index;

    @Before
    public void setup() {
        index = spy(new DMNDataTypesSubIndex(dataTypeList, dataTypeShortcuts, dataTypeStore));
    }

    @Test
    public void testGetSearchableElements() {

        final DataTypeListItem listItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem2 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem3 = mock(DataTypeListItem.class);
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataType dataType3 = mock(DataType.class);
        final String dataTypeName1 = "data type 1";
        final String dataTypeName2 = "data type 2";
        final String dataTypeName3 = "data type 3";
        final HTMLElement htmlElement1 = mock(HTMLElement.class);
        final HTMLElement htmlElement2 = mock(HTMLElement.class);
        final HTMLElement htmlElement3 = mock(HTMLElement.class);
        final List<DataTypeListItem> dataTypeListItems = asList(listItem1, listItem2, listItem3);

        when(listItem1.getDragAndDropElement()).thenReturn(htmlElement1);
        when(listItem2.getDragAndDropElement()).thenReturn(htmlElement2);
        when(listItem3.getDragAndDropElement()).thenReturn(htmlElement3);
        when(listItem1.getDataType()).thenReturn(dataType1);
        when(listItem2.getDataType()).thenReturn(dataType2);
        when(listItem3.getDataType()).thenReturn(dataType3);
        when(dataType1.getName()).thenReturn(dataTypeName1);
        when(dataType2.getName()).thenReturn(dataTypeName2);
        when(dataType3.getName()).thenReturn(dataTypeName3);
        when(dataTypeList.getItems()).thenReturn(dataTypeListItems);

        final List<DMNSearchableElement> elements = index.getSearchableElements();
        final DMNSearchableElement element1 = elements.get(0);
        final DMNSearchableElement element2 = elements.get(1);
        final DMNSearchableElement element3 = elements.get(2);

        elements.forEach(e -> e.onFound().execute());

        assertEquals(3, elements.size());
        assertEquals(dataTypeName1, element1.getText());
        assertEquals(dataTypeName2, element2.getText());
        assertEquals(dataTypeName3, element3.getText());
        verify(index).highlight(listItem1);
        verify(index).highlight(listItem2);
        verify(index).highlight(listItem3);
    }

    @Test
    public void testOnNoResultsFound() {
        index.onNoResultsFound();
        verify(dataTypeShortcuts).reset();
    }

    @Test
    public void testHighlight() {

        final DataTypeListItem listItem = mock(DataTypeListItem.class);
        final DataTypeListItem parentListItem = mock(DataTypeListItem.class);
        final DataType dataType = mock(DataType.class);
        final DataType parent = mock(DataType.class);
        final HTMLElement htmlElement = mock(HTMLElement.class);
        final String parentUUID = "parentUUID";
        final String dataTypeUUID = "dataTypeUUID";

        when(listItem.getDataType()).thenReturn(dataType);
        when(parentListItem.getDataType()).thenReturn(parent);
        when(listItem.getDragAndDropElement()).thenReturn(htmlElement);
        when(dataType.getParentUUID()).thenReturn(parentUUID);
        when(dataType.getUUID()).thenReturn(dataTypeUUID);
        when(parent.getUUID()).thenReturn(parentUUID);
        when(dataTypeStore.get(parentUUID)).thenReturn(parent);
        when(dataTypeStore.get(dataTypeUUID)).thenReturn(dataType);

        when(dataTypeList.getItems()).thenReturn(asList(listItem, parentListItem));

        index.highlight(listItem);

        verify(listItem).expand();
        verify(parentListItem).expand();
        verify(dataTypeShortcuts).highlight(htmlElement);
    }
}
