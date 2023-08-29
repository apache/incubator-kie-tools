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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Optional;

import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.emf.common.util.ArrayDelegatingEList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.AssociationDeclaration;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.FormalExpressionBodyHandler;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InputAssignmentReaderTest {

    public static final String ID = "PARENT_ID";

    @Mock
    private DataInputAssociation association;

    @Mock
    DataInput element;

    // TODO: Kogito - @Test
    public void urlEncodeConstants() throws UnsupportedEncodingException {
        final String decoded = "<<<#!!!#>>>";
        final String expected = URLEncoder.encode(decoded, "UTF-8");

        final Assignment assignment = createAssignment(decoded);

        final InputAssignmentReader iar = new InputAssignmentReader(assignment, ID);

        final AssociationDeclaration associationDeclaration = iar.getAssociationDeclaration();

        assertEquals(AssociationDeclaration.Type.FromTo, associationDeclaration.getType());
        assertEquals(expected, associationDeclaration.getSource());
    }

    private static Assignment createAssignment(String decodedBody) {
        final Assignment assignment = bpmn2.createAssignment();
        final FormalExpression from = bpmn2.createFormalExpression();
        FormalExpressionBodyHandler.of(from).setBody(decodedBody);

        final FormalExpression to = bpmn2.createFormalExpression();
        FormalExpressionBodyHandler.of(to).setBody(ID);

        assignment.setFrom(from);
        assignment.setTo(to);
        return assignment;
    }

    @Test
    public void testNullBody() {
        final Assignment assignment = createAssignment(null);
        final InputAssignmentReader iar = new InputAssignmentReader(assignment, ID);
        final AssociationDeclaration associationDeclaration = iar.getAssociationDeclaration();

        assertEquals(AssociationDeclaration.Type.FromTo, associationDeclaration.getType());
        assertEquals("", associationDeclaration.getSource());
    }

    @Test
    public void testNullAssociations() {

        when(association.getSourceRef()).thenReturn(new ArrayDelegatingEList<ItemAwareElement>() {
            @Override
            public Object[] data() {
                return null;
            }
        });
        when(association.getAssignment()).thenReturn(new ArrayDelegatingEList<Assignment>() {
            @Override
            public Object[] data() {
                return null;
            }
        });

        when(association.getTargetRef()).thenReturn(element);

        when(element.getName()).thenReturn("someName");

        final Optional<InputAssignmentReader> reader = InputAssignmentReader.fromAssociation(association);
        assertEquals(false, reader.isPresent());
    }
}