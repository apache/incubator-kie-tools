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
import org.kie.workbench.common.dmn.api.property.dmn.Text;
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
public class InputClauseTest {

    private static final String UNARY_ID = "UNARY-ID";
    private static final String INPUT_ID = "INPUT-ID";
    private static final String TEXT = "TEXT";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String CLAUSE_ID = "CLAUSE-ID";
    private static final String UUID = "uuid";
    private static final String ANOTHER_UUID = "another uuid";
    private InputClause inputClause;

    @Before
    public void setup() {
        this.inputClause = spy(new InputClause());
    }

    @Test
    public void testGetHasTypeRefs() {
        final InputClauseLiteralExpression literalExpression = mock(InputClauseLiteralExpression.class);
        final HasTypeRef hasTypeRef1 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef2 = mock(HasTypeRef.class);

        doReturn(literalExpression).when(inputClause).getInputExpression();

        when(literalExpression.getHasTypeRefs()).thenReturn(asList(hasTypeRef1, hasTypeRef2));

        final List<HasTypeRef> actualHasTypeRefs = inputClause.getHasTypeRefs();
        final List<HasTypeRef> expectedHasTypeRefs = asList(hasTypeRef1, hasTypeRef2);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }

    @Test
    public void testCopy() {
        final InputClause source = new InputClause(
                new Id(INPUT_ID),
                new Description(DESCRIPTION),
                buildInputClauseLiteralExpression(),
                buildInputClauseUnaryTests()
        );

        final InputClause target = source.copy();

        assertNotNull(target);
        assertNotEquals(INPUT_ID, target.getId().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertNotNull(target.getInputExpression());
        assertNotEquals(CLAUSE_ID, target.getInputExpression().getId().getValue());
        assertEquals(TEXT, target.getInputExpression().getText().getValue());
        assertEquals(DESCRIPTION, target.getInputExpression().getDescription().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getInputExpression().getTypeRef());
        assertNotNull(target.getInputValues());
        assertNotEquals(UNARY_ID, target.getInputValues().getId().getValue());
        assertEquals(TEXT, target.getInputValues().getText().getValue());
        assertEquals(ConstraintType.ENUMERATION, target.getInputValues().getConstraintType());
    }

    @Test
    public void testExactCopy() {
        final InputClause source = new InputClause(
                new Id(INPUT_ID),
                new Description(DESCRIPTION),
                buildInputClauseLiteralExpression(),
                buildInputClauseUnaryTests()
        );

        final InputClause target = source.exactCopy();

        assertNotNull(target);
        assertEquals(INPUT_ID, target.getId().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertNotNull(target.getInputExpression());
        assertEquals(CLAUSE_ID, target.getInputExpression().getId().getValue());
        assertEquals(TEXT, target.getInputExpression().getText().getValue());
        assertEquals(DESCRIPTION, target.getInputExpression().getDescription().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getInputExpression().getTypeRef());
        assertNotNull(target.getInputValues());
        assertEquals(UNARY_ID, target.getInputValues().getId().getValue());
        assertEquals(TEXT, target.getInputValues().getText().getValue());
        assertEquals(ConstraintType.ENUMERATION, target.getInputValues().getConstraintType());
    }

    @Test
    public void testFindDomainObject_WhenInputClauseMatches() {

        final InputClause inputClause = new InputClause(new Id(UUID),
                                                        null,
                                                        null,
                                                        null);

        final Optional<DomainObject> actual = inputClause.findDomainObject(UUID);

        assertTrue(actual.isPresent());
        assertEquals(inputClause, actual.get());
    }

    @Test
    public void testFindDomainObject_WhenInputClauseDoesNotMatches() {

        final InputClauseLiteralExpression expression = mock(InputClauseLiteralExpression.class);
        final InputClause inputClause = new InputClause(new Id(ANOTHER_UUID),
                                                        null,
                                                        expression,
                                                        null);

        final DomainObject expectedDomainObject = mock(DomainObject.class);

        when(expression.findDomainObject(UUID)).thenReturn(Optional.of(expectedDomainObject));

        final Optional<DomainObject> actual = inputClause.findDomainObject(UUID);

        assertTrue(actual.isPresent());
        assertEquals(expectedDomainObject, actual.get());
        verify(expression).findDomainObject(UUID);
    }

    private InputClauseUnaryTests buildInputClauseUnaryTests() {
        return new InputClauseUnaryTests(
                new Id(UNARY_ID),
                new Text(TEXT),
                ConstraintType.ENUMERATION
        );
    }

    private InputClauseLiteralExpression buildInputClauseLiteralExpression() {
        return new InputClauseLiteralExpression(
                new Id(CLAUSE_ID),
                new Description(DESCRIPTION),
                BuiltInType.BOOLEAN.asQName(),
                new Text(TEXT),
                new ImportedValues()
        );
    }
}
