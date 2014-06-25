package org.kie.workbench.common.screens.projecteditor.client.wizard;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.POM;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.projecteditor.client.forms.ArtifactIdChangeHandler;
import org.kie.workbench.common.screens.projecteditor.client.forms.GroupIdChangeHandler;
import org.kie.workbench.common.screens.projecteditor.client.forms.NameChangeHandler;
import org.kie.workbench.common.screens.projecteditor.client.forms.POMEditorPanel;
import org.kie.workbench.common.screens.projecteditor.client.forms.VersionChangeHandler;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.uberfire.client.callbacks.Callback;
import org.kie.uberfire.client.wizards.WizardPage;
import org.kie.uberfire.client.wizards.WizardPageStatusChangeEvent;

public class GAVWizardPage
        implements WizardPage {

    private POM pom;
    private POMEditorPanel pomEditor;
    private Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent;
    private Caller<ProjectScreenService> projectScreenService;

    @Inject
    public GAVWizardPage( POMEditorPanel pomEditor,
                          Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent,
                          Caller<ProjectScreenService> projectScreenService ) {
        this.pomEditor = pomEditor;
        this.wizardPageStatusChangeEvent = wizardPageStatusChangeEvent;
        this.projectScreenService = projectScreenService;

        // changes are passed on from the pom editor through its view onto the underlying gav editor
        this.pomEditor.addNameChangeHandler( new NameChangeHandler() {
            @Override
            public void onChange( String newName ) {
                final WizardPageStatusChangeEvent event = new WizardPageStatusChangeEvent( GAVWizardPage.this );
                GAVWizardPage.this.wizardPageStatusChangeEvent.fire( event );
            }
        } );
        this.pomEditor.addGroupIdChangeHandler( new GroupIdChangeHandler() {
            @Override
            public void onChange( String newGroupId ) {
                final WizardPageStatusChangeEvent event = new WizardPageStatusChangeEvent( GAVWizardPage.this );
                GAVWizardPage.this.wizardPageStatusChangeEvent.fire( event );
            }
        } );
        this.pomEditor.addArtifactIdChangeHandler( new ArtifactIdChangeHandler() {
            @Override
            public void onChange( String newArtifactId ) {
                final WizardPageStatusChangeEvent event = new WizardPageStatusChangeEvent( GAVWizardPage.this );
                GAVWizardPage.this.wizardPageStatusChangeEvent.fire( event );
            }
        } );
        this.pomEditor.addVersionChangeHandler( new VersionChangeHandler() {
            @Override
            public void onChange( String newVersion ) {
                final WizardPageStatusChangeEvent event = new WizardPageStatusChangeEvent( GAVWizardPage.this );
                GAVWizardPage.this.wizardPageStatusChangeEvent.fire( event );
            }
        } );
    }

    public void setPom( final POM pom ) {
        this.pom = pom;
        this.pomEditor.setPOM( pom,
                               false );
    }

    @Override
    public String getTitle() {
        return ProjectEditorResources.CONSTANTS.NewProjectWizard();
    }

    @Override
    public void isComplete( final Callback<Boolean> callback ) {
        projectScreenService.call( new RemoteCallback<Boolean>() {
            @Override
            public void callback( final Boolean result ) {
                callback.callback( Boolean.TRUE.equals( result ) );
            }
        } ).validate( pom );
    }

    @Override
    public void initialise() {
        //TODO: -Rikkola-
    }

    @Override
    public void prepareView() {
        //TODO: -Rikkola-
    }

    @Override
    public Widget asWidget() {
        return pomEditor.asWidget();
    }
}
