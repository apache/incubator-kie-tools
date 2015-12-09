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

package org.kie.workbench.common.services.backend.whitelist;

import java.util.HashMap;

import org.guvnor.common.services.backend.metadata.MetadataServerSideService;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.test.TestFileSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class PackageNameWhiteListSaverTest {

    @Mock
    private IOService ioService;

    @Mock
    private MetadataServerSideService metadataService;

    @Mock
    private CommentedOptionFactory commentedOptionFactory;

    private PackageNameWhiteListSaver saver;
    private TestFileSystem testFileSystem;

    @Before
    public void setUp() throws Exception {

        testFileSystem = new TestFileSystem();

        saver = new PackageNameWhiteListSaver( ioService,
                                               metadataService,
                                               commentedOptionFactory );
    }

    @After
    public void tearDown() throws Exception {
        testFileSystem.tearDown();
    }

    @Test
    public void testSave() throws Exception {

        final Path path = testFileSystem.createTempFile( "whitelist" );
        final WhiteList whiteList = new WhiteList();
        whiteList.add( "org.drools" );
        whiteList.add( "org.guvnor" );
        final Metadata metadata = new Metadata();
        final String comment = "comment";

        final HashMap<String, Object> attributes = new HashMap<String, Object>();
        when( metadataService.setUpAttributes( path, metadata ) ).thenReturn( attributes );
        final CommentedOption commentedOption = mock( CommentedOption.class );
        when( commentedOptionFactory.makeCommentedOption( "comment" ) ).thenReturn( commentedOption );

        saver.save( path,
                    whiteList,
                    metadata,
                    comment );

        ArgumentCaptor<String> whiteListTextArgumentCaptor = ArgumentCaptor.forClass( String.class );

        verify( ioService ).write( any( org.uberfire.java.nio.file.Path.class ),
                                   whiteListTextArgumentCaptor.capture(),
                                   eq( attributes ),
                                   eq( commentedOption ) );

        final String whiteListAsText = whiteListTextArgumentCaptor.getValue();

        assertTrue( whiteListAsText.contains( "org.drools" ) );
        assertTrue( whiteListAsText.contains( "org.guvnor" ) );

    }
}