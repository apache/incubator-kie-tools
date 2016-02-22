/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.projecteditor.client.forms.dependencies.validation;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.client.resources.i18n.ProjectEditorConstants;

import static org.junit.Assert.*;

@RunWith( GwtMockitoTestRunner.class )
public class DependencyValidatorTest {

    @GwtMock
    ProjectEditorResources resources;

    @GwtMock
    ProjectEditorConstants constants;

    @Test
    public void testValid() throws Exception {
        DependencyValidator dependencyValidator = new DependencyValidator( new Dependency( new GAV( "groupId",
                                                                                                    "artifactId",
                                                                                                    "1.0" ) ) );

        assertTrue( dependencyValidator.validate() );
    }

    @Test
    public void testGroupId() throws Exception {
        DependencyValidator dependencyValidator = new DependencyValidator( new Dependency( new GAV( null,
                                                                                                    "artifactId",
                                                                                                    "1.0" ) ) );
        assertFalse( dependencyValidator.validate() );

        assertEquals( "DependencyIsMissingAGroupId", dependencyValidator.getMessage() );
    }

    @Test
    public void testArtifactId() throws Exception {
        DependencyValidator dependencyValidator = new DependencyValidator( new Dependency( new GAV( "groupId",
                                                                                                    null,
                                                                                                    "1.0" ) ) );
        assertFalse( dependencyValidator.validate() );

        assertEquals( "DependencyIsMissingAnArtifactId", dependencyValidator.getMessage() );
    }

    @Test
    public void testVersion() throws Exception {
        DependencyValidator dependencyValidator = new DependencyValidator( new Dependency( new GAV( "groupId",
                                                                                                    "artifactId",
                                                                                                    null ) ) );
        assertFalse( dependencyValidator.validate() );

        assertEquals( "DependencyIsMissingAVersion", dependencyValidator.getMessage() );
    }
}