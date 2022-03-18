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

package org.kie.workbench.common.stunner.bpmn.definition.property.event.conditional;

import java.util.Objects;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.stunner.bpmn.definition.property.common.ConditionExpression;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.BaseCancellingEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.CancelActivity;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@FormDefinition(startElement = "cancelActivity")
public class CancellingConditionalEventExecutionSet extends BaseCancellingEventExecutionSet {

    @Property
    @FormField(afterElement = "cancelActivity",
            settings = {@FieldParam(name = "mode", value = "DROOLS_CONDITION")})
    @Valid
    private ConditionExpression conditionExpression;

    public CancellingConditionalEventExecutionSet() {
        this(new CancelActivity(true),
             new SLADueDate(),
             new ConditionExpression(new ScriptTypeValue("drools",
                                                         "")));
    }

    public CancellingConditionalEventExecutionSet(final @MapsTo("cancelActivity") CancelActivity cancelActivity,
                                                  final @MapsTo("slaDueDate") SLADueDate slaDueDate,
                                                  final @MapsTo("conditionExpression") ConditionExpression conditionExpression) {
        super(cancelActivity, slaDueDate);
        this.conditionExpression = conditionExpression;
    }

    public ConditionExpression getConditionExpression() {
        return conditionExpression;
    }

    public void setConditionExpression(ConditionExpression conditionExpression) {
        this.conditionExpression = conditionExpression;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         Objects.hashCode(conditionExpression));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CancellingConditionalEventExecutionSet) {
            CancellingConditionalEventExecutionSet other = (CancellingConditionalEventExecutionSet) o;
            return super.equals(other) &&
                    Objects.equals(conditionExpression, other.conditionExpression);
        }
        return false;
    }
}
