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

package org.kie.workbench.common.stunner.cm.factory;

import java.util.function.Function;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.factory.AbstractBPMNDiagramFactory;
import org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.core.diagram.AbstractDiagram;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;

@Dependent
public class CaseManagementDiagramFactoryImpl
        extends AbstractBPMNDiagramFactory<Metadata, Diagram<Graph, Metadata>>
        implements CaseManagementDiagramFactory {

    public CaseManagementDiagramFactoryImpl() {
        setDiagramType(CaseManagementDiagram.class);
    }

    @Override
    protected Class<?> getDefinitionSetType() {
        return CaseManagementDefinitionSet.class;
    }

    @Override
    public Class<? extends Metadata> getMetadataType() {
        return Metadata.class;
    }

    @Override
    public Diagram<Graph, Metadata> doBuild(final String name, final Metadata metadata, final Graph<DefinitionSet, ?> graph) {

        final AbstractDiagram<Graph, Metadata> result = new DiagramImpl(name, metadata);
        result.setGraph(graph);
        return result;
    }

    @Override
    protected void setDiagramProvider(Function<Graph, Node<Definition<BPMNDiagram>, ?>> diagramProvider) {
        super.setDiagramProvider(diagramProvider);
    }
}
