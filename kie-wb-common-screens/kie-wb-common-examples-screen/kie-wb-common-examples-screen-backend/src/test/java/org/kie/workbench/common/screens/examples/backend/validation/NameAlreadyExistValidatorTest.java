/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.examples.backend.validation;

import java.util.Optional;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.model.ExampleProjectError;
import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.kie.workbench.common.screens.examples.service.ProjectImportService;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NameAlreadyExistValidatorTest {

    private NameAlreadyExistValidator validator;

    private Logger logger = LoggerFactory.getLogger(NameAlreadyExistValidatorTest.class);

    @Mock
    private ProjectImportService projectImportService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OrganizationalUnit ou;

    @Mock
    private ImportProject importProject;

    @Before
    public void setUp() {

        when(importProject.getName()).thenReturn("project1");
        when(ou.getSpace().getName()).thenReturn("space1");

        this.validator = new NameAlreadyExistValidator(this.projectImportService);
    }

    @Test
    public void testNameAlreadyExist() {
        when(this.projectImportService.exist(any(), any())).thenReturn(true);
        Optional<ExampleProjectError> error = this.validator.validate(ou, importProject);
        assertTrue(error.isPresent());
        logger.info(error.get().getDescription());
    }

    @Test
    public void testProjectDoesNotExist() {
        when(this.projectImportService.exist(any(), any())).thenReturn(false);
        Optional<ExampleProjectError> error = this.validator.validate(ou, importProject);
        assertFalse(error.isPresent());
    }
}