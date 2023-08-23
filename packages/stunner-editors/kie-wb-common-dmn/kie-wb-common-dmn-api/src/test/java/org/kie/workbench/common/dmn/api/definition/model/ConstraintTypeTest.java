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

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.api.definition.model.ConstraintType.ENUMERATION;
import static org.kie.workbench.common.dmn.api.definition.model.ConstraintType.EXPRESSION;
import static org.kie.workbench.common.dmn.api.definition.model.ConstraintType.NONE;
import static org.kie.workbench.common.dmn.api.definition.model.ConstraintType.RANGE;

public class ConstraintTypeTest {

    @Test
    public void testEnumerationString() {
        testFromString(ENUMERATION, "enumeration");
        testFromString(ENUMERATION, "Enumeration");
        testFromString(ENUMERATION, "ENUMERATION");
    }

    @Test
    public void testExpressionString() {
        testFromString(EXPRESSION, "expression");
        testFromString(EXPRESSION, "Expression");
        testFromString(EXPRESSION, "EXPRESSION");
    }

    @Test
    public void testRangeString() {
        testFromString(RANGE, "range");
        testFromString(RANGE, "Range");
        testFromString(RANGE, "RANGE");
    }

    @Test
    public void testNullString() {
        testFromString(NONE, "none");
        testFromString(NONE, "None");
        testFromString(NONE, "NONE");
    }

    @Test
    public void testUnknownString() {
        testFromString(NONE, "unknownvalue");
    }

    private void testFromString(ConstraintType expected, String value) {
        ConstraintType parsed = ConstraintType.fromString(value);
        assertEquals(expected, parsed);
    }
}
