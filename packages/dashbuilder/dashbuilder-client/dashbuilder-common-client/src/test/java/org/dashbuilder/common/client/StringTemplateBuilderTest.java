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

package org.dashbuilder.common.client;

import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StringTemplateBuilderTest {

    @Test
    public void testNoReplacement() {
        StringTemplateBuilder templateBuilder = new StringTemplateBuilder();
        templateBuilder.setTemplate("Hello ${user} from ${place}.");
        Set<String> keys = templateBuilder.keys();
        String code = templateBuilder.build();

        assertEquals(keys.size(), 2);
        assertTrue(keys.contains("user"));
        assertTrue(keys.contains("place"));
        assertEquals(code, "Hello ${user} from ${place}.");
    }

    @Test
    public void testReplacement() {
        StringTemplateBuilder templateBuilder = new StringTemplateBuilder();
        templateBuilder.setTemplate("Hello ${user} from ${place}.");
        templateBuilder.replace("user", "Mark");
        templateBuilder.replace("place", "London");
        String code = templateBuilder.build();
        assertEquals(code, "Hello Mark from London.");
    }

    @Test
    public void testCustomKeys() {
        StringTemplateBuilder templateBuilder = new StringTemplateBuilder();
        templateBuilder.setKeyPrefix("[");
        templateBuilder.setKeySufix("]");
        templateBuilder.setTemplate("Hello [user] from [place].");
        templateBuilder.replace("user", "Mark");
        templateBuilder.replace("place", "London");
        String code = templateBuilder.build();
        assertEquals(code, "Hello Mark from London.");
    }
}