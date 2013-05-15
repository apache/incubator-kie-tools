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

package org.kie.workbench.common.services.builder;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.environment.se.StartMain;
import org.junit.Before;
import org.junit.Test;
import org.kie.commons.java.nio.fs.file.SimpleFileSystemProvider;
import org.kie.workbench.common.services.shared.builder.model.BuildResults;
import org.kie.workbench.common.services.shared.builder.model.IncrementalBuildResults;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigType;
import org.uberfire.backend.server.config.ConfigurationFactory;
import org.uberfire.backend.server.config.ConfigurationService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.widgets.events.ChangeType;
import org.uberfire.client.workbench.widgets.events.ResourceAddedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceBatchChangesEvent;
import org.uberfire.client.workbench.widgets.events.ResourceChange;
import org.uberfire.client.workbench.widgets.events.ResourceDeletedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceUpdatedEvent;

import static org.junit.Assert.*;

public class BuildChangeListenerTest {

    private static final String GLOBAL_SETTINGS = "settings";

    private final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();
    private BeanManager beanManager;

    private Paths paths;
    private ConfigurationService configurationService;
    private ConfigurationFactory configurationFactory;
    private BuildResultsObserver buildResultsObserver;

    @Before
    public void setUp() throws Exception {
        //Bootstrap WELD container
        StartMain startMain = new StartMain( new String[ 0 ] );
        beanManager = startMain.go().getBeanManager();

        //Instantiate Paths used in tests for Path conversion
        final Bean pathsBean = (Bean) beanManager.getBeans( Paths.class ).iterator().next();
        final CreationalContext cc1 = beanManager.createCreationalContext( pathsBean );
        paths = (Paths) beanManager.getReference( pathsBean,
                                                  Paths.class,
                                                  cc1 );

        //Instantiate ConfigurationService
        final Bean configurationServiceBean = (Bean) beanManager.getBeans( ConfigurationService.class ).iterator().next();
        final CreationalContext cc2 = beanManager.createCreationalContext( configurationServiceBean );
        configurationService = (ConfigurationService) beanManager.getReference( configurationServiceBean,
                                                                                ConfigurationService.class,
                                                                                cc2 );

        //Instantiate ConfigurationFactory
        final Bean configurationFactoryBean = (Bean) beanManager.getBeans( ConfigurationFactory.class ).iterator().next();
        final CreationalContext cc3 = beanManager.createCreationalContext( configurationFactoryBean );
        configurationFactory = (ConfigurationFactory) beanManager.getReference( configurationFactoryBean,
                                                                                ConfigurationFactory.class,
                                                                                cc3 );

        //Instantiate BuildResultsObserver
        final Bean buildResultsObserverBean = (Bean) beanManager.getBeans( BuildResultsObserver.class ).iterator().next();
        final CreationalContext cc4 = beanManager.createCreationalContext( buildResultsObserverBean );
        buildResultsObserver = (BuildResultsObserver) beanManager.getReference( buildResultsObserverBean,
                                                                                BuildResultsObserver.class,
                                                                                cc4 );

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
        final Bean buildChangeListenerBean = (Bean) beanManager.getBeans( BuildChangeListener.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( buildChangeListenerBean );
        final BuildChangeListener buildChangeListener = (BuildChangeListener) beanManager.getReference( buildChangeListenerBean,
                                                                                                        BuildChangeListener.class,
                                                                                                        cc );

        final URL resourceUrl = this.getClass().getResource( "/BuildChangeListenerRepo/src/main/resources/add.drl" );
        final org.kie.commons.java.nio.file.Path nioResourcePath = fs.getPath( resourceUrl.toURI() );
        final Path resourcePath = paths.convert( nioResourcePath );

        final ResourceAddedEvent event = new ResourceAddedEvent( resourcePath );
        buildChangeListener.addResource( event );

        final BuildResults buildResults = buildResultsObserver.getBuildResults();
        assertNotNull( buildResults );
        assertEquals( 0,
                      buildResults.getMessages().size() );

        final IncrementalBuildResults incrementalBuildResults = buildResultsObserver.getIncrementalBuildResults();
        assertNotNull( incrementalBuildResults );
        assertEquals( 0,
                      incrementalBuildResults.getAddedMessages().size() );
        assertEquals( 0,
                      incrementalBuildResults.getRemovedMessages().size() );
    }

    @Test
    public void testResourceUpdated() throws Exception {
        final Bean buildChangeListenerBean = (Bean) beanManager.getBeans( BuildChangeListener.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( buildChangeListenerBean );
        final BuildChangeListener buildChangeListener = (BuildChangeListener) beanManager.getReference( buildChangeListenerBean,
                                                                                                        BuildChangeListener.class,
                                                                                                        cc );

        final URL resourceUrl = this.getClass().getResource( "/BuildChangeListenerRepo/src/main/resources/update.drl" );
        final org.kie.commons.java.nio.file.Path nioResourcePath = fs.getPath( resourceUrl.toURI() );
        final Path resourcePath = paths.convert( nioResourcePath );

        final ResourceUpdatedEvent event = new ResourceUpdatedEvent( resourcePath );
        buildChangeListener.updateResource( event );

        final BuildResults buildResults = buildResultsObserver.getBuildResults();
        assertNotNull( buildResults );
        assertEquals( 0,
                      buildResults.getMessages().size() );

        final IncrementalBuildResults incrementalBuildResults = buildResultsObserver.getIncrementalBuildResults();
        assertNotNull( incrementalBuildResults );
        assertEquals( 0,
                      incrementalBuildResults.getAddedMessages().size() );
        assertEquals( 0,
                      incrementalBuildResults.getRemovedMessages().size() );
    }

    @Test
    public void testResourceDeleted() throws Exception {
        final Bean buildChangeListenerBean = (Bean) beanManager.getBeans( BuildChangeListener.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( buildChangeListenerBean );
        final BuildChangeListener buildChangeListener = (BuildChangeListener) beanManager.getReference( buildChangeListenerBean,
                                                                                                        BuildChangeListener.class,
                                                                                                        cc );

        final URL resourceUrl = this.getClass().getResource( "/BuildChangeListenerRepo/src/main/resources/delete.drl" );
        final org.kie.commons.java.nio.file.Path nioResourcePath = fs.getPath( resourceUrl.toURI() );
        final Path resourcePath = paths.convert( nioResourcePath );

        final ResourceDeletedEvent event = new ResourceDeletedEvent( resourcePath );
        buildChangeListener.deleteResource( event );

        final BuildResults buildResults = buildResultsObserver.getBuildResults();
        assertNotNull( buildResults );
        assertEquals( 0,
                      buildResults.getMessages().size() );

        final IncrementalBuildResults incrementalBuildResults = buildResultsObserver.getIncrementalBuildResults();
        assertNotNull( incrementalBuildResults );
        assertEquals( 0,
                      incrementalBuildResults.getAddedMessages().size() );
        assertEquals( 0,
                      incrementalBuildResults.getRemovedMessages().size() );
    }

    @Test
    public void testBatchResourceChanges() throws Exception {
        final Bean buildChangeListenerBean = (Bean) beanManager.getBeans( BuildChangeListener.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( buildChangeListenerBean );
        final BuildChangeListener buildChangeListener = (BuildChangeListener) beanManager.getReference( buildChangeListenerBean,
                                                                                                        BuildChangeListener.class,
                                                                                                        cc );

        final URL resourceUrl1 = this.getClass().getResource( "/BuildChangeListenerRepo/src/main/resources/add.drl" );
        final org.kie.commons.java.nio.file.Path nioResourcePath1 = fs.getPath( resourceUrl1.toURI() );
        final Path resourcePath1 = paths.convert( nioResourcePath1 );

        final URL resourceUrl2 = this.getClass().getResource( "/BuildChangeListenerRepo/src/main/resources/update.drl" );
        final org.kie.commons.java.nio.file.Path nioResourcePath2 = fs.getPath( resourceUrl2.toURI() );
        final Path resourcePath2 = paths.convert( nioResourcePath2 );

        final URL resourceUrl3 = this.getClass().getResource( "/BuildChangeListenerRepo/src/main/resources/delete.drl" );
        final org.kie.commons.java.nio.file.Path nioResourcePath3 = fs.getPath( resourceUrl3.toURI() );
        final Path resourcePath3 = paths.convert( nioResourcePath3 );

        final Set<ResourceChange> batch = new HashSet<ResourceChange>();
        batch.add( new ResourceChange( ChangeType.ADD,
                                       resourcePath1 ) );
        batch.add( new ResourceChange( ChangeType.UPDATE,
                                       resourcePath2 ) );
        batch.add( new ResourceChange( ChangeType.DELETE,
                                       resourcePath3 ) );

        final ResourceBatchChangesEvent event = new ResourceBatchChangesEvent( batch );
        buildChangeListener.batchResourceChanges( event );

        final BuildResults buildResults = buildResultsObserver.getBuildResults();
        assertNotNull( buildResults );
        assertEquals( 0,
                      buildResults.getMessages().size() );

        final IncrementalBuildResults incrementalBuildResults = buildResultsObserver.getIncrementalBuildResults();
        assertNotNull( incrementalBuildResults );
        assertEquals( 0,
                      incrementalBuildResults.getAddedMessages().size() );
        assertEquals( 0,
                      incrementalBuildResults.getRemovedMessages().size() );
    }

}
