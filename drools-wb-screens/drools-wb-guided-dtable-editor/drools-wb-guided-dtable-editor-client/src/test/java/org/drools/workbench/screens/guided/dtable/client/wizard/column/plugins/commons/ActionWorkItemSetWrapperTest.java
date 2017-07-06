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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ActionWorkItemSetWrapperTest {

    @Mock
    private BaseDecisionTableColumnPlugin plugin;

    @Mock
    private GuidedDecisionTableView.Presenter presenter;

    @Mock
    private GuidedDecisionTable52 model;

    @Before
    public void setup() {
        doReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY).when(model).getTableFormat();
        doReturn(model).when(presenter).getModel();
        doReturn(presenter).when(plugin).getPresenter();
    }

    @Test
    public void testClone() throws Exception {
        final ActionWorkItemSetFieldCol52 column = new ActionWorkItemSetFieldCol52();
        column.setFactField("factField");
        column.setBoundName("boundName");
        column.setValueList("valueList");
        column.setHeader("header");
        column.setUpdate(false);
        column.setDefaultValue(new DTCellValue52("defaultValue"));
        column.setHideColumn(false);
        column.setType("type");
        column.setParameterClassName("parameterClassName");
        column.setWorkItemName("workItemName");
        column.setWorkItemResultParameterName("workItemResultParameterName");

        final ActionWorkItemSetWrapper wrapper = new ActionWorkItemSetWrapper(plugin,
                                                                              column);

        final ActionWorkItemSetFieldCol52 clone = wrapper.getActionCol52();

        assertEquals("factField",
                     column.getFactField());
        assertEquals("boundName",
                     column.getBoundName());
        assertEquals("valueList",
                     column.getValueList());
        assertEquals("header",
                     column.getHeader());
        assertEquals(false,
                     column.isUpdate());
        assertEquals(new DTCellValue52("defaultValue"),
                     column.getDefaultValue());
        assertEquals(false,
                     column.isHideColumn());
        assertEquals("type",
                     column.getType());
        assertEquals("parameterClassName",
                     column.getParameterClassName());
        assertEquals("workItemName",
                     column.getWorkItemName());
        assertEquals("workItemResultParameterName",
                     column.getWorkItemResultParameterName());
        assertNotSame(column,
                      clone);
    }
}
