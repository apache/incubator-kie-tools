package org.kie.workbench.common.projecteditor.client.wizard;

import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.services.project.service.model.GAV;
import org.kie.workbench.common.projecteditor.client.forms.GAVEditor;
import org.uberfire.client.wizards.WizardPage;

import javax.inject.Inject;

public class GAVWizardPage
        implements WizardPage {

    private final GAVEditor gavEditor;
    private GAV gav;

    @Inject
    public GAVWizardPage(GAVEditor gavEditor) {
        this.gavEditor = gavEditor;
    }

    public void setGav(GAV gav) {
        this.gav = gav;
        this.gavEditor.setGAV(gav);
    }

    @Override
    public String getTitle() {
        return "GAV"; // TODO: i18n -Rikkola-
    }

    @Override
    public boolean isComplete() {
        return gav.getGroupId() != null && gav.getArtifactId() != null && gav.getVersion() != null;
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
        return gavEditor.asWidget();
    }
}
