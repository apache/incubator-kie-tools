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

package org.kie.workbench.common.screens.datamodeller.backend.server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.backend.server.file.DataModelerCopyHelper;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.io.IOService;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataModelerServiceCopyTest {

    private static final Path PATH = PathFactory.newPath( "Sample.java", "default://project/src/main/java/old/package/Sample.java" );
    private static final String NEW_NAME = "NewSample";
    private static final String NEW_PACKAGE_NAME = "new.package";
    private static final Path TARGET_DIRECTORY = PathFactory.newPath( "/", "default://project/src/main/java/new/package" );
    private static final String COMMENT = "comment";

    @Mock
    private DataModelerCopyHelper copyHelper;

    @Mock
    private CopyService copyService;

    @Mock
    private KieProjectService projectService;

    @Mock
    private IOService ioService;

    @Spy
    @InjectMocks
    private DataModelerServiceImpl dataModelerService;

    @Test
    public void copyToAnotherPackageTest() {
        makeCopy( true );

        verify( dataModelerService ).refactorClass( eq( PATH ), eq( NEW_PACKAGE_NAME ), eq( NEW_NAME ) );
        verify( copyService ).copy( eq( PATH ), eq( NEW_NAME ), eq( TARGET_DIRECTORY ), eq( COMMENT ) );
    }

    @Test
    public void copyToAnotherPackageWithoutRefactorTest() {
        makeCopy( false );

        verify( dataModelerService, never() ).refactorClass( eq( PATH ), eq( NEW_PACKAGE_NAME ), eq( NEW_NAME ) );
        verify( copyService ).copy( eq( PATH ), eq( NEW_NAME ), eq( TARGET_DIRECTORY ), eq( COMMENT ) );
    }

    private void makeCopy( boolean refactor ) {
        dataModelerService.copy( PATH, NEW_NAME, NEW_PACKAGE_NAME, TARGET_DIRECTORY, COMMENT, refactor );
    }
}
