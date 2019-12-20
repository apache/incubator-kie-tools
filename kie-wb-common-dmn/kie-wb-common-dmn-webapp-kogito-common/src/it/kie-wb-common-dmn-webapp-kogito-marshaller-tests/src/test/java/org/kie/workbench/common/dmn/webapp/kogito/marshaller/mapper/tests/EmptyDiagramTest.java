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
package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.tests;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.resources.xml.UnmarshallerXMLTests;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.kogito.client.service.KogitoClientDiagramService;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class EmptyDiagramTest extends BaseDMNTest {

    @Override
    public String getTestName() {
        return getClass().getName();
    }

    @Override
    public void run(final KogitoClientDiagramService service) throws AssertionError {
        test(service,
             UnmarshallerXMLTests.INSTANCE.empty().getText());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void doAssertions(final Diagram diagram) throws AssertionError {
        assertNotNull(diagram);

        final Graph<?, Node> graph = diagram.getGraph();
        final List<Node> nodes = StreamSupport.stream(graph.nodes().spliterator(), false).collect(Collectors.toList());
        assertEquals("Diagram has one node", 1, nodes.size());

        final Node node = nodes.get(0);
        final Object content = node.getContent();
        assertTrue("Node content is a Definition", content instanceof Definition);

        final Definition definition = (Definition) content;
        final Object definitionDefinition = definition.getDefinition();
        assertTrue("Definition definition is a DMNDiagram", definitionDefinition instanceof DMNDiagram);
    }
}
