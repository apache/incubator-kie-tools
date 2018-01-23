/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.bpmn.definition.property.task;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.bpmn.definition.property.assignee.Actors;
import org.kie.workbench.common.stunner.bpmn.definition.property.assignee.Groupid;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.Priority;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.forms.model.AssigneeEditorFieldType;
import org.kie.workbench.common.stunner.bpmn.forms.model.AssignmentsEditorFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@PropertySet
@FormDefinition(
        startElement = "taskName"
)
public class UserTaskExecutionSet implements BPMNPropertySet {

    @Property
    @FormField
    @Valid
    protected TaskName taskName;

    @Property
    @FormField(
            afterElement = "taskName"
    )
    @Valid
    private Subject subject;

    @Property
    @FormField(
            type = AssigneeEditorFieldType.class,
            afterElement = "subject",
            settings = @FieldParam(name = "type", value = "USER")
    )
    @Valid
    private Actors actors;

    @Property
    @FormField(
            type = AssigneeEditorFieldType.class,
            afterElement = "actors",
            settings = @FieldParam(name = "type", value = "GROUP")
    )
    @Valid
    private Groupid groupid;

    @Property
    @FormField(
            type = AssignmentsEditorFieldType.class,
            afterElement = "groupid"
    )
    @Valid
    private AssignmentsInfo assignmentsinfo;

    @Property
    @FormField(
            afterElement = "assignmentsinfo"
    )
    @Valid
    private IsAsync isAsync;

    @Property
    @FormField(
            afterElement = "isAsync"
    )
    @Valid
    private Skippable skippable;

    @Property
    @FormField(
            afterElement = "skippable"
    )
    @Valid
    private Priority priority;

    @Property
    @FormField(
            type = TextAreaFieldType.class,
            afterElement = "skippable"
    )
    @Valid
    private Description description;

    @Property
    @FormField(
            type = AssigneeEditorFieldType.class,
            afterElement = "description",
            settings = {
                    @FieldParam(name = "type", value = "USER"),
                    @FieldParam(name = "max", value = "1")
            }
    )
    @Valid
    private CreatedBy createdBy;

    @Property
    @FormField(
            afterElement = "createdBy"
    )
    @Valid
    private AdHocAutostart adHocAutostart;

    @Property
    @FormField(
            type = TextAreaFieldType.class,
            afterElement = "adHocAutostart",
            settings = {@FieldParam(name = "rows", value = "5")}
    )
    @Valid
    private OnEntryAction onEntryAction;

    @Property
    @FormField(
            type = TextAreaFieldType.class,
            afterElement = "onEntryAction",
            settings = {@FieldParam(name = "rows", value = "5")}
    )
    @Valid
    private OnExitAction onExitAction;

    @Property
    @FormField(
            type = ListBoxFieldType.class,
            afterElement = "onExitAction"
    )
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.REMOTE,
            className = "org.kie.workbench.common.stunner.bpmn.backend.dataproviders.ScriptLanguageFormProvider")
    @Valid
    protected ScriptLanguage scriptLanguage;

    public UserTaskExecutionSet() {
        this(new TaskName("Task"),
             new Actors(),
             new Groupid(),
             new AssignmentsInfo(),
             new IsAsync(),
             new Skippable(),
             new Priority(""),
             new Subject(""),
             new Description(""),
             new CreatedBy(),
             new AdHocAutostart(),
             new OnEntryAction(""),
             new OnExitAction(""),
             new ScriptLanguage());
    }

    public UserTaskExecutionSet(final @MapsTo("taskName") TaskName taskName,
                                final @MapsTo("actors") Actors actors,
                                final @MapsTo("groupid") Groupid groupid,
                                final @MapsTo("assignmentsinfo") AssignmentsInfo assignmentsinfo,
                                final @MapsTo("isAsync") IsAsync isAsync,
                                final @MapsTo("skippable") Skippable skippable,
                                final @MapsTo("priority") Priority priority,
                                final @MapsTo("subject") Subject subject,
                                final @MapsTo("description") Description description,
                                final @MapsTo("createdBy") CreatedBy createdBy,
                                final @MapsTo("adHocAutostart") AdHocAutostart adHocAutostart,
                                final @MapsTo("onEntryAction") OnEntryAction onEntryAction,
                                final @MapsTo("onExitAction") OnExitAction onExitAction,
                                final @MapsTo("scriptLanguage") ScriptLanguage scriptLanguage) {
        this.taskName = taskName;
        this.actors = actors;
        this.groupid = groupid;
        this.assignmentsinfo = assignmentsinfo;
        this.isAsync = isAsync;
        this.skippable = skippable;
        this.priority = priority;
        this.subject = subject;
        this.description = description;
        this.createdBy = createdBy;
        this.adHocAutostart = adHocAutostart;
        this.onEntryAction = onEntryAction;
        this.onExitAction = onExitAction;
        this.scriptLanguage = scriptLanguage;
    }

    public TaskName getTaskName() {
        return taskName;
    }

    public void setTaskName(final TaskName taskName) {
        this.taskName = taskName;
    }

    public Actors getActors() {
        return actors;
    }

    public void setActors(final Actors actors) {
        this.actors = actors;
    }

    public Groupid getGroupid() {
        return groupid;
    }

    public void setGroupid(final Groupid groupid) {
        this.groupid = groupid;
    }

    public AssignmentsInfo getAssignmentsinfo() {
        return assignmentsinfo;
    }

    public void setAssignmentsinfo(final AssignmentsInfo assignmentsinfo) {
        this.assignmentsinfo = assignmentsinfo;
    }

    public IsAsync getIsAsync() {
        return isAsync;
    }

    public void setIsAsync(IsAsync isAsync) {
        this.isAsync = isAsync;
    }

    public Skippable getSkippable() {
        return skippable;
    }

    public void setSkippable(Skippable skippable) {
        this.skippable = skippable;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public CreatedBy getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(CreatedBy createdBy) {
        this.createdBy = createdBy;
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

    public ScriptLanguage getScriptLanguage() {
        return scriptLanguage;
    }

    public void setScriptLanguage(ScriptLanguage scriptLanguage) {
        this.scriptLanguage = scriptLanguage;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(taskName.hashCode(),
                                         subject.hashCode(),
                                         actors.hashCode(),
                                         groupid.hashCode(),
                                         assignmentsinfo.hashCode(),
                                         isAsync.hashCode(),
                                         skippable.hashCode(),
                                         priority.hashCode(),
                                         description.hashCode(),
                                         createdBy.hashCode(),
                                         adHocAutostart.hashCode(),
                                         onEntryAction.hashCode(),
                                         onExitAction.hashCode(),
                                         scriptLanguage.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof UserTaskExecutionSet) {
            UserTaskExecutionSet other = (UserTaskExecutionSet) o;
            return taskName.equals(other.taskName) &&
                    subject.equals(other.subject) &&
                    actors.equals(other.actors) &&
                    groupid.equals(other.groupid) &&
                    assignmentsinfo.equals(other.assignmentsinfo) &&
                    isAsync.equals(other.isAsync) &&
                    skippable.equals(other.skippable) &&
                    priority.equals(other.priority) &&
                    description.equals(other.description) &&
                    createdBy.equals(other.createdBy) &&
                    adHocAutostart.equals(other.adHocAutostart) &&
                    onEntryAction.equals(other.onEntryAction) &&
                    onExitAction.equals(other.onExitAction) &&
                    scriptLanguage.equals(other.scriptLanguage);
        }
        return false;
    }
}
