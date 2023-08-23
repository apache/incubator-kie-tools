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

package org.kie.workbench.common.dmn.api.definition.model;

import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContextTest {

    private static final String CONTEXT_ID = "CONTEXT-ID";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String UUID = "uuid";

    private Context context;

    @Before
    public void setup() {
        this.context = spy(new Context());
    }

    @Test
    public void testGetHasTypeRefs() {

        final ContextEntry contextEntry1 = mock(ContextEntry.class);
        final ContextEntry contextEntry2 = mock(ContextEntry.class);
        final List<ContextEntry> contextEntry = asList(contextEntry1, contextEntry2);
        final HasTypeRef hasTypeRef1 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef2 = mock(HasTypeRef.class);

        doReturn(contextEntry).when(context).getContextEntry();

        when(contextEntry1.getHasTypeRefs()).thenReturn(asList(hasTypeRef1));
        when(contextEntry2.getHasTypeRefs()).thenReturn(asList(hasTypeRef2));

        final List<HasTypeRef> actualHasTypeRefs = context.getHasTypeRefs();
        final List<HasTypeRef> expectedHasTypeRefs = asList(context, hasTypeRef1, hasTypeRef2);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }

    @Test
    public void testComponentWidths() {
        assertEquals(context.getRequiredComponentWidthCount(),
                     context.getComponentWidths().size());
        context.getComponentWidths().forEach(Assert::assertNull);
    }

    @Test
    public void testCopy() {
        final Context source = new Context(new Id(CONTEXT_ID), new Description(DESCRIPTION), BuiltInType.BOOLEAN.asQName());

        final Context target = source.copy();

        assertNotNull(target);
        assertNotEquals(CONTEXT_ID, target.getId().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getTypeRef());
    }

    @Test
    public void testExactCopy() {
        final Context source = new Context(new Id(CONTEXT_ID), new Description(DESCRIPTION), BuiltInType.BOOLEAN.asQName());

        final Context target = source.exactCopy();

        assertNotNull(target);
        assertEquals(CONTEXT_ID, target.getId().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getTypeRef());
    }

    @Test
    public void testFindDomainObject() {

        final ContextEntry entry1 = mock(ContextEntry.class);
        final ContextEntry entry2 = mock(ContextEntry.class);
        final ContextEntry entry3 = mock(ContextEntry.class);
        final DomainObject expectedDomainObject = mock(DomainObject.class);

        when(entry1.findDomainObject(UUID)).thenReturn(Optional.empty());
        when(entry2.findDomainObject(UUID)).thenReturn(Optional.empty());
        when(entry3.findDomainObject(UUID)).thenReturn(Optional.of(expectedDomainObject));

        context.getContextEntry().add(entry1);
        context.getContextEntry().add(entry2);
        context.getContextEntry().add(entry3);

        final Optional<DomainObject> actual = context.findDomainObject(UUID);

        assertTrue(actual.isPresent());
        assertEquals(expectedDomainObject, actual.get());
        verify(entry1).findDomainObject(UUID);
        verify(entry2).findDomainObject(UUID);
        verify(entry3).findDomainObject(UUID);
    }
}
