/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class InformationItemNameCellTest {

    private static final String VALUE1 = "value1";

    private static final String VALUE2 = "value2";

    @Mock
    private ListSelectorView.Presenter listSelector;

    private InformationItem informationItem;

    private InformationItemNameCell cell;

    @Before
    public void setup() {
        this.informationItem = new InformationItem();
        this.cell = new InformationItemNameCell(() -> informationItem,
                                                listSelector);
    }

    @Test
    public void testGetValue() {
        informationItem.getName().setValue(VALUE1);

        assertThat(cell.getValue()).isNotNull();
        assertThat(cell.getValue().getValue()).isEqualTo(VALUE1);
    }

    @Test
    public void testSetValueDoesNoOperation() {
        final BaseGridData uiModel = new BaseGridData(false);
        uiModel.appendColumn(mock(GridColumn.class));
        uiModel.appendRow(new BaseGridRow());

        informationItem.getName().setValue(VALUE1);

        uiModel.setCellValue(0, 0, new BaseGridCellValue<>(VALUE2));

        assertThat(cell.getValue()).isNotNull();
        assertThat(cell.getValue().getValue()).isEqualTo(VALUE1);
    }
}
