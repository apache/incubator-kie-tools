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

import java.util.Objects;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;
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
@FormDefinition(startElement = "taskName")
public class UserTaskExecutionSet implements BaseUserTaskExecutionSet {

    @Property
    @FormField
    @Valid
    protected TaskName taskName;

    @Property
    @FormField(afterElement = "taskName")
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
    @FormField(afterElement = "assignmentsinfo")
    @Valid
    private IsAsync isAsync;

    @Property
    @FormField(afterElement = "isAsync")
    @Valid
    private Skippable skippable;

    @Property
    @FormField(afterElement = "skippable")
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
    @FormField(afterElement = "createdBy")
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

    @Property
    @FormField(
            type = TextAreaFieldType.class,
            afterElement = "onExitAction"
    )
    @Valid
    private Content content;

    @Property
    @FormField(afterElement = "content")
    @Valid
    private SLADueDate slaDueDate;

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
             new OnEntryAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                      ""))),
             new OnExitAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                     ""))),
             new Content(""),
             new SLADueDate(""));
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
                                final @MapsTo("content") Content content,
                                final @MapsTo("slaDueDate") SLADueDate slaDueDate) {
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
        this.content = content;
        this.slaDueDate = slaDueDate;
    }

    @Override
    public TaskName getTaskName() {
        return taskName;
    }

    public void setTaskName(final TaskName taskName) {
        this.taskName = taskName;
    }

    @Override
    public Actors getActors() {
        return actors;
    }

    public void setActors(final Actors actors) {
        this.actors = actors;
    }

    @Override
    public Groupid getGroupid() {
        return groupid;
    }

    public void setGroupid(final Groupid groupid) {
        this.groupid = groupid;
    }

    @Override
    public AssignmentsInfo getAssignmentsinfo() {
        return assignmentsinfo;
    }

    public void setAssignmentsinfo(final AssignmentsInfo assignmentsinfo) {
        this.assignmentsinfo = assignmentsinfo;
    }

    @Override
    public IsAsync getIsAsync() {
        return isAsync;
    }

    public void setIsAsync(IsAsync isAsync) {
        this.isAsync = isAsync;
    }

    @Override
    public Skippable getSkippable() {
        return skippable;
    }

    public void setSkippable(Skippable skippable) {
        this.skippable = skippable;
    }

    @Override
    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    @Override
    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    @Override
    public CreatedBy getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(CreatedBy createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public AdHocAutostart getAdHocAutostart() {
        return adHocAutostart;
    }

    public void setAdHocAutostart(AdHocAutostart adHocAutostart) {
        this.adHocAutostart = adHocAutostart;
    }

    @Override
    public OnEntryAction getOnEntryAction() {
        return onEntryAction;
    }

    public void setOnEntryAction(OnEntryAction onEntryAction) {
        this.onEntryAction = onEntryAction;
    }

    @Override
    public OnExitAction getOnExitAction() {
        return onExitAction;
    }

    public void setOnExitAction(OnExitAction onExitAction) {
        this.onExitAction = onExitAction;
    }

    @Override
    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    @Override
    public SLADueDate getSlaDueDate() {
        return slaDueDate;
    }

    public void setSlaDueDate(SLADueDate slaDueDate) {
        this.slaDueDate = slaDueDate;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(taskName),
                                         Objects.hashCode(subject),
                                         Objects.hashCode(actors),
                                         Objects.hashCode(groupid),
                                         Objects.hashCode(assignmentsinfo),
                                         Objects.hashCode(isAsync),
                                         Objects.hashCode(skippable),
                                         Objects.hashCode(priority),
                                         Objects.hashCode(description),
                                         Objects.hashCode(createdBy),
                                         Objects.hashCode(adHocAutostart),
                                         Objects.hashCode(onEntryAction),
                                         Objects.hashCode(onExitAction),
                                         Objects.hashCode(content),
                                         Objects.hashCode(slaDueDate));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof UserTaskExecutionSet) {
            UserTaskExecutionSet other = (UserTaskExecutionSet) o;
            return Objects.equals(taskName,
                                  other.taskName) &&
                    Objects.equals(subject,
                                   other.subject) &&
                    Objects.equals(actors,
                                   other.actors) &&
                    Objects.equals(groupid,
                                   other.groupid) &&
                    Objects.equals(assignmentsinfo,
                                   other.assignmentsinfo) &&
                    Objects.equals(isAsync,
                                   other.isAsync) &&
                    Objects.equals(skippable,
                                   other.skippable) &&
                    Objects.equals(priority,
                                   other.priority) &&
                    Objects.equals(description,
                                   other.description) &&
                    Objects.equals(createdBy,
                                   other.createdBy) &&
                    Objects.equals(adHocAutostart,
                                   other.adHocAutostart) &&
                    Objects.equals(onEntryAction,
                                   other.onEntryAction) &&
                    Objects.equals(onExitAction,
                                   other.onExitAction) &&
                    Objects.equals(content,
                                   other.content) &&
                    Objects.equals(slaDueDate,
                                   other.slaDueDate);
        }
        return false;
    }
}
