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

package org.kie.workbench.common.dmn.api.property.dmn;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ExpressionLanguageTest {

    private static final String VALUE = "value";

    private ExpressionLanguage expressionLanguage;

    @Test
    public void testZeroParameterConstructor() {
        this.expressionLanguage = new ExpressionLanguage();

        assertEquals("", expressionLanguage.getValue());
    }

    @Test
    public void testOneParameterConstructor() {
        this.expressionLanguage = new ExpressionLanguage(VALUE);

        assertEquals(VALUE, expressionLanguage.getValue());
    }

    @Test
    public void testSetter() {
        this.expressionLanguage = new ExpressionLanguage();
        this.expressionLanguage.setValue(VALUE);

        assertEquals(VALUE, expressionLanguage.getValue());
    }

    @Test
    public void testCopy() {
        final ExpressionLanguage source = new ExpressionLanguage(VALUE);

        final ExpressionLanguage target = source.copy();

        assertNotNull(target);
        assertEquals(VALUE, target.getValue());
    }
}
