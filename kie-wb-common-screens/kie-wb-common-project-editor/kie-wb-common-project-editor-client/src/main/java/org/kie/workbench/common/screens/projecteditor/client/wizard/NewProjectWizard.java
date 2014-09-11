package org.kie.workbench.common.screens.projecteditor.client.wizard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.model.ProjectWizard;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.uberfire.client.common.BusyIndicatorView;
import org.kie.uberfire.client.wizards.AbstractWizard;
import org.kie.uberfire.client.wizards.WizardPage;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class NewProjectWizard
        extends AbstractWizard
        implements ProjectWizard{

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<NotificationEvent> notificationEvent;

    @Inject
    private Event<NewProjectEvent> newProjectEvent;

    @Inject
    private GAVWizardPage gavWizardPage;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Inject
    private Caller<KieProjectService> projectServiceCaller;

    @Inject
    private Caller<ProjectScreenService> projectScreenService;

    @Inject
    private ProjectContext context;

    private ArrayList<WizardPage> pages = new ArrayList<WizardPage>();
    private POM pom = new POM();
    private Callback<Project> projectCallback;

    @PostConstruct
    public void setupPages() {
        pages.add( gavWizardPage );
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
        return gavWizardPage.asWidget();
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
        gavWizardPage.isComplete( callback );
    }

    public void setContent( final String projectName ) {
        // The Project Name is used to generate the folder name and hence is only checked to be a valid file name.
        // The ArtifactID is initially set to the project name, subsequently validated against the maven regex,
        // and preserved as is in the pom.xml file. However, as it is used to construct the default workspace and
        // hence package names, it is sanitized in the ProjectService.newProject() method.
        pom = new POM();
        pom.setName( projectName );
        pom.getGav().setArtifactId( projectName );
        pom.getGav().setVersion( "1.0" );
        gavWizardPage.setPom( pom );
    }
    
    public void setContent( final String projectName, final String groupId, final String version ) {
        // The Project Name is used to generate the folder name and hence is only checked to be a valid file name.
        // The ArtifactID is initially set to the project name, subsequently validated against the maven regex,
        // and preserved as is in the pom.xml file. However, as it is used to construct the default workspace and
        // hence package names, it is sanitized in the ProjectService.newProject() method.
        pom = new POM();
        pom.setName( projectName );
        pom.getGav().setGroupId( groupId );
        pom.getGav().setArtifactId( projectName );
        pom.getGav().setVersion( version );
        gavWizardPage.setPom( pom );
    }

    @Override
    public void complete() {
        super.complete();

        final String url = GWT.getModuleBaseURL();
        final String baseUrl = url.replace( GWT.getModuleName() + "/", "" );
        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
        projectServiceCaller.call( getSuccessCallback(),
                                   new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).newProject( context.getActiveRepository(),
                                                                                                               pom.getName(),
                                                                                                               pom,
                                                                                                               baseUrl );
    }

    @Override
    public void close() {
        super.close();
        invokeCallback( null );
    }

    @Override
    public void start( Callback<Project> callback ) {
        this.projectCallback = callback;
        super.start();
    }

    private RemoteCallback<KieProject> getSuccessCallback() {
        return new RemoteCallback<KieProject>() {

            @Override
            public void callback( final KieProject project ) {
                busyIndicatorView.hideBusyIndicator();
                notificationEvent.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemCreatedSuccessfully() ) );
                invokeCallback( project );
                placeManager.goTo( "projectScreen" );
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