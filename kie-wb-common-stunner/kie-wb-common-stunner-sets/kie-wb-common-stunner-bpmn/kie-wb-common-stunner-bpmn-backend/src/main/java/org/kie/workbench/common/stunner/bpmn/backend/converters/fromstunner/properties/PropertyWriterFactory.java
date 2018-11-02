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

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.AdHocSubProcess;
import org.eclipse.bpmn2.Association;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.BusinessRuleTask;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.Definitions;
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

public class PropertyWriterFactory {

    private final VariableScope variableScope = new FlatVariableScope();

    public UserTaskPropertyWriter of(UserTask e) {
        return new UserTaskPropertyWriter(e, variableScope);
    }

    public ThrowEventPropertyWriter of(ThrowEvent e) {
        return new ThrowEventPropertyWriter(e, variableScope);
    }

    public PropertyWriter of(FlowElement e) {
        return new PropertyWriter(e, variableScope);
    }

    public CallActivityPropertyWriter of(CallActivity e) {
        return new CallActivityPropertyWriter(e, variableScope);
    }

    public BoundaryEventPropertyWriter of(BoundaryEvent e) {
        return new BoundaryEventPropertyWriter(e, variableScope);
    }

    public CatchEventPropertyWriter of(CatchEvent e) {
        return new CatchEventPropertyWriter(e, variableScope);
    }

    public BusinessRuleTaskPropertyWriter of(BusinessRuleTask e) {
        return new BusinessRuleTaskPropertyWriter(e, variableScope);
    }

    public DefinitionsPropertyWriter of(Definitions e) {
        return new DefinitionsPropertyWriter(e);
    }

    public AdHocSubProcessPropertyWriter of(AdHocSubProcess e) {
        return new AdHocSubProcessPropertyWriter(e, variableScope);
    }

    public MultipleInstanceSubProcessPropertyWriter ofMultipleInstanceSubProcess(SubProcess e) {
        return new MultipleInstanceSubProcessPropertyWriter(e, variableScope);
    }

    public SubProcessPropertyWriter of(SubProcess e) {
        return new SubProcessPropertyWriter(e, variableScope);
    }

    public ProcessPropertyWriter of(Process e) {
        return new ProcessPropertyWriter(e, variableScope);
    }

    public SequenceFlowPropertyWriter of(SequenceFlow e) {
        return new SequenceFlowPropertyWriter(e, variableScope);
    }

    public AssociationPropertyWriter of(Association e) {
        return new AssociationPropertyWriter(e, variableScope);
    }

    public GatewayPropertyWriter of(Gateway e) {
        return new GatewayPropertyWriter(e, variableScope);
    }

    public LanePropertyWriter of(Lane e) {
        return new LanePropertyWriter(e, variableScope);
    }

    public ActivityPropertyWriter of(Activity e) {
        return new ActivityPropertyWriter(e, variableScope);
    }

    public ScriptTaskPropertyWriter of(ScriptTask e) {
        return new ScriptTaskPropertyWriter(e, variableScope);
    }

    public ServiceTaskPropertyWriter of(Task e) {
        return new ServiceTaskPropertyWriter(e, variableScope);
    }
}
