/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.defaulteditor.client.editor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.screens.defaulteditor.client.editor.resources.i18n.GuvnorDefaultEditorConstants;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultEditorNewFileUpload;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.type.AnyResourceTypeDefinition;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class NewFileUploader
        extends DefaultNewResourceHandler {

    private PlaceManager placeManager;
    private DefaultEditorNewFileUpload options;
    private AnyResourceTypeDefinition resourceType;
    private BusyIndicatorView busyIndicatorView;

    public NewFileUploader() {
        //Zero-argument constructor for CDI proxies
    }

    @Inject
    public NewFileUploader( final PlaceManager placeManager,
                            final DefaultEditorNewFileUpload options,
                            final AnyResourceTypeDefinition resourceType,
                            final BusyIndicatorView busyIndicatorView ) {
        this.placeManager = placeManager;
        this.options = options;
        this.resourceType = resourceType;
        this.busyIndicatorView = busyIndicatorView;
    }

    @PostConstruct
    private void setupExtensions() {
        extensions.add( new Pair<String, DefaultEditorNewFileUpload>( GuvnorDefaultEditorConstants.INSTANCE.Options(),
                                                                      options ) );
    }

    @Override
    public String getDescription() {
        return GuvnorDefaultEditorConstants.INSTANCE.NewFileDescription();
    }

    @Override
    public IsWidget getIcon() {
        return null;
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
    }

    @Override
    public void create( final org.guvnor.common.services.project.model.Package pkg,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        busyIndicatorView.showBusyIndicator( GuvnorDefaultEditorConstants.INSTANCE.Uploading() );

        //See https://bugzilla.redhat.com/show_bug.cgi?id=1091204
        //If the User-provided file name has an extension use that; otherwise use the same extension as the original (OS FileSystem) extension
        String targetFileName;
        final String originalFileName = options.getFormFileName();
        final String providedFileName = baseFileName;
        if ( providedFileName.contains( "." ) ) {
            targetFileName = providedFileName;
        } else {
            targetFileName = providedFileName + getExtension( originalFileName );
        }

        final Path path = pkg.getPackageMainResourcesPath();
        final Path newPath = PathFactory.newPathBasedOn( targetFileName,
                                                         encode( path.toURI() + "/" + targetFileName ),
                                                         path );

        options.setFolderPath( pkg.getPackageMainResourcesPath() );
        options.setFileName( targetFileName );

        options.upload( new Command() {

                            @Override
                            public void execute() {
                                busyIndicatorView.hideBusyIndicator();
                                presenter.complete();
                                notifySuccess();
                                placeManager.goTo( newPath );
                            }

                        },
                        new Command() {

                            @Override
                            public void execute() {
                                busyIndicatorView.hideBusyIndicator();
                            }
                        } );
    }

    String encode( final String uri ) {
        return URL.encode( uri );
    }

    private String getExtension( final String originalFileName ) {
        if ( originalFileName.contains( "." ) ) {
            return "." + originalFileName.substring( originalFileName.lastIndexOf( "." ) + 1 );
        }
        return "";
    }

}
