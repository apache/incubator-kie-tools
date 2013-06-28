package org.drools.workbench.screens.scorecardxls.client.handlers;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.scorecardxls.client.editor.URLHelper;
import org.drools.workbench.screens.scorecardxls.client.resources.ScoreCardXLSEditorResources;
import org.drools.workbench.screens.scorecardxls.client.resources.i18n.ScoreCardXLSEditorConstants;
import org.drools.workbench.screens.scorecardxls.client.type.ScoreCardXLSResourceType;
import org.drools.workbench.screens.scorecardxls.service.ScoreCardXLSService;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.commons.data.Pair;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.widget.AttachmentFileWidget;
import org.kie.workbench.common.widgets.client.widget.BusyIndicatorView;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

/**
 * Handler for the creation of new DRL Text Rules
 */
@ApplicationScoped
public class NewScoreCardXLSHandler extends DefaultNewResourceHandler {

    @Inject
    private Caller<ScoreCardXLSService> decisionTableXLSService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private ScoreCardXLSResourceType resourceType;

    @Inject
    private AttachmentFileWidget uploadWidget;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @PostConstruct
    private void setupExtensions() {
        extensions.add( new Pair<String, AttachmentFileWidget>( ScoreCardXLSEditorConstants.INSTANCE.Upload(),
                                                                uploadWidget ) );
    }

    @Override
    public String getDescription() {
        return ScoreCardXLSEditorConstants.INSTANCE.NewScoreCardDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( ScoreCardXLSEditorResources.INSTANCE.images().scoreCardIcon() );
    }

    @Override
    public void create( final Package pkg,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        busyIndicatorView.showBusyIndicator( ScoreCardXLSEditorConstants.INSTANCE.Uploading() );

        final Path path = pkg.getPackageMainResourcesPath();
        final String fileName = buildFileName( resourceType,
                                               baseFileName );
        final Path newPath = PathFactory.newPath( path.getFileSystem(),
                                                  fileName,
                                                  path.toURI() + "/" + fileName );

        uploadWidget.submit( path,
                             fileName,
                             URLHelper.getServletUrl(),
                             new Command() {

                                 @Override
                                 public void execute() {
                                     busyIndicatorView.hideBusyIndicator();
                                     presenter.complete();
                                     notifySuccess();
                                     final PlaceRequest place = new PathPlaceRequest( newPath );
                                     placeManager.goTo( place );
                                 }

                             },
                             new Command() {

                                 @Override
                                 public void execute() {
                                     busyIndicatorView.hideBusyIndicator();
                                 }
                             }
                           );

    }

}
