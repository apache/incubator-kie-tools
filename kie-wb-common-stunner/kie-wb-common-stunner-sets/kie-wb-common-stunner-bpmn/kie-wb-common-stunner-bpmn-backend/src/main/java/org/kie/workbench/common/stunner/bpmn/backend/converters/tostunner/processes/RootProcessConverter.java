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

import org.eclipse.bpmn2.Process;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BaseConverterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.DefinitionsPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.ProcessPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.AdHoc;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Executable;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.GlobalVariables;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Id;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.MetaDataAttributes;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Package;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.ProcessInstanceDescription;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.ProcessType;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Version;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.Imports;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.ImportsValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.core.util.StringUtils.revertIllegalCharsAttribute;

public class RootProcessConverter extends BaseRootProcessConverter<BPMNDiagramImpl, DiagramSet, ProcessData, AdvancedData> {

    public RootProcessConverter(TypedFactoryManager typedFactoryManager,
                                PropertyReaderFactory propertyReaderFactory,
                                DefinitionResolver definitionResolver,
                                BaseConverterFactory factory) {
        super(typedFactoryManager, propertyReaderFactory, definitionResolver, factory);
    }

    @Override
    protected Node<View<BPMNDiagramImpl>, Edge> createNode(String id) {
        return delegate.factoryManager.newNode(id, BPMNDiagramImpl.class);
    }

    @Override
    protected DiagramSet createDiagramSet(Process process, ProcessPropertyReader p, DefinitionsPropertyReader d) {
        return new DiagramSet(new Name(revertIllegalCharsAttribute(process.getName())),
                              new Documentation(p.getDocumentation()),
                              new Id(revertIllegalCharsAttribute(process.getId())),
                              new Package(p.getPackage()),
                              new ProcessType(p.getProcessType()),
                              new Version(p.getVersion()),
                              new AdHoc(p.isAdHoc()),
                              new ProcessInstanceDescription(p.getDescription()),
                              new Imports(new ImportsValue(p.getDefaultImports(), d.getWSDLImports())),
                              new Executable(process.isIsExecutable()),
                              new SLADueDate(p.getSlaDueDate()));
    }

    @Override
    protected ProcessData createProcessData(String processVariables) {
        return new ProcessData(new ProcessVariables(processVariables));
    }

    @Override
    protected AdvancedData createAdvancedData(String globalVariables, String metaDataAttributes) {
        return new AdvancedData(new GlobalVariables(globalVariables), new MetaDataAttributes(metaDataAttributes));
    }
}
