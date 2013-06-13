package org.kie.workbench.common.screens.projecteditor.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.kie.workbench.common.services.project.service.ProjectService;
import org.kie.workbench.common.services.shared.context.Package;
import org.kie.workbench.common.widgets.client.callbacks.DefaultErrorCallback;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;

/**
 * Handler for the creation of new Folders
 */
@ApplicationScoped
public class NewPackageHandler
        extends DefaultNewResourceHandler {

    @Inject
    private Caller<ProjectService> projectService;

    @Override
    public String getDescription() {
        return ProjectEditorConstants.INSTANCE.newPackageDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( ProjectEditorResources.INSTANCE.newFolderIcon() );
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
