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

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(LienzoMockitoTestRunner.class)
public class DecisionTableEditorDefinitionTest extends BaseDecisionTableEditorDefinitionTest {

    @Test
    public void testType() {
        assertThat(definition.getType()).isEqualTo(ExpressionType.DECISION_TABLE);
    }

    @Test
    public void testName() {
        assertThat(definition.getName()).isEqualTo(DMNEditorConstants.ExpressionEditor_DecisionTableExpressionType);
    }

    @Test
    public void testModelDefinition() {
        final Optional<DecisionTable> oModel = definition.getModelClass();
        assertThat(oModel).isPresent();
    }

    @Test
    public void testModelEnrichment() {
        final Optional<DecisionTable> oModel = definition.getModelClass();
        definition.enrich(Optional.empty(), decision, oModel);

        final DecisionTable model = oModel.get();
        assertBasicEnrichment(model);
        assertStandardInputClauseEnrichment(model);
        assertStandardOutputClauseEnrichment(model);
        assertStandardDecisionRuleEnrichment(model);
        assertParentHierarchyEnrichment(model);
    }

    @Test
    public void testEditor() {
        final Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>> oEditor = definition.getEditor(parent,
                                                                                                                                                 Optional.empty(),
                                                                                                                                                 decision,
                                                                                                                                                 hasName,
                                                                                                                                                 false,
                                                                                                                                                 0);

        assertThat(oEditor).isPresent();

        final GridWidget editor = oEditor.get();
        assertThat(editor).isInstanceOf(DecisionTableGrid.class);
    }

    @Test
    public void testIsUserSelectable() {
        assertThat(definition.isUserSelectable()).isTrue();
    }
}
