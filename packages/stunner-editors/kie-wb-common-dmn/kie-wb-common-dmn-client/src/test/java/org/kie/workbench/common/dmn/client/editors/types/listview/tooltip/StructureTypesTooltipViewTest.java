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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.DOMRect;
import elemental2.dom.DOMTokenList;
import elemental2.dom.Event;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLBodyElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.HTMLLIElement;
import elemental2.dom.HTMLParagraphElement;
import elemental2.dom.HTMLUListElement;
import elemental2.dom.Node;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeKind;
import org.mockito.Mock;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.SCROLL;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.types.listview.tooltip.StructureTypesTooltipView.DISPLAY_BLOCK;
import static org.kie.workbench.common.dmn.client.editors.types.listview.tooltip.StructureTypesTooltipView.DISPLAY_NONE;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.StructureTypesTooltipView_DescriptionCustom;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.StructureTypesTooltipView_DescriptionIncluded;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.StructureTypesTooltipView_DescriptionStructured;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class StructureTypesTooltipViewTest {

    @Mock
    private HTMLDivElement tooltip;

    @Mock
    private HTMLButtonElement close;

    @Mock
    private HTMLHeadingElement dataTypeName;

    @Mock
    private HTMLParagraphElement description;

    @Mock
    private HTMLUListElement dataTypeFields;

    @Mock
    private HTMLLIElement htmlLiElement;

    @Mock
    private HTMLElement htmlSpanElement;

    @Mock
    private HTMLAnchorElement viewDataTypeLink;

    @Mock
    private TranslationService translationService;

    @Mock
    private HTMLBodyElement bodyElement;

    @Mock
    private HTMLElement element;

    @Mock
    private StructureTypesTooltip presenter;

    private StructureTypesTooltipView view;

    @Before
    public void setup() {
        view = spy(new StructureTypesTooltipView(tooltip, close, dataTypeName, description, dataTypeFields, htmlLiElement, htmlSpanElement, viewDataTypeLink, translationService));
        view.init(presenter);

        tooltip.style = mock(CSSStyleDeclaration.class);
        tooltip.classList = mock(DOMTokenList.class);

        doReturn(bodyElement).when(view).getBody();
        doReturn(element).when(view).getElement();
    }

    @Test
    public void testSetup() {

        final String expectedDisplay = DISPLAY_NONE;

        tooltip.style.display = "";

        view.setup();

        final String actualDisplay = tooltip.style.display;

        verify(bodyElement).appendChild(element);
        assertEquals(expectedDisplay, actualDisplay);
    }

    @Test
    public void testOnClose() {
        doNothing().when(view).hide();

        view.onClose(mock(ClickEvent.class));

        verify(view).hide();
    }

    @Test
    public void testOnViewDataTypeLink() {
        final ClickEvent clickEvent = mock(ClickEvent.class);

        doNothing().when(view).hide();

        view.onViewDataTypeLink(clickEvent);

        verify(presenter).goToDataType();
        verify(view).hide();
        verify(clickEvent).stopPropagation();
        verify(clickEvent).preventDefault();
    }

    @Test
    public void testShow() {

        final HTMLElement refElement = mock(HTMLElement.class);

        doNothing().when(view).updateTooltipPosition(refElement);
        doNothing().when(view).updateTooltipAsVisible();
        doNothing().when(view).updateContent();
        doNothing().when(view).updateTooltipClass();
        doNothing().when(view).setupListeners();

        view.show(refElement);

        verify(view).updateTooltipPosition(refElement);
        verify(view).updateTooltipAsVisible();
        verify(view).updateContent();
        verify(view).updateTooltipClass();
        verify(view).setupListeners();
    }

    @Test
    public void testHide() {
        doNothing().when(view).updateTooltipAsHidden();
        doNothing().when(view).teardownListeners();

        view.hide();

        verify(view).updateTooltipAsHidden();
        verify(view).teardownListeners();
    }

    @Test
    public void testUpdateContentWhenDataTypeHasZeroFields() {

        final List<DataType> dataTypes = new ArrayList<>();
        final Node dataTypeFieldsFirstChild = mock(Node.class);
        final String typeName = "tPerson";
        final String message = "tPerson is a custom, structured data type without fields";

        doReturn(Optional.of(message)).when(view).getDescriptionText();
        when(presenter.getTypeName()).thenReturn(typeName);
        when(presenter.getTypeFields()).thenReturn(dataTypes);

        dataTypeFields.firstChild = dataTypeFieldsFirstChild;

        doAnswer((e) -> {
            dataTypeFields.firstChild = null;
            return null;
        }).when(dataTypeFields).removeChild(dataTypeFieldsFirstChild);

        view.updateContent();

        assertEquals(message, description.textContent);
        assertEquals(typeName, dataTypeName.textContent);

        verify(dataTypeFields).removeChild(dataTypeFieldsFirstChild);
        verify(dataTypeFields, never()).appendChild(any());
    }

    @Test
    public void testGetDescriptionTextWhenKindIsCustom() {

        final String typeName = "tPerson";
        final String expectedDescription = "message";

        when(presenter.getTypeName()).thenReturn(typeName);
        when(presenter.getDataTypeKind()).thenReturn(DataTypeKind.CUSTOM);
        when(translationService.format(StructureTypesTooltipView_DescriptionCustom, typeName)).thenReturn(expectedDescription);

        final Optional<String> actualDescription = view.getDescriptionText();

        assertTrue(actualDescription.isPresent());
        assertEquals(expectedDescription, actualDescription.get());
    }

    @Test
    public void testGetDescriptionTextWhenKindIsStructure() {

        final String typeName = "tPerson";
        final String expectedDescription = "message";
        final List<DataType> typeFields = asList(mock(DataType.class), mock(DataType.class));

        when(presenter.getTypeName()).thenReturn(typeName);
        when(presenter.getDataTypeKind()).thenReturn(DataTypeKind.STRUCTURE);
        when(presenter.getTypeFields()).thenReturn(typeFields);
        when(translationService.format(StructureTypesTooltipView_DescriptionStructured, typeName, typeFields.size())).thenReturn(expectedDescription);

        final Optional<String> actualDescription = view.getDescriptionText();

        assertTrue(actualDescription.isPresent());
        assertEquals(expectedDescription, actualDescription.get());
    }

    @Test
    public void testGetDescriptionTextWhenKindIsIncluded() {

        final String typeName = "tPerson";
        final String expectedDescription = "message";

        when(presenter.getTypeName()).thenReturn(typeName);
        when(presenter.getDataTypeKind()).thenReturn(DataTypeKind.INCLUDED);
        when(translationService.format(StructureTypesTooltipView_DescriptionIncluded, typeName)).thenReturn(expectedDescription);

        final Optional<String> actualDescription = view.getDescriptionText();

        assertTrue(actualDescription.isPresent());
        assertEquals(expectedDescription, actualDescription.get());
    }

    @Test
    public void testGetDescriptionTextWhenKindIsAnother() {

        final String typeName = "tPerson";
        when(presenter.getTypeName()).thenReturn(typeName);
        when(presenter.getDataTypeKind()).thenReturn(DataTypeKind.BUILT_IN);

        final Optional<String> actualDescription = view.getDescriptionText();

        assertFalse(actualDescription.isPresent());
    }

    @Test
    public void testSetupListeners() {

        final HTMLElement listItems = mock(HTMLElement.class);
        when(presenter.getListItems()).thenReturn(listItems);

        view.setupListeners();

        verify(listItems).addEventListener(SCROLL, view.SCROLL_LISTENER);
        verify(bodyElement).addEventListener(CLICK, view.CLICK_LISTENER);
    }

    @Test
    public void testTeardownListeners() {

        final HTMLElement listItems = mock(HTMLElement.class);
        when(presenter.getListItems()).thenReturn(listItems);

        view.teardownListeners();

        verify(listItems).removeEventListener(SCROLL, view.SCROLL_LISTENER);
        verify(bodyElement).removeEventListener(CLICK, view.CLICK_LISTENER);
    }

    @Test
    public void testIsOutsideWhenItsOutside() {

        final Event event = mock(Event.class);
        final HTMLElement target = mock(HTMLElement.class);
        event.target = target;

        when(element.contains(target)).thenReturn(false);
        doReturn(true).when(view).isTooltipVisible();

        assertTrue(view.isOutside(event));
    }

    @Test
    public void testIsOutsideWhenItsNotOutside() {

        final Event event = mock(Event.class);
        final HTMLElement target = mock(HTMLElement.class);
        event.target = target;

        when(element.contains(target)).thenReturn(true);
        doReturn(true).when(view).isTooltipVisible();

        assertFalse(view.isOutside(event));
    }

    @Test
    public void testIsOutsideWhenItsOutsideButTooltipIsNotVisible() {

        final Event event = mock(Event.class);
        final HTMLElement target = mock(HTMLElement.class);
        event.target = target;

        when(element.contains(target)).thenReturn(false);
        doReturn(false).when(view).isTooltipVisible();

        assertFalse(view.isOutside(event));
    }

    @Test
    public void testIsTooltipVisibleWhenItReturnsTrue() {
        tooltip.style.display = DISPLAY_NONE;

        assertFalse(view.isTooltipVisible());
    }

    @Test
    public void testIsTooltipVisibleWhenItReturnsFalse() {
        tooltip.style.display = DISPLAY_BLOCK;

        assertTrue(view.isTooltipVisible());
    }

    @Test
    public void testUpdateTooltipPosition() {

        final HTMLElement refElement = mock(HTMLElement.class);
        final DOMRect domRect = mock(DOMRect.class);

        domRect.x = 2;
        domRect.y = 4;
        domRect.width = 8;

        when(refElement.getBoundingClientRect()).thenReturn(domRect);

        view.updateTooltipPosition(refElement);

        assertEquals("4.0px", tooltip.style.top);
        assertEquals("30.0px", tooltip.style.left);
    }

    @Test
    public void testUpdateTooltipClassWhenItsOverflowing() {

        final HTMLElement listItems = mock(HTMLElement.class);
        final DOMRect dataTypesListRect = mock(DOMRect.class);
        final DOMRect tooltipRect = mock(DOMRect.class);
        when(presenter.getListItems()).thenReturn(listItems);

        when(listItems.getBoundingClientRect()).thenReturn(dataTypesListRect);
        when(tooltip.getBoundingClientRect()).thenReturn(tooltipRect);

        tooltipRect.y = 150;
        tooltipRect.height = 100;

        dataTypesListRect.y = 100;
        dataTypesListRect.height = 100;

        view.updateTooltipClass();

        verify(tooltip.classList).toggle("overflow", true);
    }

    @Test
    public void testUpdateTooltipClassWhenItsNotOverflowing() {

        final HTMLElement listItems = mock(HTMLElement.class);
        final DOMRect dataTypesListRect = mock(DOMRect.class);
        final DOMRect tooltipRect = mock(DOMRect.class);
        when(presenter.getListItems()).thenReturn(listItems);

        when(listItems.getBoundingClientRect()).thenReturn(dataTypesListRect);
        when(tooltip.getBoundingClientRect()).thenReturn(tooltipRect);

        tooltipRect.y = 50;
        tooltipRect.height = 100;

        dataTypesListRect.y = 100;
        dataTypesListRect.height = 100;

        view.updateTooltipClass();

        verify(tooltip.classList).toggle("overflow", false);
    }

    @Test
    public void testMakeFieldElement() {

        final DataType field = mock(DataType.class);
        final String typeName = "tPerson";
        final HTMLLIElement expectedHtmlLiElement = mock(HTMLLIElement.class);
        final HTMLLIElement htmlTypeElement = mock(HTMLLIElement.class);

        doReturn(expectedHtmlLiElement).when(view).makeHTMLLIElement();
        doReturn(htmlTypeElement).when(view).makeTypeElement(field);

        when(field.getName()).thenReturn(typeName);

        final HTMLLIElement actualHtmlLiElement = view.makeFieldElement(field);

        assertSame(expectedHtmlLiElement, actualHtmlLiElement);
        assertEquals(typeName, expectedHtmlLiElement.textContent);
        verify(expectedHtmlLiElement).appendChild(htmlTypeElement);
    }

    @Test
    public void testMakeTypeElement() {

        final DataType field = mock(DataType.class);
        final String type = "string";
        final HTMLElement expectedHtmlElement = mock(HTMLElement.class);

        doReturn(expectedHtmlElement).when(view).makeHTMLElement();

        when(field.getType()).thenReturn(type);

        final HTMLElement actualHtmlElement = view.makeTypeElement(field);

        assertEquals(expectedHtmlElement, actualHtmlElement);
        assertEquals(type, expectedHtmlElement.textContent);
    }

    @Test
    public void testUpdateTooltipAsVisible() {
        view.updateTooltipAsVisible();
        assertEquals(DISPLAY_BLOCK, tooltip.style.display);
    }

    @Test
    public void testUpdateTooltipAsHidden() {
        view.updateTooltipAsHidden();
        assertEquals(DISPLAY_NONE, tooltip.style.display);
    }
}
