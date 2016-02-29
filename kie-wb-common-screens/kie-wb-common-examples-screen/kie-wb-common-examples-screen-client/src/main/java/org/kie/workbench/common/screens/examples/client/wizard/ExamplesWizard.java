/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.examples.client.wizard;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.examples.client.resources.i18n.ExamplesScreenConstants;
import org.kie.workbench.common.screens.examples.client.wizard.model.ExamplesWizardModel;
import org.kie.workbench.common.screens.examples.client.wizard.pages.ExamplesWizardPage;
import org.kie.workbench.common.screens.examples.client.wizard.pages.organizationalunit.OUPage;
import org.kie.workbench.common.screens.examples.client.wizard.pages.project.ProjectPage;
import org.kie.workbench.common.screens.examples.client.wizard.pages.repository.RepositoryPage;
import org.kie.workbench.common.screens.examples.model.ExampleOrganizationalUnit;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
import org.kie.workbench.common.screens.examples.model.ExamplesMetaData;
import org.kie.workbench.common.screens.examples.service.ExamplesService;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.core.client.wizards.AbstractWizard;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;

/**
 * Wizard to import Example Repositories
 */
@Dependent
public class ExamplesWizard extends AbstractWizard {

    private static final int PREFERRED_WIDTH = 800;
    private static final int PREFERRED_HEIGHT = 800;

    private ExamplesWizardModel model;
    private List<WizardPage> pages = new ArrayList<WizardPage>();
    private RepositoryPage repositoryPage;
    private OUPage organizationalUnitPage;
    private BusyIndicatorView busyIndicatorView;
    private Caller<ExamplesService> examplesService;
    private Event<ProjectContextChangeEvent> event;
    private TranslationService translator;

    public ExamplesWizard() {
        //Zero parameter constructor for CDI proxies
    }

    @Inject
    public ExamplesWizard( final RepositoryPage repositoryPage,
                           final ProjectPage projectPage,
                           final OUPage organizationalUnitPage,
                           final BusyIndicatorView busyIndicatorView,
                           final Caller<ExamplesService> examplesService,
                           final Event<ProjectContextChangeEvent> event,
                           final TranslationService translator ) {
        pages.add( repositoryPage );
        pages.add( projectPage );
        pages.add( organizationalUnitPage );
        this.repositoryPage = repositoryPage;
        this.organizationalUnitPage = organizationalUnitPage;
        this.busyIndicatorView = busyIndicatorView;
        this.examplesService = examplesService;
        this.event = event;
        this.translator = translator;
    }

    @Override
    public void start() {
        model = new ExamplesWizardModel();
        for ( WizardPage page : pages ) {
            page.initialise();
            ( (ExamplesWizardPage) page ).setModel( model );
        }
        examplesService.call( new RemoteCallback<ExamplesMetaData>() {
            @Override
            public void callback( final ExamplesMetaData metaData ) {
                final Set<ExampleRepository> repositories = metaData.getRepositories();
                final Set<ExampleOrganizationalUnit> organizationalUnits = metaData.getOrganizationalUnits();
                repositoryPage.setRepositories( repositories );
                organizationalUnitPage.setOrganizationalUnits( organizationalUnits );
                ExamplesWizard.super.start();
            }
        } ).getMetaData();
    }

    @Override
    public void close() {
        for ( WizardPage page : pages ) {
            ( (ExamplesWizardPage) page ).destroy();
        }
        super.close();
    }

    @Override
    public List<WizardPage> getPages() {
        return pages;
    }

    @Override
    public Widget getPageWidget( final int pageNumber ) {
        WizardPage page = pages.get( pageNumber );
        page.prepareView();
        return page.asWidget();
    }

    @Override
    public String getTitle() {
        return translator.format( ExamplesScreenConstants.ExamplesWizard_WizardTitle );
    }

    @Override
    public int getPreferredHeight() {
        return PREFERRED_HEIGHT;
    }

    @Override
    public int getPreferredWidth() {
        return PREFERRED_WIDTH;
    }

    @Override
    public void isComplete( final Callback<Boolean> callback ) {
        callback.callback( true );

        //only when all pages are complete we can say the wizard is complete.
        for ( WizardPage page : this.pages ) {
            page.isComplete( new Callback<Boolean>() {
                @Override
                public void callback( final Boolean result ) {
                    if ( Boolean.FALSE.equals( result ) ) {
                        callback.callback( false );
                    }
                }
            } );
        }
    }

    @Override
    public void complete() {
        busyIndicatorView.showBusyIndicator( translator.format( ExamplesScreenConstants.ExamplesWizard_SettingUpExamples ) );
        examplesService.call( new RemoteCallback<ProjectContextChangeEvent>() {

                                  @Override
                                  public void callback( final ProjectContextChangeEvent context ) {
                                      busyIndicatorView.hideBusyIndicator();
                                      ExamplesWizard.super.complete();
                                      event.fire( context );
                                  }
                              },
                              new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).setupExamples( model.getTargetOrganizationalUnit(),
                                                                                                             model.getTargetRepository(),
                                                                                                             model.getProjects() );
    }

}
