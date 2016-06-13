/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.handlers;

import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.GuidedDecisionTableResources;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.type.GuidedDTableResourceType;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.AddDecisionTableToEditorEvent;
import org.drools.workbench.screens.guided.dtable.client.wizard.NewGuidedDecisionTableWizard;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.type.ResourceTypeDefinition;

/**
 * Handler for the creation of new Guided Decision Tables
 */
@ApplicationScoped
public class NewGuidedDecisionTableHandler extends DefaultNewResourceHandler {

    //Injected
    private PlaceManager placeManager;
    private Caller<GuidedDecisionTableEditorService> service;
    private Event<AddDecisionTableToEditorEvent> addDecisionTableToEditorEvent;
    private GuidedDTableResourceType resourceType;
    private GuidedDecisionTableOptions options;
    private BusyIndicatorView busyIndicatorView;
    private AsyncPackageDataModelOracleFactory oracleFactory;
    private SyncBeanManager iocManager;

    private NewGuidedDecisionTableWizard wizard;

    private AsyncPackageDataModelOracle oracle;

    private NewResourcePresenter newResourcePresenter;

    public NewGuidedDecisionTableHandler() {
        //Zero parameter constructor for CDI proxies
    }

    @Inject
    public NewGuidedDecisionTableHandler( final PlaceManager placeManager,
                                          final Caller<GuidedDecisionTableEditorService> service,
                                          final Event<AddDecisionTableToEditorEvent> addDecisionTableToEditorEvent,
                                          final GuidedDTableResourceType resourceType,
                                          final GuidedDecisionTableOptions options,
                                          final BusyIndicatorView busyIndicatorView,
                                          final AsyncPackageDataModelOracleFactory oracleFactory,
                                          final SyncBeanManager iocManager ) {
        this.placeManager = placeManager;
        this.service = service;
        this.addDecisionTableToEditorEvent = addDecisionTableToEditorEvent;
        this.resourceType = resourceType;
        this.options = options;
        this.busyIndicatorView = busyIndicatorView;
        this.oracleFactory = oracleFactory;
        this.iocManager = iocManager;
    }

    @PostConstruct
    private void setupExtensions() {
        extensions.add( new Pair<String, GuidedDecisionTableOptions>( GuidedDecisionTableConstants.INSTANCE.Options(),
                                                                      options ) );
    }

    @Override
    public String getDescription() {
        return GuidedDecisionTableConstants.INSTANCE.NewGuidedDecisionTableDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( GuidedDecisionTableResources.INSTANCE.images().typeGuidedDecisionTable() );
    }

    @Override
    public List<Pair<String, ? extends IsWidget>> getExtensions() {
        final boolean enableOpenInExistingEditor = getTargetEditorPlaceRequest() != null;
        options.enableOpenInExistingEditor( enableOpenInExistingEditor );
        options.setOpenInExistingEditor( false );
        return super.getExtensions();
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
    }

    @Override
    public void create( final Package pkg,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        this.newResourcePresenter = presenter;
        if ( !options.isUsingWizard() ) {
            createEmptyDecisionTable( pkg.getPackageMainResourcesPath(),
                                      baseFileName,
                                      options.getTableFormat() );
        } else {
            createDecisionTableWithWizard( pkg.getPackageMainResourcesPath(),
                                           baseFileName,
                                           options.getTableFormat() );
        }
    }

    private void createEmptyDecisionTable( final Path contextPath,
                                           final String baseFileName,
                                           final GuidedDecisionTable52.TableFormat tableFormat ) {
        final GuidedDecisionTable52 model = new GuidedDecisionTable52();
        model.setTableFormat( tableFormat );
        model.setTableName( baseFileName );
        save( contextPath,
              baseFileName,
              model );
    }

    private void createDecisionTableWithWizard( final Path contextPath,
                                                final String baseFileName,
                                                final GuidedDecisionTable52.TableFormat tableFormat ) {
        service.call( new RemoteCallback<PackageDataModelOracleBaselinePayload>() {

            @Override
            public void callback( final PackageDataModelOracleBaselinePayload dataModel ) {
                newResourcePresenter.complete();
                oracle = oracleFactory.makeAsyncPackageDataModelOracle( contextPath,
                                                                        dataModel );

                //This NewResourceHandler is @ApplicationScoped and so has a single instance of the NewGuidedDecisionTableWizard injected.
                //The Wizard maintains state and hence multiple use of the same Wizard instance leads to the Wizard UI showing stale values.
                //Rather than have the Wizard initialise fields when shown I elected to create new instances whenever needed.
                wizard = iocManager.lookupBean( NewGuidedDecisionTableWizard.class ).getInstance();

                wizard.setContent( contextPath,
                                   baseFileName,
                                   tableFormat,
                                   oracle,
                                   NewGuidedDecisionTableHandler.this );
                wizard.start();
            }
        } ).loadDataModel( contextPath );
    }

    public void destroyWizard() {
        if ( wizard != null ) {
            iocManager.destroyBean( wizard );
            wizard = null;
        }
    }

    public void save( final Path contextPath,
                      final String baseFileName,
                      final GuidedDecisionTable52 model ) {
        destroyWizard();
        oracleFactory.destroy( oracle );
        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
        service.call( getSuccessCallback( newResourcePresenter ),
                      new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).create( contextPath,
                                                                                              buildFileName( baseFileName,
                                                                                                             resourceType ),
                                                                                              model,
                                                                                              "" );
    }

    @Override
    protected RemoteCallback<Path> getSuccessCallback( final NewResourcePresenter presenter ) {
        return new RemoteCallback<Path>() {

            @Override
            public void callback( final Path path ) {
                busyIndicatorView.hideBusyIndicator();
                presenter.complete();
                notifySuccess();
                openInEditor( path );
            }

        };
    }

    private void openInEditor( final Path path ) {
        if ( options.isOpenInExistingEditor() ) {
            addDecisionTableToEditorEvent.fire( new AddDecisionTableToEditorEvent( getTargetEditorPlaceRequest(),
                                                                                   getObservablePath( path ) ) );

        } else {
            placeManager.goTo( path );
        }
    }

    private PathPlaceRequest getTargetEditorPlaceRequest() {
        final Collection<PathPlaceRequest> openEditors = placeManager.getActivitiesForResourceType( resourceType );
        if ( openEditors.size() != 1 ) {
            return null;
        }
        return openEditors.iterator().next();
    }

    private ObservablePath getObservablePath( final Path path ) {
        final ObservablePath observablePath = iocManager.lookupBean( ObservablePath.class ).getInstance();
        observablePath.wrap( path );
        return observablePath;
    }

}
