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

package org.dashbuilder.components.internal;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Access internal component information and expose during runtime.
 *
 */
public class InternalComponentsInfoTest {

    @Test
    public void testMissingPropertiesFile() {
        ProvidedComponentInfo internalComponentsInfo = new ProvidedComponentInfo();
        internalComponentsInfo.loadProperties("do not exist");
        List<String> components = internalComponentsInfo.getInternalComponentsList();
        assertTrue(components.isEmpty());
        assertNull(internalComponentsInfo.getInternalComponentsRootPath());
    }

    @Test
    public void testFoundPropertiesFile() {
        ProvidedComponentInfo internalComponentsInfo = ProvidedComponentInfo.get();
        List<String> components = internalComponentsInfo.getInternalComponentsList();
        assertTrue(components.contains("c1"));
        assertTrue(components.contains("c2"));
        assertEquals("path/to/components", internalComponentsInfo.getInternalComponentsRootPath());
    }

}