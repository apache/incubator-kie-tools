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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties;

import java.util.List;

import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Task;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Ids;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.FormalExpressionBodyHandler;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.Scripts.asCData;

public class CustomInput<T> {

    public static final CustomInputDefinition<String> taskName = new StringInput("TaskName", "Task");
    public static final CustomInputDefinition<String> priority = new StringInput("Priority", "");
    public static final CustomInputDefinition<String> subject = new StringInput("Comment", "");
    public static final CustomInputDefinition<String> description = new StringInput("Description", "");
    public static final CustomInputDefinition<String> createdBy = new StringInput("CreatedBy", "");
    public static final CustomInputDefinition<String> groupId = new StringInput("GroupId", "");
    public static final CustomInputDefinition<Boolean> skippable = new BooleanInput("Skippable", false);
    public static final CustomInputDefinition<String> content = new StringInput("Content", "");
    public static final CustomInputDefinition<String> fileName = new StringInput("fileName", "java.lang.String", "");
    public static final CustomInputDefinition<String> namespace = new StringInput("namespace", "java.lang.String", "");
    public static final CustomInputDefinition<String> dmnModelName = new StringInput("model", "java.lang.String", "");
    public static final CustomInputDefinition<String> decisionName = new StringInput("decision", "java.lang.String", "");
    public static final CustomInputDefinition<String> notStartedReassign = new StringInput("NotStartedReassign", "");
    public static final CustomInputDefinition<String> notCompletedReassign = new StringInput("NotCompletedReassign", "");
    public static final CustomInputDefinition<String> notStartedNotify = new StringInput("NotStartedNotify", "");
    public static final CustomInputDefinition<String> notCompletedNotify = new StringInput("NotCompletedNotify", "");

    private final CustomInputDefinition<T> inputDefinition;
    private final Task element;
    private final ItemDefinition typeDef;

    public CustomInput(CustomInputDefinition<T> inputDefinition, Task element) {
        this.inputDefinition = inputDefinition;
        this.element = element;
        this.typeDef = typedefInput(inputDefinition.name(), inputDefinition.type());
    }

    public ItemDefinition typeDef() {
        return typeDef;
    }

    public T get() {
        return inputDefinition.getValue(element);
    }

    public void set(T value) {
        setStringValue(String.valueOf(value));
    }

    private void setStringValue(String value) {
        if (value == null || value.isEmpty()) {
            return;
        }
        DataInputAssociation input = input(value);
        DataInput targetRef = (DataInput) input.getTargetRef();
        getIoSpecification(element).getDataInputs().add(targetRef);
        getIoSpecification(element).getInputSets().get(0).getDataInputRefs().add(targetRef);
        element.getDataInputAssociations().add(input);
    }

    private InputOutputSpecification getIoSpecification(Task element) {
        InputOutputSpecification ioSpecification = element.getIoSpecification();
        if (ioSpecification == null) {
            ioSpecification = bpmn2.createInputOutputSpecification();
            element.setIoSpecification(ioSpecification);
        }
        List<InputSet> inputSets = ioSpecification.getInputSets();
        if (inputSets.isEmpty()) {
            inputSets.add(bpmn2.createInputSet());
        }
        return ioSpecification;
    }

    private DataInputAssociation input(Object value) {
        // first we declare the type of this assignment

//        // then we declare the input that will provide
//        // the value that we assign to `source`
//        // e.g. myInput
        DataInput target = readInputFrom(inputDefinition.name(), typeDef);

        Assignment assignment = assignment(value.toString(), target.getId());

        // then we create the actual association between the two
        // e.g. foo := myInput (or, to put it differently, myInput -> foo)
        DataInputAssociation association =
                associationOf(assignment, target);

        return association;
    }

    private DataInput readInputFrom(String targetName, ItemDefinition typeDef) {
        DataInput dataInput = bpmn2.createDataInput();
        dataInput.setName(targetName);
        // the id is an encoding of the node id + the name of the input
        dataInput.setId(Ids.dataInput(element.getId(), targetName));
        dataInput.setItemSubjectRef(typeDef);
        CustomAttribute.dtype.of(dataInput).set(typeDef.getStructureRef());
        return dataInput;
    }

    private Assignment assignment(String from, String to) {
        Assignment assignment = bpmn2.createAssignment();
        FormalExpression fromExpr = bpmn2.createFormalExpression();
        FormalExpressionBodyHandler.of(fromExpr).setBody(asCData(from));
        assignment.setFrom(fromExpr);
        FormalExpression toExpr = bpmn2.createFormalExpression();
        FormalExpressionBodyHandler.of(toExpr).setBody(asCData(to));
        assignment.setTo(toExpr);
        return assignment;
    }

    private DataInputAssociation associationOf(Assignment assignment, DataInput dataInput) {
        DataInputAssociation dataInputAssociation =
                bpmn2.createDataInputAssociation();

        dataInputAssociation.getAssignment()
                .add(assignment);

        dataInputAssociation
                .setTargetRef(dataInput);
        return dataInputAssociation;
    }

    private ItemDefinition typedefInput(String name, String type) {
        ItemDefinition typeDef = bpmn2.createItemDefinition();
        typeDef.setId(Ids.dataInputItem(element.getId(), name));
        typeDef.setStructureRef(type);
        return typeDef;
    }
}
