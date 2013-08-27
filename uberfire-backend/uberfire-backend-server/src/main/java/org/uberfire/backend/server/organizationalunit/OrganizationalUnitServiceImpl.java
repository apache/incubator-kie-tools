package org.uberfire.backend.server.organizationalunit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.organizationalunit.OrganizationalUnitService;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigItem;
import org.uberfire.backend.server.config.ConfigType;
import org.uberfire.backend.server.config.ConfigurationFactory;
import org.uberfire.backend.server.config.ConfigurationService;

@Service
@ApplicationScoped
public class OrganizationalUnitServiceImpl implements OrganizationalUnitService {

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ConfigurationFactory configurationFactory;

    @Inject
    private OrganizationalUnitFactory organizationalUnitFactory;

    private Map<String, OrganizationalUnit> registeredOrganizationalUnits = new HashMap<String, OrganizationalUnit>();

    @PostConstruct
    public void loadOrganizationalUnits() {
        Collection<ConfigGroup> groups = configurationService.getConfiguration( ConfigType.ORGANIZATIONAL_UNIT );
        if ( groups != null ) {
            for ( ConfigGroup groupConfig : groups ) {
                OrganizationalUnit ou = organizationalUnitFactory.newOrganizationalUnit( groupConfig );
                registeredOrganizationalUnits.put( ou.getName(),
                                                   ou );
            }
        }
    }

    @Override
    public OrganizationalUnit getOrganizationalUnit( final String name ) {
        return registeredOrganizationalUnits.get( name );
    }

    @Override
    public Collection<OrganizationalUnit> getOrganizationalUnits() {
        return new ArrayList<OrganizationalUnit>( registeredOrganizationalUnits.values() );
    }

    @Override
    public OrganizationalUnit createOrganizationalUnit( final String name,
                                                        final String owner ) {
        final ConfigGroup groupConfig = configurationFactory.newConfigGroup( ConfigType.ORGANIZATIONAL_UNIT,
                                                                             name,
                                                                             "" );
        groupConfig.addConfigItem( configurationFactory.newConfigItem( "owner",
                                                                       owner ) );
        groupConfig.addConfigItem( configurationFactory.newConfigItem( "repositories",
                                                                       new ArrayList<String>() ) );
        groupConfig.addConfigItem( configurationFactory.newConfigItem( "security:roles",
                                                                       new ArrayList<String>() ) );
        configurationService.addConfiguration( groupConfig );

        final OrganizationalUnit newOrganizationalUnit = organizationalUnitFactory.newOrganizationalUnit( groupConfig );
        registeredOrganizationalUnits.put( newOrganizationalUnit.getName(),
                                           newOrganizationalUnit );
        return newOrganizationalUnit;
    }

    @Override
    public OrganizationalUnit createOrganizationalUnit( final String name,
                                                        final String owner,
                                                        final Collection<Repository> repositories ) {
        final ConfigGroup groupConfig = configurationFactory.newConfigGroup( ConfigType.ORGANIZATIONAL_UNIT,
                                                                             name,
                                                                             "" );
        groupConfig.addConfigItem( configurationFactory.newConfigItem( "owner",
                                                                       owner ) );
        groupConfig.addConfigItem( configurationFactory.newConfigItem( "repositories",
                                                                       getRepositoryAliases( repositories ) ) );
        groupConfig.addConfigItem( configurationFactory.newConfigItem( "security:roles",
                                                                       new ArrayList<String>() ) );
        configurationService.addConfiguration( groupConfig );

        final OrganizationalUnit newOrganizationalUnit = organizationalUnitFactory.newOrganizationalUnit( groupConfig );
        registeredOrganizationalUnits.put( newOrganizationalUnit.getName(),
                                           newOrganizationalUnit );
        return newOrganizationalUnit;
    }

    private List<String> getRepositoryAliases( final Collection<Repository> repositories ) {
        final List<String> repositoryList = new ArrayList<String>();
        for ( Repository repo : repositories ) {
            repositoryList.add( repo.getAlias() );
        }
        return repositoryList;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void addRepository( final OrganizationalUnit organizationalUnit,
                               final Repository repository ) {
        final ConfigGroup thisGroupConfig = findGroupConfig( organizationalUnit.getName() );

        if ( thisGroupConfig != null ) {
            ConfigItem<List> repositories = thisGroupConfig.getConfigItem( "repositories" );
            repositories.getValue().add( repository.getAlias() );

            configurationService.updateConfiguration( thisGroupConfig );

            final OrganizationalUnit updatedOrganizationalUnit = organizationalUnitFactory.newOrganizationalUnit( thisGroupConfig );
            registeredOrganizationalUnits.put( updatedOrganizationalUnit.getName(),
                                               updatedOrganizationalUnit );
        } else {
            throw new IllegalArgumentException( "OrganizationalUnit " + organizationalUnit.getName() + " not found" );
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void removeRepository( final OrganizationalUnit organizationalUnit,
                                  final Repository repository ) {
        final ConfigGroup thisGroupConfig = findGroupConfig( organizationalUnit.getName() );

        if ( thisGroupConfig != null ) {
            final ConfigItem<List> repositories = thisGroupConfig.getConfigItem( "repositories" );
            repositories.getValue().remove( repository.getAlias() );

            configurationService.updateConfiguration( thisGroupConfig );

            final OrganizationalUnit updatedOrganizationalUnit = organizationalUnitFactory.newOrganizationalUnit( thisGroupConfig );
            registeredOrganizationalUnits.put( updatedOrganizationalUnit.getName(),
                                               updatedOrganizationalUnit );
        } else {
            throw new IllegalArgumentException( "OrganizationalUnit " + organizationalUnit.getName() + " not found" );
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void addRole( final OrganizationalUnit organizationalUnit,
                         final String role ) {
        final ConfigGroup thisGroupConfig = findGroupConfig( organizationalUnit.getName() );

        if ( thisGroupConfig != null ) {
            final ConfigItem<List> roles = thisGroupConfig.getConfigItem( "security:roles" );
            roles.getValue().add( role );

            configurationService.updateConfiguration( thisGroupConfig );

            final OrganizationalUnit updatedOrganizationalUnit = organizationalUnitFactory.newOrganizationalUnit( thisGroupConfig );
            registeredOrganizationalUnits.put( updatedOrganizationalUnit.getName(),
                                               updatedOrganizationalUnit );
        } else {
            throw new IllegalArgumentException( "OrganizationalUnit " + organizationalUnit.getName() + " not found" );
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void removeRole( final OrganizationalUnit organizationalUnit,
                            final String role ) {
        final ConfigGroup thisGroupConfig = findGroupConfig( organizationalUnit.getName() );

        if ( thisGroupConfig != null ) {
            final ConfigItem<List> roles = thisGroupConfig.getConfigItem( "security:roles" );
            roles.getValue().remove( role );

            configurationService.updateConfiguration( thisGroupConfig );

            final OrganizationalUnit updatedOrganizationalUnit = organizationalUnitFactory.newOrganizationalUnit( thisGroupConfig );
            registeredOrganizationalUnits.put( updatedOrganizationalUnit.getName(),
                                               updatedOrganizationalUnit );
        } else {
            throw new IllegalArgumentException( "OrganizationalUnit " + organizationalUnit.getName() + " not found" );
        }
    }

    protected ConfigGroup findGroupConfig( final String name ) {
        final Collection<ConfigGroup> groups = configurationService.getConfiguration( ConfigType.ORGANIZATIONAL_UNIT );
        if ( groups != null ) {
            for ( ConfigGroup groupConfig : groups ) {
                if ( groupConfig.getName().equals( name ) ) {
                    return groupConfig;
                }
            }
        }
        return null;
    }

    @Override
    public void removeOrganizationalUnit( String groupName ) {
        final ConfigGroup thisGroupConfig = findGroupConfig( groupName );

        if ( thisGroupConfig != null ) {
            configurationService.removeConfiguration( thisGroupConfig );
            registeredOrganizationalUnits.remove( groupName );
        }

    }
}
