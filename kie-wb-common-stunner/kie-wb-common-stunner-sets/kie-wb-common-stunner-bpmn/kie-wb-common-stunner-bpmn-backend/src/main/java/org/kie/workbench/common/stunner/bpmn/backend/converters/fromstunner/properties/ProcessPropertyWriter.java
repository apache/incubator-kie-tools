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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import bpsim.BPSimDataType;
import bpsim.BpsimPackage;
import bpsim.ElementParameters;
import bpsim.Scenario;
import bpsim.ScenarioParameters;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.LaneSet;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.Relationship;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.dd.di.DiagramElement;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.DeclarationList;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.ElementContainer;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseFileVariables;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseIdPrefix;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseRoles;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.BaseProcessVariables;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpsim;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.di;

public class ProcessPropertyWriter extends BasePropertyWriter implements ElementContainer {

    private static final String defaultRelationshipType = "BPSimData";
    private final Process process;
    private final BPMNDiagram bpmnDiagram;
    private final BPMNPlane bpmnPlane;
    private Map<String, BasePropertyWriter> childElements = new HashMap<>();
    private Collection<ElementParameters> simulationParameters = new ArrayList<>();

    public ProcessPropertyWriter(Process process, VariableScope variableScope) {
        super(process, variableScope);
        this.process = process;

        this.bpmnDiagram = di.createBPMNDiagram();
        bpmnDiagram.setId(process.getId());

        this.bpmnPlane = di.createBPMNPlane();
        bpmnDiagram.setPlane(bpmnPlane);
    }

    public void setId(String value) {
        // ids should be properly sanitized at a higher level
        String sanitized = Objects.nonNull(value) ? value.replaceAll("\\s", "") : value;
        process.setId(sanitized);
    }

    public Process getProcess() {
        return process;
    }

    public void addChildShape(BPMNShape shape) {
        if (shape == null) {
            return;
        }
        List<DiagramElement> planeElement = bpmnPlane.getPlaneElement();
        if (planeElement.contains(shape)) {
            throw new IllegalArgumentException("Cannot add the same shape twice: " + shape.getId());
        }
        planeElement.add(shape);
    }

    public void addChildEdge(BPMNEdge edge) {
        if (edge == null) {
            return;
        }
        List<DiagramElement> planeElement = bpmnPlane.getPlaneElement();
        if (planeElement.contains(edge)) {
            throw new IllegalArgumentException("Cannot add the same edge twice: " + edge.getId());
        }
        planeElement.add(edge);
    }

    public BPMNDiagram getBpmnDiagram() {
        bpmnDiagram.getPlane().setBpmnElement(process);
        return bpmnDiagram;
    }

    public void addChildElement(BasePropertyWriter p) {
        Processes.addChildElement(
                p,
                childElements,
                process,
                simulationParameters,
                itemDefinitions,
                rootElements);

        addChildShape(p.getShape());
        addChildEdge(p.getEdge());

        if (p instanceof SubProcessPropertyWriter) {
            addSubProcess((SubProcessPropertyWriter) p);
        }
    }

    // recursively add all child shapes and edges (`di:` namespace)
    // because these DO NOT nest (as opposed to `bpmn2:` namespace where subProcesses nest)
    private void addSubProcess(SubProcessPropertyWriter p) {
        Collection<BasePropertyWriter> childElements =
                p.getChildElements();

        childElements.forEach(el -> {
            addChildShape(el.getShape());
            addChildEdge(el.getEdge());
            if (el instanceof SubProcessPropertyWriter) {
                addSubProcess((SubProcessPropertyWriter) el);
            }
        });
    }

    @Override
    public Collection<BasePropertyWriter> getChildElements() {
        return this.childElements.values();
    }

    public BasePropertyWriter getChildElement(String id) {
        BasePropertyWriter propertyWriter = this.childElements.get(id);
        return propertyWriter;
    }

    public void setName(String value) {
        process.setName(value);
    }

    public void setExecutable(Boolean value) {
        process.setIsExecutable(value);
    }

    public void setPackage(String value) {
        CustomAttribute.packageName.of(process).set(value);
    }

    public void setVersion(String value) {
        CustomAttribute.version.of(process).set(value);
    }

    public void setAdHoc(Boolean adHoc) {
        CustomAttribute.adHoc.of(process).set(adHoc);
    }

    public void setDescription(String value) {
        CustomElement.description.of(process).set(value);
    }

    public void setProcessVariables(BaseProcessVariables processVariables) {
        String value = processVariables.getValue();
        DeclarationList declarationList = DeclarationList.fromString(value);

        List<Property> properties = process.getProperties();
        declarationList.getDeclarations().forEach(decl -> {
            VariableScope.Variable variable =
                    variableScope.declare(this.process.getId(), decl.getIdentifier(), decl.getType());
            properties.add(variable.getTypedIdentifier());
            this.itemDefinitions.add(variable.getTypeDeclaration());
        });
    }

    public void setCaseFileVariables(CaseFileVariables caseFileVariables) {
        String value = caseFileVariables.getValue();
        DeclarationList declarationList = DeclarationList.fromString(value);

        List<Property> properties = process.getProperties();
        declarationList.getDeclarations().forEach(decl -> {
            VariableScope.Variable variable =
                    variableScope.declare(this.process.getId(),
                                          CaseFileVariables.CASE_FILE_PREFIX + decl.getIdentifier(),
                                          decl.getType());
            properties.add(variable.getTypedIdentifier());
            this.itemDefinitions.add(variable.getTypeDeclaration());
        });
    }

    public void setCaseIdPrefix(CaseIdPrefix caseIdPrefix) {
        CustomElement.caseIdPrefix.of(process).set(caseIdPrefix.getValue());
    }

    public void setCaseRoles(CaseRoles roles) {
        CustomElement.caseRole.of(process).set(roles.getValue());
    }

    public void addLaneSet(Collection<LanePropertyWriter> lanes) {
        if (lanes.isEmpty()) {
            return;
        }
        LaneSet laneSet = bpmn2.createLaneSet();
        List<org.eclipse.bpmn2.Lane> laneList = laneSet.getLanes();
        lanes.forEach(l -> laneList.add(l.getElement()));
        process.getLaneSets().add(laneSet);
        lanes.forEach(l -> {
            this.childElements.put(l.getElement().getId(), l);
            addChildShape(l.getShape());
        });
    }

    public Collection<ElementParameters> getSimulationParameters() {
        return simulationParameters;
    }

    public Relationship getRelationship() {
        Relationship relationship = bpmn2.createRelationship();
        relationship.setType(defaultRelationshipType);
        BPSimDataType simDataType = bpsim.createBPSimDataType();
        // currently support single scenario
        Scenario defaultScenario = bpsim.createScenario();
        ScenarioParameters scenarioParameters = bpsim.createScenarioParameters();
        defaultScenario.setId("default"); // single scenario suppoert
        defaultScenario.setName("Simulationscenario"); // single scenario support
        defaultScenario.setScenarioParameters(scenarioParameters);
        simDataType.getScenario().add(defaultScenario);
        ExtensionAttributeValue extensionElement = bpmn2.createExtensionAttributeValue();
        relationship.getExtensionValues().add(extensionElement);
        FeatureMap.Entry extensionElementEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(
                (EStructuralFeature.Internal) BpsimPackage.Literals.DOCUMENT_ROOT__BP_SIM_DATA,
                simDataType);
        relationship.getExtensionValues().get(0).getValue().add(extensionElementEntry);
        defaultScenario.getElementParameters().addAll(simulationParameters);

        return relationship;
    }
}
