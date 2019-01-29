/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.api.definition.v1_1;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConstraintTypeTest {

    @Test
    public void testEnumerationString() {
        testFromString(ConstraintType.ENUMERATION,"enumeration");
        testFromString(ConstraintType.ENUMERATION,"Enumeration");
        testFromString(ConstraintType.ENUMERATION,"ENUMERATION");
    }

    @Test
    public void testExpressionString() {
        testFromString(ConstraintType.EXPRESSION,"expression");
        testFromString(ConstraintType.EXPRESSION,"Expression");
        testFromString(ConstraintType.EXPRESSION,"EXPRESSION");
    }

    @Test
    public void testRangeString() {
        testFromString(ConstraintType.RANGE,"range");
        testFromString(ConstraintType.RANGE,"Range");
        testFromString(ConstraintType.RANGE,"RANGE");
    }

    @Test
    public void testUnknownString() {
        testFromString(null,"unknownvalue");
    }

    private void testFromString(ConstraintType expected, String value) {
        ConstraintType parsed = ConstraintType.fromString(value);
        assertEquals(expected, parsed);
    }
}