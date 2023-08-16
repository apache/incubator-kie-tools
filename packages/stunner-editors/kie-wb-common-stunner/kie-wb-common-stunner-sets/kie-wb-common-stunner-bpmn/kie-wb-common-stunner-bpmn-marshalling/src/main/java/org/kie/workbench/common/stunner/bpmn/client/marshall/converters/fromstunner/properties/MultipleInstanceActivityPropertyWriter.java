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

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.MultiInstanceLoopCharacteristics;
import org.eclipse.bpmn2.OutputSet;
import org.eclipse.bpmn2.Property;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Ids;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.FormalExpressionBodyHandler;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.Scripts.asCData;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.ConverterUtils.isEmpty;

public class MultipleInstanceActivityPropertyWriter extends ActivityPropertyWriter {

    private MultiInstanceLoopCharacteristics miloop;
    private InputOutputSpecification ioSpec;
    private InputSet inputSet;
    private OutputSet outputSet;

    public MultipleInstanceActivityPropertyWriter(Activity activity, VariableScope variableScope, Set<DataObject> dataObjects) {
        super(activity, variableScope, dataObjects);
    }

    public void setCollectionInput(String collectionInput) {
        if (isEmpty(collectionInput)) {
            return;
        }

        setUpLoopCharacteristics();
        String suffix = "IN_COLLECTION";
        String id = Ids.dataInput(activity.getId(), suffix);
        DataInput dataInputElement = createDataInput(id, suffix);
        ioSpec.getDataInputs().add(dataInputElement);

        // check whether this exist
        Optional<Property> property = findPropertyById(collectionInput);
        Optional<DataObject> dataObject = findDataObjectById(collectionInput);

        if (property.isPresent()) {
            processDataInput(dataInputElement, property.get());
        } else if (dataObject.isPresent()) {
            processDataInput(dataInputElement, dataObject.get());
        }

    }

    public void setCollectionOutput(String collectionOutput) {
        if (isEmpty(collectionOutput)) {
            return;
        }

        setUpLoopCharacteristics();
        String suffix = "OUT_COLLECTION";
        String id = Ids.dataOutput(activity.getId(), suffix);
        DataOutput dataOutputElement = createDataOutput(id, suffix);
        addSafe(ioSpec.getDataOutputs(), dataOutputElement);

        Optional<Property> property = findPropertyById(collectionOutput);
        Optional<DataObject> dataObject = findDataObjectById(collectionOutput);

        if (property.isPresent()) {
            processDataOutput(dataOutputElement, property.get());
        } else if (dataObject.isPresent()) {
            processDataOutput(dataOutputElement, dataObject.get());
        }
    }

    public void setInput(String name) {
        setInput(name, true);
    }

    protected void setInput(String name, boolean addDataInputAssociation) {
        if (isEmpty(name)) {
            return;
        }

        String[] variable = name.split(":");
        String variableName = getVariableName(variable);
        String variableType = getVariableType(variable);

        setUpLoopCharacteristics();
        DataInput miDataInputElement = createDataInput(variableName, variableName);
        ItemDefinition item = createItemDefinition(variableName, variableType);
        addItemDefinition(item);
        miDataInputElement.setItemSubjectRef(item);
        miloop.setInputDataItem(miDataInputElement);

        String id = Ids.dataInput(activity.getId(), variableName);
        DataInput dataInputElement = createDataInput(id, variableName);
        dataInputElement.setItemSubjectRef(item);
        addSafe(ioSpec.getDataInputs(), dataInputElement);
        addSafe(inputSet.getDataInputRefs(), dataInputElement);

        if (addDataInputAssociation) {
            DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
            dia.getSourceRef().add(miDataInputElement);
            dia.setTargetRef(dataInputElement);
            addSafe(activity.getDataInputAssociations(), dia);
        }
    }

    private String getVariableName(String[] parsedVariable) {
        return (parsedVariable.length > 0 && !parsedVariable[0].isEmpty()) ? parsedVariable[0] : "";
    }

    private String getVariableType(String[] parsedVariable) {
        return (parsedVariable.length > 1 && !parsedVariable[1].isEmpty()) ? parsedVariable[1] : Object.class.getName();
    }

    public void setOutput(String name) {
        setOutput(name, true);
    }

    public void setOutput(String name, boolean addDataOutputAssociation) {
        if (isEmpty(name)) {
            return;
        }

        String[] variable = name.split(":");
        String variableName = getVariableName(variable);
        String variableType = getVariableType(variable);

        setUpLoopCharacteristics();
        DataOutput miDataOutputElement = createDataOutput(variableName, variableName);
        ItemDefinition item = createItemDefinition(variableName, variableType);
        addItemDefinition(item);
        miDataOutputElement.setItemSubjectRef(item);
        miloop.setOutputDataItem(miDataOutputElement);

        String id = Ids.dataOutput(activity.getId(), variableName);
        DataOutput dataOutputElement = createDataOutput(id, variableName);
        dataOutputElement.setItemSubjectRef(item);
        addSafe(ioSpec.getDataOutputs(), dataOutputElement);
        addSafe(outputSet.getDataOutputRefs(), dataOutputElement);

        if (addDataOutputAssociation) {
            DataOutputAssociation doa = Bpmn2Factory.eINSTANCE.createDataOutputAssociation();
            doa.getSourceRef().add(dataOutputElement);
            doa.setTargetRef(miDataOutputElement);
            addSafe(activity.getDataOutputAssociations(), doa);
        }
    }

    public void setCompletionCondition(String expression) {
        if (!isEmpty(expression)) {
            setUpLoopCharacteristics();
            FormalExpression formalExpression = bpmn2.createFormalExpression();
            FormalExpressionBodyHandler.of(formalExpression).setBody(asCData(expression));
            miloop.setCompletionCondition(formalExpression);
        }
    }

    public void setIsSequential(boolean sequential) {
        setUpLoopCharacteristics();
        miloop.setIsSequential(sequential);
    }

    protected void setUpLoopCharacteristics() {
        if (miloop == null) {
            miloop = bpmn2.createMultiInstanceLoopCharacteristics();
            activity.setLoopCharacteristics(miloop);
            ioSpec = getIoSpecification();
            inputSet = getInputSet(ioSpec);
            outputSet = getOutputSet(ioSpec);
        }
    }

    protected static DataInput createDataInput(String id, String name) {
        DataInput dataInput = bpmn2.createDataInput();
        dataInput.setId(id);
        dataInput.setName(name);
        return dataInput;
    }

    protected static DataOutput createDataOutput(String id, String name) {
        DataOutput dataOutput = bpmn2.createDataOutput();
        dataOutput.setId(id);
        dataOutput.setName(name);
        return dataOutput;
    }

    protected ItemDefinition createItemDefinition(String name, String type) {
        ItemDefinition item = bpmn2.createItemDefinition();
        item.setId(Ids.multiInstanceItemType(activity.getId(), name));
        String varType = type.isEmpty() ? Object.class.getName() : type;
        item.setStructureRef(varType);
        return item;
    }

    protected static void addSafe(List<DataInput> inputs, DataInput dataInput) {
        inputs.removeIf(existingDataInput -> dataInput.getId().equals(existingDataInput.getId()));
        inputs.add(dataInput);
    }

    protected static void addSafe(List<DataInputAssociation> associations, DataInputAssociation inputAssociation) {
        associations.removeIf(existingDia -> inputAssociation.getTargetRef().getId().equals(existingDia.getTargetRef().getId()));
        associations.add(inputAssociation);
    }

    protected static void addSafe(List<DataOutput> outputs, DataOutput dataOutput) {
        outputs.removeIf(existingDataOutput -> dataOutput.getId().equals(existingDataOutput.getId()));
        outputs.add(dataOutput);
    }

    protected static void addSafe(List<DataOutputAssociation> associations, DataOutputAssociation outputAssociation) {
        associations.removeIf(existingDoa -> existingDoa.getSourceRef() != null && !existingDoa.getSourceRef().isEmpty() && outputAssociation.getSourceRef().get(0).getId().equals(existingDoa.getSourceRef().get(0).getId()));
        associations.add(outputAssociation);
    }

    private Optional<Property> findPropertyById(String id) {
        return variableScope.lookup(id)
                .map(VariableScope.Variable::getTypedIdentifier);
    }

    private Optional<DataObject> findDataObjectById(String id) {
        return  getDataObjects().stream().filter(elm -> elm.getId().equals(id)).findFirst();
    }

    private void processDataInput(DataInput dataInputElement, ItemAwareElement prop) {
        dataInputElement.setItemSubjectRef(prop.getItemSubjectRef());

        miloop.setLoopDataInputRef(dataInputElement);

        addSafe(inputSet.getDataInputRefs(), dataInputElement);

        DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
        dia.getSourceRef().add(prop);
        dia.setTargetRef(dataInputElement);
        addSafe(activity.getDataInputAssociations(), dia);
    }

    private void processDataOutput(DataOutput dataOutputElement, ItemAwareElement prop) {
        dataOutputElement.setItemSubjectRef(prop.getItemSubjectRef());

        miloop.setLoopDataOutputRef(dataOutputElement);

        addSafe(outputSet.getDataOutputRefs(), dataOutputElement);

        DataOutputAssociation doa = Bpmn2Factory.eINSTANCE.createDataOutputAssociation();
        doa.getSourceRef().add(dataOutputElement);
        doa.setTargetRef(prop);
        addSafe(activity.getDataOutputAssociations(), doa);
    }

}
