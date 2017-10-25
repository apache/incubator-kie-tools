/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.backend.file.upload;

import java.io.InputStream;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.errai.bus.client.api.QueueSession;
import org.jboss.errai.bus.server.api.SessionProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AbstractFileServletTest {

    @Mock
    private SessionProvider provider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private QueueSession queueSession;

    @Mock
    private HttpSession httpSession;

    @Test
    public void testGetSessionId() throws Exception {
        final String clientId = "1";
        final String sessionId = "2";

        when(request.getSession(eq(true))).thenReturn(httpSession);
        when(request.getParameter(eq("clientId"))).thenReturn(clientId);
        when(provider.createOrGetSession(httpSession,
                                         clientId)).thenReturn(queueSession);
        when(queueSession.getSessionId()).thenReturn(sessionId);

        assertEquals(sessionId,
                     abstractFileServlet().getSessionId(request,
                                                        provider));
    }

    @Test
    public void testGetSessionIdWhenClientIdIsMissing() throws Exception {
        final String clientId = null;
        final String sessionId = "InvalidSessionId";

        when(request.getSession(eq(true))).thenReturn(httpSession);
        when(request.getParameter(eq("clientId"))).thenReturn(clientId);
        when(provider.createOrGetSession(httpSession,
                                         "0")).thenReturn(queueSession);
        when(queueSession.getSessionId()).thenReturn(sessionId);

        assertEquals(sessionId,
                     abstractFileServlet().getSessionId(request,
                                                        provider));
    }

    private AbstractFileServlet abstractFileServlet() {
        return new AbstractFileServlet() {
            @Override
            protected InputStream doLoad(final Path path,
                                         final HttpServletRequest request) {
                return null;
            }

            @Override
            protected void doCreate(final Path path,
                                    final InputStream data,
                                    final HttpServletRequest request,
                                    final String comment) {

            }

            @Override
            protected void doUpdate(final Path path,
                                    final InputStream data,
                                    final HttpServletRequest request,
                                    final String comment) {

            }

            @Override
            protected Path convertPath(final String fileName,
                                       final String contextPath) throws URISyntaxException {
                return null;
            }

            @Override
            protected Path convertPath(final String fullPath) throws URISyntaxException {
                return null;
            }
        };
    }
}