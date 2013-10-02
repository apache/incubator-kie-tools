package org.kie.workbench.common.screens.projecteditor.client.handlers;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.Package;
import org.kie.commons.data.Pair;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.kie.workbench.common.screens.projecteditor.client.wizard.NewProjectWizard;
import org.kie.workbench.common.services.shared.validation.ValidatorWithReasonCallback;
import org.kie.workbench.common.widgets.client.handlers.NewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.PathLabel;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.client.common.popups.errors.ErrorPopup;
import org.uberfire.client.wizards.WizardPresenter;

/**
 * Handler for the creation of new Projects
 */
@ApplicationScoped
public class NewProjectHandler
        implements NewResourceHandler {

    private final List<Pair<String, ? extends IsWidget>> extensions = new LinkedList<Pair<String, ? extends IsWidget>>();

    private final PathLabel pathLabel = new PathLabel();

    @Inject
    private NewProjectWizard wizard;

    @Inject
    private WizardPresenter wizardPresenter;

    @Inject
    private ProjectContext context;

    @PostConstruct
    private void setupExtensions() {
        this.extensions.add( Pair.newPair( CommonConstants.INSTANCE.ItemPathSubheading(),
                                           pathLabel ) );
    }

    @Override
    public List<Pair<String, ? extends IsWidget>> getExtensions() {
        final Repository activeRepository = context.getActiveRepository();
        this.pathLabel.setPath( ( activeRepository == null ? null : activeRepository.getRoot() ) );
        return this.extensions;
    }

    @Override
    public String getDescription() {
        return ProjectEditorResources.CONSTANTS.newProjectDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( ProjectEditorResources.INSTANCE.newProjectIcon() );
    }

    @Override
    public void create( final Package pkg,
                        final String projectName,
                        final NewResourcePresenter presenter ) {
        if ( context.getActiveRepository() != null ) {
            wizard.setProjectName( projectName );
            wizardPresenter.start( wizard );
            presenter.complete();

        } else {
            ErrorPopup.showMessage( ProjectEditorResources.CONSTANTS.NoRepositorySelectedPleaseSelectARepository() );
        }
    }

    @Override
    public void validate( final String fileName,
                          final ValidatorWithReasonCallback callback ) {
        //Project names are always valid
        callback.onSuccess();
    }

    @Override
    public void acceptContext( final ProjectContext context,
                               final Callback<Boolean, Void> response ) {
        //You can always create a new Project (provided a repository has been selected)
        response.onSuccess( context.getActiveRepository() != null );
    }

}
