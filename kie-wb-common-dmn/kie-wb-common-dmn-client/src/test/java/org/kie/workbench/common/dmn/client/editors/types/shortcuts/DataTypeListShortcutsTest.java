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
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeListShortcutsTest {

    @Mock
    private DataTypeList dataTypeList;

    @Mock
    private DataTypeListShortcutsView view;

    @Mock
    private DataTypeListItem listItem;

    @Captor
    private ArgumentCaptor<Consumer<DataTypeListItem>> onDataTypeListItemUpdateArgumentCaptor;

    private DataTypeListShortcuts shortcuts;

    @Before
    public void setup() {
        when(view.getCurrentDataTypeListItem()).thenReturn(Optional.of(listItem));
        shortcuts = spy(new DataTypeListShortcuts(view));
        shortcuts.init(dataTypeList);
    }

    @Test
    public void testInit() {

        // The 'init' method is being called during the setup.

        final DataTypeListItem listItem = mock(DataTypeListItem.class);
        final HTMLElement htmlElement = mock(HTMLElement.class);
        final DataTypeList actualDataTypeList = shortcuts.getDataTypeList();
        final DataTypeList expectedDataTypeList = shortcuts.getDataTypeList();

        when(listItem.getElement()).thenReturn(htmlElement);

        assertEquals(expectedDataTypeList, actualDataTypeList);
        verify(expectedDataTypeList).registerDataTypeListItemUpdateCallback(onDataTypeListItemUpdateArgumentCaptor.capture());

        onDataTypeListItemUpdateArgumentCaptor.getValue().accept(listItem);
        verify(view).highlight(htmlElement);
    }

    @Test
    public void testOnArrowDown() {

        final Element nextDataTypeRow = mock(Element.class);
        when(view.getNextDataTypeRow()).thenReturn(Optional.of(nextDataTypeRow));

        shortcuts.onArrowDown();

        verify(view).highlight(nextDataTypeRow);
    }

    @Test
    public void testOnArrowUp() {

        final Element prevDataTypeRow = mock(Element.class);
        when(view.getPrevDataTypeRow()).thenReturn(Optional.of(prevDataTypeRow));

        shortcuts.onArrowUp();

        verify(view).highlight(prevDataTypeRow);
    }

    @Test
    public void testOnTab() {

        final Element firstDataTypeRow = mock(Element.class);
        when(view.getFirstDataTypeRow()).thenReturn(Optional.of(firstDataTypeRow));

        shortcuts.onTab();

        verify(view).highlight(firstDataTypeRow);
    }

    @Test
    public void testOnArrowLeft() {

        shortcuts.onArrowLeft();

        verify(listItem).collapse();
    }

    @Test
    public void testOnArrowRight() {

        shortcuts.onArrowRight();

        verify(listItem).expand();
    }

    @Test
    public void testOnCtrlE() {

        shortcuts.onCtrlE();

        verify(listItem).enableEditMode();
    }

    @Test
    public void testOnEscapeWhenCurrentDataTypeListItemIsPresent() {

        shortcuts.onEscape();

        verify(listItem).disableEditMode();
        verify(shortcuts, never()).reset();
    }

    @Test
    public void testOnEscapeWhenCurrentDataTypeListItemIsNotPresent() {

        final DataTypeListItem listItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem2 = mock(DataTypeListItem.class);
        final List<DataTypeListItem> items = asList(listItem1, listItem2);
        when(view.getCurrentDataTypeListItem()).thenReturn(Optional.empty());
        when(view.getVisibleDataTypeListItems()).thenReturn(items);

        shortcuts.onEscape();

        verify(listItem1).disableEditMode();
        verify(listItem2).disableEditMode();
        verify(shortcuts).reset();
    }

    @Test
    public void testOnBackspace() {

        shortcuts.onCtrlBackspace();

        verify(listItem).remove();
    }

    @Test
    public void testOnCtrlS() {

        shortcuts.onCtrlS();

        verify(listItem).saveAndCloseEditMode();
    }

    @Test
    public void testOnCtrlB() {

        shortcuts.onCtrlB();

        verify(listItem).insertNestedField();
    }

    @Test
    public void testOnCtrlU() {

        shortcuts.onCtrlU();

        verify(listItem).insertFieldAbove();
    }

    @Test
    public void testOnCtrlD() {

        shortcuts.onCtrlD();

        verify(listItem).insertFieldBelow();
    }

    @Test
    public void testReset() {
        shortcuts.reset();

        verify(view).reset();
    }

    @Test
    public void testFocusIn() {
        shortcuts.focusIn();

        verify(view).focusIn();
    }

    @Test
    public void testHighlight() {
        final Element element = mock(Element.class);
        shortcuts.highlight(element);

        verify(view).highlight(element);
    }

    @Test
    public void testOnCtrlEWhenDataTypeIsReadOnly() {
        when(listItem.isReadOnly()).thenReturn(true);

        shortcuts.onCtrlE();

        verify(listItem, never()).enableEditMode();
    }

    @Test
    public void testOnCtrlBackspaceWhenDataTypeIsReadOnly() {
        when(listItem.isReadOnly()).thenReturn(true);

        shortcuts.onCtrlBackspace();

        verify(listItem, never()).remove();
    }

    @Test
    public void testOnCtrlSWhenDataTypeIsReadOnly() {
        when(listItem.isReadOnly()).thenReturn(true);

        shortcuts.onCtrlS();

        verify(listItem, never()).saveAndCloseEditMode();
    }

    @Test
    public void testOnCtrlBWhenDataTypeIsReadOnly() {
        when(listItem.isReadOnly()).thenReturn(true);

        shortcuts.onCtrlB();

        verify(listItem, never()).insertNestedField();
    }

    @Test
    public void testOnCtrlUWhenDataTypeIsReadOnly() {
        when(listItem.isReadOnly()).thenReturn(true);

        shortcuts.onCtrlU();

        verify(listItem, never()).insertFieldAbove();
    }

    @Test
    public void testOnCtrlDWhenDataTypeIsReadOnly() {
        when(listItem.isReadOnly()).thenReturn(true);

        shortcuts.onCtrlD();

        verify(listItem, never()).insertFieldBelow();
    }
}
