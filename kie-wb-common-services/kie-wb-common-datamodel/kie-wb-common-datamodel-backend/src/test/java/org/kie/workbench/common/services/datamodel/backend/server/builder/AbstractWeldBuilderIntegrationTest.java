/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.services.datamodel.backend.server.builder;

import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.jboss.weld.environment.se.StartMain;
import org.junit.After;
import org.junit.Before;
import org.kie.workbench.common.services.backend.builder.LRUBuilderCache;
import org.kie.workbench.common.services.datamodel.backend.server.cache.LRUProjectDataModelOracleCache;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.util.List;

public abstract class AbstractWeldBuilderIntegrationTest {
    protected static final String GLOBAL_SETTINGS = "settings";

    protected final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();
    protected BeanManager beanManager;

    protected Paths paths;
    protected ConfigurationService configurationService;
    protected ConfigurationFactory configurationFactory;
    protected BuildService buildService;
    protected KieProjectService projectService;
    protected LRUBuilderCache builderCache;
    protected LRUProjectDataModelOracleCache projectDMOCache;

    private String ufGitDaemonEnabledPropValueOrig;
    private String ufGitSshEnabledPropValueOrig;

    @Before
    public void setUp() throws Exception {
        // disable git and ssh daemons as the test does not require them
        ufGitDaemonEnabledPropValueOrig = System.getProperty("org.uberfire.nio.git.daemon.enabled");
        ufGitSshEnabledPropValueOrig = System.getProperty("org.uberfire.nio.git.ssh.enabled");
        System.setProperty("org.uberfire.nio.git.daemon.enabled", "false");
        System.setProperty("org.uberfire.nio.git.ssh.enabled", "false");

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

        //Instantiate BuildService
        final Bean buildServiceBean = (Bean) beanManager.getBeans( BuildService.class ).iterator().next();
        final CreationalContext cc4 = beanManager.createCreationalContext( buildServiceBean );
        buildService = (BuildService) beanManager.getReference( buildServiceBean,
                                                                BuildService.class,
                                                                cc4 );

        //Instantiate ProjectService
        final Bean projectServiceBean = (Bean) beanManager.getBeans( KieProjectService.class ).iterator().next();
        final CreationalContext cc5 = beanManager.createCreationalContext( projectServiceBean );
        projectService = (KieProjectService) beanManager.getReference( projectServiceBean,
                                                                       KieProjectService.class,
                                                                       cc5 );

        //Instantiate LRUBuilderCache
        final Bean LRUBuilderCacheBean = (Bean) beanManager.getBeans( LRUBuilderCache.class ).iterator().next();
        final CreationalContext cc6 = beanManager.createCreationalContext( LRUBuilderCacheBean );
        builderCache = (LRUBuilderCache) beanManager.getReference( LRUBuilderCacheBean,
                                                                   LRUBuilderCache.class,
                                                                   cc6 );

        //Instantiate LRUProjectDataModelOracleCache
        final Bean LRUProjectDataModelOracleCacheBean = (Bean) beanManager.getBeans( LRUProjectDataModelOracleCache.class ).iterator().next();
        final CreationalContext cc7 = beanManager.createCreationalContext( LRUProjectDataModelOracleCacheBean );
        projectDMOCache = (LRUProjectDataModelOracleCache) beanManager.getReference( LRUProjectDataModelOracleCacheBean,
                                                                                     LRUProjectDataModelOracleCache.class,
                                                                                     cc7 );

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

    @After
    public void tearDown() {
        // we can't set properties with null values, so need to check for that first
        if (ufGitDaemonEnabledPropValueOrig != null) {
            System.setProperty("org.uberfire.nio.git.daemon.enabled", ufGitDaemonEnabledPropValueOrig);
        }
        if (ufGitSshEnabledPropValueOrig != null) {
            System.setProperty("org.uberfire.nio.git.ssh.enabled", ufGitSshEnabledPropValueOrig);
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
}
