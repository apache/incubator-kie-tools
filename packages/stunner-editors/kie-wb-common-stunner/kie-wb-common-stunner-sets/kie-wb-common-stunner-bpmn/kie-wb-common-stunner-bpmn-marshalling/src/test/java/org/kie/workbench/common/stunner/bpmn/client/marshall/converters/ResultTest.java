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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingMessage;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ResultTest {

    @Mock
    private Object value;

    private final MarshallingMessage message = MarshallingMessage.builder().message("message").build();

    @Test
    public void success() {
        final Result<Object> result = Result.success(value, message);
        assertResult(result, message, true, false, false, value);
    }

    @Test
    public void failure() {
        final Result<Object> result = Result.failure("Reason", value, message);
        assertResult(result, message, false, true, false, value);
    }

    @Test
    public void failureWithNullValue() {
        final Result<Object> result = Result.failure("Reason", message);
        assertResult(result, message, false, true, false, null);
    }

    @Test
    public void ignored() {
        final Result<Object> result = Result.ignored("Reason", value, message);
        assertResult(result, message, false, false, true, value);
    }

    @Test
    public void ignoredWithNullValue() {
        final Result<Object> result = Result.ignored("Reason", message);
        assertResult(result, message, false, false, true, null);
    }

    private void assertResult(Result result, MarshallingMessage message,
                              boolean success, boolean failure, boolean ignored, Object value) {
        assertEquals(value, result.value());
        assertEquals(message, result.messages().get(0));
        assertEquals(result.messages().size(), 1);
        assertEquals(success, result.isSuccess());
        assertEquals(failure, result.isFailure());
        assertEquals(ignored, result.isIgnored());
    }
}