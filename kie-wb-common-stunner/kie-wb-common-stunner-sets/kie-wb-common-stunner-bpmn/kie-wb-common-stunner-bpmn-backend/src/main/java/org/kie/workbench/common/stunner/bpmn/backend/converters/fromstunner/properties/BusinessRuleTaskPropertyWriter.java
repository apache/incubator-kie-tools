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

import org.eclipse.bpmn2.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomInput;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.Scripts;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.DecisionName;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.DmnModelName;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Namespace;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.RuleFlowGroup;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.RuleLanguage;

public class BusinessRuleTaskPropertyWriter extends ActivityPropertyWriter {

    private final BusinessRuleTask task;
    private final CustomInput<String> namespace;
    private final CustomInput<String> dmnModelName;
    private final CustomInput<String> decisionName;

    public BusinessRuleTaskPropertyWriter(BusinessRuleTask task, VariableScope variableScope) {
        super(task, variableScope);
        this.task = task;

        this.namespace = CustomInput.namespace.of(task);
        this.addItemDefinition(this.namespace.typeDef());

        this.dmnModelName = CustomInput.dmnModelName.of(task);
        this.addItemDefinition(this.dmnModelName.typeDef());

        this.decisionName = CustomInput.decisionName.of(task);
        this.addItemDefinition(this.decisionName.typeDef());
    }

    public void setImplementation(RuleLanguage ruleLanguage) {
        task.setImplementation(ruleLanguage.getValue());
    }

    public void setRuleFlowGroup(RuleFlowGroup ruleFlowGroup) {
        CustomAttribute.ruleFlowGroup.of(baseElement).set(ruleFlowGroup.getValue());
    }

    public void setNamespace(Namespace namespace) {
        this.namespace.set(namespace.getValue());
    }

    public void setDmnModelName(DmnModelName dmnModelName) {
        this.dmnModelName.set(dmnModelName.getValue());
    }

    public void setDecisionName(DecisionName decisionName) {
        this.decisionName.set(decisionName.getValue());
    }

    public void setOnEntryAction(OnEntryAction onEntryAction) {
        Scripts.setOnEntryAction(flowElement, onEntryAction);
    }

    public void setOnExitAction(OnExitAction onExitAction) {
        Scripts.setOnExitAction(flowElement, onExitAction);
    }

    public void setAsync(Boolean value) {
        CustomElement.async.of(baseElement).set(value);
    }

    public void setAdHocAutostart(Boolean value) {
        CustomElement.autoStart.of(baseElement).set(value);
    }

    public void setSlaDueDate(String value) {
        CustomElement.slaDueDate.of(baseElement).set(value);
    }
}
