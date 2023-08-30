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

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.dmn.api.property.dmn.Name;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;
import static org.mockito.Mockito.mock;

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
    public void testEqualsWhenUuidIsEquals() {

        import1.setUuid("uuid");
        import2.setUuid("uuid");

        assertEquals(import1, import2);
    }

    @Test
    public void testEqualsWhenUuidIsDifferent() {

        import1.setUuid("uuid1");
        import2.setUuid("uuid2");

        assertEquals(import1, import2);
    }

    @Test
    public void testHashCodeWithTheSameUuid() {

        import1.setUuid("uuid");
        import2.setUuid("uuid");

        // Imports with only UUID not being equals should be considered equals
        assertEquals(import1.hashCode(), import2.hashCode());
    }

    @Test
    public void testHashCodeWithADifferentUuidIsEquals() {

        final String theType = "the type";
        final Name theName = mock(Name.class);
        final String theNamespace = "namespace";
        final LocationURI locationURI = mock(LocationURI.class);
        import1.setUuid("uuid1");
        import1.setImportType(theType);
        import1.setName(theName);
        import1.setNamespace(theNamespace);
        import1.setLocationURI(locationURI);

        import2.setUuid("uuid2");
        import2.setImportType(theType);
        import2.setName(theName);
        import2.setNamespace(theNamespace);
        import2.setLocationURI(locationURI);

        // Imports with only UUID not being equals should be considered equals
        assertEquals(import1.hashCode(), import2.hashCode());
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

    @Test
    public void testUuidIsSet() {

        final String uuid = import1.getUuid();

        assertFalse(isEmpty(uuid));
    }
}
