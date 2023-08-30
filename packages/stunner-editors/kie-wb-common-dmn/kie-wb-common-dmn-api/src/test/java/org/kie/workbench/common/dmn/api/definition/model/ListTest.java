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
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ListTest {

    private static final String LIST_ID = "LIST_ID";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String UUID = "uuid";

    private List list;

    @Before
    public void setup() {
        this.list = spy(new List());
    }

    @Test
    public void testGetHasTypeRefs() {
        final Expression expression1 = mock(Expression.class); //added
        final Expression expression2 = mock(Expression.class); //added
        final java.util.List<HasExpression> hasExpressions = asList(HasExpression.wrap(list, expression1), HasExpression.wrap(list, expression2));
        final HasTypeRef hasTypeRef1 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef2 = mock(HasTypeRef.class);

        doReturn(hasExpressions).when(list).getExpression();

        when(expression1.getHasTypeRefs()).thenReturn(asList(hasTypeRef1));
        when(expression2.getHasTypeRefs()).thenReturn(asList(hasTypeRef2));

        final java.util.List<HasTypeRef> actualHasTypeRefs = list.getHasTypeRefs();
        final java.util.List<HasTypeRef> expectedHasTypeRefs = asList(list, hasTypeRef1, hasTypeRef2);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }

    @Test
    public void testComponentWidths() {
        assertEquals(list.getRequiredComponentWidthCount(),
                     list.getComponentWidths().size());
        list.getComponentWidths().forEach(Assert::assertNull);
    }

    @Test
    public void testCopy() {
        final List source = new List(
                new Id(LIST_ID),
                new Description(DESCRIPTION),
                BuiltInType.BOOLEAN.asQName(),
                new ArrayList<>()
        );

        final List target = source.copy();

        assertNotNull(target);
        assertNotEquals(LIST_ID, target.getId().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getTypeRef());
        assertTrue(target.getExpression().isEmpty());
    }

    @Test
    public void testExactCopy() {
        final List source = new List(
                new Id(LIST_ID),
                new Description(DESCRIPTION),
                BuiltInType.BOOLEAN.asQName(),
                new ArrayList<>()
        );

        final List target = source.exactCopy();

        assertNotNull(target);
        assertEquals(LIST_ID, target.getId().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getTypeRef());
        assertTrue(target.getExpression().isEmpty());
    }

    @Test
    public void testFindDomainObject() {

        final List list = new List();
        final HasExpression hasExpression1 = mock(HasExpression.class);
        final HasExpression hasExpression2 = mock(HasExpression.class);
        final HasExpression hasExpression3 = mock(HasExpression.class);
        final HasExpression hasExpression4 = mock(HasExpression.class);

        final Expression expressionThatReturnsEmpty = mock(Expression.class);
        final Expression expression = mock(Expression.class);
        final DomainObject domainObject = mock(DomainObject.class);

        when(expression.findDomainObject(UUID)).thenReturn(Optional.of(domainObject));
        when(expressionThatReturnsEmpty.findDomainObject(UUID)).thenReturn(Optional.empty());
        when(hasExpression3.getExpression()).thenReturn(expressionThatReturnsEmpty);
        when(hasExpression4.getExpression()).thenReturn(expression);

        list.getExpression().addAll(Arrays.asList(hasExpression1,
                                                  hasExpression2,
                                                  hasExpression3,
                                                  hasExpression4));

        final Optional<DomainObject> foundDomainObject = list.findDomainObject(UUID);

        assertTrue(foundDomainObject.isPresent());
        assertEquals(domainObject, foundDomainObject.get());
    }

    @Test
    public void testFindDomainObject_WhenNothingHasBeenFound() {

        final List list = new List();
        final HasExpression hasExpression1 = mock(HasExpression.class);
        final HasExpression hasExpression2 = mock(HasExpression.class);
        final HasExpression hasExpression3 = mock(HasExpression.class);
        final HasExpression hasExpression4 = mock(HasExpression.class);

        list.getExpression().addAll(Arrays.asList(hasExpression1,
                                                  hasExpression2,
                                                  hasExpression3,
                                                  hasExpression4));

        final Optional<DomainObject> foundDomainObject = list.findDomainObject(UUID);

        assertFalse(foundDomainObject.isPresent());
    }
}
