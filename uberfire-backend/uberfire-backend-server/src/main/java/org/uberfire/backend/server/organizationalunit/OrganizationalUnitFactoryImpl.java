package org.uberfire.backend.server.organizationalunit;

import java.util.List;
import javax.inject.Inject;

import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.organizationalunit.impl.OrganizationalUnitImpl;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigItem;

public class OrganizationalUnitFactoryImpl implements OrganizationalUnitFactory {

    @Inject
    private RepositoryService repositoryService;

    @Override
    public OrganizationalUnit newOrganizationalUnit( ConfigGroup groupConfig ) {

        OrganizationalUnitImpl organizationalUnit = new OrganizationalUnitImpl( groupConfig.getName(),
                                                                                groupConfig.getConfigItemValue( "owner" ) );
        ConfigItem<List<String>> repositories = groupConfig.getConfigItem( "repositories" );
        if ( repositories != null ) {
            for ( String alias : repositories.getValue() ) {
                organizationalUnit.getRepositories().add( repositoryService.getRepository( alias ) );
            }
        }

        //Copy in Security Roles required to access this resource
        ConfigItem<List<String>> roles = groupConfig.getConfigItem( "security:roles" );
        if ( roles != null ) {
            for ( String role : roles.getValue() ) {
                organizationalUnit.getRoles().add( role );
            }
        }
        return organizationalUnit;
    }
}
