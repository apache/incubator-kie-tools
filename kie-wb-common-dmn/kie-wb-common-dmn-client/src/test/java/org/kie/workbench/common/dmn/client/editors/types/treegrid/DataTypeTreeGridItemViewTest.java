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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.CSSProperties.MarginLeftUnionType;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.DOMTokenList;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLTableRowElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.DataType;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeTreeGridItemViewTest {

    @Mock
    private HTMLTableRowElement row;

    @Mock
    private HTMLElement level;

    @Mock
    private HTMLElement arrow;

    @Mock
    private HTMLElement name;

    @Mock
    private HTMLElement type;

    @Mock
    private DataType dataType;

    @Mock
    private DataTypeTreeGridItem presenter;

    @Captor
    private ArgumentCaptor<Integer> integerCaptor;

    private DataTypeTreeGridItemView view;

    @Before
    public void setup() {
        view = spy(new DataTypeTreeGridItemView(row, level, arrow, name, type));
        view.init(presenter);
    }

    @Test
    public void testSetDataType() {

        doNothing().when(view).setupRowMetadata(dataType);
        doNothing().when(view).setupArrow(dataType);
        doNothing().when(view).setupIndentationLevel();
        doNothing().when(view).setupDataTypeValues(dataType);

        view.setDataType(dataType);

        verify(view).setupRowMetadata(dataType);
        verify(view).setupArrow(dataType);
        verify(view).setupIndentationLevel();
        verify(view).setupDataTypeValues(dataType);
    }

    @Test
    public void testSetupRowMetadataWhenDataTypeIsBasicAndDefaultAndExternal() {

        final DOMTokenList classList = mock(DOMTokenList.class);

        when(dataType.isBasic()).thenReturn(true);
        when(dataType.isDefault()).thenReturn(true);
        when(dataType.isExternal()).thenReturn(true);
        when(dataType.getUUID()).thenReturn("1234");
        row.classList = classList;

        view.setupRowMetadata(dataType);

        verify(row).setAttribute("data-row-uuid", "1234");
        verify(classList).add("basic-row");
        verify(classList).add("default-row");
        verify(classList).add("external-row");
    }

    @Test
    public void testSetupRowMetadataWhenDataTypeIsNotBasicNeitherDefaultNeitherExternal() {

        final DOMTokenList classList = mock(DOMTokenList.class);

        when(dataType.isBasic()).thenReturn(false);
        when(dataType.isDefault()).thenReturn(false);
        when(dataType.isExternal()).thenReturn(false);
        when(dataType.getUUID()).thenReturn("1234");
        row.classList = classList;

        view.setupRowMetadata(dataType);

        verify(row).setAttribute("data-row-uuid", "1234");
        verify(classList).add("structure-row");
        verify(classList).add("custom-row");
        verify(classList).add("nested-row");
    }

    @Test
    public void testSetupRowMetadataWhenDataTypeIsNotBasic() {

        final DOMTokenList classList = mock(DOMTokenList.class);

        when(dataType.isBasic()).thenReturn(false);
        when(dataType.getUUID()).thenReturn("1234");
        row.classList = classList;

        view.setupRowMetadata(dataType);

        verify(row).setAttribute("data-row-uuid", "1234");
        verify(classList).add("structure-row");
    }

    @Test
    public void testSetupArrowWhenDataTypeHasSubDataTypes() {

        final DOMTokenList classList = mock(DOMTokenList.class);

        when(dataType.hasSubDataTypes()).thenReturn(true);
        arrow.classList = classList;

        view.setupArrow(dataType);

        verify(classList).remove("hidden");
    }

    @Test
    public void testSetupArrowWhenDataTypeDoesNotHaveSubDataTypes() {

        final DOMTokenList classList = mock(DOMTokenList.class);

        when(dataType.hasSubDataTypes()).thenReturn(false);
        arrow.classList = classList;

        view.setupArrow(dataType);

        verify(classList).add("hidden");
    }

    @Test
    public void testSetupIndentationLevel() {

        final CSSStyleDeclaration style = mock(CSSStyleDeclaration.class);
        final MarginLeftUnionType margin = mock(MarginLeftUnionType.class);

        when(presenter.getLevel()).thenReturn(2);
        doReturn(margin).when(view).margin(anyInt());
        level.style = style;

        view.setupIndentationLevel();

        verify(view).margin(integerCaptor.capture());
        assertEquals(40, (int) integerCaptor.getValue());
        assertEquals(margin, level.style.marginLeft);
    }

    @Test
    public void testSetupDataTypeValues() {

        final String name = "name";
        final String type = "type";

        when(dataType.getName()).thenReturn(name);
        when(dataType.getType()).thenReturn(type);

        view.setupDataTypeValues(dataType);

        assertEquals(this.name.textContent, name);
    }

    @Test
    public void testOnArrowClickEvent() {
        final ClickEvent event = mock(ClickEvent.class);

        view.onArrowClickEvent(event);

        verify(presenter).expandOrCollapseSubTypes();
    }

    @Test
    public void testSetupSelectComponent() {

        final DataTypeSelect select = mock(DataTypeSelect.class);
        final HTMLElement htmlElement = mock(HTMLElement.class);

        when(select.getElement()).thenReturn(htmlElement);
        type.innerHTML = "previous content";

        view.setupSelectComponent(select);

        assertFalse(type.innerHTML.contains("previous content"));
        verify(type).appendChild(htmlElement);
    }

    @Test
    public void testExpand() {

        final DOMTokenList classList = mock(DOMTokenList.class);
        final HTMLElement arrow = mock(HTMLElement.class);
        arrow.classList = classList;

        doReturn(arrow).when(view).getArrow();

        view.expand();

        verify(classList).remove("fa-angle-right");
        verify(classList).add("fa-angle-down");
        verify(presenter).expandSubDataTypes();
    }

    @Test
    public void testCollapse() {

        final DOMTokenList classList = mock(DOMTokenList.class);
        final HTMLElement arrow = mock(HTMLElement.class);
        arrow.classList = classList;
        doReturn(arrow).when(view).getArrow();

        view.collapse();

        verify(classList).remove("fa-angle-down");
        verify(classList).add("fa-angle-right");
        verify(presenter).collapseSubDataTypes();
    }

    @Test
    public void testIsCollapsedWhenItIsRightArrow() {

        final HTMLElement arrow = mock(HTMLElement.class);
        final DOMTokenList classList = mock(DOMTokenList.class);

        arrow.classList = classList;
        when(classList.contains("fa-angle-right")).thenReturn(true);
        doReturn(arrow).when(view).getArrow();

        assertTrue(view.isCollapsed());
    }

    @Test
    public void testIsCollapsedArrowWhenItIsNotRightArrow() {

        final HTMLElement arrow = mock(HTMLElement.class);
        final DOMTokenList classList = mock(DOMTokenList.class);

        arrow.classList = classList;
        when(classList.contains("fa-angle-right")).thenReturn(false);
        doReturn(arrow).when(view).getArrow();

        assertFalse(view.isCollapsed());
    }

    @Test
    public void testIsCollapsedWhenArrowIsARightArrow() {

        final DOMTokenList classList = mock(DOMTokenList.class);
        final HTMLElement arrow = mock(HTMLElement.class);
        arrow.classList = classList;
        when(classList.contains("fa-angle-right")).thenReturn(true);

        assertTrue(view.isCollapsed(arrow));
    }

    @Test
    public void testIsCollapsedWhenArrowIsNotARightArrow() {

        final DOMTokenList classList = mock(DOMTokenList.class);
        final HTMLElement arrow = mock(HTMLElement.class);
        arrow.classList = classList;
        when(classList.contains("fa-angle-right")).thenReturn(false);

        assertFalse(view.isCollapsed(arrow));
    }

    @Test
    public void testCollapseSubDataTypes() {

        final DOMTokenList classList = mock(DOMTokenList.class);

        doReturn(row).when(view).getRowElement(dataType);
        row.classList = classList;

        view.collapseSubType(dataType);

        classList.add("hidden");
        verify(presenter).collapseSubDataTypes(dataType);
    }

    @Test
    public void testExpandSubDataTypesWhenElementIsNotCollapsed() {

        final DOMTokenList classList = mock(DOMTokenList.class);

        doReturn(arrow).when(view).getArrowElement(row);
        doReturn(row).when(view).getRowElement(dataType);
        doReturn(false).when(view).isCollapsed(arrow);
        row.classList = classList;

        view.expandSubType(dataType);

        classList.remove("hidden");
        verify(presenter).expandSubDataTypes(dataType);
    }

    @Test
    public void testExpandSubDataTypesWhenElementIsCollapsed() {

        final DOMTokenList classList = mock(DOMTokenList.class);

        doReturn(arrow).when(view).getArrowElement(row);
        doReturn(row).when(view).getRowElement(dataType);
        doReturn(true).when(view).isCollapsed(arrow);
        row.classList = classList;

        view.expandSubType(dataType);

        classList.remove("hidden");
        verify(presenter, never()).expandSubDataTypes(dataType);
    }
}
