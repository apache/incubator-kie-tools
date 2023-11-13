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

package org.kie.workbench.common.stunner.bpmn.definition.property.task;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.subProcess.IsCase;

public interface BaseReusableSubprocessTaskExecutionSet extends BPMNPropertySet {

    CalledElement getCalledElement();

    Independent getIndependent();

    AbortParent getAbortParent();

    void setAbortParent(final AbortParent abortParent);

    WaitForCompletion getWaitForCompletion();

    void setCalledElement(final CalledElement calledElement);

    void setIndependent(final Independent independent);

    void setWaitForCompletion(final WaitForCompletion waitForCompletion);

    IsAsync getIsAsync();

    void setIsAsync(final IsAsync isAsync);

    OnEntryAction getOnEntryAction();

    void setOnEntryAction(final OnEntryAction onEntryAction);

    OnExitAction getOnExitAction();

    void setOnExitAction(final OnExitAction onExitAction);

    IsCase getIsCase();

    void setIsCase(final IsCase isCase);

    AdHocAutostart getAdHocAutostart();

    void setAdHocAutostart(AdHocAutostart adHocAutostart);

    IsMultipleInstance getIsMultipleInstance();

    void setIsMultipleInstance(IsMultipleInstance isMultipleInstance);

    MultipleInstanceExecutionMode getMultipleInstanceExecutionMode();

    void setMultipleInstanceExecutionMode(MultipleInstanceExecutionMode multipleInstanceExecutionMode);

    MultipleInstanceCollectionInput getMultipleInstanceCollectionInput();

    void setMultipleInstanceCollectionInput(MultipleInstanceCollectionInput multipleInstanceCollectionInput);

    MultipleInstanceDataInput getMultipleInstanceDataInput();

    void setMultipleInstanceDataInput(MultipleInstanceDataInput multipleInstanceDataInput);

    MultipleInstanceCollectionOutput getMultipleInstanceCollectionOutput();

    void setMultipleInstanceCollectionOutput(MultipleInstanceCollectionOutput multipleInstanceCollectionOutput);

    MultipleInstanceDataOutput getMultipleInstanceDataOutput();

    void setMultipleInstanceDataOutput(MultipleInstanceDataOutput multipleInstanceDataOutput);

    MultipleInstanceCompletionCondition getMultipleInstanceCompletionCondition();

    void setMultipleInstanceCompletionCondition(MultipleInstanceCompletionCondition multipleInstanceCompletionCondition);

    SLADueDate getSlaDueDate();
}
