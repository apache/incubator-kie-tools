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

import java.util.Optional;

import org.eclipse.bpmn2.Interface;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.Operation;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.definition.property.service.GenericServiceTaskValue;
import org.kie.workbench.common.stunner.core.util.StringUtils;

public class GenericServiceTaskPropertyReader extends MultipleInstanceActivityPropertyReader {

    public static final String JAVA = GenericServiceTaskValue.JAVA;
    public static final String WEB_SERVICE = "WebService";
    private final ServiceTask task;

    public GenericServiceTaskPropertyReader(ServiceTask task, BPMNDiagram diagram, DefinitionResolver definitionResolver) {
        super(task, diagram, definitionResolver);
        this.task = task;
    }

    public GenericServiceTaskValue getGenericServiceTask() {
        GenericServiceTaskValue value = new GenericServiceTaskValue();
        final String implementation = Optional.ofNullable(CustomAttribute.serviceImplementation.of(task).get())
                .filter(StringUtils::nonEmpty)
                .orElseGet(() -> task.getImplementation());
        value.setServiceImplementation(getServiceImplementation(implementation));

        final String operation = Optional.ofNullable(CustomAttribute.serviceOperation.of(task).get())
                .filter(StringUtils::nonEmpty)
                .orElseGet(() -> Optional
                        .ofNullable(task.getOperationRef())
                        .map(Operation::getName)
                        .orElse(null));
        value.setServiceOperation(operation);

        value.setInMessageStructure(Optional.ofNullable(task.getOperationRef())
                                            .map(Operation::getInMessageRef)
                                            .map(Message::getItemRef)
                                            .map(ItemDefinition::getStructureRef)
                                            .orElse(null));

        value.setOutMessagetructure(Optional.ofNullable(task.getOperationRef())
                                            .map(Operation::getOutMessageRef)
                                            .map(Message::getItemRef)
                                            .map(ItemDefinition::getStructureRef)
                                            .orElse(null));

        final String serviceInterface = Optional.ofNullable(CustomAttribute.serviceInterface.of(task).get())
                .filter(StringUtils::nonEmpty)
                .orElseGet(() -> Optional
                        .ofNullable(task.getOperationRef())
                        .map(Operation::eContainer)
                        .filter(container -> container instanceof Interface)
                        .map(container -> (Interface) container)
                        .map(Interface::getName)
                        .orElse(null));
        value.setServiceInterface(serviceInterface);

        return value;
    }

    public static String getServiceImplementation(String implementation) {
        return Optional.ofNullable(implementation)
                                               .filter(StringUtils::nonEmpty)
                                               .filter(impl -> JAVA.equalsIgnoreCase(impl))
                                               .map(java -> JAVA)//assert that matches "Java"
                                               .orElse(WEB_SERVICE);
    }

    public boolean isAsync() {
        return CustomElement.async.of(element).get();
    }

    public boolean isAdHocAutostart() {
        return CustomElement.autoStart.of(element).get();
    }

    public String getSLADueDate() {
        return CustomElement.slaDueDate.of(element).get();
    }
}
