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

package org.kie.workbench.common.dmn.client.editors.types.shortcuts;

import java.util.List;
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
import org.kie.workbench.common.dmn.client.editors.types.common.ScrollHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.selectpicker.JQueryList;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.UUID_ATTR;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.FOCUSED_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.editors.types.shortcuts.DataTypeListShortcutsView.HIGHLIGHT;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeListShortcutsViewTest {

    @Mock
    private DataTypeListShortcuts presenter;

    @Mock
    private ScrollHelper scrollHelper;

    private DataTypeListShortcutsView view;

    @Before
    public void setup() {
        view = spy(new DataTypeListShortcutsView(scrollHelper));
        view.init(presenter);
    }

    @Test
    public void testGetFirstDataTypeRowWhenFirstRowExists() {

        final Element dataTypeRow1 = fakeDataTypeRow("123");
        final Element dataTypeRow2 = fakeDataTypeRow("456");
        final Element dataTypeRow3 = fakeDataTypeRow("789");
        final JQueryList<Element> jQueryList = mock(JQueryList.class);

        jQueryList.length = 3;
        doReturn(jQueryList).when(view).filterVisible();
        when(jQueryList.get(0)).thenReturn(dataTypeRow1);
        when(jQueryList.get(1)).thenReturn(dataTypeRow2);
        when(jQueryList.get(2)).thenReturn(dataTypeRow3);

        final Element actual = view.getFirstDataTypeRow().orElseThrow(RuntimeException::new);

        assertEquals(dataTypeRow1, actual);
    }

    @Test
    public void testGetFirstDataTypeRowWhenFirstRowDoesNotExist() {

        final JQueryList<Element> jQueryList = mock(JQueryList.class);

        jQueryList.length = 0;
        doReturn(jQueryList).when(view).filterVisible();

        assertFalse(view.getFirstDataTypeRow().isPresent());
    }

    @Test
    public void testGetNextDataTypeRowWhenElementIsNotTheLastOne() {

        final Element dataTypeRow1 = fakeDataTypeRow("123");
        final Element dataTypeRow2 = fakeDataTypeRow("456");
        final Element dataTypeRow3 = fakeDataTypeRow("789");
        final JQueryList<Element> jQueryList = mock(JQueryList.class);

        jQueryList.length = 3;
        when(jQueryList.get(0)).thenReturn(dataTypeRow1);
        when(jQueryList.get(1)).thenReturn(dataTypeRow2);
        when(jQueryList.get(2)).thenReturn(dataTypeRow3);
        doReturn(jQueryList).when(view).filterVisible();
        doReturn("456").when(view).getCurrentUUID();

        final Element actual = view.getNextDataTypeRow().orElseThrow(RuntimeException::new);

        assertEquals(dataTypeRow3, actual);
    }

    @Test
    public void testGetNextDataTypeRowWhenElementIsTheLastOne() {

        final Element dataTypeRow1 = fakeDataTypeRow("123");
        final Element dataTypeRow2 = fakeDataTypeRow("456");
        final Element dataTypeRow3 = fakeDataTypeRow("789");
        final JQueryList<Element> jQueryList = mock(JQueryList.class);

        jQueryList.length = 3;
        when(jQueryList.get(0)).thenReturn(dataTypeRow1);
        when(jQueryList.get(1)).thenReturn(dataTypeRow2);
        when(jQueryList.get(2)).thenReturn(dataTypeRow3);
        doReturn(jQueryList).when(view).filterVisible();
        doReturn("789").when(view).getCurrentUUID();

        final Element actual = view.getNextDataTypeRow().orElseThrow(RuntimeException::new);

        assertEquals(dataTypeRow1, actual);
    }

    @Test
    public void testGetPrevDataTypeRowWhenElementIsNotTheFirstOne() {

        final Element dataTypeRow1 = fakeDataTypeRow("123");
        final Element dataTypeRow2 = fakeDataTypeRow("456");
        final Element dataTypeRow3 = fakeDataTypeRow("789");
        final JQueryList<Element> jQueryList = mock(JQueryList.class);

        jQueryList.length = 3;
        when(jQueryList.get(0)).thenReturn(dataTypeRow1);
        when(jQueryList.get(1)).thenReturn(dataTypeRow2);
        when(jQueryList.get(2)).thenReturn(dataTypeRow3);
        doReturn(jQueryList).when(view).filterVisible();
        doReturn("456").when(view).getCurrentUUID();

        final Element actual = view.getPrevDataTypeRow().orElseThrow(RuntimeException::new);

        assertEquals(dataTypeRow1, actual);
    }

    @Test
    public void testGetPrevDataTypeRowWhenElementIsTheFirstOne() {

        final Element dataTypeRow1 = fakeDataTypeRow("123");
        final Element dataTypeRow2 = fakeDataTypeRow("456");
        final Element dataTypeRow3 = fakeDataTypeRow("789");
        final JQueryList<Element> jQueryList = mock(JQueryList.class);

        jQueryList.length = 3;
        when(jQueryList.get(0)).thenReturn(dataTypeRow1);
        when(jQueryList.get(1)).thenReturn(dataTypeRow2);
        when(jQueryList.get(2)).thenReturn(dataTypeRow3);
        doReturn(jQueryList).when(view).filterVisible();
        doReturn("123").when(view).getCurrentUUID();

        final Element actual = view.getPrevDataTypeRow().orElseThrow(RuntimeException::new);

        assertEquals(dataTypeRow3, actual);
    }

    @Test
    public void testGetCurrentDataTypeListItem() {

        final DataTypeList dataTypeList = mock(DataTypeList.class);
        final DataTypeListItem item = mock(DataTypeListItem.class);
        final DataType dataType = fakeDataType("123");
        final Element dataTypeRow1 = fakeDataTypeRow("123");
        final Element dataTypeRow2 = fakeDataTypeRow("456");
        final Element dataTypeRow3 = fakeDataTypeRow("789");
        final JQueryList<Element> jQueryList = mock(JQueryList.class);
        final List<DataTypeListItem> items = singletonList(item);

        when(item.getDataType()).thenReturn(dataType);
        when(dataTypeList.getItems()).thenReturn(items);
        when(presenter.getDataTypeList()).thenReturn(dataTypeList);
        jQueryList.length = 3;
        when(jQueryList.get(0)).thenReturn(dataTypeRow1);
        when(jQueryList.get(1)).thenReturn(dataTypeRow2);
        when(jQueryList.get(2)).thenReturn(dataTypeRow3);
        doReturn(jQueryList).when(view).filterVisible();
        doReturn("123").when(view).getCurrentUUID();

        final DataTypeListItem actual = view.getCurrentDataTypeListItem().orElseThrow(RuntimeException::new);

        assertEquals(item, actual);
    }

    @Test
    public void testGetVisibleDataTypeListItems() {

        final DataTypeList dataTypeList = mock(DataTypeList.class);
        final DataTypeListItem item1 = mock(DataTypeListItem.class);
        final DataTypeListItem item2 = mock(DataTypeListItem.class);
        final DataTypeListItem item3 = mock(DataTypeListItem.class);
        final DataType dataType1 = fakeDataType("123");
        final DataType dataType2 = fakeDataType("456");
        final DataType dataType3 = fakeDataType("789");
        final Element dataTypeRow1 = fakeDataTypeRow("123");
        final Element dataTypeRow2 = fakeDataTypeRow("456");
        final Element dataTypeRow3 = fakeDataTypeRow("789");
        final List<DataTypeListItem> items = asList(item1, item2, item3);
        final JQueryList<Element> jQueryList = mock(JQueryList.class);

        when(item1.getDataType()).thenReturn(dataType1);
        when(item2.getDataType()).thenReturn(dataType2);
        when(item3.getDataType()).thenReturn(dataType3);
        when(dataTypeList.getItems()).thenReturn(items);
        when(presenter.getDataTypeList()).thenReturn(dataTypeList);
        jQueryList.length = 3;
        when(jQueryList.get(0)).thenReturn(dataTypeRow1);
        when(jQueryList.get(1)).thenReturn(dataTypeRow2);
        when(jQueryList.get(2)).thenReturn(dataTypeRow3);
        doReturn(jQueryList).when(view).filterVisible();

        final List<DataTypeListItem> actual = view.getVisibleDataTypeListItems();
        final List<DataTypeListItem> expected = asList(item1, item2, item3);

        assertEquals(expected, actual);
    }

    @Test
    public void testHighlight() {

        final String uuid = "uuid";
        final Element element = fakeDataTypeRow(uuid);

        doNothing().when(view).cleanCurrentHighlight();
        doNothing().when(view).setCurrentUUID(anyString());
        doNothing().when(view).addHighlightClass(any());
        doNothing().when(view).scrollTo(any());

        view.highlight(element);

        verify(view).cleanCurrentHighlight();
        verify(view).setCurrentUUID(uuid);
        verify(view).addHighlightClass(element);
        verify(view).scrollTo(element);
    }

    @Test
    public void testReset() {

        doNothing().when(view).cleanCurrentHighlight();
        doNothing().when(view).setCurrentUUID(anyString());

        view.reset();

        verify(view).cleanCurrentHighlight();
        verify(view).setCurrentUUID("");
    }

    @Test
    public void testCleanCurrentHighlight() {

        final Element element1 = fakeDataTypeRow("uuid1");
        final Element element2 = fakeDataTypeRow("uuid2");
        final NodeList<Element> elements = spy(new NodeList<>());

        element1.classList = mock(DOMTokenList.class);
        element2.classList = mock(DOMTokenList.class);
        elements.length = 2;
        doReturn(element1).when(elements).getAt(0);
        doReturn(element2).when(elements).getAt(1);
        doReturn(elements).when(view).querySelectorAll("." + HIGHLIGHT);

        view.cleanCurrentHighlight();

        verify(element1.classList).remove(HIGHLIGHT);
        verify(element2.classList).remove(HIGHLIGHT);
    }

    @Test
    public void getFocusedDataTypeListItemWhenElementExists() {

        final String uuid = "uuid";
        final Element element = fakeDataTypeRow(uuid);
        final DataTypeList dataTypeList = mock(DataTypeList.class);
        final DataTypeListItem item = mock(DataTypeListItem.class);
        final DataType dataType = fakeDataType(uuid);
        final List<DataTypeListItem> items = singletonList(item);

        when(item.getDataType()).thenReturn(dataType);
        when(dataTypeList.getItems()).thenReturn(items);
        when(presenter.getDataTypeList()).thenReturn(dataTypeList);
        doReturn(Optional.of(element)).when(view).querySelector("." + FOCUSED_CSS_CLASS);

        final Optional<DataTypeListItem> focusedDataTypeListItem = view.getFocusedDataTypeListItem();

        assertTrue(focusedDataTypeListItem.isPresent());
        assertEquals(item, focusedDataTypeListItem.get());
    }

    @Test
    public void getFocusedDataTypeListItemWhenElementDoesNotExist() {

        doReturn(Optional.empty()).when(view).querySelector("." + FOCUSED_CSS_CLASS);

        assertFalse(view.getFocusedDataTypeListItem().isPresent());
    }

    @Test
    public void testAddHighlightClass() {
        final Element element = fakeDataTypeRow("uuid");
        element.classList = mock(DOMTokenList.class);

        view.addHighlightClass(element);

        verify(element.classList).add(HIGHLIGHT);
    }

    @Test
    public void testScrollTo() {

        final Element element = mock(Element.class);
        final HTMLElement container = mock(HTMLElement.class);
        final DataTypeList dataTypeList = mock(DataTypeList.class);

        when(presenter.getDataTypeList()).thenReturn(dataTypeList);
        when(dataTypeList.getListItemsElement()).thenReturn(container);

        view.scrollTo(element);

        verify(scrollHelper).scrollTo(element, container, 20);
    }

    @Test
    public void testListsNextWhenItReturnsEmpty() {

        final Element dataTypeRow1 = fakeDataTypeRow("123");
        final Element dataTypeRow2 = fakeDataTypeRow("456");
        final Element dataTypeRow3 = fakeDataTypeRow("789");
        final List<Element> elements = asList(dataTypeRow1, dataTypeRow2, dataTypeRow3);

        final Optional<Element> actual = view.utils.next(elements, dataTypeRow3);

        assertFalse(actual.isPresent());
    }

    @Test
    public void testListsNextWhenItReturnsNextElement() {

        final Element dataTypeRow1 = fakeDataTypeRow("123");
        final Element dataTypeRow2 = fakeDataTypeRow("456");
        final Element dataTypeRow3 = fakeDataTypeRow("789");
        final List<Element> elements = asList(dataTypeRow1, dataTypeRow2, dataTypeRow3);

        final Optional<Element> actual = view.utils.next(elements, dataTypeRow2);

        assertTrue(actual.isPresent());
        assertEquals(dataTypeRow3, actual.get());
    }

    @Test
    public void testListsPrevWhenItReturnsEmpty() {

        final Element dataTypeRow1 = fakeDataTypeRow("123");
        final Element dataTypeRow2 = fakeDataTypeRow("456");
        final Element dataTypeRow3 = fakeDataTypeRow("789");
        final List<Element> elements = asList(dataTypeRow1, dataTypeRow2, dataTypeRow3);

        final Optional<Element> actual = view.utils.prev(elements, dataTypeRow1);

        assertFalse(actual.isPresent());
    }

    @Test
    public void testListsPrevWhenItReturnsPreviousElement() {

        final Element dataTypeRow1 = fakeDataTypeRow("123");
        final Element dataTypeRow2 = fakeDataTypeRow("456");
        final Element dataTypeRow3 = fakeDataTypeRow("789");
        final List<Element> elements = asList(dataTypeRow1, dataTypeRow2, dataTypeRow3);

        final Optional<Element> actual = view.utils.prev(elements, dataTypeRow2);

        assertTrue(actual.isPresent());
        assertEquals(dataTypeRow1, actual.get());
    }

    @Test
    public void testListsFirstWhenItReturnsEmpty() {
        final Optional<Element> actual = view.utils.first(emptyList());
        assertFalse(actual.isPresent());
    }

    @Test
    public void testListsFirstWhenItReturnsFirstElement() {

        final Element dataTypeRow1 = fakeDataTypeRow("123");
        final Element dataTypeRow2 = fakeDataTypeRow("456");
        final Element dataTypeRow3 = fakeDataTypeRow("789");
        final List<Element> elements = asList(dataTypeRow1, dataTypeRow2, dataTypeRow3);

        final Optional<Element> actual = view.utils.first(elements);

        assertTrue(actual.isPresent());
        assertEquals(dataTypeRow1, actual.get());
    }

    @Test
    public void testListsLastWhenItReturnsEmpty() {
        final Optional<Element> actual = view.utils.last(emptyList());
        assertFalse(actual.isPresent());
    }

    @Test
    public void testListsLastWhenItReturnsLastElement() {

        final Element dataTypeRow1 = fakeDataTypeRow("123");
        final Element dataTypeRow2 = fakeDataTypeRow("456");
        final Element dataTypeRow3 = fakeDataTypeRow("789");
        final List<Element> elements = asList(dataTypeRow1, dataTypeRow2, dataTypeRow3);

        final Optional<Element> actual = view.utils.last(elements);

        assertTrue(actual.isPresent());
        assertEquals(dataTypeRow3, actual.get());
    }

    private DataType fakeDataType(final String uuid) {
        final DataType dataType = mock(DataType.class);
        when(dataType.getUUID()).thenReturn(uuid);
        return dataType;
    }

    private Element fakeDataTypeRow(final String uuid) {
        final Element element = mock(Element.class);
        when(element.getAttribute(UUID_ATTR)).thenReturn(uuid);
        return element;
    }
}
