/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.Element;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.ConstraintType;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintComponent;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPickerEvent;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPickerTarget;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.api.definition.v1_1.ConstraintType.ENUMERATION;
import static org.kie.workbench.common.dmn.api.definition.v1_1.ConstraintType.EXPRESSION;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.HIDDEN_CSS_CLASS;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeConstraintModalViewTest {

    @Mock
    private HTMLDivElement header;

    @Mock
    private HTMLDivElement body;

    @Mock
    private HTMLDivElement footer;

    @Mock
    private HTMLDivElement componentContainer;

    @Mock
    private HTMLButtonElement okButton;

    @Mock
    private HTMLButtonElement cancelButton;

    @Mock
    private HTMLAnchorElement clearAllAnchor;

    @Mock
    private HTMLElement type;

    @Mock
    private HTMLDivElement selectConstraint;

    @Mock
    private HTMLDivElement constraintWarningMessage;

    @Mock
    private HTMLButtonElement closeConstraintWarningMessage;

    @Mock
    private DataTypeConstraintModal presenter;

    private DataTypeConstraintModalView view;

    @Before
    public void setup() {
        view = spy(new DataTypeConstraintModalView(header, body, footer, componentContainer, okButton, cancelButton, clearAllAnchor, type, selectConstraint, constraintWarningMessage, closeConstraintWarningMessage));
        view.init(presenter);
    }

    @Test
    public void testInit() {

        doNothing().when(view).setupSelectPicker();
        doNothing().when(view).setupSelectPickerOnChangeHandler();
        doNothing().when(view).setupEmptyContainer();

        view.init();

        verify(view).setupSelectPicker();
        verify(view).setupSelectPickerOnChangeHandler();
        verify(view).setupEmptyContainer();
    }

    @Test
    public void testGetHeader() {

        final String expectedHeader = "header";

        header.textContent = expectedHeader;

        final String actualHeader = view.getHeader();

        assertEquals(expectedHeader, actualHeader);
    }

    @Test
    public void testGetBody() {
        assertEquals(body, view.getBody());
    }

    @Test
    public void testGetFooter() {
        assertEquals(footer, view.getFooter());
    }

    @Test
    public void testOnOkButtonClick() {

        view.onOkButtonClick(mock(ClickEvent.class));

        verify(presenter).save();
    }

    @Test
    public void testOnCancelButtonClick() {

        view.onCancelButtonClick(mock(ClickEvent.class));

        verify(presenter).hide();
    }

    @Test
    public void testOnClearAllAnchorClick() {

        view.onClearAllAnchorClick(mock(ClickEvent.class));

        verify(presenter).clearAll();
    }

    @Test
    public void testSetType() {

        final String expectedText = "type";

        this.type.textContent = "something";

        view.setType(expectedText);

        final String actualText = this.type.textContent;

        assertEquals(expectedText, actualText);
    }

    @Test
    public void testOnSelectChangeWhenValueIsNotBlank() {

        final JQuerySelectPickerEvent jQueryEvent = mock(JQuerySelectPickerEvent.class);
        final JQuerySelectPickerTarget pickerTarget = mock(JQuerySelectPickerTarget.class);
        final ConstraintType constraintType = ENUMERATION;

        doNothing().when(view).loadComponent(ENUMERATION);
        jQueryEvent.target = pickerTarget;
        pickerTarget.value = constraintType.value();

        view.onSelectChange(jQueryEvent);

        verify(view).loadComponent(constraintType);
    }

    @Test
    public void testSetupEmptyContainer() {

        componentContainer.innerHTML = "something";

        view.setupEmptyContainer();

        assertEquals("", componentContainer.innerHTML);
        verify(componentContainer).appendChild(selectConstraint);
    }

    @Test
    public void testLoadComponent() {

        final ConstraintType constraintType = ENUMERATION;
        final DataTypeConstraintComponent constrainComponent = mock(DataTypeConstraintComponent.class);
        final Element element = mock(Element.class);

        when(presenter.getCurrentComponent()).thenReturn(constrainComponent);
        when(constrainComponent.getElement()).thenReturn(element);
        componentContainer.innerHTML = "something";

        view.loadComponent(constraintType);

        assertEquals("", componentContainer.innerHTML);
        verify(presenter).setupComponent(constraintType);
        verify(componentContainer).appendChild(element);
    }

    @Test
    public void testOnShowWhenConstraintValueIsBlank() {

        final Element selectPicker = mock(HTMLElement.class);

        when(presenter.getConstraintValue()).thenReturn(null);
        when(presenter.inferComponentType(any())).thenCallRealMethod();
        doReturn(selectPicker).when(view).getSelectPicker();

        view.onShow();

        verify(view).setPickerValue(selectPicker, EXPRESSION.value());
    }

    @Test
    public void testOnShowWhenConstraintValueIsNotBlank() {

        final Element selectPicker = mock(HTMLElement.class);
        final String constraint = "1,2,3";

        when(presenter.getConstraintValue()).thenReturn(constraint);
        when(presenter.inferComponentType(constraint)).thenReturn(ENUMERATION);
        doReturn(selectPicker).when(view).getSelectPicker();

        view.onShow();

        verify(view).setPickerValue(selectPicker, ENUMERATION.value());
    }

    @Test
    public void testSetupSelectPicker() {

        final Element element = mock(Element.class);
        doReturn(element).when(view).getSelectPicker();

        view.setupSelectPicker();

        verify(view).triggerPickerAction(element, "refresh");
    }

    @Test
    public void testSetupSelectPickerOnChangeHandler() {

        final Element element = mock(Element.class);
        doReturn(element).when(view).getSelectPicker();

        view.setupSelectPickerOnChangeHandler();

        verify(view).setupOnChangeHandler(element);
    }

    @Test
    public void testGetSelectPicker() {

        final HTMLElement expectedSelect = mock(HTMLElement.class);

        when(body.querySelector(".selectpicker")).thenReturn(expectedSelect);

        final Element actualSelect = view.getSelectPicker();

        assertEquals(expectedSelect, actualSelect);
    }

    @Test
    public void testOnCloseConstraintWarningClick() {
        constraintWarningMessage.classList = mock(DOMTokenList.class);

        view.onCloseConstraintWarningClick(mock(ClickEvent.class));

        verify(constraintWarningMessage.classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testShowConstraintWarningMessage() {
        constraintWarningMessage.classList = mock(DOMTokenList.class);

        view.showConstraintWarningMessage();

        verify(constraintWarningMessage.classList).remove(HIDDEN_CSS_CLASS);
    }
}
