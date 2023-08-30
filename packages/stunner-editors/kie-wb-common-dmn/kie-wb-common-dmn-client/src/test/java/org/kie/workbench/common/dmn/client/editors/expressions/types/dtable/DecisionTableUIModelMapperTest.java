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

import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.InputClause;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.OutputClause;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClause;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClauseText;
import org.kie.workbench.common.dmn.api.definition.model.UnaryTests;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl.RowSelectionStrategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.widgets.grid.model.BaseHasDynamicHeightCell.DEFAULT_HEIGHT;
import static org.mockito.Mockito.doReturn;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableUIModelMapperTest {

    @Mock
    private DecisionTableRowNumberColumn uiRowNumberColumn;

    @Mock
    private InputClauseColumn uiInputClauseColumn;

    @Mock
    private OutputClauseColumn uiOutputClauseColumn;

    @Mock
    private RuleAnnotationClauseColumn uiAnnotationClauseColumn;

    @Mock
    private ListSelectorView.Presenter listSelector;

    private BaseGridData uiModel;

    private DecisionTable dtable;

    private DecisionTableUIModelMapper mapper;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.uiModel = new BaseGridData();
        this.uiModel.appendRow(new BaseGridRow());
        this.uiModel.appendRow(new BaseGridRow());
        this.uiModel.appendColumn(uiRowNumberColumn);
        this.uiModel.appendColumn(uiInputClauseColumn);
        this.uiModel.appendColumn(uiOutputClauseColumn);
        this.uiModel.appendColumn(uiAnnotationClauseColumn);
        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiInputClauseColumn).getIndex();
        doReturn(2).when(uiOutputClauseColumn).getIndex();
        doReturn(3).when(uiAnnotationClauseColumn).getIndex();

        this.dtable = new DecisionTable();
        this.dtable.getInput().add(new InputClause());
        this.dtable.getOutput().add(new OutputClause());
        this.dtable.getAnnotations().add(new RuleAnnotationClause());
        this.dtable.getRule().add(new DecisionRule() {
            {
                getInputEntry().add(new UnaryTests() {{
                    getText().setValue("i1");
                }});
                getOutputEntry().add(new LiteralExpression() {{
                    getText().setValue("o1");
                }});
                getAnnotationEntry().add(new RuleAnnotationClauseText() {{
                    getText().setValue("a1");
                }});
            }
        });
        this.dtable.getRule().add(new DecisionRule() {
            {
                getInputEntry().add(new UnaryTests() {{
                    getText().setValue("i2");
                }});
                getOutputEntry().add(new LiteralExpression() {{
                    getText().setValue("o2");
                }});
                getAnnotationEntry().add(new RuleAnnotationClauseText() {{
                    getText().setValue("a2");
                }});
            }
        });

        this.mapper = new DecisionTableUIModelMapper(() -> uiModel,
                                                     () -> Optional.of(dtable),
                                                     listSelector,
                                                     DEFAULT_HEIGHT);
    }

    @Test
    public void testFromDMNModelRowNumber() {
        mapper.fromDMNModel(0, 0);
        mapper.fromDMNModel(1, 0);

        assertEquals(1,
                     uiModel.getCell(0, 0).getValue().getValue());
        assertEquals(RowSelectionStrategy.INSTANCE,
                     uiModel.getCell(0, 0).getSelectionStrategy());
        assertTrue(uiModel.getCell(0, 0) instanceof DecisionTableGridCell);

        assertEquals(2,
                     uiModel.getCell(1, 0).getValue().getValue());
        assertEquals(RowSelectionStrategy.INSTANCE,
                     uiModel.getCell(1, 0).getSelectionStrategy());
        assertTrue(uiModel.getCell(1, 0) instanceof DecisionTableGridCell);
    }

    @Test
    public void testFromDMNModelInputClause() {
        mapper.fromDMNModel(0, 1);
        mapper.fromDMNModel(1, 1);

        assertEquals("i1",
                     uiModel.getCell(0, 1).getValue().getValue());
        assertTrue(uiModel.getCell(0, 1) instanceof DecisionTableGridCell);

        assertEquals("i2",
                     uiModel.getCell(1, 1).getValue().getValue());
        assertTrue(uiModel.getCell(1, 1) instanceof DecisionTableGridCell);
    }

    @Test
    public void testFromDMNModelOutputClause() {
        mapper.fromDMNModel(0, 2);
        mapper.fromDMNModel(1, 2);

        assertEquals("o1",
                     uiModel.getCell(0, 2).getValue().getValue());
        assertTrue(uiModel.getCell(0, 2) instanceof DecisionTableGridCell);

        assertEquals("o2",
                     uiModel.getCell(1, 2).getValue().getValue());
        assertTrue(uiModel.getCell(1, 2) instanceof DecisionTableGridCell);
    }

    @Test
    public void testFromDMNModelRuleAnnotationClause() {
        mapper.fromDMNModel(0, 3);
        mapper.fromDMNModel(1, 3);

        assertEquals("a1",
                     uiModel.getCell(0, 3).getValue().getValue());
        assertTrue(uiModel.getCell(0, 3) instanceof DecisionTableGridCell);

        assertEquals("a2",
                     uiModel.getCell(1, 3).getValue().getValue());
        assertTrue(uiModel.getCell(1, 3) instanceof DecisionTableGridCell);
    }

    @Test
    public void testToDMNModelInputClause() {
        mapper.toDMNModel(0, 1, Optional::empty);
        mapper.toDMNModel(1, 1, () -> Optional.of(new BaseGridCellValue<>("value")));

        assertEquals("",
                     dtable.getRule().get(0).getInputEntry().get(0).getText().getValue());

        assertEquals("value",
                     dtable.getRule().get(1).getInputEntry().get(0).getText().getValue());
    }

    @Test
    public void testToDMNModelOutputClause() {
        mapper.toDMNModel(0, 2, Optional::empty);
        mapper.toDMNModel(1, 2, () -> Optional.of(new BaseGridCellValue<>("value")));

        assertEquals("",
                     dtable.getRule().get(0).getOutputEntry().get(0).getText().getValue());

        assertEquals("value",
                     dtable.getRule().get(1).getOutputEntry().get(0).getText().getValue());
    }

    @Test
    public void testToDMNModelRuleAnnotationClause() {
        mapper.toDMNModel(0, 3, Optional::empty);
        mapper.toDMNModel(1, 3, () -> Optional.of(new BaseGridCellValue<>("value")));

        assertEquals("",
                     dtable.getRule().get(0).getAnnotationEntry().get(0).getText().getValue());

        assertEquals("value",
                     dtable.getRule().get(1).getAnnotationEntry().get(0).getText().getValue());
    }
}
