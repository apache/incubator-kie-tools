package org.kie.workbench.common.screens.projecteditor.client.handlers;

import java.util.Map;
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
import org.kie.workbench.common.screens.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.kie.workbench.common.services.shared.validation.ValidatorWithReasonCallback;
import org.kie.workbench.common.services.shared.validation.java.IdentifierValidationService;
import org.kie.workbench.common.widgets.client.callbacks.DefaultErrorCallback;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;

/**
 * Handler for the creation of new Folders
 */
@ApplicationScoped
public class NewPackageHandler
        extends DefaultNewResourceHandler {

    @Inject
    private Caller<ProjectService> projectService;

    @Inject
    private Caller<IdentifierValidationService> validationService;

    @Override
    public String getDescription() {
        return ProjectEditorResources.CONSTANTS.newPackageDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( ProjectEditorResources.INSTANCE.newFolderIcon() );
    }

    @Override
    public void validate( final String fileName,
                          final ValidatorWithReasonCallback callback ) {
        if ( pathLabel.getPath() == null ) {
            Window.alert( CommonConstants.INSTANCE.MissingPath() );
            callback.onFailure();
            return;
        }

        validationService.call( new RemoteCallback<Map<String, Boolean>>() {
            @Override
            public void callback( final Map<String, Boolean> results ) {
                if ( results.containsValue( Boolean.FALSE ) ) {
                    callback.onFailure( ProjectEditorResources.CONSTANTS.InvalidPackageName( fileName ) );
                } else {
                    callback.onSuccess();
                }
            }
        } ).evaluateIdentifiers( fileName.split( "\\.",
                                                 -1 ) );
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
