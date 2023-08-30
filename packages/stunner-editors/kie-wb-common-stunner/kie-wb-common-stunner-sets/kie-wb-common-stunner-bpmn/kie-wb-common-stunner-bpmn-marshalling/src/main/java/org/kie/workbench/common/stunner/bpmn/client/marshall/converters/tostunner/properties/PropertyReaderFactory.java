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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.bpmn2.AdHocSubProcess;
import org.eclipse.bpmn2.Association;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.BusinessRuleTask;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.DataObjectReference;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.TextAnnotation;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.util.CorrelationReaderData;
import org.kie.workbench.common.stunner.bpmn.client.workitem.WorkItemDefinitionClientParser;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.events.CorrelationModel;

public class PropertyReaderFactory {

    protected final BPMNDiagram diagram;
    protected final DefinitionResolver definitionResolver;
    protected final List<CorrelationReaderData> correlationReaderDataList = new ArrayList<>();

    public PropertyReaderFactory(DefinitionResolver definitionResolver) {
        this.diagram = definitionResolver.getDiagram();
        this.definitionResolver = definitionResolver;
    }

    public FlowElementPropertyReader of(FlowElement el) {
        return new FlowElementPropertyReader(el, diagram, definitionResolver.getShape(el.getId()), definitionResolver.getResolutionFactor());
    }

    public LanePropertyReader of(Lane el) {
        return new LanePropertyReader(el, diagram, definitionResolver.getShape(el.getId()), definitionResolver.getResolutionFactor());
    }

    public LanePropertyReader of(Lane el, Lane elParent) {
        return new LanePropertyReader(el, diagram, definitionResolver.getShape(el.getId()), definitionResolver.getShape(elParent.getId()), definitionResolver.getResolutionFactor());
    }

    public SequenceFlowPropertyReader of(SequenceFlow el) {
        return new SequenceFlowPropertyReader(el, diagram, definitionResolver);
    }

    public AssociationPropertyReader of(Association el) {
        return new AssociationPropertyReader(el, diagram, definitionResolver);
    }

    public GatewayPropertyReader of(Gateway el) {
        return new GatewayPropertyReader(el, diagram, definitionResolver.getShape(el.getId()), definitionResolver.getResolutionFactor());
    }

    public TaskPropertyReader of(Task el) {
        return new TaskPropertyReader(el, diagram, definitionResolver);
    }

    public UserTaskPropertyReader of(UserTask el) {
        return new UserTaskPropertyReader(el, diagram, definitionResolver);
    }

    public ScriptTaskPropertyReader of(ScriptTask el) {
        return new ScriptTaskPropertyReader(el, diagram, definitionResolver);
    }

    public GenericServiceTaskPropertyReader of(ServiceTask el) {
        return new GenericServiceTaskPropertyReader(el, diagram, definitionResolver);
    }

    public BusinessRuleTaskPropertyReader of(BusinessRuleTask el) {
        return new BusinessRuleTaskPropertyReader(el, diagram, definitionResolver);
    }

    public ServiceTaskPropertyReader ofCustom(Task el) {
        String serviceTaskName = CustomAttribute.serviceTaskName.of(el).get();
        return definitionResolver
                .getWorkItemDefinitions()
                .stream()
                .filter(wid -> Objects.equals(wid.getName(), serviceTaskName))
                .findFirst()
                .map(def -> new ServiceTaskPropertyReader(el, def, diagram, definitionResolver))
                .orElseGet(() -> new ServiceTaskPropertyReader(el,
                                                               WorkItemDefinitionClientParser.emptyWid().setName(serviceTaskName),
                                                               diagram,
                                                               definitionResolver));
    }

    public CallActivityPropertyReader of(CallActivity el) {
        return new CallActivityPropertyReader(el, diagram, definitionResolver);
    }

    public CatchEventPropertyReader of(CatchEvent el) {
        if (el instanceof BoundaryEvent) {
            return new BoundaryEventPropertyReader((BoundaryEvent) el, diagram, definitionResolver);
        } else {
            return new CatchEventPropertyReader(el, diagram, definitionResolver);
        }
    }

    public ThrowEventPropertyReader of(ThrowEvent el) {
        return new ThrowEventPropertyReader(el, diagram, definitionResolver);
    }

    public SubProcessPropertyReader of(SubProcess el) {
        return new SubProcessPropertyReader(el, diagram, definitionResolver);
    }

    public AdHocSubProcessPropertyReader of(AdHocSubProcess el) {
        return new AdHocSubProcessPropertyReader(el, diagram, definitionResolver);
    }

    public MultipleInstanceSubProcessPropertyReader ofMultipleInstance(SubProcess el) {
        return new MultipleInstanceSubProcessPropertyReader(el, diagram, definitionResolver);
    }

    public ProcessPropertyReader of(Process el) {
        return new ProcessPropertyReader(el, diagram, definitionResolver.getShape(el.getId()), definitionResolver.getResolutionFactor());
    }

    public TextAnnotationPropertyReader of(TextAnnotation el) {
        return new TextAnnotationPropertyReader(el, diagram, definitionResolver.getShape(el.getId()), definitionResolver.getResolutionFactor());
    }

    public DefinitionsPropertyReader of(Definitions el) {
        return new DefinitionsPropertyReader(el, diagram, definitionResolver.getShape(el.getId()), definitionResolver.getResolutionFactor());
    }

    public DataObjectPropertyReader of(DataObjectReference el) {
        return new DataObjectPropertyReader(el, diagram, definitionResolver.getShape(el.getId()), definitionResolver.getResolutionFactor());
    }

    public CollaborationPropertyReader of(Definitions definitions, Process process) {
        return new CollaborationPropertyReader(definitions, process, correlationReaderDataList);
    }

    public CorrelationPropertyReader of(CorrelationModel correlationModel) {
        return new CorrelationPropertyReader(correlationModel, correlationReaderDataList);
    }
}
