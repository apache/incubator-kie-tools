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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PartitionManager;
import org.picketlink.idm.RelationshipManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.Partition;
import org.picketlink.idm.model.basic.Grant;
import org.picketlink.idm.model.basic.Realm;
import org.picketlink.idm.model.basic.Role;
import org.picketlink.idm.model.basic.User;
import org.picketlink.idm.query.IdentityQuery;
import org.picketlink.idm.query.RelationshipQuery;
import org.uberfire.user.management.model.UserInformation;
import org.uberfire.user.management.model.UserManagerContent;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class UserManagementServiceImplTest {

    //Mock test Partition data
    final Partition partition = new Realm( "test" ) {
        @Override
        public String getId() {
            return "mock-id";
        }
    };

    //Mock test User data
    final User user1 = new User( "manstis" ) {
        @Override
        public String getId() {
            return "mock-id";
        }

        @Override
        public Partition getPartition() {
            return partition;
        }
    };
    final User user2 = new User( "jfeurth" ) {
        @Override
        public String getId() {
            return "mock-id";
        }

        @Override
        public Partition getPartition() {
            return partition;
        }
    };
    final List<User> users = new ArrayList<User>() {{
        add( user1 );
    }};

    //Mock test Roles data
    final Role role1 = new Role( "admin" );
    final Role role2 = new Role( "analyst" );
    final List<Role> roles = new ArrayList<Role>() {{
        add( role1 );
        add( role2 );
    }};

    //Mock test Grants data
    final Grant grant1 = new Grant( user1,
                                    role1 );
    final List<Grant> grants = new ArrayList<Grant>() {{
        add( grant1 );
    }};

    private PartitionManager partitionManager;
    private IdentityManager identityManager;
    private RelationshipManager relationshipManager;

    @Before
    public void setup() {
        partitionManager = mock( PartitionManager.class );
        identityManager = mock( IdentityManager.class );
        relationshipManager = mock( RelationshipManager.class );

        when( partitionManager.createIdentityManager() ).thenReturn( identityManager );
        when( partitionManager.createRelationshipManager() ).thenReturn( relationshipManager );

        final IdentityQuery identityQuery = mock( IdentityQuery.class );
        when( identityManager.createIdentityQuery( User.class ) ).thenReturn( identityQuery );
        when( identityQuery.getResultCount() ).thenReturn( users.size() );
        when( identityQuery.getResultList() ).thenReturn( users );

        final RelationshipQuery relationshipQuery = mock( RelationshipQuery.class );
        when( relationshipManager.createRelationshipQuery( Grant.class ) ).thenReturn( relationshipQuery );
        when( relationshipQuery.getResultCount() ).thenReturn( grants.size() );
        when( relationshipQuery.getResultList() ).thenReturn( grants );

        final IdentityQuery rolesQuery = mock( IdentityQuery.class );
        when( identityManager.createIdentityQuery( Role.class ) ).thenReturn( rolesQuery );
        when( rolesQuery.getResultCount() ).thenReturn( roles.size() );
        when( rolesQuery.getResultList() ).thenReturn( roles );
    }

    @Test
    public void testLoadContent() {
        final UserManagementServiceImpl service = new UserManagementServiceImpl( partitionManager );

        final UserManagerContent content = service.loadContent();

        assertNotNull( content );
        assertNotNull( content.getUserInformation() );
        assertNotNull( content.getCapabilities() );

        assertTrue( content.getCapabilities().isAddUserSupported() );
        assertTrue( content.getCapabilities().isUpdateUserRolesSupported() );
        assertTrue( content.getCapabilities().isUpdateUserPasswordSupported() );
        assertTrue( content.getCapabilities().isDeleteUserSupported() );

        assertEquals( 1,
                      content.getUserInformation().size() );
        assertEquals( user1.getLoginName(),
                      content.getUserInformation().get( 0 ).getUserName() );
        assertEquals( 1,
                      content.getUserInformation().get( 0 ).getUserRoles().size() );
        assertEquals( role1.getName(),
                      content.getUserInformation().get( 0 ).getUserRoles().toArray()[ 0 ] );

    }

    @Test
    public void testAddUser() {
        final UserManagementServiceImpl service = new UserManagementServiceImpl( partitionManager );

        final UserInformation userInformation = new UserInformation( user2.getLoginName(),
                                                                     new HashSet<String>() {{
                                                                         add( role1.getName() );
                                                                         add( role2.getName() );
                                                                     }} );
        service.addUser( userInformation,
                         "password" );

        //You can't mock equals so use a concrete implementation
        final User user = new User() {

            @Override
            public String getLoginName() {
                return user2.getLoginName();
            }

            @Override
            public boolean equals( final Object obj ) {
                if ( !( obj instanceof User ) ) {
                    return false;
                }
                final User other = (User) obj;
                return other.getLoginName().equals( getLoginName() );
            }
        };
        //You can't mock equals so use a concrete implementation
        final Password password = new Password( "password" ) {
            @Override
            public boolean equals( Object obj ) {
                if ( !( obj instanceof Password ) ) {
                    return false;
                }
                final Password other = (Password) obj;
                final String s1 = new String( getValue() );
                final String s2 = new String( other.getValue() );
                return s1.equals( s2 );
            }
        };
        //You can't mock equals so use a concrete implementation
        final Grant grant1 = new Grant() {

            @Override
            public Role getRole() {
                return role1;
            }

            @Override
            public boolean equals( final Object obj ) {
                if ( !( obj instanceof Grant ) ) {
                    return false;
                }
                final Grant other = (Grant) obj;
                return other.getRole().getName().equals( getRole().getName() );
            }
        };
        //You can't mock equals so use a concrete implementation
        final Grant grant2 = new Grant() {

            @Override
            public Role getRole() {
                return role2;
            }

            @Override
            public boolean equals( final Object obj ) {
                if ( !( obj instanceof Grant ) ) {
                    return false;
                }
                final Grant other = (Grant) obj;
                return other.getRole().getName().equals( getRole().getName() );
            }
        };

        verify( identityManager ).add( eq( user ) );
        verify( identityManager ).updateCredential( eq( user ),
                                                    eq( password ) );
        verify( relationshipManager,
                times( 1 ) ).add( eq( grant1 ) );
        verify( relationshipManager,
                times( 1 ) ).add( eq( grant2 ) );
    }

    @Test
    public void testUpdateUser() {
        final UserManagementServiceImpl service = new UserManagementServiceImpl( partitionManager );

        final UserInformation userInformation = new UserInformation( user1.getLoginName(),
                                                                     new HashSet<String>() {{
                                                                         add( role2.getName() );
                                                                     }} );
        service.updateUser( userInformation );

        //You can't mock equals so use a concrete implementation
        final Grant grant1 = new Grant() {

            @Override
            public Role getRole() {
                return role1;
            }

            @Override
            public boolean equals( final Object obj ) {
                if ( !( obj instanceof Grant ) ) {
                    return false;
                }
                final Grant other = (Grant) obj;
                return other.getRole().getName().equals( getRole().getName() );
            }
        };
        //You can't mock equals so use a concrete implementation
        final Grant grant2 = new Grant() {

            @Override
            public Role getRole() {
                return role2;
            }

            @Override
            public boolean equals( final Object obj ) {
                if ( !( obj instanceof Grant ) ) {
                    return false;
                }
                final Grant other = (Grant) obj;
                return other.getRole().getName().equals( getRole().getName() );
            }
        };

        verify( relationshipManager,
                times( 1 ) ).remove( eq( grant1 ) );
        verify( relationshipManager,
                times( 1 ) ).add( eq( grant2 ) );
    }

    @Test
    public void testUpdateUserPassword() {
        final UserManagementServiceImpl service = new UserManagementServiceImpl( partitionManager );

        final UserInformation userInformation = new UserInformation( user1.getLoginName(),
                                                                     new HashSet<String>() {{
                                                                         add( role2.getName() );
                                                                     }} );
        service.updateUser( userInformation,
                            "newPassword" );

        //You can't mock equals so use a concrete implementation
        final User user = new User() {

            @Override
            public String getLoginName() {
                return user1.getLoginName();
            }

            @Override
            public boolean equals( final Object obj ) {
                if ( !( obj instanceof User ) ) {
                    return false;
                }
                final User other = (User) obj;
                return other.getLoginName().equals( getLoginName() );
            }
        };
        //You can't mock equals so use a concrete implementation
        final Password password = new Password( "newPassword" ) {
            @Override
            public boolean equals( Object obj ) {
                if ( !( obj instanceof Password ) ) {
                    return false;
                }
                final Password other = (Password) obj;
                final String s1 = new String( getValue() );
                final String s2 = new String( other.getValue() );
                return s1.equals( s2 );
            }
        };
        //You can't mock equals so use a concrete implementation
        final Grant grant1 = new Grant() {

            @Override
            public Role getRole() {
                return role1;
            }

            @Override
            public boolean equals( final Object obj ) {
                if ( !( obj instanceof Grant ) ) {
                    return false;
                }
                final Grant other = (Grant) obj;
                return other.getRole().getName().equals( getRole().getName() );
            }
        };
        //You can't mock equals so use a concrete implementation
        final Grant grant2 = new Grant() {

            @Override
            public Role getRole() {
                return role2;
            }

            @Override
            public boolean equals( final Object obj ) {
                if ( !( obj instanceof Grant ) ) {
                    return false;
                }
                final Grant other = (Grant) obj;
                return other.getRole().getName().equals( getRole().getName() );
            }
        };

        verify( identityManager ).updateCredential( eq( user ),
                                                    eq( password ) );
        verify( relationshipManager,
                times( 1 ) ).remove( eq( grant1 ) );
        verify( relationshipManager,
                times( 1 ) ).add( eq( grant2 ) );
    }

    @Test
    public void testDeleteUser() {
        final UserManagementServiceImpl service = new UserManagementServiceImpl( partitionManager );

        final UserInformation userInformation = new UserInformation( user1.getLoginName(),
                                                                     Collections.EMPTY_SET );
        service.deleteUser( userInformation );

        //You can't mock equals so use a concrete implementation
        final User user = new User() {

            @Override
            public String getLoginName() {
                return user1.getLoginName();
            }

            @Override
            public boolean equals( final Object obj ) {
                if ( !( obj instanceof User ) ) {
                    return false;
                }
                final User other = (User) obj;
                return getLoginName().equals( other.getLoginName() );
            }
        };

        verify( identityManager ).remove( eq( user ) );
    }

}
