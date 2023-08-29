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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties;

import java.util.Collection;
import java.util.List;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.Relationship;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.emf.ecore.impl.EAttributeImpl;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.util.PropertyWriterUtils;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.WSDLImport;

public class DefinitionsPropertyWriter {

    private final Definitions definitions;

    public DefinitionsPropertyWriter(Definitions definitions) {
        this.definitions = definitions;
        definitions.setTargetNamespace("http://www.omg.org/bpmn20");
        setSchemaLocation(definitions);
    }

    private static void setSchemaLocation(Definitions definitions) {
        ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
        EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                "xsi",
                "schemaLocation",
                false,
                false);
        EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(
                extensionAttribute,
                "http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd " +
                        "http://www.jboss.org/drools drools.xsd " +
                        "http://www.bpsim.org/schemas/1.0 bpsim.xsd " +
                        "http://www.omg.org/spec/DD/20100524/DC DC.xsd " +
                        "http://www.omg.org/spec/DD/20100524/DI DI.xsd ");
        definitions.getAnyAttribute().add(extensionEntry);
    }

    public void setExporter(String exporter) {
        definitions.setExporter(exporter);
    }

    public void setExporterVersion(String version) {
        definitions.setExporterVersion(version);
    }

    public void setProcess(Process process) {
        definitions.getRootElements().add(process);
    }

    public void setDiagram(BPMNDiagram bpmnDiagram) {
        definitions.getDiagrams().add(bpmnDiagram);
    }

    public void setRelationship(Relationship relationship) {
        relationship.getSources().add(definitions);
        relationship.getTargets().add(definitions);
        definitions.getRelationships().add(relationship);
    }

    public void setWSDLImports(List<WSDLImport> wsdlImports) {
        wsdlImports.stream()
                .map(PropertyWriterUtils::toImport)
                .forEach(definitions.getImports()::add);
    }

    public void addAllRootElements(Collection<? extends RootElement> rootElements) {
        definitions.getRootElements().addAll(rootElements);
    }

    public Definitions getDefinitions() {
        return definitions;
    }
}
