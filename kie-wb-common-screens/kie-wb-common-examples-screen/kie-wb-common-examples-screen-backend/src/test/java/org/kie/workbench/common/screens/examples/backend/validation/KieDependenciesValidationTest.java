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
import java.util.Optional;

import org.apache.maven.model.Dependency;
import org.guvnor.common.services.project.model.Dependencies;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.POMService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.model.ExampleProject;
import org.kie.workbench.common.screens.examples.model.ExampleProjectError;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class KieDependenciesValidationTest {

    private KieDependenciesValidator validator;

    @Mock
    private POMService pomService;

    @Mock
    private MandatoryDependencies mandatoryDependencies;

    @Mock
    private ExampleProject exampleProject;

    @Mock
    private Dependencies dependencies;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private POM pom;

    @Before
    public void setUp() {
        Path path = mock(Path.class);
        when(path.toURI()).thenReturn("/");
        when(this.exampleProject.getRoot()).thenReturn(path);
        when(this.pomService.load(any())).thenReturn(this.pom);

        when(this.mandatoryDependencies.getDependencies())
                .thenReturn(Arrays.asList(createMandatoryDependency("org.mandatory",
                                                                    "dependencyA",
                                                                    "1.0.0",
                                                                    "provided"),
                                          createMandatoryDependency("org.mandatory",
                                                                    "dependencyB",
                                                                    "2.1.0",
                                                                    "provided"),
                                          createMandatoryDependency("org.mandatory",
                                                                    "dependencyTest",
                                                                    "1.1.0",
                                                                    "test")));

        this.validator = new KieDependenciesValidator(this.pomService,
                                                      this.mandatoryDependencies);
    }

    @Test
    public void testContainsAllMandatoryDependencies() {

        when(dependencies.get(eq(new GAV("org.mandatory",
                                         "dependencyA",
                                         "1.0.0")))).thenReturn(createDependency("org.mandatory",
                                                                                 "dependencyA",
                                                                                 "1.0.0",
                                                                                 "provided"));

        when(dependencies.get(eq(new GAV("org.mandatory",
                                         "dependencyB",
                                         "2.1.0")))).thenReturn(createDependency("org.mandatory",
                                                                                 "dependencyB",
                                                                                 "2.1.0",
                                                                                 "provided"));

        when(dependencies.get(eq(new GAV("org.not.mandatory",
                                         "dependencyA",
                                         "3.1.0")))).thenReturn(createDependency("org.not.mandatory",
                                                                                 "dependencyA",
                                                                                 "3.1.0",
                                                                                 "compile"));
        when(dependencies.get(eq(new GAV("org.mandatory",
                                         "dependencyTest",
                                         "1.1.0")))).thenReturn(createDependency("org.mandatory",
                                                                                 "dependencyTest",
                                                                                 "1.1.0",
                                                                                 "test"));

        when(this.pom.getDependencies()).thenReturn(dependencies);
        Optional<ExampleProjectError> errors = this.validator.validate(exampleProject);
        assertFalse(errors.isPresent());
    }

    @Test
    public void testContainsMandatoryDependenciesWithWrongTestScope() {

        when(dependencies.get(eq(new GAV("org.mandatory",
                                         "dependencyA",
                                         "1.0.0")))).thenReturn(createDependency("org.mandatory",
                                                                                 "dependencyA",
                                                                                 "1.0.0",
                                                                                 "provided"));

        when(dependencies.get(eq(new GAV("org.mandatory",
                                         "dependencyB",
                                         "2.1.0")))).thenReturn(createDependency("org.mandatory",
                                                                                 "dependencyB",
                                                                                 "2.1.0",
                                                                                 "provided"));
        when(dependencies.get(eq(new GAV("org.mandatory",
                                         "dependencyTest",
                                         "1.1.0")))).thenReturn(createDependency("org.mandatory",
                                                                                 "dependencyTest",
                                                                                 "1.1.0",
                                                                                 "compile"));

        when(this.pom.getDependencies()).thenReturn(dependencies);
        Optional<ExampleProjectError> errors = this.validator.validate(exampleProject);
        assertFalse(errors.isPresent());
    }

    @Test
    public void testMissingOneKieDependency() {

        when(dependencies.get(eq(new GAV("org.mandatory",
                                         "dependencyA",
                                         "1.0.0")))).thenReturn(createDependency("org.mandatory",
                                                                                 "dependencyA",
                                                                                 "1.0.0",
                                                                                 "provided"));

        when(dependencies.get(eq(new GAV("org.not.mandatory",
                                         "dependencyA",
                                         "3.1.0")))).thenReturn(createDependency("org.not.mandatory",
                                                                                 "dependencyA",
                                                                                 "3.1.0",
                                                                                 "compile"));

        when(this.pom.getDependencies()).thenReturn(dependencies);
        Optional<ExampleProjectError> error = this.validator.validate(exampleProject);
        assertTrue(error.isPresent());
        assertEquals(KieDependenciesValidator.class.getCanonicalName(),
                     error.get().getId());
    }

    @Test
    public void testMissingAllKieDependencies() {

        when(dependencies.get(eq(new GAV("org.not.mandatory",
                                         "dependencyA",
                                         "3.1.0"))))
                .thenReturn(createDependency("org.not.mandatory",
                                             "dependencyA",
                                             "3.1.0",
                                             "compile"));

        when(this.pom.getDependencies()).thenReturn(dependencies);
        Optional<ExampleProjectError> error = this.validator.validate(exampleProject);
        assertTrue(error.isPresent());
        assertEquals(KieDependenciesValidator.class.getCanonicalName(),
                     error.get().getId());
    }

    @Test
    public void testMissingAllKieDependenciesWithNullScope() {

        when(dependencies.get(eq(new GAV("org.mandatory",
                                         "dependencyA",
                                         "1.0.0")))).thenReturn(createDependency("org.mandatory",
                                                                                 "dependencyA",
                                                                                 "1.0.0",
                                                                                 "provided"));

        when(dependencies.get(eq(new GAV("org.mandatory",
                                         "dependencyB",
                                         "2.1.0")))).thenReturn(createDependency("org.mandatory",
                                                                                 "dependencyB",
                                                                                 "2.1.0",
                                                                                 null));
        when(dependencies.get(eq(new GAV("org.mandatory",
                                         "dependencyTest",
                                         "1.1.0")))).thenReturn(createDependency("org.mandatory",
                                                                                 "dependencyTest",
                                                                                 "1.1.0",
                                                                                 "test"));

        when(this.pom.getDependencies()).thenReturn(dependencies);
        Optional<ExampleProjectError> error = this.validator.validate(exampleProject);
        assertTrue(error.isPresent());
        assertEquals(KieDependenciesValidator.class.getCanonicalName(),
                     error.get().getId());
    }

    private Dependency createMandatoryDependency(String groupId,
                                                 String artifactId,
                                                 String version,
                                                 String scope) {

        Dependency dependency = new Dependency();
        dependency.setGroupId(groupId);
        dependency.setArtifactId(artifactId);
        dependency.setVersion(version);
        dependency.setScope(scope);
        return dependency;
    }

    private org.guvnor.common.services.project.model.Dependency createDependency(String groupId,
                                                                                 String artifactId,
                                                                                 String version,
                                                                                 String scope) {

        org.guvnor.common.services.project.model.Dependency dependency = new org.guvnor.common.services.project.model.Dependency();
        dependency.setGroupId(groupId);
        dependency.setArtifactId(artifactId);
        dependency.setVersion(version);
        dependency.setScope(scope);
        return dependency;
    }
}