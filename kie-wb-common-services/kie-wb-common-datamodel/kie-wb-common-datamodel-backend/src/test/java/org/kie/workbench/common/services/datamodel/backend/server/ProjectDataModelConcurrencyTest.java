package org.kie.workbench.common.services.datamodel.backend.server;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder;
import org.guvnor.common.services.project.builder.events.InvalidateDMOProjectCacheEvent;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ProjectDataModelConcurrencyTest {

    @Deployment()
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create( JavaArchive.class,
                                  "builder.jar" )
                .addPackage( "org.kie.commons.java.nio.fs.jgit" )
                .addPackage( "org.kie.workbench.common.services.datamodel.backend.server" )
                .addPackage( "org.kie.workbench.common.services.datamodel.backend.server.cache" )
                .addPackage( "org.guvnor.m2repo.backend.server" )
                .addPackage( "org.guvnor.common.services.project.service" )
                .addPackage( "org.guvnor.common.services.project.backend.server" )
                .addPackage( "org.guvnor.common.services.builder" )
                .addPackage( "org.guvnor.common.services.backend.file" )
                .addPackage( "org.guvnor.common.services.backend.metadata" )
                .addPackage( "org.guvnor.common.services.backend.config" )
                .addPackage( "org.guvnor.common.services.backend.rulenames" )
                .addPackage( "org.guvnor.common.services.shared.metadata" )
                .addPackage( "org.guvnor.common.services.shared.config" )
                .addPackage( "org.guvnor.common.services.shared.rulenames" )
                .addPackage( "javax.servlet" )
                .addPackage( "javax.servlet.http" )
                .addPackage( "org.uberfire.commons.cluster" )
                .addPackage( "org.uberfire.backend.server.util" )
                .addPackage( "org.uberfire.backend.server.config" )
                .addPackage( "org.uberfire.backend.organizationalunit" )
                .addPackage( "org.uberfire.backend.repositories" )
                .addPackage( "org.uberfire.backend.repositories.impl.git" )
                .addPackage( "org.uberfire.backend.server.io" )
                .addPackage( "org.uberfire.backend.server.organizationalunit" )
                .addPackage( "org.uberfire.backend.server.repositories" )
                .addPackage( "org.uberfire.backend.server.cluster" )
                .addAsManifestResource( "META-INF/beans.xml",
                                        ArchivePaths.create( "beans.xml" ) );
    }

    @Inject
    private Paths paths;

    @Inject
    private BuildResultsObserver buildResultsObserver;

    @Inject
    private BuildService buildService;

    @Inject
    private ProjectService projectService;

    @Inject
    private DataModelService dataModelService;

    @Inject
    private ResourceChangeIncrementalBuilder buildChangeListener;

    @Inject
    private SessionInfo sessionInfo;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Event<InvalidateDMOProjectCacheEvent> invalidateDMOProjectCacheEvent;

    @Test
    public void testConcurrentResourceUpdates() throws URISyntaxException {
        final URL pomUrl = this.getClass().getResource( "/DataModelBackendTest1/pom.xml" );
        final org.uberfire.java.nio.file.Path nioPomPath = ioService.get( pomUrl.toURI() );
        final Path pomPath = paths.convert( nioPomPath );

        final URL resourceUrl = this.getClass().getResource( "/DataModelBackendTest1/src/main/resources/empty.rdrl" );
        final org.uberfire.java.nio.file.Path nioResourcePath = ioService.get( resourceUrl.toURI() );
        final Path resourcePath = paths.convert( nioResourcePath );

        //Force full build before attempting incremental changes
        final Project project = projectService.resolveProject( resourcePath );
        final BuildResults buildResults = buildService.build( project );
        assertNotNull( buildResults );
        assertEquals( 0,
                      buildResults.getMessages().size() );

        //Perform incremental build
        final int THREADS = 200;
        final Result result = new Result();
        ExecutorService es = Executors.newCachedThreadPool();
        for ( int i = 0; i < THREADS; i++ ) {
            final int operation = ( i % 3 );

            switch ( operation ) {
                case 0:
                    es.execute( new Runnable() {
                        @Override
                        public void run() {
                            try {
                                System.out.println( "[Thread: " + Thread.currentThread().getName() + "] Request to update POM received" );
                                invalidateCaches( project,
                                                  pomPath );
                                buildChangeListener.updateResource( pomPath );
                                System.out.println( "[Thread: " + Thread.currentThread().getName() + "] POM update completed" );
                            } catch ( Throwable e ) {
                                result.setFailed( true );
                                result.setMessage( e.getMessage() );
                                System.out.println( ExceptionUtils.getFullStackTrace( e ) );
                            }
                        }
                    } );
                    break;
                case 1:
                    es.execute( new Runnable() {
                        @Override
                        public void run() {
                            try {
                                System.out.println( "[Thread: " + Thread.currentThread().getName() + "] Request to update Resource received" );
                                invalidateCaches( project,
                                                  resourcePath );
                                buildChangeListener.addResource( resourcePath );
                                System.out.println( "[Thread: " + Thread.currentThread().getName() + "] Resource update completed" );
                            } catch ( Throwable e ) {
                                result.setFailed( true );
                                result.setMessage( e.getMessage() );
                                System.out.println( ExceptionUtils.getFullStackTrace( e ) );
                            }
                        }
                    } );
                    break;
                case 2:
                    es.execute( new Runnable() {
                        @Override
                        public void run() {
                            try {
                                System.out.println( "[Thread: " + Thread.currentThread().getName() + "] Request for DataModel received" );
                                dataModelService.getDataModel( resourcePath );
                                System.out.println( "[Thread: " + Thread.currentThread().getName() + "] DataModel request completed" );
                            } catch ( Throwable e ) {
                                result.setFailed( true );
                                result.setMessage( e.getMessage() );
                                System.out.println( ExceptionUtils.getFullStackTrace( e ) );
                            }
                        }
                    } );

            }
        }

        es.shutdown();
        try {
            es.awaitTermination( 5,
                                 TimeUnit.MINUTES );
        } catch ( InterruptedException e ) {
        }
        if ( result.isFailed() ) {
            fail( result.getMessage() );
        }

    }

    private void invalidateCaches( final Project project,
                                   final Path resourcePath ) {
        invalidateDMOProjectCacheEvent.fire( new InvalidateDMOProjectCacheEvent( sessionInfo,
                                                                                 project,
                                                                                 resourcePath ) );
    }

    private static class Result {

        private boolean failed = false;
        private String message = "";

        public synchronized boolean isFailed() {
            return failed;
        }

        public synchronized void setFailed( boolean failed ) {
            this.failed = failed;
        }

        public synchronized String getMessage() {
            return message;
        }

        public synchronized void setMessage( String message ) {
            this.message = message;
        }
    }

}
