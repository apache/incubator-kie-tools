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
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.AssociationDeclaration;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class InputAssignmentReaderTest {

    @Test
    public void urlEncodeConstants() throws UnsupportedEncodingException {
        String decoded = "<<<#!!!#>>>";
        String expected = URLEncoder.encode(decoded, "UTF-8");

        Assignment assignment = bpmn2.createAssignment();
        FormalExpression from = bpmn2.createFormalExpression();
        from.setBody(decoded);

        FormalExpression to = bpmn2.createFormalExpression();
        to.setBody("ID");

        assignment.setFrom(from);
        assignment.setTo(to);

        InputAssignmentReader iar = new InputAssignmentReader(assignment, "ID");

        AssociationDeclaration associationDeclaration = iar.getAssociationDeclaration();

        assertEquals(AssociationDeclaration.Type.FromTo, associationDeclaration.getType());
        assertEquals(expected, associationDeclaration.getSource());
    }
}