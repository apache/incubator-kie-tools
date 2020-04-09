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

package org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(MockitoJUnitRunner.class)
public class VariableDeclarationTest {

    private static final String CONSTRUCTOR_IDENTIFIER = "Variable Declaration Test";
    private static final String CONSTRUCTOR_TYPE = "Integer";
    private static final String CONSTRUCTOR_TAGS = "[input;customTag]";

    private static final String VAR_IDENTIFIER = "Variable-Declaration-Test";
    private static final String VAR_NAME = "Variable Declaration Test";
    private static final String VAR_TAGS = "[internal;input;customTag]";

    private VariableDeclaration tested;

    @Before
    public void setup() {
        tested = new VariableDeclaration(CONSTRUCTOR_IDENTIFIER, CONSTRUCTOR_TYPE, CONSTRUCTOR_TAGS);
    }

    @Test
    public void testIdentifier() {
        String identifier = tested.getIdentifier();
        assertEquals(identifier, VAR_IDENTIFIER);
    }

    @Test
    public void testName() {
        String name = tested.getTypedIdentifier().getName();
        assertEquals(name, VAR_NAME);
    }

    @Test
    public void testTags() {
        String tags = tested.getTags();
        assertEquals(CONSTRUCTOR_TAGS, tags);
    }

    @Test
    public void testEquals() {
        VariableDeclaration comparable = new VariableDeclaration(CONSTRUCTOR_IDENTIFIER, CONSTRUCTOR_TYPE, CONSTRUCTOR_TAGS);
        assertEquals(tested, comparable);
    }

    @Test
    public void testNotEquals() {
        VariableDeclaration comparable = new VariableDeclaration(CONSTRUCTOR_IDENTIFIER, CONSTRUCTOR_TYPE, "[input;customTagX]");
        assertNotEquals(tested, comparable);
    }

    @Test
    public void testToString() {
        assertEquals(tested.toString(), CONSTRUCTOR_IDENTIFIER + ":" + CONSTRUCTOR_TYPE + ":" + CONSTRUCTOR_TAGS);
        assertNotEquals(tested.toString(), CONSTRUCTOR_IDENTIFIER + ":" + CONSTRUCTOR_TYPE + ":" + "[myCustomTag]");

        VariableDeclaration comparable = new VariableDeclaration(CONSTRUCTOR_IDENTIFIER, CONSTRUCTOR_TYPE, null);
        assertEquals(comparable.toString(), CONSTRUCTOR_IDENTIFIER + ":" + CONSTRUCTOR_TYPE);

        comparable = new VariableDeclaration(CONSTRUCTOR_IDENTIFIER, CONSTRUCTOR_TYPE, "");
        assertEquals(comparable.toString(), CONSTRUCTOR_IDENTIFIER + ":" + CONSTRUCTOR_TYPE);
    }
}
