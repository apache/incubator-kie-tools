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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.FormalExpression;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.AssociationDeclaration;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

@RunWith(MockitoJUnitRunner.class)
public class InputAssignmentReaderTest {

    public static final String ID = "PARENT_ID";

    @Test
    public void urlEncodeConstants() throws UnsupportedEncodingException {
        final String decoded = "<<<#!!!#>>>";
        final String expected = URLEncoder.encode(decoded, "UTF-8");

        final Assignment assignment = createAssignment(decoded);

        final InputAssignmentReader iar = new InputAssignmentReader(assignment, ID);

        final AssociationDeclaration associationDeclaration = iar.getAssociationDeclaration();

        assertEquals(AssociationDeclaration.Type.FromTo, associationDeclaration.getType());
        assertEquals(expected, associationDeclaration.getSource());
    }

    private Assignment createAssignment(String decodedBody) {
        final Assignment assignment = bpmn2.createAssignment();
        final FormalExpression from = bpmn2.createFormalExpression();
        from.setBody(decodedBody);

        final FormalExpression to = bpmn2.createFormalExpression();
        to.setBody(ID);

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
}