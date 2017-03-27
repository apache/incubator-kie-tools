/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.cm.project.factory;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.project.factory.impl.BPMNProjectDiagramFactory;
import org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet;
import org.kie.workbench.common.stunner.cm.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.cm.util.CaseManagementUtils;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;

@ApplicationScoped
public class CaseManagementProjectDiagramFactory
        extends BPMNProjectDiagramFactory {

    @Override
    protected Class<?> getDefinitionSetType() {
        return CaseManagementDefinitionSet.class;
    }

    @Override
    protected DiagramSet getDiagramSet(final Graph<DefinitionSet, ?> graph) {
        final Node<Definition<BPMNDiagram>, ?> diagramNode = getFirstDiagramNode(graph);
        if (null == diagramNode) {
            throw new IllegalStateException("A BPMN Diagram is expected to be present on BPMN Diagram graphs.");
        }
        final BPMNDiagram diagram = diagramNode.getContent().getDefinition();
        return diagram.getDiagramSet();
    }

    @SuppressWarnings("unchecked")
    private Node<Definition<BPMNDiagram>, ?> getFirstDiagramNode(final Graph graph) {
        return CaseManagementUtils.getFirstDiagramNode(graph);
    }
}