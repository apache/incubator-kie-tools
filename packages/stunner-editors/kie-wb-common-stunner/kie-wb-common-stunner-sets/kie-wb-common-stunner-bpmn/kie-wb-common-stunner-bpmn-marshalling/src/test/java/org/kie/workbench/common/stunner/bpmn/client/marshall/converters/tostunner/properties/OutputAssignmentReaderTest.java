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

import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.impl.DataOutputAssociationImpl;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.URL;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.AssociationDeclaration;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
    public void testFromAssociationWithDataObjectItem() {
        ItemAwareElement item = mock(DataObject.class);
        DataOutputAssociation outputAssociation = mockDataOutputAssociation(SOURCE_NAME, item);
        OutputAssignmentReader outputReader = OutputAssignmentReader.fromAssociation(outputAssociation);
        assertNotNull(outputReader);
        assertNotNull(SOURCE_NAME, outputReader.getAssociationDeclaration());
    }

    @Test
    public void testFromAssociationWithExpression() {
        URL url = mock(URL.class);
        StringUtils.setURL(url);

        DataOutput output = spy(DataOutput.class);
        when(output.getName()).thenReturn(TARGET_NAME);
        Assignment assignment = spy(Assignment.class);
        FormalExpression to = mock(FormalExpression.class);
        when(assignment.getTo()).thenReturn(to);
        EList<Assignment> assignments = mock(EList.class);
        when(assignments.get(0)).thenReturn(assignment);
        DataOutputAssociationImpl out = spy(DataOutputAssociationImpl.class);
        when(out.getAssignment()).thenReturn(assignments);

        OutputAssignmentReader outputReader = OutputAssignmentReader.fromAssociation(out);
        assertNull(outputReader);

        EList<ItemAwareElement> outputs = mock(EList.class);
        when(outputs.get(0)).thenReturn(output);
        when(outputs.isEmpty()).thenReturn(false);
        when(out.getSourceRef()).thenReturn(outputs);

        outputReader = OutputAssignmentReader.fromAssociation(out);
        assertResult(TARGET_NAME, "", AssociationDeclaration.Type.FromTo, outputReader.getAssociationDeclaration());

        outputReader = OutputAssignmentReader.fromAssociation(out);
        assertResult(TARGET_NAME, "", AssociationDeclaration.Type.FromTo, outputReader.getAssociationDeclaration());
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
        when(outputAssociation.getSourceRef()).thenReturn(ECollections.singletonEList(dataOutput));
        when(outputAssociation.getTargetRef()).thenReturn(targetRef);
        return outputAssociation;
    }
}
