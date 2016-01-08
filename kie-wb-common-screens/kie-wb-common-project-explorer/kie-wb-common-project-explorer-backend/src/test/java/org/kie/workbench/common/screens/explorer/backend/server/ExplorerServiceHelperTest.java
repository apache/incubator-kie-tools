/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.explorer.backend.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.guvnor.common.services.backend.file.LinkedFilter;
import org.guvnor.common.services.backend.metadata.attribute.OtherMetaView;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.service.ActiveOptions;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.UserServicesImpl;
import org.uberfire.backend.server.VFSLockServiceImpl;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExplorerServiceHelperTest {

    private SimpleFileSystemProvider fileSystemProvider;

    @Mock
    private KieProjectService projectService;

    @Mock
    private FolderListingResolver folderListingResolver;

    @Mock
    private IOService ioService;

    @Mock
    private IOService ioServiceConfig;

    @Mock
    private OtherMetaView otherMetaView;

    @Mock
    private VFSLockServiceImpl lockService;

    @Mock
    private MetadataService metadataService;

    @Mock
    private UserServicesImpl userServices;

    @Mock
    private Package pkg;

    @Mock
    private Path srcPath;

    @Mock
    private Path srcTestPath;

    @Mock
    private Path srcResourcesPath;

    @Mock
    private Path testResourcesPath;

    private org.uberfire.java.nio.file.Path path;
    private ExplorerServiceHelper helper;

    private final List<String> tags = new ArrayList<String>() {{
        add( "tag" );
    }};

    @Before
    public void setUp() throws Exception {
        fileSystemProvider = new SimpleFileSystemProvider();

        //Ensure URLs use the default:// scheme
        fileSystemProvider.forceAsDefault();

        path = fileSystemProvider.getPath( this.getClass().getResource( "myfile.file" ).toURI() );

        when( srcPath.toURI() ).thenReturn( path.toUri().toString() );
        when( srcTestPath.toURI() ).thenReturn( path.toUri().toString() );
        when( srcResourcesPath.toURI() ).thenReturn( path.toUri().toString() );
        when( testResourcesPath.toURI() ).thenReturn( path.toUri().toString() );

        when( pkg.getPackageMainSrcPath() ).thenReturn( srcPath );
        when( pkg.getPackageTestSrcPath() ).thenReturn( srcTestPath );
        when( pkg.getPackageMainResourcesPath() ).thenReturn( srcResourcesPath );
        when( pkg.getPackageTestResourcesPath() ).thenReturn( testResourcesPath );

        when( metadataService.getTags( any( Path.class ) ) ).thenReturn( tags );

        when( ioService.newDirectoryStream( any( org.uberfire.java.nio.file.Path.class ),
                                            any( LinkedFilter.class ) ) ).thenReturn( new DirectoryStreamMock() {

            private List<org.uberfire.java.nio.file.Path> items = new ArrayList<org.uberfire.java.nio.file.Path>() {{
                add( path );
            }};

            @Override
            public Iterator<org.uberfire.java.nio.file.Path> iterator() {
                return items.iterator();
            }
        } );

        helper = new ExplorerServiceHelper( projectService,
                                            folderListingResolver,
                                            ioService,
                                            ioServiceConfig,
                                            lockService,
                                            metadataService,
                                            userServices );
    }

    @Test
    public void testGetItemsWithoutTags() {
        final ActiveOptions options = new ActiveOptions();
        final List<FolderItem> fis = helper.getItems( pkg,
                                                      options );

        assertNotNull( fis );
        assertEquals( 4,
                      fis.size() );

        assertEquals( 0,
                      fis.get( 0 ).getTags().size() );
        assertEquals( 0,
                      fis.get( 1 ).getTags().size() );
        assertEquals( 0,
                      fis.get( 2 ).getTags().size() );
        assertEquals( 0,
                      fis.get( 3 ).getTags().size() );
    }

    @Test
    public void testGetItemsWithTags() {
        final ActiveOptions options = new ActiveOptions( Option.SHOW_TAG_FILTER );
        final List<FolderItem> fis = helper.getItems( pkg,
                                                      options );

        assertNotNull( fis );
        assertEquals( 4,
                      fis.size() );

        assertEquals( 1,
                      fis.get( 0 ).getTags().size() );
        assertEquals( 1,
                      fis.get( 1 ).getTags().size() );
        assertEquals( 1,
                      fis.get( 2 ).getTags().size() );
        assertEquals( 1,
                      fis.get( 3 ).getTags().size() );
    }

}