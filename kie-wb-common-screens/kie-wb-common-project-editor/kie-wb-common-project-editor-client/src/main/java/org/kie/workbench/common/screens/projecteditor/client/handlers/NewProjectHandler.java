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
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.client.wizard.NewProjectWizard;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.services.shared.validation.ValidatorWithReasonCallback;
import org.kie.workbench.common.widgets.client.handlers.NewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.PathLabel;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.client.common.popups.errors.ErrorPopup;
import org.uberfire.client.wizards.WizardPresenter;
import org.uberfire.commons.data.Pair;
import org.uberfire.workbench.type.AnyResourceTypeDefinition;
import org.uberfire.workbench.type.ResourceTypeDefinition;

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
    private Caller<ValidationService> validationService;

    @Inject
    //We don't really need this for Packages but it's required by DefaultNewResourceHandler
    private AnyResourceTypeDefinition resourceType;

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
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
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
    public void validate( final String projectName,
                          final ValidatorWithReasonCallback callback ) {
        if ( pathLabel.getPath() == null ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.MissingPath() );
            callback.onFailure();
            return;
        }

        validationService.call( new RemoteCallback<Boolean>() {
            @Override
            public void callback( final Boolean response ) {
                if ( Boolean.TRUE.equals( response ) ) {
                    callback.onSuccess();
                } else {
                    callback.onFailure( CommonConstants.INSTANCE.InvalidFileName0( projectName ) );
                }
            }
        } ).isProjectNameValid( projectName );
    }

    @Override
    public void acceptContext( final ProjectContext context,
                               final Callback<Boolean, Void> response ) {
        //You can always create a new Project (provided a repository has been selected)
        response.onSuccess( context.getActiveRepository() != null );
    }

}
