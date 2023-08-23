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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.workbench.common.dmn.api.definition.model.ConstraintType.NONE;

@RunWith(MockitoJUnitRunner.class)
public class InputClauseUnaryTestsTest {

    private static final String TEXT = "TEXT";
    private static final String UNARY_ID = "UNARY_ID";

    @Test
    public void testDefaultConstraintTypeProperty() {
        final InputClauseUnaryTests inputClause = new InputClauseUnaryTests();
        assertEquals(NONE.value(), inputClause.getConstraintTypeProperty().getValue());
    }

    @Test
    public void ensureThatConstraintTypePropertyIsSetToExpression() {
        ensureThatConstraintTypePropertyIsSet(ConstraintType.EXPRESSION);
    }

    @Test
    public void ensureThatConstraintTypePropertyIsSetToRange() {
        ensureThatConstraintTypePropertyIsSet(ConstraintType.RANGE);
    }

    @Test
    public void ensureThatConstraintTypePropertyIsSetToEnumeration() {
        ensureThatConstraintTypePropertyIsSet(ConstraintType.ENUMERATION);
    }

    private void ensureThatConstraintTypePropertyIsSet(final ConstraintType constraintType) {
        final InputClauseUnaryTests inputClause = new InputClauseUnaryTests(new Id("id"),
                                                                            new Text(""),
                                                                            constraintType);

        assertEquals(constraintType.value(), inputClause.getConstraintTypeProperty().getValue());
    }

    @Test
    public void ensureThatConstraintTypeIsSetToExpression() {
        ensureThatConstraintTypeIsSet(ConstraintType.EXPRESSION);
    }

    @Test
    public void ensureThatConstraintTypeIsSetToRange() {
        ensureThatConstraintTypeIsSet(ConstraintType.RANGE);
    }

    @Test
    public void ensureThatConstraintTypeIsSetToEnumeration() {
        ensureThatConstraintTypeIsSet(ConstraintType.ENUMERATION);
    }

    @Test
    public void ensureThatConstraintTypeIsSetToNone() {
        ensureThatConstraintTypeIsSet(NONE);
    }

    @Test
    public void ensureThatConstraintTypeIsSetToNull() {
        ensureThatConstraintTypeIsSet(NONE);
    }

    private void ensureThatConstraintTypeIsSet(final ConstraintType constraintType) {
        final InputClauseUnaryTests inputClause = new InputClauseUnaryTests(new Id("id"),
                                                                            new Text(""),
                                                                            constraintType);

        assertEquals(constraintType, inputClause.getConstraintType());
    }

    @Test
    public void testCopy() {
        final InputClauseUnaryTests source = new InputClauseUnaryTests(
                new Id(UNARY_ID),
                new Text(TEXT),
                ConstraintType.ENUMERATION
        );

        final InputClauseUnaryTests target = source.copy();

        assertNotNull(target);
        assertNotEquals(UNARY_ID, target.getId().getValue());
        assertEquals(TEXT, target.getText().getValue());
        assertEquals(ConstraintType.ENUMERATION, target.getConstraintType());
    }

    @Test
    public void testExactCopy() {
        final InputClauseUnaryTests source = new InputClauseUnaryTests(
                new Id(UNARY_ID),
                new Text(TEXT),
                ConstraintType.ENUMERATION
        );

        final InputClauseUnaryTests target = source.exactCopy();

        assertNotNull(target);
        assertEquals(UNARY_ID, target.getId().getValue());
        assertEquals(TEXT, target.getText().getValue());
        assertEquals(ConstraintType.ENUMERATION, target.getConstraintType());
    }
}
