/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.drltext.backend.server;

import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.drools.workbench.screens.drltext.service.DRLTextEditorService;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.guvnor.test.CDITestSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DRLTextEditorServiceImplCDITest extends CDITestSetup {

    private List<ValidationMessage> validationMessages;
    private DRLTextEditorService drlService;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        drlService = getReference(DRLTextEditorService.class);
    }

    @After
    public void tearDown() throws Exception {
        super.cleanup();
    }

    @Test
    public void testValidDSRLFile() throws Exception {
        validateResource("DslSentencesInDrlFile/src/main/resources/org/kiegroup/unemploy.dslr");

        assertEquals(0, validationMessages.size());
    }

    @Test
    public void testInvalidDSRLFile() throws Exception {
        validateResource("DslSentencesInDrlFile/src/main/resources/org/kiegroup/unemploy-invalid.dslr");

        assertEquals(3, validationMessages.size());
        assertTrue(validationMessages.get(0).getText().contains("Unable to expand: a"));
        assertTrue(validationMessages.get(1).getText().contains("Unable to expand:     b"));
        assertTrue(validationMessages.get(2).getText().contains("mismatched input 'then'"));
    }

    private void validateResource(final String resource) throws Exception {
        final URL resourceURL = getClass().getResource(resource);
        final org.uberfire.java.nio.file.Path resourceNioPath = fileSystemProvider.getPath(resourceURL.toURI());
        final Path resourcePath = Paths.convert(resourceNioPath);

        validationMessages = drlService.validate(resourcePath,
                                                 IOUtils.toString(resourceURL.toURI(), "UTF-8"));
    }
}
