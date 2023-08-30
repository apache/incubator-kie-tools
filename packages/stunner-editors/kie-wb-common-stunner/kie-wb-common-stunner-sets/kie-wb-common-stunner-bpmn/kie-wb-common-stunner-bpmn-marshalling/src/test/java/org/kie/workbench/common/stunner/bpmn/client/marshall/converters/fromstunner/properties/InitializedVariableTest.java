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

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;

import com.google.gwt.safehtml.shared.UriUtils;
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
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.URL;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.InitializedVariable;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.VariableDeclaration;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Ids;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.FormalExpressionBodyHandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.InitializedVariable.createCustomInput;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.Scripts.asCData;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InitializedVariableTest {

    private VariableScope varScope;

    // TODO: Kogito - @Test
    public void urlDecodeConstants() throws UnsupportedEncodingException {
        String expected = "<<<#!!!#>>>";
        String encoded = UriUtils.encode(expected);

        VariableDeclaration variable = new VariableDeclaration("PARENT_ID", "Object");
        InitializedVariable.InputConstant c = new InitializedVariable.InputConstant("PARENT", variable, encoded);

        Assignment assignment = c.getDataInputAssociation().getAssignment().get(0);

        FormalExpression to = (FormalExpression) assignment.getTo();
        assertEquals(Ids.dataInput("PARENT", "PARENT_ID"), FormalExpressionBodyHandler.of(to).getBody());

        FormalExpression from = (FormalExpression) assignment.getFrom();
        assertEquals(asCData(expected), from.getBody());
    }

    @Before
    public void setup() {
        varScope = new FlatVariableScope();
        varScope.declare("", "BooleanSource", "Boolean");
        varScope.declare("", "BooleanTarget", "Boolean");
    }

    @Test
    public void testGetDataInput() {

        final String SOURCE_VAR = "BooleanSource";
        final String DATA_INPUT_ID = "_Data-Input-TestInputX";
        final String DATA_INPUT_NAME = "Data Input Test";
        final String DATA_INPUT_ASSOCIATION_ID = "Data Input Test";
        final String DATA_INPUT_ASSOCIATION_VALUE = "BooleanSource";
        final String INIT_INPUT_VAR_ID = "Data-Input-Test";
        final String INIT_INPUT_VAR_TYPE = "Boolean";

        final VariableDeclaration varDeclaration = new VariableDeclaration("Data Input Test", "Boolean");

        InitializedVariable.InitializedInputVariable initializedInputVar =
                new InitializedVariable.InputVariableReference(
                        "",
                        varScope,
                        varDeclaration,
                        SOURCE_VAR,
                        new HashSet<>()
                );

        DataInput dataInput = initializedInputVar.getDataInput();

        DataInputAssociation dataInputAssociation = initializedInputVar.getDataInputAssociation();
        List<ItemAwareElement> sourceRef = dataInputAssociation.getSourceRef();
        PropertyImpl source = (PropertyImpl) sourceRef.get(0);
        DataInput target = (DataInput) dataInputAssociation.getTargetRef();

        String dataInputId = dataInput.getId();
        String dataInputName = dataInput.getName();
        String dataInputAssociationID = target.getName();
        String dataInputAssociationValue = source.getId();
        String initVarID = initializedInputVar.getIdentifier();
        String initVarType = initializedInputVar.getType();

        assertEquals(dataInputId, DATA_INPUT_ID);
        assertEquals(dataInputName, DATA_INPUT_NAME);
        assertEquals(dataInputAssociationID, DATA_INPUT_ASSOCIATION_ID);
        assertEquals(dataInputAssociationValue, DATA_INPUT_ASSOCIATION_VALUE);
        assertEquals(initVarID, INIT_INPUT_VAR_ID);
        assertEquals(initVarType, INIT_INPUT_VAR_TYPE);
    }

    @Test
    public void testInputCustomVariable() {
        URL url = mock(URL.class);

        StringUtils.setURL(url);
        VariableDeclaration declaration = new VariableDeclaration("Identifier", "type");

        when(url.decodeQueryString("#{expression}")).thenReturn("#{expression}");
        InitializedVariable.InitializedInputVariable input = createCustomInput("parent", declaration, "#{expression}");
        assertTrue(input instanceof InitializedVariable.InputExpression);

        when(url.decodeQueryString("constant")).thenReturn("constant");
        input = createCustomInput("parent", declaration, "constant");
        assertTrue(input instanceof InitializedVariable.InputConstant);
    }

    @Test
    public void testGetDataOuput() {
        final VariableDeclaration varDeclaration = new VariableDeclaration("Data Output Test", "Boolean");
        final String TARGET_VAR = "BooleanTarget";

        final String DATA_OUTPUT_ID = "_Data-Output-TestOutputX";
        final String DATA_OUTPUT_NAME = "Data Output Test";
        final String DATA_OUTPUT_ASSOCIATION_ID = "Data Output Test";
        final String DATA_OUTPUT_ASSOCIATION_VALUE = "BooleanTarget";
        final String INIT_OUTPUT_VAR_ID = "Data-Output-Test";
        final String INIT_OUTPUT_VAR_TYPE = "Boolean";

        InitializedVariable.InitializedOutputVariable initializedOutputVar =
                new InitializedVariable.OutputVariableReference(
                        "",
                        varScope,
                        varDeclaration,
                        TARGET_VAR,
                        new HashSet<>()
                );

        DataOutput dataOuput = initializedOutputVar.getDataOutput();

        DataOutputAssociation dataOutputAssociation = initializedOutputVar.getDataOutputAssociation();
        List<ItemAwareElement> sourceRef = dataOutputAssociation.getSourceRef();
        DataOutput source = (DataOutput) sourceRef.get(0);
        PropertyImpl target = (PropertyImpl) dataOutputAssociation.getTargetRef();

        String dataOuputID = dataOuput.getId();
        String dataOutputName = dataOuput.getName();
        String dataOutputAssociationID = source.getName();
        String dataOutputAssocationValue = target.getId();
        String initVarID = initializedOutputVar.getIdentifier();
        String initVarType = initializedOutputVar.getType();

        assertEquals(dataOuputID, DATA_OUTPUT_ID);
        assertEquals(dataOutputName, DATA_OUTPUT_NAME);
        assertEquals(dataOutputAssociationID, DATA_OUTPUT_ASSOCIATION_ID);
        assertEquals(dataOutputAssocationValue, DATA_OUTPUT_ASSOCIATION_VALUE);
        assertEquals(initVarID, INIT_OUTPUT_VAR_ID);
        assertEquals(initVarType, INIT_OUTPUT_VAR_TYPE);
    }
}