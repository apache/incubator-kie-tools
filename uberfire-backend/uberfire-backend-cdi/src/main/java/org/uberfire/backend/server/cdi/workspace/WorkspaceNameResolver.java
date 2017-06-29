/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.uberfire.backend.server.cdi.workspace;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.uberfire.rpc.SessionInfo;
import org.uberfire.workspace.WorkspaceContext;

/**
 * Resolves Workspace name. At this moment the workspace name is based on usernames, so exists a workspace
 * per user. If there is no user session found the workspace name is called "global". This situation could happen during
 * some Server Side executions.
 */
@ApplicationScoped
public class WorkspaceNameResolver {

    public static final String GLOBAL_WORKSPACE_NAME = "global";
    private BeanManager beanManager;

    public WorkspaceNameResolver() {
    }

    @Inject
    public WorkspaceNameResolver(BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    /**
     * Resolves the workspace name based on the user session. If not session detected returns "global".
     * @return the workspace name or "global".
     */
    public String getWorkspaceName() {
        try {
            SessionInfo sessionInfo = getSessionInfo();
            return sessionInfo.getIdentity().getIdentifier();
        } catch (Exception e) {
            String name = WorkspaceContext.get();
            if (name == null || name.isEmpty()) {
                return GLOBAL_WORKSPACE_NAME;
            } else {
                return name;
            }
        }
    }

    protected SessionInfo getSessionInfo() {
        final Bean<SessionInfo> bean = (Bean<SessionInfo>) this.beanManager.getBeans(SessionInfo.class).iterator().next();
        final CreationalContext<SessionInfo> creationalContext = this.beanManager.createCreationalContext(bean);
        return (SessionInfo) this.beanManager.getReference(bean,
                                                           SessionInfo.class,
                                                           creationalContext);
    }
}
