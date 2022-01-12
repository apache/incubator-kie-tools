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

import org.eclipse.bpmn2.AdHocOrdering;
import org.eclipse.bpmn2.AdHocSubProcess;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.FormalExpression;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.ConverterUtils;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.FormalExpressionBodyHandler;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BaseAdHocActivationCondition;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BaseAdHocCompletionCondition;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.Scripts.asCData;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.Scripts.scriptLanguageToUri;

public class AdHocSubProcessPropertyWriter extends SubProcessPropertyWriter {

    private final AdHocSubProcess process;

    public AdHocSubProcessPropertyWriter(AdHocSubProcess process, VariableScope variableScope, Set<DataObject> dataObjects) {
        super(process, variableScope, dataObjects);
        this.process = process;
    }

    public void setAdHocActivationCondition(BaseAdHocActivationCondition adHocActivationCondition) {
        if (ConverterUtils.nonEmpty(adHocActivationCondition.getValue())) {
            CustomElement.customActivationCondition.of(flowElement).set(adHocActivationCondition.getValue());
        }
    }

    public void setAdHocOrdering(org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocOrdering value) {
        process.setOrdering(AdHocOrdering.getByName(value.getValue()));
    }

    public void setAdHocCompletionCondition(BaseAdHocCompletionCondition adHocCompletionCondition) {
        FormalExpression e = bpmn2.createFormalExpression();
        ScriptTypeValue s = adHocCompletionCondition.getValue();
        e.setLanguage(scriptLanguageToUri(s.getLanguage()));
        FormalExpressionBodyHandler.of(e).setBody(asCData(s.getScript()));
        process.setCompletionCondition(e);
    }

    public void setAdHocAutostart(boolean autoStart) {
        CustomElement.autoStart.of(flowElement).set(autoStart);
    }
}
