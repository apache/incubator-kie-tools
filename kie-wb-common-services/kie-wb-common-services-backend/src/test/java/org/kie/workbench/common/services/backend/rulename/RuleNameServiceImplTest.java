/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.services.backend.rulename;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.enterprise.inject.Instance;

import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.backend.source.BaseSourceService;
import org.kie.workbench.common.services.backend.source.SourceService;
import org.kie.workbench.common.services.backend.source.SourceServices;
import org.kie.workbench.common.services.backend.source.SourceServicesImpl;
import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceAdded;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceChange;
import org.uberfire.workbench.events.ResourceCopied;
import org.uberfire.workbench.events.ResourceCopiedEvent;
import org.uberfire.workbench.events.ResourceDeleted;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamed;
import org.uberfire.workbench.events.ResourceRenamedEvent;
import org.uberfire.workbench.events.ResourceUpdated;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RuleNameServiceImplTest {

    private RuleNameServiceImpl service;
    private SimpleFileSystemProvider simpleFileSystemProvider;
    private ArrayList<SourceService> sourceServicesList = new ArrayList<SourceService>();
    private DTableSourceServiceMock gdstSourceService;
    private Project project;
    private ProjectService projectService;
    private IOService ioService;

    @Before
    public void setUp() throws Exception {
        simpleFileSystemProvider = new SimpleFileSystemProvider();
        simpleFileSystemProvider.forceAsDefault();

        project = mock( Project.class );
        ioService = new MockIOService();

        sourceServicesList.add( new MockSourceService( "rdrl" ) );
        gdstSourceService = new DTableSourceServiceMock( "gdst" );
        sourceServicesList.add( gdstSourceService );

        Instance instance = mock( Instance.class );
        when( instance.iterator() ).thenReturn( sourceServicesList.iterator() );

        SourceServices sourceServices = new SourceServicesImpl( instance );
        projectService = mock( ProjectService.class );
        service = new RuleNameServiceImpl( sourceServices, projectService, ioService );
    }

    @After
    public void tearDown() throws Exception {
        gdstSourceService.source = "";
    }

    @Test
    public void testLoadAll() throws Exception {
        when( projectService.resolveProject( any( Path.class ) ) ).thenReturn( project );

        gdstSourceService.source =
                "package org.test\n"
                        + "rule test\n"
                        + "when\n"
                        + "then\n"
                        + "end\n";

        String uriToResource = this.getClass().getResource( "hello.rdrl" ).toURI().toString();
        URI uriToRootPath = URI.create( uriToResource.substring( 0, uriToResource.length() - "hello.rdrl".length() ) );
        final org.uberfire.backend.vfs.Path rootPath = Paths.convert( simpleFileSystemProvider.getPath( uriToRootPath ) );

        OrganizationalUnit organizationalUnit = mock( OrganizationalUnit.class );
        Repository repository = mock( Repository.class );

        when( project.getRootPath() ).thenReturn( rootPath );

        service.onProjectContextChange( new ProjectContextChangeEvent( organizationalUnit, repository, project ) );

        assertEquals( 1, service.getRuleNames( project, "some.pkg" ).size() );
        assertEquals( 7, service.getRuleNames( project, "org.test" ).size() );
        assertEquals( 2, service.getRuleNames( project, "org.rename" ).size() );
        assertFalse( service.getRuleNames( project, "pkg1" ).contains( "This rule is in a dot file and should be ignored" ) );
        assertEquals( 2, service.getRuleNames( project, "pkg1" ).size() );
        assertEquals( 2, service.getRuleNames( project, "pkg2" ).size() );

        // Context changes back for the second time
        service.onProjectContextChange( new ProjectContextChangeEvent( organizationalUnit, repository, project ) );

        assertEquals( 1, service.getRuleNames( project, "some.pkg" ).size() );
        assertEquals( 7, service.getRuleNames( project, "org.test" ).size() );
        assertEquals( 2, service.getRuleNames( project, "org.rename" ).size() );
        assertEquals( 2, service.getRuleNames( project, "pkg1" ).size() );
        assertEquals( 2, service.getRuleNames( project, "pkg2" ).size() );
    }

    @Test
    public void testNullProjectContext() throws Exception {
        OrganizationalUnit organizationalUnit = mock( OrganizationalUnit.class );
        Repository repository = mock( Repository.class );
        Project project = null;

        service.onProjectContextChange( new ProjectContextChangeEvent( organizationalUnit,
                                                                       repository,
                                                                       project ) );

        assertEquals( 0,
                      service.getRuleNames( project,
                                            "some.pkg" ).size() );
        assertEquals( 0,
                      service.getRuleNames( project,
                                            "org.test" ).size() );
        assertEquals( 0,
                      service.getRuleNames( project,
                                            "org.rename" ).size() );
        assertFalse( service.getRuleNames( project,
                                           "pkg1" ).contains( "This rule is in a dot file and should be ignored" ) );
        assertEquals( 0,
                      service.getRuleNames( project,
                                            "pkg1" ).size() );
        assertEquals( 0,
                      service.getRuleNames( project,
                                            "pkg2" ).size() );
    }

    @Test
    public void testEmpty() throws Exception {
        when( projectService.resolveProject( any( Path.class ) ) ).thenReturn( project );

        assertEquals( 0, service.getRuleNames( project, "some.pgk" ).size() );
    }

    @Test
    public void testEmptyDRLAdded() throws Exception {

        when( projectService.resolveProject( any( Path.class ) ) ).thenReturn( project );

        final Path testPath = Paths.convert( simpleFileSystemProvider.getPath( this.getClass().getResource( "empty.drl" ).toURI() ) );

        fireResourceAddedEvent( testPath );

        assertEquals( 0, service.getRuleNames( project, "some.pkg" ).size() );
    }

    @Test
    public void testDRLAdded() throws Exception {
        when( projectService.resolveProject( any( Path.class ) ) ).thenReturn( project );

        final Path testPath = Paths.convert( simpleFileSystemProvider.getPath( this.getClass().getResource( "test.drl" ).toURI() ) );

        fireResourceAddedEvent( testPath );

        assertEquals( 1, service.getRuleNames( project, "some.pkg" ).size() );
        assertTrue( service.getRuleNames( project, "some.pkg" ).contains( "test" ) );
    }

    @Test
    public void testTwoRDRLAddedDifferentProjects() throws Exception {
        SessionInfo sessionInfo = mock( SessionInfo.class );

        final org.uberfire.backend.vfs.Path rdrl1 = Paths.convert( simpleFileSystemProvider.getPath( this.getClass().getResource( "hello.rdrl" ).toURI() ) );
        final org.uberfire.backend.vfs.Path rdrl2 = Paths.convert( simpleFileSystemProvider.getPath( this.getClass().getResource( "myRule.rdrl" ).toURI() ) );

        Project project1 = mock( Project.class );
        Project project2 = mock( Project.class );

        when( projectService.resolveProject( rdrl1 ) ).thenReturn( project1 );
        when( projectService.resolveProject( rdrl2 ) ).thenReturn( project2 );

        service.processResourceAdd( new ResourceAddedEvent( rdrl1, sessionInfo ) );
        service.processResourceAdd( new ResourceAddedEvent( rdrl2, sessionInfo ) );

        assertEquals( 1, service.getRuleNames( project1, "org.test" ).size() );
        assertTrue( service.getRuleNames( project1, "org.test" ).contains( "hello" ) );

        assertEquals( 1, service.getRuleNames( project2, "org.test" ).size() );
        assertTrue( service.getRuleNames( project2, "org.test" ).contains( "myRule" ) );
    }

    @Test
    public void testTwoDRLAddedDifferentPackages() throws Exception {
        when( projectService.resolveProject( any( Path.class ) ) ).thenReturn( project );

        final Path drlPath = Paths.convert( simpleFileSystemProvider.getPath( this.getClass().getResource( "test.drl" ).toURI() ) );
        final Path rdrlPath = Paths.convert( simpleFileSystemProvider.getPath( this.getClass().getResource( "hello.rdrl" ).toURI() ) );

        fireResourceAddedEvent( drlPath );
        fireResourceAddedEvent( rdrlPath );

        assertEquals( 1, service.getRuleNames( project, "some.pkg" ).size() );
        assertTrue( service.getRuleNames( project, "some.pkg" ).contains( "test" ) );
        assertEquals( 1, service.getRuleNames( project, "org.test" ).size() );
        assertTrue( service.getRuleNames( project, "org.test" ).contains( "hello" ) );
    }

    @Test
    public void testNoSourceServiceForFile() throws Exception {
        when( projectService.resolveProject( any( Path.class ) ) ).thenReturn( project );

        final Path testPath = Paths.convert( simpleFileSystemProvider.getPath( this.getClass().getResource( "test.someunknownformat" ).toURI() ) );

        fireResourceAddedEvent( testPath );

        assertEquals( 0, service.getRuleNames( project, "some.package" ).size() );
    }

    @Test
    public void testDelete() throws Exception {
        when( projectService.resolveProject( any( Path.class ) ) ).thenReturn( project );

        final Path testPath = Paths.convert( simpleFileSystemProvider.getPath( this.getClass().getResource( "hello.rdrl" ).toURI() ) );
        final Path testPath2 = Paths.convert( simpleFileSystemProvider.getPath( this.getClass().getResource( "hello.drl" ).toURI() ) );

        fireResourceAddedEvent( testPath );
        fireResourceAddedEvent( testPath2 );

        assertEquals( 5, service.getRuleNames( project, "org.test" ).size() );
        assertTrue( service.getRuleNames( project, "org.test" ).contains( "hello" ) );

        fireResourceDeletedEvent( testPath );

        assertEquals( 4, service.getRuleNames( project, "org.test" ).size() );
        assertFalse( service.getRuleNames( project, "org.test" ).contains( "hello" ) );
    }

    @Test
    public void testNoSourceServiceForFileDelete() throws Exception {
        when( projectService.resolveProject( any( Path.class ) ) ).thenReturn( project );

        final Path testPath = Paths.convert( simpleFileSystemProvider.getPath( this.getClass().getResource( "test.someunknownformat" ).toURI() ) );

        fireResourceDeletedEvent( testPath );

        assertEquals( 0, service.getRuleNames( project, "some.package" ).size() );
    }

    @Test
    public void testUpdate() throws Exception {
        when( projectService.resolveProject( any( Path.class ) ) ).thenReturn( project );

        final Path testPath = Paths.convert( simpleFileSystemProvider.getPath( this.getClass().getResource( "empty.gdst" ).toURI() ) );

        gdstSourceService.source =
                "package org.test\n"
                        + "rule test\n"
                        + "when\n"
                        + "then\n"
                        + "end\n";

        fireResourceAddedEvent( testPath );

        assertEquals( 1, service.getRuleNames( project, "org.test" ).size() );
        assertTrue( service.getRuleNames( project, "org.test" ).contains( "test" ) );

        gdstSourceService.source =
                "package org.test\n"
                        + "rule newName\n"
                        + "when\n"
                        + "then\n"
                        + "end\n";

        fireResourceUpdatedEvent( testPath );

        assertEquals( 1, service.getRuleNames( project, "org.test" ).size() );
        assertTrue( service.getRuleNames( project, "org.test" ).contains( "newName" ) );
    }

    @Test
    public void testRename() throws Exception {
        when( projectService.resolveProject( any( Path.class ) ) ).thenReturn( project );

        final Path originalTestPath = Paths.convert( simpleFileSystemProvider.getPath( this.getClass().getResource( "pkg1/original.drl" ).toURI() ) );
        final Path newTestPath = Paths.convert( simpleFileSystemProvider.getPath( this.getClass().getResource( "pkg2/new.drl" ).toURI() ) );

        fireResourceAddedEvent( originalTestPath );

        assertEquals( 2, service.getRuleNames( project, "pkg1" ).size() );
        assertTrue( service.getRuleNames( project, "pkg1" ).contains( "Rule 1" ) );
        assertTrue( service.getRuleNames( project, "pkg1" ).contains( "Rule2" ) );

        fireResourceRenameEvent( newTestPath, originalTestPath );

        assertEquals( 2, service.getRuleNames( project, "pkg2" ).size() );
        assertTrue( service.getRuleNames( project, "pkg1" ).isEmpty() );
        assertTrue( service.getRuleNames( project, "pkg2" ).contains( "Rule 1" ) );
        assertTrue( service.getRuleNames( project, "pkg2" ).contains( "Rule2" ) );
    }

    @Test
    public void testCopy() throws Exception {
        when( projectService.resolveProject( any( Path.class ) ) ).thenReturn( project );

        final Path originalTestPath = Paths.convert( simpleFileSystemProvider.getPath( this.getClass().getResource( "pkg1/original.drl" ).toURI() ) );
        final Path newTestPath = Paths.convert( simpleFileSystemProvider.getPath( this.getClass().getResource( "pkg2/new.drl" ).toURI() ) );

        fireResourceAddedEvent( originalTestPath );

        assertEquals( 2, service.getRuleNames( project, "pkg1" ).size() );
        assertTrue( service.getRuleNames( project, "pkg1" ).contains( "Rule 1" ) );
        assertTrue( service.getRuleNames( project, "pkg1" ).contains( "Rule2" ) );

        fireResourceCopyEvent( newTestPath, originalTestPath );

        assertEquals( 2, service.getRuleNames( project, "pkg1" ).size() );
        assertTrue( service.getRuleNames( project, "pkg1" ).contains( "Rule 1" ) );
        assertTrue( service.getRuleNames( project, "pkg1" ).contains( "Rule2" ) );

        assertEquals( 2, service.getRuleNames( project, "pkg2" ).size() );
        assertTrue( service.getRuleNames( project, "pkg2" ).contains( "Rule 1" ) );
        assertTrue( service.getRuleNames( project, "pkg2" ).contains( "Rule2" ) );
    }

    @Test
    public void testBatchChange() throws Exception {

        when( projectService.resolveProject( any( Path.class ) ) ).thenReturn( project );

        final org.uberfire.backend.vfs.Path copyOriginalTestPath = Paths.convert( simpleFileSystemProvider.getPath( this.getClass().getResource( "pkg1/original.drl" ).toURI() ) );

        final org.uberfire.backend.vfs.Path updateTestPath = Paths.convert( simpleFileSystemProvider.getPath( this.getClass().getResource( "empty.gdst" ).toURI() ) );
        final org.uberfire.backend.vfs.Path deleteTestPath = Paths.convert( simpleFileSystemProvider.getPath( this.getClass().getResource( "hello.drl" ).toURI() ) );
        final org.uberfire.backend.vfs.Path addTestPath = Paths.convert( simpleFileSystemProvider.getPath( this.getClass().getResource( "test.drl" ).toURI() ) );

        final org.uberfire.backend.vfs.Path oldRenameTestPath = Paths.convert( simpleFileSystemProvider.getPath( this.getClass().getResource( "oldRename.drl" ).toURI() ) );
        final org.uberfire.backend.vfs.Path newRenameTestPath = Paths.convert( simpleFileSystemProvider.getPath( this.getClass().getResource( "newRename.drl" ).toURI() ) );

        gdstSourceService.source =
                "package org.update\n" +
                        "rule oldName\n" +
                        "  when\n" +
                        "  then\n" +
                        "end";

        // Add a pile of resources
        fireResourceAddedEvent( copyOriginalTestPath );
        fireResourceAddedEvent( updateTestPath );
        fireResourceAddedEvent( deleteTestPath );
        fireResourceAddedEvent( oldRenameTestPath );

        assertEquals( 1, service.getRuleNames( project, "org.update" ).size() );
        assertTrue( service.getRuleNames( project, "org.update" ).contains( "oldName" ) );

        assertTrue( service.getRuleNames( project, "some.pgk" ).isEmpty() );

        assertEquals( 4, service.getRuleNames( project, "org.test" ).size() );

        assertEquals( 2, service.getRuleNames( project, "pkg1" ).size() );

        assertEquals( 1, service.getRuleNames( project, "org.rename" ).size() );
        assertTrue( service.getRuleNames( project, "org.rename" ).contains( "old rename" ) );

        // Modify in every possible way

        gdstSourceService.source =
                "package org.update\n" +
                        "rule newName\n" +
                        "  when\n" +
                        "  then\n" +
                        "end";

        final Path copyNewTestPath = Paths.convert( simpleFileSystemProvider.getPath( this.getClass().getResource( "pkg2/new.drl" ).toURI() ) );

        HashMap<org.uberfire.backend.vfs.Path, Collection<ResourceChange>> batch = new HashMap<org.uberfire.backend.vfs.Path, Collection<ResourceChange>>();

        applyUpdatedResource( updateTestPath, batch );
        applyAddResource( addTestPath, batch );
        applyDeleteResource( deleteTestPath, batch );
        applyCopyResource( copyOriginalTestPath, copyNewTestPath, batch );
        applyRenameResource( oldRenameTestPath, newRenameTestPath, batch );

        fireBatchEvent( batch );

        assertEquals( 1, service.getRuleNames( project, "org.update" ).size() );
        assertFalse( service.getRuleNames( project, "org.update" ).contains( "oldName" ) );
        assertTrue( service.getRuleNames( project, "org.update" ).contains( "newName" ) );

        assertEquals( 1, service.getRuleNames( project, "some.pkg" ).size() );
        assertTrue( service.getRuleNames( project, "some.pkg" ).contains( "test" ) );

        assertTrue( service.getRuleNames( project, "org.test" ).isEmpty() );

        assertEquals( 2, service.getRuleNames( project, "pkg2" ).size() );

        assertEquals( 1, service.getRuleNames( project, "org.rename" ).size() );
        assertTrue( service.getRuleNames( project, "org.rename" ).contains( "new rename" ) );
    }

    private void applyRenameResource( Path renameOriginalTestPath,
                                      Path renameNewTestPath,
                                      HashMap<org.uberfire.backend.vfs.Path, Collection<ResourceChange>> batch ) {
        ArrayList<ResourceChange> resourceChanges = new ArrayList<ResourceChange>();
        ResourceRenamed resourceRenamed = new ResourceRenamed( renameNewTestPath );
        resourceChanges.add( resourceRenamed );
        batch.put( renameOriginalTestPath, resourceChanges );
    }

    private void applyCopyResource( Path copyOriginalTestPath,
                                    Path copyNewTestPath,
                                    HashMap<org.uberfire.backend.vfs.Path, Collection<ResourceChange>> batch ) {
        ArrayList<ResourceChange> resourceChanges = new ArrayList<ResourceChange>();
        ResourceCopied resourceCopied = new ResourceCopied( copyNewTestPath );
        resourceChanges.add( resourceCopied );
        batch.put( copyOriginalTestPath, resourceChanges );
    }

    private void applyDeleteResource( Path path,
                                      HashMap<org.uberfire.backend.vfs.Path, Collection<ResourceChange>> batch ) {
        ArrayList<ResourceChange> resourceChanges = new ArrayList<ResourceChange>();
        resourceChanges.add( new ResourceDeleted() );
        batch.put( path, resourceChanges );
    }

    private void applyAddResource( Path path,
                                   HashMap<org.uberfire.backend.vfs.Path, Collection<ResourceChange>> batch ) {
        ArrayList<ResourceChange> resourceChanges = new ArrayList<ResourceChange>();
        resourceChanges.add( new ResourceAdded() );
        batch.put( path, resourceChanges );
    }

    private void applyUpdatedResource( Path path,
                                       HashMap<org.uberfire.backend.vfs.Path, Collection<ResourceChange>> batch ) {
        ArrayList<ResourceChange> resourceChanges = new ArrayList<ResourceChange>();
        resourceChanges.add( new ResourceUpdated() );
        batch.put( path, resourceChanges );
    }

    private void fireBatchEvent( HashMap<org.uberfire.backend.vfs.Path, Collection<ResourceChange>> batch ) {
        SessionInfo sessionInfo = mock( SessionInfo.class );
        ResourceBatchChangesEvent resourceBatchChangesEvent = new ResourceBatchChangesEvent( batch, sessionInfo );
        service.processBatchChanges( resourceBatchChangesEvent );
    }

    private void fireResourceCopyEvent( Path newPath,
                                        Path oldPath ) {
        service.processResourceCopied( getResourceCopyEvent( newPath, oldPath ) );
    }

    private ResourceCopiedEvent getResourceCopyEvent( Path newPath,
                                                      Path oldPath ) {
        SessionInfo sessionInfo = mock( SessionInfo.class );
        return new ResourceCopiedEvent( oldPath, newPath, sessionInfo );
    }

    private void fireResourceRenameEvent( Path newPath,
                                          Path oldPath ) {
        service.processResourceRenamed( getResourceRenameEvent( newPath, oldPath ) );
    }

    private ResourceRenamedEvent getResourceRenameEvent( Path newPath,
                                                         Path oldPath ) {
        SessionInfo sessionInfo = mock( SessionInfo.class );
        return new ResourceRenamedEvent( oldPath, newPath, sessionInfo );
    }

    private void fireResourceUpdatedEvent( Path path ) {
        service.processResourceUpdate( getResourceUpdateEvent( path ) );
    }

    private ResourceUpdatedEvent getResourceUpdateEvent( Path path ) {
        SessionInfo sessionInfo = mock( SessionInfo.class );
        return new ResourceUpdatedEvent( path, sessionInfo );
    }

    private void fireResourceDeletedEvent( Path path ) {
        service.processResourceDelete( getResourceDeletedEvent( path ) );
    }

    private void fireResourceAddedEvent( Path path ) {
        service.processResourceAdd( getResourceAddedEvent( path ) );
    }

    private ResourceDeletedEvent getResourceDeletedEvent( Path path ) {
        SessionInfo sessionInfo = mock( SessionInfo.class );
        return new ResourceDeletedEvent( path, sessionInfo );
    }

    private ResourceAddedEvent getResourceAddedEvent( Path path ) {
        SessionInfo sessionInfo = mock( SessionInfo.class );
        return new ResourceAddedEvent( path, sessionInfo );
    }

    class DTableSourceServiceMock extends MockSourceService {

        String source;

        public DTableSourceServiceMock( String pattern ) {
            super( pattern );
        }

        @Override
        public String getSource( org.uberfire.java.nio.file.Path path ) {
            return source;
        }

    }

    class MockIOService extends IOServiceMock {

        @Override
        public String readAllString( org.uberfire.java.nio.file.Path path )
                throws IllegalArgumentException, NoSuchFileException, org.uberfire.java.nio.IOException {
            return readFile( path );
        }
    }

    class MockSourceService extends BaseSourceService {

        private String pattern;

        public MockSourceService( String pattern ) {
            this.pattern = pattern;
        }

        @Override
        public String getSource( org.uberfire.java.nio.file.Path path,
                                 Object model ) {
            return null;
        }

        @Override
        public String getSource( org.uberfire.java.nio.file.Path path ) {
            return readFile( path );
        }

        @Override
        public String getPattern() {
            return pattern;
        }
    }

    private String readFile( org.uberfire.java.nio.file.Path path ) {
        String substring = path.toString().substring( path.toString().indexOf( "test-classes" ) + "test-classes".length() );
        InputStream resourceAsStream = getClass().getResourceAsStream( substring );

        StringBuilder drl = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader( new InputStreamReader( resourceAsStream ) );
            String line;
            while ( ( line = reader.readLine() ) != null ) {
                drl.append( line ).append( "\n" );
            }
            resourceAsStream.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        return drl.toString();
    }
}
