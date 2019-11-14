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

package org.kie.workbench.common.screens.examples.validation;

import java.util.Optional;

import org.guvnor.common.services.project.service.POMService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.model.ExampleProjectError;
import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ImportProjectValidatorTest {

    private ImportProjectValidator importProjectValidator;

    @Mock
    private POMService pomService;

    @Mock
    private ImportProject importProject;

    @Captor
    private ArgumentCaptor<Path> pathCaptor;

    @Before
    public void setUp() {
        this.importProjectValidator = spy(new TestValidator());
    }

    @Test
    public void testGetPomWithFinalSlash() {
        Path path = mock(Path.class);
        when(path.toURI()).thenReturn("/");
        when(this.importProject.getRoot()).thenReturn(path);
        this.importProjectValidator.getPom(this.pomService,
                                           path);

        verify(this.pomService).load(pathCaptor.capture());
        assertEquals("/pom.xml",
                     pathCaptor.getValue().toURI());
    }

    @Test
    public void testGetPomWithoutFinalSlash() {
        Path path = mock(Path.class);
        when(path.toURI()).thenReturn("/testPath");
        when(this.importProject.getRoot()).thenReturn(path);
        this.importProjectValidator.getPom(this.pomService,
                                           path);

        verify(this.pomService).load(pathCaptor.capture());
        assertEquals("/testPath/pom.xml",
                     pathCaptor.getValue().toURI());
    }

    private class TestValidator extends ImportProjectValidator {

        @Override
        protected Optional<ExampleProjectError> getError(OrganizationalUnit ou, ImportProject importProject) {
            return Optional.empty();
        }
    }
}