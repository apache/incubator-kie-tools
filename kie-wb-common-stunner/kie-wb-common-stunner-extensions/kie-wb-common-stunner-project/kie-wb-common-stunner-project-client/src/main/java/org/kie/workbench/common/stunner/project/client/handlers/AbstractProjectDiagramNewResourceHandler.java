/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.project.client.handlers;

import com.google.gwt.logging.client.LogConfiguration;
import org.guvnor.common.services.project.model.Package;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.type.ResourceTypeDefinition;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractProjectDiagramNewResourceHandler<R extends ClientResourceType> extends DefaultNewResourceHandler {

    private static Logger LOGGER = Logger.getLogger( AbstractProjectDiagramNewResourceHandler.class.getName() );

    private final DefinitionManager definitionManager;
    private final ClientProjectDiagramService projectDiagramServices;
    private final R projectDiagramResourceType;

    public AbstractProjectDiagramNewResourceHandler( final DefinitionManager definitionManager,
                                                     final ClientProjectDiagramService projectDiagramServices,
                                                     final R projectDiagramResourceType ) {
        this.definitionManager = definitionManager;
        this.projectDiagramServices = projectDiagramServices;
        this.projectDiagramResourceType = projectDiagramResourceType;
    }

    protected abstract Class<?> getDefinitionSetType();

    protected abstract String getEditorIdentifier();

    @Override
    public ResourceTypeDefinition getResourceType() {
        return projectDiagramResourceType;
    }

    @Override
    public void create( final Package pkg,
                        final String name,
                        final NewResourcePresenter presenter ) {
        BusyPopup.showMessage( "Loading..." );
        final Path path = pkg.getPackageMainResourcesPath();
        final Class<?> type = getDefinitionSetType();
        final String setId = getId( type );
        final String projPkg = pkg.getPackageName();
        final String projName = pkg.getProjectRootPath().getFileName();
        projectDiagramServices.create( path, name, setId, projName, projPkg, new ServiceCallback<Path>() {
            @Override
            public void onSuccess( Path path ) {
                BusyPopup.close();
                presenter.complete();
                notifySuccess();
                PlaceRequest place = new PathPlaceRequest( path, getEditorIdentifier() );
                placeManager.goTo( place );
            }

            @Override
            public void onError( ClientRuntimeError error ) {
                showError( error );
            }
        } );

    }

    private String getId( final Class<?> type ) {
        final Object set = definitionManager.definitionSets().getDefinitionSetByType( type );
        return definitionManager.adapters().forDefinitionSet().getId( set );
    }

    private void showError( final ClientRuntimeError error ) {
        final String msg = error.toString();
        log( Level.SEVERE, msg );
        ErrorPopup.showMessage( msg );
        BusyPopup.close();
    }

    private void log( final Level level, final String message ) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            LOGGER.log( level, message );
        }
    }

}
