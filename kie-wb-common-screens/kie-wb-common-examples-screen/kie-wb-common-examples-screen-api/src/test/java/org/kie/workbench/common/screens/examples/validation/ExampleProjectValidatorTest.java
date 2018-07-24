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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.model.ExampleProject;
import org.kie.workbench.common.screens.examples.model.ExampleProjectError;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExampleProjectValidatorTest {

    private ExampleProjectValidator exampleProjectValidator;

    @Mock
    private POMService pomService;

    @Mock
    private ExampleProject exampleProject;

    @Captor
    private ArgumentCaptor<Path> pathCaptor;

    @Before
    public void setUp() {
        this.exampleProjectValidator = spy(new TestValidator());
    }

    @Test
    public void testGetPomWithFinalSlash() {
        Path path = mock(Path.class);
        when(path.toURI()).thenReturn("/");
        when(this.exampleProject.getRoot()).thenReturn(path);
        this.exampleProjectValidator.getPom(this.pomService,
                                            path);

        verify(this.pomService).load(pathCaptor.capture());
        assertEquals("/pom.xml",
                     pathCaptor.getValue().toURI());
    }

    @Test
    public void testGetPomWithoutFinalSlash() {
        Path path = mock(Path.class);
        when(path.toURI()).thenReturn("/testPath");
        when(this.exampleProject.getRoot()).thenReturn(path);
        this.exampleProjectValidator.getPom(this.pomService,
                                            path);

        verify(this.pomService).load(pathCaptor.capture());
        assertEquals("/testPath/pom.xml",
                     pathCaptor.getValue().toURI());
    }

    private class TestValidator extends ExampleProjectValidator {

        @Override
        protected Optional<ExampleProjectError> getError(Path projectPath) {
            return Optional.empty();
        }
    }
}