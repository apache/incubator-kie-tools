/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl;

import java.util.ArrayList;
import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.EnumSingleSelectBooleanUiColumn;
import org.junit.Test;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class BaseColumnConverterImplTest {

    @Test
    public void newSingleSelectionEnumColumnReturnsEnumSingleSelectBooleanUiColumn() {
        assertTrue(getConverter().newSingleSelectionEnumColumn("FactType",
                                                               "factField",
                                                               DataType.DataTypes.BOOLEAN,
                                                               mock(BaseColumn.class),
                                                               mock(GuidedDecisionTablePresenter.Access.class),
                                                               mock(GuidedDecisionTableView.class)) instanceof EnumSingleSelectBooleanUiColumn);
    }

    private BaseColumnConverterImpl getConverter() {
        return new BaseColumnConverterImpl() {
            @Override
            public boolean handles(BaseColumn column) {
                return false;
            }

            @Override
            public GridColumn<?> convertColumn(BaseColumn column, GuidedDecisionTablePresenter.Access access, GuidedDecisionTableView gridWidget) {
                return null;
            }

            @Override
            public List<GridColumn.HeaderMetaData> makeHeaderMetaData(BaseColumn column) {
                return new ArrayList<>();
            }
        };
    }
}