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

import bpsim.ElementParameters;
import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.bpmn2.Documentation;
import org.eclipse.bpmn2.FlowElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class PropertyWriter extends BasePropertyWriter {

    protected final FlowElement flowElement;

    public PropertyWriter(FlowElement flowElement, VariableScope variableScope) {
        super(flowElement, variableScope);
        this.flowElement = flowElement;
    }

    public FlowElement getFlowElement() {
        return flowElement;
    }

    public void setName(String value) {
        flowElement.setName(StringEscapeUtils.escapeXml10(value.trim()));
        CustomElement.name.of(flowElement).set(value);
    }

    public void setDocumentation(String value) {
        Documentation documentation = bpmn2.createDocumentation();
        documentation.setText(asCData(value));
        flowElement.getDocumentation().add(documentation);
    }

    public ElementParameters getSimulationParameters() {
        return null;
    }
}
