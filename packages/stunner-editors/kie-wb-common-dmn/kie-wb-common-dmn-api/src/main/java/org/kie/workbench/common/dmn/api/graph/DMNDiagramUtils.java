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

package org.kie.workbench.common.dmn.api.graph;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

@Dependent
public class DMNDiagramUtils {

    private static Definitions NONE = null;

    public DMNDiagramUtils() {
        //CDI proxy
    }

    public List<DRGElement> getDRGElements(final Diagram diagram) {
        return getDefinitionStream(diagram)
                .filter(d -> d instanceof DRGElement)
                .map(d -> (DRGElement) d)
                .collect(Collectors.toList());
    }

    public String getNamespace(final Diagram diagram) {
        return getDefinitionStream(diagram)
                .filter(d -> d instanceof DMNDiagram)
                .map(d -> (DMNDiagram) d)
                .findFirst()
                .map(DMNDiagram::getDefinitions)
                .map(definitions -> definitions.getNamespace().getValue())
                .orElse("");
    }

    public Definitions getDefinitions(final Diagram diagram) {
        return getDefinitionStream(diagram)
                .filter(d -> d instanceof DMNDiagram)
                .map(d -> (DMNDiagram) d)
                .findFirst()
                .map(DMNDiagram::getDefinitions)
                .orElse(NONE);
    }

    public Stream<Object> getDefinitionStream(final Diagram diagram) {
        return getNodeStream(diagram)
                .map(Node::getContent)
                .filter(c -> c instanceof Definition)
                .map(c -> (Definition) c)
                .map(Definition::getDefinition);
    }

    @SuppressWarnings("unchecked")
    public Stream<Node> getNodeStream(final Diagram diagram) {
        final Graph<?, Node> graph = diagram.getGraph();
        return StreamSupport.stream(graph.nodes().spliterator(), false);
    }
}
