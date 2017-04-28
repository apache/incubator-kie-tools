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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
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
import org.kie.workbench.common.screens.explorer.model.FolderItemOperation;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.service.ActiveOptions;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.UserServicesImpl;
import org.uberfire.backend.server.VFSLockServiceImpl;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
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
    private DeleteService deleteService;

    @Mock
    private RenameService renameService;

    @Mock
    private CopyService copyService;

    @Mock
    private Package pkg;

    @Mock
    private Package childPkg;

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

        helper = spy( new ExplorerServiceHelper( projectService,
                                                 folderListingResolver,
                                                 ioService,
                                                 ioServiceConfig,
                                                 lockService,
                                                 metadataService,
                                                 userServices,
                                                 deleteService,
                                                 renameService,
                                                 copyService ) );
    }

    @Test
    public void testBusinessViewWithoutTags() {
        final List<FolderItem> fis = getFolderItems( Option.BUSINESS_CONTENT );
        checkFolderItemsWithoutTags( fis );
    }

    @Test
    public void testTechnicalViewWithoutTags() {
        final List<FolderItem> fis = getFolderItems( Option.TECHNICAL_CONTENT );
        checkFolderItemsWithoutTags( fis );
    }

    protected void checkFolderItemsWithoutTags( List<FolderItem> fis ) {
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
    public void testBusinessViewWithTags() {
        final List<FolderItem> fis = getFolderItems( Option.BUSINESS_CONTENT, Option.SHOW_TAG_FILTER );
        checkFolderItemsWithTags( fis );
    }

    @Test
    public void testTechnicalViewWithags() {
        final List<FolderItem> fis = getFolderItems( Option.TECHNICAL_CONTENT, Option.SHOW_TAG_FILTER );
        checkFolderItemsWithTags( fis );
    }

    protected void checkFolderItemsWithTags( List<FolderItem> fis ) {
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

    protected List<FolderItem> getFolderItems( Option... options ) {
        final ActiveOptions activeOptions = new ActiveOptions( options );
        return helper.getItems( pkg, activeOptions );
    }

    @Test
    public void testDeleteOperationHasRestrictions() {
        givenThatOperationHasRestrictions( FolderItemOperation.DELETE );
        givenThatOperationHasNoRestrictions( FolderItemOperation.RENAME );
        givenThatOperationHasNoRestrictions( FolderItemOperation.COPY );

        List<FolderItemOperation> restrictedOperations = whenRestrictedOperationsAreListed();

        thenOperationIsRestricted( FolderItemOperation.DELETE, restrictedOperations );
        thenThereAreNOperationsRestricted( 1, restrictedOperations );
    }

    @Test
    public void testRenameOperationHasRestrictions() {
        givenThatOperationHasNoRestrictions( FolderItemOperation.DELETE );
        givenThatOperationHasRestrictions( FolderItemOperation.RENAME );
        givenThatOperationHasNoRestrictions( FolderItemOperation.COPY );

        List<FolderItemOperation> restrictedOperations = whenRestrictedOperationsAreListed();

        thenOperationIsRestricted( FolderItemOperation.RENAME, restrictedOperations );
        thenThereAreNOperationsRestricted( 1, restrictedOperations );
    }

    @Test
    public void testCopyOperationHasRestrictions() {
        givenThatOperationHasNoRestrictions( FolderItemOperation.DELETE );
        givenThatOperationHasNoRestrictions( FolderItemOperation.RENAME );
        givenThatOperationHasRestrictions( FolderItemOperation.COPY );

        List<FolderItemOperation> restrictedOperations = whenRestrictedOperationsAreListed();

        thenOperationIsRestricted( FolderItemOperation.COPY, restrictedOperations );
        thenThereAreNOperationsRestricted( 1, restrictedOperations );
    }

    @Test
    public void testDeleteRenameCopyOperationHasRestrictions() {
        givenThatOperationHasRestrictions( FolderItemOperation.DELETE );
        givenThatOperationHasRestrictions( FolderItemOperation.RENAME );
        givenThatOperationHasRestrictions( FolderItemOperation.COPY );

        List<FolderItemOperation> restrictedOperations = whenRestrictedOperationsAreListed();

        thenOperationIsRestricted( FolderItemOperation.DELETE, restrictedOperations );
        thenOperationIsRestricted( FolderItemOperation.RENAME, restrictedOperations );
        thenOperationIsRestricted( FolderItemOperation.COPY, restrictedOperations );
        thenThereAreNOperationsRestricted( 3, restrictedOperations );
    }

    @Test
    public void packageHasNoAssetsTest() {
        doReturn( false ).when( helper ).hasAssets( srcPath );
        doReturn( false ).when( helper ).hasAssets( srcResourcesPath );
        doReturn( false ).when( helper ).hasAssets( srcTestPath );
        doReturn( false ).when( helper ).hasAssets( testResourcesPath );
        doReturn( new HashSet<Package>() {{ add( childPkg ); }} ).when( projectService ).resolvePackages( pkg );
        doReturn( false ).when( helper ).hasAssets( childPkg );

        assertFalse( helper.hasAssets( pkg ) );
    }

    @Test
    public void packageHasAssetsInsideSrcPathTest() {
        doReturn( true ).when( helper ).hasAssets( srcPath );
        doReturn( false ).when( helper ).hasAssets( srcResourcesPath );
        doReturn( false ).when( helper ).hasAssets( srcTestPath );
        doReturn( false ).when( helper ).hasAssets( testResourcesPath );
        doReturn( new HashSet<Package>() {{ add( childPkg ); }} ).when( projectService ).resolvePackages( pkg );
        doReturn( false ).when( helper ).hasAssets( childPkg );

        assertTrue( helper.hasAssets( pkg ) );
    }

    @Test
    public void packageHasAssetsInsideResourcesPathTest() {
        doReturn( false ).when( helper ).hasAssets( srcPath );
        doReturn( true ).when( helper ).hasAssets( srcResourcesPath );
        doReturn( false ).when( helper ).hasAssets( srcTestPath );
        doReturn( false ).when( helper ).hasAssets( testResourcesPath );
        doReturn( new HashSet<Package>() {{ add( childPkg ); }} ).when( projectService ).resolvePackages( pkg );
        doReturn( false ).when( helper ).hasAssets( childPkg );

        assertTrue( helper.hasAssets( pkg ) );
    }

    @Test
    public void packageHasAssetsInsideTestSrcPathTest() {
        doReturn( false ).when( helper ).hasAssets( srcPath );
        doReturn( false ).when( helper ).hasAssets( srcResourcesPath );
        doReturn( true ).when( helper ).hasAssets( srcTestPath );
        doReturn( false ).when( helper ).hasAssets( testResourcesPath );
        doReturn( new HashSet<Package>() {{ add( childPkg ); }} ).when( projectService ).resolvePackages( pkg );
        doReturn( false ).when( helper ).hasAssets( childPkg );

        assertTrue( helper.hasAssets( pkg ) );
    }

    @Test
    public void packageHasAssetsInsideTestResourcesPathTest() {
        doReturn( false ).when( helper ).hasAssets( srcPath );
        doReturn( false ).when( helper ).hasAssets( srcResourcesPath );
        doReturn( false ).when( helper ).hasAssets( srcTestPath );
        doReturn( true ).when( helper ).hasAssets( testResourcesPath );
        doReturn( new HashSet<Package>() {{ add( childPkg ); }} ).when( projectService ).resolvePackages( pkg );
        doReturn( false ).when( helper ).hasAssets( childPkg );

        assertTrue( helper.hasAssets( pkg ) );
    }

    @Test
    public void packageHasAssetsInsideChildPackageTest() {
        doReturn( false ).when( helper ).hasAssets( srcPath );
        doReturn( false ).when( helper ).hasAssets( srcResourcesPath );
        doReturn( false ).when( helper ).hasAssets( srcTestPath );
        doReturn( false ).when( helper ).hasAssets( testResourcesPath );
        doReturn( new HashSet<Package>() {{ add( childPkg ); }} ).when( projectService ).resolvePackages( pkg );
        doReturn( true ).when( helper ).hasAssets( childPkg );

        assertTrue( helper.hasAssets( pkg ) );
    }

    private void givenThatOperationHasRestrictions( FolderItemOperation operation ) {
        mockOperationRestrictions( operation, true );
    }

    private void givenThatOperationHasNoRestrictions( FolderItemOperation operation ) {
        mockOperationRestrictions( operation, false );
    }

    private void mockOperationRestrictions( FolderItemOperation operation, boolean hasRestrictions ) {
        if ( FolderItemOperation.DELETE.equals( operation ) ) {
            mockDeleteRestrictions( hasRestrictions );
        } else if ( FolderItemOperation.RENAME.equals( operation ) ) {
            mockRenameRestrictions( hasRestrictions );
        } else if ( FolderItemOperation.COPY.equals( operation ) ) {
            mockCopyRestrictions( hasRestrictions );
        }
    }

    private List<FolderItemOperation> whenRestrictedOperationsAreListed() {
        return helper.getRestrictedOperations( getPath( "file.txt" ) );
    }

    private void thenOperationIsRestricted( FolderItemOperation operation, List<FolderItemOperation> restrictedOperations ) {
        assertTrue( restrictedOperations.contains( operation ) );
    }

    private void thenThereAreNOperationsRestricted( int n, List<FolderItemOperation> restrictedOperations ) {
        assertEquals( n, restrictedOperations.size() );
    }

    private void mockDeleteRestrictions( boolean hasRestrictions ) {
        when( deleteService.hasRestriction( any( Path.class ) ) ).thenReturn( hasRestrictions );
    }

    private void mockRenameRestrictions( boolean hasRestrictions ) {
        when( renameService.hasRestriction( any( Path.class ) ) ).thenReturn( hasRestrictions );
    }

    private void mockCopyRestrictions( boolean hasRestrictions ) {
        when( copyService.hasRestriction( any( Path.class ) ) ).thenReturn( hasRestrictions );
    }

    private Path getPath( String fileName ) {
        return PathFactory.newPath( fileName, "default://tmp/" + fileName );
    }
}
