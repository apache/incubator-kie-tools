package org.kie.workbench.common.screens.projecteditor.client.handlers;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.asset.management.model.RepositoryStructureModel;
import org.guvnor.asset.management.service.RepositoryStructureService;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.structure.client.validation.ValidatorWithReasonCallback;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.client.common.popups.errors.ErrorPopup;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.client.wizard.NewProjectWizard;
import org.kie.workbench.common.services.shared.validation.ValidationService;

import org.kie.workbench.common.widgets.client.handlers.NewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.PathLabel;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;

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
    private Caller<ValidationService> validationService;

    @Inject
    //We don't really need this for Packages but it's required by DefaultNewResourceHandler
    private AnyResourceTypeDefinition resourceType;

    @Inject
    private ProjectContext context;

    @Inject
    private NewProjectWizard wizard;
    
    @Inject
    private Caller<RepositoryStructureService> repoStructureService;

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
            
            
            repoStructureService.call(new RemoteCallback<RepositoryStructureModel>(){

                @Override
                public void callback(RepositoryStructureModel repoModel) {
                    if(repoModel.isManaged()){
                                wizard.setContent(projectName, repoModel.getPOM().getGav().getGroupId(), repoModel.getPOM().getGav().getVersion());
                   }else{
                       wizard.setContent( projectName );
                   }
                   wizard.start();
                   presenter.complete();   
                }
            }).load(context.getActiveRepository());
                

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
        
        if(context.getActiveRepository() != null){
        
            //You can always create a new Project (provided a repository has been selected)
             repoStructureService.call(new RemoteCallback<RepositoryStructureModel>(){

                    @Override
                    public void callback(RepositoryStructureModel repoModel) {
                        
                        if(repoModel != null && repoModel.isManaged()){
                            boolean isMultiModule = repoModel.isMultiModule();
                            response.onSuccess( isMultiModule );
                        }
                    }
             }).load(context.getActiveRepository());
        }else{
            response.onSuccess( false );
        }
        
    }

}