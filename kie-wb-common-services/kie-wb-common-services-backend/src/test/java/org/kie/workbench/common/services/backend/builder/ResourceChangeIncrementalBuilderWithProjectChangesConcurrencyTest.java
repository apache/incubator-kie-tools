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

package org.kie.workbench.common.services.backend.builder;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.guvnor.test.WeldJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.refactoring.backend.server.impact.ResourceReferenceCollector;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.*;

@RunWith(WeldJUnitRunner.class)
public class ResourceChangeIncrementalBuilderWithProjectChangesConcurrencyTest {

    private static final Logger logger = LoggerFactory.getLogger(ResourceChangeIncrementalBuilderWithProjectChangesConcurrencyTest.class);

    private static final String GLOBAL_SETTINGS = "settings";

    private final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();

    @Inject
    private Paths paths;
    @Inject
    private ConfigurationService configurationService;
    @Inject
    private ConfigurationFactory configurationFactory;
    @Inject
    private BuildService buildService;
    @Inject
    private KieProjectService projectService;
    @Inject
    private BeanManager beanManager;

    @Before
    public void setUp() throws Exception {
        //Define mandatory properties
        List<ConfigGroup> globalConfigGroups = configurationService.getConfiguration( ConfigType.GLOBAL );
        boolean globalSettingsDefined = false;
        for ( ConfigGroup globalConfigGroup : globalConfigGroups ) {
            if ( GLOBAL_SETTINGS.equals( globalConfigGroup.getName() ) ) {
                globalSettingsDefined = true;
                break;
            }
        }
        if ( !globalSettingsDefined ) {
            configurationService.addConfiguration( getGlobalConfiguration() );
        }
    }

    private ConfigGroup getGlobalConfiguration() {
        //Global Configurations used by many of Drools Workbench editors
        final ConfigGroup group = configurationFactory.newConfigGroup( ConfigType.GLOBAL,
                                                                       GLOBAL_SETTINGS,
                                                                       "" );
        group.addConfigItem( configurationFactory.newConfigItem( "build.enable-incremental",
                                                                 "true" ) );
        return group;
    }

    @Test
    public void testConcurrentResourceUpdates() throws URISyntaxException {
        final Bean buildChangeListenerBean = (Bean) beanManager.getBeans( org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( buildChangeListenerBean );
        final org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder buildChangeListener = (org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder) beanManager.getReference( buildChangeListenerBean,
                                                                                                                                                                                                        org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder.class,
                                                                                                                                                                                                        cc );

        final URL pomUrl = this.getClass().getResource( "/BuildChangeListenerRepo/pom.xml" );
        final org.uberfire.java.nio.file.Path nioPomPath = fs.getPath( pomUrl.toURI() );
        final Path pomPath = paths.convert( nioPomPath );

        final URL resourceUrl = this.getClass().getResource( "/BuildChangeListenerRepo/src/main/resources/update.drl" );
        final org.uberfire.java.nio.file.Path nioResourcePath = fs.getPath( resourceUrl.toURI() );
        final Path resourcePath = paths.convert( nioResourcePath );

        //Force full build before attempting incremental changes
        final KieProject project = projectService.resolveProject( resourcePath );
        final BuildResults buildResults = buildService.build( project );
        assertNotNull( buildResults );
        assertEquals( 0,
                      buildResults.getErrorMessages().size() );
        assertEquals( 1,
                      buildResults.getInformationMessages().size() );

        //Perform incremental build
        final int THREADS = 200;
        final Result result = new Result();
        ExecutorService es = Executors.newCachedThreadPool();
        for ( int i = 0; i < THREADS; i++ ) {
            final Path p = ( i % 5 == 0 ) ? pomPath : resourcePath;
            es.execute( new Runnable() {
                @Override
                public void run() {
                    try {
                        logger.debug( "Thread " + Thread.currentThread().getName() + " has started for " + p.toURI() );
                        buildChangeListener.updateResource( p );
                        logger.debug( "Thread " + Thread.currentThread().getName() + " has completed " + p.toURI() );
                    } catch ( Throwable e ) {
                        result.setFailed( true );
                        result.setMessage( e.getMessage() );
                        logger.debug( e.getMessage() );
                    }
                }
            } );
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
