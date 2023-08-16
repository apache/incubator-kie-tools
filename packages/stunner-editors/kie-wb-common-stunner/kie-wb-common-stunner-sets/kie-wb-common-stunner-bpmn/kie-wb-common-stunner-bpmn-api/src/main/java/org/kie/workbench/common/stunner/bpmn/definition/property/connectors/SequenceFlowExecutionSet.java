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

package org.kie.workbench.common.stunner.bpmn.definition.property.connectors;

import java.util.Objects;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.bpmn.definition.property.common.ConditionExpression;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.bpmn.forms.model.ConditionEditorFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@FormDefinition(
        startElement = "priority"
)
public class SequenceFlowExecutionSet implements BPMNPropertySet {

    @Property
    @FormField
    @Valid
    private Priority priority;

    @Property
    @FormField(afterElement = "priority",
            type = ConditionEditorFieldType.class,
            settings = {@FieldParam(name = "mode", value = "FLOW_CONDITION")})
    @Valid
    private ConditionExpression conditionExpression;

    public SequenceFlowExecutionSet() {
        this(new Priority(""),
             new ConditionExpression(new ScriptTypeValue("java",
                                                         ""))
        );
    }

    public SequenceFlowExecutionSet(final @MapsTo("priority") Priority priority,
                                    final @MapsTo("conditionExpression") ConditionExpression conditionExpression) {
        this.priority = priority;
        this.conditionExpression = conditionExpression;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(final Priority priority) {
        this.priority = priority;
    }

    public ConditionExpression getConditionExpression() {
        return conditionExpression;
    }

    public void setConditionExpression(final ConditionExpression conditionExpression) {
        this.conditionExpression = conditionExpression;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(priority),
                                         Objects.hashCode(conditionExpression));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SequenceFlowExecutionSet) {
            SequenceFlowExecutionSet other = (SequenceFlowExecutionSet) o;
            return Objects.equals(priority,
                                  other.priority) &&
                    Objects.equals(conditionExpression,
                                   other.conditionExpression);
        }
        return false;
    }
}
