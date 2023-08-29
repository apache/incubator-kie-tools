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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bpsim.ElementParameters;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.LaneSet;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.DeclarationList;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.ElementContainer;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.Scripts;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.SimulationSets;
import org.kie.workbench.common.stunner.bpmn.definition.BaseAdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.BaseProcessVariables;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;

public class SubProcessPropertyWriter extends MultipleInstanceActivityPropertyWriter implements ElementContainer {

    protected final SubProcess process;
    private Collection<ElementParameters> simulationParameters = new ArrayList<>();
    private Map<String, BasePropertyWriter> childElements = new HashMap<>();

    public SubProcessPropertyWriter(SubProcess process, VariableScope variableScope, Set<DataObject> dataObjects) {
        super(process, variableScope, dataObjects);
        this.process = process;
    }

    public void addChildElement(BasePropertyWriter p) {
        p.setParent(this);
        Processes.addChildElement(
                p,
                childElements,
                process,
                simulationParameters,
                itemDefinitions,
                rootElements);
    }

    @Override
    public Collection<BasePropertyWriter> getChildElements() {
        return childElements.values();
    }

    public BasePropertyWriter getChildElement(String id) {
        return this.childElements.get(id);
    }

    @Override
    public void addChildEdge(BPMNEdge edge) {

    }

    public void setDescription(String value) {
        CustomElement.description.of(flowElement).set(value);
    }

    public void setSimulationSet(SimulationSet simulations) {
        ElementParameters elementParameters = SimulationSets.toElementParameters(simulations);
        elementParameters.setElementRef(this.baseElement.getId());
        this.simulationParameters.add(elementParameters);
    }

    public void setProcessVariables(BaseProcessVariables processVariables) {
        String value = processVariables.getValue();
        value = StringUtils.preFilterVariablesForGenerics(value);
        DeclarationList declarationList = DeclarationList.fromString(value);

        List<Property> properties = process.getProperties();
        declarationList.getDeclarations().forEach(decl -> {
            VariableScope.Variable variable =
                    variableScope.declare(this.process.getId(), decl.getIdentifier(), StringUtils.postFilterForGenerics(decl.getType()));
            if (!decl.getTags().isEmpty()) {
                CustomElement.customTags.of(variable.getTypedIdentifier()).set(decl.getTags());
            }
            properties.add(variable.getTypedIdentifier());
            this.itemDefinitions.add(variable.getTypeDeclaration());
        });
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
        });
    }

    public void setOnEntryAction(OnEntryAction onEntryAction) {
        Scripts.setOnEntryAction(process, onEntryAction);
    }

    public void setOnExitAction(OnExitAction onExitAction) {
        Scripts.setOnExitAction(process, onExitAction);
    }

    public void setAsync(Boolean isAsync) {
        CustomElement.async.of(flowElement).set(isAsync);
    }

    public void setSlaDueDate(SLADueDate slaDueDate) {
        CustomElement.slaDueDate.of(flowElement).set(slaDueDate.getValue());
    }

    @Override
    public void setAbsoluteBounds(Node<? extends View, ?> node) {
        super.setAbsoluteBounds(node);
        Object definition = node.getContent().getDefinition();
        if (definition instanceof BaseAdHocSubprocess ||
                definition instanceof EventSubprocess ||
                definition instanceof EmbeddedSubprocess) {
            shape.setIsExpanded(true);
        }
    }
}
