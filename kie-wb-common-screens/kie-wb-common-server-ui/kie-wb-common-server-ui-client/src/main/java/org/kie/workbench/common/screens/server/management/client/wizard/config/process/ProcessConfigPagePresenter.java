package org.kie.workbench.common.screens.server.management.client.wizard.config.process;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.kie.server.controller.api.model.spec.ProcessConfig;
import org.kie.workbench.common.screens.server.management.client.widget.config.process.ProcessConfigPresenter;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;

@Dependent
public class ProcessConfigPagePresenter implements WizardPage {

    private final ProcessConfigPresenter processConfigPresenter;

    @Inject
    public ProcessConfigPagePresenter( final ProcessConfigPresenter processConfigPresenter ) {
        this.processConfigPresenter = processConfigPresenter;
    }

    @Override
    public String getTitle() {
        return processConfigPresenter.getView().getConfigPageTitle();
    }

    @Override
    public void isComplete( final Callback<Boolean> callback ) {
        callback.callback( true );
    }

    @Override
    public void initialise() {

    }

    @Override
    public void prepareView() {

    }

    @Override
    public Widget asWidget() {
        return processConfigPresenter.getView().asWidget();
    }

    public ProcessConfig buildProcessConfig(){
        return processConfigPresenter.buildProcessConfig();
    }

    public void clear() {
        processConfigPresenter.clear();
    }
}
