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


package org.kie.workbench.common.stunner.core.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class BindableShapeSetThumbProviderTest {

    @Mock
    DefinitionManager definitionManager;

    @Mock
    DefinitionManager definitionManager2;

    @Test
    public void testIsSameClass() {
        BindableShapeSetThumbProvider provider = new StubProvider(definitionManager);
        BindableShapeSetThumbProvider sameProvider = new StubProvider(definitionManager2);
        assertTrue(provider.isSameClass(provider.getClass(), provider.getClass()));
        assertTrue(provider.isSameClass(provider.getClass(), sameProvider.getClass()));
        assertTrue(provider.isSameClass(sameProvider.getClass(), provider.getClass()));
        assertFalse(provider.isSameClass(null, provider.getClass()));
        assertFalse(provider.isSameClass(provider.getClass(), null));
        assertFalse(provider.isSameClass(provider.getClass(), Object.class));
        assertFalse(provider.isSameClass(Object.class, provider.getClass()));
    }

    private class StubProvider extends BindableShapeSetThumbProvider {

        public StubProvider(DefinitionManager definitionManager) {
            super(definitionManager);
        }

        @Override
        protected boolean thumbFor(Class<?> clazz) {
            return false;
        }

        @Override
        public String getThumbnailUri() {
            return null;
        }
    }
}
