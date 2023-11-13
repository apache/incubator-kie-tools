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

package org.dashbuilder.displayer.external;

import java.util.Collections;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.core.JsMap;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.dashbuilder.displayer.external.ExternalComponentMessageHelper.COMPONENT_RUNTIME_ID_PROP;
import static org.dashbuilder.displayer.external.ExternalComponentMessageHelper.FILTER_PROP;
import static org.dashbuilder.displayer.external.ExternalComponentMessageType.DATASET;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ExternalComponentMessageHelperTest {

    @Mock
    JsMap<String, Object> properties;

    @InjectMocks
    ExternalComponentMessage message;

    static private ExternalComponentMessageHelper helper;

    @BeforeClass
    public static void setup() {
        helper = new ExternalComponentMessageHelper();
    }

    @Test
    public void testWithId() {
        String id = "abc";
        helper.withId(message, id);
        verify(properties).set(eq(COMPONENT_RUNTIME_ID_PROP), eq(id));
    }

    @Test
    public void testFilterRequest() {
        Optional<ExternalFilterRequest> filterRequestOp = helper.filterRequest(message);
        assertFalse(filterRequestOp.isPresent());

        ExternalFilterRequest request = mock(ExternalFilterRequest.class);
        when(properties.get(eq(FILTER_PROP))).thenReturn(request);
        
        filterRequestOp = helper.filterRequest(message);
        
        assertEquals(request, filterRequestOp.get());
    }
    
    @Test
    public void testDataSetMessage() {
        ExternalDataSet ds = mock(ExternalDataSet.class);
        ExternalComponentMessage newDataSetMessage = helper.newDataSetMessage(ds, Collections.emptyMap());
        assertEquals(DATASET.name(), newDataSetMessage.type);
    }
    
    @Test
    public void testNewInitMessage() {
        ExternalComponentMessage initMessage = helper.newInitMessage(Collections.emptyMap());
        assertEquals(ExternalComponentMessageType.INIT.name(), initMessage.type);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNullMessageType() {
        ExternalComponentMessage message = mock(ExternalComponentMessage.class);
        when(message.getType()).thenReturn(null);
        helper.messageType(message);
    }

}