/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.project.config;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.structure.backend.repositories.RepositoryCopierImpl;
import org.guvnor.structure.repositories.NewBranchEvent;
import org.uberfire.io.IOService;
import org.uberfire.rpc.SessionInfo;

@Alternative
public class MigrationRepositoryCopierImpl extends RepositoryCopierImpl {

    public MigrationRepositoryCopierImpl() {
        super();
    }

    @Inject
    public MigrationRepositoryCopierImpl(final @Named("ioStrategy") IOService ioService,
                                         final Event<NewBranchEvent> newBranchEventEvent,
                                         final MigrationConfiguredRepositories configuredRepositories,
                                         final MigrationRepositoryServiceImpl repositoryService,
                                         final SessionInfo sessionInfo) {
        super(ioService,
              newBranchEventEvent,
              configuredRepositories,
              repositoryService,
              sessionInfo);
    }
}
