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

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Lane;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;

public class LanePropertyWriter extends BasePropertyWriter {

    private final Lane lane;

    public LanePropertyWriter(Lane lane, VariableScope variableScope) {
        super(lane, variableScope);
        this.lane = lane;
    }

    public void setName(String value) {
        lane.setName(SafeHtmlUtils.htmlEscape(value.trim()));
        CustomElement.name.of(lane).set(value);
    }

    @Override
    public Lane getElement() {
        return (Lane) super.getElement();
    }

    @Override
    public void addChild(BasePropertyWriter child) {
        lane.getFlowNodeRefs().add((FlowNode) child.getElement());
    }
}
