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
package org.kie.workbench.common.stunner.cm.backend.converters.fromstunner.properties;

import org.eclipse.bpmn2.CallActivity;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.CallActivityPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.VariableScope;
import org.kie.workbench.common.stunner.cm.backend.converters.customproperties.CaseManagementCustomElement;

public class CaseManagementCallActivityPropertyWriter extends CallActivityPropertyWriter {

    public CaseManagementCallActivityPropertyWriter(CallActivity activity, VariableScope variableScope) {
        super(activity, variableScope);
    }

    public void setCase(Boolean isCase) {
        CaseManagementCustomElement.isCase.of(flowElement).set(isCase);
    }

    public void setAdHocAutostart(boolean autoStart) {
        CustomElement.autoStart.of(flowElement).set(autoStart);
    }
}
