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

package org.kie.workbench.common.dmn.client.property.dmn;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.NamedElement;
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultValueUtilitiesTest {

    private final String PREFIX = "prefix-";

    private Graph<?, Node> graph;

    @Before
    public void setup() {
        this.graph = new GraphImpl<>(UUID.randomUUID().toString(),
                                     new GraphNodeStoreImpl());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateNewNodeName_UnhandledType() {
        DefaultValueUtilities.updateNewNodeName(graph, new LiteralExpression());
    }

    @Test
    public void testUpdateNewNodeName_BusinessKnowledgeModel() {
        //Add some existing nodes to ensure naming is not affected by existing content
        graph.addNode(makeMockNode(new Decision()));
        graph.addNode(makeMockNode(new InputData()));
        graph.addNode(makeMockNode(new KnowledgeSource()));
        graph.addNode(makeMockNode(new TextAnnotation()));

        final BusinessKnowledgeModel bkm1 = new BusinessKnowledgeModel();
        final BusinessKnowledgeModel bkm2 = new BusinessKnowledgeModel();
        final BusinessKnowledgeModel bkm3 = new BusinessKnowledgeModel();
        final BusinessKnowledgeModel bkm4 = new BusinessKnowledgeModel();

        assertUpdateNewNodeName(bkm1,
                                bkm2,
                                (bkm) -> bkm.getName().getValue(),
                                () -> BusinessKnowledgeModel.class.getSimpleName() + "-1",
                                () -> BusinessKnowledgeModel.class.getSimpleName() + "-2");

        //Update existing names manually and add two more
        bkm1.getName().setValue("bkm");
        bkm2.getName().setValue(BusinessKnowledgeModel.class.getSimpleName() + "-5");
        assertUpdateNewNodeName(bkm3,
                                bkm4,
                                (bkm) -> bkm.getName().getValue(),
                                () -> BusinessKnowledgeModel.class.getSimpleName() + "-6",
                                () -> BusinessKnowledgeModel.class.getSimpleName() + "-7");
    }

    @Test
    public void testUpdateNewNodeName_Decision() {
        //Add some existing nodes to ensure naming is not affected by existing content
        graph.addNode(makeMockNode(new BusinessKnowledgeModel()));
        graph.addNode(makeMockNode(new InputData()));
        graph.addNode(makeMockNode(new KnowledgeSource()));
        graph.addNode(makeMockNode(new TextAnnotation()));

        final Decision decision1 = new Decision();
        final Decision decision2 = new Decision();
        final Decision decision3 = new Decision();
        final Decision decision4 = new Decision();

        assertUpdateNewNodeName(decision1,
                                decision2,
                                (decision) -> decision.getName().getValue(),
                                () -> Decision.class.getSimpleName() + "-1",
                                () -> Decision.class.getSimpleName() + "-2");

        //Update existing names manually and add two more
        decision1.getName().setValue("decision");
        decision2.getName().setValue(Decision.class.getSimpleName() + "-5");
        assertUpdateNewNodeName(decision3,
                                decision4,
                                (decision) -> decision.getName().getValue(),
                                () -> Decision.class.getSimpleName() + "-6",
                                () -> Decision.class.getSimpleName() + "-7");
    }

    @Test
    public void testUpdateNewNodeName_InputData() {
        //Add some existing nodes to ensure naming is not affected by existing content
        graph.addNode(makeMockNode(new BusinessKnowledgeModel()));
        graph.addNode(makeMockNode(new Decision()));
        graph.addNode(makeMockNode(new KnowledgeSource()));
        graph.addNode(makeMockNode(new TextAnnotation()));

        final InputData inputData1 = new InputData();
        final InputData inputData2 = new InputData();
        final InputData inputData3 = new InputData();
        final InputData inputData4 = new InputData();

        assertUpdateNewNodeName(inputData1,
                                inputData2,
                                (inputData) -> inputData.getName().getValue(),
                                () -> InputData.class.getSimpleName() + "-1",
                                () -> InputData.class.getSimpleName() + "-2");

        //Update existing names manually and add two more
        inputData1.getName().setValue("inputData");
        inputData2.getName().setValue(InputData.class.getSimpleName() + "-5");
        assertUpdateNewNodeName(inputData3,
                                inputData4,
                                (inputData) -> inputData.getName().getValue(),
                                () -> InputData.class.getSimpleName() + "-6",
                                () -> InputData.class.getSimpleName() + "-7");
    }

    @Test
    public void testUpdateNewNodeName_KnowledgeSource() {
        //Add some existing nodes to ensure naming is not affected by existing content
        graph.addNode(makeMockNode(new BusinessKnowledgeModel()));
        graph.addNode(makeMockNode(new Decision()));
        graph.addNode(makeMockNode(new InputData()));
        graph.addNode(makeMockNode(new TextAnnotation()));

        final KnowledgeSource knowledgeSource1 = new KnowledgeSource();
        final KnowledgeSource knowledgeSource2 = new KnowledgeSource();
        final KnowledgeSource knowledgeSource3 = new KnowledgeSource();
        final KnowledgeSource knowledgeSource4 = new KnowledgeSource();

        assertUpdateNewNodeName(knowledgeSource1,
                                knowledgeSource2,
                                (knowledgeSource) -> knowledgeSource.getName().getValue(),
                                () -> KnowledgeSource.class.getSimpleName() + "-1",
                                () -> KnowledgeSource.class.getSimpleName() + "-2");

        //Update existing names manually and add two more
        knowledgeSource1.getName().setValue("knowledgeSource");
        knowledgeSource2.getName().setValue(KnowledgeSource.class.getSimpleName() + "-5");
        assertUpdateNewNodeName(knowledgeSource3,
                                knowledgeSource4,
                                (knowledgeSource) -> knowledgeSource.getName().getValue(),
                                () -> KnowledgeSource.class.getSimpleName() + "-6",
                                () -> KnowledgeSource.class.getSimpleName() + "-7");
    }

    @Test
    public void testUpdateNewNodeName_TextAnnotation() {
        //Add some existing nodes to ensure naming is not affected by existing content
        graph.addNode(makeMockNode(new BusinessKnowledgeModel()));
        graph.addNode(makeMockNode(new Decision()));
        graph.addNode(makeMockNode(new InputData()));
        graph.addNode(makeMockNode(new KnowledgeSource()));

        final TextAnnotation textAnnotation1 = new TextAnnotation();
        final TextAnnotation textAnnotation2 = new TextAnnotation();
        final TextAnnotation textAnnotation3 = new TextAnnotation();
        final TextAnnotation textAnnotation4 = new TextAnnotation();

        assertUpdateNewNodeName(textAnnotation1,
                                textAnnotation2,
                                (textAnnotation) -> textAnnotation.getText().getValue(),
                                () -> TextAnnotation.class.getSimpleName() + "-1",
                                () -> TextAnnotation.class.getSimpleName() + "-2");

        //Update existing names manually and add two more
        textAnnotation1.getText().setValue("textAnnotation");
        textAnnotation2.getText().setValue(TextAnnotation.class.getSimpleName() + "-5");
        assertUpdateNewNodeName(textAnnotation3,
                                textAnnotation4,
                                (textAnnotation) -> textAnnotation.getText().getValue(),
                                () -> TextAnnotation.class.getSimpleName() + "-6",
                                () -> TextAnnotation.class.getSimpleName() + "-7");
    }

    @Test
    public void testUpdateNewNodeName_DecisionService() {
        //Add some existing nodes to ensure naming is not affected by existing content
        graph.addNode(makeMockNode(new BusinessKnowledgeModel()));
        graph.addNode(makeMockNode(new Decision()));
        graph.addNode(makeMockNode(new InputData()));
        graph.addNode(makeMockNode(new KnowledgeSource()));

        final DecisionService decisionService1 = new DecisionService();
        final DecisionService decisionService2 = new DecisionService();
        final DecisionService decisionService3 = new DecisionService();
        final DecisionService decisionService4 = new DecisionService();

        assertUpdateNewNodeName(decisionService1,
                                decisionService2,
                                (decisionService) -> decisionService.getName().getValue(),
                                () -> DecisionService.class.getSimpleName() + "-1",
                                () -> DecisionService.class.getSimpleName() + "-2");

        //Update existing names manually and add two more
        decisionService1.getName().setValue("decisionService");
        decisionService2.getName().setValue(DecisionService.class.getSimpleName() + "-5");
        assertUpdateNewNodeName(decisionService3,
                                decisionService4,
                                (decisionService) -> decisionService.getName().getValue(),
                                () -> DecisionService.class.getSimpleName() + "-6",
                                () -> DecisionService.class.getSimpleName() + "-7");
    }

    private <T extends DMNModelInstrumentedBase> void assertUpdateNewNodeName(final T content1,
                                                                              final T content2,
                                                                              final Function<T, String> nameExtractor,
                                                                              final Supplier<String> content1NameSupplier,
                                                                              final Supplier<String> content2NameSupplier) {
        //Add one...
        graph.addNode(makeMockNode(content1));
        DefaultValueUtilities.updateNewNodeName(graph, content1);
        assertThat(nameExtractor.apply(content1)).isEqualTo(content1NameSupplier.get());

        //...and then another
        graph.addNode(makeMockNode(content2));
        DefaultValueUtilities.updateNewNodeName(graph, content2);
        assertThat(nameExtractor.apply(content2)).isEqualTo(content2NameSupplier.get());
    }

    @Test
    public void testExtractIndex() {
        assertThat(DefaultValueUtilities.extractIndex("", PREFIX)).isNotPresent();
        assertThat(DefaultValueUtilities.extractIndex("1", PREFIX)).isNotPresent();
        assertThat(DefaultValueUtilities.extractIndex("a", PREFIX)).isNotPresent();
        assertThat(DefaultValueUtilities.extractIndex(PREFIX + "a", PREFIX)).isNotPresent();

        assertThat(DefaultValueUtilities.extractIndex(PREFIX + "1", PREFIX)).isPresent().hasValue(1);
        assertThat(DefaultValueUtilities.extractIndex(PREFIX + "55", PREFIX)).isPresent().hasValue(55);
    }

    private Node makeMockNode(final DMNModelInstrumentedBase dmnModel) {
        final View view = new ViewImpl<>(dmnModel, Bounds.create(0, 0, 0, 0));
        final Node<View, Edge> node = new NodeImpl<>(UUID.randomUUID().toString());
        node.setContent(view);
        return node;
    }

    @Test
    public void testUpdateNewNodeNameWhenNomeIsAlreadySet() {
        final String existingName = "existingName";
        final NamedElement dec = mock(NamedElement.class);
        final Name name = new Name();
        name.setValue(existingName);
        when(dec.getName()).thenReturn(name);

        DefaultValueUtilities.updateNewNodeName(graph, dec);

        assertEquals(existingName, dec.getName().getValue());
    }
}
