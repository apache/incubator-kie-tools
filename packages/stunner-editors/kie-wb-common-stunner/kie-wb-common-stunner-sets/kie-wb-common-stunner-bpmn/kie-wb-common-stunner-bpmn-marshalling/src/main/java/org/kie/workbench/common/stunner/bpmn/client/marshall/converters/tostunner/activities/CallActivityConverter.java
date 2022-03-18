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

package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.activities;

import org.eclipse.bpmn2.CallActivity;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.CallActivityPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.subProcess.IsCase;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AbortParent;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocAutostart;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.CalledElement;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Independent;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsMultipleInstance;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCompletionCondition;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceExecutionMode;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.WaitForCompletion;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class CallActivityConverter extends BaseCallActivityConverter<ReusableSubprocess, ReusableSubprocessTaskExecutionSet> {

    public CallActivityConverter(TypedFactoryManager factoryManager,
                                 PropertyReaderFactory propertyReaderFactory) {
        super(factoryManager, propertyReaderFactory);
    }

    @Override
    protected Node<View<ReusableSubprocess>, Edge> createNode(CallActivity activity, CallActivityPropertyReader p) {
        return factoryManager.newNode(activity.getId(), ReusableSubprocess.class);
    }

    @Override
    protected ReusableSubprocessTaskExecutionSet createReusableSubprocessTaskExecutionSet(CallActivity activity,
                                                                                          CallActivityPropertyReader p) {
        return new ReusableSubprocessTaskExecutionSet(new CalledElement(activity.getCalledElement()),
                                                      new IsCase(p.isCase()),
                                                      new Independent(p.isIndependent()),
                                                      new AbortParent(p.isAbortParent()),
                                                      new WaitForCompletion(p.isWaitForCompletion()),
                                                      new IsAsync(p.isAsync()),
                                                      new AdHocAutostart(p.isAdHocAutostart()),
                                                      new IsMultipleInstance(p.isMultipleInstance()),
                                                      new MultipleInstanceExecutionMode(p.isSequential()),
                                                      new MultipleInstanceCollectionInput(p.getCollectionInput()),
                                                      new MultipleInstanceDataInput(p.getDataInput()),
                                                      new MultipleInstanceCollectionOutput(p.getCollectionOutput()),
                                                      new MultipleInstanceDataOutput(p.getDataOutput()),
                                                      new MultipleInstanceCompletionCondition(p.getCompletionCondition()),
                                                      new OnEntryAction(p.getOnEntryAction()),
                                                      new OnExitAction(p.getOnExitAction()),
                                                      new SLADueDate(p.getSlaDueDate()));
    }
}
