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

package org.kie.workbench.common.screens.projecteditor.client.forms;

import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;

public class MockPomServiceCaller
        implements Caller<POMService> {

    private POM gavModel;
    private final POMService service;
    private RemoteCallback callback;
    private POM pomModel;

    public MockPomServiceCaller() {
        service = new POMService() {

            @Override
            public POM load( Path path ) {
                callback.callback( gavModel );
                return gavModel;
            }

            @Override
            public Path save( Path path,
                              POM content,
                              Metadata metadata,
                              String comment ) {
                MockPomServiceCaller.this.pomModel = content;
                callback.callback( path );
                return path;
            }

            @Override
            public Path create( Path projectRoot,
                                String baseURL,
                                POM pom ) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Path save(Path path, POM content, Metadata metadata, String comment, boolean updateModules) {
                return null;
            }

        };
    }

    @Override
    public POMService call() {
        return service;
    }

    @Override
    public POMService call( RemoteCallback<?> remoteCallback ) {
        callback = remoteCallback;
        return service;
    }

    @Override
    public POMService call( RemoteCallback<?> remoteCallback,
                            ErrorCallback<?> errorCallback ) {
        callback = remoteCallback;
        return service;
    }

    public void setGav( POM gavModel ) {
        this.gavModel = gavModel;
    }

    public POM getSavedPOM() {
        return pomModel;
    }
}
