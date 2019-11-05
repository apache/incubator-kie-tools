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

import java.util.Set;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.VFSLockServiceImpl;
import org.uberfire.backend.vfs.VFSLockService;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.server.BaseFilteredServlet;

@WebServlet(name = "ReleaseUserLocksServlet", urlPatterns = "/releaseUserLocksServlet")
public class ReleaseUserLocksServlet extends BaseFilteredServlet {

    private static final Logger logger = LoggerFactory.getLogger(ReleaseUserLocksServlet.class);

    @Inject
    private VFSLockService vfsLockService;

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response) {
        final HttpSession session = request.getSession();

        if (session != null && session.getAttribute(VFSLockServiceImpl.LOCK_SESSION_ATTRIBUTE_NAME) != null) {
            final Set<LockInfo> locks =
                    (Set<LockInfo>) session.getAttribute(VFSLockServiceImpl.LOCK_SESSION_ATTRIBUTE_NAME);

            try {
                locks.forEach(lockInfo -> vfsLockService.releaseLock(lockInfo.getFile()));
                locks.clear();
            } catch (Exception e) {
                logger.error("Error when releasing locks.", e);
            }
        }
    }
}