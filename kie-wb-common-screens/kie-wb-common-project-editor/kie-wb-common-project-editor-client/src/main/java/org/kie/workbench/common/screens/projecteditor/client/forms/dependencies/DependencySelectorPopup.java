/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.projecteditor.client.forms.dependencies;

import java.util.ArrayList;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.kie.workbench.common.screens.projecteditor.client.forms.GAVSelectionHandler;

@Dependent
public class DependencySelectorPopup
        implements DependencySelectorPresenter {

    @Inject
    private DependencySelectorPopupView view;

    @Inject
    private Caller<M2RepoService> m2RepoService;

    private ArrayList<GAVSelectionHandler> selectionHandlers = new ArrayList<GAVSelectionHandler>();

    @AfterInitialization
    public void init() {
        view.init( this );
    }

    public void show() {
        view.show();
    }

    @Override
    public void onPathSelection( String pathToDependency ) {
        m2RepoService.call( new RemoteCallback<GAV>() {
            @Override
            public void callback( GAV gav ) {
                for ( GAVSelectionHandler handler : selectionHandlers ) {
                    handler.onSelection( gav );
                }
            }
        } ).loadGAVFromJar( pathToDependency );

        view.hide();
    }

    public void addSelectionHandler( GAVSelectionHandler selectionHandler ) {
        selectionHandlers.add( selectionHandler );
    }
}
