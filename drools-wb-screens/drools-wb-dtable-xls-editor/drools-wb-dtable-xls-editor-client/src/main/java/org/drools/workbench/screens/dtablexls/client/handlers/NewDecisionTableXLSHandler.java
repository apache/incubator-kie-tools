package org.drools.workbench.screens.dtablexls.client.handlers;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ioc.client.api.Caller;
import org.drools.workbench.screens.dtablexls.client.editor.URLHelper;
import org.drools.workbench.screens.dtablexls.client.resources.i18n.DecisionTableXLSEditorConstants;
import org.drools.workbench.screens.dtablexls.client.resources.images.ImageResources;
import org.drools.workbench.screens.dtablexls.client.type.DecisionTableXLSResourceType;
import org.kie.commons.data.Pair;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.widget.AttachmentFileWidget;
import org.kie.workbench.common.widgets.client.widget.BusyIndicatorView;
import org.drools.workbench.screens.dtablexls.service.DecisionTableXLSService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.PathPlaceRequest;

/**
 * Handler for the creation of new XLS Decision Tables
 */
@ApplicationScoped
public class NewDecisionTableXLSHandler extends DefaultNewResourceHandler {

    @Inject
    private Caller<DecisionTableXLSService> decisionTableXLSService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private DecisionTableXLSResourceType resourceType;

    @Inject
    private AttachmentFileWidget uploadWidget;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @PostConstruct
    private void setupExtensions() {
        extensions.add( new Pair<String, AttachmentFileWidget>( DecisionTableXLSEditorConstants.INSTANCE.Upload(),
                                                                uploadWidget ) );
    }

    @Override
    public String getDescription() {
        return DecisionTableXLSEditorConstants.INSTANCE.NewDecisionTableDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( ImageResources.INSTANCE.decisionTableSmall() );
    }

    @Override
    public void create( final Path contextPath,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        busyIndicatorView.showBusyIndicator( DecisionTableXLSEditorConstants.INSTANCE.Uploading() );

        final String fileName = buildFileName( resourceType,
                                               baseFileName );
        final Path newPath = PathFactory.newPath( contextPath.getFileSystem(),
                                                  fileName,
                                                  contextPath.toURI() + "/" + fileName );

        uploadWidget.submit( contextPath,
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
