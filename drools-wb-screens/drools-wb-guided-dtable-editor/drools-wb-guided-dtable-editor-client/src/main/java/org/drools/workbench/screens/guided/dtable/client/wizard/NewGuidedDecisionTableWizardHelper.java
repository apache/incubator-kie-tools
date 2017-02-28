/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.wizard;

import javax.inject.Inject;

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.type.GuidedDTableResourceType;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

/**
 * Helps create and save a new Guided Decision Table using the {@link NewGuidedDecisionTableWizard} wizard.
 * Handles all of the boiler-plate to initialise and manage the Wizard's state together with performing the
 * creation of any resulting Guided Decision Table. Users of this class need to provide a "Save" success callback.
 */
public class NewGuidedDecisionTableWizardHelper {

    private Caller<GuidedDecisionTableEditorService> dtService;
    private AsyncPackageDataModelOracleFactory oracleFactory;
    private SyncBeanManager beanManager;

    private NewGuidedDecisionTableWizard wizard;

    private GuidedDTableResourceType dtResourceType = new GuidedDTableResourceType();

    @Inject
    public NewGuidedDecisionTableWizardHelper( final Caller<GuidedDecisionTableEditorService> dtService,
                                               final AsyncPackageDataModelOracleFactory oracleFactory,
                                               final SyncBeanManager beanManager ) {
        this.dtService = dtService;
        this.oracleFactory = oracleFactory;
        this.beanManager = beanManager;
    }

    /**
     * Presents the {@link NewGuidedDecisionTableWizard} to Users to creates a new Guided Decision Table.
     * @param contextPath
     *         The base path where the Decision Table will be created. Cannot be null.
     * @param baseFileName
     *         The base file name of the new Decision Table. Cannot be null.
     * @param tableFormat
     *         The format of the Decision Table. Cannot be null.
     * @param view
     *         A {@link HasBusyIndicator} to handle status messages. Cannot be null.
     * @param onSaveSuccessCallback
     *         Called when the new Decision Table has successfully been created. Cannot be null.
     */
    public void createNewGuidedDecisionTable( final Path contextPath,
                                              final String baseFileName,
                                              final GuidedDecisionTable52.TableFormat tableFormat,
                                              final GuidedDecisionTable52.HitPolicy hitPolicy,
                                              final HasBusyIndicator view,
                                              final RemoteCallback<Path> onSaveSuccessCallback ) {
        PortablePreconditions.checkNotNull( "contextPath",
                                            contextPath );
        PortablePreconditions.checkNotNull( "baseFileName",
                                            baseFileName );
        PortablePreconditions.checkNotNull( "tableFormat",
                                            tableFormat );
        PortablePreconditions.checkNotNull( "hitPolicy",
                                            hitPolicy );
        PortablePreconditions.checkNotNull( "view",
                                            view );
        PortablePreconditions.checkNotNull( "onSaveSuccessCallback",
                                            onSaveSuccessCallback );

        dtService.call( new RemoteCallback<PackageDataModelOracleBaselinePayload>() {

            @Override
            public void callback( final PackageDataModelOracleBaselinePayload dataModel ) {
                final AsyncPackageDataModelOracle oracle = oracleFactory.makeAsyncPackageDataModelOracle( contextPath,
                                                                                                          dataModel );

                //NewGuidedDecisionTableHandler is @ApplicationScoped and so has a single instance of the NewGuidedDecisionTableWizard injected.
                //The Wizard maintains state and hence multiple use of the same Wizard instance leads to the Wizard UI showing stale values.
                //Rather than have the Wizard initialise fields when shown I elected to create new instances whenever needed.
                wizard = beanManager.lookupBean( NewGuidedDecisionTableWizard.class ).getInstance();

                wizard.setContent( contextPath,
                                   baseFileName,
                                   tableFormat,
                                   hitPolicy,
                                   oracle,
                                   new NewGuidedDecisionTableWizard.GuidedDecisionTableWizardHandler() {

                                       @Override
                                       public void save( final Path contextPath,
                                                         final String baseFileName,
                                                         final GuidedDecisionTable52 model ) {
                                           destroyWizard();
                                           oracleFactory.destroy( oracle );
                                           view.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
                                           dtService.call( ( Path path ) -> {
                                                               view.hideBusyIndicator();
                                                               onSaveSuccessCallback.callback( path );
                                                           },
                                                           new HasBusyIndicatorDefaultErrorCallback( view ) ).create( contextPath,
                                                                                                                      buildFileName( baseFileName ),
                                                                                                                      model,
                                                                                                                      "" );

                                       }

                                       private String buildFileName( final String baseFileName ) {
                                           final String suffix = dtResourceType.getSuffix();
                                           final String prefix = dtResourceType.getPrefix();
                                           final String extension = !( suffix == null || "".equals( suffix ) ) ? "." + dtResourceType.getSuffix() : "";
                                           if ( baseFileName.endsWith( extension ) ) {
                                               return prefix + baseFileName;
                                           }
                                           return prefix + baseFileName + extension;
                                       }

                                       @Override
                                       public void destroyWizard() {
                                           if ( wizard != null ) {
                                               beanManager.destroyBean( wizard );
                                               wizard = null;
                                           }
                                       }
                                   } );
                wizard.start();
            }
        } ).loadDataModel( contextPath );
    }

}