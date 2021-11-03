/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dashbuilder.client.error;

import org.junit.Test;

import static org.dashbuilder.client.error.DefaultRuntimeErrorCallback.PARSING_JSON_MSG;
import static org.dashbuilder.client.error.DefaultRuntimeErrorCallback.SCRIPT_ERROR_MSG;
import static org.dashbuilder.client.error.DefaultRuntimeErrorCallback.extractMessageRecursively;
import static org.dashbuilder.client.error.DefaultRuntimeErrorCallback.PARSING_JSON_SYNTAX_MSG;
import static org.dashbuilder.client.error.DefaultRuntimeErrorCallback.isServerOfflineException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DefaultRuntimeErrorCallbackTest {

    @Test
    public void testIsServerOfflineException() {
        assertTrue(isServerOfflineException(new Exception(PARSING_JSON_SYNTAX_MSG)));
        assertTrue(isServerOfflineException(new Exception(PARSING_JSON_MSG)));
        assertTrue(isServerOfflineException(new Exception(SCRIPT_ERROR_MSG)));
        
        assertFalse(isServerOfflineException(new Exception()));
        assertFalse(isServerOfflineException(new Exception("any message")));
    }

    @Test
    public void testExtractMessageRecursively() {
        Exception cause = new Exception("CAUSE");
        Exception intermediary = new Exception("INTERMEDIARY", cause);
        Exception e = new Exception("FIRST", intermediary);
        String message = DefaultRuntimeErrorCallback.extractMessageRecursively(e);
        assertEquals("FIRST Caused by: INTERMEDIARY Caused by: CAUSE", message);

        String MESSAGE = "Message";
        message = extractMessageRecursively(new Exception(MESSAGE));
        assertEquals(MESSAGE, message);

        assertEquals("", extractMessageRecursively(null));
        
    }
}