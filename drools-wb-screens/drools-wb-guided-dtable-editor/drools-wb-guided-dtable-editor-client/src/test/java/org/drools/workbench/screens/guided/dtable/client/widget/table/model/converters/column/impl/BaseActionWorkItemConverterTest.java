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
package org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl;

import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.BaseColumnConverter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public abstract class BaseActionWorkItemConverterTest {

    protected static final int WIDTH = 200;

    protected static final String HEADER = "header";

    protected static final String WID_NAME = "WID";

    @Mock
    protected GuidedDecisionTableView gridWidget;

    protected GuidedDecisionTablePresenter.Access access = new GuidedDecisionTablePresenter.Access();

    protected BaseColumnConverter converter;

    @Before
    public void setup() {
        this.converter = getConverter();
    }

    protected abstract BaseColumnConverter getConverter();

    protected abstract BaseColumn getColumn();

    protected abstract int getExpectedPriority();

    protected abstract String getExpectedColumnGroup();

    @Test
    public void checkPriority() {
        assertEquals(getExpectedPriority(),
                     converter.priority());
    }

    @Test
    public void checkHandlesSupportedColumn() {
        assertTrue(converter.handles(getColumn()));
    }

    @Test
    public void checkDoesNotHandleUnsupportedColumn() {
        assertFalse(converter.handles(mock(ConditionCol52.class)));
    }

    @Test
    public void checkConvertColumn() {
        final BaseColumn column = getColumn();

        final GridColumn<?> uiColumn = converter.convertColumn(column,
                                                               access,
                                                               gridWidget);

        assertTrue(uiColumn.isResizable());
        assertTrue(uiColumn.isVisible());
        assertEquals(WIDTH,
                     uiColumn.getWidth(),
                     0.0);

        assertEquals(2,
                     uiColumn.getHeaderMetaData().size());
        final GridColumn.HeaderMetaData row0 = uiColumn.getHeaderMetaData().get(0);
        assertEquals(WID_NAME,
                     row0.getTitle());
        assertEquals(ActionCol52.class.getName(),
                     row0.getColumnGroup());

        final GridColumn.HeaderMetaData row1 = uiColumn.getHeaderMetaData().get(1);
        assertEquals(HEADER,
                     row1.getTitle());
        assertEquals(getExpectedColumnGroup(),
                     row1.getColumnGroup());
    }
}
