package org.uberfire.backend.server.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.backend.group.Group;
import org.uberfire.backend.group.GroupService;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigItem;
import org.uberfire.backend.server.config.ConfigType;
import org.uberfire.backend.server.config.ConfigurationFactory;
import org.uberfire.backend.server.config.ConfigurationService;

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
                registeredGroups.put( group.getName(), group );
            }
        }
    }

    @Override
    public Group getGroup( String name ) {
        return registeredGroups.get( name );
    }

    @Override
    public Collection<Group> getGroups() {
        return Collections.unmodifiableCollection( registeredGroups.values() );
    }

    @Override
    public Group createGroup( String name,
                              String owner ) {
        ConfigGroup groupConfig = configurationFactory.newConfigGroup( ConfigType.GROUP, name, "" );
        groupConfig.addConfigItem( configurationFactory.newConfigItem( "owner", owner ) );
        groupConfig.addConfigItem( configurationFactory.newConfigItem( "security:roles", new ArrayList<String>() ) );
        configurationService.addConfiguration( groupConfig );
        return groupFactory.newGroup( groupConfig );
    }

    @Override
    public Group createGroup( String name,
                              String owner,
                              Collection<Repository> repositories ) {
        ConfigGroup groupConfig = configurationFactory.newConfigGroup( ConfigType.GROUP, name, "" );
        groupConfig.addConfigItem( configurationFactory.newConfigItem( "owner", owner ) );
        groupConfig.addConfigItem( configurationFactory.newConfigItem( "security:roles", new ArrayList<String>() ) );
        List<String> repositoryList = new ArrayList<String>();
        for ( Repository repo : repositories ) {
            repositoryList.add( repo.getAlias() );
        }
        groupConfig.addConfigItem( configurationFactory.newConfigItem( "repositories", repositoryList ) );
        configurationService.addConfiguration( groupConfig );
        return groupFactory.newGroup( groupConfig );
    }

    @Override
    public void addRepository( Group group,
                               Repository repository ) {
        ConfigGroup thisGroupConfig = findGroupConfig( group.getName() );

        if ( thisGroupConfig != null ) {
            ConfigItem<List> repositories = thisGroupConfig.getConfigItem( "repositories" );
            repositories.getValue().add( repository.getAlias() );

            configurationService.addConfiguration( thisGroupConfig );

            Group updatedGroup = groupFactory.newGroup( thisGroupConfig );
            registeredGroups.put( updatedGroup.getName(), updatedGroup );
        } else {
            throw new IllegalArgumentException( "Group " + group.getName() + " not found" );
        }
    }

    @Override
    public void removeRepository( Group group,
                                  Repository repository ) {
        ConfigGroup thisGroupConfig = findGroupConfig( group.getName() );

        if ( thisGroupConfig != null ) {
            ConfigItem<List> repositories = thisGroupConfig.getConfigItem( "repositories" );
            repositories.getValue().remove( repository.getAlias() );

            configurationService.addConfiguration( thisGroupConfig );

            Group updatedGroup = groupFactory.newGroup( thisGroupConfig );
            registeredGroups.put( updatedGroup.getName(), updatedGroup );
        } else {
            throw new IllegalArgumentException( "Group " + group.getName() + " not found" );
        }
    }

    @Override
    public void addRole( Group group,
                         String role ) {
        ConfigGroup thisGroupConfig = findGroupConfig( group.getName() );

        if ( thisGroupConfig != null ) {
            ConfigItem<List> roles = thisGroupConfig.getConfigItem( "security:roles" );
            roles.getValue().add( role );

            configurationService.addConfiguration( thisGroupConfig );

            Group updatedGroup = groupFactory.newGroup( thisGroupConfig );
            registeredGroups.put( updatedGroup.getName(), updatedGroup );
        } else {
            throw new IllegalArgumentException( "Group " + group.getName() + " not found" );
        }
    }

    @Override
    public void removeRole( Group group,
                            String role ) {
        ConfigGroup thisGroupConfig = findGroupConfig( group.getName() );

        if ( thisGroupConfig != null ) {
            ConfigItem<List> roles = thisGroupConfig.getConfigItem( "security:roles" );
            roles.getValue().remove( role );

            configurationService.addConfiguration( thisGroupConfig );

            Group updatedGroup = groupFactory.newGroup( thisGroupConfig );
            registeredGroups.put( updatedGroup.getName(), updatedGroup );
        } else {
            throw new IllegalArgumentException( "Group " + group.getName() + " not found" );
        }
    }

    protected ConfigGroup findGroupConfig( String name ) {

        Collection<ConfigGroup> groups = configurationService.getConfiguration( ConfigType.GROUP );
        if ( groups != null ) {
            for ( ConfigGroup groupConfig : groups ) {
                if ( groupConfig.getName().equals( name ) ) {
                    return groupConfig;
                }
            }
        }
        return null;
    }
}
