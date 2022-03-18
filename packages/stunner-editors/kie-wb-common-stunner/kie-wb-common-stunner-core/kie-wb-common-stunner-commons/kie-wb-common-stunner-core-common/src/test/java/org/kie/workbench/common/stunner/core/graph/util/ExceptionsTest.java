/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.graph.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExceptionsTest {

    public static final String DEFAULT_RESULT = "DEFAULT_RESULT";
    public static final String RESULT = "RESULT";

    @Test
    public void swallowRuntimeException() {
        final String result = Exceptions.swallow(() -> methodThatThrowsRuntimeException(), DEFAULT_RESULT);
        assertEquals(result, DEFAULT_RESULT);
    }

    @Test
    public void swallowException() {
        final String result = Exceptions.swallow(() -> methodThatThrowsException(), DEFAULT_RESULT);
        assertEquals(result, DEFAULT_RESULT);
    }

    @Test
    public void swallowNoException() {
        final String result = Exceptions.swallow(() -> methodNoException(), DEFAULT_RESULT);
        assertEquals(result, RESULT);
    }

    private String methodNoException() {
        return RESULT;
    }

    private String methodThatThrowsRuntimeException() {
        throw new RuntimeException();
    }

    private String methodThatThrowsException() throws Exception {
        throw new Exception();
    }
}