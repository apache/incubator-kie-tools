/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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

import java.util.Calendar;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.uberfire.ext.security.server.CacheHeadersFilter.*;

@RunWith(MockitoJUnitRunner.class)
public class BasicAuthSecurityFilterTest {

    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private FilterChain chain;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private HttpSession httpSession;
    
    @Test
    public void testIndependentSessionInvalidated() throws Exception {

        SessionProvider sessionProvider = new SessionProvider(httpSession, 1);

        when(authenticationService.getUser()).thenReturn(new UserImpl("testUser"));
        when(request.getSession(anyBoolean())).then(new Answer<HttpSession>() {
            @Override
            public HttpSession answer(InvocationOnMock invocationOnMock) throws Throwable {
                return sessionProvider.provideSession();
            }
        });

        final BasicAuthSecurityFilter filter = new BasicAuthSecurityFilter(authenticationService);
        filter.doFilter(request, response, chain);

        verify(httpSession, times(1)).invalidate();
    }

    @Test
    public void testExistingSessionNotInvalidated() throws Exception {

        SessionProvider sessionProvider = new SessionProvider(httpSession);

        when(authenticationService.getUser()).thenReturn(new UserImpl("testUser"));
        when(request.getSession(anyBoolean())).then(new Answer<HttpSession>() {
            @Override
            public HttpSession answer(InvocationOnMock invocationOnMock) throws Throwable {
                return sessionProvider.provideSession();
            }
        });

        final BasicAuthSecurityFilter filter = new BasicAuthSecurityFilter(authenticationService);
        filter.doFilter(request, response, chain);

        verify(httpSession, never()).invalidate();
    }

    private class SessionProvider {
        private int counter = 0;
        private HttpSession httpSession;

        public SessionProvider(HttpSession httpSession) {
            this.httpSession = httpSession;
        }

        public SessionProvider(HttpSession httpSession, int counter) {
            this.httpSession = httpSession;
            this.counter = counter;
        }

        public HttpSession provideSession() {
            if (counter == 0) {
                return httpSession;
            }
            counter--;
            return null;
        }
    }

}
