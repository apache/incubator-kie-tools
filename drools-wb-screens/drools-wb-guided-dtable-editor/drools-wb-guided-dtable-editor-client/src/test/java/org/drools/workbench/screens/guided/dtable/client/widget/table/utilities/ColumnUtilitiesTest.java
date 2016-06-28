/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget.table.utilities;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ColumnUtilitiesTest {

    @Mock
    GuidedDecisionTable52 model;
    @Mock
    AsyncPackageDataModelOracle oracle;

    Pattern52 pattern;
    ConditionCol52 column;
    ColumnUtilities utilities;

    @Before
    public void setUp() {
        utilities = new ColumnUtilities(model, oracle);
        pattern = new Pattern52();
        column = new ConditionCol52();
        when(model.getPattern(column)).thenReturn(pattern);
    }

    @Test
    public void getTypeSafeType_operatorIn() {
        column.setOperator("in");
        check();
    }

    @Test
    public void getTypeSafeType_operatorNotIn() {
        column.setOperator("not in");
        check();
    }

    private void check() {
        assertEquals(DataType.DataTypes.STRING, utilities.getTypeSafeType(pattern, column));
        assertEquals(DataType.DataTypes.STRING, utilities.getTypeSafeType((BaseColumn)column));
        assertEquals(DataType.TYPE_STRING, utilities.getType((BaseColumn)column));
        verify(oracle, never()).getFieldType(anyString(), anyString());
    }
}