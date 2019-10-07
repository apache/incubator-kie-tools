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

import java.util.Optional;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.rule.Attribute;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.screens.guided.dtable.client.widget.DefaultValueWidgetFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AttributeColumnConfigRowTest {

    private AttributeColumnConfigRow columnConfigRow;

    @Mock
    AttributeCol52 attributeColumn;

    @Mock
    AttributeCol52 editedAttributeColumn;

    @Mock
    GuidedDecisionTableModellerView.Presenter presenter;

    @Mock
    AttributeColumnConfigRowView view;

    @Mock
    GuidedDecisionTableView.Presenter decisionTable;

    @Mock
    DefaultValueWidgetFactory.DefaultValueChangedEvent event;

    @Mock
    DTCellValue52 defaultValue;

    @Captor
    ArgumentCaptor<ClickHandler> clickCaptor;

    @Captor
    ArgumentCaptor<DefaultValueWidgetFactory.DefaultValueChangedEventHandler> defaultValueCaptor;

    @Mock
    CheckBox useRowNumberCheckBox;

    @Mock
    CheckBox reverseOrderCheckBox;

    @Mock
    CheckBox hideColumnCheckBox;

    @Before
    public void setUp() throws Exception {
        columnConfigRow = new AttributeColumnConfigRow();
        columnConfigRow.view = view;

        when(attributeColumn.getAttribute()).thenReturn(Attribute.SALIENCE.getAttributeName());
        when(attributeColumn.cloneColumn()).thenReturn(editedAttributeColumn);
        when(presenter.isActiveDecisionTableEditable()).thenReturn(true);
        when(presenter.getActiveDecisionTable()).thenReturn(Optional.of(decisionTable));

        when(view.addUseRowNumberCheckBox(any(), anyBoolean(), any())).thenReturn(useRowNumberCheckBox);
        when(view.addReverseOrderCheckBox(any(), anyBoolean(), any())).thenReturn(reverseOrderCheckBox);
        when(view.addHideColumnCheckBox(any(), any())).thenReturn(hideColumnCheckBox);
    }

    @Test
    public void testInit() throws Exception {
        columnConfigRow.init(attributeColumn, presenter);

        verify(view).setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        verify(view).addColumnLabel(attributeColumn);
    }

    @Test
    public void testInitUseRowNumberCheckBox() throws Exception {
        when(useRowNumberCheckBox.getValue()).thenReturn(true);
        columnConfigRow.init(attributeColumn, presenter);
        verify(view).addUseRowNumberCheckBox(eq(attributeColumn), eq(true), clickCaptor.capture());
        //Simulates that checkbox was clicked
        clickCaptor.getValue().onClick(null);
        verify(editedAttributeColumn).setUseRowNumber(true);
        verify(reverseOrderCheckBox).setEnabled(true);
        verify(decisionTable).updateColumn(attributeColumn, editedAttributeColumn);
    }

    @Test
    public void testInitUseRowNumberCheckBoxWithoutActiveDecisionTable() throws Exception {
        when(useRowNumberCheckBox.getValue()).thenReturn(true);
        when(presenter.getActiveDecisionTable()).thenReturn(Optional.empty());

        columnConfigRow.init(attributeColumn,
                             presenter);
        verify(view).addUseRowNumberCheckBox(eq(attributeColumn),
                                             eq(true),
                                             clickCaptor.capture());
        //Simulates that checkbox was clicked
        clickCaptor.getValue().onClick(null);

        verify(decisionTable,
               never()).updateColumn(any(AttributeCol52.class),
                                     any(AttributeCol52.class));
    }

    @Test
    public void testInitReverseOrder() throws Exception {
        when(reverseOrderCheckBox.getValue()).thenReturn(true);
        columnConfigRow.init(attributeColumn, presenter);
        verify(view).addReverseOrderCheckBox(eq(attributeColumn), eq(true), clickCaptor.capture());
        //Simulates that checkbox was clicked
        clickCaptor.getValue().onClick(null);
        verify(editedAttributeColumn).setReverseOrder(true);
        verify(decisionTable).updateColumn(attributeColumn, editedAttributeColumn);
    }

    @Test
    public void testInitReverseOrderWithoutActiveDecisionTable() throws Exception {
        when(reverseOrderCheckBox.getValue()).thenReturn(true);
        when(presenter.getActiveDecisionTable()).thenReturn(Optional.empty());

        columnConfigRow.init(attributeColumn,
                             presenter);
        verify(view).addReverseOrderCheckBox(eq(attributeColumn),
                                             eq(true),
                                             clickCaptor.capture());
        //Simulates that checkbox was clicked
        clickCaptor.getValue().onClick(null);

        verify(decisionTable,
               never()).updateColumn(any(AttributeCol52.class),
                                     any(AttributeCol52.class));
    }

    @Test
    public void testInitDefaultValue() throws Exception {
        when(event.getEditedDefaultValue()).thenReturn(defaultValue);
        columnConfigRow.init(attributeColumn, presenter);
        verify(view).addDefaultValue(eq(attributeColumn), eq(true), defaultValueCaptor.capture());
        //Simulates that checkbox was clicked
        defaultValueCaptor.getValue().onDefaultValueChanged(event);
        verify(editedAttributeColumn).setDefaultValue(defaultValue);
        verify(decisionTable).updateColumn(attributeColumn, editedAttributeColumn);
    }

    @Test
    public void testInitDefaultValueWithoutActiveDecisionTable() throws Exception {
        when(event.getEditedDefaultValue()).thenReturn(defaultValue);
        when(presenter.getActiveDecisionTable()).thenReturn(Optional.empty());

        columnConfigRow.init(attributeColumn,
                             presenter);
        verify(view).addDefaultValue(eq(attributeColumn),
                                     eq(true),
                                     defaultValueCaptor.capture());
        //Simulates that checkbox was clicked
        defaultValueCaptor.getValue().onDefaultValueChanged(event);

        verify(decisionTable,
               never()).updateColumn(any(AttributeCol52.class),
                                     any(AttributeCol52.class));
    }

    @Test
    public void testInitHideColumn() throws Exception {
        when(hideColumnCheckBox.getValue()).thenReturn(true);
        columnConfigRow.init(attributeColumn, presenter);
        verify(view).addHideColumnCheckBox(eq(attributeColumn), clickCaptor.capture());
        //Simulates that checkbox was clicked
        clickCaptor.getValue().onClick(null);
        verify(editedAttributeColumn).setHideColumn(true);
        verify(decisionTable).updateColumn(attributeColumn, editedAttributeColumn);
    }

    @Test
    public void testInitHideColumnWithoutActiveDecisionTable() throws Exception {
        when(hideColumnCheckBox.getValue()).thenReturn(true);
        when(presenter.getActiveDecisionTable()).thenReturn(Optional.empty());

        columnConfigRow.init(attributeColumn,
                             presenter);
        verify(view).addHideColumnCheckBox(eq(attributeColumn),
                                           clickCaptor.capture());
        //Simulates that checkbox was clicked
        clickCaptor.getValue().onClick(null);

        verify(decisionTable,
               never()).updateColumn(any(AttributeCol52.class),
                                     any(AttributeCol52.class));
    }
}
