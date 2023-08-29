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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner;

import java.util.List;

import org.eclipse.bpmn2.Definitions;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.processes.RootProcessConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.CollaborationPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.DefinitionsPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.ProcessPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.Correlation;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.BaseDiagramSet;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;

public class DefinitionsConverter {

    private final ConverterFactory converterFactory;
    private final PropertyWriterFactory propertyWriterFactory;
    private final RootProcessConverter processConverter;

    public DefinitionsConverter(ConverterFactory converterFactory, PropertyWriterFactory propertyWriterFactory) {
        this.propertyWriterFactory = propertyWriterFactory;
        this.converterFactory = converterFactory;
        this.processConverter = converterFactory.processConverter();
    }

    public DefinitionsConverter(Graph graph) {
        this.propertyWriterFactory = new PropertyWriterFactory();
        this.converterFactory = new ConverterFactory(new DefinitionsBuildingContext(graph), propertyWriterFactory);
        this.processConverter = this.converterFactory.processConverter();
    }

    public Definitions toDefinitions() {
        Definitions definitions = bpmn2.createDefinitions();

        DefinitionsPropertyWriter p = propertyWriterFactory.of(definitions);
        ProcessPropertyWriter pp = processConverter.convertProcess();

        Node<Definition<BPMNDiagram>, ?> node = converterFactory.context.firstNode();
        BPMNDiagram definition = node.getContent().getDefinition();
        BaseDiagramSet diagramSet = definition.getDiagramSet();

        p.setExporter("jBPM Process Modeler");
        p.setExporterVersion("2.0");
        p.setProcess(pp.getProcess());
        p.setDiagram(pp.getBpmnDiagram());
        p.setRelationship(pp.getRelationship());
        p.setWSDLImports(diagramSet.getImports().getValue().getWSDLImports());
        p.addAllRootElements(pp.getItemDefinitions());
        p.addAllRootElements(pp.getRootElements());
        p.addAllRootElements(pp.getInterfaces());

        List<Correlation> correlations = definition.getCollaborationSet().getCorrelations().getValue().getCorrelations();

        CollaborationPropertyWriter collaborationPropertyWriter = propertyWriterFactory.of(definitions, pp.getProcess());
        collaborationPropertyWriter.setCorrelations(correlations);

        return definitions;
    }
}
