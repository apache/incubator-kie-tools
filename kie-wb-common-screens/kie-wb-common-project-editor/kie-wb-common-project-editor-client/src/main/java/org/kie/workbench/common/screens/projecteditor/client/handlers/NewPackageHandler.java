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
package org.kie.workbench.common.screens.projecteditor.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.workbench.type.AnyResourceTypeDefinition;
import org.uberfire.workbench.type.ResourceTypeDefinition;

/**
 * Handler for the creation of new Folders
 */
@ApplicationScoped
public class NewPackageHandler
        extends DefaultNewResourceHandler {

    private Caller<KieProjectService> projectService;
    private Caller<ValidationService> validationService;
    //We don't really need this for Packages but it's required by DefaultNewResourceHandler
    private AnyResourceTypeDefinition resourceType;

    public NewPackageHandler() {
    }

    @Inject
    public NewPackageHandler( Caller<KieProjectService> projectService,
                              Caller<ValidationService> validationService,
                              AnyResourceTypeDefinition resourceType ) {
        this.projectService = projectService;
        this.validationService = validationService;
        this.resourceType = resourceType;
    }

    @Override
    public String getDescription() {
        return ProjectEditorResources.CONSTANTS.newPackageDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( ProjectEditorResources.INSTANCE.newFolderIcon() );
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
    }

    @Override
    public void validate( final String packageName,
                          final ValidatorWithReasonCallback callback ) {
        if ( getPackage() == null ) {
            Window.alert( CommonConstants.INSTANCE.MissingPath() );
            callback.onFailure();
            return;
        }

        validationService.call( new RemoteCallback<Boolean>() {
            @Override
            public void callback( final Boolean response ) {
                if ( Boolean.TRUE.equals( response ) ) {
                    callback.onSuccess();
                } else {
                    callback.onFailure( ProjectEditorResources.CONSTANTS.InvalidPackageName( packageName ) );
                }
            }
        } ).isPackageNameValid( packageName );
    }

    @Override
    public void create( final Package pkg,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        projectService.call( getPackageSuccessCallback( presenter ),
                             new NewPackageErrorCallback() ).newPackage( pkg,
                                                                         baseFileName );
    }

    private RemoteCallback<Package> getPackageSuccessCallback( final NewResourcePresenter presenter ) {
        return new RemoteCallback<Package>() {

            @Override
            public void callback( final Package pkg ) {
                presenter.complete();
                notifySuccess();
            }
        };
    }

}
