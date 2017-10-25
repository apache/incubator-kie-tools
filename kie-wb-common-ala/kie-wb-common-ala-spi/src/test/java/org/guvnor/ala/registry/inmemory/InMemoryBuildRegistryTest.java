/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.registry.inmemory;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.ala.build.Binary;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class InMemoryBuildRegistryTest {

    private static final String BINARY_NAME = "BINARY_NAME";

    private static final int BINARIES_COUNT = 10;

    private InMemoryBuildRegistry buildRegistry;

    @Before
    public void setUp() {
        buildRegistry = new InMemoryBuildRegistry();
    }

    @Test
    public void registerBinary() {
        Binary binary = mock(Binary.class);
        when(binary.getName()).thenReturn(BINARY_NAME);
        buildRegistry.registerBinary(binary);

        List<Binary> result = buildRegistry.getAllBinaries();
        assertTrue(result.contains(binary));
    }

    @Test
    public void testGetAllBinaries() {
        List<Binary> binaries = new ArrayList<>();
        for (int i = 0; i < BINARIES_COUNT; i++) {
            Binary binary = mock(Binary.class);
            when(binary.getName()).thenReturn(BINARY_NAME + Integer.toString(i));
            binaries.add(binary);
        }

        binaries.forEach(binary -> buildRegistry.registerBinary(binary));

        List<Binary> result = buildRegistry.getAllBinaries();
        assertEquals(binaries.size(),
                     result.size());

        for (Binary binary : binaries) {
            assertTrue(result.contains(binary));
        }
    }
}
