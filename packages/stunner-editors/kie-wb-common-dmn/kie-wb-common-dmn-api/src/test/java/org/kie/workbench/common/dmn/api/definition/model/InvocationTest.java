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

import java.util.ArrayList;
import java.util.Arrays;
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InvocationTest {

    private static final String INVOCATION_ID = "INVOCATION-ID";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String UUID = "uuid";
    private Invocation invocation;

    @Before
    public void setup() {
        this.invocation = spy(new Invocation());
    }

    @Test
    public void testGetHasTypeRefs() {
        final Expression expression = mock(Expression.class);
        final Binding binding1 = mock(Binding.class); //added
        final Binding binding2 = mock(Binding.class); //added
        final List<Binding> binding = asList(binding1, binding2);
        final HasTypeRef hasTypeRef1 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef2 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef3 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef4 = mock(HasTypeRef.class);

        doReturn(expression).when(invocation).getExpression();
        doReturn(binding).when(invocation).getBinding();

        when(expression.getHasTypeRefs()).thenReturn(asList(hasTypeRef1, hasTypeRef2));
        when(binding1.getHasTypeRefs()).thenReturn(asList(hasTypeRef3));
        when(binding2.getHasTypeRefs()).thenReturn(asList(hasTypeRef4));

        final List<HasTypeRef> actualHasTypeRefs = invocation.getHasTypeRefs();
        final List<HasTypeRef> expectedHasTypeRefs = asList(invocation, hasTypeRef1, hasTypeRef2, hasTypeRef3, hasTypeRef4);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }

    @Test
    public void testComponentWidths() {
        assertEquals(invocation.getRequiredComponentWidthCount(),
                     invocation.getComponentWidths().size());
        invocation.getComponentWidths().forEach(Assert::assertNull);
    }

    @Test
    public void testCopy() {
        final Invocation source = new Invocation(
                new Id(INVOCATION_ID),
                new Description(DESCRIPTION),
                BuiltInType.BOOLEAN.asQName(),
                null,
                new ArrayList<>()
        );

        final Invocation target = source.copy();

        assertNotNull(target);
        assertNotEquals(INVOCATION_ID, target.getId().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getTypeRef());
        assertNull(target.getExpression());
        assertTrue(target.getBinding().isEmpty());
    }

    @Test
    public void testExactCopy() {
        final Invocation source = new Invocation(
                new Id(INVOCATION_ID),
                new Description(DESCRIPTION),
                BuiltInType.BOOLEAN.asQName(),
                null,
                new ArrayList<>()
        );

        final Invocation target = source.exactCopy();

        assertNotNull(target);
        assertEquals(INVOCATION_ID, target.getId().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getTypeRef());
        assertNull(target.getExpression());
        assertTrue(target.getBinding().isEmpty());
    }

    @Test
    public void testFindDomainObject_FromExpression() {

        final DomainObject expectedDomainObject = mock(DomainObject.class);
        final Expression expression = mock(Expression.class);
        final Invocation invocation = new Invocation();
        invocation.setExpression(expression);

        when(expression.findDomainObject(UUID)).thenReturn(Optional.of(expectedDomainObject));

        final Optional<DomainObject> actual = invocation.findDomainObject(UUID);

        verify(expression).findDomainObject(UUID);
        assertTrue(actual.isPresent());
        assertEquals(expectedDomainObject, actual.get());
    }

    @Test
    public void testFindDomainObject_FromBinding() {

        final DomainObject expectedDomainObject = mock(DomainObject.class);
        final Expression expression = mock(Expression.class);
        final Invocation invocation = new Invocation();
        final Binding binding1 = mock(Binding.class);
        final Binding binding2 = mock(Binding.class);
        final Binding binding3 = mock(Binding.class);

        invocation.getBinding().addAll(Arrays.asList(binding1, binding2, binding3));
        invocation.setExpression(expression);

        when(binding3.findDomainObject(UUID)).thenReturn(Optional.of(expectedDomainObject));

        final Optional<DomainObject> actual = invocation.findDomainObject(UUID);

        verify(expression).findDomainObject(UUID);
        verify(binding1).findDomainObject(UUID);
        verify(binding2).findDomainObject(UUID);
        verify(binding3).findDomainObject(UUID);

        assertTrue(actual.isPresent());
        assertEquals(expectedDomainObject, actual.get());
    }
}
