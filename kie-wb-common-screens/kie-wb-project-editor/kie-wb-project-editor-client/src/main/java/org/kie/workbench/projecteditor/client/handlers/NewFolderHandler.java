package org.kie.workbench.projecteditor.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.widgets.common.client.callbacks.DefaultErrorCallback;
import org.kie.workbench.widgets.common.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.widgets.common.client.handlers.NewResourcePresenter;
import org.kie.guvnor.project.service.ProjectService;
import org.kie.workbench.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.uberfire.backend.vfs.Path;

/**
 * Handler for the creation of new Folders
 */
@ApplicationScoped
public class NewFolderHandler extends DefaultNewResourceHandler {

    @Inject
    private Caller<ProjectService> projectService;

    @Override
    public String getDescription() {
        return ProjectEditorConstants.INSTANCE.newFolderDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( ProjectEditorResources.INSTANCE.newFolderIcon() );
    }

    @Override
    public void create( final Path contextPath,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        projectService.call( getSuccessCallback( presenter ),
                             new DefaultErrorCallback() ).newDirectory( contextPath,
                                                                        baseFileName );
    }

    protected RemoteCallback<Path> getSuccessCallback( final NewResourcePresenter presenter ) {
        return new RemoteCallback<Path>() {

            @Override
            public void callback( Path pathToPom ) {
                presenter.complete();
                notifySuccess();
            }
        };
    }

    @Override
    public void acceptPath( final Path path,
                            final Callback<Boolean, Void> callback ) {
        //You can only create new Folders outside of packages
        if ( path == null ) {
            callback.onSuccess( false );
        }
        projectService.call( new RemoteCallback<Path>() {
            @Override
            public void callback( final Path path ) {
                callback.onSuccess( path == null );
            }
        } ).resolvePackage( path );
    }

}
