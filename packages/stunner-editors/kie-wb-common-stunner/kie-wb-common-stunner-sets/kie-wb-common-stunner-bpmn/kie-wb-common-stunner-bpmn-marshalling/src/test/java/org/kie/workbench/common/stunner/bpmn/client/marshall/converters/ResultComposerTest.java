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

package org.kie.workbench.common.stunner.bpmn.client.marshall.converters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingMessage;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ResultComposerTest {

    Result result1;
    Result result2;
    Result result3;
    MarshallingMessage message1;
    MarshallingMessage message2;
    MarshallingMessage message3;

    @Mock
    private Object value;

    @Before
    public void setUp() {
        message1 = MarshallingMessage.builder().build();
        message2 = MarshallingMessage.builder().build();
        message3 = MarshallingMessage.builder().build();
        result1 = Result.success(value, message1);
        result2 = Result.failure("", value, message2);
        result3 = Result.ignored("", value, message3);
    }

    @Test
    public void compose() {
        final Result result = ResultComposer.compose(value, result1, result2, result3);
        assertResult(result);
    }

    private void assertResult(Result result) {
        assertEquals(3, result.messages().size());
        assertTrue(result.messages().stream().anyMatch(message1::equals));
        assertTrue(result.messages().stream().anyMatch(message2::equals));
        assertTrue(result.messages().stream().anyMatch(message3::equals));
    }

    @Test
    public void composeList() {
        final Result result = ResultComposer.compose(value, result1, result2, result3);
        assertResult(result);
    }
}