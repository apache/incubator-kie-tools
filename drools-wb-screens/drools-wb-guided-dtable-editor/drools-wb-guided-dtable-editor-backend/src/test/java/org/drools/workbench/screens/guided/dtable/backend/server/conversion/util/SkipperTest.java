/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.backend.server.conversion.util;

import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SkipperTest {

    @Test
    public void testDialect() {
        AttributeCol52 dialect = new AttributeCol52();
        dialect.setAttribute("dialect");
        assertTrue(Skipper.isDialect(dialect));
    }

    @Test
    public void testSalience() {
        final AttributeCol52 salience = new AttributeCol52();
        salience.setAttribute("salience");
        assertFalse(Skipper.isDialect(salience));
    }

    @Test
    public void testNotAttributeCol52() {
        assertFalse(Skipper.isDialect(new ActionCol52()));
    }
}