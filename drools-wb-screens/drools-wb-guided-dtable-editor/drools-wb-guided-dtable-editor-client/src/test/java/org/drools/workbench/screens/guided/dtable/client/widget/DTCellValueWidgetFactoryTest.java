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

package org.drools.workbench.screens.guided.dtable.client.widget;

import java.util.HashMap;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.Mock;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DTCellValueWidgetFactoryTest {

    @Mock
    DTColumnConfig52 column;

    @Mock
    GuidedDecisionTable52 model;

    @Mock
    AsyncPackageDataModelOracle oracle;

    ActionInsertFactCol52 insertFactCol52;

    DTCellValue52 cellValue;

    DTCellValueWidgetFactory factory;

    @Before
    public void setUp() throws Exception {
        ApplicationPreferences.setUp(new HashMap<String, String>() {{
            put(ApplicationPreferences.DATE_FORMAT,
                "dd-MM-yyyy");
        }});

        when(model.getTableFormat()).thenReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY);

        factory = DTCellValueWidgetFactory.getInstance(model,
                                                       oracle,
                                                       false,
                                                       false);

        insertFactCol52 = new ActionInsertFactCol52();
        cellValue = new DTCellValue52();
    }

    @Test
    public void testGetWidgetValueList() throws Exception {
        insertFactCol52.setValueList("a,b,c");
        Widget widget = factory.getWidget(insertFactCol52,
                                          cellValue);

        assertTrue(widget instanceof ListBox);
    }

    @Test
    public void testGetWidgetEnums() throws Exception {
        insertFactCol52.setFactType("Person");
        insertFactCol52.setFactField("name");
        when(oracle.hasEnums("Person",
                             "name")).thenReturn(true);
        Widget widget = factory.getWidget(insertFactCol52,
                                          cellValue);

        assertTrue(widget instanceof ListBox);
    }

    @Test
    public void testGetWidgetEnumsForBRLColumn() throws Exception {
        BRLActionVariableColumn column = mock(BRLActionVariableColumn.class);
        doReturn("Person").when(column).getFactType();
        doReturn("name").when(column).getFactField();
        when(oracle.hasEnums("Person",
                             "name")).thenReturn(true);
        IsWidget widget = factory.getWidget(column,
                                            cellValue);

        assertTrue(widget instanceof ListBox);
    }

    @Test
    public void testGeTextBoxForBRLColumn() throws Exception {
        BRLActionVariableColumn column = mock(BRLActionVariableColumn.class);
        doReturn("Person").when(column).getFactType();
        doReturn("name").when(column).getFactField();
        when(oracle.hasEnums("Person",
                             "name")).thenReturn(false);
        IsWidget widget = factory.getWidget(column,
                                            cellValue);

        assertTrue(widget instanceof TextBox);
    }

    @Test
    public void testEnumDropdownForBRLColumn() throws Exception {
        BRLConditionVariableColumn column = mock(BRLConditionVariableColumn.class);
        doReturn("Person").when(column).getFactType();
        doReturn("name").when(column).getFactField();
        when(oracle.hasEnums("Person",
                             "name")).thenReturn(true);
        IsWidget widget = factory.getWidget(column,
                                            cellValue);

        assertTrue(widget instanceof ListBox);
    }
}
