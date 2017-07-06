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
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryActionInsertFactCol52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ActionInsertFactWrapperTest {

    @Mock
    private GuidedDecisionTableView.Presenter presenter;

    @Mock
    private GuidedDecisionTable52 model;

    @Mock
    private BaseDecisionTableColumnPlugin plugin;

    @Before
    public void setup() {
        doReturn(model).when(presenter).getModel();
        doReturn(presenter).when(plugin).getPresenter();
    }

    @Test
    public void testNewActionInsertFactWhenTableFormatIsExtendedEntry() throws Exception {
        doReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY).when(model).getTableFormat();

        final ActionWrapper wrapper = new ActionInsertFactWrapper(plugin);

        assertTrue(wrapper.getActionCol52() instanceof ActionInsertFactCol52);
    }

    @Test
    public void testNewActionInsertFactWhenTableFormatIsLimitedEntry() throws Exception {
        doReturn(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY).when(model).getTableFormat();

        final ActionWrapper wrapper = new ActionInsertFactWrapper(plugin);

        assertTrue(wrapper.getActionCol52() instanceof LimitedEntryActionInsertFactCol52);
    }

    @Test
    public void testCloneALimitedEntryActionInsert() throws Exception {
        doReturn(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY).when(model).getTableFormat();

        final LimitedEntryActionInsertFactCol52 column = new LimitedEntryActionInsertFactCol52();
        column.setFactField("factField");
        column.setBoundName("boundName");
        column.setValueList("valueList");
        column.setHeader("header");
        column.setInsertLogical(false);
        column.setDefaultValue(new DTCellValue52("defaultValue"));
        column.setFactType("factType");
        column.setHideColumn(false);
        column.setType("type");
        column.setValue(new DTCellValue52("value"));

        final ActionInsertFactWrapper wrapper = new ActionInsertFactWrapper(plugin,
                                                                            column);

        final ActionInsertFactCol52 clone = wrapper.getActionCol52();

        assertEquals("factField",
                     column.getFactField());
        assertEquals("boundName",
                     column.getBoundName());
        assertEquals("valueList",
                     column.getValueList());
        assertEquals("header",
                     column.getHeader());
        assertEquals(false,
                     column.isInsertLogical());
        assertEquals(new DTCellValue52("defaultValue"),
                     column.getDefaultValue());
        assertEquals("factType",
                     column.getFactType());
        assertEquals(false,
                     column.isHideColumn());
        assertEquals("type",
                     column.getType());
        assertEquals(new DTCellValue52("value"),
                     column.getValue());
        assertNotSame(column,
                      clone);
    }

    @Test
    public void testCloneAnActionInsert() throws Exception {
        doReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY).when(model).getTableFormat();

        final ActionInsertFactCol52 column = new ActionInsertFactCol52();
        column.setFactField("factField");
        column.setBoundName("boundName");
        column.setValueList("valueList");
        column.setHeader("header");
        column.setInsertLogical(false);
        column.setDefaultValue(new DTCellValue52("defaultValue"));
        column.setFactType("factType");
        column.setHideColumn(false);
        column.setType("type");

        final ActionInsertFactWrapper wrapper = new ActionInsertFactWrapper(plugin,
                                                                            column);

        final ActionInsertFactCol52 clone = wrapper.getActionCol52();

        assertEquals("factField",
                     column.getFactField());
        assertEquals("boundName",
                     column.getBoundName());
        assertEquals("valueList",
                     column.getValueList());
        assertEquals("header",
                     column.getHeader());
        assertEquals(false,
                     column.isInsertLogical());
        assertEquals(new DTCellValue52("defaultValue"),
                     column.getDefaultValue());
        assertEquals("factType",
                     column.getFactType());
        assertEquals(false,
                     column.isHideColumn());
        assertEquals("type",
                     column.getType());
        assertNotSame(column,
                      clone);
    }
}
