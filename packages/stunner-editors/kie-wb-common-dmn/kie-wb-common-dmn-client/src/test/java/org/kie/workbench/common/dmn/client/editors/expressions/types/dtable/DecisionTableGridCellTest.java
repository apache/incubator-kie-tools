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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseHasDynamicHeightCellTest;
import org.kie.workbench.common.dmn.client.widgets.grid.model.HasDynamicHeight;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DecisionTableGridCellTest extends BaseHasDynamicHeightCellTest<DecisionTableGridCell> {

    @Mock
    private GridCellValue<String> value;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Override
    public DecisionTableGridCell makeCell() {
        return makeCell(LINE_HEIGHT);
    }

    @Override
    protected DecisionTableGridCell makeCell(final double lineHeight) {
        return new DecisionTableGridCell<>(value,
                                           listSelector,
                                           lineHeight);
    }

    @Test
    public void testIsAHasDynamicHeightSubclass() {
        assertThat(cell).isInstanceOf(HasDynamicHeight.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetEditor() {
        assertThat(cell.getEditor()).isNotEmpty();
        assertThat(cell.getEditor().get()).isSameAs(listSelector);
    }
}
