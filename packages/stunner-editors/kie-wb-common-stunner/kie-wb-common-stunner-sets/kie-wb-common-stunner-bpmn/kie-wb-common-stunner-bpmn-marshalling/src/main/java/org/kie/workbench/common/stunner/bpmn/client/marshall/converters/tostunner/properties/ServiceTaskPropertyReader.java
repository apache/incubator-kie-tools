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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomInput;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.ConverterUtils;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;

public class ServiceTaskPropertyReader extends TaskPropertyReader {

    private final WorkItemDefinition workItemDefinition;

    public ServiceTaskPropertyReader(Task task, WorkItemDefinition workItemDefinition, BPMNDiagram diagram, DefinitionResolver definitionResolver) {
        super(task, diagram, definitionResolver);
        this.workItemDefinition = workItemDefinition;
    }

    @Override
    public String getName() {
        String name = super.getName();
        if (ConverterUtils.isEmpty(name)) {
            return "";
        } else {
            return name;
        }
    }

    @Override
    public String getDocumentation() {
        String documentation = super.getDocumentation();
        if (ConverterUtils.isEmpty(documentation)) {
            String defaultDocumentation = workItemDefinition.getDocumentation();
            return defaultDocumentation == null ? "" : defaultDocumentation;
        } else {
            return documentation;
        }
    }

    public String getTaskName() {
        return CustomInput.taskName.of(task).get();
    }

    public boolean isAsync() {
        return CustomElement.async.of(element).get();
    }

    public boolean isAdHocAutoStart() {
        return CustomElement.autoStart.of(element).get();
    }

    public String getServiceTaskName() {
        return workItemDefinition.getName();
    }

    public String getServiceTaskCategory() {
        return workItemDefinition.getCategory();
    }

    public String getServiceTaskDefaultHandler() {
        return workItemDefinition.getDefaultHandler();
    }

    public String getServiceTaskDescription() {
        return workItemDefinition.getDescription();
    }

    public String getSlaDueDate() {
        return CustomElement.slaDueDate.of(element).get();
    }
}
