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

import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Specializes;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.uberfire.commons.concurrent.ManagedExecutorServiceProducer;

public class WorkspaceManagedExecutorServiceProducer extends ManagedExecutorServiceProducer {

    private final WorkspaceNameResolver workspaceNameResolver;

    @Inject
    public WorkspaceManagedExecutorServiceProducer(WorkspaceNameResolver workspaceNameResolver) {
        this.workspaceNameResolver = workspaceNameResolver;
    }

    @Produces
    @Specializes
    @Override
    public ManagedExecutorService produceExecutorService(InjectionPoint injectionPoint) {
        return new WorkspaceManagedExecutorService(workspaceNameResolver,
                                                   this.getManagedExecutorService());
    }
}
