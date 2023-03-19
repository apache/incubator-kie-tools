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

package org.dashbuilder.backend.services.impl;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ImportValidationServiceImplTest {

    ImportValidationServiceImpl importValidationService;

    @Before
    public void init() {
        importValidationService = new ImportValidationServiceImpl();
    }

    @Test
    public void validFileTest() {
        String file = this.getClass().getResource("/valid_import.zip").getFile();
        assertTrue(importValidationService.validate(file));
    }
    
    @Test
    public void invalidFileTest() {
        String file = this.getClass().getResource("/not_valid.zip").getFile();
        assertFalse(importValidationService.validate(file));
    }

}