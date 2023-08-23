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

package org.kie.workbench.common.dmn.client.commands.expressions.types.relation;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.client.editors.expressions.types.relation.RelationColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasGraphCommand;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

public abstract class BaseMoveCommandsTest<C extends AbstractCanvasGraphCommand> {

    protected static final String II1 = "ii1";
    protected static final String II2 = "ii2";

    @Mock
    protected RowNumberColumn uiRowNumberColumn;

    @Mock
    protected RelationColumn uiModelColumn1;

    @Mock
    protected RelationColumn uiModelColumn2;

    @Mock
    protected org.uberfire.mvp.Command canvasOperation;

    @Mock
    protected AbstractCanvasHandler handler;

    @Mock
    protected GraphCommandExecutionContext gce;

    @Mock
    protected RuleManager ruleManager;

    protected Relation relation;

    protected DMNGridData uiModel;

    protected C command;

    protected void addRelationColumn(final String identifier) {
        relation.getColumn().add(new InformationItem() {{
            setId(new Id(identifier));
        }});
    }

    protected void addRelationRow(final String identifier) {
        final List row = new List();
        for (int index = 0; index < relation.getColumn().size(); index++) {
            final String ii = makeIdentifier(identifier, index);
            row.getExpression().add(HasExpression.wrap(row,
                                                       new LiteralExpression() {{
                                                           setId(new Id(ii));
                                                       }}));
        }
        relation.getRow().add(row);
    }

    protected String makeIdentifier(final String base,
                                    final int index) {
        return base + "e" + index;
    }

    protected void addUiModelColumn(final GridColumn<?> uiColumn) {
        uiModel.appendColumn(uiColumn);
    }

    protected void addUiModelRow(final int rowIndex) {
        final GridRow uiRow = new BaseGridRow();
        uiModel.appendRow(uiRow);
        uiModel.setCellValue(rowIndex, 0, new BaseGridCellValue<>(rowIndex + 1));
    }
}
