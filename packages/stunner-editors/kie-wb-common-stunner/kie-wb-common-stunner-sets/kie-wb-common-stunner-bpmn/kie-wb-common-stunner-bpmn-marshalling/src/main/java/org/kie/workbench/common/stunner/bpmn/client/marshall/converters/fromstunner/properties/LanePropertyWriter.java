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

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import org.eclipse.bpmn2.Auditing;
import org.eclipse.bpmn2.CategoryValue;
import org.eclipse.bpmn2.DataObjectReference;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.Monitoring;
import org.eclipse.bpmn2.impl.FlowNodeImpl;
import org.eclipse.emf.common.util.EList;
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
        if (child instanceof DataObjectPropertyWriter) {
            final DataObjectReference element = (DataObjectReference) child.getElement();

            FlowNode node = new FlowNodeImpl() {
                @Override
                public Auditing getAuditing() {
                    return element.getAuditing();
                }

                @Override
                public Monitoring getMonitoring() {
                    return element.getMonitoring();
                }

                @Override
                public EList<CategoryValue> getCategoryValueRef() {
                    return element.getCategoryValueRef();
                }

                @Override
                public String getName() {
                    return element.getName();
                }

                @Override
                public String toString() {
                    return element.toString();
                }
            };
            lane.getFlowNodeRefs().add(node);
        } else {
            lane.getFlowNodeRefs().add((FlowNode) child.getElement());
        }
    }
}
