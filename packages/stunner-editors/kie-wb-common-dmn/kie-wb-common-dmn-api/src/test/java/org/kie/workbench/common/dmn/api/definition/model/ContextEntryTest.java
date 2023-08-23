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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
public class ContextEntryTest {

    private static final String ITEM_ID = "item-id";
    private static final String DESCRIPTION = "description";
    private static final String INFORMATION_ITEM_NAME = "item-name";
    private static final String UUID = "uuid";
    private static final String ANOTHER_UUID = "another uuid";
    private ContextEntry contextEntry;

    @Before
    public void setup() {
        this.contextEntry = spy(new ContextEntry());
    }

    @Test
    public void testGetHasTypeRefs() {
        final Expression expression = mock(Expression.class);
        final InformationItem variable = mock(InformationItem.class);
        final HasTypeRef hasTypeRef1 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef2 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef3 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef4 = mock(HasTypeRef.class);

        doReturn(expression).when(contextEntry).getExpression();
        doReturn(variable).when(contextEntry).getVariable();

        when(expression.getHasTypeRefs()).thenReturn(asList(hasTypeRef1, hasTypeRef2));
        when(variable.getHasTypeRefs()).thenReturn(asList(hasTypeRef3, hasTypeRef4));

        final List<HasTypeRef> actualHasTypeRefs = contextEntry.getHasTypeRefs();
        final List<HasTypeRef> expectedHasTypeRefs = asList(hasTypeRef1, hasTypeRef2, hasTypeRef3, hasTypeRef4);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }

    @Test
    public void testCopy() {
        final ContextEntry source = new ContextEntry();
        final InformationItem informationItem = new InformationItem(new Id(ITEM_ID), new Description(DESCRIPTION), new Name(INFORMATION_ITEM_NAME), BuiltInType.BOOLEAN.asQName());
        source.setVariable(informationItem);

        final ContextEntry target = source.copy();

        assertNotNull(target);
        assertNull(target.getExpression());
        assertNotNull(target.getVariable());
        assertNotEquals(ITEM_ID, target.getVariable().getId().getValue());
        assertEquals(DESCRIPTION, target.getVariable().getDescription().getValue());
        assertEquals(INFORMATION_ITEM_NAME, target.getVariable().getName().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getVariable().getTypeRef());
    }

    @Test
    public void testExactCopy() {
        final ContextEntry source = new ContextEntry();
        final InformationItem informationItem = new InformationItem(new Id(ITEM_ID), new Description(DESCRIPTION), new Name(INFORMATION_ITEM_NAME), BuiltInType.BOOLEAN.asQName());
        source.setVariable(informationItem);

        final ContextEntry target = source.exactCopy();

        assertNotNull(target);
        assertNull(target.getExpression());
        assertNotNull(target.getVariable());
        assertEquals(ITEM_ID, target.getVariable().getId().getValue());
        assertEquals(DESCRIPTION, target.getVariable().getDescription().getValue());
        assertEquals(INFORMATION_ITEM_NAME, target.getVariable().getName().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getVariable().getTypeRef());
    }

    @Test
    public void testFindDomainObject_WhenVariableMatches() {

        final ContextEntry contextEntry = new ContextEntry();

        final InformationItem variable = new InformationItem(new Id(UUID),
                                                             null,
                                                             null,
                                                             null);

        contextEntry.setVariable(variable);

        final Optional<DomainObject> result = contextEntry.findDomainObject(UUID);

        assertTrue(result.isPresent());
        assertEquals(variable, result.get());
    }

    @Test
    public void testFindDomainObject_WhenVariableDoesNotMatches() {

        final ContextEntry contextEntry = new ContextEntry();
        final Expression expression = mock(Expression.class);
        final DomainObject expressionDomainObject = mock(DomainObject.class);

        final InformationItem variable = new InformationItem(new Id(ANOTHER_UUID),
                                                             null,
                                                             null,
                                                             null);

        contextEntry.setVariable(variable);
        contextEntry.setExpression(expression);

        when(expression.findDomainObject(UUID)).thenReturn(Optional.of(expressionDomainObject));

        final Optional<DomainObject> result = contextEntry.findDomainObject(UUID);

        assertTrue(result.isPresent());
        assertEquals(result.get(), expressionDomainObject);
        verify(expression).findDomainObject(UUID);
    }

    @Test
    public void testFindDomainObject_WhenVariableDoesNotMatchesAndExpressionIsNull() {

        final ContextEntry contextEntry = new ContextEntry();

        final InformationItem variable = new InformationItem(new Id(ANOTHER_UUID),
                                                             null,
                                                             null,
                                                             null);

        contextEntry.setVariable(variable);

        final Optional<DomainObject> result = contextEntry.findDomainObject(UUID);

        assertFalse(result.isPresent());
    }
}
