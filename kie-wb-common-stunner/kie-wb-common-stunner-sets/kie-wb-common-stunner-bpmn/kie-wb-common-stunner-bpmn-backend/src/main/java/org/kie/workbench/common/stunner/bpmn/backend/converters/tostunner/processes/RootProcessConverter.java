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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.processes;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.bpmn2.Process;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.ConverterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.ProcessPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseFileVariables;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseManagementSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseRoles;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.AdHoc;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Executable;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Id;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Package;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.ProcessInstanceDescription;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Version;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

/**
 * Convert the root Process with all its children to a BPMNDiagram
 */
public class RootProcessConverter {

    private final ProcessConverterDelegate delegate;

    public RootProcessConverter(
            TypedFactoryManager typedFactoryManager,
            PropertyReaderFactory propertyReaderFactory,
            DefinitionResolver definitionResolver,
            ConverterFactory factory) {

        this.delegate = new ProcessConverterDelegate(
                typedFactoryManager,
                propertyReaderFactory,
                definitionResolver,
                factory);
    }

    public BpmnNode convertProcess() {
        Process process = delegate.definitionResolver.getProcess();
        String definitionsId = delegate.definitionResolver.getDefinitions().getId();
        BpmnNode processRoot = convertProcessNode(definitionsId, process);

        Map<String, BpmnNode> nodes =
                delegate.convertChildNodes(
                        processRoot,
                        process.getFlowElements(),
                        process.getLaneSets());

        delegate.convertEdges(
                processRoot,
                Stream.concat(process.getFlowElements().stream(),
                              process.getArtifacts().stream()).collect(Collectors.toList()),
                nodes);

        return processRoot;
    }

    private BpmnNode convertProcessNode(String id, Process process) {
        Node<View<BPMNDiagramImpl>, Edge> diagramNode =
                delegate.factoryManager.newNode(id, BPMNDiagramImpl.class);
        BPMNDiagramImpl definition = diagramNode.getContent().getDefinition();

        ProcessPropertyReader e = delegate.propertyReaderFactory.of(process);

        definition.setDiagramSet(new DiagramSet(
                new Name(process.getName()),
                new Documentation(e.getDocumentation()),
                new Id(process.getId()),
                new Package(e.getPackage()),
                new Version(e.getVersion()),
                new AdHoc(e.isAdHoc()),
                new ProcessInstanceDescription(e.getDescription()),
                new Executable(process.isIsExecutable()))
        );

        definition.setCaseManagementSet(new CaseManagementSet(
                new CaseRoles(e.getCaseRoles()),
                new CaseFileVariables(e.getCaseFileVariables()))
        );

        definition.setProcessData(new ProcessData(
                new ProcessVariables(e.getProcessVariables())
        ));

        diagramNode.getContent().setBounds(e.getBounds());

        definition.setFontSet(e.getFontSet());
        definition.setBackgroundSet(e.getBackgroundSet());

        return BpmnNode.of(diagramNode);
    }
}