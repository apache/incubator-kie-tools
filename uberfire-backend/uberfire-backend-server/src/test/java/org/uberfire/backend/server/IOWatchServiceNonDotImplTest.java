/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.backend.server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class IOWatchServiceNonDotImplTest {

    @Test
    public void shouldFilterTest() {
        Path path = mock(Path.class);

        IOWatchServiceNonDotImpl io = new IOWatchServiceNonDotImpl();

        assertFalse(io.shouldFilter(null));

        when(path.getFileName()).thenReturn(null);
        assertFalse(io.shouldFilter(path));

        Path filename = mock(Path.class);
        when(filename.toString()).thenReturn("dont_start_with_.");
        when(path.getFileName()).thenReturn(filename);
        assertFalse(io.shouldFilter(path));

        when(filename.toString()).thenReturn(".dora");
        assertTrue(io.shouldFilter(path));
    }
}