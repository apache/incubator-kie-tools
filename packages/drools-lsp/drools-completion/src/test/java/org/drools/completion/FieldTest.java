/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.completion;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FieldTest {

    @Test
    void defaultsToFieldOrigin() {
        Field f = new Field("name", "String");
        assertEquals(Field.Origin.FIELD, f.origin);
        assertEquals("name", f.name);
        assertEquals("String", f.type);
    }

    @Test
    void carriesExplicitOrigin() {
        Field g = new Field("active", "boolean", null, Field.Origin.GETTER);
        assertEquals(Field.Origin.GETTER, g.origin);
        Field c = new Field("LOW", "Severity", "1", Field.Origin.ENUM_CONSTANT);
        assertEquals(Field.Origin.ENUM_CONSTANT, c.origin);
        assertEquals("1", c.args);
    }
}
