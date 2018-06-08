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

package org.drools.workbench.services.verifier.plugin.client.api;

import java.util.HashMap;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class HeaderMetaDataTest {

    @Test(expected = IllegalArgumentException.class)
    public void tryToFindAndFail() throws Exception {
        new HeaderMetaData(new HashMap<>()).getPatternsByColumnNumber(1);
    }

    @Test
    public void isEmpty() throws Exception {
        assertTrue(new HeaderMetaData(new HashMap<>()).isEmpty());
    }

    @Test
    public void findByColumnNumber() throws Exception {
        final HashMap<Integer, ModelMetaData> patternsByColumnNumber = new HashMap<>();
        final ModelMetaData metaData = mock(ModelMetaData.class);
        patternsByColumnNumber.put(1, metaData);
        final HeaderMetaData headerMetaData = new HeaderMetaData(patternsByColumnNumber);
        assertEquals(metaData,
                     headerMetaData.getPatternsByColumnNumber(1));
        assertEquals(1, headerMetaData.size());
        assertFalse(headerMetaData.isEmpty());
    }
}