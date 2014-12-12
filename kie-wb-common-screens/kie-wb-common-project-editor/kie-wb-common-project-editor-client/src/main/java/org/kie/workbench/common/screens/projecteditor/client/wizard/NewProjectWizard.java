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
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.model.ProjectWizard;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
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
    boolean openEditor = true;

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
        pom.getGav().setArtifactId( sanitizeProjectName( projectName ) );
        pom.getGav().setVersion( "1.0" );
        gavWizardPage.setPom( pom );
    }

    public void setContent( final String projectName,
                            final String groupId,
                            final String version ) {
        // The Project Name is used to generate the folder name and hence is only checked to be a valid file name.
        // The ArtifactID is initially set to the project name, subsequently validated against the maven regex,
        // and preserved as is in the pom.xml file. However, as it is used to construct the default workspace and
        // hence package names, it is sanitized in the ProjectService.newProject() method.
        pom = new POM();
        pom.setName( projectName );
        pom.getGav().setGroupId( groupId );
        pom.getGav().setArtifactId( sanitizeProjectName( projectName ) );
        pom.getGav().setVersion( version );
        gavWizardPage.setPom( pom );
    }

    //The projectName has been validated as a FileSystem folder name, which may not be consistent with Maven ArtifactID
    //naming restrictions (see org.apache.maven.model.validation.DefaultModelValidator.java::ID_REGEX). Therefore we'd
    //best sanitize the projectName
    private String sanitizeProjectName( final String projectName ) {
        //Only [A-Za-z0-9_\-.] are valid so strip everything else out
        return projectName != null ? projectName.replaceAll( "[^A-Za-z0-9_\\-.]", "" ) : projectName;
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