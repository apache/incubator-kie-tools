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

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.data.observer.Observer;
import org.kie.workbench.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.guvnor.project.model.POM;
import org.kie.guvnor.project.service.POMService;
import org.kie.workbench.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.Command;

public class POMEditorPanel
        implements IsWidget {

    private final POMEditorPanelView view;
    private Path path;
    private POM model;
    private final Caller<POMService> pomServiceCaller;
    private Observer<POM> observer;

    @Inject
    public POMEditorPanel( final Caller<POMService> pomServiceCaller,
                           final POMEditorPanelView view ) {
        this.pomServiceCaller = pomServiceCaller;
        this.view = view;

    }

    public void init( final Path path,
                      final boolean isReadOnly ) {
        this.path = path;
        if ( isReadOnly ) {
            view.setReadOnly();
        }
        //Busy popup is handled by ProjectEditorScreen
        pomServiceCaller.call( getModelSuccessCallback(),
                               new HasBusyIndicatorDefaultErrorCallback( view ) ).load( path );
    }

    private RemoteCallback<POM> getModelSuccessCallback() {
        return new RemoteCallback<POM>() {

            @Override
            public void callback( final POM model ) {
                POMEditorPanel.this.model = model;
                observer = new Observer(model);

                view.setGAV( model.getGav() );
                view.addArtifactIdChangeHandler( new ArtifactIdChangeHandler() {
                    @Override
                    public void onChange( String newArtifactId ) {
                        setTitle( newArtifactId );
                    }
                } );
                setTitle( model.getGav().getArtifactId() );
                view.setDependencies( POMEditorPanel.this.model.getDependencies() );
            }
        };
    }

    private void setTitle( final String titleText ) {
        if ( titleText == null || titleText.isEmpty() ) {
            view.setTitleText( ProjectEditorConstants.INSTANCE.ProjectModel() );
        } else {
            view.setTitleText( titleText );
        }
    }

    public void save( final String commitMessage,
                      final Command callback,
                      final Metadata metadata ) {
        //Busy popup is handled by ProjectEditorScreen
        pomServiceCaller.call( getSaveSuccessCallback( callback ),
                               new HasBusyIndicatorDefaultErrorCallback( view ) ).save( path,
                                                                                        model,
                                                                                        metadata,
                                                                                        commitMessage );
    }

    private RemoteCallback<Path> getSaveSuccessCallback( final Command callback ) {
        return new RemoteCallback<Path>() {

            @Override
            public void callback( final Path path ) {
                callback.execute();
                view.showSaveSuccessful( "pom.xml" );
            }
        };
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public String getTitle() {
        return view.getTitleWidget();
    }

    public boolean isDirty() {
        return observer.isDirty(model);
    }
}