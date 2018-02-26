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

package org.kie.workbench.common.stunner.bpmn.backend.converters.properties;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.BusinessRuleTask;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.kie.workbench.common.stunner.bpmn.backend.converters.DefinitionResolver;

public class PropertyReaderFactory {

    private final BPMNPlane plane;
    private final DefinitionResolver definitionResolver;

    public PropertyReaderFactory(Definitions definitions) {
        this.plane = findPlane(definitions);
        this.definitionResolver = new DefinitionResolver(definitions);
    }

    private BPMNPlane findPlane(Definitions definitions) {
        return definitions.getDiagrams().get(0).getPlane();
    }

    public FlowElementPropertyReader of(FlowElement el) {
        return new FlowElementPropertyReader(el, plane);
    }

    public LanePropertyReader of(Lane el) {
        return new LanePropertyReader(el, plane);
    }

    public SequenceFlowPropertyReader of(SequenceFlow el) {
        return new SequenceFlowPropertyReader(el, plane);
    }

    public GatewayPropertyReader of(Gateway el) {
        return new GatewayPropertyReader(el, plane);
    }

    public NoneTaskPropertyReader of(Task el) {
        return new NoneTaskPropertyReader(el, plane, definitionResolver);
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

    public ActivityPropertyReader of(Activity el) {
        return new ActivityPropertyReader(el, plane, definitionResolver);
    }

    public EventPropertyReader of(Event el) {
        return EventPropertyReader.of(el, plane, definitionResolver);
    }

    public SubProcessPropertyReader of(SubProcess el) {
        return new SubProcessPropertyReader(el, plane, definitionResolver);
    }

    public ProcessPropertyReader of(Process el) {
        return new ProcessPropertyReader(el, plane);
    }
}
