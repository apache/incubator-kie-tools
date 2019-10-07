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

package org.drools.workbench.screens.guided.dtable.client.widget.table.columns.control;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.rule.Attribute;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WithClassesToStub(ColumnLabelWidget.class)
@RunWith(GwtMockitoTestRunner.class)
public class AttributeColumnConfigRowViewTest {

    AttributeColumnConfigRowView view;

    @Mock
    AttributeCol52 attributeColumn;

    @Mock
    DTCellValue52 defaultValue;

    @Mock
    DeleteColumnManagementAnchorWidget deleteWidget;

    @Captor
    ArgumentCaptor<Widget> widgetCaptor;

    @Before
    public void setUp() throws Exception {
        view = spy(new AttributeColumnConfigRowView(deleteWidget));

        when(attributeColumn.getAttribute()).thenReturn(Attribute.SALIENCE.getAttributeName());
        when(attributeColumn.getDefaultValue()).thenReturn(defaultValue);

        Map<String, String> preferences = new HashMap<>();
        preferences.put(ApplicationPreferences.DATE_FORMAT, "dd/mm/yy");
        ApplicationPreferences.setUp(preferences);
    }

    @Test
    public void testAddRemoveAttributeButton() throws Exception {
        view.addRemoveAttributeButton(null, false);
        verify(view).add(widgetCaptor.capture());
        assertTrue(widgetCaptor.getValue() instanceof Anchor);
    }

    @Test
    public void testAddColumnLabel() throws Exception {
        view.addColumnLabel(attributeColumn);
        verify(view).add(widgetCaptor.capture());
        assertTrue(widgetCaptor.getValue() instanceof ColumnLabelWidget);
    }

    @Test
    public void testAddDefaultValue() throws Exception {
        view.addDefaultValue(attributeColumn, true, null);
        verify(view).add(any(Widget.class));
    }

    @Test
    public void testAddUseRowNumberCheckBox() throws Exception {
        view.addUseRowNumberCheckBox(attributeColumn, true, null);
        verify(view).add(widgetCaptor.capture());
        assertTrue(widgetCaptor.getValue() instanceof CheckBox);
    }

    @Test
    public void testAddReverseOrderCheckBox() throws Exception {
        view.addReverseOrderCheckBox(attributeColumn, true, null);
        verify(view).add(widgetCaptor.capture());
        assertTrue(widgetCaptor.getValue() instanceof CheckBox);
    }

    @Test
    public void testAddHideColumnCheckBox() throws Exception {
        view.addHideColumnCheckBox(attributeColumn, null);
        verify(view).add(widgetCaptor.capture());
        assertTrue(widgetCaptor.getValue() instanceof CheckBox);
    }
}
