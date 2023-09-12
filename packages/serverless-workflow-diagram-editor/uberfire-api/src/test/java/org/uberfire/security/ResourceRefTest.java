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


package org.uberfire.security;

import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ResourceRefTest {

    @Test
    public void testDependencies() {
        ResourceRef dep1 = new ResourceRef("dep1",
                                           () -> "type");
        ResourceRef ref = new ResourceRef("id",
                                          () -> "type",
                                          Arrays.asList(dep1));
        assertNotNull(ref.getDependencies());
        assertEquals(ref.getDependencies().size(),
                     1);
        assertEquals(ref.getDependencies().get(0),
                     dep1);
    }

    @Test
    public void testEmptyDependencies() {
        ResourceRef ref = new ResourceRef("id",
                                          () -> "type");
        assertNotNull(ref.getIdentifier());
        assertNotNull(ref.getResourceType());
        assertNotNull(ref.getDependencies());
        assertEquals(ref.getIdentifier(),
                     "id");
        assertEquals(ref.getResourceType().getName(),
                     "type");
    }
}
