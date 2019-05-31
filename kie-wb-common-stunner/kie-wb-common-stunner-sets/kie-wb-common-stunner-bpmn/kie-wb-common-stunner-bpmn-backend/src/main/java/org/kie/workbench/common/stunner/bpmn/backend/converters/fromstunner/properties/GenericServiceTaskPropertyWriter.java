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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import org.eclipse.bpmn2.Interface;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.Operation;
import org.eclipse.bpmn2.ServiceTask;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomAttribute;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class GenericServiceTaskPropertyWriter extends ActivityPropertyWriter {

    private final ServiceTask task;
    private final Interface iface;
    private Message message;

    public GenericServiceTaskPropertyWriter(ServiceTask task, VariableScope variableScope) {
        super(task, variableScope);
        this.task = task;
        this.iface = bpmn2.createInterface();
    }

    public void setServiceImplementation(String serviceImplementation) {
        if (!serviceImplementation.equals("Java")) {
            serviceImplementation = "##WebService";
        }
        task.setImplementation(serviceImplementation);
        CustomAttribute.serviceImplementation.of(task).set(serviceImplementation);
    }

    public void setServiceOperation(String serviceOperation) {
        CustomAttribute.serviceOperation.of(task).set(serviceOperation);

        Operation operation = bpmn2.createOperation();
        operation.setId(task.getId() + "_ServiceOperation");
        operation.setName(serviceOperation);
        operation.setImplementationRef(serviceOperation);

        iface.getOperations().add(operation);
        task.setOperationRef(operation);
        addInterfaceDefinition(iface);
        operation.setInMessageRef(message);
    }

    public void setServiceInterface(String serviceInterface) {
        message = bpmn2.createMessage();
        message.setId(task.getId() + "_InMessage");
        ItemDefinition itemDefinition = bpmn2.createItemDefinition();
        itemDefinition.setId(task.getId() + "_InMessageType");
        addItemDefinition(itemDefinition);

        message.setItemRef(itemDefinition);
        addRootElement(message);

        CustomAttribute.serviceInterface.of(task).set(serviceInterface);
        iface.setImplementationRef(serviceInterface);
        iface.setName(serviceInterface);
        iface.setId(task.getId() + "_ServiceInterface");
    }
}
