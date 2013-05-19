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

package org.kie.workbench.projecteditor.client.forms;

import org.jboss.errai.bus.client.api.ErrorCallback;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.project.model.KModuleModel;
import org.kie.guvnor.project.service.KModuleService;
import org.kie.workbench.common.services.shared.metadata.model.Metadata;
import org.uberfire.backend.vfs.Path;

public class MockProjectEditorServiceCaller
        implements Caller<KModuleService> {

    private final KModuleService service;

    private KModuleModel savedModel;
    private KModuleModel modelForLoading;

    private RemoteCallback callback;
    private Path pathToRelatedKModuleFileIfAny;

    MockProjectEditorServiceCaller() {

        service = new KModuleService() {

            @Override
            public Path setUpKModuleStructure( Path pathToPom ) {
                callback.callback( pathToRelatedKModuleFileIfAny );
                return pathToRelatedKModuleFileIfAny;
            }

            @Override
            public Path save( Path path,
                              KModuleModel model,
                              Metadata metadata,
                              String commitMessage ) {
                callback.callback( null );
                savedModel = model;
                return path;
            }

            @Override
            public KModuleModel load( Path path ) {
                callback.callback( modelForLoading );
                return modelForLoading;
            }

            @Override
            public Path pathToRelatedKModuleFileIfAny( Path pathToPomXML ) {
                callback.callback( pathToRelatedKModuleFileIfAny );
                return pathToRelatedKModuleFileIfAny;
            }

        };
    }

    public KModuleModel getSavedModel() {
        return savedModel;
    }

    @Override
    public KModuleService call( RemoteCallback<?> callback ) {
        this.callback = callback;
        return service;
    }

    @Override
    public KModuleService call( RemoteCallback<?> callback,
                                ErrorCallback errorCallback ) {
        this.callback = callback;
        return service;
    }

    public void setUpModelForLoading( KModuleModel upModelForLoading ) {
        this.modelForLoading = upModelForLoading;
    }

    public void setPathToRelatedKModuleFileIfAny( Path pathToRelatedKModuleFileIfAny ) {
        this.pathToRelatedKModuleFileIfAny = pathToRelatedKModuleFileIfAny;
    }

}
