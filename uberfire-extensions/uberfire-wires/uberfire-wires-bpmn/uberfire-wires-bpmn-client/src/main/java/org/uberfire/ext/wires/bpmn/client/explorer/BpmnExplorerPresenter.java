/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.wires.bpmn.client.explorer;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.wires.bpmn.api.service.BpmnService;
import org.uberfire.ext.wires.bpmn.client.resources.i18n.BpmnEditorConstants;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "BPMN Explorer")
public class BpmnExplorerPresenter {

    @Inject
    private Caller<BpmnService> service;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private BpmnExplorerView view;

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        service.call( new RemoteCallback<List<Path>>() {
            @Override
            public void callback( final List<Path> files ) {
                view.setContent( files );
            }
        } ).listFiles();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return BpmnEditorConstants.INSTANCE.bpmnExplorerTitle();
    }

    @WorkbenchPartView
    public UberView<BpmnExplorerPresenter> getWidget() {
        return view;
    }

    public void openFile( final Path file ) {
        placeManager.goTo( new PathPlaceRequest( file ) );
    }

}
