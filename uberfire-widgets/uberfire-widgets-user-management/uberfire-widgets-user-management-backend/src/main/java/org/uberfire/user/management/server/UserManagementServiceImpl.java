/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.user.management.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PartitionManager;
import org.picketlink.idm.RelationshipManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.basic.Grant;
import org.picketlink.idm.model.basic.Role;
import org.picketlink.idm.model.basic.User;
import org.picketlink.idm.query.IdentityQuery;
import org.picketlink.idm.query.RelationshipQuery;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.user.management.model.UserInformation;
import org.uberfire.user.management.model.UserManagerCapabilities;
import org.uberfire.user.management.model.UserManagerContent;
import org.uberfire.user.management.service.UserManagementService;

/**
 * Default implementation of UserManagementService that delegates to PicketLink.
 */
@Service
@ApplicationScoped
public class UserManagementServiceImpl implements UserManagementService {

    private PartitionManager partitionManager;

    @Inject
    public UserManagementServiceImpl( final PartitionManager partitionManager ) {
        this.partitionManager = PortablePreconditions.checkNotNull( "partitionManager",
                                                                    partitionManager );
    }

    /**
     * @return
     * @see UserManagementService#loadContent()
     */
    @Override
    public UserManagerContent loadContent() {
        return new UserManagerContent( loadUserInformation(),
                                       loadUserManagerCapabilities() );
    }

    private UserManagerCapabilities loadUserManagerCapabilities() {
        return new UserManagerCapabilities( true,
                                            true,
                                            true,
                                            true );
    }

    private List<UserInformation> loadUserInformation() {
        final IdentityManager identityManager = partitionManager.createIdentityManager();
        final List<UserInformation> userInformation = new ArrayList<UserInformation>();
        final IdentityQuery<User> users = identityManager.createIdentityQuery( User.class );
        for ( User user : users.getResultList() ) {
            userInformation.add( new UserInformation( user.getLoginName(),
                                                      getRoles( user ) ) );
        }

        return userInformation;
    }

    private Set<String> getRoles( final User user ) {
        final RelationshipManager relationshipManager = partitionManager.createRelationshipManager();
        final RelationshipQuery<Grant> grants = relationshipManager.createRelationshipQuery( Grant.class );
        final Set<String> userRoles = new HashSet<String>();
        for ( Grant grant : grants.getResultList() ) {
            if ( grant.getAssignee().equals( user ) ) {
                userRoles.add( grant.getRole().getName() );
            }
        }
        return userRoles;
    }

    /**
     * @param userInformation Basic user information. Cannot be null.
     * @param userPassword User's password. Cannot be null.
     * @see UserManagementService#addUser(UserInformation, String)
     */
    @Override
    public void addUser( final UserInformation userInformation,
                         final String userPassword ) {
        PortablePreconditions.checkNotNull( "userInformation",
                                            userInformation );
        PortablePreconditions.checkNotNull( "userPassword",
                                            userPassword );

        final IdentityManager identityManager = partitionManager.createIdentityManager();
        final RelationshipManager relationshipManager = partitionManager.createRelationshipManager();

        //Create user
        final User user = new User( userInformation.getUserName() );
        //user.setEmail( userInformation.getUserEmail() );
        //user.setFirstName( userInformation.getUserFirstName() );
        //user.setLastName( userInformation.getUserLastName() );

        identityManager.add( user );
        identityManager.updateCredential( user,
                                          new Password( userPassword ) );

        //Add roles to User
        //First retrieve all known roles, so as not to try to create a duplicate
        final Set<String> existingRoleNames = new HashSet<String>();
        final IdentityQuery<Role> rolesQuery = identityManager.createIdentityQuery( Role.class );
        for ( Role role : rolesQuery.getResultList() ) {
            existingRoleNames.add( role.getName() );
        }

        //Then add roles to user
        final Set<String> userRoleNames = userInformation.getUserRoles();
        for ( String userRoleName : userRoleNames ) {
            final Role role = new Role( userRoleName );
            if ( !existingRoleNames.contains( userRoleName ) ) {
                identityManager.add( role );
            }
            relationshipManager.add( new Grant( user,
                                                role ) );
        }
    }

    /**
     * @param userInformation Basic user information. Cannot be null.
     * @see UserManagementService#updateUser(UserInformation)
     */
    @Override
    public void updateUser( final UserInformation userInformation ) {
        PortablePreconditions.checkNotNull( "userInformation",
                                            userInformation );

        final IdentityManager identityManager = partitionManager.createIdentityManager();
        final RelationshipManager relationshipManager = partitionManager.createRelationshipManager();

        final User user = findUser( userInformation.getUserName() );
        if ( user == null ) {
            return;
        }
        //user.setEmail( userInformation.getUserEmail() );
        //user.setFirstName( userInformation.getUserFirstName() );
        //user.setLastName( userInformation.getUserLastName() );

        //Remove existing relationships
        final RelationshipQuery<Grant> grants = relationshipManager.createRelationshipQuery( Grant.class );
        for ( Grant grant : grants.getResultList() ) {
            if ( grant.getAssignee().getId().equals( user.getId() ) ) {
                relationshipManager.remove( grant );
            }
        }

        //Add new relationships
        final Set<String> userRoleNames = userInformation.getUserRoles();
        for ( String userRoleName : userRoleNames ) {
            Role role = findRole( userRoleName );
            if ( role == null ) {
                role = new Role( userRoleName );
                identityManager.add( role );
            }
            relationshipManager.add( new Grant( user,
                                                role ) );
        }
    }

    /**
     * @param userInformation Basic user information. Cannot be null.
     * @param userPassword User's password. Cannot be null.
     * @see UserManagementService#updateUser(UserInformation, String)
     */
    @Override
    public void updateUser( final UserInformation userInformation,
                            final String userPassword ) {
        PortablePreconditions.checkNotNull( "userInformation",
                                            userInformation );
        PortablePreconditions.checkNotNull( "userPassword",
                                            userPassword );

        final IdentityManager identityManager = partitionManager.createIdentityManager();
        final RelationshipManager relationshipManager = partitionManager.createRelationshipManager();

        final User user = findUser( userInformation.getUserName() );
        if ( user == null ) {
            return;
        }
        //user.setEmail( userInformation.getUserEmail() );
        //user.setFirstName( userInformation.getUserFirstName() );
        //user.setLastName( userInformation.getUserLastName() );

        //Update User
        identityManager.updateCredential( user,
                                          new Password( userPassword ) );

        //Remove existing relationships
        final RelationshipQuery<Grant> grants = relationshipManager.createRelationshipQuery( Grant.class );
        for ( Grant grant : grants.getResultList() ) {
            if ( grant.getAssignee().getId().equals( user.getId() ) ) {
                relationshipManager.remove( grant );
            }
        }

        //Add new relationships
        final Set<String> userRoleNames = userInformation.getUserRoles();
        for ( String userRoleName : userRoleNames ) {
            Role role = findRole( userRoleName );
            if ( role == null ) {
                role = new Role( userRoleName );
                identityManager.add( role );
            }
            relationshipManager.add( new Grant( user,
                                                role ) );
        }
    }

    /**
     * @param userInformation Basic user information. Cannot be null.
     * @see UserManagementService#deleteUser(UserInformation)
     */
    @Override
    public void deleteUser( final UserInformation userInformation ) {
        PortablePreconditions.checkNotNull( "userInformation",
                                            userInformation );

        final User user = findUser( userInformation.getUserName() );
        if ( user == null ) {
            return;
        }
        final IdentityManager identityManager = partitionManager.createIdentityManager();
        identityManager.remove( user );
    }

    private User findUser( final String userName ) {
        final IdentityManager identityManager = partitionManager.createIdentityManager();
        final IdentityQuery<User> users = identityManager.createIdentityQuery( User.class );
        for ( User user : users.getResultList() ) {
            if ( user.getLoginName().equals( userName ) ) {
                return user;
            }
        }
        return null;
    }

    private Role findRole( final String roleName ) {
        final IdentityManager identityManager = partitionManager.createIdentityManager();
        final IdentityQuery<Role> rolesQuery = identityManager.createIdentityQuery( Role.class );
        for ( Role role : rolesQuery.getResultList() ) {
            if ( role.getName().equals( roleName ) ) {
                return role;
            }
        }
        return null;
    }

}
