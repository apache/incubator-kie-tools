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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import java.util.List;
import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase.Namespace;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.OutputClause;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionImpl;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(LienzoMockitoTestRunner.class)
public class DecisionTableEditorDefinitionEnricherTest extends BaseDecisionTableEditorDefinitionTest {

    @Test
    @SuppressWarnings("unchecked")
    public void testModelEnrichmentWhenTopLevelDecisionTableWithInputData() {
        final String uuid = UUID.uuid();
        final Node<Definition, Edge> node = new NodeImpl<>(uuid);
        final Node<Definition, Edge> sourceNode1 = new NodeImpl<>(UUID.uuid());
        final Node<Definition, Edge> sourceNode2 = new NodeImpl<>(UUID.uuid());
        final String inputData1Name = "z-inputData1";
        final String inputData2Name = "a-inputData2";
        final InputData inputData1 = new InputData();
        final InputData inputData2 = new InputData();
        inputData1.getName().setValue(inputData1Name);
        inputData2.getName().setValue(inputData2Name);
        final QName inputData1QName = new QName(Namespace.FEEL.getUri(), BuiltInType.STRING.getName());
        final QName inputData2QName = new QName(Namespace.FEEL.getUri(), BuiltInType.NUMBER.getName());
        inputData1.getVariable().setTypeRef(inputData1QName);
        inputData2.getVariable().setTypeRef(inputData2QName);

        final Definition<InputData> sourceNode1Definition = new DefinitionImpl<>(inputData1);
        final Definition<InputData> sourceNode2Definition = new DefinitionImpl<>(inputData2);
        sourceNode1.setContent(sourceNode1Definition);
        sourceNode2.setContent(sourceNode2Definition);

        final Edge edge1 = new EdgeImpl<>(UUID.uuid());
        final Edge edge2 = new EdgeImpl<>(UUID.uuid());
        edge1.setTargetNode(node);
        edge1.setSourceNode(sourceNode1);
        edge2.setTargetNode(node);
        edge2.setSourceNode(sourceNode2);

        node.getInEdges().add(edge1);
        node.getInEdges().add(edge2);
        sourceNode1.getOutEdges().add(edge1);
        sourceNode2.getOutEdges().add(edge2);

        graph.addNode(node);
        graph.addNode(sourceNode1);
        graph.addNode(sourceNode2);

        final Optional<DecisionTable> oModel = definition.getModelClass();
        definition.enrich(Optional.of(uuid), oModel);

        final DecisionTable model = oModel.get();
        assertBasicEnrichment(model);

        final List<InputClause> input = model.getInput();
        assertThat(input.size()).isEqualTo(2);
        assertThat(input.get(0).getInputExpression()).isInstanceOf(LiteralExpression.class);
        assertThat(input.get(0).getInputExpression().getText()).isEqualTo(inputData2Name);
        assertThat(input.get(0).getInputExpression().getTypeRef()).isEqualTo(inputData2QName);
        assertThat(input.get(1).getInputExpression()).isInstanceOf(LiteralExpression.class);
        assertThat(input.get(1).getInputExpression().getText()).isEqualTo(inputData1Name);
        assertThat(input.get(1).getInputExpression().getTypeRef()).isEqualTo(inputData1QName);

        assertStandardOutputClauseEnrichment(model);
        assertStandardDecisionRuleEnrichment(model, 2, 1);
        assertParentHierarchyEnrichment(model, 2, 1);
    }

    @Test
    public void testModelEnrichmentWhenTopLevelDecisionTableWithoutInputData() {
        final String uuid = UUID.uuid();
        final Node<Definition, Edge> node = new NodeImpl<>(uuid);
        graph.addNode(node);

        final Optional<DecisionTable> oModel = definition.getModelClass();
        definition.enrich(Optional.of(uuid), oModel);

        final DecisionTable model = oModel.get();
        assertBasicEnrichment(model);
        assertStandardInputClauseEnrichment(model);
        assertStandardOutputClauseEnrichment(model);
        assertStandardDecisionRuleEnrichment(model, 1, 1);
        assertParentHierarchyEnrichment(model, 1, 1);
    }

    @Test
    public void testModelEnrichmentWhenParentIsContextEntry() {
        final String NAME = "context-entry";
        final ContextEntry contextEntry = new ContextEntry();
        final InformationItem variable = new InformationItem();
        variable.getName().setValue(NAME);
        contextEntry.setVariable(variable);

        final Optional<DecisionTable> oModel = definition.getModelClass();
        oModel.get().setParent(contextEntry);

        definition.enrich(Optional.empty(), oModel);

        final DecisionTable model = oModel.get();
        assertBasicEnrichment(model);
        assertStandardInputClauseEnrichment(model);

        final List<OutputClause> output = model.getOutput();
        assertThat(output.size()).isEqualTo(1);
        assertThat(output.get(0).getName()).isEqualTo(NAME);

        assertStandardDecisionRuleEnrichment(model, 1, 1);
        assertParentHierarchyEnrichment(model, 1, 1);
    }
}
