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

package org.dashbuilder.client.parser;

import java.util.Collections;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;

import static org.junit.Assert.assertEquals;

@RunWith(GwtMockitoTestRunner.class)
public class PropertyReplacementServiceTest {

    private String TEXT_WITH_PROPERTY = "Property: ${prop}";

    @InjectMocks
    PropertyReplacementService replaceService;
    
    @Test
    public void testPropertiesReplacement() {
        var result = replaceService.replace(TEXT_WITH_PROPERTY, Collections.singletonMap("prop", "value"));
        assertEquals("Property: value", result);
    }

    @Test
    public void testPropertiesReplacementWithoutProperty() {
        var result = replaceService.replace(TEXT_WITH_PROPERTY, Collections.emptyMap());
        assertEquals(TEXT_WITH_PROPERTY, result);
    }


}