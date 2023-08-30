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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.processes;

import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.ConverterFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.AdHocSubProcessPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocActivationCondition;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocAutostart;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocCompletionCondition;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocOrdering;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class SubProcessConverter extends BaseSubProcessConverter<AdHocSubprocess, ProcessData, AdHocSubprocessTaskExecutionSet> {

    public SubProcessConverter(TypedFactoryManager typedFactoryManager,
                               PropertyReaderFactory propertyReaderFactory,
                               DefinitionResolver definitionResolver,
                               ConverterFactory converterFactory) {
        super(typedFactoryManager,
              propertyReaderFactory,
              definitionResolver,
              converterFactory);
    }

    @Override
    protected Node<View<AdHocSubprocess>, Edge> createNode(String id) {
        return delegate.factoryManager.newNode(id, AdHocSubprocess.class);
    }

    @Override
    protected ProcessData createProcessData(String processVariables) {
        return new ProcessData(new ProcessVariables(processVariables));
    }

    @Override
    protected AdHocSubprocessTaskExecutionSet createAdHocSubprocessTaskExecutionSet(AdHocSubProcessPropertyReader p) {
        return new AdHocSubprocessTaskExecutionSet(new AdHocActivationCondition(p.getAdHocActivationCondition()),
                                                   new AdHocCompletionCondition(p.getAdHocCompletionCondition()),
                                                   new AdHocOrdering(p.getAdHocOrdering()),
                                                   new AdHocAutostart(p.isAdHocAutostart()),
                                                   new OnEntryAction(p.getOnEntryAction()),
                                                   new OnExitAction(p.getOnExitAction()),
                                                   new IsAsync(p.isAsync()),
                                                   new SLADueDate(p.getSlaDueDate()));
    }
}