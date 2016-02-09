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

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AllKieProjectFilesFilterTest {

    private AllKieProjectFilesFilter filesFilter;

    @Before
    public void setUp() throws Exception {
        filesFilter = new AllKieProjectFilesFilter();
    }

    @Test
    public void testAllProjectFiles() throws Exception {

        assertTrue( filesFilter.accept( getTempFile( "File.java" ) ) );
        assertTrue( filesFilter.accept( getTempFile( "File.drl" ) ) );
        assertTrue( filesFilter.accept( getTempFile( "File.dslr" ) ) );
        assertTrue( filesFilter.accept( getTempFile( "File.dsl" ) ) );
        assertTrue( filesFilter.accept( getTempFile( "File.rdrl" ) ) );
        assertTrue( filesFilter.accept( getTempFile( "File.rdslr" ) ) );
        assertTrue( filesFilter.accept( getTempFile( "File.gdrl" ) ) );
        assertTrue( filesFilter.accept( getTempFile( "kmodule.xml" ) ) );

        assertFalse( filesFilter.accept( getTempFile( ".File.java" ) ) );
        assertFalse( filesFilter.accept( getTempFile( ".kmodule.xml" ) ) );

    }

    private org.uberfire.java.nio.file.Path getTempFile( final String fullFileName ) throws IOException {
        final Path path = mock( Path.class );
        final org.uberfire.java.nio.file.Path fileName = mock( Path.class );
        when( path.getFileName() ).thenReturn( fileName );
        when( fileName.toString() ).thenReturn( fullFileName );
        return path;
    }
}