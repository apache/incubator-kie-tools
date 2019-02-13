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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

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
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.InitializedVariable;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.ParsedAssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.FlatVariableScope;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.VariableScope;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ParsedAssignmentsInfoTest {

    private static final String ASSIGNMENTS_INFO = "Years of Service:Integer||Data Test:Boolean||[din]Years of Service=35,[dout]Data Test->BooleanTest";

    private ParsedAssignmentsInfo tested;

    @Before
    public void setup() {
        AssignmentsInfo assignmentsInfos = new AssignmentsInfo(ASSIGNMENTS_INFO);
        tested = ParsedAssignmentsInfo.of(assignmentsInfos);
    }

    @Test
    public void testCreateInitializedInputVariables() {
        final String DATA_INPUT_ID = "_Years-of-ServiceInputX";
        final String DATA_INPUT_NAME = "Years of Service";
        final String DATA_INPUT_ASSOCIATION_ID = "Years of Service";
        final String DATA_INPUT_ASSOCIATION_VALUE = "<![CDATA[35]]>";
        final String INIT_INPUT_VAR_ID = "Years-of-Service";
        final String INIT_INPUT_VAR_TYPE = "Integer";

        VariableScope variableScope = new FlatVariableScope();
        List<InitializedVariable.InitializedInputVariable> initializedInputVariables =
                tested.createInitializedInputVariables("", variableScope);

        assertEquals(initializedInputVariables.size(), 1);

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
                tested.createInitializedOutputVariables("", variableScope);

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