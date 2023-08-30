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

import java.util.Set;

import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.DataObject;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.Scripts;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;

public class CallActivityPropertyWriter extends MultipleInstanceActivityPropertyWriter {

    private final CallActivity activity;

    public CallActivityPropertyWriter(CallActivity activity, VariableScope variableScope, Set<DataObject> dataObjects) {
        super(activity, variableScope, dataObjects);
        this.activity = activity;
    }

    public void setOnEntryAction(OnEntryAction onEntryAction) {
        Scripts.setOnEntryAction(activity, onEntryAction);
    }

    public void setOnExitAction(OnExitAction onExitAction) {
        Scripts.setOnExitAction(activity, onExitAction);
    }

    public void setIndependent(Boolean independent) {
        CustomAttribute.independent.of(activity).set(independent);
    }

    public void setAbortParent(Boolean abortParent) {
        CustomElement.abortParent.of(activity).set(abortParent);
    }

    public void setWaitForCompletion(Boolean waitForCompletion) {
        CustomAttribute.waitForCompletion.of(activity).set(waitForCompletion);
    }

    public void setAsync(Boolean async) {
        CustomElement.async.of(activity).set(async);
    }

    public void setCalledElement(String value) {
        activity.setCalledElement(value);
    }

    public void setCase(Boolean isCase) {
        CustomElement.isCase.of(flowElement).set(isCase);
    }

    public void setAdHocAutostart(boolean autoStart) {
        CustomElement.autoStart.of(flowElement).set(autoStart);
    }

    public void setSlaDueDate(SLADueDate slaDueDate) {
        CustomElement.slaDueDate.of(flowElement).set(slaDueDate.getValue());
    }
}
