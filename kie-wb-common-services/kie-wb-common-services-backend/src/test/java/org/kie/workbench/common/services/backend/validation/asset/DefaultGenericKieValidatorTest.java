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

package org.kie.workbench.common.services.backend.validation.asset;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import com.google.common.io.Resources;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.guvnor.test.TestFileSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultGenericKieValidatorTest {

    private TestFileSystem testFileSystem;
    private DefaultGenericKieValidator validator;

    @Before
    public void setUp() throws Exception {
        testFileSystem = new TestFileSystem();

        validator = testFileSystem.getReference(DefaultGenericKieValidator.class);
    }

    @After
    public void tearDown() throws Exception {
        testFileSystem.tearDown();
    }

    @Test
    public void testWorks() throws Exception {
        final Path path = resourcePath("/GuvnorM2RepoDependencyExample1/src/main/resources/rule2.drl");
        final URL urlToValidate = this.getClass().getResource("/GuvnorM2RepoDependencyExample1/src/main/resources/rule2.drl");

        final List<ValidationMessage> errors = validator.validate(path,
                                                                  Resources.toString(urlToValidate, Charset.forName("UTF-8")));

        assertTrue(errors.isEmpty());
    }

    @Test
    public void validatingAnAlreadyInvalidAssetShouldReportErrors() throws Exception {
        final Path path = resourcePath("/BuilderExampleBrokenSyntax/src/main/resources/rule1.drl");
        final URL urlToValidate = this.getClass().getResource("/BuilderExampleBrokenSyntax/src/main/resources/rule1.drl");

        final List<ValidationMessage> errors1 = validator.validate(path,
                                                                   Resources.toString(urlToValidate, Charset.forName("UTF-8")));

        final List<ValidationMessage> errors2 = validator.validate(path,
                                                                   Resources.toString(urlToValidate, Charset.forName("UTF-8")));

        assertFalse(errors1.isEmpty());
        assertFalse(errors2.isEmpty());
        assertEquals(errors1.size(),
                     errors2.size());
    }

    private Path resourcePath(final String resourceName) throws URISyntaxException {
        final URL url = this.getClass().getResource(resourceName);
        return Paths.convert(testFileSystem.fileSystemProvider.getPath(url.toURI()));
    }
}
