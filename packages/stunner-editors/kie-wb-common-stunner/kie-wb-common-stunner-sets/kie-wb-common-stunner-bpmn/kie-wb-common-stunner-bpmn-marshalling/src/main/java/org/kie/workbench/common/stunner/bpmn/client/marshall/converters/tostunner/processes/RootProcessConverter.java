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

package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.processes;

import java.util.List;

import org.eclipse.bpmn2.Process;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.BaseConverterFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.CollaborationPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.DefinitionsPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.ProcessPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.Correlation;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.diagram.CollaborationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.diagram.Correlations;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.diagram.CorrelationsValue;
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
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.RootProcessAdvancedData;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class RootProcessConverter extends BaseRootProcessConverter<BPMNDiagramImpl, DiagramSet, ProcessData, RootProcessAdvancedData, CollaborationSet> {

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
    protected DiagramSet createDiagramSet(Process process, ProcessPropertyReader e, DefinitionsPropertyReader d) {
        return new DiagramSet(new Name(process.getName()),
                              new Documentation(e.getDocumentation()),
                              new Id(process.getId()),
                              new Package(e.getPackage()),
                              new ProcessType(e.getProcessType()),
                              new Version(e.getVersion()),
                              new AdHoc(e.isAdHoc()),
                              new ProcessInstanceDescription(e.getDescription()),
                              new Imports(new ImportsValue(e.getDefaultImports(), d.getWSDLImports())),
                              new Executable(process.isIsExecutable()),
                              new SLADueDate(e.getSlaDueDate()));
    }

    @Override
    public ProcessData createProcessData(String processVariables) {
        return new ProcessData(new ProcessVariables(processVariables));
    }

    @Override
    public RootProcessAdvancedData createAdvancedData(String globalVariables, String metaDataAttributes) {
        return new RootProcessAdvancedData(new GlobalVariables(globalVariables), new MetaDataAttributes(metaDataAttributes));
    }

    @Override
    protected CollaborationSet createCollaborations(CollaborationPropertyReader collaborationPropertyReader) {
        List<Correlation> correlationList = collaborationPropertyReader.getCorrelations();
        CorrelationsValue correlationsValue = new CorrelationsValue(correlationList);
        Correlations correlations = new Correlations(correlationsValue);
        return new CollaborationSet(correlations);
    }
}
