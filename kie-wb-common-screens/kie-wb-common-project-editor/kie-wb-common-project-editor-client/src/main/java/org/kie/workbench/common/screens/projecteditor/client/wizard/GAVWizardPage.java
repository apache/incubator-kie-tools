package org.kie.workbench.common.screens.projecteditor.client.wizard;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.kie.workbench.common.screens.projecteditor.client.forms.*;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.uberfire.client.wizards.WizardPage;
import org.uberfire.client.wizards.WizardPageStatusChangeEvent;

public class GAVWizardPage
        implements WizardPage {

    private GAV gav;
    private POMEditorPanel pomEditor;
    private Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent;

    @Inject
    public GAVWizardPage( POMEditorPanel pomEditor,
                          Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent ) {
        this.pomEditor = pomEditor;
        this.wizardPageStatusChangeEvent = wizardPageStatusChangeEvent;
    }

    public void setPom( POM pom ) {
        this.pomEditor.setPOM( pom, false );
        this.gav = pom.getGav();
        // changes are passed on from the pom editor through its view onto the underlying gav editor
        this.pomEditor.addGroupIdChangeHandler( new GroupIdChangeHandler() {
            @Override
            public void onChange( String newGroupId ) {
                final WizardPageStatusChangeEvent event = new WizardPageStatusChangeEvent( GAVWizardPage.this );
                wizardPageStatusChangeEvent.fire( event );
            }
        } );
        this.pomEditor.addArtifactIdChangeHandler( new ArtifactIdChangeHandler() {
            @Override
            public void onChange( String newArtifactId ) {
                final WizardPageStatusChangeEvent event = new WizardPageStatusChangeEvent( GAVWizardPage.this );
                wizardPageStatusChangeEvent.fire( event );
            }
        } );
        this.pomEditor.addVersionChangeHandler( new VersionChangeHandler() {
            @Override
            public void onChange( String newVersion ) {
                final WizardPageStatusChangeEvent event = new WizardPageStatusChangeEvent( GAVWizardPage.this );
                wizardPageStatusChangeEvent.fire( event );
            }
        } );
    }

    @Override
    public String getTitle() {
        return ProjectEditorResources.CONSTANTS.NewProjectWizard();
    }

    @Override
    public boolean isComplete() {
        boolean validGroupId = !( gav.getGroupId() == null || gav.getGroupId().isEmpty() || !gav.getGroupId().matches("^[a-zA-Z0-9\\.\\-_]+$") );
        boolean validArtifactId = !( gav.getArtifactId() == null || gav.getArtifactId().isEmpty() || !gav.getArtifactId().matches("^[a-zA-Z0-9\\.\\-_]+$") );
        boolean validVersion = !( gav.getVersion() == null || gav.getVersion().isEmpty() || !gav.getArtifactId().matches("^[a-zA-Z0-9\\.\\-_]+$") );
        return validGroupId && validArtifactId && validVersion;
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
