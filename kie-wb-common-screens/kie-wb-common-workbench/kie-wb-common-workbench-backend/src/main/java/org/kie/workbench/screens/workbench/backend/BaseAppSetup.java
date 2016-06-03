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

package org.kie.workbench.screens.workbench.backend;

import java.io.File;
import java.io.FileFilter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;

public abstract class BaseAppSetup {

    protected static final Logger logger = LoggerFactory.getLogger( BaseAppSetup.class );

    protected static final String GLOBAL_SETTINGS = "settings";
    protected static final String GIT_SCHEME = "git";

    protected IOService ioService;

    protected RepositoryService repositoryService;

    protected OrganizationalUnitService organizationalUnitService;

    protected KieProjectService projectService;

    protected ConfigurationService configurationService;

    protected ConfigurationFactory configurationFactory;

    protected BaseAppSetup() {
    }

    public BaseAppSetup( final IOService ioService,
                         final RepositoryService repositoryService,
                         final OrganizationalUnitService organizationalUnitService,
                         final KieProjectService projectService,
                         final ConfigurationService configurationService,
                         final ConfigurationFactory configurationFactory ) {
        this.ioService = ioService;
        this.repositoryService = repositoryService;
        this.organizationalUnitService = organizationalUnitService;
        this.projectService = projectService;
        this.configurationService = configurationService;
        this.configurationFactory = configurationFactory;
    }

    protected Repository createRepository( final String alias,
                                           final String scheme,
                                           final String origin,
                                           final OrganizationalUnit organizationalUnit ) {
        return createRepository( alias, scheme, origin, null, null, organizationalUnit );
    }

    protected Repository createRepository( final String alias,
                                           final String scheme,
                                           final String origin,
                                           final String user,
                                           final String password ) {
        return createRepository( alias, scheme, origin, user, password, null );
    }

    protected Repository createRepository( final String alias,
                                           final String scheme,
                                           final String origin,
                                           final String user,
                                           final String password,
                                           final OrganizationalUnit organizationalUnit ) {
        logger.info( "Cloning Repository '" + alias + "' from '" + origin + "'." );
        Repository repository = repositoryService.getRepository( alias );

        if ( repository == null ) {
            try {
                final RepositoryEnvironmentConfigurations configurations = new RepositoryEnvironmentConfigurations();
                if ( origin != null ) {
                    configurations.setOrigin( origin );
                }
                if (user != null && password != null) {
                    configurations.setUserName( user );
                    configurations.setPassword( password );
                }

                repository = repositoryService.createRepository( scheme,
                                                                 alias,
                                                                 configurations );

                if ( organizationalUnit != null ) {
                    organizationalUnitService.addRepository( organizationalUnit,
                                                             repository );
                }
            } catch ( Exception e ) {
                logger.error( "Failed to clone Repository '" + alias + "'",
                              e );
            }
        } else {
            logger.info( "Repository '" + alias + "' already exists." );
        }

        return repository;
    }

    protected OrganizationalUnit createOU( final Repository repository,
                                           final String ouName,
                                           final String ouOwner ) {
        logger.info( "Creating Organizational Unit '" + ouName + "'." );
        OrganizationalUnit ou = organizationalUnitService.getOrganizationalUnit( ouName );

        if ( ou == null ) {
            List<Repository> repositories = new ArrayList<>();
            if (repository != null) {
                repositories.add( repository );
            }

            organizationalUnitService.createOrganizationalUnit( ouName,
                                                                ouOwner,
                                                                null,
                                                                repositories );
            logger.info( "Created Organizational Unit '" + ouName + "'." );
        } else {
            logger.info( "Organizational Unit '" + ouName + "' already exists." );
        }

        return ou;
    }

    protected void createProject( final Repository repository,
                                  final String group,
                                  final String artifact,
                                  final String version ) {
        GAV gav = new GAV( group, artifact, version );

        try {
            if ( repository != null ) {
                final String projectLocation = repository.getUri() + ioService.getFileSystem( URI.create( repository.getUri() ) ).getSeparator() + artifact;
                if ( !ioService.exists( ioService.get( URI.create( projectLocation ) ) ) ) {
                    projectService.newProject( repository.getBranchRoot( repository.getDefaultBranch() ),
                                               new POM( gav ),
                                               "/" );
                }
            } else {
                logger.error( "Repository was not found (is null), cannot add project" );
            }
        } catch ( Exception e ) {
            logger.error( "Unable to bootstrap project {} in repository {}", gav, repository.getAlias(), e );
        }
    }

    protected void loadExampleRepositories( final String exampleRepositoriesRoot,
                                            final String ouName,
                                            final String ouOwner,
                                            final String scheme ) {
        final File root = new File( exampleRepositoriesRoot );
        if ( !root.isDirectory() ) {
            logger.error( "System Property '" + exampleRepositoriesRoot + "' does not point to a folder." );

        } else {
            OrganizationalUnit organizationalUnit = createOU( null, ouName, ouOwner );

            final FileFilter filter = pathName -> pathName.isDirectory();

            logger.info( "Cloning Example Repositories." );

            for ( File child : root.listFiles( filter ) ) {
                final String repositoryAlias = child.getName();
                final String repositoryOrigin = child.getAbsolutePath();
                createRepository( repositoryAlias, scheme, repositoryOrigin, organizationalUnit );
            }

            logger.info( "Example Repositories cloned." );
        }
    }

    protected void setupConfigurationGroup( ConfigType configType, String configGroupName, ConfigGroup configGroup, ConfigItem... configItemsToSetManually ) {
        List<ConfigGroup> existentConfigGroups = configurationService.getConfiguration( configType );
        boolean settingsDefined = false;

        for ( ConfigGroup existentConfigGroup : existentConfigGroups ) {
            if ( configGroupName.equals( existentConfigGroup.getName() ) ) {
                settingsDefined = true;

                if ( configItemsToSetManually != null ) {
                    for ( ConfigItem configItem : configItemsToSetManually ) {
                        ConfigItem existentConfigItem = existentConfigGroup.getConfigItem( configItem.getName() );
                        if ( existentConfigItem == null ) {
                            existentConfigGroup.addConfigItem( configItem );
                            configurationService.updateConfiguration( existentConfigGroup );
                        } else if ( !existentConfigItem.getValue().equals( configItem.getValue() ) ) {
                            existentConfigItem.setValue( configItem.getValue() );
                            configurationService.updateConfiguration( existentConfigGroup );
                        }
                    }
                }

                break;
            }
        }

        if ( !settingsDefined ) {
            configurationService.addConfiguration( configGroup );
        }
    }
}
