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


package org.kie.workbench.common.stunner.bpmn.client.documentation.decorator;

import java.util.Optional;
import java.util.function.Supplier;

import org.kie.workbench.common.stunner.bpmn.definition.property.event.compensation.ActivityRef;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class ActivityRefDecorator implements PropertyDecorator {

    private final ActivityRef activityRef;
    private final Supplier<Diagram> diagram;
    private final DefinitionUtils definitionUtils;

    public ActivityRefDecorator(final ActivityRef activityRef, final Supplier<Diagram> diagram,
                                final DefinitionUtils definitionUtils) {
        this.activityRef = activityRef;
        this.diagram = diagram;
        this.definitionUtils = definitionUtils;
    }

    @Override
    public String getValue() {
        final Graph graph = diagram.get().getGraph();
        final String propertyValue = activityRef.getValue();
        final Node node = graph.getNode(propertyValue);
        final String value = Optional.ofNullable(node)
                .map(Node::getContent)
                .filter(c -> c instanceof Definition)
                .map(c -> (Definition) c)
                .map(Definition::getDefinition)
                .map(def -> definitionUtils.getName(def))
                .orElse(propertyValue);
        return value;
    }
}
