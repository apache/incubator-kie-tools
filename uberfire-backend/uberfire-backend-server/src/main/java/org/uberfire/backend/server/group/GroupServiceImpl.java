package org.uberfire.backend.server.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.group.GroupService;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigItem;
import org.uberfire.backend.server.config.ConfigType;
import org.uberfire.backend.server.config.ConfigurationFactory;
import org.uberfire.backend.server.config.ConfigurationService;

@Service
@ApplicationScoped
public class GroupServiceImpl implements GroupService {

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ConfigurationFactory configurationFactory;

    @Inject
    private GroupFactory groupFactory;

    private Map<String, Group> registeredGroups = new HashMap<String, Group>();

    @PostConstruct
    public void loadGroups() {
        Collection<ConfigGroup> groups = configurationService.getConfiguration( ConfigType.GROUP );
        if ( groups != null ) {
            for ( ConfigGroup groupConfig : groups ) {
                Group group = groupFactory.newGroup( groupConfig );
                registeredGroups.put( group.getName(),
                                      group );
            }
        }
    }

    @Override
    public Group getGroup( final String name ) {
        return registeredGroups.get( name );
    }

    @Override
    public Collection<Group> getGroups() {
        return new ArrayList<Group>( registeredGroups.values() );
    }

    @Override
    public Group createGroup( final String name,
                              final String owner ) {
        final ConfigGroup groupConfig = configurationFactory.newConfigGroup( ConfigType.GROUP,
                                                                             name,
                                                                             "" );
        groupConfig.addConfigItem( configurationFactory.newConfigItem( "owner",
                                                                       owner ) );
        groupConfig.addConfigItem( configurationFactory.newConfigItem( "repositories",
                                                                       new ArrayList<String>() ) );
        groupConfig.addConfigItem( configurationFactory.newConfigItem( "security:roles",
                                                                       new ArrayList<String>() ) );
        configurationService.addConfiguration( groupConfig );

        final Group newGroup = groupFactory.newGroup( groupConfig );
        registeredGroups.put( newGroup.getName(),
                              newGroup );
        return newGroup;
    }

    @Override
    public Group createGroup( final String name,
                              final String owner,
                              final Collection<Repository> repositories ) {
        final ConfigGroup groupConfig = configurationFactory.newConfigGroup( ConfigType.GROUP,
                                                                             name,
                                                                             "" );
        groupConfig.addConfigItem( configurationFactory.newConfigItem( "owner",
                                                                       owner ) );
        groupConfig.addConfigItem( configurationFactory.newConfigItem( "repositories",
                                                                       getRepositoryAliases( repositories ) ) );
        groupConfig.addConfigItem( configurationFactory.newConfigItem( "security:roles",
                                                                       new ArrayList<String>() ) );
        configurationService.addConfiguration( groupConfig );

        final Group newGroup = groupFactory.newGroup( groupConfig );
        registeredGroups.put( newGroup.getName(),
                              newGroup );
        return newGroup;
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
    public void addRepository( final Group group,
                               final Repository repository ) {
        final ConfigGroup thisGroupConfig = findGroupConfig( group.getName() );

        if ( thisGroupConfig != null ) {
            ConfigItem<List> repositories = thisGroupConfig.getConfigItem( "repositories" );
            repositories.getValue().add( repository.getAlias() );

            configurationService.updateConfiguration( thisGroupConfig );

            final Group updatedGroup = groupFactory.newGroup( thisGroupConfig );
            registeredGroups.put( updatedGroup.getName(),
                                  updatedGroup );
        } else {
            throw new IllegalArgumentException( "Group " + group.getName() + " not found" );
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void removeRepository( final Group group,
                                  final Repository repository ) {
        final ConfigGroup thisGroupConfig = findGroupConfig( group.getName() );

        if ( thisGroupConfig != null ) {
            final ConfigItem<List> repositories = thisGroupConfig.getConfigItem( "repositories" );
            repositories.getValue().remove( repository.getAlias() );

            configurationService.updateConfiguration( thisGroupConfig );

            final Group updatedGroup = groupFactory.newGroup( thisGroupConfig );
            registeredGroups.put( updatedGroup.getName(),
                                  updatedGroup );
        } else {
            throw new IllegalArgumentException( "Group " + group.getName() + " not found" );
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void addRole( final Group group,
                         final String role ) {
        final ConfigGroup thisGroupConfig = findGroupConfig( group.getName() );

        if ( thisGroupConfig != null ) {
            final ConfigItem<List> roles = thisGroupConfig.getConfigItem( "security:roles" );
            roles.getValue().add( role );

            configurationService.updateConfiguration( thisGroupConfig );

            final Group updatedGroup = groupFactory.newGroup( thisGroupConfig );
            registeredGroups.put( updatedGroup.getName(),
                                  updatedGroup );
        } else {
            throw new IllegalArgumentException( "Group " + group.getName() + " not found" );
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void removeRole( final Group group,
                            final String role ) {
        final ConfigGroup thisGroupConfig = findGroupConfig( group.getName() );

        if ( thisGroupConfig != null ) {
            final ConfigItem<List> roles = thisGroupConfig.getConfigItem( "security:roles" );
            roles.getValue().remove( role );

            configurationService.updateConfiguration( thisGroupConfig );

            final Group updatedGroup = groupFactory.newGroup( thisGroupConfig );
            registeredGroups.put( updatedGroup.getName(),
                                  updatedGroup );
        } else {
            throw new IllegalArgumentException( "Group " + group.getName() + " not found" );
        }
    }

    protected ConfigGroup findGroupConfig( final String name ) {
        final Collection<ConfigGroup> groups = configurationService.getConfiguration( ConfigType.GROUP );
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
	public void removeGroup(String groupName) {
		 final ConfigGroup thisGroupConfig = findGroupConfig( groupName );

        if ( thisGroupConfig != null ) {
        	configurationService.removeConfiguration(thisGroupConfig);
        	registeredGroups.remove(groupName);
        }
		
	}
}
