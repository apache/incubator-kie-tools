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

package org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop;

import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import elemental2.dom.NodeList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.UUID_ATTR;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandlerShiftStrategy.INSERT_INTO_HOVERED_DATA_TYPE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandlerShiftStrategy.INSERT_NESTED_DATA_TYPE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandlerShiftStrategy.INSERT_SIBLING_DATA_TYPE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandlerShiftStrategy.INSERT_TOP_LEVEL_DATA_TYPE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandlerShiftStrategy.INSERT_TOP_LEVEL_DATA_TYPE_AT_THE_TOP;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.DATA_X_POSITION;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.DATA_Y_POSITION;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.DRAGGING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DNDDataTypesHandlerContextTest {

    @Mock
    private DataTypeStore dataTypeStore;

    @Mock
    private DNDListComponent dndListComponent;

    @Mock
    private DNDDataTypesHandler dndDataTypesHandler;

    @Mock
    private Element currentElement;

    @Mock
    private Element hoverElement;

    private DNDDataTypesHandlerContext context;

    @Before
    public void setup() {

        when(dndDataTypesHandler.getDataTypeStore()).thenReturn(dataTypeStore);
        when(dndDataTypesHandler.getDndListComponent()).thenReturn(dndListComponent);

        context = new DNDDataTypesHandlerContext(dndDataTypesHandler, currentElement, hoverElement);
    }

    @Test
    public void testGetReferenceWhenHoveredDataTypeIsPresent() {

        final Optional<DataType> hoverDataType = Optional.of(mock(DataType.class));
        final String uuid = "0000-1111-2222-3333";

        when(hoverElement.getAttribute(UUID_ATTR)).thenReturn(uuid);
        when(dataTypeStore.get(uuid)).thenReturn(hoverDataType.get());

        final Optional<DataType> reference = context.getReference();

        assertEquals(hoverDataType, reference);
    }

    @Test
    public void testGetReferenceWhenHoveredDataTypeIsPresentButHoveredDataTypeIsReadOnly() {

        final Element previousElement = mock(Element.class);
        final Optional<DataType> hoverDataType = Optional.of(mock(DataType.class));
        final Optional<DataType> previousDataType = Optional.of(mock(DataType.class));
        final String hoverUUID = "0000-0000-0000-0000";
        final String previousUUID = "1111-1111-1111-1111";

        when(dndListComponent.getPreviousElement(any(), any())).thenReturn(Optional.of(previousElement));
        when(dataTypeStore.get(previousUUID)).thenReturn(previousDataType.get());
        when(dataTypeStore.get(hoverUUID)).thenReturn(hoverDataType.get());
        when(previousElement.getAttribute(UUID_ATTR)).thenReturn(previousUUID);
        when(hoverElement.getAttribute(UUID_ATTR)).thenReturn(hoverUUID);
        when(hoverDataType.get().isReadOnly()).thenReturn(true);

        final Optional<DataType> reference = context.getReference();

        assertEquals(previousDataType, reference);
    }

    @Test
    public void testGetReferenceWhenPreviousDataTypeIsPresent() {

        final Element previousElement = mock(Element.class);
        final Optional<DataType> previousDataType = Optional.of(mock(DataType.class));
        final String uuid = "0000-1111-2222-3333";

        when(dndListComponent.getPreviousElement(any(), any())).thenReturn(Optional.of(previousElement));
        when(dataTypeStore.get(uuid)).thenReturn(previousDataType.get());
        when(previousElement.getAttribute(UUID_ATTR)).thenReturn(uuid);

        final Optional<DataType> reference = context.getReference();

        assertEquals(previousDataType, reference);
    }

    @Test
    public void testGetReferenceWhenCurrentDataTypeIsPresent() {

        final Element firstElement = mock(Element.class);
        final Element hiddenElement = mock(Element.class);
        final Element wrongFirstElement = mock(Element.class);
        final HTMLElement dragArea = mock(HTMLElement.class);
        final Optional<DataType> currentDataType = Optional.of(mock(DataType.class));
        final Optional<DataType> firstDataType = Optional.of(mock(DataType.class));
        final String currentUUID = "0000-0000-0000-0000";
        final String firstUUID = "1111-1111-1111-1111";

        dragArea.childNodes = spy(new NodeList<>());
        dragArea.childNodes.length = 4;
        currentElement.classList = mock(DOMTokenList.class);
        wrongFirstElement.classList = mock(DOMTokenList.class);
        firstElement.classList = mock(DOMTokenList.class);

        doReturn(hiddenElement).when(dragArea.childNodes).getAt(0);
        doReturn(currentElement).when(dragArea.childNodes).getAt(1);
        doReturn(wrongFirstElement).when(dragArea.childNodes).getAt(2);
        doReturn(firstElement).when(dragArea.childNodes).getAt(3);
        when(hiddenElement.getAttribute(DATA_Y_POSITION)).thenReturn("-1");
        when(currentElement.getAttribute(DATA_Y_POSITION)).thenReturn("0");
        when(wrongFirstElement.getAttribute(DATA_Y_POSITION)).thenReturn("1");
        when(firstElement.getAttribute(DATA_Y_POSITION)).thenReturn("2");
        when(hiddenElement.getAttribute(DATA_X_POSITION)).thenReturn("0");
        when(currentElement.getAttribute(DATA_X_POSITION)).thenReturn("0");
        when(wrongFirstElement.getAttribute(DATA_X_POSITION)).thenReturn("0");
        when(firstElement.getAttribute(DATA_X_POSITION)).thenReturn("0");
        when(dndListComponent.getDragArea()).thenReturn(dragArea);
        when(dndListComponent.getPreviousElement(any(), any())).thenReturn(Optional.empty());
        when(dataTypeStore.get(currentUUID)).thenReturn(currentDataType.get());
        when(dataTypeStore.get(firstUUID)).thenReturn(firstDataType.get());
        when(currentDataType.get().getName()).thenReturn("Current Data Type");
        when(firstDataType.get().getName()).thenReturn("First Data Type");
        when(currentElement.getAttribute(UUID_ATTR)).thenReturn(currentUUID);
        when(firstElement.getAttribute(UUID_ATTR)).thenReturn(firstUUID);
        when(wrongFirstElement.classList.contains(DRAGGING)).thenReturn(true);
        when(firstElement.classList.contains(DRAGGING)).thenReturn(false);

        // The current element is loaded into constructor.
        // So re-instantiating context here, since this test mock the current element behavior.
        final DNDDataTypesHandlerContext context = new DNDDataTypesHandlerContext(dndDataTypesHandler, currentElement, hoverElement);
        final Optional<DataType> reference = context.getReference();

        assertEquals(firstDataType, reference);
    }

    @Test
    public void testGetReferenceWhenItIsNotPresent() {
        when(dndListComponent.getPreviousElement(any(), any())).thenReturn(Optional.empty());
        assertFalse(context.getReference().isPresent());
    }

    @Test
    public void testGetStrategyInsertIntoHoveredDataType() {

        final Optional<DataType> hoverDataType = Optional.of(mock(DataType.class));
        final String uuid = "0000-1111-2222-3333";

        when(hoverElement.getAttribute(UUID_ATTR)).thenReturn(uuid);
        when(dataTypeStore.get(uuid)).thenReturn(hoverDataType.get());
        loadReferenceContext();

        final DNDDataTypesHandlerShiftStrategy actualShiftStrategy = context.getStrategy();
        final DNDDataTypesHandlerShiftStrategy expectedShiftStrategy = INSERT_INTO_HOVERED_DATA_TYPE;

        assertEquals(expectedShiftStrategy, actualShiftStrategy);
    }

    @Test
    public void testGetStrategyInsertIntoHoveredDataTypeWhenHoveredDataTypeIsReadOnly() {

        final Optional<DataType> hoverDataType = Optional.of(mock(DataType.class));
        final String uuid = "0000-1111-2222-3333";

        when(hoverDataType.get().isReadOnly()).thenReturn(true);
        when(hoverElement.getAttribute(UUID_ATTR)).thenReturn(uuid);
        when(dataTypeStore.get(uuid)).thenReturn(hoverDataType.get());
        when(dndListComponent.getPreviousElement(any(), any())).thenReturn(Optional.empty());
        loadReferenceContext();

        final DNDDataTypesHandlerShiftStrategy actualShiftStrategy = context.getStrategy();
        final DNDDataTypesHandlerShiftStrategy expectedShiftStrategy = INSERT_TOP_LEVEL_DATA_TYPE_AT_THE_TOP;

        assertEquals(expectedShiftStrategy, actualShiftStrategy);
    }

    @Test
    public void testGetStrategyInsertTopLevelDataTypeAtTheTop() {

        when(dndListComponent.getPreviousElement(any(), any())).thenReturn(Optional.empty());
        loadReferenceContext();

        final DNDDataTypesHandlerShiftStrategy actualShiftStrategy = context.getStrategy();
        final DNDDataTypesHandlerShiftStrategy expectedShiftStrategy = INSERT_TOP_LEVEL_DATA_TYPE_AT_THE_TOP;

        assertEquals(expectedShiftStrategy, actualShiftStrategy);
    }

    @Test
    public void testGetStrategyInsertTopLevelDataType() {

        final Element previousElement = mock(Element.class);
        final String uuid = "0000-1111-2222-3333";
        final Optional<DataType> previousDataType = Optional.of(mock(DataType.class));

        when(dataTypeStore.get(uuid)).thenReturn(previousDataType.get());
        when(dndListComponent.getPreviousElement(any(), any())).thenReturn(Optional.of(previousElement));
        when(currentElement.getAttribute(DATA_X_POSITION)).thenReturn("0");
        when(previousElement.getAttribute(DATA_X_POSITION)).thenReturn("0");
        when(previousElement.getAttribute(UUID_ATTR)).thenReturn(uuid);
        loadReferenceContext();

        final DNDDataTypesHandlerShiftStrategy actualShiftStrategy = context.getStrategy();
        final DNDDataTypesHandlerShiftStrategy expectedShiftStrategy = INSERT_TOP_LEVEL_DATA_TYPE;

        assertEquals(expectedShiftStrategy, actualShiftStrategy);
    }

    @Test
    public void testGetStrategyInsertNestedDataType() {

        final Element previousElement = mock(Element.class);
        final String uuid = "0000-1111-2222-3333";
        final Optional<DataType> previousDataType = Optional.of(mock(DataType.class));

        when(dataTypeStore.get(uuid)).thenReturn(previousDataType.get());
        when(dndListComponent.getPreviousElement(any(), any())).thenReturn(Optional.of(previousElement));
        when(currentElement.getAttribute(DATA_X_POSITION)).thenReturn("1");
        when(previousElement.getAttribute(DATA_X_POSITION)).thenReturn("0");
        when(previousElement.getAttribute(UUID_ATTR)).thenReturn(uuid);
        loadReferenceContext();

        final DNDDataTypesHandlerShiftStrategy actualShiftStrategy = context.getStrategy();
        final DNDDataTypesHandlerShiftStrategy expectedShiftStrategy = INSERT_NESTED_DATA_TYPE;

        assertEquals(expectedShiftStrategy, actualShiftStrategy);
    }

    @Test
    public void testGetStrategyInsertNestedDataTypeWhenPreviousDataTypeIsReadOnly() {

        final Element previousElement = mock(Element.class);
        final String uuid = "0000-1111-2222-3333";
        final Optional<DataType> previousDataType = Optional.of(mock(DataType.class));

        when(previousDataType.get().isReadOnly()).thenReturn(true);
        when(dataTypeStore.get(uuid)).thenReturn(previousDataType.get());
        when(dndListComponent.getPreviousElement(any(), any())).thenReturn(Optional.of(previousElement));
        when(currentElement.getAttribute(DATA_X_POSITION)).thenReturn("1");
        when(previousElement.getAttribute(DATA_X_POSITION)).thenReturn("0");
        when(previousElement.getAttribute(UUID_ATTR)).thenReturn(uuid);
        loadReferenceContext();

        final DNDDataTypesHandlerShiftStrategy actualShiftStrategy = context.getStrategy();
        final DNDDataTypesHandlerShiftStrategy expectedShiftStrategy = INSERT_SIBLING_DATA_TYPE;

        assertEquals(expectedShiftStrategy, actualShiftStrategy);
    }

    @Test
    public void testGetStrategyInsertSiblingDataType() {

        final Element previousElement = mock(Element.class);
        final String uuid = "0000-1111-2222-3333";
        final Optional<DataType> previousDataType = Optional.of(mock(DataType.class));

        when(dataTypeStore.get(uuid)).thenReturn(previousDataType.get());
        when(dndListComponent.getPreviousElement(any(), any())).thenReturn(Optional.of(previousElement));
        when(currentElement.getAttribute(DATA_X_POSITION)).thenReturn("1");
        when(previousElement.getAttribute(DATA_X_POSITION)).thenReturn("1");
        when(previousElement.getAttribute(UUID_ATTR)).thenReturn(uuid);
        loadReferenceContext();

        final DNDDataTypesHandlerShiftStrategy actualShiftStrategy = context.getStrategy();
        final DNDDataTypesHandlerShiftStrategy expectedShiftStrategy = INSERT_SIBLING_DATA_TYPE;

        assertEquals(expectedShiftStrategy, actualShiftStrategy);
    }

    private void loadReferenceContext() {
        context.getReference();
    }
}
