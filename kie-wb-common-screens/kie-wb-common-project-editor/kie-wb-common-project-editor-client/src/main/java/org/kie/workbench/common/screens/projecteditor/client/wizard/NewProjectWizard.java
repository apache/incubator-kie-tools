/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.screens.projecteditor.client.wizard;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.model.ProjectWizard;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.core.client.wizards.AbstractWizard;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class NewProjectWizard
        extends AbstractWizard
        implements ProjectWizard {

    private PlaceManager placeManager;
    private Event<NotificationEvent> notificationEvent;
    private POMWizardPage pomWizardPage;
    private BusyIndicatorView busyIndicatorView;
    private Caller<KieProjectService> projectServiceCaller;
    private ProjectContext context;

    private ArrayList<WizardPage> pages = new ArrayList<WizardPage>();
    private Callback<Project> projectCallback;
    boolean openEditor = true;

    public NewProjectWizard() {
    }

    @Inject
    public NewProjectWizard( final PlaceManager placeManager,
                             final Event<NotificationEvent> notificationEvent,
                             final POMWizardPage pomWizardPage,
                             final BusyIndicatorView busyIndicatorView,
                             final Caller<KieProjectService> projectServiceCaller,
                             final ProjectContext context ) {
        this.placeManager = placeManager;
        this.notificationEvent = notificationEvent;
        this.pomWizardPage = pomWizardPage;
        this.busyIndicatorView = busyIndicatorView;
        this.projectServiceCaller = projectServiceCaller;
        this.context = context;
    }

    @PostConstruct
    public void setupPages() {
        pages.add( pomWizardPage );
    }

    @Override
    public String getTitle() {
        return ProjectEditorResources.CONSTANTS.NewProject();
    }

    @Override
    public List<WizardPage> getPages() {
        return pages;
    }

    @Override
    public Widget getPageWidget( int pageNumber ) {
        return pomWizardPage.asWidget();
    }

    @Override
    public int getPreferredHeight() {
        return 550;
    }

    @Override
    public int getPreferredWidth() {
        return 800;
    }

    @Override
    public void isComplete( final Callback<Boolean> callback ) {
        //We only have one page; this is simple!
        pomWizardPage.isComplete( callback );
    }

    @Override
    public void initialise() {
        pomWizardPage.setPom( new POMBuilder()
                                      .setGroupId( context.getActiveOrganizationalUnit().getDefaultGroupId() )
                                      .build() );
    }

    @Override
    public void initialise( final POM pom ) {
        final POMBuilder pomBuilder = new POMBuilder( pom );

        if ( !pom.hasParent() ) {
            pomBuilder.addKieBuildPlugin( ApplicationPreferences.getCurrentDroolsVersion() );
        }

        pomWizardPage.setPom( pomBuilder.build() );
    }

    @Override
    public void complete() {
        super.complete();

        final String url = GWT.getModuleBaseURL();
        final String baseUrl = url.replace( GWT.getModuleName() + "/", "" );
        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
        projectServiceCaller.call( getSuccessCallback(),
                                   new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).newProject( context.getActiveRepository(),
                                                                                                               pomWizardPage.getPom(),
                                                                                                               baseUrl );
    }

    @Override
    public void close() {
        super.close();
        invokeCallback( null );
    }

    @Override
    public void start() {
        this.openEditor = true;
        this.projectCallback = null;
        super.start();
    }

    @Override
    public void start( Callback<Project> callback,
                       boolean openEditor ) {
        this.projectCallback = callback;
        this.openEditor = openEditor;
        super.start();
    }

    private RemoteCallback<KieProject> getSuccessCallback() {
        return new RemoteCallback<KieProject>() {

            @Override
            public void callback( final KieProject project ) {
                busyIndicatorView.hideBusyIndicator();
                notificationEvent.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemCreatedSuccessfully() ) );
                invokeCallback( project );
                if ( openEditor ) {
                    placeManager.goTo( "projectScreen" );
                }
            }
        };
    }

    private void invokeCallback( Project project ) {
        if ( projectCallback != null ) {
            projectCallback.callback( project );
            projectCallback = null;
        }
    }

}