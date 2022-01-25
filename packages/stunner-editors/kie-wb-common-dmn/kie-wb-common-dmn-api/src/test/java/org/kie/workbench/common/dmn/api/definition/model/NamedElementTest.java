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
package org.kie.workbench.common.dmn.api.definition.model;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.NameHolder;

import static org.junit.Assert.assertEquals;

public class NamedElementTest {

    private static final String NAME = "name";

    private NamedElement namedElement;

    @Before
    public void setup() {
        this.namedElement = new NamedElement() {
            //Nothing abstract to implement!
        };
    }

    @Test
    public void testSetName() {
        namedElement.setName(new Name(NAME));

        assertEquals(NAME, namedElement.getName().getValue());
        assertEquals(NAME, namedElement.getNameHolder().getValue().getValue());
    }

    @Test
    public void testSetNameHolder() {
        namedElement.setNameHolder(new NameHolder(new Name(NAME)));

        assertEquals(NAME, namedElement.getName().getValue());
        assertEquals(NAME, namedElement.getNameHolder().getValue().getValue());
    }
}
