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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.processes;

import org.eclipse.bpmn2.Process;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.ConverterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.DefinitionsBuildingContext;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.ProcessPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseFileVariables;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseIdPrefix;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseRoles;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.BaseDiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.BaseProcessData;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class RootProcessConverter {

    private final ProcessConverterDelegate delegate;
    private final DefinitionsBuildingContext context;
    private final PropertyWriterFactory propertyWriterFactory;

    public RootProcessConverter(DefinitionsBuildingContext context,
                                PropertyWriterFactory propertyWriterFactory,
                                ConverterFactory converterFactory) {
        this.delegate = new ProcessConverterDelegate(converterFactory);
        this.context = context;
        this.propertyWriterFactory = propertyWriterFactory;
    }

    public ProcessPropertyWriter convertProcess() {
        ProcessPropertyWriter processRoot = convertProcessNode(context.firstNode());

        delegate.convertChildNodes(processRoot, context);
        delegate.convertEdges(processRoot, context);
        delegate.postConvertChildNodes(processRoot, context);
        return processRoot;
    }

    private ProcessPropertyWriter convertProcessNode(Node<Definition<BPMNDiagram>, ?> node) {
        Process process = bpmn2.createProcess();

        ProcessPropertyWriter p = propertyWriterFactory.of(process);
        BPMNDiagram definition = node.getContent().getDefinition();

        BaseDiagramSet diagramSet = definition.getDiagramSet();

        p.setName(diagramSet.getName().getValue());
        p.setDocumentation(diagramSet.getDocumentation().getValue());

        p.setId(diagramSet.getId().getValue());
        p.setPackage(diagramSet.getPackageProperty().getValue());
        p.setVersion(diagramSet.getVersion().getValue());
        p.setAdHoc(diagramSet.getAdHoc().getValue());
        p.setDescription(diagramSet.getProcessInstanceDescription().getValue());
        p.setGlobalVariables(diagramSet.getGlobalVariables());
        p.setExecutable(diagramSet.getExecutable().getValue());
        p.setSlaDueDate(diagramSet.getSlaDueDate());

        BaseProcessData processData = definition.getProcessData();
        p.setProcessVariables(processData.getProcessVariables());

        //Case Management
        final CaseIdPrefix caseIdPrefix = definition.getCaseManagementSet().getCaseIdPrefix();
        p.setCaseIdPrefix(caseIdPrefix);

        final CaseRoles caseRoles = definition.getCaseManagementSet().getCaseRoles();
        p.setCaseRoles(caseRoles);

        final CaseFileVariables caseFileVariables = definition.getCaseManagementSet().getCaseFileVariables();
        p.setCaseFileVariables(caseFileVariables);

        return p;
    }
}