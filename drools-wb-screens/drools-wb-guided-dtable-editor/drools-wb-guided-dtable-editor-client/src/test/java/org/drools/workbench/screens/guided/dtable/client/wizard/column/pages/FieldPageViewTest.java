/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class FieldPageViewTest {

    @Mock
    private ListBox fieldList;

    @Mock
    private TextBox fieldTextBox;

    @Mock
    private Div fieldListDescription;

    @Mock
    private Div predicateFieldDescription;

    @Mock
    private Div patternWarning;

    @Mock
    private Div fieldWarning;

    @Mock
    private Div info;

    @Mock
    private TranslationService translationService;

    @Mock
    private FieldPage page;

    private FieldPageView view;

    @Before
    public void setup() {
        view = new FieldPageView(fieldList,
                                 fieldTextBox,
                                 patternWarning,
                                 translationService,
                                 fieldListDescription,
                                 predicateFieldDescription);

        view.init(page);
    }

    @Test
    public void testEnableFieldList() {

        view.enableListFieldView();

        verify(fieldList).setVisible(true);
        verify(fieldTextBox).setVisible(false);
        verify(fieldListDescription).setHidden(false);
        verify(predicateFieldDescription).setHidden(true);
    }

    @Test
    public void testEnableTextField() {

        view.enablePredicateFieldView();

        verify(fieldList).setVisible(false);
        verify(fieldTextBox).setVisible(true);
        verify(fieldListDescription).setHidden(true);
        verify(predicateFieldDescription).setHidden(false);
    }

    @Test
    public void testSetField() {

        final String field = "field";

        view.setField(field);

        verify(fieldTextBox).setText(field);
    }

    @Test
    public void onFieldTextBoxChange() {

        final String field = "field";
        final KeyUpEvent event = mock(KeyUpEvent.class);

        doReturn(field).when(fieldTextBox).getText();

        view.onFieldTextBoxChange(event);

        verify(page).setEditingCol(field);
    }
}
