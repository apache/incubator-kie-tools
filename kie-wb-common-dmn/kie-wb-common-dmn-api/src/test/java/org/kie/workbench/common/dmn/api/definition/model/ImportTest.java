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

package org.kie.workbench.common.dmn.api.definition.model;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ImportTest {

    private Import import1;

    private Import import2;

    @Before
    public void setup() {
        import1 = new Import();
        import2 = new Import();
        import1.setId(new Id("123"));
        import2.setId(new Id("123"));
    }

    @Test
    public void testNotEqualsId() {

        import1.setId(new Id("123"));
        import2.setId(new Id("456"));

        assertNotEquals(import1, import2);
    }

    @Test
    public void testEqualsId() {
        assertEquals(import1, import2);
    }

    @Test
    public void testNotEqualsDescription() {

        import1.setDescription(new Description("desc1"));
        import2.setDescription(new Description("desc2"));

        assertNotEquals(import1, import2);
    }

    @Test
    public void testEqualsDescription() {

        import1.setDescription(new Description("desc"));
        import2.setDescription(new Description("desc"));

        assertEquals(import1, import2);
    }

    @Test
    public void testEqualsName() {

        import1.setName(new Name("name"));
        import2.setName(new Name("name"));

        assertEquals(import1, import2);
    }

    @Test
    public void testNotEqualsName() {

        import1.setName(new Name("name1"));
        import2.setName(new Name("name2"));

        assertNotEquals(import1, import2);
    }

    @Test
    public void testHashCodeWithTheSameId() {

        import1.setId(new Id("123"));
        import2.setId(new Id("123"));

        assertEquals(import1.hashCode(), import2.hashCode());
    }

    @Test
    public void testHashCodeWithTheADifferentId() {

        import1.setId(new Id("123"));
        import2.setId(new Id("456"));

        assertNotEquals(import1.hashCode(), import2.hashCode());
    }

    @Test
    public void testHashCodeWithTheSameName() {

        import1.setName(new Name("123"));
        import2.setName(new Name("123"));

        assertEquals(import1.hashCode(), import2.hashCode());
    }

    @Test
    public void testHashCodeWithTheADifferentName() {

        import1.setName(new Name("123"));
        import2.setName(new Name("456"));

        assertNotEquals(import1.hashCode(), import2.hashCode());
    }

    @Test
    public void testHashCodeWithTheSameDescription() {

        import1.setDescription(new Description("desc"));
        import2.setDescription(new Description("desc"));

        assertEquals(import1.hashCode(), import2.hashCode());
    }

    @Test
    public void testHashCodeWithTheADifferentDescription() {

        import1.setDescription(new Description("desc1"));
        import2.setDescription(new Description("desc2"));

        assertNotEquals(import1.hashCode(), import2.hashCode());
    }
}
