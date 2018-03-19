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

import java.util.Collections;
import java.util.Optional;

import org.eclipse.bpmn2.BusinessRuleTask;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;

public class BusinessRuleTaskPropertyReader extends TaskPropertyReader {

    public BusinessRuleTaskPropertyReader(BusinessRuleTask task, BPMNPlane plane, DefinitionResolver definitionResolver) {
        super(task, plane, definitionResolver);
    }

    public String getRuleFlowGroup() {
        return CustomAttribute.ruleFlowGroup.of(element).get();
    }

    public AssignmentsInfo getAssignmentsInfo() {
        Optional<InputOutputSpecification> ioSpecification =
                Optional.ofNullable(task.getIoSpecification());

        AssignmentsInfo info = AssignmentsInfos.of(
                ioSpecification.map(InputOutputSpecification::getDataInputs)
                        .orElse(Collections.emptyList()),
                task.getDataInputAssociations(),
                ioSpecification.map(InputOutputSpecification::getDataOutputs)
                        .orElse(Collections.emptyList()),
                task.getDataOutputAssociations(),
                ioSpecification.isPresent()
        );

        // do not break compatibility with old marshallers: return
        // empty delimited fields instead of empty string
        if (info.getValue().isEmpty()) {
            info.setValue("||||");
        }
        return info;
    }

    public ScriptTypeListValue getOnEntryAction() {
        return Scripts.onEntry(element.getExtensionValues());
    }

    public ScriptTypeListValue getOnExitAction() {
        return Scripts.onExit(element.getExtensionValues());
    }

    public boolean isAsync() {
        return CustomElement.async.of(element).get();
    }

    public boolean isAdHocAutoStart() {
        return CustomElement.autoStart.of(element).get();
    }
}
