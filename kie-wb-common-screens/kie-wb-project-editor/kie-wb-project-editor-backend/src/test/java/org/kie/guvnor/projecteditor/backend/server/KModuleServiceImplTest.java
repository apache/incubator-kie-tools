/*
 * Copyright 2012 JBoss Inc
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

package org.kie.guvnor.projecteditor.backend.server;

import java.util.List;
import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.kie.commons.io.IOService;
import org.kie.guvnor.project.backend.server.KModuleContentHandler;
import org.kie.guvnor.project.backend.server.POMContentHandler;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.workbench.projecteditor.backend.server.KModuleServiceImpl;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.security.Identity;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class KModuleServiceImplTest {

    private IOService ioService;
    private Paths paths;
    private KModuleServiceImpl serviceImpl;
    private KModuleContentHandler kProjectContentHandler;
    private POMContentHandler POMContentHandler;
    private Event invalidateDMOProjectCache;

    @Before
    public void setUp() throws Exception {
        ioService = mock( IOService.class );
        paths = mock( Paths.class );
        kProjectContentHandler = mock( KModuleContentHandler.class );
        POMContentHandler = mock( POMContentHandler.class );
        invalidateDMOProjectCache = mock( Event.class );

        setUpWrite();
        serviceImpl = new KModuleServiceImpl( ioService,
                                              paths,
                                              mock( MetadataService.class ),
                                              kProjectContentHandler,
                                              mock( Event.class ),
                                              mock( Identity.class ) );
    }

    private void setUpWrite() {
        org.kie.commons.java.nio.file.Path writtenPath = mock( org.kie.commons.java.nio.file.Path.class );
        when(
                ioService.write( any( org.kie.commons.java.nio.file.Path.class ), anyString() )
            ).thenReturn(
                writtenPath
                        );
        Path path = mock( Path.class );
        when(
                paths.convert( writtenPath )
            ).thenReturn(
                path
                        );
    }

    @Test
    public void testSetUpProjectStructure() throws Exception {

        Path pathToProjectRoot = mock( Path.class );
        org.kie.commons.java.nio.file.Path directory = setUpPathToProjectRoot( pathToProjectRoot );

        org.kie.commons.java.nio.file.Path mainJava = mock( org.kie.commons.java.nio.file.Path.class );
        setUpDirectory( directory, "src/main/java", mainJava );
        org.kie.commons.java.nio.file.Path mainResources = mock( org.kie.commons.java.nio.file.Path.class );
        setUpDirectory( directory, "src/main/resources", mainResources );
        org.kie.commons.java.nio.file.Path testJava = mock( org.kie.commons.java.nio.file.Path.class );
        setUpDirectory( directory, "src/test/java", testJava );
        org.kie.commons.java.nio.file.Path testResources = mock( org.kie.commons.java.nio.file.Path.class );
        setUpDirectory( directory, "src/test/resources", testResources );

        org.kie.commons.java.nio.file.Path kmodule = mock( org.kie.commons.java.nio.file.Path.class );
        setUpDirectory( directory, "src/main/resources/META-INF/kmodule.xml", kmodule );

        serviceImpl.setUpKModuleStructure( pathToProjectRoot );

        verify( ioService ).createDirectory( mainJava );
        verify( ioService ).createDirectory( mainResources );
        verify( ioService ).createDirectory( testJava );
        verify( ioService ).createDirectory( testResources );

        verify( ioService ).write( eq( kmodule ), anyString() );
    }

    private org.kie.commons.java.nio.file.Path setUpPathToProjectRoot( Path pathToProjectRoot ) {
        org.kie.commons.java.nio.file.Path nioPath = mock( org.kie.commons.java.nio.file.Path.class );
        when(
                paths.convert( pathToProjectRoot )
            ).thenReturn(
                nioPath
                        );

        return nioPath;
    }

    private void setUpDirectory( org.kie.commons.java.nio.file.Path directory,
                                 String pathAsText,
                                 org.kie.commons.java.nio.file.Path path ) {
        when(
                directory.resolve( pathAsText )
            ).thenReturn(
                path
                        );
    }

    //
//    @Test
//    public void testLoadKProject() throws Exception {
//
//        Path path = messagesEvent( Path.class );
//        when(
//                ioService.readAllString( path )
//            ).thenReturn(
//                "blaaXML"
//                        );
//
//        KModuleModel original = new KModuleModel();
//        when(
//                kProjectEditorContentHandler.toModel( "blaaXML" )
//            ).thenReturn(
//                original
//                        );
//
//        KModuleModel loaded = service.loadKProject( path );
//
//        assertEquals( original, loaded );
//    }
//
//    @Test
//    public void testSaveKPRoject() throws Exception {
//        Path path = messagesEvent( Path.class );
//        KModuleModel kProjectModel = new KModuleModel();
//
//        when(
//                kProjectEditorContentHandler.toString( kProjectModel )
//            ).thenReturn(
//                "Here I am, tadaa!"
//                        );
//
//        service.saveKProject( path, kProjectModel );
//
//        verify( ioService ).write( path, "Here I am, tadaa!" );
//    }
//
//    @Test
//    public void testLoadGav() throws Exception {
//
//        Path path = messagesEvent( Path.class );
//        when(
//                ioService.readAllString( path )
//            ).thenReturn(
//                "someXML"
//                        );
//
//        GroupArtifactVersionModel original = new GroupArtifactVersionModel();
//        when(
//                groupArtifactVersionModelContentHandler.toModel( "someXML" )
//            ).thenReturn(
//                original
//                        );
//
//        GroupArtifactVersionModel loaded = service.loadGav( path );
//
//        assertEquals( original, loaded );
//    }
//
//    @Test
//    public void testSaveGAV() throws Exception {
//        Path vfsPath = mock(Path.class);
//        org.kie.commons.java.nio.file.Path nioPath = mock(org.kie.commons.java.nio.file.Path.class);
//        POM gavModel = new POM();
//
//        when(paths.convert(vfsPath)).thenReturn(nioPath);
//
//        when(
//                POMContentHandler.toString(gavModel)
//        ).thenReturn(
//                "Howdy!"
//        );
//
//        service.savePOM(vfsPath, gavModel);
//
//        verify(ioService).write(nioPath, "Howdy!");
//        verify(invalidateDMOProjectCache).fire(any());
//    }

    //
//    @Test
//    public void testCheckIfKProjectExists() throws Exception {
//        Path path = PathFactory.newPath( "file://project/pom.xml" );
//
//        when(
//                ioService.exists( argThat( new PathMatcher( "file://project/src/main/resources/META-INF/kproject.xml" ) ) )
//            ).thenReturn(
//                true
//                        );
//
//        Path result = service.pathToRelatedKProjectFileIfAny( path );
//        assertNotNull( result );
//        assertEquals( "file://project/src/main/resources/META-INF/kproject.xml", result.toURI() );
//    }
//
//    @Test
//    public void testCheckIfKProjectExistsWhenItDoesNot() throws Exception {
//        Path path = PathFactory.newPath( "file://secondproject/pom.xml" );
//
//        when(
//                ioService.exists( argThat( new PathMatcher( "file://secondproject/src/main/resources/META-INF/kproject.xml" ) ) )
//            ).thenReturn(
//                false
//                        );
//
//        assertNull( service.pathToRelatedKProjectFileIfAny( path ) );
//    }
//
    private void assertContains( String uri,
                                 List<org.kie.commons.java.nio.file.Path> allValues ) {
        boolean contains = false;
        for ( org.kie.commons.java.nio.file.Path path : allValues ) {
            if ( uri.equals( path.toUri() ) ) {
                contains = true;
                break;
            }
        }

        assertTrue( "Values should contain " + uri, contains );
    }
//
//    class PathMatcher extends ArgumentMatcher<Path> {
//
//        private final String uri;
//
//        PathMatcher( String uri ) {
//            this.uri = uri;
//        }
//
//        public boolean matches( Object path ) {
//            return ( (Path) path ).toURI().matches( uri );
//        }
//    }
}
