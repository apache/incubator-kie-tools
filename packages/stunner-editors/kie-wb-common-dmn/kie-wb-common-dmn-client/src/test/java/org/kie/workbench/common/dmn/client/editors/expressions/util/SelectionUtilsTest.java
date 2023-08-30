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
package org.kie.workbench.common.dmn.client.editors.expressions.util;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SelectionUtilsTest {

    @Mock
    private GridColumn uiColumn0;

    @Mock
    private GridColumn uiColumn1;

    @Mock
    private GridColumn.HeaderMetaData headerMetaData;

    private GridData uiModel;

    @Before
    public void setup() {
        this.uiModel = new DMNGridData();
        this.uiModel.appendColumn(uiColumn0);
        this.uiModel.appendColumn(uiColumn1);
        this.uiModel.appendRow(new BaseGridRow());
        this.uiModel.appendRow(new BaseGridRow());

        when(uiColumn0.getIndex()).thenReturn(0);
        when(uiColumn1.getIndex()).thenReturn(1);
        when(uiColumn0.getHeaderMetaData()).thenReturn(Collections.singletonList(headerMetaData));
        when(uiColumn1.getHeaderMetaData()).thenReturn(Collections.singletonList(headerMetaData));
    }

    @Test
    public void testIsMultiSelectZeroSelections() {
        assertThat(SelectionUtils.isMultiSelect(uiModel)).isFalse();
    }

    @Test
    public void testIsMultiSelectSingleSelection() {
        uiModel.selectCell(0, 0);

        assertThat(SelectionUtils.isMultiSelect(uiModel)).isFalse();
    }

    @Test
    public void testIsMultiSelectMultipleSelections() {
        uiModel.selectCell(0, 0);
        uiModel.selectCell(0, 1);

        assertThat(SelectionUtils.isMultiSelect(uiModel)).isTrue();
    }

    @Test
    public void testIsMultiRowZeroSelections() {
        assertThat(SelectionUtils.isMultiRow(uiModel)).isFalse();
    }

    @Test
    public void testIsMultiRowSingleSelection() {
        uiModel.selectCell(0, 0);

        assertThat(SelectionUtils.isMultiRow(uiModel)).isFalse();
    }

    @Test
    public void testIsMultiRowMultipleSelections() {
        uiModel.selectCell(0, 0);
        uiModel.selectCell(1, 0);

        assertThat(SelectionUtils.isMultiRow(uiModel)).isTrue();
    }

    @Test
    public void testIsMultiColumnZeroSelections() {
        assertThat(SelectionUtils.isMultiColumn(uiModel)).isFalse();
    }

    @Test
    public void testIsMultiColumnSingleSelection() {
        uiModel.selectCell(0, 0);

        assertThat(SelectionUtils.isMultiColumn(uiModel)).isFalse();
    }

    @Test
    public void testIsMultiColumnMultipleSelections() {
        uiModel.selectCell(0, 0);
        uiModel.selectCell(0, 1);

        assertThat(SelectionUtils.isMultiColumn(uiModel)).isTrue();
    }

    @Test
    public void testIsMultiHeaderColumnZeroSelections() {
        assertThat(SelectionUtils.isMultiHeaderColumn(uiModel)).isFalse();
    }

    @Test
    public void testIsMultiHeaderColumnSingleSelection() {
        uiModel.selectHeaderCell(0, 0);

        assertThat(SelectionUtils.isMultiHeaderColumn(uiModel)).isFalse();
    }

    @Test
    public void testIsMultiHeaderColumnMultipleSelections() {
        uiModel.selectHeaderCell(0, 0);
        uiModel.selectHeaderCell(0, 1);

        assertThat(SelectionUtils.isMultiHeaderColumn(uiModel)).isTrue();
    }
}
