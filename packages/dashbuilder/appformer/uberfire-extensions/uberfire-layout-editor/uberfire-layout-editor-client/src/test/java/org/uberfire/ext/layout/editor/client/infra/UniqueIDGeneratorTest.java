/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.layout.editor.client.infra;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UniqueIDGeneratorTest {

    private UniqueIDGenerator idGenerator;

    @Before
    public void setup() {
        idGenerator = new UniqueIDGenerator();
    }

    @Test
    public void testCreateAccordionID() {
        String accordionID = "accordionID";
        String accordionID1 = idGenerator.createAccordionID(accordionID);
        String accordionID2 = idGenerator.createAccordionID(accordionID);
        assertNotEquals(accordionID,
                        accordionID2);
        assertNotEquals(accordionID1,
                        accordionID2);
    }

    @Test
    public void testCreateAccordionIDShouldRemoveSpaces() {
        String accordionID = "accordionID";
        String accordionID1 = idGenerator.createAccordionID(accordionID);
        assertTrue(accordionID1.contains(accordionID));

        accordionID = "accordion ID";
        accordionID1 = idGenerator.createAccordionID(accordionID);
        assertTrue(!accordionID1.contains(accordionID));
    }
}