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
package org.kie.workbench.common.stunner.bpmn.workitem;

import java.util.Objects;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocAutostart;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskName;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@PropertySet
@FormDefinition(startElement = "taskName")
public class ServiceTaskExecutionSet implements BPMNPropertySet {

    @Property
    @FormField
    @Valid
    protected TaskName taskName;

    @Property
    @FormField(afterElement = "taskName")
    @Valid
    private IsAsync isAsync;

    @Property
    @FormField(afterElement = "isAsync")
    @Valid
    private AdHocAutostart adHocAutostart;

    @Property
    @FormField(afterElement = "adHocAutostart",
            settings = {@FieldParam(name = "mode", value = "ACTION_SCRIPT")})
    @Valid
    private OnEntryAction onEntryAction;

    @Property
    @FormField(afterElement = "onEntryAction",
            settings = {@FieldParam(name = "mode", value = "ACTION_SCRIPT")})
    @Valid
    private OnExitAction onExitAction;

    public ServiceTaskExecutionSet() {
        this(new TaskName("Service Task"),
             new IsAsync(),
             new AdHocAutostart(),
             new OnEntryAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                      ""))),
             new OnExitAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                     ""))));
    }

    public ServiceTaskExecutionSet(final @MapsTo("taskName") TaskName taskName,
                                   final @MapsTo("isAsync") IsAsync isAsync,
                                   final @MapsTo("adHocAutostart") AdHocAutostart adHocAutostart,
                                   final @MapsTo("onEntryAction") OnEntryAction onEntryAction,
                                   final @MapsTo("onExitAction") OnExitAction onExitAction) {
        this.taskName = taskName;
        this.isAsync = isAsync;
        this.adHocAutostart = adHocAutostart;
        this.onEntryAction = onEntryAction;
        this.onExitAction = onExitAction;
    }

    public TaskName getTaskName() {
        return taskName;
    }

    public void setTaskName(final TaskName taskName) {
        this.taskName = taskName;
    }

    public IsAsync getIsAsync() {
        return isAsync;
    }

    public void setIsAsync(IsAsync isAsync) {
        this.isAsync = isAsync;
    }

    public AdHocAutostart getAdHocAutostart() {
        return adHocAutostart;
    }

    public void setAdHocAutostart(AdHocAutostart adHocAutostart) {
        this.adHocAutostart = adHocAutostart;
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

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(taskName),
                                         Objects.hashCode(isAsync),
                                         Objects.hashCode(adHocAutostart),
                                         Objects.hashCode(onEntryAction),
                                         Objects.hashCode(onExitAction));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ServiceTaskExecutionSet) {
            ServiceTaskExecutionSet other = (ServiceTaskExecutionSet) o;
            return Objects.equals(taskName,
                                  other.taskName) &&
                    Objects.equals(isAsync,
                                   other.isAsync) &&
                    Objects.equals(adHocAutostart,
                                   other.adHocAutostart) &&
                    Objects.equals(onEntryAction,
                                   other.onEntryAction) &&
                    Objects.equals(onExitAction,
                                   other.onExitAction);
        }
        return false;
    }
}
