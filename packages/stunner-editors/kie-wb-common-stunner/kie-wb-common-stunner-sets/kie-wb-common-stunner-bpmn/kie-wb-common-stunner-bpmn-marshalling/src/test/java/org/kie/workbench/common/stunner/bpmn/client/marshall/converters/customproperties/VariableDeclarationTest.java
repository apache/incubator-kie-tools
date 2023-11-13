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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.impl.DataObjectImpl;
import org.eclipse.bpmn2.impl.PropertyImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.FlatVariableScope;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.VariableScope;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(GwtMockitoTestRunner.class)
public class VariableDeclarationTest {

    private static final String CONSTRUCTOR_IDENTIFIER = "Variable Declaration Test";
    private static final String CONSTRUCTOR_TYPE = "Integer";
    private static final String CONSTRUCTOR_TAGS = "[input;customTag]";

    private static final String VAR_IDENTIFIER = "Variable-Declaration-Test";
    private static final String VAR_NAME = "Variable Declaration Test";

    private VariableDeclaration tested;

    @Test
    public void testInitializedVariablesSourceTarget() {

        String ASSIGNMENTS_INFO = "BooleanTest:Boolean||Test:Boolean||[din]Data Test->BooleanTest,[dout]Data Test->BooleanTest";

        AssignmentsInfo assignmentsInfos = new AssignmentsInfo(ASSIGNMENTS_INFO);
        ParsedAssignmentsInfo tested2 = ParsedAssignmentsInfo.of(assignmentsInfos);

        VariableScope variableScope = new FlatVariableScope();
        List<InitializedVariable.InitializedInputVariable> initializedInputVariables = tested2.createInitializedInputVariables("", variableScope, new HashSet<>());
        assertEquals(1, initializedInputVariables.size());
        InitializedVariable.InitializedInputVariable initializedInputVariable = initializedInputVariables.get(0);

        DataInput dataInput = initializedInputVariable.getDataInput();
        assertEquals("Variable Input Name Dont Match", "BooleanTest", dataInput.getName());
        DataInputAssociation dataInputAssociation = initializedInputVariable.getDataInputAssociation();
        assertNull("Data Input Association must be null", dataInputAssociation);

        assertEquals("Variable Name Dont Match", "BooleanTest", initializedInputVariable.getIdentifier());
        assertEquals("Variable Type Dont Match", "Boolean", initializedInputVariable.getType());
        assertTrue("Variable must be a Variable Reference", initializedInputVariable instanceof InitializedVariable.InputVariableReference);

        /////////////////////////////////////////////////////////////

        ASSIGNMENTS_INFO = "BooleanTest:Boolean||Test:Boolean||[din]Data Test->BooleanTest,[dout]Data Test->BooleanTest";

        assignmentsInfos = new AssignmentsInfo(ASSIGNMENTS_INFO);
        tested2 = ParsedAssignmentsInfo.of(assignmentsInfos);

        variableScope = new FlatVariableScope();
        variableScope.declare("", "Data Test", "Boolean");
        initializedInputVariables = tested2.createInitializedInputVariables("", variableScope, new HashSet<>());
        assertEquals(1, initializedInputVariables.size());
        initializedInputVariable = initializedInputVariables.get(0);

        dataInput = initializedInputVariable.getDataInput();
        assertEquals("Variable Input Name Dont Match", "BooleanTest", dataInput.getName());
        dataInputAssociation = initializedInputVariable.getDataInputAssociation();
        assertNotNull("Data Input Association must not be null", dataInputAssociation);
        assertEquals("Variable Name Dont Match", "BooleanTest", initializedInputVariable.getIdentifier());
        assertEquals("Variable Type Dont Match", "Boolean", initializedInputVariable.getType());
        assertTrue("Variable must be a Variable Reference", initializedInputVariable instanceof InitializedVariable.InputVariableReference);

        /////////////////////////////////////////////////////////////

        ASSIGNMENTS_INFO = "BooleanTest:Boolean||Test:Boolean||[din]Data Test->BooleanTest,[dout]Data Test->BooleanTest";

        assignmentsInfos = new AssignmentsInfo(ASSIGNMENTS_INFO);
        tested2 = ParsedAssignmentsInfo.of(assignmentsInfos);

        variableScope = new FlatVariableScope();
        Set<DataObject> dataObjects = new HashSet<>();
        DataObject dataObject = mockDataObject("Data Test");
        dataObjects.add(dataObject);
        initializedInputVariables = tested2.createInitializedInputVariables("", variableScope, dataObjects);

        assertEquals(1, initializedInputVariables.size());
        initializedInputVariable = initializedInputVariables.get(0);

        dataInput = initializedInputVariable.getDataInput();
        assertEquals("Variable Input Name Dont Match", "BooleanTest", dataInput.getName());
        dataInputAssociation = initializedInputVariable.getDataInputAssociation();
        assertEquals("Data Objects Must be 1 Size", 1, dataInputAssociation.getSourceRef().size());
        assertEquals("Data Object name must be :Data test", "Data Test", dataInputAssociation.getSourceRef().get(0).getId());
        assertEquals("Variable Name Dont Match", "BooleanTest", initializedInputVariable.getIdentifier());
        assertEquals("Variable Type Dont Match", "Boolean", initializedInputVariable.getType());
        assertTrue("Variable must be a Variable Reference", initializedInputVariable instanceof InitializedVariable.InputVariableReference);

        /////////////////////////////////////////////////////////////

        ASSIGNMENTS_INFO = "BooleanTest:Boolean||Test:Boolean||[din]Data Test->BooleanTest,[dout]Data Test->BooleanTest";

        assignmentsInfos = new AssignmentsInfo(ASSIGNMENTS_INFO);
        tested2 = ParsedAssignmentsInfo.of(assignmentsInfos);

        variableScope = new FlatVariableScope();
        dataObjects.clear();
        dataObject = mockDataObject("Test");
        dataObjects.add(dataObject);
        initializedInputVariables = tested2.createInitializedInputVariables("", variableScope, dataObjects);

        assertEquals(1, initializedInputVariables.size());
        initializedInputVariable = initializedInputVariables.get(0);

        dataInput = initializedInputVariable.getDataInput();
        assertEquals("Variable Input Name Dont Match", "BooleanTest", dataInput.getName());
        dataInputAssociation = initializedInputVariable.getDataInputAssociation();
        assertNull("Input Association must be null", dataInputAssociation);
        assertEquals("Variable Name Dont Match", "BooleanTest", initializedInputVariable.getIdentifier());
        assertEquals("Variable Type Dont Match", "Boolean", initializedInputVariable.getType());
        assertTrue("Variable must be a Variable Reference", initializedInputVariable instanceof InitializedVariable.InputVariableReference);

        /////////////////////////////////////////////////////////////
        ASSIGNMENTS_INFO = "Data Test:Boolean||[din]Data Test->BooleanTest,[dout]Data Test->BooleanTest";

        assignmentsInfos = new AssignmentsInfo(ASSIGNMENTS_INFO);
        tested2 = ParsedAssignmentsInfo.of(assignmentsInfos);
        variableScope = new FlatVariableScope();
        initializedInputVariables = tested2.createInitializedInputVariables("", variableScope, new HashSet<>());
        assertEquals(1, initializedInputVariables.size());

        initializedInputVariable = initializedInputVariables.get(0);
        dataInput = initializedInputVariable.getDataInput();
        assertEquals("Variable Input Name Dont Match", "Data Test", dataInput.getName());

        assertEquals("Variable Name Dont Match", "Data-Test", initializedInputVariable.getIdentifier());
        assertEquals("Variable Type Dont Match", "Boolean", initializedInputVariable.getType());
        assertTrue("Variable must be an Empty Input reference", initializedInputVariable instanceof InitializedVariable.InputEmpty);

        //////////////////////////////////////
        ASSIGNMENTS_INFO = "Years of Service:Integer||Data Test:Boolean||[din]Years of Service=35,[dout]Data Test->BooleanTest";
        assignmentsInfos = new AssignmentsInfo(ASSIGNMENTS_INFO);
        tested2 = ParsedAssignmentsInfo.of(assignmentsInfos);

        final String DATA_INPUT_ID = "_Years-of-ServiceInputX";
        final String DATA_INPUT_NAME = "Years of Service";
        final String DATA_INPUT_ASSOCIATION_ID = "Years of Service";
        final String INIT_INPUT_VAR_ID = "Years-of-Service";
        final String INIT_INPUT_VAR_TYPE = "Integer";

        variableScope = new FlatVariableScope();
        initializedInputVariables = tested2.createInitializedInputVariables("", variableScope, new HashSet<>());
        assertEquals(1, initializedInputVariables.size());
        initializedInputVariable = initializedInputVariables.get(0);
        dataInput = initializedInputVariable.getDataInput();

        dataInputAssociation = initializedInputVariable.getDataInputAssociation();
        DataInput target = (DataInput) dataInputAssociation.getTargetRef();

        String dataInputID = dataInput.getId();
        String dataInputName = dataInput.getName();
        String dataInputAssociationID = target.getName();
        String initVarID = initializedInputVariable.getIdentifier();
        String initVarType = initializedInputVariable.getType();

        assertEquals(dataInputID, DATA_INPUT_ID);
        assertEquals(dataInputName, DATA_INPUT_NAME);
        assertEquals(dataInputAssociationID, DATA_INPUT_ASSOCIATION_ID);
        assertEquals(initVarID, INIT_INPUT_VAR_ID);
        assertEquals(initVarType, INIT_INPUT_VAR_TYPE);

        //////////////////////////////////////////////////////////////////////

        String DATA_OUTPUT_ID = "_Data-TestOutputX";
        String DATA_OUTPUT_NAME = "Data Test";
        String DATA_OUTPUT_ASSOCIATION_ID = "Data Test";
        String DATA_OUTPUT_ASSOCIATION_VALUE = "BooleanTest";
        String INIT_OUTPUT_VAR_ID = "Data-Test";
        String INIT_OUTPUT_VAR_TYPE = "Boolean";

        variableScope = new FlatVariableScope();
        variableScope.declare("", "BooleanTest", "Boolean");

        List<InitializedVariable.InitializedOutputVariable> initializedOutputVariables = tested2.createInitializedOutputVariables("", variableScope, new HashSet<>());
        assertEquals(1, initializedOutputVariables.size());
        InitializedVariable.InitializedOutputVariable initializedOutputVariable = initializedOutputVariables.get(0);
        DataOutput dataOutput = initializedOutputVariable.getDataOutput();
        DataOutputAssociation dataOutputAssociation = initializedOutputVariable.getDataOutputAssociation();
        List<ItemAwareElement> sourceRef = dataOutputAssociation.getSourceRef();
        DataOutput source = (DataOutput) sourceRef.get(0);
        PropertyImpl targetOutput = (PropertyImpl) dataOutputAssociation.getTargetRef();

        String dataOuputID = dataOutput.getId();
        String dataOutputName = dataOutput.getName();
        String dataOutputAssociationID = source.getName();
        String dataOutputAssocationValue = targetOutput.getId();
        initVarID = initializedOutputVariable.getIdentifier();
        initVarType = initializedOutputVariable.getType();

        assertEquals(dataOuputID, DATA_OUTPUT_ID);
        assertEquals(dataOutputName, DATA_OUTPUT_NAME);
        assertEquals(dataOutputAssociationID, DATA_OUTPUT_ASSOCIATION_ID);
        assertEquals(dataOutputAssocationValue, DATA_OUTPUT_ASSOCIATION_VALUE);
        assertEquals(initVarID, INIT_OUTPUT_VAR_ID);
        assertEquals(initVarType, INIT_OUTPUT_VAR_TYPE);

        ///////////////////////////////////////////////////////////
        // Test no Data Objects
        DATA_OUTPUT_ID = "_Data-TestOutputX";
        DATA_OUTPUT_NAME = "Data Test";
        DATA_OUTPUT_ASSOCIATION_ID = "Data Test";
        DATA_OUTPUT_ASSOCIATION_VALUE = "BooleanTest";
        INIT_OUTPUT_VAR_ID = "Data-Test";
        INIT_OUTPUT_VAR_TYPE = "Boolean";

        variableScope = new FlatVariableScope();
        variableScope.declare("", "BooleanTest", "Boolean");

        initializedOutputVariables = tested2.createInitializedOutputVariables("", variableScope, new HashSet<>());
        assertEquals(1, initializedOutputVariables.size());
        initializedOutputVariable = initializedOutputVariables.get(0);
        dataOutput = initializedOutputVariable.getDataOutput();
        dataOutputAssociation = initializedOutputVariable.getDataOutputAssociation();
        sourceRef = dataOutputAssociation.getSourceRef();
        source = (DataOutput) sourceRef.get(0);
        targetOutput = (PropertyImpl) dataOutputAssociation.getTargetRef();

        dataOuputID = dataOutput.getId();
        dataOutputName = dataOutput.getName();
        dataOutputAssociationID = source.getName();
        dataOutputAssocationValue = targetOutput.getId();
        initVarID = initializedOutputVariable.getIdentifier();
        initVarType = initializedOutputVariable.getType();

        assertEquals(dataOuputID, DATA_OUTPUT_ID);
        assertEquals(dataOutputName, DATA_OUTPUT_NAME);
        assertEquals(dataOutputAssociationID, DATA_OUTPUT_ASSOCIATION_ID);
        assertEquals(dataOutputAssocationValue, DATA_OUTPUT_ASSOCIATION_VALUE);
        assertEquals(initVarID, INIT_OUTPUT_VAR_ID);
        assertEquals(initVarType, INIT_OUTPUT_VAR_TYPE);


        ///////////////////////////////////////////////////////////
        // Test Null Data Output Association
        ASSIGNMENTS_INFO = "BooleanTest:Boolean||Test:Boolean||[din]Data Test->BooleanTest,[dout]Data Test->BooleanTest";

        assignmentsInfos = new AssignmentsInfo(ASSIGNMENTS_INFO);
        tested2 = ParsedAssignmentsInfo.of(assignmentsInfos);

        DATA_OUTPUT_ID = "_Data-TestOutputX";
        DATA_OUTPUT_NAME = "Data Test";
        DATA_OUTPUT_ASSOCIATION_ID = "Data Test";
        DATA_OUTPUT_ASSOCIATION_VALUE = "BooleanTest";
        INIT_OUTPUT_VAR_ID = "Data-Test";
        INIT_OUTPUT_VAR_TYPE = "Boolean";

        variableScope = new FlatVariableScope();
        variableScope.declare("", "BooleanTest", "Boolean");

        initializedOutputVariables = tested2.createInitializedOutputVariables("", variableScope, new HashSet<>());
        assertEquals(1, initializedOutputVariables.size());
        initializedOutputVariable = initializedOutputVariables.get(0);
        dataOutput = initializedOutputVariable.getDataOutput();
        dataOutputAssociation = initializedOutputVariable.getDataOutputAssociation();
        // No Data Objects
        assertNull(dataOutputAssociation);

        //////////////////////////////////////////////////////////

        DATA_OUTPUT_ID = "_Data-TestOutputX";
        DATA_OUTPUT_NAME = "Data Test";
        DATA_OUTPUT_ASSOCIATION_ID = "Data Test";
        DATA_OUTPUT_ASSOCIATION_VALUE = "BooleanTest";
        INIT_OUTPUT_VAR_ID = "Data-Test";
        INIT_OUTPUT_VAR_TYPE = "Boolean";

        variableScope = new FlatVariableScope();
        variableScope.declare("", "BooleanTest2", "Boolean");

        ASSIGNMENTS_INFO = "Years of Service:Integer||Data Test:Boolean||[din]Years of Service=35,[dout]Data Test->BooleanTest";

        assignmentsInfos = new AssignmentsInfo(ASSIGNMENTS_INFO);
        tested2 = ParsedAssignmentsInfo.of(assignmentsInfos);

        dataObjects.clear();
        dataObject = mockDataObject("BooleanTest");
        dataObjects.add(dataObject);
        initializedOutputVariables = tested2.createInitializedOutputVariables("", variableScope, dataObjects);

        assertEquals(1, initializedOutputVariables.size());
        initializedOutputVariable = initializedOutputVariables.get(0);
        dataOutput = initializedOutputVariable.getDataOutput();
        dataOutputAssociation = initializedOutputVariable.getDataOutputAssociation();

        assertNotNull("Data Output Association must not be null", dataOutputAssociation);
        assertEquals("Variable Output Name Dont Match", "Data Test", dataOutput.getName());

        dataOuputID = dataOutput.getId();
        dataOutputName = dataOutput.getName();
        dataOutputAssociationID = source.getName();
        dataOutputAssocationValue = targetOutput.getId();
        initVarID = initializedOutputVariable.getIdentifier();
        initVarType = initializedOutputVariable.getType();

        assertEquals(dataOuputID, DATA_OUTPUT_ID);
        assertEquals(dataOutputName, DATA_OUTPUT_NAME);
        assertEquals(dataOutputAssociationID, DATA_OUTPUT_ASSOCIATION_ID);
        assertEquals(dataOutputAssocationValue, DATA_OUTPUT_ASSOCIATION_VALUE);
        assertEquals(initVarID, INIT_OUTPUT_VAR_ID);
        assertEquals(initVarType, INIT_OUTPUT_VAR_TYPE);

        ////////////////////////////////////////////////////
        // Test no matching Data Object
        DATA_OUTPUT_ID = "_Data-TestOutputX";
        DATA_OUTPUT_NAME = "Data Test";
        DATA_OUTPUT_ASSOCIATION_ID = "Data Test";
        DATA_OUTPUT_ASSOCIATION_VALUE = "BooleanTest";
        INIT_OUTPUT_VAR_ID = "Data-Test";
        INIT_OUTPUT_VAR_TYPE = "Boolean";

        variableScope = new FlatVariableScope();
        variableScope.declare("", "BooleanTest2", "Boolean");

        ASSIGNMENTS_INFO = "Years of Service:Integer||Data Test:Boolean||[din]Years of Service=35,[dout]Data Test->BooleanTest";

        assignmentsInfos = new AssignmentsInfo(ASSIGNMENTS_INFO);
        tested2 = ParsedAssignmentsInfo.of(assignmentsInfos);

        dataObjects.clear();
        dataObject = mockDataObject("BooleanTests");
        dataObjects.add(dataObject);
        initializedOutputVariables = tested2.createInitializedOutputVariables("", variableScope, dataObjects);

        assertEquals(1, initializedOutputVariables.size());
        initializedOutputVariable = initializedOutputVariables.get(0);
        dataOutput = initializedOutputVariable.getDataOutput();
        dataOutputAssociation = initializedOutputVariable.getDataOutputAssociation();

        assertNull("Data Output Association must be null", dataOutputAssociation);
        assertEquals("Variable Output Name Dont Match", "Data Test", dataOutput.getName());

        dataOuputID = dataOutput.getId();
        dataOutputName = dataOutput.getName();
        dataOutputAssociationID = source.getName();
        dataOutputAssocationValue = targetOutput.getId();
        initVarID = initializedOutputVariable.getIdentifier();
        initVarType = initializedOutputVariable.getType();

        assertEquals(dataOuputID, DATA_OUTPUT_ID);
        assertEquals(dataOutputName, DATA_OUTPUT_NAME);
        assertEquals(dataOutputAssociationID, DATA_OUTPUT_ASSOCIATION_ID);
        assertEquals(dataOutputAssocationValue, DATA_OUTPUT_ASSOCIATION_VALUE);
        assertEquals(initVarID, INIT_OUTPUT_VAR_ID);
        assertEquals(initVarType, INIT_OUTPUT_VAR_TYPE);
    }

    @Test
    public void testDataObjectEquals() {
        DataObject dataObject1 = mockDataObject("dataObject1");
        DataObject dataObject2 = mockDataObject("dataObject2");
        DataObjectImpl dataObject1Cast = (DataObjectImpl) dataObject1;
        DataObjectImpl dataObject2Cast = (DataObjectImpl) dataObject2;

        assertFalse(dataObject1Cast.equals(dataObject2Cast));
        assertTrue(dataObject1Cast.equals(dataObject1Cast));
        assertFalse(dataObject1Cast.equals("someString"));
        dataObject2 = mockDataObject("dataObject1");
        dataObject2Cast = (DataObjectImpl) dataObject2;
        assertFalse(dataObject1Cast.equals(dataObject2Cast));
    }

    private static DataObject mockDataObject(String id) {
        DataObject element = Bpmn2Factory.eINSTANCE.createDataObject();
        element.setId(id);
        return element;
    }

    @Before
    public void setup() {
        tested = new VariableDeclaration(CONSTRUCTOR_IDENTIFIER, CONSTRUCTOR_TYPE, CONSTRUCTOR_TAGS);
    }

    @Test
    public void testIdentifier() {
        String identifier = tested.getIdentifier();
        assertEquals(VAR_IDENTIFIER, identifier);
    }

    @Test
    public void testName() {
        String name = tested.getTypedIdentifier().getName();
        assertEquals(VAR_NAME, name);
    }

    @Test
    public void testTags() {
        String tags = tested.getTags();
        assertEquals(CONSTRUCTOR_TAGS, tags);
    }

    @Test
    public void testEquals() {
        VariableDeclaration comparable = new VariableDeclaration(CONSTRUCTOR_IDENTIFIER, CONSTRUCTOR_TYPE, CONSTRUCTOR_TAGS);
        assertEquals(tested, comparable);
    }

    @Test
    public void testNotEquals() {
        VariableDeclaration comparable = new VariableDeclaration(CONSTRUCTOR_IDENTIFIER, CONSTRUCTOR_TYPE, "[input;customTagX]");
        assertNotEquals(tested, comparable);
    }

    @Test
    public void testToString() {
        assertEquals(tested.toString(), CONSTRUCTOR_IDENTIFIER + ":" + CONSTRUCTOR_TYPE + ":" + CONSTRUCTOR_TAGS);
        assertNotEquals(tested.toString(), CONSTRUCTOR_IDENTIFIER + ":" + CONSTRUCTOR_TYPE + ":" + "[myCustomTag]");

        VariableDeclaration comparable = new VariableDeclaration(CONSTRUCTOR_IDENTIFIER, CONSTRUCTOR_TYPE, null);
        assertEquals(comparable.toString(), CONSTRUCTOR_IDENTIFIER + ":" + CONSTRUCTOR_TYPE);

        comparable = new VariableDeclaration(CONSTRUCTOR_IDENTIFIER, CONSTRUCTOR_TYPE, "");
        assertEquals(comparable.toString(), CONSTRUCTOR_IDENTIFIER + ":" + CONSTRUCTOR_TYPE);
    }

    @Test
    public void testToStringNoException() {
        VariableDeclaration comparable = VariableDeclaration.fromString("" + CONSTRUCTOR_TYPE);
        assertEquals(comparable.toString(), CONSTRUCTOR_TYPE);
    }
}
