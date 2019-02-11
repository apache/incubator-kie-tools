/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collections;
import java.util.List;

import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.Property;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.AssociationDeclaration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        assertResult(SOURCE_NAME, TARGET_NAME, outputReader.getAssociationDeclaration());
    }

    @Test
    public void testFromAssociationWithPropertyId() {
        Property property = mockProperty(TARGET_ID, null);
        DataOutputAssociation outputAssociation = mockDataOutputAssociation(SOURCE_NAME, property);
        OutputAssignmentReader outputReader = OutputAssignmentReader.fromAssociation(outputAssociation);
        assertNotNull(outputReader);
        assertResult(SOURCE_NAME, TARGET_ID, outputReader.getAssociationDeclaration());
    }

    @Test
    public void testFromAssociationWithNonPropertyItem() {
        ItemAwareElement item = mock(ItemAwareElement.class);
        DataOutputAssociation outputAssociation = mockDataOutputAssociation(SOURCE_NAME, item);
        assertNull(OutputAssignmentReader.fromAssociation(outputAssociation));
    }

    private void assertResult(String sourceName, String targetId, AssociationDeclaration associationDeclaration) {
        assertNotNull(associationDeclaration);
        assertEquals(sourceName, associationDeclaration.getSource());
        assertEquals(targetId, associationDeclaration.getTarget());
        assertEquals(AssociationDeclaration.Type.SourceTarget, associationDeclaration.getType());
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
