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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.MultiInstanceLoopCharacteristics;
import org.eclipse.bpmn2.OutputSet;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.SubProcess;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Ids;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.Scripts.asCData;

public class MultipleInstanceSubProcessPropertyWriter extends SubProcessPropertyWriter {

    private final MultiInstanceLoopCharacteristics miloop;
    private final InputOutputSpecification ioSpec;
    private final InputSet inputSet;
    private final OutputSet outputSet;

    public MultipleInstanceSubProcessPropertyWriter(SubProcess process, VariableScope variableScope) {
        super(process, variableScope);
        this.miloop = bpmn2.createMultiInstanceLoopCharacteristics();
        process.setLoopCharacteristics(miloop);
        this.ioSpec = bpmn2.createInputOutputSpecification();
        this.inputSet = bpmn2.createInputSet();
        this.ioSpec.getInputSets().add(inputSet);
        this.outputSet = bpmn2.createOutputSet();
        this.ioSpec.getOutputSets().add(outputSet);

        process.setIoSpecification(ioSpec);
    }

    public void setCollectionInput(String collectionInput) {
        // ignore empty input
        if (collectionInput == null) {
            return;
        }

        DataInput dataInputElement = createDataInput("IN_COLLECTION");
        Property prop = findPropertyById(collectionInput); // check whether this exist or throws
        miloop.setLoopDataInputRef(dataInputElement);
        this.inputSet.getDataInputRefs().add(dataInputElement);

        DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
        dia.getSourceRef().add(prop);
        dia.setTargetRef(dataInputElement);
        process.getDataInputAssociations().add(dia);
    }

    public void setInput(String value) {
        DataInput dataInput = createDataInput(value);
        miloop.setInputDataItem(dataInput);
    }

    public void setCollectionOutput(String collectionOutput) {
        // ignore empty input
        if (collectionOutput == null) {
            return;
        }

        DataOutput dataOutputElement = createDataOutput("OUT_COLLECTION");
        Property prop = findPropertyById(collectionOutput); // check whether this exist or throws
        miloop.setLoopDataOutputRef(dataOutputElement);
        ItemDefinition item = bpmn2.createItemDefinition();
        item.setId(Ids.multiInstanceItemType(process.getId(), "OUT_COLLECTION"));
        dataOutputElement.setItemSubjectRef(item);
        this.addItemDefinition(item);
        this.outputSet.getDataOutputRefs().add(dataOutputElement);

        DataOutputAssociation doa = Bpmn2Factory.eINSTANCE.createDataOutputAssociation();
        doa.getSourceRef().add(dataOutputElement);
        doa.setTargetRef(prop);
        process.getDataOutputAssociations().add(doa);
    }

    public void setOutput(String name) {
        DataOutput dataOutput = createDataOutput(name);
        miloop.setOutputDataItem(dataOutput);
        ItemDefinition item = bpmn2.createItemDefinition();
        item.setId(Ids.multiInstanceItemType(process.getId(), name));
        dataOutput.setItemSubjectRef(item);
        this.addItemDefinition(item);
    }

    private Property findPropertyById(String id) {
        return variableScope.lookup(id).getTypedIdentifier();
    }

    public DataInput createDataInput(String name) {
        DataInput dataInput = bpmn2.createDataInput();
        dataInput.setId(Ids.dataInput(process.getId(), name));
        dataInput.setName(name);

        this.ioSpec.getDataInputs().add(dataInput);
        return dataInput;
    }

    public DataOutput createDataOutput(String value) {
        DataOutput dataOutput = bpmn2.createDataOutput();
        dataOutput.setId(Ids.dataOutput(process.getId(), value));
        dataOutput.setName(value);

        this.ioSpec.getDataOutputs().add(dataOutput);
        return dataOutput;
    }

    public void setCompletionCondition(String expression) {
        FormalExpression formalExpression = bpmn2.createFormalExpression();
        formalExpression.setBody(asCData(expression));
        this.miloop.setCompletionCondition(formalExpression);
    }
}
