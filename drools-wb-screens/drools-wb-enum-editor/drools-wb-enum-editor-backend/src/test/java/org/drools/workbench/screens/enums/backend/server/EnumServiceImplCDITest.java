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
 */

package org.drools.workbench.screens.enums.backend.server;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.drools.workbench.screens.enums.service.EnumService;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.guvnor.test.CDITestSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

public class EnumServiceImplCDITest extends CDITestSetup {

    private EnumService enumService;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        enumService = getReference(EnumService.class);
    }

    @After
    public void tearDown() throws Exception {
        super.cleanup();
    }

    @Test
    public void testValidate() throws Exception {
        final Path path = getPath("enums/src/main/resources/guvnor/feature/enums/personAge.enumeration");

        final List<ValidationMessage> validationMessages = enumService.validate(path);

        Assertions.assertThat(validationMessages).isEmpty();
    }

    @Test
    public void testValidateWrongSyntax() throws Exception {
        final Path path = getPath("enums/src/main/resources/guvnor/feature/enums/personAgeWrongSyntax.enumeration");

        final List<ValidationMessage> validationMessages = enumService.validate(path);

        Assertions.assertThat(validationMessages).hasSize(3);
        Assertions.assertThat(validationMessages.stream()
                                      .map(message -> message.getText())
                                      .filter(messageText -> isOneOfExpectedError(messageText))
                                      .count()).isEqualTo(3);
    }

    private static boolean isOneOfExpectedError(final String messageText) {
        return messageText.contains("Unable to load enumeration data.") ||
                messageText.contains("[Error: unbalanced braces [ ... ]]\n" +
                                             "[Near : {... [ 'Person.age' : [10[,20,30] ] ....}]\n" +
                                             "             ^\n" +
                                             "[Line: 1, Column: 1]") ||
                messageText.contains("Error type: org.mvel2.CompileException");
    }

    private Path getPath(String resource) throws URISyntaxException {
        final URL resourceURL = getClass().getResource(resource);
        final org.uberfire.java.nio.file.Path resourceNioPath = fileSystemProvider.getPath(resourceURL.toURI());
        return Paths.convert(resourceNioPath);
    }
}
