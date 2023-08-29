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

import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.ExpressionLanguage;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class LiteralExpressionTest {

    private static final String LITERAL_ID = "LITERAL-ID";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String TEXT = "TEXT";
    private static final String EXPRESSION_LANGUAGE = "EXPRESSION-LANGUAGE";
    private static final String UUID = "uuid";
    private LiteralExpression literalExpression;

    @Before
    public void setup() {
        this.literalExpression = new LiteralExpression();
    }

    @Test
    public void testGetHasTypeRefs() {
        final java.util.List<HasTypeRef> actualHasTypeRefs = literalExpression.getHasTypeRefs();
        final java.util.List<HasTypeRef> expectedHasTypeRefs = singletonList(literalExpression);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }

    @Test
    public void testComponentWidths() {
        assertEquals(literalExpression.getRequiredComponentWidthCount(),
                     literalExpression.getComponentWidths().size());
        literalExpression.getComponentWidths().forEach(Assert::assertNull);
    }

    @Test
    public void testCopy() {
        final LiteralExpression source = new LiteralExpression(
                new Id(LITERAL_ID),
                new Description(DESCRIPTION),
                BuiltInType.BOOLEAN.asQName(),
                new Text(TEXT),
                null,
                new ExpressionLanguage(EXPRESSION_LANGUAGE)
        );

        final LiteralExpression target = source.copy();

        assertNotNull(target);
        assertNotEquals(LITERAL_ID, target.getId().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getTypeRef());
        assertEquals(TEXT, target.getText().getValue());
        assertNull(target.getImportedValues());
        assertEquals(EXPRESSION_LANGUAGE, target.getExpressionLanguage().getValue());
    }

    @Test
    public void testExactCopy() {
        final LiteralExpression source = new LiteralExpression(
                new Id(LITERAL_ID),
                new Description(DESCRIPTION),
                BuiltInType.BOOLEAN.asQName(),
                new Text(TEXT),
                null,
                new ExpressionLanguage(EXPRESSION_LANGUAGE)
        );

        final LiteralExpression target = source.exactCopy();

        assertNotNull(target);
        assertEquals(LITERAL_ID, target.getId().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getTypeRef());
        assertEquals(TEXT, target.getText().getValue());
        assertNull(target.getImportedValues());
        assertEquals(EXPRESSION_LANGUAGE, target.getExpressionLanguage().getValue());
    }

    @Test
    public void testFindDomainObject() {

        final LiteralExpression literalExpression = new LiteralExpression(new Id(UUID),
                                                                          null,
                                                                          null,
                                                                          null,
                                                                          null,
                                                                          null);

        final Optional<DomainObject> foundDomainObject = literalExpression.findDomainObject(UUID);

        assertTrue(foundDomainObject.isPresent());
        assertEquals(literalExpression, foundDomainObject.get());
    }

    @Test
    public void testFindDomainObject_WhenNothingHasBeenFound() {

        final LiteralExpression literalExpression = new LiteralExpression();

        final Optional<DomainObject> foundDomainObject = literalExpression.findDomainObject(UUID);

        assertFalse(foundDomainObject.isPresent());
    }
}
