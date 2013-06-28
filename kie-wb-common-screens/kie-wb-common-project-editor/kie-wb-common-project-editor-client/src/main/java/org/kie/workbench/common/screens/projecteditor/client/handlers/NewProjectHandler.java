package org.kie.workbench.common.screens.projecteditor.client.handlers;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
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
import org.kie.workbench.common.widgets.client.handlers.NewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.PathLabel;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.common.ErrorPopup;
import org.uberfire.client.wizards.WizardPresenter;
import org.uberfire.workbench.events.RepositoryChangeEvent;

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

    private boolean isRepositorySelected = false;

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
        return ProjectEditorConstants.INSTANCE.newProjectDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( ProjectEditorResources.INSTANCE.newProjectIcon() );
    }

    @Override
    public void create( final Package pkg,
                        final String projectName,
                        final NewResourcePresenter presenter ) {
        if ( isRepositorySelected ) {
            wizard.setProjectName( projectName );
            wizardPresenter.start( wizard );
            presenter.complete();

        } else {
            ErrorPopup.showMessage( ProjectEditorConstants.INSTANCE.NoRepositorySelectedPleaseSelectARepository() );
        }
    }

    @Override
    public boolean validate() {
        return true;
    }

    public void selectedRepositoryChanged( @Observes final RepositoryChangeEvent event ) {
        isRepositorySelected = ( event.getRepository() != null );
    }

    @Override
    public void acceptPath( final Path path,
                            final Callback<Boolean, Void> response ) {
        //You can always create a new Project (provided a repository has been selected)
        response.onSuccess( isRepositorySelected );
    }

}
