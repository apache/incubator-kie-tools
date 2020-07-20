/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.impl.DataOutputAssociationImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.AssociationDeclaration;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OutputAssignmentReaderTest {

    private static final String SOURCE_NAME = "SOURCE_NAME";
    private static final String TARGET_NAME = "TARGET_NAME";
    private static final String TARGET_ID = "TARGET_ID";

    @Test
    public void testFromAssociationWithPropertyName() {
        Property property = mockProperty(TARGET_ID, TARGET_NAME);
        DataOutputAssociation outputAssociation = mockDataOutputAssociation(SOURCE_NAME, property);
        OutputAssignmentReader outputReader = OutputAssignmentReader.fromAssociation(outputAssociation);
        assertNotNull(outputReader);
        assertResult(SOURCE_NAME, TARGET_NAME, AssociationDeclaration.Type.SourceTarget, outputReader.getAssociationDeclaration());
    }

    @Test
    public void testFromAssociationWithPropertyId() {
        Property property = mockProperty(TARGET_ID, null);
        DataOutputAssociation outputAssociation = mockDataOutputAssociation(SOURCE_NAME, property);
        OutputAssignmentReader outputReader = OutputAssignmentReader.fromAssociation(outputAssociation);
        assertNotNull(outputReader);
        assertResult(SOURCE_NAME, TARGET_ID, AssociationDeclaration.Type.SourceTarget, outputReader.getAssociationDeclaration());
    }

    @Test
    public void testFromAssociationWithExpression() {
        DataOutput output = spy(DataOutput.class);
        when(output.getName()).thenReturn(TARGET_NAME);
        Assignment assignment = spy(Assignment.class);
        FormalExpression to = mock(FormalExpression.class);
        when(assignment.getTo()).thenReturn(to);
        when(to.getBody()).thenReturn(SOURCE_NAME);
        List<Assignment> assignments = new ArrayList<Assignment>();
        assignments.add(assignment);
        DataOutputAssociationImpl out = spy(DataOutputAssociationImpl.class);
        when(out.getAssignment()).thenReturn(assignments);

        OutputAssignmentReader outputReader = OutputAssignmentReader.fromAssociation(out);
        assertNull(outputReader);

        List<ItemAwareElement> outputs = new ArrayList<>();
        outputs.add(output);
        when(out.getSourceRef()).thenReturn(outputs);

        outputReader = OutputAssignmentReader.fromAssociation(out);
        assertResult(TARGET_NAME, SOURCE_NAME, AssociationDeclaration.Type.FromTo, outputReader.getAssociationDeclaration());

        when(to.getBody()).thenReturn("null");
        outputReader = OutputAssignmentReader.fromAssociation(out);
        assertResult(TARGET_NAME, "", AssociationDeclaration.Type.FromTo, outputReader.getAssociationDeclaration());
    }

    @Test
    public void testFromAssociationWithDataObjectItem() {
        ItemAwareElement item = mock(DataObject.class);
        DataOutputAssociation outputAssociation = mockDataOutputAssociation(SOURCE_NAME, item);
        OutputAssignmentReader outputReader = OutputAssignmentReader.fromAssociation(outputAssociation);
        assertNotNull(outputReader);
        assertNotNull(SOURCE_NAME, outputReader.getAssociationDeclaration());
    }

    private void assertResult(String sourceName, String targetId, AssociationDeclaration.Type type, AssociationDeclaration associationDeclaration) {
        assertNotNull(associationDeclaration);
        assertEquals(sourceName, associationDeclaration.getSource());
        assertEquals(targetId, associationDeclaration.getTarget());
        assertEquals(type, associationDeclaration.getType());
        assertEquals(AssociationDeclaration.Direction.Output, associationDeclaration.getDirection());
    }

    private static Property mockProperty(String id, String name) {
        Property property = mock(Property.class);
        when(property.getId()).thenReturn(id);
        when(property.getName()).thenReturn(name);
        return property;
    }

    private static DataOutputAssociation mockDataOutputAssociation(String dataOutputName, ItemAwareElement targetRef) {
        DataOutputAssociation outputAssociation = mock(DataOutputAssociation.class);
        DataOutput dataOutput = mock(DataOutput.class);
        when(dataOutput.getName()).thenReturn(dataOutputName);
        List<ItemAwareElement> sourceRefs = Collections.singletonList(dataOutput);
        when(outputAssociation.getSourceRef()).thenReturn(sourceRefs);
        when(outputAssociation.getTargetRef()).thenReturn(targetRef);
        return outputAssociation;
    }
}
