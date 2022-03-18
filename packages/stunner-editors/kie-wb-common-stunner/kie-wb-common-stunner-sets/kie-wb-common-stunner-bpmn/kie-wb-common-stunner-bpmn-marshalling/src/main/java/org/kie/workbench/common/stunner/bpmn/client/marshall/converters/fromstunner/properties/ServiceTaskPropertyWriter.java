/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties;

import java.util.Set;

import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.Task;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomInput;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.Scripts;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;

public class ServiceTaskPropertyWriter extends ActivityPropertyWriter {

    private final Task task;

    public ServiceTaskPropertyWriter(Task task, VariableScope variableScope, Set<DataObject> dataObejcts) {
        super(task, variableScope, dataObejcts);
        this.task = task;
    }

    public void setAsync(Boolean value) {
        CustomElement.async.of(baseElement).set(value);
    }

    public void setAdHocAutostart(Boolean value) {
        CustomElement.autoStart.of(baseElement).set(value);
    }

    public void setOnEntryAction(OnEntryAction onEntryAction) {
        Scripts.setOnEntryAction(flowElement, onEntryAction);
    }

    public void setOnExitAction(OnExitAction onExitAction) {
        Scripts.setOnExitAction(flowElement, onExitAction);
    }

    public void setServiceTaskName(String name) {
        CustomAttribute.serviceTaskName.of(flowElement).set(name);
    }

    public void setTaskName(String value) {
        CustomInput.taskName.of(task).set(value);
    }

    public void setSlaDueDate(String value) {
        CustomElement.slaDueDate.of(baseElement).set(value);
    }
}
