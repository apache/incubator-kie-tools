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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.screens.guided.dtable.client.resources.GuidedDecisionTableResources;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.type.GuidedDTableResourceType;
import org.drools.workbench.screens.guided.dtable.client.wizard.NewGuidedDecisionTableWizardHelper;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourceSuccessEvent;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.workbench.type.ResourceTypeDefinition;

/**
 * Handler for the creation of new Guided Decision Tables
 */
@ApplicationScoped
public class NewGuidedDecisionTableHandler extends DefaultNewResourceHandler {

    //Injected
    private PlaceManager placeManager;
    private Caller<GuidedDecisionTableEditorService> service;
    private GuidedDTableResourceType resourceType;
    private GuidedDecisionTableOptions options;
    private BusyIndicatorView busyIndicatorView;
    private NewGuidedDecisionTableWizardHelper helper;

    private NewResourcePresenter newResourcePresenter;

    public NewGuidedDecisionTableHandler() {
        //Zero parameter constructor for CDI proxies
    }

    @Inject
    public NewGuidedDecisionTableHandler( final PlaceManager placeManager,
                                          final Caller<GuidedDecisionTableEditorService> service,
                                          final GuidedDTableResourceType resourceType,
                                          final GuidedDecisionTableOptions options,
                                          final BusyIndicatorView busyIndicatorView,
                                          final NewGuidedDecisionTableWizardHelper helper ) {
        this.placeManager = placeManager;
        this.service = service;
        this.resourceType = resourceType;
        this.options = options;
        this.busyIndicatorView = busyIndicatorView;
        this.helper = helper;
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
                                      baseFileName );
        } else {
            createDecisionTableWithWizard( pkg.getPackageMainResourcesPath(),
                                           baseFileName );
        }
    }

    private void createEmptyDecisionTable( final Path contextPath,
                                           final String baseFileName ) {
        final GuidedDecisionTable52 model = new GuidedDecisionTable52();
        model.setTableFormat( options.getTableFormat() );
        model.setHitPolicy( options.getHitPolicy() );

        if(GuidedDecisionTable52.HitPolicy.RESOLVED_HIT.equals( options.getHitPolicy() )) {
            final MetadataCol52 metadataCol52 = new MetadataCol52();

            metadataCol52.setMetadata( GuidedDecisionTable52.HitPolicy.RESOLVED_HIT_METADATA_NAME );
            model.getMetadataCols().add( metadataCol52 );
        }

        model.setTableName( baseFileName );

        final RemoteCallback<Path> onSaveSuccessCallback = getSuccessCallback( newResourcePresenter );
        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
        service.call( ( Path path ) -> {
                          busyIndicatorView.hideBusyIndicator();
                          onSaveSuccessCallback.callback( path );
                      },
                      new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).create( contextPath,
                                                                                              buildFileName( baseFileName,
                                                                                                             resourceType ),
                                                                                              model,
                                                                                              "" );
    }

    private void createDecisionTableWithWizard( final Path contextPath,
                                                final String baseFileName ) {
        helper.createNewGuidedDecisionTable( contextPath,
                                             baseFileName,
                                             options.getTableFormat(),
                                             options.getHitPolicy(),
                                             busyIndicatorView,
                                             getSuccessCallback( newResourcePresenter ) );
    }

    @Override
    protected RemoteCallback<Path> getSuccessCallback( final NewResourcePresenter presenter ) {
        return ( Path path ) -> {
            presenter.complete();
            notifySuccess();
            newResourceSuccessEvent.fire( new NewResourceSuccessEvent( path ) );
            placeManager.goTo( path );
        };
    }

}
