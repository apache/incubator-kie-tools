/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.backend.server.security;

import org.jboss.errai.security.shared.api.GroupImpl;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JAASAuthenticationServiceTest {

    private JAASAuthenticationService tested;

    @Before
    public void setup() {
        tested = spy( new JAASAuthenticationService( JAASAuthenticationService.DEFAULT_DOMAIN ) );
    }

    @Test
    public void testNoLogin() throws Exception {
        assertEquals( User.ANONYMOUS, tested.getUser() );
    }

    @Test
    public void testGetAnnonymous() throws Exception {
        assertFalse( tested.isLoggedIn() );
    }

    @Test
    public void testLogin() throws Exception {
        String username = "user1";
        String password = "password1";
        RoleRegistry.get().registerRole( "admin" );
        RoleRegistry.get().registerRole( "role1" );
        Set<Principal> principals = mockPrincipals( "admin", "role1", "group1" );
        Subject subject = new Subject();
        subject.getPrincipals().addAll( principals );
        LoginContext loginContext = mock( LoginContext.class );
        when( loginContext.getSubject() ).thenReturn( subject );
        doReturn( loginContext ).when( tested ).createLoginContext( anyString(), anyString() );

        User user = tested.login( username, password );

        assertNotNull( user );
        assertEquals( username, user.getIdentifier() );
        assertEquals( 2, user.getRoles().size() );
        assertTrue( user.getRoles().contains( new RoleImpl( "admin" ) ) );
        assertTrue( user.getRoles().contains( new RoleImpl( "role1" ) ) );
        assertEquals( 1, user.getGroups().size() );
        assertTrue( user.getGroups().contains( new GroupImpl( "group1" ) ) );
    }

    @Test
    public void testLoginNoPrincipal() throws Exception {
        String username = "user1";
        String password = "password1";
        Subject subject = new Subject();
        LoginContext loginContext = mock( LoginContext.class );
        when( loginContext.getSubject() ).thenReturn( subject );
        doReturn( loginContext ).when( tested ).createLoginContext( anyString(), anyString() );

        User user = tested.login( username, password );

        assertNotNull( user );
        assertEquals( username, user.getIdentifier() );
        assertEquals( 0, user.getRoles().size() );
        assertEquals( 0, user.getGroups().size() );
    }

    @Test
    public void testLoginSubjectGroups() throws Exception {
        String username = "user1";
        String password = "password1";
        RoleRegistry.get().registerRole( "admin" );
        RoleRegistry.get().registerRole( "role1" );
        Set<Principal> principals = mockPrincipals( "admin", "role1", "group1" );
        Group aclGroup = mock( Group.class );
        doReturn( JAASAuthenticationService.DEFAULT_ROLE_PRINCIPLE_NAME ).when( aclGroup ).getName();
        Set<Principal> aclGroups = mockPrincipals( "g1", "g2" );
        Enumeration<? extends Principal> aclGroupsEnum = Collections.enumeration( aclGroups );
        doReturn( aclGroupsEnum ).when( aclGroup ).members();
        Subject subject = new Subject();
        subject.getPrincipals().addAll( principals );
        subject.getPrincipals().add( aclGroup );
        LoginContext loginContext = mock( LoginContext.class );
        when( loginContext.getSubject() ).thenReturn( subject );
        doReturn( loginContext ).when( tested ).createLoginContext( anyString(), anyString() );

        User user = tested.login( username, password );

        assertNotNull( user );
        assertEquals( username, user.getIdentifier() );
        assertEquals( 2, user.getRoles().size() );
        assertTrue( user.getRoles().contains( new RoleImpl( "admin" ) ) );
        assertTrue( user.getRoles().contains( new RoleImpl( "role1" ) ) );
        assertEquals( 3, user.getGroups().size() );
        assertTrue( user.getGroups().contains( new GroupImpl( "group1" ) ) );
        assertTrue( user.getGroups().contains( new GroupImpl( "g1" ) ) );
        assertTrue( user.getGroups().contains( new GroupImpl( "g2" ) ) );
    }

    @Test
    public void testLoggedIn() throws Exception {
        String username = "user1";
        String password = "password1";
        RoleRegistry.get().registerRole( "admin" );
        Set<Principal> principals = mockPrincipals( "admin" );
        Subject subject = new Subject();
        subject.getPrincipals().addAll( principals );
        LoginContext loginContext = mock( LoginContext.class );
        when( loginContext.getSubject() ).thenReturn( subject );
        doReturn( loginContext ).when( tested ).createLoginContext( anyString(), anyString() );

        tested.login( username, password );

        assertTrue( tested.isLoggedIn() );
    }

    @Test
    public void testGetUser() throws Exception {
        String username = "user1";
        String password = "password1";
        RoleRegistry.get().registerRole( "admin" );
        Set<Principal> principals = mockPrincipals( "admin" );
        Subject subject = new Subject();
        subject.getPrincipals().addAll( principals );
        LoginContext loginContext = mock( LoginContext.class );
        when( loginContext.getSubject() ).thenReturn( subject );
        doReturn( loginContext ).when( tested ).createLoginContext( anyString(), anyString() );

        User user = tested.login( username, password );
        User user1 = tested.getUser();

        assertEquals( user, user1 );
    }

    private Set<Principal> mockPrincipals( String... names ) {
        Set<Principal> principals = new HashSet<Principal>();
        for ( String name : names ) {
            Principal p1 = mock( Principal.class );
            when( p1.getName() ).thenReturn( name );
            principals.add( p1 );
        }
        return principals;
    }

}