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

import java.net.URL;
import java.util.List;

import org.guvnor.common.services.backend.file.JavaFileFilter;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.guvnor.test.TestFileSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.*;

public class DefaultGenericKieValidatorTest {

    private TestFileSystem             testFileSystem;
    private DefaultGenericKieValidator validator;

    @Before
    public void setUp() throws Exception {
        testFileSystem = new TestFileSystem();

        validator = testFileSystem.getReference( DefaultGenericKieValidator.class );
    }

    @After
    public void tearDown() throws Exception {
        testFileSystem.tearDown();
    }

    @Test
    public void testWorks() throws Exception {

        final URL urlToValidate = this.getClass().getResource( "/GuvnorM2RepoDependencyExample1/src/main/resources/rule2.drl" );
        final Path pathToValidate = testFileSystem.fileSystemProvider.getPath( urlToValidate.toURI() );

        final List<ValidationMessage> validationMessages = validator.validate( Paths.convert( pathToValidate ),
                                                                               this.getClass().getResourceAsStream( "/GuvnorM2RepoDependencyExample1/src/main/resources/rule2.drl" ),
                                                                               new JavaFileFilter() );

        assertTrue( validationMessages.isEmpty() );
    }

    @Test
    public void testNoFilterForJavaFiles() throws Exception {

        final URL urlToValidate = this.getClass().getResource( "/GuvnorM2RepoDependencyExample1/src/main/resources/rule2.drl" );
        final Path pathToValidate = testFileSystem.fileSystemProvider.getPath( urlToValidate.toURI() );

        final List<ValidationMessage> validationMessages = validator.validate( Paths.convert( pathToValidate ),
                                                                               this.getClass().getResourceAsStream( "/GuvnorM2RepoDependencyExample1/src/main/resources/rule2.drl" ) );

        assertFalse( validationMessages.isEmpty() );
    }

    @Test
    public void testNoProject() throws Exception {

        final URL urlToValidate = this.getClass().getResource( "/META-INF/beans.xml" );
        final Path pathToValidate = testFileSystem.fileSystemProvider.getPath( urlToValidate.toURI() );

        final List<ValidationMessage> validationMessages = validator.validate( Paths.convert( pathToValidate ),
                                                                               this.getClass().getResourceAsStream( "/META-INF/beans.xml" ) );

        assertTrue( validationMessages.isEmpty() );
    }
}