/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.builder.core;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.guvnor.test.WeldJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;
import org.uberfire.workbench.events.ResourceAdded;
import org.uberfire.workbench.events.ResourceChange;
import org.uberfire.workbench.events.ResourceUpdated;

import static org.junit.Assert.*;

@RunWith(WeldJUnitRunner.class)
public class ResourceChangeIncrementalBuilderWithoutFullBuildTest extends BuilderTestBase {

    private static final String GLOBAL_SETTINGS = "settings";

    private final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();

    @Inject
    private Paths paths;
    @Inject
    private ConfigurationService configurationService;
    @Inject
    private ConfigurationFactory configurationFactory;
    @Inject
    private BuildResultsObserver buildResultsObserver;
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
    public void testResourceAdded() throws Exception {

        final Bean buildChangeListenerBean = (Bean) beanManager.getBeans( org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( buildChangeListenerBean );
        final org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder buildChangeListener = (org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder) beanManager.getReference( buildChangeListenerBean,
                                                                                                                                                                                                        org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder.class,
                                                                                                                                                                                                        cc );

        final URL resourceUrl = this.getClass().getResource( "/BuildChangeListenerRepo/src/main/resources/add.drl" );
        final org.uberfire.java.nio.file.Path nioResourcePath = fs.getPath( resourceUrl.toURI() );
        final Path resourcePath = paths.convert( nioResourcePath );

        //Perform incremental build (Without a full Build first)
        buildChangeListener.addResource( resourcePath );

        waitForBuildResults(buildResultsObserver);
        final BuildResults buildResults = buildResultsObserver.getBuildResults();
        assertNotNull( buildResults );
        assertEquals( 0,
                      buildResults.getErrorMessages().size() );
        assertEquals( 1,
                      buildResults.getInformationMessages().size() );

        final IncrementalBuildResults incrementalBuildResults = buildResultsObserver.getIncrementalBuildResults();
        assertNull( incrementalBuildResults );
    }

    @Test
    public void testResourceUpdated() throws Exception {
        final Bean buildChangeListenerBean = (Bean) beanManager.getBeans( org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( buildChangeListenerBean );
        final org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder buildChangeListener = (org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder) beanManager.getReference( buildChangeListenerBean,
                                                                                                                                                                                                        org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder.class,
                                                                                                                                                                                                        cc );

        final URL resourceUrl = this.getClass().getResource( "/BuildChangeListenerRepo/src/main/resources/update.drl" );
        final org.uberfire.java.nio.file.Path nioResourcePath = fs.getPath( resourceUrl.toURI() );
        final Path resourcePath = paths.convert( nioResourcePath );

        //Perform incremental build (Without a full Build first)
        buildChangeListener.updateResource( resourcePath );

        waitForBuildResults(buildResultsObserver);
        final BuildResults buildResults = buildResultsObserver.getBuildResults();
        assertNotNull( buildResults );
        assertEquals( 0,
                      buildResults.getErrorMessages().size() );
        assertEquals( 1,
                      buildResults.getInformationMessages().size() );
        final IncrementalBuildResults incrementalBuildResults = buildResultsObserver.getIncrementalBuildResults();
        assertNull( incrementalBuildResults );
    }

    @Test
    public void testNonPackageResourceUpdated() throws Exception {
        //This tests changes to a resource that is neither pom.xml nor kmodule.xml nor within a Package
        final Bean buildChangeListenerBean = (Bean) beanManager.getBeans( org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( buildChangeListenerBean );
        final org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder buildChangeListener = (org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder) beanManager.getReference( buildChangeListenerBean,
                                                                                                                                                                                                        org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder.class,
                                                                                                                                                                                                        cc );

        final URL resourceUrl = this.getClass().getResource( "/BuildChangeListenerRepo/project.imports" );
        final org.uberfire.java.nio.file.Path nioResourcePath = fs.getPath( resourceUrl.toURI() );
        final Path resourcePath = paths.convert( nioResourcePath );

        //Perform incremental build (Without a full Build first)
        buildChangeListener.updateResource( resourcePath );

        final BuildResults buildResults = buildResultsObserver.getBuildResults();
        assertNull( buildResults );

        final IncrementalBuildResults incrementalBuildResults = buildResultsObserver.getIncrementalBuildResults();
        assertNull( incrementalBuildResults );
    }

    @Test
    public void testPomResourceUpdated() throws Exception {
        //This tests changes pom.xml
        final Bean buildChangeListenerBean = (Bean) beanManager.getBeans( org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( buildChangeListenerBean );
        final org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder buildChangeListener = (org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder) beanManager.getReference( buildChangeListenerBean,
                                                                                                                                                                                                        org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder.class,
                                                                                                                                                                                                        cc );

        final URL resourceUrl = this.getClass().getResource( "/BuildChangeListenerRepo/pom.xml" );
        final org.uberfire.java.nio.file.Path nioResourcePath = fs.getPath( resourceUrl.toURI() );
        final Path resourcePath = paths.convert( nioResourcePath );

        //Perform incremental build (Without a full Build first)
        buildChangeListener.updateResource( resourcePath );

        waitForBuildResults(buildResultsObserver);
        final BuildResults buildResults = buildResultsObserver.getBuildResults();
        assertNotNull( buildResults );
        assertEquals( 0,
                      buildResults.getErrorMessages().size() );
        assertEquals( 1,
                      buildResults.getInformationMessages().size() );

        final IncrementalBuildResults incrementalBuildResults = buildResultsObserver.getIncrementalBuildResults();
        assertNull( incrementalBuildResults );
    }

    @Test
    public void testResourceDeleted() throws Exception {
        final Bean buildChangeListenerBean = (Bean) beanManager.getBeans( org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( buildChangeListenerBean );
        final org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder buildChangeListener = (org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder) beanManager.getReference( buildChangeListenerBean,
                                                                                                                                                                                                        org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder.class,
                                                                                                                                                                                                        cc );

        final URL resourceUrl = this.getClass().getResource( "/BuildChangeListenerRepo/src/main/resources/delete.drl" );
        final org.uberfire.java.nio.file.Path nioResourcePath = fs.getPath( resourceUrl.toURI() );
        final Path resourcePath = paths.convert( nioResourcePath );

        //Perform incremental build (Without a full Build first)
        buildChangeListener.deleteResource( resourcePath );

        waitForBuildResults(buildResultsObserver);
        final BuildResults buildResults = buildResultsObserver.getBuildResults();
        assertNotNull( buildResults );
        assertEquals( 0,
                      buildResults.getErrorMessages().size() );
        assertEquals( 1,
                      buildResults.getInformationMessages().size() );

        final IncrementalBuildResults incrementalBuildResults = buildResultsObserver.getIncrementalBuildResults();
        assertNull( incrementalBuildResults );
    }

    @Test
    public void testBatchResourceChanges() throws Exception {
        final Bean buildChangeListenerBean = (Bean) beanManager.getBeans( org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( buildChangeListenerBean );
        final org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder buildChangeListener = (org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder) beanManager.getReference( buildChangeListenerBean,
                                                                                                                                                                                                        org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder.class,
                                                                                                                                                                                                        cc );

        final URL resourceUrl1 = this.getClass().getResource( "/BuildChangeListenerRepo/src/main/resources/add.drl" );
        final org.uberfire.java.nio.file.Path nioResourcePath1 = fs.getPath( resourceUrl1.toURI() );
        final Path resourcePath1 = paths.convert( nioResourcePath1 );

        final URL resourceUrl2 = this.getClass().getResource( "/BuildChangeListenerRepo/src/main/resources/update.drl" );
        final org.uberfire.java.nio.file.Path nioResourcePath2 = fs.getPath( resourceUrl2.toURI() );
        final Path resourcePath2 = paths.convert( nioResourcePath2 );

        final URL resourceUrl3 = this.getClass().getResource( "/BuildChangeListenerRepo/src/main/resources/delete.drl" );
        final org.uberfire.java.nio.file.Path nioResourcePath3 = fs.getPath( resourceUrl3.toURI() );
        final Path resourcePath3 = paths.convert( nioResourcePath3 );

        final Map<Path, Collection<ResourceChange>> batch = new HashMap<Path, Collection<ResourceChange>>();
        batch.put( resourcePath1, new ArrayList<ResourceChange>() {{
            add( new ResourceAdded( "" ) );
        }} );

        batch.put( resourcePath2, new ArrayList<ResourceChange>() {{
            add( new ResourceUpdated( "" ) );
        }} );

        batch.put( resourcePath3, new ArrayList<ResourceChange>() {{
            add( new ResourceUpdated( "" ) );
        }} );

//        batch.add( new ResourceChange( ResourceChangeType.ADD,
//                                       resourcePath1,
//                                       new SessionInfoImpl( "id",
//                                                            new IdentityImpl( "user",
//                                                                              Collections.<Role>emptyList() ) ) ) );
//        batch.add( new ResourceChange( ResourceChangeType.UPDATE,
//                                       resourcePath2,
//                                       new SessionInfoImpl( "id",
//                                                            new IdentityImpl( "user",
//                                                                              Collections.<Role>emptyList() ) ) ) );
//        batch.add( new ResourceChange( ResourceChangeType.DELETE,
//                                       resourcePath3,
//                                       new SessionInfoImpl( "id",
//                                                            new IdentityImpl( "user",
//                                                                              Collections.<Role>emptyList() ) ) ) );

        //Perform incremental build (Without a full Build first)
        buildChangeListener.batchResourceChanges( batch );

        waitForBuildResults(buildResultsObserver);
        final BuildResults buildResults = buildResultsObserver.getBuildResults();
        assertNotNull( buildResults );
        assertEquals( 0,
                      buildResults.getErrorMessages().size() );
        assertEquals( 1,
                      buildResults.getInformationMessages().size() );

        final IncrementalBuildResults incrementalBuildResults = buildResultsObserver.getIncrementalBuildResults();
        assertNull( incrementalBuildResults );
    }

}
