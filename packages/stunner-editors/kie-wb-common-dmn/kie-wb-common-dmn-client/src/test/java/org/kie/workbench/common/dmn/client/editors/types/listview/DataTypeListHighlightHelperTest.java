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

import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import elemental2.dom.Node;
import elemental2.dom.NodeList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeUtils;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListHighlightHelper.HIGHLIGHT;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListHighlightHelper.LEVEL_BACKGROUND_LINE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListHighlightHelper.LEVEL_HIGHLIGHT;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.UUID_ATTR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeListHighlightHelperTest {

    @Mock
    private DataTypeList dataTypeList;

    @Mock
    private DataTypeUtils dataTypeUtils;

    private DataTypeListHighlightHelper helper;

    @Before
    public void setup() {
        helper = spy(new DataTypeListHighlightHelper(dataTypeUtils));
        helper.init(dataTypeList);
    }

    @Test
    public void testHighlight() {

        final Element element = mock(Element.class);

        element.classList = mock(DOMTokenList.class);
        doNothing().when(helper).cleanHighlightClass();

        helper.highlight(element);

        verify(helper).cleanHighlightClass();
        verify(element.classList).add(HIGHLIGHT);
    }

    @Test
    public void testGetDataTypeListItemWhenListItemIsPresent() {

        final String uuid1 = "uuid1";
        final String uuid2 = "uuid2";
        final String uuid3 = "uuid3";
        final DataTypeListItem listItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem2 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem3 = mock(DataTypeListItem.class);
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataType dataType3 = mock(DataType.class);
        final List<DataTypeListItem> listItems = asList(listItem1, listItem2, listItem3);

        when(dataTypeList.getItems()).thenReturn(listItems);
        when(listItem1.getDataType()).thenReturn(dataType1);
        when(listItem2.getDataType()).thenReturn(dataType2);
        when(listItem3.getDataType()).thenReturn(dataType3);
        when(dataType1.getUUID()).thenReturn(uuid1);
        when(dataType2.getUUID()).thenReturn(uuid2);
        when(dataType3.getUUID()).thenReturn(uuid3);

        final Optional<DataTypeListItem> dataTypeListItem = helper.getDataTypeListItem(uuid2);

        assertTrue(dataTypeListItem.isPresent());
        assertEquals(listItem2, dataTypeListItem.get());
    }

    @Test
    public void testGetDataTypeListItemWhenListItemIsNotPresent() {

        final String uuid1 = "uuid1";
        final String uuid2 = "uuid2";
        final String uuid3 = "uuid3";
        final DataTypeListItem listItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem2 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem3 = mock(DataTypeListItem.class);
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataType dataType3 = mock(DataType.class);
        final List<DataTypeListItem> listItems = asList(listItem1, listItem2, listItem3);

        when(dataTypeList.getItems()).thenReturn(listItems);
        when(listItem1.getDataType()).thenReturn(dataType1);
        when(listItem2.getDataType()).thenReturn(dataType2);
        when(listItem3.getDataType()).thenReturn(dataType3);
        when(dataType1.getUUID()).thenReturn(uuid1);
        when(dataType2.getUUID()).thenReturn(uuid2);
        when(dataType3.getUUID()).thenReturn(uuid3);

        final Optional<DataTypeListItem> dataTypeListItem = helper.getDataTypeListItem("1234");

        assertFalse(dataTypeListItem.isPresent());
    }

    @Test
    public void testHighlightLevel() {

        final DataTypeListItem personListItem = mock(DataTypeListItem.class);
        final DataTypeListItem personNameListItem = mock(DataTypeListItem.class);
        final DataTypeListItem parsonAgeListItem = mock(DataTypeListItem.class);

        final DataType personDataType = mock(DataType.class);
        final DataType personNameDataType = mock(DataType.class);
        final DataType parsonAgeDataType = mock(DataType.class);

        final String personUUID = "0000";
        final String personNameUUID = "1111";
        final String parsonAgeUUID = "2222";

        final HTMLElement personElement = mock(HTMLElement.class);
        final HTMLElement personNameElement = mock(HTMLElement.class);
        final HTMLElement parsonAgeElement = mock(HTMLElement.class);

        doReturn(Optional.of(personListItem)).when(helper).getDataTypeListItem(personUUID);
        doReturn(Optional.of(personNameListItem)).when(helper).getDataTypeListItem(personNameUUID);
        doReturn(Optional.of(parsonAgeListItem)).when(helper).getDataTypeListItem(parsonAgeUUID);
        doNothing().when(helper).cleanLevelHighlightClass();
        doNothing().when(helper).appendBackgroundLine(any(), any());

        personElement.classList = mock(DOMTokenList.class);
        personNameElement.classList = mock(DOMTokenList.class);
        parsonAgeElement.classList = mock(DOMTokenList.class);

        when(personElement.getAttribute(UUID_ATTR)).thenReturn(personUUID);
        when(personNameElement.getAttribute(UUID_ATTR)).thenReturn(personNameUUID);
        when(parsonAgeElement.getAttribute(UUID_ATTR)).thenReturn(parsonAgeUUID);

        when(personDataType.getUUID()).thenReturn(personUUID);
        when(personNameDataType.getUUID()).thenReturn(personNameUUID);
        when(parsonAgeDataType.getUUID()).thenReturn(parsonAgeUUID);

        when(personDataType.isTopLevel()).thenReturn(true);
        when(personNameDataType.isTopLevel()).thenReturn(false);
        when(parsonAgeDataType.isTopLevel()).thenReturn(false);

        when(personListItem.getDataType()).thenReturn(personDataType);
        when(personNameListItem.getDataType()).thenReturn(personNameDataType);
        when(parsonAgeListItem.getDataType()).thenReturn(parsonAgeDataType);

        when(personListItem.getDragAndDropElement()).thenReturn(personElement);
        when(personNameListItem.getDragAndDropElement()).thenReturn(personNameElement);
        when(parsonAgeListItem.getDragAndDropElement()).thenReturn(parsonAgeElement);

        when(personDataType.getSubDataTypes()).thenReturn(asList(personNameDataType, parsonAgeDataType));
        when(dataTypeUtils.getTopLevelParent(personNameDataType)).thenReturn(personDataType);

        helper.highlightLevel(personNameElement);

        verify(helper).cleanLevelHighlightClass();
        verify(helper, times(1)).appendBackgroundLine(personDataType, personElement);
        verify(personElement.classList).add(LEVEL_HIGHLIGHT);
        verify(personNameElement.classList).add(LEVEL_HIGHLIGHT);
        verify(parsonAgeElement.classList).add(LEVEL_HIGHLIGHT);
    }

    @Test
    public void testHighlightLevelWhenDataTypeListIsNull() {
        helper.init(null);
        assertThatThrownBy(() -> helper.highlightLevel(mock(HTMLElement.class)))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("DataTypeListHighlightHelper error 'dataTypeList' must be initialized.");
    }

    @Test
    public void testCleanHighlightClass() {

        final Element element1 = mock(Element.class);
        final Element element2 = mock(Element.class);
        final NodeList<Element> elements = spy(new NodeList<>());

        element1.classList = mock(DOMTokenList.class);
        element2.classList = mock(DOMTokenList.class);
        elements.length = 2;
        doReturn(element1).when(elements).getAt(0);
        doReturn(element2).when(elements).getAt(1);
        doReturn(elements).when(helper).querySelectorAll("." + HIGHLIGHT);

        helper.cleanHighlightClass();

        verify(element1.classList).remove(HIGHLIGHT);
        verify(element2.classList).remove(HIGHLIGHT);
    }

    @Test
    public void testCleanLevelHighlightClass() {

        final Element element1 = mock(Element.class);
        final Element element2 = mock(Element.class);
        final Element backgroundLine1 = mock(Element.class);
        final Element backgroundLine2 = mock(Element.class);
        final Node parentNode = mock(Node.class);
        final NodeList<Element> highlightedElements = spy(new NodeList<>());
        final NodeList<Element> backgroundLines = spy(new NodeList<>());

        element1.classList = mock(DOMTokenList.class);
        element2.classList = mock(DOMTokenList.class);
        backgroundLine1.parentNode = parentNode;
        backgroundLine2.parentNode = parentNode;
        highlightedElements.length = 2;
        backgroundLines.length = 2;
        doReturn(element1).when(highlightedElements).getAt(0);
        doReturn(element2).when(highlightedElements).getAt(1);
        doReturn(backgroundLine1).when(backgroundLines).getAt(0);
        doReturn(backgroundLine2).when(backgroundLines).getAt(1);
        doReturn(highlightedElements).when(helper).querySelectorAll("." + LEVEL_HIGHLIGHT);
        doReturn(backgroundLines).when(helper).querySelectorAll("." + LEVEL_BACKGROUND_LINE);

        helper.cleanLevelHighlightClass();

        verify(element1.classList).remove(LEVEL_HIGHLIGHT);
        verify(element2.classList).remove(LEVEL_HIGHLIGHT);
        verify(parentNode).removeChild(backgroundLine1);
        verify(parentNode).removeChild(backgroundLine2);
    }

    @Test
    public void testAppendBackgroundLine() {

        final DataType person = mock(DataType.class);
        final DataType personAge = mock(DataType.class);
        final DataType personName = mock(DataType.class);
        final DataType personAddress = mock(DataType.class);
        final String personUUID = "0000";
        final String personAgeUUID = "1111";
        final String personNameUUID = "2222";
        final String personAddressUUID = "3333";
        final String personAddressStreetUUID = "4444";
        final DataType personAddressStreet = mock(DataType.class);
        final DataTypeListItem personItem = mock(DataTypeListItem.class);
        final DataTypeListItem personAgeItem = mock(DataTypeListItem.class);
        final DataTypeListItem personNameItem = mock(DataTypeListItem.class);
        final DataTypeListItem personAddressItem = mock(DataTypeListItem.class);
        final DataTypeListItem personAddressStreetItem = mock(DataTypeListItem.class);
        final Element backgroundLine = mock(Element.class);
        final HTMLElement element = mock(HTMLElement.class);
        final NodeList<Element> nodeList = new NodeList<>();

        backgroundLine.classList = mock(DOMTokenList.class);
        element.offsetHeight = 70;
        nodeList.length = 3;

        doReturn(backgroundLine).when(helper).createElement("div");
        doReturn(nodeList).when(helper).querySelectorAll(".kie-dnd-current-dragging");
        doReturn(Optional.of(personItem)).when(helper).getDataTypeListItem(personUUID);
        doReturn(Optional.of(personAgeItem)).when(helper).getDataTypeListItem(personAgeUUID);
        doReturn(Optional.of(personNameItem)).when(helper).getDataTypeListItem(personNameUUID);
        doReturn(Optional.of(personAddressItem)).when(helper).getDataTypeListItem(personAddressUUID);
        doReturn(Optional.of(personAddressStreetItem)).when(helper).getDataTypeListItem(personAddressStreetUUID);

        when(person.getUUID()).thenReturn(personUUID);
        when(personAge.getUUID()).thenReturn(personAgeUUID);
        when(personName.getUUID()).thenReturn(personNameUUID);
        when(personAddress.getUUID()).thenReturn(personAddressUUID);
        when(personAddressStreet.getUUID()).thenReturn(personAddressStreetUUID);

        when(personItem.isCollapsed()).thenReturn(false);
        when(personAgeItem.isCollapsed()).thenReturn(false);
        when(personNameItem.isCollapsed()).thenReturn(false);
        when(personAddressItem.isCollapsed()).thenReturn(true);
        when(personAddressStreetItem.isCollapsed()).thenReturn(false);

        when(person.getSubDataTypes()).thenReturn(asList(personAge, personName, personAddress));
        when(personAddress.getSubDataTypes()).thenReturn(singletonList(personAddressStreet));

        helper.appendBackgroundLine(person, element);

        verify(backgroundLine.classList).add(LEVEL_BACKGROUND_LINE);
        verify(element).appendChild(backgroundLine);
        verify(backgroundLine).setAttribute("style", "height: 490px");
    }

    @Test
    public void testHasBackgroundLineWhenItReturnsTrue() {
        final HTMLElement element = mock(HTMLElement.class);
        final HTMLElement line = mock(HTMLElement.class);
        when(element.querySelector("." + LEVEL_BACKGROUND_LINE)).thenReturn(line);
        assertTrue(helper.hasBackgroundLine(element));
    }

    @Test
    public void testHasBackgroundLineWhenItReturnsFalse() {
        final HTMLElement element = mock(HTMLElement.class);
        assertFalse(helper.hasBackgroundLine(element));
    }
}
