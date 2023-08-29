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

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.DataAssociation;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.MultiInstanceLoopCharacteristics;
import org.eclipse.bpmn2.Property;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.FormalExpressionBodyHandler;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MultipleInstanceActivityPropertyWriterTest {

    private static final String ACTIVITY_ID = "ACTIVITY_ID";
    private static final String PROPERTY_ID = "PROPERTY_ID";
    private static final String ITEM_ID = "ITEM_ID";
    private static final String IN_COLLECTION = "IN_COLLECTION";
    private static final String OUT_COLLECTION = "OUT_COLLECTION";
    private static final String INPUT_VARIABLE_ID = "INPUT_VARIABLE_ID";
    private static final String OUTPUT_VARIABLE_ID = "OUTPUT_VARIABLE_ID";
    private static final String COMPLETION_CONDITION = "COMPLETION_CONDITION";

    private Activity activity;

    private VariableScope variableScope;

    private MultipleInstanceActivityPropertyWriter writer;

    @Before
    public void setUp() {
        activity = Factories.bpmn2.createUserTask();
        activity.setId(ACTIVITY_ID);
        variableScope = mock(VariableScope.class);
        VariableScope.Variable variable = mock(VariableScope.Variable.class);
        when(variableScope.lookup(PROPERTY_ID)).thenReturn(Optional.of(variable));
        Property property = mockProperty(PROPERTY_ID, ITEM_ID);
        when(variable.getTypedIdentifier()).thenReturn(property);
        writer = new MultipleInstanceActivityPropertyWriter(activity, variableScope, new HashSet<>());
    }

    @Test
    public void testSetInputNotFailed() {
        writer.setInput(null, false);
        writer.setInput("", false);

        assertNull(activity.getIoSpecification());
        assertTrue(activity.getDataInputAssociations().isEmpty());
        assertNull(activity.getLoopCharacteristics());
    }

    @Test
    public void testSetEmptyVariable() {
        final String emptyName = "";
        writer.setInput(":", false);

        assertInputsInitialized();
        String inputId = ACTIVITY_ID + "_" + "InputX";
        String itemSubjectRef = ACTIVITY_ID + "_multiInstanceItemType_";
        assertHasDataInput(activity.getIoSpecification(), inputId, itemSubjectRef, emptyName);
        assertDontHasDataInputAssociation(activity, INPUT_VARIABLE_ID, inputId);
    }

    private static DataObject mockDataObject(String id) {
        DataObject element = Bpmn2Factory.eINSTANCE.createDataObject();
        element.setId(id);
        return element;
    }

    @Test
    public void testSetCollectionInput() {
        writer.setCollectionInput(PROPERTY_ID);
        assertInputsInitialized();
        String inputId = ACTIVITY_ID + "_" + IN_COLLECTION + "InputX";
        assertHasDataInput(activity.getIoSpecification(), inputId, ITEM_ID, IN_COLLECTION);
        assertHasDataInputAssociation(activity, PROPERTY_ID, inputId);
        assertEquals(inputId, ((MultiInstanceLoopCharacteristics) activity.getLoopCharacteristics()).getLoopDataInputRef().getId());
        // Test with Data Objects
        when(variableScope.lookup(PROPERTY_ID)).thenReturn(Optional.empty());

        Set<DataObject> dataObjects = new HashSet<>();
        DataObject dataObject = mockDataObject(PROPERTY_ID);

        dataObjects.add(dataObject);
        writer = new MultipleInstanceActivityPropertyWriter(activity, variableScope, dataObjects);
        writer.setCollectionInput(PROPERTY_ID);
        assertInputsInitialized();
        inputId = ACTIVITY_ID + "_" + IN_COLLECTION + "InputX";
        assertHasDataInput(activity.getIoSpecification(), inputId, ITEM_ID, IN_COLLECTION);
        assertHasDataInputAssociation(activity, PROPERTY_ID, inputId);
        // Test no option
        when(variableScope.lookup(PROPERTY_ID)).thenReturn(Optional.empty());

        dataObjects.clear();
        dataObject = mockDataObject("SomeOtherId");

        dataObjects.add(dataObject);
        writer = new MultipleInstanceActivityPropertyWriter(activity, variableScope, dataObjects);
        writer.setCollectionInput(PROPERTY_ID);
        assertInputsInitialized();
        inputId = ACTIVITY_ID + "_" + IN_COLLECTION + "InputX";
        assertHasDataInput(activity.getIoSpecification(), inputId, ITEM_ID, IN_COLLECTION);
        assertHasDataInputAssociation(activity, PROPERTY_ID, inputId);
    }

    @Test
    public void testSetCollectionInputEmpty() {
        writer = spy(writer);
        writer.setCollectionInput(null);
        verify(writer, Mockito.times(0)).setUpLoopCharacteristics();
    }

    @Test
    public void setSetInputAndAddAssociation() {
        testSetInput(true);
    }

    @Test
    public void setSetInputAndDontAddAssociation() {
        testSetInput(false);
    }

    private void testSetInput(boolean addAssociation) {
        writer.setInput(INPUT_VARIABLE_ID, addAssociation);
        assertInputsInitialized();
        String inputId = ACTIVITY_ID + "_" + INPUT_VARIABLE_ID + "InputX";
        String itemSubjectRef = ACTIVITY_ID + "_multiInstanceItemType_" + INPUT_VARIABLE_ID;
        assertHasDataInput(activity.getIoSpecification(), inputId, itemSubjectRef, INPUT_VARIABLE_ID);
        if (addAssociation) {
            assertHasDataInputAssociation(activity, INPUT_VARIABLE_ID, inputId);
        } else {
            assertDontHasDataInputAssociation(activity, INPUT_VARIABLE_ID, inputId);
        }
    }

    @Test
    public void testSetOutputAndAddAssociation() {
        testSetOutput(true);
    }

    @Test
    public void testSetOutputAndDontAddAssociation() {
        testSetOutput(false);
    }

    private void testSetOutput(boolean addAssociation) {
        writer.setOutput(OUTPUT_VARIABLE_ID, true);
        assertOutputsInitialized();
        String outputId = ACTIVITY_ID + "_" + OUTPUT_VARIABLE_ID + "OutputX";
        String itemSubjectRef = ACTIVITY_ID + "_multiInstanceItemType_" + OUTPUT_VARIABLE_ID;
        assertHasDataOutput(activity.getIoSpecification(), outputId, itemSubjectRef, OUTPUT_VARIABLE_ID);
        if (addAssociation) {
            assertHasDataOutputAssociation(activity, outputId, OUTPUT_VARIABLE_ID);
        } else {
            assertDontHasDataInputAssociation(activity, outputId, OUTPUT_VARIABLE_ID);
        }
    }

    @Test
    public void testSetCollectionOutput() {
        writer.setCollectionOutput(PROPERTY_ID);
        assertOutputsInitialized();
        String outputId = ACTIVITY_ID + "_" + OUT_COLLECTION + "OutputX";
        assertHasDataOutput(activity.getIoSpecification(), outputId, ITEM_ID, OUT_COLLECTION);
        assertHasDataOutputAssociation(activity, outputId, PROPERTY_ID);
        assertEquals(outputId, ((MultiInstanceLoopCharacteristics) activity.getLoopCharacteristics()).getLoopDataOutputRef().getId());

        // Test with Data Objects
        when(variableScope.lookup(PROPERTY_ID)).thenReturn(Optional.empty());

        Set<DataObject> dataObjects = new HashSet<>();
        DataObject dataObject = mockDataObject(PROPERTY_ID);

        dataObjects.add(dataObject);
        writer = new MultipleInstanceActivityPropertyWriter(activity, variableScope, dataObjects);
        writer.setCollectionOutput(PROPERTY_ID);
        assertOutputsInitialized();
        outputId = ACTIVITY_ID + "_" + OUT_COLLECTION + "OutputX";
        assertHasDataOutputAssociation(activity, outputId, PROPERTY_ID);

        // Test no option
        when(variableScope.lookup(PROPERTY_ID)).thenReturn(Optional.empty());

        dataObjects.clear();
        dataObject = mockDataObject("SomeOtherId");

        dataObjects.add(dataObject);
        writer = new MultipleInstanceActivityPropertyWriter(activity, variableScope, dataObjects);
        writer.setCollectionOutput(PROPERTY_ID);
        assertOutputsInitialized();
        outputId = ACTIVITY_ID + "_" + OUT_COLLECTION + "OutputX";
        assertHasDataOutputAssociation(activity, outputId, PROPERTY_ID);
    }

    @Test
    public void testSetCompletionCondition() {
        writer.setCompletionCondition(COMPLETION_CONDITION);
        assertEquals("<![CDATA[COMPLETION_CONDITION]]>", FormalExpressionBodyHandler.of((FormalExpression) ((MultiInstanceLoopCharacteristics) activity.getLoopCharacteristics()).getCompletionCondition()).getBody());
    }

    @Test
    public void testSetIsSequentialTrue() {
        writer.setIsSequential(true);
        assertTrue(((MultiInstanceLoopCharacteristics) activity.getLoopCharacteristics()).isIsSequential());
    }

    @Test
    public void testSetIsSequentialFalse() {
        writer.setIsSequential(false);
        assertFalse(((MultiInstanceLoopCharacteristics) activity.getLoopCharacteristics()).isIsSequential());
    }

    private void assertInputsInitialized() {
        assertNotNull(activity.getIoSpecification());
        assertNotNull(activity.getIoSpecification().getDataInputs());
        assertNotNull(activity.getDataInputAssociations());
        assertNotNull(activity.getLoopCharacteristics());
    }

    private void assertOutputsInitialized() {
        assertNotNull(activity.getIoSpecification());
        assertNotNull(activity.getIoSpecification().getDataOutputs());
        assertNotNull(activity.getDataOutputAssociations());
        assertNotNull(activity.getLoopCharacteristics());
    }

    private void assertHasDataInput(InputOutputSpecification ioSpecification, String id, String itemSubjectRef, String name) {
        assertNotNull(ioSpecification.getDataInputs());
        Optional<DataInput> dataInput = ioSpecification.getDataInputs().stream()
                .filter(di -> Objects.equals(id, di.getId()))
                .filter(di -> Objects.equals(itemSubjectRef, di.getItemSubjectRef().getId()))
                .filter(di -> Objects.equals(name, di.getName()))
                .findFirst();
        if (!dataInput.isPresent()) {
            fail(String.format("DataInput: id: %s, itemSubjectRef: %s, name: %s was not found.", id, itemSubjectRef, name));
        }
    }

    private void assertHasDataOutput(InputOutputSpecification ioSpecification, String id, String itemSubjectRef, String name) {
        assertNotNull(ioSpecification.getDataOutputs());
        Optional<DataOutput> dataOutput = ioSpecification.getDataOutputs().stream()
                .filter(dout -> Objects.equals(id, dout.getId()))
                .filter(dout -> Objects.equals(itemSubjectRef, dout.getItemSubjectRef().getId()))
                .filter(dout -> Objects.equals(name, dout.getName()))
                .findFirst();
        if (!dataOutput.isPresent()) {
            fail(String.format("DataOutput: id: %s, itemSubjectRef: %s, name: %s was not found.", id, itemSubjectRef, name));
        }
    }

    private void assertHasDataInputAssociation(Activity activity, String sourceRef, String targetRef) {
        assertNotNull(activity.getDataInputAssociations());
        Optional<DataInputAssociation> inputAssociation = findDataAssociation(activity.getDataInputAssociations(), sourceRef, targetRef);
        if (!inputAssociation.isPresent()) {
            fail(String.format("DataInputAssociation sourceRef: %s, targetRef: %s was not found.", sourceRef, targetRef));
        }
    }

    private void assertDontHasDataInputAssociation(Activity activity, String sourceRef, String targetRef) {
        assertNotNull(activity.getDataInputAssociations());
        Optional<DataInputAssociation> inputAssociation = findDataAssociation(activity.getDataInputAssociations(), sourceRef, targetRef);
        if (inputAssociation.isPresent()) {
            fail(String.format("DataInputAssociation sourceRef: %s, targetRef: %s shouldn't be present.", sourceRef, targetRef));
        }
    }

    private void assertHasDataOutputAssociation(Activity activity, String sourceRef, String targetRef) {
        assertNotNull(activity.getDataOutputAssociations());
        Optional<DataOutputAssociation> outputAssociation = findDataAssociation(activity.getDataOutputAssociations(), sourceRef, targetRef);
        if (!outputAssociation.isPresent()) {
            fail(String.format("DataInputAssociation sourceRef: %s, targetRef: %s was not found.", sourceRef, targetRef));
        }
    }

    private <T extends DataAssociation> Optional<T> findDataAssociation(List<T> dataAssociations, String sourceRef, String targetRef) {
        assertNotNull(dataAssociations);
        return dataAssociations.stream()
                .filter(dia -> !dia.getSourceRef().isEmpty())
                .filter(dia -> Objects.equals(sourceRef, dia.getSourceRef().get(0).getId()))
                .filter(dia -> Objects.equals(targetRef, dia.getTargetRef().getId()))
                .findFirst();
    }

    private Property mockProperty(String name, String itemSubjectRef) {
        Property property = mock(Property.class);
        when(property.getId()).thenReturn(name);
        when(property.getName()).thenReturn(name);
        ItemDefinition itemDefinition = mock(ItemDefinition.class);
        when(itemDefinition.getId()).thenReturn(itemSubjectRef);
        when(property.getItemSubjectRef()).thenReturn(itemDefinition);
        return property;
    }
}
