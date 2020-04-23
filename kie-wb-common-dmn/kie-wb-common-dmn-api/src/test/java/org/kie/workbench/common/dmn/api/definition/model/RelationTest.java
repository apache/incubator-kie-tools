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

package org.kie.workbench.common.dmn.api.definition.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RelationTest {

    private static final String RELATION_ID = "RELATION-ID";
    private static final String DESCRIPTION = "DESCRIPTION";
    private Relation relation;

    @Before
    public void setup() {
        this.relation = spy(new Relation());
    }

    @Test
    public void testGetHasTypeRefs() {
        final InformationItem column1 = mock(InformationItem.class);
        final InformationItem column2 = mock(InformationItem.class);
        final List<InformationItem> column = asList(column1, column2);
        final org.kie.workbench.common.dmn.api.definition.model.List row1 = mock(org.kie.workbench.common.dmn.api.definition.model.List.class);
        final org.kie.workbench.common.dmn.api.definition.model.List row2 = mock(org.kie.workbench.common.dmn.api.definition.model.List.class);
        final List<org.kie.workbench.common.dmn.api.definition.model.List> row = asList(row1, row2);
        final HasTypeRef hasTypeRef1 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef2 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef3 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef4 = mock(HasTypeRef.class);

        doReturn(column).when(relation).getColumn();
        doReturn(row).when(relation).getRow();

        when(column1.getHasTypeRefs()).thenReturn(asList(hasTypeRef1));
        when(column2.getHasTypeRefs()).thenReturn(asList(hasTypeRef2));
        when(row1.getHasTypeRefs()).thenReturn(asList(hasTypeRef3));
        when(row2.getHasTypeRefs()).thenReturn(asList(hasTypeRef4));

        final List<HasTypeRef> actualHasTypeRefs = relation.getHasTypeRefs();
        final List<HasTypeRef> expectedHasTypeRefs = asList(relation, hasTypeRef1, hasTypeRef2, hasTypeRef3, hasTypeRef4);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }

    @Test
    public void testComponentWidths() {
        assertEquals(relation.getRequiredComponentWidthCount(),
                     relation.getComponentWidths().size());
        relation.getComponentWidths().forEach(Assert::assertNull);
    }

    @Test
    public void testCopy() {
        final Relation source = new Relation(
                new Id(RELATION_ID),
                new Description(DESCRIPTION),
                BuiltInType.BOOLEAN.asQName(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        final Relation target = source.copy();

        assertNotNull(target);
        assertNotEquals(RELATION_ID, target.getId());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getTypeRef());
        assertTrue(target.getColumn().isEmpty());
        assertTrue(target.getRow().isEmpty());
    }
}
