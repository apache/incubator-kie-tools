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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.AdHocSubProcess;
import org.eclipse.bpmn2.Association;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.BusinessRuleTask;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;

public class PropertyReaderFactory {

    private final BPMNPlane plane;
    private final DefinitionResolver definitionResolver;

    public PropertyReaderFactory(DefinitionResolver definitionResolver) {
        this.plane = definitionResolver.getPlane();
        this.definitionResolver = definitionResolver;
    }

    public FlowElementPropertyReader of(FlowElement el) {
        return new FlowElementPropertyReader(el, plane, definitionResolver.getShape(el.getId()));
    }

    public LanePropertyReader of(Lane el) {
        return new LanePropertyReader(el, plane, definitionResolver.getShape(el.getId()));
    }

    public SequenceFlowPropertyReader of(SequenceFlow el) {
        return new SequenceFlowPropertyReader(el, plane, definitionResolver);
    }

    public AssociationPropertyReader of(Association el) {
        return new AssociationPropertyReader(el, plane, definitionResolver);
    }

    public GatewayPropertyReader of(Gateway el) {
        return new GatewayPropertyReader(el, plane, definitionResolver.getShape(el.getId()));
    }

    public TaskPropertyReader of(Task el) {
        return new TaskPropertyReader(el, plane, definitionResolver);
    }

    public UserTaskPropertyReader of(UserTask el) {
        return new UserTaskPropertyReader(el, plane, definitionResolver);
    }

    public ScriptTaskPropertyReader of(ScriptTask el) {
        return new ScriptTaskPropertyReader(el, plane, definitionResolver);
    }

    public BusinessRuleTaskPropertyReader of(BusinessRuleTask el) {
        return new BusinessRuleTaskPropertyReader(el, plane, definitionResolver);
    }

    public ServiceTaskPropertyReader ofCustom(Task el) {
        String serviceTaskName = CustomAttribute.serviceTaskName.of(el).get();
        Optional<WorkItemDefinition> def = definitionResolver
                .getWorkItemDefinitions()
                .stream()
                .filter(wid -> wid.getName().equals(serviceTaskName))
                .findFirst();
        if (def.isPresent()) {
            return new ServiceTaskPropertyReader(el, def.get(), plane, definitionResolver);
        } else {
            throw new NoSuchElementException("Cannot find WorkItemDefinition for " + serviceTaskName);
        }
    }

    public ActivityPropertyReader of(Activity el) {
        return new ActivityPropertyReader(el, plane, definitionResolver);
    }

    public CatchEventPropertyReader of(CatchEvent el) {
        if (el instanceof BoundaryEvent) {
            return new BoundaryEventPropertyReader((BoundaryEvent) el, plane, definitionResolver);
        } else {
            return new CatchEventPropertyReader(el, plane, definitionResolver);
        }
    }

    public ThrowEventPropertyReader of(ThrowEvent el) {
        return new ThrowEventPropertyReader(el, plane, definitionResolver);
    }

    public SubProcessPropertyReader of(SubProcess el) {
        return new SubProcessPropertyReader(el, plane, definitionResolver);
    }

    public AdHocSubProcessPropertyReader of(AdHocSubProcess el) {
        return new AdHocSubProcessPropertyReader(el, plane, definitionResolver);
    }

    public MultipleInstanceSubProcessPropertyReader ofMultipleInstance(SubProcess el) {
        return new MultipleInstanceSubProcessPropertyReader(el, plane, definitionResolver);
    }

    public ProcessPropertyReader of(Process el) {
        return new ProcessPropertyReader(el, plane, definitionResolver.getShape(el.getId()));
    }
}
