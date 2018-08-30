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

import javax.enterprise.inject.Instance;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.validation.CopyValidator;
import org.kie.workbench.common.services.shared.validation.DeleteValidator;
import org.kie.workbench.common.services.shared.validation.SaveValidator;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.ValidationService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ValidationServiceImplTest {

    @Mock
    private ValidationService uberfireValidationService;
    @Mock
    private PackageNameValidator packageValidator;
    @Mock
    private ModuleNameValidator projectValidator;
    @Mock
    private JavaFileNameValidator javaValidator;
    @Mock
    private Instance<SaveValidator> saveValidatorInstance;
    @Mock
    private Instance<CopyValidator> copyValidatorInstance;
    @Mock
    private Instance<DeleteValidator> deleteValidatorInstance;

    @InjectMocks
    private ValidationServiceImpl validationService;

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
    public void testValidateVersion() {
        assertTrue(validationService.validateGAVVersion("1111"));
        assertTrue(validationService.validateGAVVersion("1.0-SNAPSHOT"));
        assertTrue(validationService.validateGAVVersion("1.1.Final"));
        assertTrue(validationService.validateGAVVersion("1.1-Final"));
        assertTrue(validationService.validateGAVVersion("1.1-Beta-11"));

        assertFalse(validationService.validateGAVVersion("1.1 Beta 11"));
    }

    @Test
    public void testValidatorsCalled() {
        String mockName = "bxmsftw";
        Path mockPath = mock(Path.class);

        validationService.isProjectNameValid(mockName);
        validationService.isPackageNameValid(mockName);
        validationService.isFileNameValid(mockPath, mockName);
        validationService.isJavaFileNameValid(mockName);
        validationService.isFileNameValid(mockName);

        verify(projectValidator).isValid(mockName);
        verify(packageValidator).isValid(mockName);
        verify(uberfireValidationService).isFileNameValid(mockPath, mockName);
        verify(javaValidator).isValid(mockName);
        verify(uberfireValidationService).isFileNameValid(mockName);
    }

    @Test
    public void testProjectNameValid() {

        when(projectValidator.isValid(any())).thenReturn(false);
        assertFalse(validationService.isProjectNameValid("foo"));

        when(projectValidator.isValid(any())).thenReturn(true);
        assertTrue(validationService.isProjectNameValid("t"));
        assertTrue(validationService.isProjectNameValid("test"));
        assertTrue(validationService.isProjectNameValid("test_"));
        assertTrue(validationService.isProjectNameValid("test-"));
        assertTrue(validationService.isProjectNameValid("test."));
        assertTrue(validationService.isProjectNameValid("test0"));
        assertTrue(validationService.isProjectNameValid("Test-0"));
        assertTrue(validationService.isProjectNameValid("Test-._"));
        assertTrue(validationService.isProjectNameValid("Test.test"));

        assertFalse(validationService.isProjectNameValid("test@"));
        assertFalse(validationService.isProjectNameValid("test\\u1234"));
        assertFalse(validationService.isProjectNameValid("\\u1234\\u1111"));
        assertFalse(validationService.isProjectNameValid("test "));
        assertFalse(validationService.isProjectNameValid(" test "));
        assertFalse(validationService.isProjectNameValid("test!"));
    }

    @Test
    public void testValidateBranchName() {
        assertTrue(validationService.isBranchNameValid("test"));
        assertTrue(validationService.isBranchNameValid("test#"));
        assertTrue(validationService.isBranchNameValid("test!"));
        assertTrue(validationService.isBranchNameValid("test-"));
        assertTrue(validationService.isBranchNameValid("test_"));
        assertTrue(validationService.isBranchNameValid("test&"));
        assertTrue(validationService.isBranchNameValid("test%"));

        assertFalse(validationService.isBranchNameValid("@test"));
        assertFalse(validationService.isBranchNameValid("test@"));
        assertFalse(validationService.isBranchNameValid("te@st"));

        assertFalse(validationService.isBranchNameValid("/test"));
        assertFalse(validationService.isBranchNameValid("test/"));
        assertFalse(validationService.isBranchNameValid("te/st"));

        assertFalse(validationService.isBranchNameValid("..test"));
        assertFalse(validationService.isBranchNameValid("test.."));
        assertFalse(validationService.isBranchNameValid("te..st"));

        assertTrue(validationService.isBranchNameValid("\041test"));
        assertTrue(validationService.isBranchNameValid("test\041"));
        assertTrue(validationService.isBranchNameValid("te\041st"));

        assertFalse(validationService.isBranchNameValid("\000test"));
        assertFalse(validationService.isBranchNameValid("test\000"));
        assertFalse(validationService.isBranchNameValid("te\000st"));

        assertFalse(validationService.isBranchNameValid("\010test"));
        assertFalse(validationService.isBranchNameValid("test\010"));
        assertFalse(validationService.isBranchNameValid("te\010st"));

        assertFalse(validationService.isBranchNameValid("\037test"));
        assertFalse(validationService.isBranchNameValid("test\037"));
        assertFalse(validationService.isBranchNameValid("te\037st"));

        assertFalse(validationService.isBranchNameValid("\177test"));
        assertFalse(validationService.isBranchNameValid("test\177"));
        assertFalse(validationService.isBranchNameValid("te\177st"));

        assertFalse(validationService.isBranchNameValid(" test"));
        assertFalse(validationService.isBranchNameValid("test "));
        assertFalse(validationService.isBranchNameValid("te st"));

        assertFalse(validationService.isBranchNameValid("~test"));
        assertFalse(validationService.isBranchNameValid("test~"));
        assertFalse(validationService.isBranchNameValid("te~st"));

        assertFalse(validationService.isBranchNameValid("^test"));
        assertFalse(validationService.isBranchNameValid("test^"));
        assertFalse(validationService.isBranchNameValid("te^st"));

        assertFalse(validationService.isBranchNameValid(":test"));
        assertFalse(validationService.isBranchNameValid("test:"));
        assertFalse(validationService.isBranchNameValid("te:st"));

        assertFalse(validationService.isBranchNameValid("?test"));
        assertFalse(validationService.isBranchNameValid("test?"));
        assertFalse(validationService.isBranchNameValid("te?st"));

        assertFalse(validationService.isBranchNameValid("*test"));
        assertFalse(validationService.isBranchNameValid("test*"));
        assertFalse(validationService.isBranchNameValid("te*st"));

        assertFalse(validationService.isBranchNameValid("[test"));
        assertFalse(validationService.isBranchNameValid("test["));
        assertFalse(validationService.isBranchNameValid("te[st"));

        assertFalse(validationService.isBranchNameValid(".test"));
        assertFalse(validationService.isBranchNameValid("test."));
        assertTrue(validationService.isBranchNameValid("te.st"));

        assertFalse(validationService.isBranchNameValid("//test"));
        assertFalse(validationService.isBranchNameValid("test//"));
        assertFalse(validationService.isBranchNameValid("te//st"));

        assertFalse(validationService.isBranchNameValid("@{test"));
        assertFalse(validationService.isBranchNameValid("test@{"));
        assertFalse(validationService.isBranchNameValid("te@{st"));

        assertFalse(validationService.isBranchNameValid("@"));

        assertFalse(validationService.isBranchNameValid("\\test"));
        assertFalse(validationService.isBranchNameValid("test\\"));
        assertFalse(validationService.isBranchNameValid("te\\st"));

    }
}
