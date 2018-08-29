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
 *
 */

package org.kie.workbench.common.screens.examples.backend.validation;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.POMService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.kie.workbench.common.screens.examples.model.ExampleProjectError;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CheckModulesValidationTest {

    private CheckModulesValidator validator;

    @Mock
    private POMService pomService;

    @Mock
    private ImportProject importProject;

    @Mock
    private POM pom;

    @Before
    public void setUp() {
        Path path = mock(Path.class);
        when(path.toURI()).thenReturn("/");
        when(this.importProject.getRoot()).thenReturn(path);
        when(this.pomService.load(any())).thenReturn(this.pom);

        this.validator = new CheckModulesValidator(this.pomService);
    }

    @Test
    public void testProjectHasNoModules() {
        when(this.pom.getModules()).thenReturn(Collections.EMPTY_LIST);
        Optional<ExampleProjectError> error = this.validator.validate(importProject);
        assertFalse(error.isPresent());
    }

    @Test
    public void testProjectHasModules() {
        when(this.pom.getModules()).thenReturn(Arrays.asList("aModule"));

        Optional<ExampleProjectError> error = this.validator.validate(importProject);
        assertTrue(error.isPresent());
        assertEquals(CheckModulesValidator.class.getCanonicalName(),
                     error.get().getId());
    }
}