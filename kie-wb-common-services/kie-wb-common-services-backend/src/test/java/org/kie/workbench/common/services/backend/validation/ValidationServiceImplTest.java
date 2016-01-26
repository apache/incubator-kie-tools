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

package org.kie.workbench.common.services.backend.validation;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ValidationServiceImplTest {

    private ValidationServiceImpl validationService;

    @Before
    public void setUp() throws Exception {
        validationService = new ValidationServiceImpl( mock( org.uberfire.ext.editor.commons.service.ValidationService.class ),
                                                       mock( PackageNameValidator.class ),
                                                       mock( ProjectNameValidator.class ),
                                                       mock( JavaFileNameValidator.class ) );

    }

    @Test
    public void testValidateGroup() {
        assertFalse(validationService.validateGroupId("proj w spcs"));
        assertFalse(validationService.validateGroupId("."));
        assertFalse(validationService.validateGroupId(".name"));
        assertFalse(validationService.validateGroupId("name."));

        assertTrue(validationService.validateGroupId("proj-w-hyps"));
        assertTrue(validationService.validateGroupId("proj.w.int"));
        assertTrue(validationService.validateGroupId("proj.w.123"));
    }

    @Test
    public void testValidateArtifact() {
        assertFalse(validationService.validateArtifactId("proj w spcs"));
        assertFalse(validationService.validateArtifactId("."));
        assertFalse(validationService.validateArtifactId(".name"));
        assertFalse(validationService.validateArtifactId("name."));

        assertTrue(validationService.validateArtifactId("proj-w-hyps"));
        assertTrue(validationService.validateArtifactId("proj.w.int"));
        assertTrue(validationService.validateArtifactId("proj.2.123"));
    }

    @Test
    public void testValidateVersion() throws Exception {
        assertTrue( validationService.validateGAVVersion( "1111" ) );
        assertTrue( validationService.validateGAVVersion( "1.0-SNAPSHOT" ) );
        assertTrue( validationService.validateGAVVersion( "1.1.Final" ) );
        assertTrue( validationService.validateGAVVersion( "1.1-Final" ) );
        assertTrue( validationService.validateGAVVersion( "1.1-Beta-11" ) );

        assertFalse( validationService.validateGAVVersion( "1.1 Beta 11" ) );
    }
}