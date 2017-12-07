/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLDivElement;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTablePopoverUtils;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class FieldPageViewTest {

    @Mock
    private ListBox fieldList;

    @Mock
    private TextBox fieldTextBox;

    @Mock
    private HTMLDivElement fieldListDescription;

    @Mock
    private HTMLDivElement predicateFieldDescription;

    @Mock
    private HTMLDivElement patternWarning;

    @Mock
    private HTMLDivElement fieldWarning;

    @Mock
    private HTMLDivElement info;

    @Mock
    private HTMLDivElement bindingContainer;

    @Mock
    private HTMLDivElement bindingFieldContainer;

    @Mock
    private HTMLDivElement fieldBindingWarning;

    @Mock
    private HTMLDivElement predicateBindingInfo;

    @Mock
    private Elemental2DomUtil elemental2DomUtil;

    @Mock
    private DecisionTablePopoverUtils popoverUtils;

    @Mock
    private TranslationService translationService;

    @Mock
    private FieldPage page;

    private FieldPageView view;

    @Before
    public void setup() {
        view = new FieldPageView(fieldList,
                                 fieldTextBox,
                                 fieldWarning,
                                 patternWarning,
                                 fieldListDescription,
                                 predicateFieldDescription,
                                 bindingContainer,
                                 bindingFieldContainer,
                                 fieldBindingWarning,
                                 predicateBindingInfo,
                                 translationService,
                                 elemental2DomUtil,
                                 popoverUtils);

        view.init(page);
    }

    @Test
    public void testEnableFieldList() {

        view.enableListFieldView();

        verify(fieldList).setVisible(true);
        verify(fieldTextBox).setVisible(false);

        assertFalse(fieldListDescription.hidden);
        assertTrue(predicateFieldDescription.hidden);
    }

    @Test
    public void testEnableTextField() {

        view.enablePredicateFieldView();

        verify(fieldList).setVisible(false);
        verify(fieldTextBox).setVisible(true);

        assertTrue(fieldListDescription.hidden);
        assertFalse(predicateFieldDescription.hidden);
    }

    @Test
    public void testSetField() {

        final String field = "field";

        view.setField(field);

        verify(fieldTextBox).setText(field);
    }

    @Test
    public void testOnFieldTextBoxChange() {

        final String field = "field";
        final KeyUpEvent event = mock(KeyUpEvent.class);

        doReturn(field).when(fieldTextBox).getText();

        view.onFieldTextBoxChange(event);

        verify(page).setEditingCol(field);
    }

    @Test
    public void testSetupBinding() {

        final IsWidget isWidget = mock(IsWidget.class);
        final Widget widget = mock(Widget.class);

        doReturn(widget).when(isWidget).asWidget();

        view.setupBinding(isWidget);

        verify(elemental2DomUtil).removeAllElementChildren(bindingFieldContainer);
        verify(elemental2DomUtil).appendWidgetToElement(bindingFieldContainer, widget);
    }

    @Test
    public void testShowFieldBindingWarning() {

        view.showFieldBindingWarning();

        assertFalse(fieldBindingWarning.hidden);
    }

    @Test
    public void testHideFieldBindingWarning() {

        view.hideFieldBindingWarning();

        assertTrue(fieldBindingWarning.hidden);
    }

    @Test
    public void testShowPredicateBindingInfo() {

        view.showPredicateBindingInfo();

        assertFalse(predicateBindingInfo.hidden);
    }

    @Test
    public void testHidePredicateBindingInfo() {

        view.hidePredicateBindingInfo();

        assertTrue(predicateBindingInfo.hidden);
    }

    @Test
    public void testBindingToggleWhenItIsVisible() {

        final boolean isVisible = true;

        view.bindingToggle(isVisible);

        assertFalse(bindingContainer.hidden);
    }

    @Test
    public void testBindingToggleWhenItIsNotVisible() {

        final boolean isVisible = false;

        view.bindingToggle(isVisible);

        assertTrue(bindingContainer.hidden);
    }
}
