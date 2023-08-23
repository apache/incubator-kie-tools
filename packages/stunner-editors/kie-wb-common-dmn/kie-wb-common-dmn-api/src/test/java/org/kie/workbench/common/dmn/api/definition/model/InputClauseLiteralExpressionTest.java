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
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class InputClauseLiteralExpressionTest {

    private static final String CLAUSE_ID = "CLAUSE_ID";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String TEXT = "TEXT";
    private InputClauseLiteralExpression inputClauseLiteralExpression;

    @Before
    public void setup() {
        this.inputClauseLiteralExpression = new InputClauseLiteralExpression();
    }

    @Test
    public void testGetHasTypeRefs() {
        final List<HasTypeRef> actualHasTypeRefs = inputClauseLiteralExpression.getHasTypeRefs();
        final List<HasTypeRef> expectedHasTypeRefs = singletonList(inputClauseLiteralExpression);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }

    @Test
    public void testCopy() {
        final InputClauseLiteralExpression source = new InputClauseLiteralExpression(
                new Id(CLAUSE_ID),
                new Description(DESCRIPTION),
                BuiltInType.BOOLEAN.asQName(),
                new Text(TEXT),
                new ImportedValues()
        );

        final InputClauseLiteralExpression target = source.copy();

        assertNotNull(target);
        assertNotEquals(CLAUSE_ID, target.getId().getValue());
        assertEquals(TEXT, target.getText().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getTypeRef());
    }

    @Test
    public void testExactCopy() {
        final InputClauseLiteralExpression source = new InputClauseLiteralExpression(
                new Id(CLAUSE_ID),
                new Description(DESCRIPTION),
                BuiltInType.BOOLEAN.asQName(),
                new Text(TEXT),
                new ImportedValues()
        );

        final InputClauseLiteralExpression target = source.exactCopy();

        assertNotNull(target);
        assertEquals(CLAUSE_ID, target.getId().getValue());
        assertEquals(TEXT, target.getText().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getTypeRef());
    }

    @Test
    public void testFindDomainObject_WhenInputClauseLiteralExpressionMatches() {

        final String uuid = "uuid";
        final InputClauseLiteralExpression inputClause = new InputClauseLiteralExpression(new Id(uuid),
                                                                                          null,
                                                                                          null,
                                                                                          null,
                                                                                          null);

        final Optional<DomainObject> actual = inputClause.findDomainObject(uuid);

        assertTrue(actual.isPresent());
        assertEquals(inputClause, actual.get());
    }

    @Test
    public void testFindDomainObject_WhenImportedValueMatches() {

        final ImportedValues importedValues = new ImportedValues();
        // The UUID for ImporterValues is read-only, so we can't set it for test
        final String uuid = importedValues.getDomainObjectUUID();
        final InputClauseLiteralExpression inputClause = new InputClauseLiteralExpression(new Id(uuid),
                                                                                          null,
                                                                                          null,
                                                                                          null,
                                                                                          importedValues);

        final Optional<DomainObject> actual = inputClause.findDomainObject(uuid);

        assertTrue(actual.isPresent());
        assertEquals(inputClause, actual.get());
    }

    @Test
    public void testFindDomainObject_WhenNothingMatches() {

        final InputClauseLiteralExpression inputClause = new InputClauseLiteralExpression();

        final Optional<DomainObject> actual = inputClause.findDomainObject("some id");

        assertFalse(actual.isPresent());
    }
}
