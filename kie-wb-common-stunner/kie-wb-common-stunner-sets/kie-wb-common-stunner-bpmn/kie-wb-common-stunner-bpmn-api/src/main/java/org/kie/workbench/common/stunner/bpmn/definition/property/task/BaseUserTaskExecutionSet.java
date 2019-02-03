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

package org.kie.workbench.common.stunner.bpmn.definition.property.task;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.bpmn.definition.property.assignee.Actors;
import org.kie.workbench.common.stunner.bpmn.definition.property.assignee.Groupid;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.Priority;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;

public interface BaseUserTaskExecutionSet extends BPMNPropertySet {

    TaskName getTaskName();

    Actors getActors();

    Groupid getGroupid();

    AssignmentsInfo getAssignmentsinfo();

    IsAsync getIsAsync();

    Skippable getSkippable();

    Priority getPriority();

    Subject getSubject();

    Description getDescription();

    CreatedBy getCreatedBy();

    AdHocAutostart getAdHocAutostart();

    OnEntryAction getOnEntryAction();

    OnExitAction getOnExitAction();

    Content getContent();

    SLADueDate getSlaDueDate();
}
