/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.projecteditor.client.forms;

import java.util.Set;

import org.jboss.errai.bus.client.api.ErrorCallback;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.services.shared.builder.BuildService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.widgets.events.ResourceChange;

public class MockBuildServiceCaller
        implements Caller<BuildService> {

    private final BuildService service;
    private RemoteCallback callback;
    private boolean buildWasCalled = false;

    public MockBuildServiceCaller() {
        service = new BuildService() {

            @Override
            public void build( Path pathToPom ) {
                callback.callback( null );
                buildWasCalled = true;
            }

            @Override
            public void buildAndDeploy( Path pathToPom ) {
                callback.callback( null );
                buildWasCalled = true;
            }

            @Override
            public void addPackageResource( Path resource ) {
            }

            @Override
            public void deletePackageResource( Path resource ) {
            }

            @Override
            public void updatePackageResource( Path resource ) {
            }

            @Override
            public void updateProjectResource( Path resource ) {
            }

            @Override
            public void applyBatchResourceChanges( Path projectRoot,
                                                   Set<ResourceChange> changes ) {
            }
        };
    }

    @Override
    public BuildService call( RemoteCallback<?> remoteCallback ) {
        callback = remoteCallback;
        return service;
    }

    @Override
    public BuildService call( RemoteCallback<?> remoteCallback,
                              ErrorCallback errorCallback ) {
        callback = remoteCallback;
        return service;
    }

    public boolean isBuildWasCalled() {
        return buildWasCalled;
    }
}
