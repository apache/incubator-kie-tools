/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.project.backend;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.kie.workbench.screens.workbench.backend.BaseAppSetup;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;
import org.uberfire.io.IOService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ApplicationScoped
@Startup(StartupType.BOOTSTRAP)
public class WorkbenchAppSetup extends BaseAppSetup {

    public static final String GIT_REPO_NAME = "org.kie.workbench.common.stunner.project.demo.name";
    public static final String GIT_URL = "org.kie.workbench.common.stunner.project.demo.url";
    public static final String GIT_USERNAME = "org.kie.workbench.common.stunner.project.demo.username";
    public static final String GIT_PASS = "org.kie.workbench.common.stunner.project.demo.password";

    private static final String NAME = "jbpm-playground";
    private static final String URL = "https://github.com/guvnorngtestuser1/jbpm-console-ng-playground-kjar.git";
    private final static String USER = "guvnorngtestuser1";
    private final static String PASS = "test1234";

    protected WorkbenchAppSetup() {
    }

    @Inject
    public WorkbenchAppSetup( @Named("ioStrategy") final IOService ioService,
                              final RepositoryService repositoryService,
                              final OrganizationalUnitService organizationalUnitService,
                              final KieProjectService projectService,
                              final ConfigurationService configurationService,
                              final ConfigurationFactory configurationFactory ) {
        super( ioService, repositoryService, organizationalUnitService, projectService, configurationService, configurationFactory );
    }

    @PostConstruct
    public void onStartup() {

        String repoName = getRepoName();
        String repoUrl = getGitUrl();
        String[] credentials = getGitCredentials();

        if ( null != repoName && null != repoUrl && null != credentials ) {

            try {
                Repository jbpmRepo = repositoryService.getRepository( repoName );
                if ( jbpmRepo == null ) {

                    final RepositoryEnvironmentConfigurations configurations = new RepositoryEnvironmentConfigurations();
                    configurations.setOrigin( repoUrl );
                    configurations.setUserName( credentials[0] );
                    configurations.setPassword( credentials[1] );

                    jbpmRepo = repositoryService.createRepository( "git",
                            repoName,
                            configurations );
                }

                // TODO in case groups are not defined
                Collection<OrganizationalUnit> groups = organizationalUnitService.getOrganizationalUnits();
                if ( groups == null || groups.isEmpty() ) {
                    final List<Repository> repositories = new ArrayList<Repository>();
                    repositories.add( jbpmRepo );
//                repositories.add( guvnorRepo );

                    organizationalUnitService.createOrganizationalUnit( "demo",
                            "demo@jbpm.org",
                            null,
                            repositories );
                }

                //Define mandatory properties
                setupConfigurationGroup( ConfigType.GLOBAL,
                        GLOBAL_SETTINGS,
                        getGlobalConfiguration() );
            } catch ( Exception e ) {
                throw new RuntimeException( "Error during stunner's repository initialization.", e );
            }

        }

    }

    private ConfigGroup getGlobalConfiguration() {
        //Global Configurations used by many of Drools Workbench editors
        final ConfigGroup group = configurationFactory.newConfigGroup( ConfigType.GLOBAL,
                GLOBAL_SETTINGS,
                "" );
        group.addConfigItem( configurationFactory.newConfigItem( "drools.dateformat",
                "dd-MMM-yyyy" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "drools.datetimeformat",
                "dd-MMM-yyyy hh:mm:ss" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "drools.defaultlanguage",
                "en" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "drools.defaultcountry",
                "US" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "build.enable-incremental",
                "true" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "rule-modeller-onlyShowDSLStatements",
                "false" ) );
        return group;
    }

    private String getRepoName() {
        String name = System.getProperty( GIT_REPO_NAME );
        if ( isEmpty( name ) ) {
            return NAME;
        }
        return name;
    }

    private String getGitUrl() {
        String url = System.getProperty( GIT_URL );
        if ( isEmpty( url ) ) {
            return URL;
        }
        return url;
    }

    private String[] getGitCredentials() {
        String user = System.getProperty( GIT_USERNAME );
        String pass = System.getProperty( GIT_PASS );
        if ( isEmpty( user ) || isEmpty( pass ) ) {
            return new String[] { USER, PASS };
        }
        return new String[] { user, pass };
    }

    private boolean isEmpty( String s ) {
        return null == s || s.trim().length() == 0;
    }
}

