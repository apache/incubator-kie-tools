/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.ext.wires.core.grids.client.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoggingTest {

    private static final String MESSAGE = "message";

    private static final long PREVIOUS_TIME_MILLIS = 100;

    @Mock
    private Logger logger;

    @Test
    public void testLogEnabled() {
        when(logger.isLoggable(Level.FINEST)).thenReturn(true);

        assertThat(Logging.log(logger, MESSAGE)).isGreaterThan(0);

        verify(logger).log(eq(Level.FINEST), eq(MESSAGE));
    }

    @Test
    public void testLogWithPreviousTimeTimeMillisEnabled() {
        when(logger.isLoggable(Level.FINEST)).thenReturn(true);

        // There's no guarantee that the method takes >0 ms to complete
        // so compare the result is at least the same as the previous time
        assertThat(Logging.log(logger, MESSAGE, PREVIOUS_TIME_MILLIS)).isGreaterThanOrEqualTo(PREVIOUS_TIME_MILLIS);

        verify(logger).log(eq(Level.FINEST), contains(MESSAGE));
    }

    @Test
    public void testLogDisabled() {
        when(logger.isLoggable(Level.FINEST)).thenReturn(false);

        assertThat(Logging.log(logger, MESSAGE)).isGreaterThan(0);

        verify(logger, never()).log(any(Level.class), anyString());
    }

    @Test
    public void testLogWithPreviousTimeTimeMillisDisabled() {
        when(logger.isLoggable(Level.FINEST)).thenReturn(false);

        // There's no guarantee that the method takes >0 ms to complete
        // so compare the result is at least the same as the previous time
        assertThat(Logging.log(logger, MESSAGE, PREVIOUS_TIME_MILLIS)).isGreaterThanOrEqualTo(PREVIOUS_TIME_MILLIS);

        verify(logger, never()).log(any(Level.class), anyString());
    }
}
