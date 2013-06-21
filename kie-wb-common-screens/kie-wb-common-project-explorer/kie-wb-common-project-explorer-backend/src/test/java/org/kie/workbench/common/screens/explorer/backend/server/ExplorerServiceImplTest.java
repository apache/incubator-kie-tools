/*
 * Copyright 2013 JBoss Inc
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

public class ExplorerServiceImplTest {

//    private final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();
//    private BeanManager beanManager;
//    private Paths paths;
//
//    @Before
//    public void setUp() throws Exception {
//        //Bootstrap WELD container
//        StartMain startMain = new StartMain( new String[ 0 ] );
//        beanManager = startMain.go().getBeanManager();
//
//        //Instantiate Paths used in tests for Path conversion
//        final Bean pathsBean = (Bean) beanManager.getBeans( Paths.class ).iterator().next();
//        final CreationalContext cc = beanManager.createCreationalContext( pathsBean );
//        paths = (Paths) beanManager.getReference( pathsBean,
//                                                  Paths.class,
//                                                  cc );
//
//        //Ensure URLs use the default:// scheme
//        fs.forceAsDefault();
//    }
//
//    @Test
//    public void testParent() throws URISyntaxException {
//        final URL parentUrl = this.getClass().getResource( "/" );
//        final org.kie.commons.java.nio.file.Path parentNioPath = fs.getPath( parentUrl.toURI() );
//
//        final URL childUrl = this.getClass().getResource( "/ExplorerBackendTestProjectStructureValid" );
//        final org.kie.commons.java.nio.file.Path childNioPath = fs.getPath( childUrl.toURI() );
//        final org.kie.commons.java.nio.file.Path childParentNioPath = childNioPath.getParent();
//
//        assertEquals( parentNioPath,
//                      childParentNioPath );
//    }
//
//    @Test
//    public void testExplorerServiceInstantiation() throws Exception {
//
//        final Bean explorerServiceBean = (Bean) beanManager.getBeans( ExplorerService.class ).iterator().next();
//        final CreationalContext cc = beanManager.createCreationalContext( explorerServiceBean );
//        final ExplorerService explorerService = (ExplorerService) beanManager.getReference( explorerServiceBean,
//                                                                                            ExplorerService.class,
//                                                                                            cc );
//        assertNotNull( explorerService );
//    }
//
//    @Test
//    public void testContentInScopeNonProjectPath() throws Exception {
//
//        final Bean explorerServiceBean = (Bean) beanManager.getBeans( ExplorerService.class ).iterator().next();
//        final CreationalContext cc = beanManager.createCreationalContext( explorerServiceBean );
//        final ExplorerService explorerService = (ExplorerService) beanManager.getReference( explorerServiceBean,
//                                                                                            ExplorerService.class,
//                                                                                            cc );
//
//        final URL testUrl = this.getClass().getResource( "/" );
//        final org.kie.commons.java.nio.file.Path testNioPath = fs.getPath( testUrl.toURI() );
//        final Path testPath = paths.convert( testNioPath );
//
//        //Depending on where this Test runs the actual results are undetermined as the VFS root is in a sub-folder on the hosts actual FS
//        final ExplorerContent result = explorerService.getContentInScope( testPath );
//        assertNotNull( result );
//        assertTrue( result.getItems().size() > 0 );
//        assertTrue( result.getBreadCrumbs().size() > 0 );
//
//        //But there should be a single Project called 'ProjectStructureValid'
//        boolean fail = true;
//        for ( final FolderItem item : result.getItems() ) {
//            if ( item instanceof Project ) {
//                assertEquals( "ExplorerBackendTestProjectStructureValid",
//                              item.getCaption() );
//                if ( fail == false ) {
//                    fail( "There should be only one ProjectItem called 'ProjectStructureValid'" );
//                }
//                fail = false;
//            }
//        }
//    }
//
//    @Test
//    public void testContentInScopeProjectRootPath() throws Exception {
//
//        final Bean explorerServiceBean = (Bean) beanManager.getBeans( ExplorerService.class ).iterator().next();
//        final CreationalContext cc = beanManager.createCreationalContext( explorerServiceBean );
//        final ExplorerService explorerService = (ExplorerService) beanManager.getReference( explorerServiceBean,
//                                                                                            ExplorerService.class,
//                                                                                            cc );
//
//        final URL testUrl = this.getClass().getResource( "/ExplorerBackendTestProjectStructureValid" );
//        final org.kie.commons.java.nio.file.Path testNioPath = fs.getPath( testUrl.toURI() );
//        final Path testPath = paths.convert( testNioPath );
//
//        //Get items in scope
//        final ExplorerContent result = explorerService.getContentInScope( testPath );
//
//        checkProjectRoot( result );
//    }
//
//    @Test
//    //A Path that is not at least as deep as src/main/resources resolves to the Project root
//    public void testContentInScopeProjectSrcPath() throws Exception {
//
//        final Bean explorerServiceBean = (Bean) beanManager.getBeans( ExplorerService.class ).iterator().next();
//        final CreationalContext cc = beanManager.createCreationalContext( explorerServiceBean );
//        final ExplorerService explorerService = (ExplorerService) beanManager.getReference( explorerServiceBean,
//                                                                                            ExplorerService.class,
//                                                                                            cc );
//
//        final URL testUrl = this.getClass().getResource( "/ExplorerBackendTestProjectStructureValid/src" );
//        final org.kie.commons.java.nio.file.Path testNioPath = fs.getPath( testUrl.toURI() );
//        final Path testPath = paths.convert( testNioPath );
//
//        //Get items in scope
//        final ExplorerContent result = explorerService.getContentInScope( testPath );
//
//        checkProjectRoot( result );
//    }
//
//    @Test
//    //A Path that is not at least as deep as src/main/resources resolves to the Project root
//    public void testContentInScopeProjectMainPath() throws Exception {
//
//        final Bean explorerServiceBean = (Bean) beanManager.getBeans( ExplorerService.class ).iterator().next();
//        final CreationalContext cc = beanManager.createCreationalContext( explorerServiceBean );
//        final ExplorerService explorerService = (ExplorerService) beanManager.getReference( explorerServiceBean,
//                                                                                            ExplorerService.class,
//                                                                                            cc );
//
//        final URL testUrl = this.getClass().getResource( "/ExplorerBackendTestProjectStructureValid/src/main" );
//        final org.kie.commons.java.nio.file.Path testNioPath = fs.getPath( testUrl.toURI() );
//        final Path testPath = paths.convert( testNioPath );
//
//        //Get items in scope
//        final ExplorerContent result = explorerService.getContentInScope( testPath );
//
//        checkProjectRoot( result );
//    }
//
//    //Generic assertions for any Path that resolves to a Project root
//    private void checkProjectRoot( final ExplorerContent result ) throws URISyntaxException {
//        assertNotNull( result );
//
//        //Check items count
//        final List<FolderItem> items = result.getItems();
//        assertEquals( 3,
//                      items.size() );
//
//        //Check items' type
//        assertContainsFileItem( items,
//                                0 );
//        assertContainsFolderItem( items,
//                                  2 );
//        assertContainsParentFolderItem( items,
//                                        1 );
//
//        //Check items' caption
//
//        assertEquals( ItemNames.SOURCE_JAVA,
//                      items.get( 0 ).getCaption() );
//        assertEquals( ItemNames.SOURCE_RESOURCES,
//                      items.get( 1 ).getCaption() );
//        assertEquals( "..",
//                      items.get( 2 ).getCaption() );
//
//        //Check items' Paths
//        assertEquals( makePath( "/ExplorerBackendTestProjectStructureValid/src/main/java" ),
//                      items.get( 0 ).getPath() );
//        assertEquals( makePath( "/ExplorerBackendTestProjectStructureValid/src/main/resources" ),
//                      items.get( 1 ).getPath() );
//        assertEquals( makePath( "/" ),
//                      items.get( 2 ).getPath() );
//
//        //Check breadcrumbs
//        List<BreadCrumb> breadCrumbs = result.getBreadCrumbs();
//        assertNotNull( breadCrumbs );
//
//        //Breadcrumbs include items above the Project root as the VFS used writes to a sub-folder
//        //of the host's actual FS. We therefore only check the tail of the BreadCrumbs
//        int breadCrumbIndex = breadCrumbs.size();
//        assertTrue( breadCrumbIndex > 0 );
//        breadCrumbIndex--;
//        assertEquals( "ExplorerBackendTestProjectStructureValid",
//                      breadCrumbs.get( breadCrumbIndex ).getCaption() );
//        assertEquals( makePath( "/ExplorerBackendTestProjectStructureValid" ),
//                      breadCrumbs.get( breadCrumbIndex ).getPath() );
//    }
//
//    @Test
//    public void testContentInScopeProjectResourcesPath() throws Exception {
//
//        final Bean explorerServiceBean = (Bean) beanManager.getBeans( ExplorerService.class ).iterator().next();
//        final CreationalContext cc = beanManager.createCreationalContext( explorerServiceBean );
//        final ExplorerService explorerService = (ExplorerService) beanManager.getReference( explorerServiceBean,
//                                                                                            ExplorerService.class,
//                                                                                            cc );
//
//        final URL testUrl = this.getClass().getResource( "/ExplorerBackendTestProjectStructureValid/src/main/resources" );
//        final org.kie.commons.java.nio.file.Path testNioPath = fs.getPath( testUrl.toURI() );
//        final Path testPath = paths.convert( testNioPath );
//
//        //Get items in scope
//        final ExplorerContent result = explorerService.getContentInScope( testPath );
//        assertNotNull( result );
//
//        //Check items count
//        final List<FolderItem> items = result.getItems();
//        assertEquals( 3,
//                      items.size() );
//
//        //Check items' type
//        assertContainsFileItem( items,
//                                1 );
//        assertContainsPackageItem( items,
//                                   1 );
//        assertContainsParentPackageItem( items,
//                                         1 );
//
//        //Check items' caption
//        assertContainsCaption( "rule1.drl",
//                               items );
//        assertContainsCaption( "org",
//                               items );
//        assertContainsCaption( "..",
//                               items );
//
//        //Check items' Paths
//        assertContainsPath( makePath( "/ExplorerBackendTestProjectStructureValid/src/main/resources/rule1.drl" ),
//                            items );
//        assertContainsPath( makePath( "/ExplorerBackendTestProjectStructureValid/src/main/resources/org" ),
//                            items );
//        assertContainsPathUri( makePath( "/ExplorerBackendTestProjectStructureValid/src/main" ).toURI(),
//                               items );
//
//        //Check breadcrumbs
//        List<BreadCrumb> breadCrumbs = result.getBreadCrumbs();
//        assertNotNull( breadCrumbs );
//
//        //Breadcrumbs include items above the Project root as the VFS used writes to a sub-folder
//        //of the host's actual FS. We therefore only check the tail of the BreadCrumbs
//        int breadCrumbIndex = breadCrumbs.size();
//        assertTrue( breadCrumbIndex > 0 );
//        breadCrumbIndex--;
//        assertEquals( ItemNames.SOURCE_RESOURCES,
//                      breadCrumbs.get( breadCrumbIndex ).getCaption() );
//        assertEquals( makePath( "/ExplorerBackendTestProjectStructureValid/src/main/resources" ),
//                      breadCrumbs.get( breadCrumbIndex ).getPath() );
//        breadCrumbIndex--;
//        assertEquals( "ExplorerBackendTestProjectStructureValid",
//                      breadCrumbs.get( breadCrumbIndex ).getCaption() );
//        assertEquals( makePath( "/ExplorerBackendTestProjectStructureValid" ),
//                      breadCrumbs.get( breadCrumbIndex ).getPath() );
//    }
//
//    private void assertContainsPathUri( String pathUri,
//                                        List<FolderItem> items ) {
//        boolean found = false;
//        for ( FolderItem item : items ) {
//            if ( pathUri.equals( item.getPath().toURI() ) ) {
//                found = true;
//            }
//        }
//        assertTrue( "Find path uri", found );
//    }
//
//    private void assertContainsPath( Path path,
//                                     List<FolderItem> items ) {
//        boolean found = false;
//        for ( FolderItem item : items ) {
//            if ( path.equals( item.getPath() ) ) {
//                found = true;
//            }
//        }
//        assertTrue( "Find path", found );
//    }
//
//    private void assertContainsCaption( String caption,
//                                        List<FolderItem> items ) {
//        boolean found = false;
//        for ( FolderItem item : items ) {
//            if ( caption.equals( item.getCaption() ) ) {
//                found = true;
//            }
//        }
//        assertTrue( "Find caption", found );
//    }
//
//    private void assertContainsParentFolderItem( List<FolderItem> items,
//                                                 int amount ) {
//        int count = 0;
//        for ( FolderItem item : items ) {
//            if ( item instanceof ParentFolderItem ) {
//                count++;
//            }
//        }
//        assertEquals( amount, count );
//    }
//
//    private void assertContainsParentPackageItem( List<FolderItem> items,
//                                                  int amount ) {
//        int count = 0;
//        for ( FolderItem item : items ) {
//            if ( item instanceof ParentPackageItem ) {
//                count++;
//            }
//        }
//        assertEquals( amount, count );
//    }
//
//    private void assertContainsPackageItem( List<FolderItem> items,
//                                            int amount ) {
//        int count = 0;
//        for ( FolderItem item : items ) {
//            if ( item instanceof org.kie.workbench.common.screens.explorer.model.Package ) {
//                count++;
//            }
//        }
//        assertEquals( amount, count );
//    }
//
//    private void assertContainsFolderItem( List<FolderItem> items,
//                                           int amount ) {
//        int count = 0;
//        for ( FolderItem item : items ) {
//            if ( item instanceof FolderItem ) {
//                count++;
//            }
//        }
//        assertEquals( amount, count );
//    }
//
//    private void assertContainsFileItem( List<FolderItem> items,
//                                         int amount ) {
//        int count = 0;
//        for ( FolderItem item : items ) {
//            if ( item instanceof FileItem ) {
//                count++;
//            }
//        }
//        assertEquals( amount, count );
//    }
//
//    @Test
//    public void testContentInScopeProjectSubPackagePath() throws Exception {
//
//        final Bean explorerServiceBean = (Bean) beanManager.getBeans( ExplorerService.class ).iterator().next();
//        final CreationalContext cc = beanManager.createCreationalContext( explorerServiceBean );
//        final ExplorerService explorerService = (ExplorerService) beanManager.getReference( explorerServiceBean,
//                                                                                            ExplorerService.class,
//                                                                                            cc );
//
//        final URL testUrl = this.getClass().getResource( "/ExplorerBackendTestProjectStructureValid/src/main/resources/org/kie/test" );
//        final org.kie.commons.java.nio.file.Path testNioPath = fs.getPath( testUrl.toURI() );
//        final Path testPath = paths.convert( testNioPath );
//
//        //Get items in scope
//        final ExplorerContent result = explorerService.getContentInScope( testPath );
//        assertNotNull( result );
//
//        //Check items count
//        final List<FolderItem> items = result.getItems();
//        assertEquals( 2,
//                      items.size() );
//
//        //Check items' type
//        assertContainsFileItem( items, 1 );
//        assertContainsParentPackageItem( items, 1 );
//
//        //Check items' caption
//        assertEquals( "rule1.drl",
//                      items.get( 0 ).getCaption() );
//        assertEquals( "..",
//                      items.get( 1 ).getCaption() );
//
//        //Check items' Paths
//        assertEquals( makePath( "/ExplorerBackendTestProjectStructureValid/src/main/resources/org/kie/test/rule1.drl" ),
//                      items.get( 0 ).getPath() );
//        assertEquals( makePath( "/ExplorerBackendTestProjectStructureValid/src/main/resources/org/kie" ).toURI(),
//                      items.get( 1 ).getPath().toURI() );
//
//        //Check breadcrumbs
//        List<BreadCrumb> breadCrumbs = result.getBreadCrumbs();
//        assertNotNull( breadCrumbs );
//
//        //Breadcrumbs include items above the Project root as the VFS used writes to a sub-folder
//        //of the host's actual FS. We therefore only check the tail of the BreadCrumbs
//        int breadCrumbIndex = breadCrumbs.size();
//        assertTrue( breadCrumbIndex > 0 );
//        breadCrumbIndex--;
//        assertEquals( "test",
//                      breadCrumbs.get( breadCrumbIndex ).getCaption() );
//        assertEquals( makePath( "/ExplorerBackendTestProjectStructureValid/src/main/resources/org/kie/test" ),
//                      breadCrumbs.get( breadCrumbIndex ).getPath() );
//        breadCrumbIndex--;
//        assertEquals( "kie",
//                      breadCrumbs.get( breadCrumbIndex ).getCaption() );
//        assertEquals( makePath( "/ExplorerBackendTestProjectStructureValid/src/main/resources/org/kie" ),
//                      breadCrumbs.get( breadCrumbIndex ).getPath() );
//        breadCrumbIndex--;
//        assertEquals( "org",
//                      breadCrumbs.get( breadCrumbIndex ).getCaption() );
//        assertEquals( makePath( "/ExplorerBackendTestProjectStructureValid/src/main/resources/org" ),
//                      breadCrumbs.get( breadCrumbIndex ).getPath() );
//        breadCrumbIndex--;
//        assertEquals( ItemNames.SOURCE_RESOURCES,
//                      breadCrumbs.get( breadCrumbIndex ).getCaption() );
//        assertEquals( makePath( "/ExplorerBackendTestProjectStructureValid/src/main/resources" ),
//                      breadCrumbs.get( breadCrumbIndex ).getPath() );
//        breadCrumbIndex--;
//        assertEquals( "ExplorerBackendTestProjectStructureValid",
//                      breadCrumbs.get( breadCrumbIndex ).getCaption() );
//        assertEquals( makePath( "/ExplorerBackendTestProjectStructureValid" ),
//                      breadCrumbs.get( breadCrumbIndex ).getPath() );
//    }
//
//    private Path makePath( final String url ) throws URISyntaxException {
//        final URL testUrl = this.getClass().getResource( url );
//        final org.kie.commons.java.nio.file.Path testNioPath = fs.getPath( testUrl.toURI() );
//        final Path testPath = paths.convert( testNioPath );
//        return testPath;
//    }

}
