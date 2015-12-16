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
import org.drools.workbench.screens.guided.dtable.client.resources.GuidedDecisionTableResources;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.type.GuidedDTableResourceType;
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

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<GuidedDecisionTableEditorService> service;

    @Inject
    private GuidedDTableResourceType resourceType;

    @Inject
    private GuidedDecisionTableOptions options;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Inject
    private AsyncPackageDataModelOracleFactory oracleFactory;

    @Inject
    private SyncBeanManager iocManager;

    private NewGuidedDecisionTableWizard wizard;

    private AsyncPackageDataModelOracle oracle;

    private NewResourcePresenter newResourcePresenter;

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

}
