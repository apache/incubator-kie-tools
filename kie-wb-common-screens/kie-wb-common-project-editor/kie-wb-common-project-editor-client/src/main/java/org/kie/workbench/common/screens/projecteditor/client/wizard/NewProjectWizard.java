package org.kie.workbench.common.screens.projecteditor.client.wizard;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.widget.BusyIndicatorView;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.wizards.Wizard;
import org.uberfire.client.wizards.WizardPage;
import org.uberfire.client.wizards.WizardPresenter;
import org.uberfire.workbench.events.NotificationEvent;

public class NewProjectWizard
        implements Wizard<NewProjectWizardContext> {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<NotificationEvent> notificationEvent;

    @Inject
    private Event<ProjectContextChangeEvent> projectContextChangeEvent;

    @Inject
    private WizardPresenter presenter;

    @Inject
    private GAVWizardPage gavWizardPage;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Inject
    private Caller<ProjectService> projectServiceCaller;

    @Inject
    private ProjectContext context;

    private ArrayList<WizardPage> pages = new ArrayList<WizardPage>();
    private POM pom;
    private String projectName;

    @PostConstruct
    public void setupPages() {
        pom = new POM();
        pages.add( gavWizardPage );
    }

    @Override
    public String getTitle() {
        return ProjectEditorConstants.INSTANCE.NewProject();
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
        return 300;
    }

    @Override
    public int getPreferredWidth() {
        return 500;
    }

    @Override
    public boolean isComplete() {
        for ( WizardPage page : this.pages ) {
            if ( !page.isComplete() ) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void complete() {
        presenter.hide();
        final String url = GWT.getModuleBaseURL();
        final String baseUrl = url.replace( GWT.getModuleName() + "/", "" );
        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
        projectServiceCaller.call( getSuccessCallback(),
                                   new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).newProject( context.getActiveRepository(),
                                                                                                               projectName,
                                                                                                               pom,
                                                                                                               baseUrl );
    }

    private RemoteCallback<Project> getSuccessCallback() {
        return new RemoteCallback<Project>() {

            @Override
            public void callback( final Project project ) {
                busyIndicatorView.hideBusyIndicator();
                notificationEvent.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemCreatedSuccessfully() ) );
                projectContextChangeEvent.fire( new ProjectContextChangeEvent( context.getActiveOrganizationalUnit(),
                                                                               context.getActiveRepository(),
                                                                               project ) );
                placeManager.goTo( "projectScreen" );
            }
        };
    }

    public void setProjectName( final String projectName ) {
        this.projectName = projectName;
        pom.getGav().setArtifactId( projectName );
        gavWizardPage.setGav( pom.getGav() );
    }
}
