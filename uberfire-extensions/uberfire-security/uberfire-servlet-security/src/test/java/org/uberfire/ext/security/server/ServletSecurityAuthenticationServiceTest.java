/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.security.server;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.errai.security.shared.api.GroupImpl;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.server.security.RoleRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.uberfire.ext.security.server.ServletSecurityAuthenticationService.USER_SESSION_ATTR_NAME;

@RunWith(MockitoJUnitRunner.class)
public class ServletSecurityAuthenticationServiceTest {

    private static final String USERNAME = "user1";
    private static final String PASSWORD = "password1";

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession httpSession;

    private ServletSecurityAuthenticationService tested;

    @Before
    public void setup() throws Exception {

        Principal p1 = mock(Principal.class);
        when(p1.getName()).thenReturn(USERNAME);
        doReturn(p1).when(request).getUserPrincipal();
        doReturn(httpSession).when(request).getSession();
        doReturn(null).when(httpSession).getAttribute(eq(USER_SESSION_ATTR_NAME));
        when(request.getSession(anyBoolean())).then(new Answer<HttpSession>() {
            @Override
            public HttpSession answer(InvocationOnMock invocationOnMock) throws Throwable {
                return httpSession;
            }
        });

        tested = spy(new ServletSecurityAuthenticationService());

        // Set the request in the thread context.
        SecurityIntegrationFilter.requests.set(request);
    }

    @Test
    public void testLoggedIn() {
        assertTrue(tested.isLoggedIn());
    }

    @Test
    public void testNotLoggedIn() {
        doReturn(null).when(request).getUserPrincipal();
        assertFalse(tested.isLoggedIn());
    }

    @Test
    public void testLogin() throws Exception {

        RoleRegistry.get().registerRole("admin");
        RoleRegistry.get().registerRole("role1");
        Set<Principal> principals = mockPrincipals("admin",
                                                   "role1",
                                                   "group1",
                                                   null);
        Subject subject = new Subject();
        subject.getPrincipals().addAll(principals);
        doReturn(subject).when(tested).getSubjectFromPolicyContext();

        User user = tested.login(USERNAME,
                                 PASSWORD);

        assertNotNull(user);
        assertEquals(USERNAME,
                     user.getIdentifier());
        assertEquals(2,
                     user.getRoles().size());
        assertTrue(user.getRoles().contains(new RoleImpl("admin")));
        assertTrue(user.getRoles().contains(new RoleImpl("role1")));
        assertEquals(1,
                     user.getGroups().size());
        assertTrue(user.getGroups().contains(new GroupImpl("group1")));
    }

    @Test
    public void testLoginNoPrincipal() throws Exception {

        Subject subject = new Subject();
        doReturn(subject).when(tested).getSubjectFromPolicyContext();

        User user = tested.login(USERNAME,
                                 PASSWORD);

        assertNotNull(user);
        assertEquals(USERNAME,
                     user.getIdentifier());
        assertEquals(0,
                     user.getRoles().size());
        assertEquals(0,
                     user.getGroups().size());
    }

    @Test
    public void testLoginSubjectGroups() throws Exception {
        String username = "user1";
        String password = "password1";
        RoleRegistry.get().registerRole("admin");
        RoleRegistry.get().registerRole("role1");
        Set<Principal> principals = mockPrincipals("admin",
                                                   "role1",
                                                   "group1");
        Group aclGroup = mock(Group.class);
        doReturn(ServletSecurityAuthenticationService.DEFAULT_ROLE_PRINCIPLE_NAME).when(aclGroup).getName();
        Set<Principal> aclGroups = mockPrincipals("g1",
                                                  "g2");
        Enumeration<? extends Principal> aclGroupsEnum = Collections.enumeration(aclGroups);
        doReturn(aclGroupsEnum).when(aclGroup).members();
        Subject subject = new Subject();
        subject.getPrincipals().addAll(principals);
        subject.getPrincipals().add(aclGroup);
        doReturn(subject).when(tested).getSubjectFromPolicyContext();

        User user = tested.login(username,
                                 password);

        assertNotNull(user);
        assertEquals(username,
                     user.getIdentifier());
        assertEquals(2,
                     user.getRoles().size());
        assertTrue(user.getRoles().contains(new RoleImpl("admin")));
        assertTrue(user.getRoles().contains(new RoleImpl("role1")));
        assertEquals(3,
                     user.getGroups().size());
        assertTrue(user.getGroups().contains(new GroupImpl("group1")));
        assertTrue(user.getGroups().contains(new GroupImpl("g1")));
        assertTrue(user.getGroups().contains(new GroupImpl("g2")));
    }

    @Test
    public void testLoginWithUsernameInPrincipal() throws Exception {
        RoleRegistry.get().registerRole("admin");
        RoleRegistry.get().registerRole("role1");
        Set<Principal> principals = mockPrincipals("admin",
                                                   "role1",
                                                   "group1",
                                                   USERNAME,
                                                   null);
        Subject subject = new Subject();
        subject.getPrincipals().addAll(principals);
        doReturn(subject).when(tested).getSubjectFromPolicyContext();

        User user = tested.login(USERNAME,
                                 PASSWORD);

        assertNotNull(user);
        assertEquals(USERNAME,
                     user.getIdentifier());
        assertEquals(2,
                     user.getRoles().size());
        assertTrue(user.getRoles().contains(new RoleImpl("admin")));
        assertTrue(user.getRoles().contains(new RoleImpl("role1")));
        assertEquals(1,
                     user.getGroups().size());
        assertTrue(user.getGroups().contains(new GroupImpl("group1")));
        assertFalse(user.getGroups().contains(new GroupImpl("user1")));
    }

    @Test
    public void testLogout() throws Exception {
        tested.logout();
        verify(request,
               times(1)).logout();
        verify(httpSession,
               times(1)).invalidate();
    }

    @Test
    public void testSwallowIllegalStateExceptionDuringLogoutWithKeycloak() {
        doThrow(new IllegalStateException("UT000021: Session already invalidated")).when(httpSession).invalidate();
        tested.logout();
    }

    @Test
    public void testReThrowUnexpectedIllegalStateExceptionDuringLogout() {
        String exceptionMsg = "This exception should be propagated!";
        doThrow(new IllegalStateException(exceptionMsg)).when(httpSession).invalidate();
        try {
            tested.logout();
        } catch (IllegalStateException ise) {
            // the exception message needs to be the same as defined above
            assertEquals(exceptionMsg, ise.getMessage());
        }
    }

    private Set<Principal> mockPrincipals(String... names) {
        Set<Principal> principals = new HashSet<Principal>();
        for (String name : names) {
            Principal p1 = mock(Principal.class);
            when(p1.getName()).thenReturn(name);
            principals.add(p1);
        }
        return principals;
    }
}