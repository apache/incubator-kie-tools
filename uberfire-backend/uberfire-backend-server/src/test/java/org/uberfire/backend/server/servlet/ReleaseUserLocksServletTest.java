/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.backend.server.servlet;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.VFSLockServiceImpl;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSLockService;
import org.uberfire.backend.vfs.impl.LockInfo;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ReleaseUserLocksServletTest {

    @Mock
    private VFSLockService vfsLockService;

    @InjectMocks
    private ReleaseUserLocksServlet releaseUserLocksServlet;

    @Test
    public void releaseUserLocksWhenInvalidSessionTest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        doReturn(null).when(request).getSession();

        releaseUserLocksServlet.doGet(request,
                                      response);

        verify(vfsLockService, never()).releaseLock(any(Path.class));
    }

    @Test
    public void releaseUserLocksWhenNoLockAttributeTest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        doReturn(session).when(request).getSession();
        doReturn(null).when(session).getAttribute(VFSLockServiceImpl.LOCK_SESSION_ATTRIBUTE_NAME);

        releaseUserLocksServlet.doGet(request,
                                      response);

        verify(vfsLockService, never()).releaseLock(any(Path.class));
    }

    @Test
    public void releaseUserLocksSuccessTest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        Set<LockInfo> locks = new HashSet<>();
        locks.add(mock(LockInfo.class));

        doReturn(session).when(request).getSession();
        doReturn(locks).when(session).getAttribute(VFSLockServiceImpl.LOCK_SESSION_ATTRIBUTE_NAME);

        releaseUserLocksServlet.doGet(request,
                                      response);

        verify(vfsLockService).releaseLock(any(Path.class));
    }
}