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

import java.util.HashSet;
import java.util.List;

import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.impl.PropertyImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.InitializedVariable;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.ParsedAssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.FlatVariableScope;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.VariableScope;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ParsedAssignmentsInfoTest {

    private static final String ASSIGNMENTS_INFO = "Years of Service:Integer||Data Test:Boolean||[din]Years of Service=35,[dout]Data Test->BooleanTest";
    private static final String ASSIGNMENTS_INFO_DUPLICATE = "Years of Service:Integer||Data Test:Boolean||[din]Years of Service=35,[dout]Data Test->BooleanTest,[dout]Data Test->BooleanTest2";
    private static final String ASSIGNMENTS_INFO_NO_ASSOCIATION = "||Data Test||";

    private ParsedAssignmentsInfo tested;
    private ParsedAssignmentsInfo testedDuplicates;
    private ParsedAssignmentsInfo testedNoAssociation;

    @Before
    public void setup() {
        AssignmentsInfo assignmentsInfos = new AssignmentsInfo(ASSIGNMENTS_INFO);
        AssignmentsInfo assignmentsInfosDuplicates = new AssignmentsInfo(ASSIGNMENTS_INFO_DUPLICATE);
        AssignmentsInfo assignmentNoAssociation = new AssignmentsInfo(ASSIGNMENTS_INFO_NO_ASSOCIATION);
        tested = ParsedAssignmentsInfo.of(assignmentsInfos);
        testedDuplicates = ParsedAssignmentsInfo.of(assignmentsInfosDuplicates);
        testedNoAssociation = ParsedAssignmentsInfo.of(assignmentNoAssociation);
    }

    // TODO: Kogito - @Test
    public void testCreateInitializedInputVariables() {
        final String DATA_INPUT_ID = "_Years-of-ServiceInputX";
        final String DATA_INPUT_NAME = "Years of Service";
        final String DATA_INPUT_ASSOCIATION_ID = "Years of Service";
        final String DATA_INPUT_ASSOCIATION_VALUE = "<![CDATA[35]]>";
        final String INIT_INPUT_VAR_ID = "Years-of-Service";
        final String INIT_INPUT_VAR_TYPE = "Integer";

        VariableScope variableScope = new FlatVariableScope();
        List<InitializedVariable.InitializedInputVariable> initializedInputVariables =
                tested.createInitializedInputVariables("", variableScope, new HashSet<>());

        assertEquals(1, initializedInputVariables.size());

        InitializedVariable.InitializedInputVariable initializedInputVariable =
                initializedInputVariables.get(0);

        DataInput dataInput = initializedInputVariable.getDataInput();

        DataInputAssociation dataInputAssociation = initializedInputVariable.getDataInputAssociation();
        DataInput target = (DataInput) dataInputAssociation.getTargetRef();

        List<Assignment> assignments = dataInputAssociation.getAssignment();
        Assignment assignment = assignments.get(0);
        FormalExpression from = (FormalExpression) assignment.getFrom();

        String dataInputID = dataInput.getId();
        String dataInputName = dataInput.getName();
        String dataInputAssociationID = target.getName();
        String dataInputAssociationValue = from.getBody();
        String initVarID = initializedInputVariable.getIdentifier();
        String initVarType = initializedInputVariable.getType();

        assertEquals(dataInputID, DATA_INPUT_ID);
        assertEquals(dataInputName, DATA_INPUT_NAME);
        assertEquals(dataInputAssociationID, DATA_INPUT_ASSOCIATION_ID);
        assertEquals(dataInputAssociationValue, DATA_INPUT_ASSOCIATION_VALUE);
        assertEquals(initVarID, INIT_INPUT_VAR_ID);
        assertEquals(initVarType, INIT_INPUT_VAR_TYPE);
    }

    @Test
    public void testCreateInitializedOutputVariables() {
        final String DATA_OUTPUT_ID = "_Data-TestOutputX";
        final String DATA_OUTPUT_NAME = "Data Test";
        final String DATA_OUTPUT_ASSOCIATION_ID = "Data Test";
        final String DATA_OUTPUT_ASSOCIATION_VALUE = "BooleanTest";
        final String INIT_OUTPUT_VAR_ID = "Data-Test";
        final String INIT_OUTPUT_VAR_TYPE = "Boolean";

        VariableScope variableScope = new FlatVariableScope();
        variableScope.declare("", "BooleanTest", "Boolean");

        List<InitializedVariable.InitializedOutputVariable> initializedOutputVariables =
                tested.createInitializedOutputVariables("", variableScope, new HashSet<>());

        assertEquals(1, initializedOutputVariables.size());

        InitializedVariable.InitializedOutputVariable initializedOutputVariable =
                initializedOutputVariables.get(0);

        DataOutput dataOuput = initializedOutputVariable.getDataOutput();

        DataOutputAssociation dataOutputAssociation = initializedOutputVariable.getDataOutputAssociation();
        List<ItemAwareElement> sourceRef = dataOutputAssociation.getSourceRef();
        DataOutput source = (DataOutput) sourceRef.get(0);
        PropertyImpl target = (PropertyImpl) dataOutputAssociation.getTargetRef();

        String dataOuputID = dataOuput.getId();
        String dataOutputName = dataOuput.getName();
        String dataOutputAssociationID = source.getName();
        String dataOutputAssocationValue = target.getId();
        String initVarID = initializedOutputVariable.getIdentifier();
        String initVarType = initializedOutputVariable.getType();

        assertEquals(dataOuputID, DATA_OUTPUT_ID);
        assertEquals(dataOutputName, DATA_OUTPUT_NAME);
        assertEquals(dataOutputAssociationID, DATA_OUTPUT_ASSOCIATION_ID);
        assertEquals(dataOutputAssocationValue, DATA_OUTPUT_ASSOCIATION_VALUE);
        assertEquals(initVarID, INIT_OUTPUT_VAR_ID);
        assertEquals(initVarType, INIT_OUTPUT_VAR_TYPE);
    }

    @Test
    public void testCreateInitializedOutputVariablesNoAssociation() {
        final String DATA_OUTPUT_ID = "_Data-TestOutputX";
        final String DATA_OUTPUT_NAME = "Data Test";
        final String INIT_OUTPUT_VAR_ID = "Data-Test";

        VariableScope variableScope = new FlatVariableScope();
        List<InitializedVariable.InitializedOutputVariable> initializedOutputVariables =
                testedNoAssociation.createInitializedOutputVariables("", variableScope, new HashSet<>());

        assertEquals(1, initializedOutputVariables.size());

        InitializedVariable.InitializedOutputVariable initializedOutputVariable =
                initializedOutputVariables.get(0);
        DataOutput dataOuput = initializedOutputVariable.getDataOutput();
        DataOutputAssociation dataOutputAssociation = initializedOutputVariable.getDataOutputAssociation();
        String dataOuputID = dataOuput.getId();
        String dataOutputName = dataOuput.getName();
        String initVarID = initializedOutputVariable.getIdentifier();

        assertNull(dataOutputAssociation);
        assertEquals(dataOuputID, DATA_OUTPUT_ID);
        assertEquals(dataOutputName, DATA_OUTPUT_NAME);
        assertEquals(initVarID, INIT_OUTPUT_VAR_ID);
    }

    @Test
    public void testCreateInitializedOutputVariablesDuplicates() {
        final String DATA_OUTPUT_ID = "_Data-TestOutputX";
        final String DATA_OUTPUT_NAME = "Data Test";
        final String DATA_OUTPUT_ASSOCIATION_ID = "Data Test";
        final String DATA_OUTPUT_ASSOCIATION_VALUE_1 = "BooleanTest";
        final String DATA_OUTPUT_ASSOCIATION_VALUE_2 = "BooleanTest2";
        final String INIT_OUTPUT_VAR_ID = "Data-Test";
        final String INIT_OUTPUT_VAR_TYPE = "Boolean";

        VariableScope variableScope = new FlatVariableScope();
        variableScope.declare("", "BooleanTest", "Boolean");
        variableScope.declare("", "BooleanTest2", "Boolean");

        List<InitializedVariable.InitializedOutputVariable> initializedOutputVariables =
                testedDuplicates.createInitializedOutputVariables("", variableScope, new HashSet<>());

        assertEquals(2, initializedOutputVariables.size());

        //Test first variable
        InitializedVariable.InitializedOutputVariable initializedOutputVariable1 =
                initializedOutputVariables.get(0);

        DataOutput dataOuput1 = initializedOutputVariable1.getDataOutput();

        DataOutputAssociation dataOutputAssociation1 = initializedOutputVariable1.getDataOutputAssociation();
        List<ItemAwareElement> sourceRef1 = dataOutputAssociation1.getSourceRef();
        DataOutput source1 = (DataOutput) sourceRef1.get(0);
        PropertyImpl target1 = (PropertyImpl) dataOutputAssociation1.getTargetRef();

        String dataOuputID1 = dataOuput1.getId();
        String dataOutputName1 = dataOuput1.getName();
        String dataOutputAssociationID1 = source1.getName();
        String dataOutputAssocationValue1 = target1.getId();
        String initVarID1 = initializedOutputVariable1.getIdentifier();
        String initVarType1 = initializedOutputVariable1.getType();

        assertEquals(dataOuputID1, DATA_OUTPUT_ID);
        assertEquals(dataOutputName1, DATA_OUTPUT_NAME);
        assertEquals(dataOutputAssociationID1, DATA_OUTPUT_ASSOCIATION_ID);
        assertEquals(dataOutputAssocationValue1, DATA_OUTPUT_ASSOCIATION_VALUE_1);
        assertEquals(initVarID1, INIT_OUTPUT_VAR_ID);
        assertEquals(initVarType1, INIT_OUTPUT_VAR_TYPE);

        //Test duplicate variable
        InitializedVariable.InitializedOutputVariable initializedOutputVariable2 =
                initializedOutputVariables.get(1);

        DataOutput dataOuput2 = initializedOutputVariable2.getDataOutput();

        DataOutputAssociation dataOutputAssociation2 = initializedOutputVariable2.getDataOutputAssociation();
        List<ItemAwareElement> sourceRef2 = dataOutputAssociation2.getSourceRef();
        DataOutput source2 = (DataOutput) sourceRef2.get(0);
        PropertyImpl target2 = (PropertyImpl) dataOutputAssociation2.getTargetRef();

        String dataOuputID2 = dataOuput2.getId();
        String dataOutputName2 = dataOuput2.getName();
        String dataOutputAssociationID2 = source2.getName();
        String dataOutputAssocationValue2 = target2.getId();
        String initVarID2 = initializedOutputVariable2.getIdentifier();
        String initVarType2 = initializedOutputVariable2.getType();

        assertEquals(dataOuputID2, DATA_OUTPUT_ID);
        assertEquals(dataOutputName2, DATA_OUTPUT_NAME);
        assertEquals(dataOutputAssociationID2, DATA_OUTPUT_ASSOCIATION_ID);
        assertEquals(dataOutputAssocationValue2, DATA_OUTPUT_ASSOCIATION_VALUE_2);
        assertEquals(initVarID2, INIT_OUTPUT_VAR_ID);
        assertEquals(initVarType2, INIT_OUTPUT_VAR_TYPE);
    }

    @Test
    public void fromStringWithNoDataOutput() {
        ParsedAssignmentsInfo assignmentsInfo =
                ParsedAssignmentsInfo.fromString("|IntermediateMessageEventCatchingOutputVar1:String||");
        assertTrue(assignmentsInfo.getOutputs().getDeclarations().isEmpty());
        assertTrue(assignmentsInfo.getAssociations().getInputs().isEmpty());
        assertTrue(assignmentsInfo.getAssociations().getOutputs().isEmpty());
    }

    @Test
    public void fromString() {
        String original =
                "|input1:String,input2:String||output1:String,output2:String|[din]pv1->input1,[din]pv2->input2,[dout]output1->pv2,[dout]output2->pv2";
        assertParseUnparse(original);
    }

    @Test
    public void fromString2() {
        String original =
                "||IntermediateMessageEventCatchingOutputVar1:String||[dout]IntermediateMessageEventCatchingOutputVar1->var1";
        assertParseUnparse(original);
    }

    private void assertParseUnparse(String original) {
        ParsedAssignmentsInfo assignmentsInfo =
                ParsedAssignmentsInfo.fromString(original);

        String s1 = assignmentsInfo.toString();
        String s2 = ParsedAssignmentsInfo.fromString(s1).toString();

        assertEquals(original, s2);
    }
}