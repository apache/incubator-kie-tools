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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.AssociationDeclaration.Direction.Input;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.AssociationDeclaration.Direction.Output;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.AssociationDeclaration.Type.FromTo;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.AssociationDeclaration.Type.SourceTarget;

public class AssociationDeclarationTest {

    private static final String INPUT_ASSIGNMENTS_VALUE = "[din]var1->input1";
    private static final String INPUT_ASSIGNMENTS_VALUE_MISSING = "[din]var1->";
    private static final String OUTPUT_ASSIGNMENTS_VALUE = "[dout]output1->var1";
    private static final String OUTPUT_ASSIGNMENTS_VALUE_MISSING = "[dout]output1->";

    @Test(expected = IllegalArgumentException.class)
    public void testFromStringNull() {
        AssociationDeclaration.fromString(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromStringEmpty() {
        AssociationDeclaration.fromString("");
    }

    @Test
    public void testFromStringInput() {
        AssociationDeclaration associationDeclaration = AssociationDeclaration.fromString(INPUT_ASSIGNMENTS_VALUE);
        assertEquals(associationDeclaration.getSource(), "var1");
        assertEquals(associationDeclaration.getTarget(), "input1");
        assertEquals(associationDeclaration.getType(), SourceTarget);
        assertEquals(associationDeclaration.getDirection(), Input);
    }

    @Test
    public void testFromStringOutput() {
        AssociationDeclaration associationDeclaration = AssociationDeclaration.fromString(OUTPUT_ASSIGNMENTS_VALUE);
        assertEquals(associationDeclaration.getSource(), "output1");
        assertEquals(associationDeclaration.getTarget(), "var1");
        assertEquals(associationDeclaration.getType(), SourceTarget);
        assertEquals(associationDeclaration.getDirection(), Output);
    }

    @Test
    public void testFromStringInputMissing() {
        AssociationDeclaration associationDeclaration = AssociationDeclaration.fromString(INPUT_ASSIGNMENTS_VALUE_MISSING);
        assertEquals(associationDeclaration.getSource(), "var1");
        assertEquals(associationDeclaration.getTarget(), "");
        assertEquals(associationDeclaration.getType(), SourceTarget);
        assertEquals(associationDeclaration.getDirection(), Input);
    }

    @Test
    public void testFromStringOutputMissing() {
        AssociationDeclaration associationDeclaration = AssociationDeclaration.fromString(OUTPUT_ASSIGNMENTS_VALUE_MISSING);
        assertEquals(associationDeclaration.getSource(), "output1");
        assertEquals(associationDeclaration.getTarget(), "");
        assertEquals(associationDeclaration.getType(), SourceTarget);
        assertEquals(associationDeclaration.getDirection(), Output);
    }

    @Test
    public void testEquals() {
        String source = "source";
        String target = "target";
        AssociationDeclaration declaration = new AssociationDeclaration(Input, FromTo, source, target);
        assertFalse(declaration.equals(source));

        AssociationDeclaration declaration2 = new AssociationDeclaration(Input, FromTo, source, target);
        assertEquals(declaration, declaration2);

        declaration2.setDirection(Output);
        assertFalse(declaration.equals(declaration2));

        declaration2.setDirection(Input);
        assertEquals(declaration, declaration2);
        declaration2.setType(SourceTarget);
        assertFalse(declaration.equals(declaration2));

        declaration2.setType(FromTo);
        assertEquals(declaration, declaration2);
        declaration2.setTarget(source);
        assertFalse(declaration.equals(declaration2));

        declaration2.setTarget(target);
        assertEquals(declaration, declaration2);
        declaration2.setSource(target);
        assertFalse(declaration.equals(declaration2));
    }
}