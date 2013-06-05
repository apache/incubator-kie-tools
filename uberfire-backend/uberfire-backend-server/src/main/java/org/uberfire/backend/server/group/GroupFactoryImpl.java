package org.uberfire.backend.server.group;

import java.util.List;
import javax.inject.Inject;

import org.uberfire.backend.group.Group;
import org.uberfire.backend.group.impl.GroupImpl;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigItem;

public class GroupFactoryImpl implements GroupFactory {

    @Inject
    private RepositoryService repositoryService;

    @Override
    public Group newGroup( ConfigGroup groupConfig ) {

        GroupImpl group = new GroupImpl( groupConfig.getName(), groupConfig.getConfigItemValue( "owner" ) );
        ConfigItem<List<String>> repositories = groupConfig.getConfigItem( "repositories" );
        if ( repositories != null ) {
            for ( String alias : repositories.getValue() ) {
                group.getRepositories().add( repositoryService.getRepository( alias ) );
            }
        }

        //Copy in Security Roles required to access this resource
        ConfigItem<List<String>> roles = groupConfig.getConfigItem( "security:roles" );
        if ( roles != null ) {
            for ( String role : roles.getValue() ) {
                group.getRoles().add( role );
            }
        }
        return group;
    }
}
