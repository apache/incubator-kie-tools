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
package org.kie.workbench.common.stunner.bpmn.definition.property.subProcess.execution;

import java.util.Objects;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BaseSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@PropertySet
@FormDefinition(startElement = "onEntryAction")
public class EmbeddedSubprocessExecutionSet extends BaseSubprocessTaskExecutionSet {

    @Property
    @FormField(settings = {@FieldParam(name = "mode", value = "ACTION_SCRIPT")})
    @Valid
    private OnEntryAction onEntryAction;

    @Property
    @FormField(settings = {@FieldParam(name = "mode", value = "ACTION_SCRIPT")},
            afterElement = "onEntryAction")
    @Valid
    private OnExitAction onExitAction;

    @Property
    @FormField(afterElement = "onExitAction")
    @Valid
    private IsAsync isAsync;

    public EmbeddedSubprocessExecutionSet() {
        this(new OnEntryAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                      ""))),
             new OnExitAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                     ""))),
             new IsAsync(),
             new SLADueDate());
    }

    public EmbeddedSubprocessExecutionSet(final @MapsTo("onEntryAction") OnEntryAction onEntryAction,
                                          final @MapsTo("onExitAction") OnExitAction onExitAction,
                                          final @MapsTo("isAsync") IsAsync isAsync,
                                          final @MapsTo("slaDueDate") SLADueDate slaDueDate) {
        super(slaDueDate);
        this.onEntryAction = onEntryAction;
        this.onExitAction = onExitAction;
        this.isAsync = isAsync;
    }

    public OnEntryAction getOnEntryAction() {
        return onEntryAction;
    }

    public void setOnEntryAction(OnEntryAction onEntryAction) {
        this.onEntryAction = onEntryAction;
    }

    public OnExitAction getOnExitAction() {
        return onExitAction;
    }

    public void setOnExitAction(OnExitAction onExitAction) {
        this.onExitAction = onExitAction;
    }

    public IsAsync getIsAsync() {
        return isAsync;
    }

    public void setIsAsync(IsAsync isAsync) {
        this.isAsync = isAsync;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         Objects.hashCode(onEntryAction),
                                         Objects.hashCode(onExitAction),
                                         Objects.hashCode(isAsync));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof EmbeddedSubprocessExecutionSet) {
            EmbeddedSubprocessExecutionSet other = (EmbeddedSubprocessExecutionSet) o;
            return super.equals(other) &&
                    Objects.equals(onEntryAction, other.onEntryAction) &&
                    Objects.equals(onExitAction, other.onExitAction) &&
                    Objects.equals(isAsync, other.isAsync);
        }
        return false;
    }
}
