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
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.ExpressionLanguage;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.workbench.common.dmn.api.definition.model.ConstraintType.NONE;

public class UnaryTestsTest {

    private static final String UNARY_ID = "UNARY-ID";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String TEXT_VALUE = "TEXT-VALUE";
    private static final String EXPRESSION_LANGUAGE = "EXPRESSION-LANGUAGE";

    @Test
    public void testCopy() {
        final UnaryTests source = new UnaryTests(
                new Id(UNARY_ID),
                new Description(DESCRIPTION),
                new Text(TEXT_VALUE),
                new ExpressionLanguage(EXPRESSION_LANGUAGE),
                NONE
        );

        final UnaryTests target = source.copy();

        assertNotNull(target);
        assertNotEquals(UNARY_ID, target.getId().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertEquals(TEXT_VALUE, target.getText().getValue());
        assertEquals(EXPRESSION_LANGUAGE, target.getExpressionLanguage().getValue());
        assertEquals(NONE, target.getConstraintType());
    }

    @Test
    public void testExactCopy() {
        final UnaryTests source = new UnaryTests(
                new Id(UNARY_ID),
                new Description(DESCRIPTION),
                new Text(TEXT_VALUE),
                new ExpressionLanguage(EXPRESSION_LANGUAGE),
                NONE
        );

        final UnaryTests target = source.exactCopy();

        assertNotNull(target);
        assertEquals(UNARY_ID, target.getId().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertEquals(TEXT_VALUE, target.getText().getValue());
        assertEquals(EXPRESSION_LANGUAGE, target.getExpressionLanguage().getValue());
        assertEquals(NONE, target.getConstraintType());
    }
}
