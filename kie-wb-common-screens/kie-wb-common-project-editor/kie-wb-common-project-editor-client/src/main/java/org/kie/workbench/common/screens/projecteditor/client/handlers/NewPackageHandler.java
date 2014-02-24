package org.kie.workbench.common.screens.projecteditor.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.service.ProjectService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.services.shared.validation.ValidatorWithReasonCallback;
import org.kie.workbench.common.widgets.client.callbacks.DefaultErrorCallback;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.workbench.type.AnyResourceTypeDefinition;
import org.uberfire.workbench.type.ResourceTypeDefinition;

/**
 * Handler for the creation of new Folders
 */
@ApplicationScoped
public class NewPackageHandler
        extends DefaultNewResourceHandler {

    @Inject
    private Caller<ProjectService> projectService;

    @Inject
    private Caller<ValidationService> validationService;

    @Inject
    //We don't really need this for Packages but it's required by DefaultNewResourceHandler
    private AnyResourceTypeDefinition resourceType;

    @Override
    public String getDescription() {
        return ProjectEditorResources.CONSTANTS.newPackageDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( ProjectEditorResources.INSTANCE.newFolderIcon() );
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
    }

    @Override
    public void validate( final String packageName,
                          final ValidatorWithReasonCallback callback ) {
        if ( pathLabel.getPath() == null ) {
            Window.alert( CommonConstants.INSTANCE.MissingPath() );
            callback.onFailure();
            return;
        }

        validationService.call( new RemoteCallback<Boolean>() {
            @Override
            public void callback( final Boolean response ) {
                if ( Boolean.TRUE.equals( response ) ) {
                    callback.onSuccess();
                } else {
                    callback.onFailure( ProjectEditorResources.CONSTANTS.InvalidPackageName( packageName ) );
                }
            }
        } ).isPackageNameValid( packageName );
    }

    @Override
    public void create( final Package pkg,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        projectService.call( getPackageSuccessCallback( presenter ),
                             new DefaultErrorCallback() ).newPackage( pkg,
                                                                      baseFileName );
    }

    private RemoteCallback<Package> getPackageSuccessCallback( final NewResourcePresenter presenter ) {
        return new RemoteCallback<Package>() {

            @Override
            public void callback( final Package pkg ) {
                presenter.complete();
                notifySuccess();
            }
        };
    }

}
