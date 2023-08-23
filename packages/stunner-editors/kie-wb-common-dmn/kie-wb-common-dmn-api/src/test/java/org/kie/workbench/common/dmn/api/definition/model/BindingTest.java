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
import org.kie.workbench.common.dmn.api.definition.HasVariable;
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
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BindingTest {

    private static final String ITEM_ID = "ITEM-ID";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String INFORMATION_ITEM_NAME = "INFORMATION-ITEM-NAME";
    private static final String UUID = "uuid";
    private static final String ANOTHER_UUID = "another uuid";

    private Binding binding;

    @Before
    public void setup() {
        this.binding = spy(new Binding());
    }

    @Test
    public void testImplementsHasVariable() {
        assertTrue(binding instanceof HasVariable);
    }

    @Test
    public void testHasVariableProxyGetter() {
        assertEquals(binding.getParameter(),
                     binding.getVariable());
    }

    @Test
    public void testHasVariableProxySetter() {
        final InformationItem variable = new InformationItem();
        binding.setVariable(variable);

        assertEquals(variable,
                     binding.getParameter());
    }

    @Test
    public void testParameterSetter() {
        final InformationItem variable = new InformationItem();
        binding.setParameter(variable);

        assertEquals(variable,
                     binding.getVariable());
    }

    @Test
    public void testGetHasTypeRefs() {
        final Expression expression = mock(Expression.class);
        final InformationItem parameter = mock(InformationItem.class);
        final HasTypeRef hasTypeRef1 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef2 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef3 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef4 = mock(HasTypeRef.class);

        doReturn(expression).when(binding).getExpression();
        doReturn(parameter).when(binding).getParameter();

        when(expression.getHasTypeRefs()).thenReturn(asList(hasTypeRef1, hasTypeRef2));
        when(parameter.getHasTypeRefs()).thenReturn(asList(hasTypeRef3, hasTypeRef4));

        final List<HasTypeRef> actualHasTypeRefs = binding.getHasTypeRefs();
        final List<HasTypeRef> expectedHasTypeRefs = asList(hasTypeRef1, hasTypeRef2, hasTypeRef3, hasTypeRef4);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }

    @Test
    public void testCopy() {
        final Binding source = new Binding();
        final InformationItem informationItem = new InformationItem(new Id(ITEM_ID), new Description(DESCRIPTION), new Name(INFORMATION_ITEM_NAME), BuiltInType.BOOLEAN.asQName());
        source.setParameter(informationItem);

        final Binding target = source.copy();

        assertNotNull(target);
        assertNull(target.getExpression());
        assertNotNull(target.getParameter());
        assertNotEquals(ITEM_ID, target.getParameter().getId().getValue());
        assertEquals(DESCRIPTION, target.getParameter().getDescription().getValue());
        assertEquals(INFORMATION_ITEM_NAME, target.getParameter().getName().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getParameter().getTypeRef());
    }

    @Test
    public void testExactCopy() {
        final Binding source = new Binding();
        final InformationItem informationItem = new InformationItem(new Id(ITEM_ID), new Description(DESCRIPTION), new Name(INFORMATION_ITEM_NAME), BuiltInType.BOOLEAN.asQName());
        source.setParameter(informationItem);

        final Binding target = source.exactCopy();

        assertNotNull(target);
        assertNull(target.getExpression());
        assertNotNull(target.getParameter());
        assertEquals(ITEM_ID, target.getParameter().getId().getValue());
        assertEquals(DESCRIPTION, target.getParameter().getDescription().getValue());
        assertEquals(INFORMATION_ITEM_NAME, target.getParameter().getName().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getParameter().getTypeRef());
    }

    @Test
    public void testFindDomainObject_WhenParameterMatches() {

        final Binding binding = new Binding();
        final String uuid = "uuid";

        final InformationItem parameter = new InformationItem(new Id(uuid),
                                                              null,
                                                              null,
                                                              null);

        binding.setParameter(parameter);

        final Optional<DomainObject> result = binding.findDomainObject(uuid);

        assertTrue(result.isPresent());
        assertEquals(parameter, result.get());
    }

    @Test
    public void testFindDomainObject_WhenParameterDoesNotMatches() {

        final Binding binding = new Binding();

        final Expression expression = mock(Expression.class);
        final Optional<DomainObject> expressionDomainObject =  Optional.of(mock(DomainObject.class));

        final InformationItem parameter = new InformationItem(new Id(ANOTHER_UUID),
                                                              null,
                                                              null,
                                                              null);

        binding.setExpression(expression);
        binding.setParameter(parameter);

        when(expression.findDomainObject(UUID)).thenReturn(expressionDomainObject);

        final Optional<DomainObject> result = binding.findDomainObject(UUID);

        assertEquals(expressionDomainObject, result);
        verify(expression).findDomainObject(UUID);
    }

    @Test
    public void testFindDomainObject_WhenParameterDoesNotMatchesAndExpressionIsNull() {

        final Binding binding = new Binding();

        final InformationItem parameter = new InformationItem(new Id(ANOTHER_UUID),
                                                              null,
                                                              null,
                                                              null);

        binding.setParameter(parameter);

        final Optional<DomainObject> result = binding.findDomainObject(UUID);

        assertFalse(result.isPresent());
    }
}
