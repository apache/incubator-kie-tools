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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties;

import java.util.Set;

import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.Interface;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.Operation;
import org.eclipse.bpmn2.ServiceTask;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.GenericServiceTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.Scripts;
import org.kie.workbench.common.stunner.bpmn.definition.property.service.GenericServiceTaskValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;

public class GenericServiceTaskPropertyWriter extends MultipleInstanceActivityPropertyWriter {

    private final ServiceTask task;
    private final Interface iface;

    public GenericServiceTaskPropertyWriter(ServiceTask task, VariableScope variableScope, Set<DataObject> dataObjects) {
        super(task, variableScope, dataObjects);
        this.task = task;
        this.iface = bpmn2.createInterface();
    }

    public void setValue(GenericServiceTaskValue value) {
        //1 Implementation
        String serviceImplementation = value.getServiceImplementation();
        task.setImplementation(GenericServiceTaskPropertyReader.getServiceImplementation(serviceImplementation));
        CustomAttribute.serviceImplementation.of(task).set(serviceImplementation);

        //-------------------------------------------------------------

        //2 Interface
        String serviceInterface = value.getServiceInterface();

        //in message
        final Message inMessage;
        ItemDefinition itemDefinitionInMsg = bpmn2.createItemDefinition();
        itemDefinitionInMsg.setId(task.getId() + "_InMessageType");
        itemDefinitionInMsg.setStructureRef(value.getInMessageStructure());
        addItemDefinition(itemDefinitionInMsg);

        inMessage = bpmn2.createMessage();
        inMessage.setId(task.getId() + "_InMessage");
        inMessage.setItemRef(itemDefinitionInMsg);
        addRootElement(inMessage);

        //out message
        final Message outMessage;
        ItemDefinition itemDefinitionOutMsg = bpmn2.createItemDefinition();
        itemDefinitionOutMsg.setId(task.getId() + "_OutMessageType");
        itemDefinitionOutMsg.setStructureRef(value.getOutMessagetructure());
        addItemDefinition(itemDefinitionOutMsg);

        outMessage = bpmn2.createMessage();
        outMessage.setId(task.getId() + "_OutMessage");
        outMessage.setItemRef(itemDefinitionOutMsg);
        addRootElement(outMessage);

        //custom attribute
        CustomAttribute.serviceInterface.of(task).set(serviceInterface);
        iface.setImplementationRef(serviceInterface);
        iface.setName(serviceInterface);
        iface.setId(task.getId() + "_ServiceInterface");

        //-------------------------------------------------------------

        //3 Operation
        String serviceOperation = value.getServiceOperation();
        CustomAttribute.serviceOperation.of(task).set(serviceOperation);

        Operation operation = bpmn2.createOperation();
        operation.setId(task.getId() + "_ServiceOperation");
        operation.setName(serviceOperation);
        operation.setImplementationRef(serviceOperation);

        iface.getOperations().add(operation);
        task.setOperationRef(operation);
        addInterfaceDefinition(iface);
        operation.setInMessageRef(inMessage);
        operation.setOutMessageRef(outMessage);
    }

    public void setAdHocAutostart(boolean autoStart) {
        CustomElement.autoStart.of(task).set(autoStart);
    }

    public void setAsync(boolean async) {
        CustomElement.async.of(task).set(async);
    }

    public void setSLADueDate(String slaDueDate) {
        CustomElement.slaDueDate.of(task).set(slaDueDate);
    }

    public void setOnEntryAction(OnEntryAction onEntryAction) {
        Scripts.setOnEntryAction(task, onEntryAction);
    }

    public void setOnExitAction(OnExitAction onExitAction) {
        Scripts.setOnExitAction(task, onExitAction);
    }
}
