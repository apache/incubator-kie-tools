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

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class OutputClauseLiteralExpressionTest {

    private static final String TEXT = "TEXT";
    private static final String CLAUSE_ID = "CLAUSE-ID";
    private static final String DESCRIPTION = "DESCRIPTION";
    private OutputClauseLiteralExpression outputClauseLiteralExpression;

    @Before
    public void setup() {
        this.outputClauseLiteralExpression = new OutputClauseLiteralExpression();
    }

    @Test
    public void testGetHasTypeRefs() {
        final List<HasTypeRef> actualHasTypeRefs = outputClauseLiteralExpression.getHasTypeRefs();
        final List<HasTypeRef> expectedHasTypeRefs = singletonList(outputClauseLiteralExpression);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }

    @Test
    public void testCopy() {
        final OutputClauseLiteralExpression source = new OutputClauseLiteralExpression(
                new Id(CLAUSE_ID),
                new Description(DESCRIPTION),
                BuiltInType.BOOLEAN.asQName(),
                new Text(TEXT),
                null
        );

        final OutputClauseLiteralExpression target = source.copy();

        assertNotNull(target);
        assertNotEquals(CLAUSE_ID, target.getId().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getTypeRef());
        assertEquals(TEXT, target.getText().getValue());
        assertNull(target.getImportedValues());
    }

    @Test
    public void testExactCopy() {
        final OutputClauseLiteralExpression source = new OutputClauseLiteralExpression(
                new Id(CLAUSE_ID),
                new Description(DESCRIPTION),
                BuiltInType.BOOLEAN.asQName(),
                new Text(TEXT),
                null
        );

        final OutputClauseLiteralExpression target = source.exactCopy();

        assertNotNull(target);
        assertEquals(CLAUSE_ID, target.getId().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getTypeRef());
        assertEquals(TEXT, target.getText().getValue());
        assertNull(target.getImportedValues());
    }
}
