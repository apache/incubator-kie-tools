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

import java.util.concurrent.ExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import org.uberfire.commons.concurrent.ExecutorServiceProducer;
import org.uberfire.commons.concurrent.Managed;

public class WorkspaceExecutorServiceProducer extends ExecutorServiceProducer {

    private WorkspaceNameResolver workspaceNameResolver;

    @Inject
    public WorkspaceExecutorServiceProducer(WorkspaceNameResolver workspaceNameResolver) {
        this.workspaceNameResolver = workspaceNameResolver;
    }

    @Produces
    @ApplicationScoped
    @Managed
    @Specializes
    @Override
    public ExecutorService produceExecutorService() {
        return new WorkspaceExecutorService(workspaceNameResolver,
                                            this.getManagedExecutorService());
    }

    protected WorkspaceNameResolver getWorkspaceNameResolver() {
        return workspaceNameResolver;
    }
}
