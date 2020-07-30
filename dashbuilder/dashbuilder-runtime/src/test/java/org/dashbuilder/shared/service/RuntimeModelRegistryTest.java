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

package org.dashbuilder.shared.service;

import java.util.Collection;
import java.util.Optional;

import org.dashbuilder.shared.model.DashbuilderRuntimeMode;
import org.dashbuilder.shared.model.RuntimeModel;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RuntimeModelRegistryTest {
    
    @Test
    public void testAcceptingNewImportsMultiple() {
        RuntimeModelRegistry registry = new RuntimeModelRegistryMock(DashbuilderRuntimeMode.MULTIPLE_IMPORT, false);
        assertTrue(registry.acceptingNewImports());
    }
    
    @Test
    public void testNotAcceptingNewImportsStatic() {
        RuntimeModelRegistry registry = new RuntimeModelRegistryMock(DashbuilderRuntimeMode.STATIC, false);
        assertFalse(registry.acceptingNewImports());
    }
    
    @Test
    public void testNotAcceptingNewImportsSingleAndNotEmpty() {
        RuntimeModelRegistry registry = new RuntimeModelRegistryMock(DashbuilderRuntimeMode.SINGLE_IMPORT, false);
        assertFalse(registry.acceptingNewImports());
    }
    
    @Test
    public void testNotAcceptingNewImportsSingleAndEmpty() {
        RuntimeModelRegistry registry = new RuntimeModelRegistryMock(DashbuilderRuntimeMode.SINGLE_IMPORT, true);
        assertTrue(registry.acceptingNewImports());
    }
    
    // Having classloading issues with Mockito, hence having to create this
    public class RuntimeModelRegistryMock implements RuntimeModelRegistry {

        private DashbuilderRuntimeMode mode;
        
        
        public RuntimeModelRegistryMock(DashbuilderRuntimeMode mode, boolean b) {
            super();
            this.mode = mode;
            this.b = b;
        }

        private boolean b;

        @Override
        public Optional<RuntimeModel> single() {
            return null;
        }

        @Override
        public boolean isEmpty() {
            return b;
        }

        @Override
        public DashbuilderRuntimeMode getMode() {
            return mode;
        }

        @Override
        public Optional<RuntimeModel> get(String id) {
            return null;
        }

        @Override
        public void setMode(DashbuilderRuntimeMode mode) {
        }

        @Override
        public Optional<RuntimeModel> registerFile(String filePath) {
            return null;
        }

        @Override
        public void remove(String runtimeModelid) {
            
        }

        @Override
        public Collection<String> availableModels() {
            return null;
        }}

}