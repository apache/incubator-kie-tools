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
package org.kie.workbench.common.dmn.api.definition;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class HasExpressionTest {

    @Mock
    private DMNModelInstrumentedBase parent;

    @Test
    public void testNOP() {
        final HasExpression hasExpression = HasExpression.NOP;

        assertNull(hasExpression.getExpression());
        assertNull(hasExpression.asDMNModelInstrumentedBase());

        final Context context = new Context();
        hasExpression.setExpression(context);

        assertNull(hasExpression.getExpression());
        assertNull(hasExpression.asDMNModelInstrumentedBase());
    }

    @Test
    public void testWrapNull() {
        final HasExpression hasExpression = HasExpression.wrap(parent, null);

        assertNull(hasExpression.getExpression());
        assertEquals(parent, hasExpression.asDMNModelInstrumentedBase());

        final Context context = new Context();
        hasExpression.setExpression(context);

        assertNotNull(hasExpression.getExpression());
        assertEquals(context, hasExpression.getExpression());
        assertEquals(parent, hasExpression.asDMNModelInstrumentedBase());
    }

    @Test
    public void testWrapNonNull() {
        final LiteralExpression le = new LiteralExpression();
        final HasExpression hasExpression = HasExpression.wrap(parent, le);

        assertNotNull(hasExpression.getExpression());
        assertEquals(le, hasExpression.getExpression());
        assertEquals(parent, hasExpression.asDMNModelInstrumentedBase());

        final Context context = new Context();
        hasExpression.setExpression(context);

        assertNotNull(hasExpression.getExpression());
        assertEquals(context, hasExpression.getExpression());
        assertEquals(parent, hasExpression.asDMNModelInstrumentedBase());
    }
}
