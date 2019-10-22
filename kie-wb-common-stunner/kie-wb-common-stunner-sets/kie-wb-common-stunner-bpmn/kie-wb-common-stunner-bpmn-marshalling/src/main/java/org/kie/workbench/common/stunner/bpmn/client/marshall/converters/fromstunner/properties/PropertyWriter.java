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

import bpsim.ElementParameters;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import org.eclipse.bpmn2.FlowElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;

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
        if (value == null || value.isEmpty()) {
            return;
        }
        flowElement.setName(SafeHtmlUtils.htmlEscape(value.trim()));
        CustomElement.name.of(flowElement).set(value);
    }

    public ElementParameters getSimulationParameters() {
        return null;
    }
}
